package org.springframework.contrib.gae.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

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
}
