package org.springframework.contrib.gae.security;

import com.googlecode.objectify.Key;
import org.junit.Before;
import org.junit.Test;
import org.springframework.contrib.gae.objectify.ObjectifyTest;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ConstantConditions")
public class UserAdapterTest extends ObjectifyTest {

    private TestUserEntityAdapter userAdapter;

    @Before
    public void before() {
        userAdapter = new TestUserEntityAdapter();
    }

    @Test
    public void getUserKey_willReturnUserKey() {

        Optional<Key<TestUserEntity>> optionalKey = userAdapter.getUserKey("username", TestUserEntity.class);

        assertThat(optionalKey.isPresent(), is(true));
        assertThat(optionalKey.get(), equalTo(Key.create(TestUserEntity.class, "username")));
    }


}