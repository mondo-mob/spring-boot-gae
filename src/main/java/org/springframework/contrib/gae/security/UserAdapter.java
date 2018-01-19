package org.springframework.contrib.gae.security;

import com.googlecode.objectify.Key;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for adapting between an arbitrary application user and Spring Security's {@link UserDetails} type.
 *
 * @param <U> the application user type
 */
public interface UserAdapter<U extends GaeUser> {

    /**
     * Create a new application user from the supplied {@link UserDetails}.
     *
     * @param userDetails the user details to populate new user with
     * @return the application user
     */
    U newFromUserDetails(UserDetails userDetails);

    /**
     * Merge the user details provided into the application user.
     *
     * @param user        the application user
     * @param userDetails the user details to merge
     */
    void mergeUserDetails(U user, UserDetails userDetails);

    /**
     * Set the password on the application user to the supplied password.
     *
     * @param user     the application user to set the password for
     * @param password the password to set
     */
    void setPassword(U user, String password);

    /**
     * Return the collection of granted authorities for the application user.
     *
     * @param user the user to retrieve the authorities from
     * @return a collection of granted authorities for the application user
     */
    Collection<? extends GrantedAuthority> getAuthorities(U user);

    /**
     * Return the application user as {@link UserDetails}. A simple means of
     * achieving this is to copy the application user's details into the
     * {@link org.springframework.security.core.userdetails.User.UserBuilder}.
     *
     * @param user the application user to convert
     * @return the converted user details
     */
    UserDetails toUserDetails(U user);

    /**
     * By default, the simplest lookup of a user is by the Objectify key. To use
     * other means of identifying a user (e.g. login by email) you may not want to
     * bind this to the key, allowing for updates. If that is the case you can
     * customise how to lookup a user.
     *
     * @param username Spring's {@link UserDetails} username. You could map this to email
     *                 address for example.
     * @param userClass The application user type.
     *
     * @return Key for user if the use can be identified/found.
     */
    default Optional<Key<U>> getUserKey(String username, Class<U> userClass) {
        return Optional.of(Key.create(userClass, username));
    }

}
