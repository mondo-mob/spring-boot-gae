package org.springframework.contrib.gae.security;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class TestUserEntity implements GaeUser {
    @Id
    private String username;
    private String password;
    private Set<String> roles = new LinkedHashSet<>();

    private TestUserEntity() {
    }

    public TestUserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public TestUserEntity setRoles(Collection<String> roles) {
        this.roles = new LinkedHashSet<>(roles);
        return this;
    }

    public UserDetails toUserDetails() {
        return User.withUsername(username)
                .password(password)
                .authorities(roles.toArray(new String[roles.size()]))
                .build();
    }
}
