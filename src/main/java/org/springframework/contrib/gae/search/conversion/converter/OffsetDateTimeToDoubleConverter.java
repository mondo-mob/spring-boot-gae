package org.springframework.contrib.gae.search.conversion.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.OffsetDateTime;

/**
 * Convert {@link OffsetDateTime} values to {@link Double} values.
 * @see InstantToDoubleConverter
 */
public class OffsetDateTimeToDoubleConverter implements Converter<OffsetDateTime, Double> {
    private static final InstantToDoubleConverter INSTANT_TO_DOUBLE = new InstantToDoubleConverter();

    @Override
    public Double convert(OffsetDateTime source) {
        return INSTANT_TO_DOUBLE.convert(source.toInstant());
    }
}
