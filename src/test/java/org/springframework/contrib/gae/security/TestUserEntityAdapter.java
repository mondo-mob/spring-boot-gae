package org.springframework.contrib.gae.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TestUserEntityAdapter implements UserAdapter<TestUserEntity> {
    @Override
    public TestUserEntity newFromUserDetails(UserDetails userDetails) {
        TestUserEntity entity = new TestUserEntity(userDetails.getUsername(), userDetails.getPassword());
        entity.setRoles(grantedAuthoritiesToRoles(userDetails.getAuthorities()));
        return entity;
    }

    @Override
    public void mergeUserDetails(TestUserEntity user, UserDetails userDetails) {
        user.setPassword(userDetails.getPassword());
        user.setRoles(grantedAuthoritiesToRoles(userDetails.getAuthorities()));
    }

    @Override
    public void setPassword(TestUserEntity user, String password) {
        user.setPassword(password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(TestUserEntity user) {
        return user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public UserDetails toUserDetails(TestUserEntity user) {
        return user.toUserDetails();
    }

    private List<String> grantedAuthoritiesToRoles(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
}
