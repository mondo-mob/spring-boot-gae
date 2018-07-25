package org.springframework.contrib.gae.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.contrib.gae.objectify.ObjectifyTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GaeUserDetailsManagerTest extends ObjectifyTest {
    private static final String TEST_USERNAME = "test-username-123";
    private static final String TEST_USER_PASSWORD = "password";

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserAdapter<TestUserEntity> userAdapter = new TestUserEntityAdapter();

    private GaeUserDetailsManager<TestUserEntity> manager;

    private TestUserEntity userEntity;

    @Before
    public void setUp() {
        objectify.register(TestUserEntity.class);

        when(passwordEncoder.encode(anyString())).thenAnswer((invocation -> String.format("encoded('%s')", invocation.getArguments()[0])));

        manager = new GaeUserDetailsManager<>(TestUserEntity.class, userAdapter, passwordEncoder);

        userEntity = testUser();
    }

    @Test
    public void loadUserByUsername_willReturnUser_whenUserExists() {
        UserDetails userDetails = manager.loadUserByUsername(userEntity.getUsername());

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(TEST_USERNAME);
    }

    @Test
    public void loadUserByUsername_willThrowException_whenUserDoesNotExist() {
        String username = "idontexist";

        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage(username);

        UserDetails user = manager.loadUserByUsername(username);

        assertThat(user).isNull();
    }

    @Test
    public void createUser_willPersistNewUser() {
        UserDetails newUser = User.withUsername("bar").password("password").roles("USER").build();

        manager.createUser(newUser);

        UserDetails savedUser = manager.loadUserByUsername(newUser.getUsername());

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(savedUser.getPassword()).isEqualTo("encoded('password')");

        List<String> roles = savedUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        assertThat(roles).containsExactly("ROLE_USER");
    }

    @Test
    public void updateUser_willUpdateUser_whenUserExists() {
        manager.updateUser(
                User.withUsername(userEntity.getUsername())
                        .password("new")
                        .roles("USER", "ADMIN")
                        .build()
        );

        UserDetails user = manager.loadUserByUsername(TEST_USERNAME);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(userEntity.getUsername());
        assertThat(user.getPassword()).isEqualTo("encoded('new')");

        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        assertThat(roles).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    public void updateUser_willDoNothing_whenUserDoesNotExist() {
        String username = "idontexist";

        manager.updateUser(
                User.withUsername(username)
                        .password("new")
                        .roles("USER", "ADMIN")
                        .build()
        );

        assertThat(manager.userExists(username)).isFalse();
    }

    @Test
    public void deleteUser_willDeleteUser_whenUserExists() {
        assertThat(manager.userExists(userEntity.getUsername())).isTrue();

        manager.deleteUser(userEntity.getUsername());

        assertThat(manager.userExists(userEntity.getUsername())).isFalse();
    }

    @Test
    public void deleteUser_willDoNothing_whenUserDoesNotExist() {
        String username = "idontexist";

        assertThat(manager.userExists(username)).isFalse();

        manager.deleteUser(username);

        assertThat(manager.userExists(username)).isFalse();
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void changePassword_willUpdatePassword() {
        manager.changePassword(TEST_USER_PASSWORD, "new-password");

        UserDetails user = manager.loadUserByUsername(TEST_USERNAME);
        assertThat(user).isNotNull();
        assertThat(user.getPassword()).isEqualTo("encoded('new-password')");
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void changePassword_willReauthenticate_andUpdatePassword_whenAuthenticationManagerSet() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        manager.setAuthenticationManager(authenticationManager);

        manager.changePassword(TEST_USER_PASSWORD, "new-password");

        UserDetails user = manager.loadUserByUsername(TEST_USERNAME);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(user);
        assertThat(user).isNotNull();
        assertThat(user.getPassword()).isEqualTo("encoded('new-password')");

        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().getPrincipal()).isEqualTo(TEST_USERNAME);
        assertThat(tokenCaptor.getValue().getCredentials()).isEqualTo(TEST_USER_PASSWORD);
    }

    @Test
    public void changePassword_willThrowAccessDenied_whenUserNotAuthenticated() {
        thrown.expect(AccessDeniedException.class);
        thrown.expectMessage("Can't change password as no Authentication object found in context for current user.");

        manager.changePassword(TEST_USER_PASSWORD, "new-password");
    }


    @Test
    public void userExists_willReturnTrue_whenUserExists() {
        boolean exists = manager.userExists(userEntity.getUsername());
        assertThat(exists).isTrue();
    }

    @Test
    public void userExists_willReturnFalse_whenUserDoesNotExist() {
        boolean exists = manager.userExists("idontexist");
        assertThat(exists).isFalse();
    }

    private TestUserEntity testUser() {
        TestUserEntity entity = new TestUserEntity(TEST_USERNAME, TEST_USER_PASSWORD);
        ofy().save().entity(entity).now();
        return entity;
    }
}
