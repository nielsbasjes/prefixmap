/*
 * Copyright (C) 2018-2021 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.basjes.collections.prefixmap;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestASCIIPrefixTrie extends AbstractPrefixTrieTests {

    @Override
    PrefixTrie<String> createPrefixTrie(boolean caseSensitive) {
        return new ASCIIPrefixTrie<>(caseSensitive);
    }

    @Test
    void testPutNonASCIIPrefix() {
        // These are 1 char per character
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new ASCIIPrefixTrie<>(false).add("你好", "Hello in Chinese")
        );
        assertEquals("Only readable ASCII is allowed as prefix !!!", exception.getMessage());
    }

    @Test
    void testPutNonASCIIValue() {
        assertDoesNotThrow(
            () -> new ASCIIPrefixTrie<>(false).add("Hello in Chinese", "你好")
        );
    }

    @Test
    void testRemoveNonASCIIPrefix() {
        PrefixTrie<String> prefixLookup = new ASCIIPrefixTrie<>(false);
        prefixLookup.add("Something",    "To ensure not empty");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> prefixLookup.remove("你好")
        );
        assertEquals("Only readable ASCII is allowed as prefix !!!", exception.getMessage());
    }

    @Test
    void testPutEmojiPrefix() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            // These are 2 chars per character
            () -> new ASCIIPrefixTrie<>(false).add("🖖", "May the force be with you (🖖)")
        );
        assertEquals("Only readable ASCII is allowed as prefix !!!", exception.getMessage());
    }

    @Test
    void testPutEmojiValue() {
        assertDoesNotThrow(() -> new ASCIIPrefixTrie<>(false).add("LLAP", "May the force be with you (🖖)"));
    }

    @Test
    void testRemoveEmojiPrefix() {
        PrefixTrie<String> prefixLookup = new ASCIIPrefixTrie<>(false);
        prefixLookup.add("Something",    "To ensure not empty");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> prefixLookup.remove("🖖")
        );
        assertEquals("Only readable ASCII is allowed as prefix !!!", exception.getMessage());
    }

    @Test
    void testCaseINSensitiveLookup(){
        PrefixTrie<String> prefixLookup = new ASCIIPrefixTrie<>(false);
        prefixLookup.add("ABC",    "Result ABC");
        prefixLookup.add("ABCD",    "Result ABCD");
        // The ABCDE is missing !!!
        prefixLookup.add("ABCDEF",  "Result ABCDEF");

        // ----------------------------------------------------
        // Shortest Match
        checkShortest(prefixLookup, "MisMatch", null);
        // These are 1 char per character
        checkShortest(prefixLookup, "你好",     null);

        // Same case
        checkShortest(prefixLookup, "A",       null);
        checkShortest(prefixLookup, "AB",      null);
        checkShortest(prefixLookup, "ABC",     "Result ABC");
        checkShortest(prefixLookup, "ABCD",    "Result ABC");
        checkShortest(prefixLookup, "ABCDE",   "Result ABC");
        checkShortest(prefixLookup, "ABCDEF",  "Result ABC");

        checkShortest(prefixLookup, "A-",      null);
        checkShortest(prefixLookup, "AB-",     null);
        checkShortest(prefixLookup, "ABC-",    "Result ABC");
        checkShortest(prefixLookup, "ABCD-",   "Result ABC");
        checkShortest(prefixLookup, "ABCDE-",  "Result ABC");
        checkShortest(prefixLookup, "ABCDEF-", "Result ABC");

        checkShortest(prefixLookup, "ABC\\t",  "Result ABC");
        checkShortest(prefixLookup, "ABC€",    "Result ABC");
        checkShortest(prefixLookup, "ABCD€",    "Result ABC");

        // Different case
        checkShortest(prefixLookup, "a",       null);
        checkShortest(prefixLookup, "ab",      null);
        checkShortest(prefixLookup, "abc",     "Result ABC");
        checkShortest(prefixLookup, "abcd",    "Result ABC");
        checkShortest(prefixLookup, "abcde",   "Result ABC");
        checkShortest(prefixLookup, "abcdef",  "Result ABC");

        checkShortest(prefixLookup, "a-",      null);
        checkShortest(prefixLookup, "ab-",     null);
        checkShortest(prefixLookup, "abc-",    "Result ABC");
        checkShortest(prefixLookup, "abcd-",   "Result ABC");
        checkShortest(prefixLookup, "abcde-",  "Result ABC");
        checkShortest(prefixLookup, "abcdef-", "Result ABC");

        checkShortest(prefixLookup, "abc\\t",  "Result ABC");
        checkShortest(prefixLookup, "abc€",    "Result ABC");
        checkShortest(prefixLookup, "abcd€",   "Result ABC");

        // ----------------------------------------------------
        // Longest Match
        checkLongest(prefixLookup, "MisMatch", null);
        // These are 1 char per character
        checkLongest(prefixLookup, "你好",      null);

        // Same case
        checkLongest(prefixLookup, "A",       null);
        checkLongest(prefixLookup, "AB",      null);
        checkLongest(prefixLookup, "ABC",     "Result ABC");
        checkLongest(prefixLookup, "ABCD",    "Result ABCD");
        checkLongest(prefixLookup, "ABCDE",   "Result ABCD");
        checkLongest(prefixLookup, "ABCDEF",  "Result ABCDEF");

        checkLongest(prefixLookup, "A-",      null);
        checkLongest(prefixLookup, "AB-",     null);
        checkLongest(prefixLookup, "ABC-",    "Result ABC");
        checkLongest(prefixLookup, "ABCD-",   "Result ABCD");
        checkLongest(prefixLookup, "ABCDE-",  "Result ABCD");
        checkLongest(prefixLookup, "ABCDEF-", "Result ABCDEF");

        checkLongest(prefixLookup, "ABC\\t",  "Result ABC");
        checkLongest(prefixLookup, "ABC€",    "Result ABC");
        checkLongest(prefixLookup, "ABCD€",   "Result ABCD");

        // Different case
        checkLongest(prefixLookup, "a",       null);
        checkLongest(prefixLookup, "ab",      null);
        checkLongest(prefixLookup, "abc",     "Result ABC");
        checkLongest(prefixLookup, "abcd",    "Result ABCD");
        checkLongest(prefixLookup, "abcde",   "Result ABCD");
        checkLongest(prefixLookup, "abcdef",  "Result ABCDEF");

        checkLongest(prefixLookup, "a-",      null);
        checkLongest(prefixLookup, "ab-",     null);
        checkLongest(prefixLookup, "abc-",    "Result ABC");
        checkLongest(prefixLookup, "abcd-",   "Result ABCD");
        checkLongest(prefixLookup, "abcde-",  "Result ABCD");
        checkLongest(prefixLookup, "abcdef-", "Result ABCDEF");

        checkLongest(prefixLookup, "abc\\t",  "Result ABC");
        checkLongest(prefixLookup, "abc€",    "Result ABC");
        checkLongest(prefixLookup, "abcd€",   "Result ABCD");

        // ----------------------------------------------------
        // Contains
        checkContains(prefixLookup, "MisMatch", false);

        // These are 1 char per character
        checkContains(prefixLookup, "你",       false);
        checkContains(prefixLookup, "你好",     false);

        // Same case
        checkContains(prefixLookup, "A",       false);
        checkContains(prefixLookup, "AB",      false);
        checkContains(prefixLookup, "ABC",     true);
        checkContains(prefixLookup, "ABCD",    true);
        checkContains(prefixLookup, "ABCDE",   false);
        checkContains(prefixLookup, "ABCDEF",  true);

        checkContains(prefixLookup, "A-",      false);
        checkContains(prefixLookup, "AB-",     false);
        checkContains(prefixLookup, "ABC-",    false);
        checkContains(prefixLookup, "ABCD-",   false);
        checkContains(prefixLookup, "ABCDE-",  false);
        checkContains(prefixLookup, "ABCDEF-", false);

        checkContains(prefixLookup, "ABC\\t",  false);
        checkContains(prefixLookup, "ABC€",    false);
        checkContains(prefixLookup, "ABCD€",   false);

        checkGetAllIterator(prefixLookup, "ABCD", "Result ABC", "Result ABCD");

        // Different case
        checkContains(prefixLookup, "a",       false);
        checkContains(prefixLookup, "ab",      false);
        checkContains(prefixLookup, "abc",     true);
        checkContains(prefixLookup, "abcd",    true);
        checkContains(prefixLookup, "abcde",   false);
        checkContains(prefixLookup, "abcdef",  true);

        checkContains(prefixLookup, "a-",      false);
        checkContains(prefixLookup, "ab-",     false);
        checkContains(prefixLookup, "abc-",    false);
        checkContains(prefixLookup, "abcd-",   false);
        checkContains(prefixLookup, "abcde-",  false);
        checkContains(prefixLookup, "abcdef-", false);

        checkContains(prefixLookup, "abc\\t",  false);
        checkContains(prefixLookup, "abc€",    false);
        checkContains(prefixLookup, "abcd€",   false);

        checkGetAllIterator(prefixLookup, "abcd", "Result ABC", "Result ABCD");
    }

    @Test
    void testCaseSensitiveLookup(){
        PrefixTrie<String> prefixLookup = new ASCIIPrefixTrie<>(true);
        prefixLookup.add("ABC",    "Result ABC");
        prefixLookup.add("ABCD",    "Result ABCD");
        // The ABCDE is missing !!!
        prefixLookup.add("ABCDEF",  "Result ABCDEF");

        // ----------------------------------------------------
        // Shortest Match
        checkShortest(prefixLookup, "MisMatch", null);
        // These are 1 char per character
        checkShortest(prefixLookup, "你好",     null);
        // These are 2 chars per character
        checkShortest(prefixLookup, "🖖👹",    null);

        // Same case
        checkShortest(prefixLookup, "A",       null);
        checkShortest(prefixLookup, "AB",      null);
        checkShortest(prefixLookup, "ABC",     "Result ABC");
        checkShortest(prefixLookup, "ABCD",    "Result ABC");
        checkShortest(prefixLookup, "ABCDE",   "Result ABC");
        checkShortest(prefixLookup, "ABCDEF",  "Result ABC");

        checkShortest(prefixLookup, "A-",      null);
        checkShortest(prefixLookup, "AB-",     null);
        checkShortest(prefixLookup, "ABC-",    "Result ABC");
        checkShortest(prefixLookup, "ABCD-",   "Result ABC");
        checkShortest(prefixLookup, "ABCDE-",  "Result ABC");
        checkShortest(prefixLookup, "ABCDEF-", "Result ABC");

        checkShortest(prefixLookup, "ABC\\t",  "Result ABC");
        checkShortest(prefixLookup, "ABC€",    "Result ABC");
        checkShortest(prefixLookup, "ABCD€",   "Result ABC");

        // Different case
        checkShortest(prefixLookup, "a",       null);
        checkShortest(prefixLookup, "ab",      null);
        checkShortest(prefixLookup, "abc",     null);
        checkShortest(prefixLookup, "abcd",    null);
        checkShortest(prefixLookup, "abcde",   null);
        checkShortest(prefixLookup, "abcdef",  null);

        checkShortest(prefixLookup, "a-",      null);
        checkShortest(prefixLookup, "ab-",     null);
        checkShortest(prefixLookup, "abc-",    null);
        checkShortest(prefixLookup, "abcd-",   null);
        checkShortest(prefixLookup, "abcde-",  null);
        checkShortest(prefixLookup, "abcdef-", null);

        checkShortest(prefixLookup, "abc\\t",  null);
        checkShortest(prefixLookup, "abc€",    null);
        checkShortest(prefixLookup, "abcd€",   null);

        // ----------------------------------------------------
        // Longest Match
        checkLongest(prefixLookup, "MisMatch", null);
        // These are 1 char per character
        checkLongest(prefixLookup, "你好",     null);
        // These are 2 chars per character
        checkLongest(prefixLookup, "🖖👹",    null);
            // Same case
        checkLongest(prefixLookup, "A",       null);
        checkLongest(prefixLookup, "AB",      null);
        checkLongest(prefixLookup, "ABC",     "Result ABC");
        checkLongest(prefixLookup, "ABCD",    "Result ABCD");
        checkLongest(prefixLookup, "ABCDE",   "Result ABCD");
        checkLongest(prefixLookup, "ABCDEF",  "Result ABCDEF");

        checkLongest(prefixLookup, "A-",      null);
        checkLongest(prefixLookup, "AB-",     null);
        checkLongest(prefixLookup, "ABC-",    "Result ABC");
        checkLongest(prefixLookup, "ABCD-",   "Result ABCD");
        checkLongest(prefixLookup, "ABCDE-",  "Result ABCD");
        checkLongest(prefixLookup, "ABCDEF-", "Result ABCDEF");

        checkLongest(prefixLookup, "ABC\\t",  "Result ABC");
        checkLongest(prefixLookup, "ABC€",    "Result ABC");
        checkLongest(prefixLookup, "ABCD€",   "Result ABCD");

        // Different case
        checkLongest(prefixLookup, "a",       null);
        checkLongest(prefixLookup, "ab",      null);
        checkLongest(prefixLookup, "abc",     null);
        checkLongest(prefixLookup, "abcd",    null);
        checkLongest(prefixLookup, "abcde",   null);
        checkLongest(prefixLookup, "abcdef",  null);

        checkLongest(prefixLookup, "a-",      null);
        checkLongest(prefixLookup, "ab-",     null);
        checkLongest(prefixLookup, "abc-",    null);
        checkLongest(prefixLookup, "abcd-",   null);
        checkLongest(prefixLookup, "abcde-",  null);
        checkLongest(prefixLookup, "abcdef-", null);

        checkLongest(prefixLookup, "abc\\t",  null);
        checkLongest(prefixLookup, "abc€",    null);
        checkLongest(prefixLookup, "abcd€",   null);

        // ----------------------------------------------------
        // Contains
        checkContains(prefixLookup, "MisMatch", false);

        // These are 1 char per character
        checkContains(prefixLookup, "你",      false);
        checkContains(prefixLookup, "你好",    false);
        // These are 2 chars per character
        checkContains(prefixLookup, "🖖",     false);
        checkContains(prefixLookup, "🖖👹",   false);

        // Same case
        checkContains(prefixLookup, "A",       false);
        checkContains(prefixLookup, "AB",      false);
        checkContains(prefixLookup, "ABC",     true);
        checkContains(prefixLookup, "ABCD",    true);
        checkContains(prefixLookup, "ABCDE",   false);
        checkContains(prefixLookup, "ABCDEF",  true);

        checkContains(prefixLookup, "A-",      false);
        checkContains(prefixLookup, "AB-",     false);
        checkContains(prefixLookup, "ABC-",    false);
        checkContains(prefixLookup, "ABCD-",   false);
        checkContains(prefixLookup, "ABCDE-",  false);
        checkContains(prefixLookup, "ABCDEF-", false);

        checkContains(prefixLookup, "ABC\\t",  false);
        checkContains(prefixLookup, "ABC€",    false);
        checkContains(prefixLookup, "ABCD€",   false);

        checkGetAllIterator(prefixLookup, "ABCD", "Result ABC", "Result ABCD");

        // Different case
        checkContains(prefixLookup, "a",       false);
        checkContains(prefixLookup, "ab",      false);
        checkContains(prefixLookup, "abc",     false);
        checkContains(prefixLookup, "abcd",    false);
        checkContains(prefixLookup, "abcde",   false);
        checkContains(prefixLookup, "abcdef",  false);

        checkContains(prefixLookup, "a-",      false);
        checkContains(prefixLookup, "ab-",     false);
        checkContains(prefixLookup, "abc-",    false);
        checkContains(prefixLookup, "abcd-",   false);
        checkContains(prefixLookup, "abcde-",  false);
        checkContains(prefixLookup, "abcdef-", false);

        checkContains(prefixLookup, "abc\\t",  false);
        checkContains(prefixLookup, "abc€",    false);
        checkContains(prefixLookup, "abcd€",   false);

        checkGetAllIterator(prefixLookup, "abcd"); // No output
    }

    @Test
    void testIteratorBasics() {
        PrefixTrie<String> prefixLookup = new ASCIIPrefixTrie<>(false);
        prefixLookup.add("A",       "Result A");
        prefixLookup.add("ABC",     "Result ABC");
        prefixLookup.add("ABCDE",   "Result ABCDE");
        prefixLookup.add("ABCDEFG", "Result ABCDEFG");

        Iterator<String> matches = prefixLookup.getAllMatches("aBcDeF");
        assertTrue(matches.hasNext());
        assertEquals("Result A", matches.next());
        assertTrue(matches.hasNext());
        assertEquals("Result ABC", matches.next());
        assertTrue(matches.hasNext());
        assertEquals("Result ABCDE", matches.next());
        assertFalse(matches.hasNext());

        assertThrows(NoSuchElementException.class, matches::next);
    }

    @Test
    void testCaseNonASCIIIterator() {
        PrefixTrie<String> prefixLookup = new ASCIIPrefixTrie<>(false);
        prefixLookup.add("",         "Empty");
        prefixLookup.add("A",        "Result A");
        prefixLookup.add("ABC",      "Result ABC");
        prefixLookup.add("ABCDE",    "Result ABCDE");
        prefixLookup.add("ABCDEFG",  "Result ABCDEFG");

        checkGetAllIterator(prefixLookup, "",           "Empty");
        checkGetAllIterator(prefixLookup, "aB",         "Empty", "Result A");
        checkGetAllIterator(prefixLookup, "aBc",        "Empty", "Result A", "Result ABC");
        checkGetAllIterator(prefixLookup, "aBc🖖",       "Empty", "Result A", "Result ABC");
        checkGetAllIterator(prefixLookup, "aBc🖖e",       "Empty", "Result A", "Result ABC");
        checkGetAllIterator(prefixLookup, "aBc🖖eF",     "Empty", "Result A", "Result ABC");
        checkGetAllIterator(prefixLookup, "aBc🖖eFgH",   "Empty", "Result A", "Result ABC");

        checkGetAllIterator(prefixLookup, "🖖",          "Empty");
        checkGetAllIterator(prefixLookup, "🖖BcDe",      "Empty");

        checkGetAllIterator(prefixLookup, "你好",          "Empty");
        checkGetAllIterator(prefixLookup, "你好DeF",       "Empty");
        checkGetAllIterator(prefixLookup, "你🖖DeFg",      "Empty");
    }

    @Test
    void testCaseINSensitiveIterator() {
        PrefixTrie<String> prefixLookup = new ASCIIPrefixTrie<>(false);
        prefixLookup.add("A",       "Result A");
        prefixLookup.add("ABC",     "Result ABC");
        prefixLookup.add("ABCDE",   "Result ABCDE");
        prefixLookup.add("ABCDEFG", "Result ABCDEFG");

        checkGetAllIterator(prefixLookup, "aBcDeF", "Result A", "Result ABC", "Result ABCDE");
    }
}
