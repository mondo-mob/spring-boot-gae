package org.springframework.contrib.gae.search.conversion.converter;

import com.googlecode.objectify.Key;
import org.springframework.core.convert.converter.Converter;

public class KeyToStringConverter implements Converter<Key, String> {

    @Override
    public String convert(Key source) {
        return source.toWebSafeString();
    }

}
