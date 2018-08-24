package org.springframework.contrib.gae.search;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SearchHelper {

    /**
     * Generates all substring ngrams of one or more input strings, intended to be used as a search index to facilitate partial string searching.
     *
     * @param strings   The source strings to split.
     *
     * @return Searchable text, lowercase and separated by space.
     */
    public static String getSearchableString(String... strings) {
        return getSearchableString(1, strings);
    }

    /**
     * Generates all substring ngrams of one or more input strings, intended to be used as a search index to facilitate partial string searching.
     *
     * @param minLength Minimum length to apply to a substring without any separated spaces.
     *                  NOTE: This may produce some strings with length lower than minLength only if they appear a space. This is to allow searching for sequences.
     * @param strings   The source strings to split.
     *
     * @return Searchable text, lowercase and separated by space.
     */
    public static String getSearchableString(int minLength, String... strings) {
        return getSearchableString(minLength, Stream.of(strings));
    }

    /**
     * Generates all substring ngrams of one or more input strings, intended to be used as a search index to facilitate partial string searching.
     *
     * @param strings   The source strings to split.
     *
     * @return Searchable text, lowercase and separated by space.
     */
    public static String getSearchableString(Collection<String> strings) {
        return getSearchableString(1, strings);
    }

    /**
     * Generates all substring ngrams of one or more input strings, intended to be used as a search index to facilitate partial string searching.
     *
     * @param minLength Minimum length to apply to a substring without any separated spaces.
     *                  NOTE: This may produce some strings with length lower than minLength only if they appear a space. This is to allow searching for sequences.
     * @param strings   The source strings to split.
     *
     * @return Searchable text, lowercase and separated by space.
     */
    public static String getSearchableString(int minLength, Collection<String> strings) {
        return getSearchableString(minLength, strings.stream());
    }

    private static String getSearchableString(int minLength, Stream<String> stringStream) {
        return stringStream
                .map(StringUtils::trimToNull)
                .filter(Objects::nonNull)
                .map(String::toLowerCase)

                .flatMap(str -> getAllSubstrings(str, minLength).stream())
                .distinct()
                .collect(Collectors.joining(" "));
    }

    // Ported from project
    private static Set<String> getAllSubstrings(String str, int minLength) {
        Set<String> substrings = new LinkedHashSet<>();
        for (int length = minLength; length <= str.length(); length++) {

            for (int start = 0; start + length <= str.length(); start++) {
                String subString = str.substring(start, start + length).trim();
                if (StringUtils.length(subString) >= minLength) {
                    substrings.add(subString);
                }
            }

        }
        return substrings;
    }
}
