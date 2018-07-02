package org.springframework.contrib.gae.search;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SearchHelperTest {

    @Test
    public void getSearchableString_willBeEmptyStringWhenOnlyNullsAndBlanks() {
        String searchableText = SearchHelper.getSearchableString(null, "", "     ");

        assertThat(searchableText, is(""));
    }

    @Test
    public void getSearchableString_willIgnoreNullsAndBlanks() {
        String searchableText = SearchHelper.getSearchableString(null, "ab", "", "     ");

        System.out.println("TEXT: " + searchableText);
        assertMatch(searchableText, "a");
        assertMatch(searchableText, "b");
        assertMatch(searchableText, "ab");
    }

    @Test
    public void getSearchableString_willGenerate() {
        String studentNumber = "1234";
        String studentName = "Jo Smith";

        String searchableText = SearchHelper.getSearchableString(studentNumber, studentName);

        System.out.println("TEXT: " + searchableText);
        assertThat(searchableText.length(), is(151));
        assertMatches(searchableText,
                "1",
                "2",
                "3",
                "4",
                "12",
                "123",
                "1234",
                "23",
                "234",
                "34",

                "j",
                "o",
                "s",
                "m",
                "i",
                "t",
                "h",
                "jo",
                "jo s",
                "jo sm",
                "jo smi",
                "jo smit",
                "jo smith",

                "sm",
                "smi",
                "smit",
                "smith",
                "mi",
                "mit",
                "mith",
                "it",
                "ith",
                "th"
        );
    }

    @Test
    public void getSearchableString_willGenerateMinLength2() {
        String studentNumber = "1234";
        String studentName = "Jo Smith";

        String searchableText = SearchHelper.getSearchableString(2, studentNumber, studentName);

        System.out.println("TEXT: " + searchableText);
        assertThat(searchableText.length(), is(129));

        assertMatches(searchableText,
                "12",
                "123",
                "1234",
                "23",
                "234",
                "34",

                "jo",
                "jo s",
                "jo sm",
                "jo smi",
                "jo smit",
                "jo smith",

                "o s",
                "sm",
                "smi",
                "smit",
                "smith",
                "mi",
                "mit",
                "mith",
                "it",
                "ith",
                "th"
        );
    }

    // In theory everything that matches here should be a successful search
    private void assertMatches(String searchText, String... expectedMatches) {
        for (String expectedMatch : expectedMatches) {
            assertMatch(searchText, expectedMatch);
        }
    }

    // Mimick how google search works by searching for a term that starts at the start of the string or separated by a space
    private void assertMatch(String searchText, String term) {
        // Uses regex "\bsearch term\b" to denote word boundaries
        Pattern pattern = Pattern.compile(String.format("\\b%s\\b", term));

        assertThat(String.format("Cannot find whole word [%s] within string:%n%s", term, searchText),
                pattern.matcher(searchText).find(), is(true));

    }
    
}