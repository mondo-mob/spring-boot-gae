package org.springframework.contrib.gae.search.conversion.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.ZonedDateTime;

/**
 * Convert {@link ZonedDateTime} values to {@link Double} values.
 *
 * @see InstantToDoubleConverter
 */
public class ZonedDateTimeToDoubleConverter implements Converter<ZonedDateTime, Double> {
    private static final InstantToDoubleConverter INSTANT_TO_DOUBLE = new InstantToDoubleConverter();

    @Override
    public Double convert(ZonedDateTime source) {
        return INSTANT_TO_DOUBLE.convert(source.toInstant());
    }
}
