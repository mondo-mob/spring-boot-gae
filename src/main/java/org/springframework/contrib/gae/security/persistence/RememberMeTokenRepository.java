package org.springframework.contrib.gae.security.persistence;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.QueryKeys;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.springframework.contrib.gae.util.Nulls.ifNotNull;

@Repository
public class RememberMeTokenRepository implements PersistentTokenRepository {

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        RememberMeToken entity = new RememberMeToken(token.getSeries(), token.getUsername(), token.getTokenValue(), token.getDate());
        ofy().save().entity(entity).now();
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        RememberMeToken entity = ofy().load().key(Key.create(RememberMeToken.class, series)).now();
        if (entity != null) {
            ofy().save().entity(
                    entity
                            .setToken(tokenValue)
                            .setLastUsed(lastUsed)
            ).now();
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String series) {
        RememberMeToken entity = ofy().load().key(Key.create(RememberMeToken.class, series)).now();
        return ifNotNull(entity, e -> new PersistentRememberMeToken(e.getUsername(), e.getSeries(), e.getToken(), e.getLastUsed()));
    }

    @Override
    public void removeUserTokens(String username) {
        QueryKeys<RememberMeToken> keys = ofy().load().type(RememberMeToken.class).filter("username", username).keys();
        ofy().delete().keys(keys).now();
    }
}
