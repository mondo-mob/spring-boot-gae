package org.springframework.contrib.gae.security;

/**
 * Marker interface for the GAE user. Consumers of the GAE security module should mark their user entity with this interface
 * to ensure that the appropriate user Class instance is injected into the {@link GaeUserDetailsManager}.
 */
public interface GaeUser {
}
