package org.springframework.contrib.gae.security;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.contrib.gae.objectify.ObjectifyTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class GaeUserDetailsManagerTest extends ObjectifyTest {

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
        assertThat(userDetails.getUsername()).isEqualTo(userEntity.getUsername());
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

        UserDetails user = manager.loadUserByUsername(userEntity.getUsername());
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
    @Ignore
    public void changePassword() {
        fail();  // TODO: needs test
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
        TestUserEntity entity = new TestUserEntity(randomUUID().toString(), "password");
        ofy().save().entity(entity).now();
        return entity;
    }
}