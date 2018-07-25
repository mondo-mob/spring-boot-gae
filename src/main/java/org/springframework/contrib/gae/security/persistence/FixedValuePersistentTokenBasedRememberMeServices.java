package org.springframework.contrib.gae.security.persistence;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

/**
 * Customised PersistentTokenBasedRememberMeServices to fix issues with the default implementation
 * that cause unwanted CookieTheftExceptions in a multi-threaded environment. The fix here is
 * to effectively disable the cookie theft detection by returning the same token value for each
 * request.
 * See https://github.com/spring-projects/spring-security/issues/3079
 */
public class FixedValuePersistentTokenBasedRememberMeServices extends PersistentTokenBasedRememberMeServices {

    // Don't have access to private variable in parent
    private final PersistentTokenRepository tokenRepository;

    public FixedValuePersistentTokenBasedRememberMeServices(String key,
                                                            UserDetailsService userDetailsService,
                                                            PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
        this.tokenRepository = tokenRepository;
    }

    protected UserDetails processAutoLoginCookie(String[] cookieTokens,
                                                 HttpServletRequest request, HttpServletResponse response) {

        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2
                    + " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        final String presentedSeries = cookieTokens[0];
        final String presentedToken = cookieTokens[1];

        PersistentRememberMeToken token = tokenRepository.getTokenForSeries(presentedSeries);

        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException(
                    "No persistent token found for series id: " + presentedSeries);
        }

        // We have a match for this user/series combination
        if (!presentedToken.equals(token.getTokenValue())) {
            // Token doesn't match series value. Delete all logins for this user and throw
            // an exception to warn them.
            tokenRepository.removeUserTokens(token.getUsername());

            throw new CookieTheftException(
                    messages.getMessage(
                            "PersistentTokenBasedRememberMeServices.cookieStolen",
                            "Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack."));
        }

        if (token.getDate().getTime() + getTokenValiditySeconds() * 1000L < System.currentTimeMillis()) {
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }

        // Token also matches, so login is valid. Update the token value, keeping the
        // *same* series number.
        if (logger.isDebugEnabled()) {
            logger.debug("Refreshing persistent login token for user '"
                    + token.getUsername() + "', series '" + token.getSeries() + "'");
        }

        // ****** Changed here to reuse token value instead of generating new one for every request ******
        PersistentRememberMeToken newToken = new PersistentRememberMeToken(
                token.getUsername(), token.getSeries(), token.getTokenValue(), new Date());
        // ************************************************************************

        try {
            tokenRepository.updateToken(newToken.getSeries(), newToken.getTokenValue(), newToken.getDate());
            addCookie(newToken, request, response);
        } catch (Exception e) {
            logger.error("Failed to update token: ", e);
            throw new RememberMeAuthenticationException("Autologin failed due to data access problem");
        }

        return getUserDetailsService().loadUserByUsername(token.getUsername());
    }

    private void addCookie(PersistentRememberMeToken token, HttpServletRequest request, HttpServletResponse response) {
        this.setCookie(new String[]{token.getSeries(), token.getTokenValue()}, this.getTokenValiditySeconds(), request, response);
    }
}
