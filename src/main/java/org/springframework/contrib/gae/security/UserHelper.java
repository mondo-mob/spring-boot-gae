package org.springframework.contrib.gae.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Defines a simple interface for converting between the application user and Spring Security's {@link UserDetails} type.
 *
 * @param <U> the application user type
 */
public interface UserHelper<U> {

    U newFromUserDetails(UserDetails userDetails);

    void mergeUserDetails(U user, UserDetails userDetails);

    void changePassword(U user, String newPassword);

    Collection<? extends GrantedAuthority> getAuthorities(U user);

    UserDetails toUserDetails(U user);
}
