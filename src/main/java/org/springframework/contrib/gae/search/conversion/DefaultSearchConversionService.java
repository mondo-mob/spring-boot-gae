package org.springframework.contrib.gae.search.conversion;

import org.springframework.contrib.gae.search.conversion.converter.DateToStringConverter;
import org.springframework.contrib.gae.search.conversion.converter.GeoPointToStringConverter;
import org.springframework.contrib.gae.search.conversion.converter.KeyToStringConverter;
import org.springframework.contrib.gae.search.conversion.converter.LocalDateToDateConverter;
import org.springframework.contrib.gae.search.conversion.converter.OffsetDateTimeToDateConverter;
import org.springframework.contrib.gae.search.conversion.converter.OffsetDateTimeToDoubleConverter;
import org.springframework.contrib.gae.search.conversion.converter.OffsetDateTimeToStringConverter;
import org.springframework.contrib.gae.search.conversion.converter.RefToStringConverter;
import org.springframework.contrib.gae.search.conversion.converter.ZonedDateTimeToDateConverter;
import org.springframework.contrib.gae.search.conversion.converter.ZonedDateTimeToDoubleConverter;
import org.springframework.contrib.gae.search.conversion.converter.ZonedDateTimeToStringConverter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Converters used by the search API to convert from application data types to SearchAPI data types.
 */
public class DefaultSearchConversionService extends DefaultConversionService {

    /**
     * Create a new instance.
     */
    public DefaultSearchConversionService() {
        addDefaultConverters(this);
    }

    /**
     * Add all the default Search API converters to the given registry.
     *
     * @param registry The registry to add to.
     */
    public static void addDefaultConverters(ConverterRegistry registry) {
        registry.addConverter(new DateToStringConverter());
        registry.addConverter(new RefToStringConverter());
        registry.addConverter(new KeyToStringConverter());

        registry.addConverter(new OffsetDateTimeToStringConverter());
        registry.addConverter(new OffsetDateTimeToDateConverter());
        registry.addConverter(new OffsetDateTimeToDoubleConverter());

        registry.addConverter(new ZonedDateTimeToStringConverter());
        registry.addConverter(new ZonedDateTimeToDateConverter());
        registry.addConverter(new ZonedDateTimeToDoubleConverter());

        registry.addConverter(new LocalDateToDateConverter());

        registry.addConverter(new GeoPointToStringConverter());
    }
}
