package org.springframework.contrib.gae.security;

import com.googlecode.objectify.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Optional;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class GaeUserDetailsManager<U> implements UserDetailsManager {

    private static final Logger LOG = LoggerFactory.getLogger(GaeUserDetailsManager.class);

    private final Class<U> userClass;
    private final UserHelper<U> userHelper;
    private final PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    public GaeUserDetailsManager(Class<U> userClass,
                                 UserHelper<U> userHelper,
                                 PasswordEncoder passwordEncoder) {
        this.userClass = userClass;
        this.userHelper = userHelper;
        this.passwordEncoder = passwordEncoder;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUser(username)
            .map(userHelper::toUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public void createUser(UserDetails details) {
        U user = userHelper.newFromUserDetails(new UserDetailsWithEncodedPassword(details, passwordEncoder));
        ofy().save().entity(user).now();
    }

    @Override
    public void updateUser(UserDetails details) {
        loadUser(details.getUsername())
            .map((user) -> {
                userHelper.mergeUserDetails(user, new UserDetailsWithEncodedPassword(details, passwordEncoder));
                return user;
            })
            .ifPresent((user) -> ofy().save().entity(user).now());
    }

    @Override
    public void deleteUser(String username) {
        ofy().delete().key(Key.create(userClass, username)).now();
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext()
            .getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException("Can't change password as no Authentication object found in context for current user.");
        }

        String username = currentUser.getName();

        // If an authentication manager has been set, re-authenticate the user with the supplied password.
        if (authenticationManager != null) {
            LOG.debug("Reauthenticating user '{}' for password change request.", username);

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username, oldPassword));
        } else {
            LOG.debug("No authentication manager set. Password won't be re-checked.");
        }

        LOG.debug("Changing password for user '{}'", username);

        U user = loadUser(username).orElseThrow(() -> new IllegalStateException("Current user doesn't exist in database."));
        userHelper.changePassword(user, passwordEncoder.encode(newPassword));
        ofy().save().entity(user).now();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, userHelper.getAuthorities(user));
        authentication.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public boolean userExists(String username) {
        return loadUser(username).isPresent();
    }

    private Optional<U> loadUser(String username) {
        U user = ofy().load().key(Key.create(userClass, username)).now();
        return Optional.ofNullable(user);
    }
}
