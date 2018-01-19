package org.springframework.contrib.gae.security.persistence;

import com.googlecode.objectify.Key;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.objectify.ObjectifyTest;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import java.util.Date;

import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class RememberMeTokenRepositoryTest extends ObjectifyTest {

    @Autowired
    private RememberMeTokenRepository repository;


    @Test
    public void createNewToken() {
        PersistentRememberMeToken token = new PersistentRememberMeToken("user-a", randomUUID().toString(), "value", new Date());

        repository.createNewToken(token);

        RememberMeToken saved = load(token);

        assertThat(saved.getSeries(), is(token.getSeries()));
        assertThat(saved.getUsername(), is(token.getUsername()));
        assertThat(saved.getToken(), is(token.getTokenValue()));
        assertThat(saved.getLastUsed(), is(token.getDate()));
    }

    @Test
    public void updateToken() {
        PersistentRememberMeToken original = new PersistentRememberMeToken("user-a", randomUUID().toString(), "value", new Date());
        repository.createNewToken(original);

        Date updated = new Date();
        repository.updateToken(original.getSeries(), "new-token-value", updated);

        RememberMeToken saved = load(original);
        assertThat(saved.getSeries(), is(original.getSeries()));
        assertThat(saved.getUsername(), is(original.getUsername()));
        assertThat(saved.getToken(), is("new-token-value"));
        assertThat(saved.getLastUsed(), is(updated));

    }

    @Test
    public void updateToken_willIgnore_whenTokenNotFound() {
        repository.updateToken("does-not-exist", "new-token-value", new Date());

        RememberMeToken saved = ofy().load().key(Key.create(RememberMeToken.class,"does-not-exist")).now();
        assertThat(saved, nullValue());
    }

    @Test
    public void getTokenForSeries() {
        PersistentRememberMeToken token = new PersistentRememberMeToken("user-a", randomUUID().toString(), "value", new Date());
        repository.createNewToken(token);

        PersistentRememberMeToken retrieved = repository.getTokenForSeries(token.getSeries());

        assertThat(retrieved.getSeries(), is(token.getSeries()));
        assertThat(retrieved.getUsername(), is(token.getUsername()));
        assertThat(retrieved.getTokenValue(), is(token.getTokenValue()));
        assertThat(retrieved.getDate(), is(token.getDate()));
    }

    @Test
    public void getTokenForSeries_willReturnNull_whenNotFound() {
        PersistentRememberMeToken result = repository.getTokenForSeries("not-found");

        assertThat(result, nullValue());
    }

    @Test
    public void removeUserToken() {
        PersistentRememberMeToken token1 = new PersistentRememberMeToken("user-a", randomUUID().toString(), "value", new Date());
        PersistentRememberMeToken token2 = new PersistentRememberMeToken("user-a", randomUUID().toString(), "value", new Date());
        repository.createNewToken(token1);
        repository.createNewToken(token2);

        repository.removeUserTokens("user-a");

        assertThat(load(token1), nullValue());
        assertThat(load(token2), nullValue());

    }

    @Test
    public void removeUserToken_willDoNothing_whenTokenDoesNotExist() {
        repository.removeUserTokens("user-a");

        RememberMeToken saved = ofy().load().key(Key.create(RememberMeToken.class,"does-not-exist")).now();
        assertThat(saved, nullValue());
    }

    private RememberMeToken load(PersistentRememberMeToken source) {
        return ofy().load().key(Key.create(RememberMeToken.class, source.getSeries())).now();
    }

}