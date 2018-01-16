package org.springframework.contrib.gae.util;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class NullsTest {

    @Test
    public void ifNotNull_willReturnNull_whenParamNull() {
        String result = Nulls.ifNotNull(null, Object::toString);

        assertThat(result, nullValue());
    }

    @Test
    public void ifNotNull_willTransform_whenParamNotNull() {
        String result = Nulls.ifNotNull(12, Object::toString);

        assertThat(result, is("12"));
    }

}