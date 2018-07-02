package org.springframework.contrib.gae.search.conversion.converter;

import com.googlecode.objectify.Ref;
import org.springframework.core.convert.converter.Converter;

/**
 * {@link Ref} and {@link com.googlecode.objectify.Key} should be indexed with same value.
 * Objectify allows you to interchange and stores them as key. This converter defers to
 * {@link KeyToStringConverter}.
 */
public class RefToStringConverter implements Converter<Ref, String> {
    private static final KeyToStringConverter KEY_CONVERTER = new KeyToStringConverter();

    @Override
    public String convert(Ref source) {
        return KEY_CONVERTER.convert(source.getKey());
    }

}
