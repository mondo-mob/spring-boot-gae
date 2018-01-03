package org.springframework.contrib.gae.security.repository;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.QueryKeys;
import org.springframework.contrib.gae.objectify.repository.ObjectifyRepository;
import org.springframework.contrib.gae.security.entity.RememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

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
            ofy().save().entity(entity).now();
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String series) {
        RememberMeToken entity = ofy().load().key(Key.create(RememberMeToken.class, series)).now();
        return new PersistentRememberMeToken(entity.getUsername(), entity.getSeries(), entity.getToken(), entity.getLastUsed());
    }

    @Override
    public void removeUserTokens(String username) {
        QueryKeys<RememberMeToken> keys = ofy().load().type(RememberMeToken.class).filter("username", username).keys();
        ofy().delete().keys(keys);
    }
}
