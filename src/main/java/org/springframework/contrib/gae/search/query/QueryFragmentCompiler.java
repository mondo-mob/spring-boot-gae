package org.springframework.contrib.gae.search.query;

import com.google.appengine.api.search.GeoPoint;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.contrib.gae.search.IndexType;
import org.springframework.contrib.gae.search.Operator;
import org.springframework.contrib.gae.search.metadata.SearchFieldMetadata;
import org.springframework.contrib.gae.search.metadata.SearchMetadata;
import org.springframework.contrib.gae.search.metadata.impl.MetadataUtils;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Compiles a filter fragment into a filter string.
 */
class QueryFragmentCompiler implements Function<Query.Fragment, String> {
    private final Class<?> entityType;
    private final SearchMetadata searchMetadata;
    private final ConversionService conversionService;
    private final QueryEscapeFunction escapeFunction = new QueryEscapeFunction();

    public QueryFragmentCompiler(Class<?> entityType, SearchMetadata searchMetadata, ConversionService conversionService) {
        this.entityType = entityType;
        this.searchMetadata = searchMetadata;
        this.conversionService = conversionService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String apply(Query.Fragment fragment) {
        if (fragment.isRaw()) {
            return String.valueOf(fragment.getValue());
        }

        SearchFieldMetadata fieldMetadata = searchMetadata.getField(entityType, fragment.getField());
        String field = fieldMetadata.getEncodedName();
        String operator = fragment.getOperator().getSymbol();

        if (isCollection(fragment.getValue())) {
            List<String> values = convertValuesToString(fieldMetadata.getIndexType(), getIterator(fragment.getValue()));
            String value = StringUtils.join(values, " OR ");
            return String.format("%s%s(%s)", field, Operator.IS.getSymbol(), value);
        } else {
            String value = convertValueToString(fieldMetadata.getIndexType(), fragment.getValue());
            return String.format("%s%s%s", field, operator, value);
        }
    }

    protected List<String> convertValuesToString(IndexType indexType, Iterator<?> values) {
        List<String> stringValues = new ArrayList<>();
        values.forEachRemaining(value -> stringValues.add(convertValueToString(indexType, value)));
        return stringValues;
    }

    protected String convertValueToString(IndexType indexType, Object value) {
        Object indexedValue = convertToIndexedValue(indexType, value);
        return convertIndexedValueToString(indexType, indexedValue);
    }

    private Object convertToIndexedValue(IndexType indexType, Object value) {
        Class<?> indexedAsType = getConversionType(indexType);
        return conversionService.convert(value, indexedAsType);
    }

    private String convertIndexedValueToString(IndexType indexType, Object indexedValue) {
        String stringValue = conversionService.convert(indexedValue, String.class);
        if (shouldQuoteQueryValue(indexType)) {
            return escapeFunction.apply(stringValue);
        }
        return stringValue;
    }

    private boolean isCollection(Object object) {
        return MetadataUtils.isCollectionType(object.getClass());
    }

    private Iterator<?> getIterator(Object object) {
        if (TypeUtils.isArrayType(object.getClass())) {
            return Arrays.stream((Object[]) object).iterator();
        }
        return ((Collection<?>) object).stream().iterator();
    }

    private Class<?> getConversionType(IndexType indexType) {
        switch (indexType) {
            case NUMBER:
                return Double.class;
            case DATE:
                return Date.class;
            case GEOPOINT:
                return GeoPoint.class;
            default:
                return String.class;
        }
    }

    private boolean shouldQuoteQueryValue(IndexType indexType) {
        switch (indexType) {
            case IDENTIFIER:
            case HTML:
            case TEXT:
                return true;
            default:
                return false;
        }
    }
}
