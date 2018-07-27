package org.springframework.contrib.gae.search.conversion.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Convert {@link LocalDate} values to {@link Date} values.
 */
public class LocalDateToDateConverter implements Converter<LocalDate, Date> {
    @Override
    public Date convert(LocalDate source) {
        return Date.from(source.atStartOfDay(ZoneOffset.UTC).toInstant());
    }
}
