/*
 * Copyright (C) 2018-2025 Niels Basjes
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class AbstractPrefixTrieTests {

    abstract PrefixTrie<String> createPrefixTrie(boolean caseSensitive);

    protected void checkShortest(PrefixTrie<String> prefixLookup, String prefix, String expected) {
        assertEquals(expected, prefixLookup.getShortestMatch(prefix),
            "Wrong 'ShortestMatch' result for '" + prefix + "'");
        assertEquals(expected, prefixLookup.getShortestMatch(prefix.codePoints().iterator()),
            "Wrong 'ShortestMatch' result for '" + prefix + "' as int[]");
    }

    protected void checkLongest(PrefixTrie<String> prefixLookup, String prefix, String expected) {
        assertEquals(expected, prefixLookup.getLongestMatch(prefix),
            "Wrong 'ShortestMatch' result for '" + prefix + "'");
        assertEquals(expected, prefixLookup.getLongestMatch(prefix.codePoints().iterator()),
            "Wrong 'ShortestMatch' result for '" + prefix + "' as int[]");
    }

    protected void checkContains(PrefixTrie<String> prefixLookup, String prefix, boolean expected) {
        assertEquals(expected, prefixLookup.containsPrefix(prefix),
            "Wrong 'ContainsPrefix' result for '" + prefix + "'");
        assertEquals(expected, prefixLookup.containsPrefix(prefix.codePoints().iterator()),
            "Wrong 'ContainsPrefix' result for '" + prefix + "' as int[]");
    }

    protected void checkGetAllIterator(PrefixTrie<String> prefixLookup, String prefix, String... expected) {
        List<String> result = new ArrayList<>();
        prefixLookup.getAllMatches(prefix).forEachRemaining(result::add);
        assertArrayEquals(expected, result.toArray(), "Wrong 'getAllMatches' result for '" + prefix + "'");

        result.clear();
        prefixLookup.getAllMatches(prefix.codePoints().iterator()).forEachRemaining(result::add);
        assertArrayEquals(expected, result.toArray(), "Wrong 'getAllMatches' result for '" + prefix + "' as int[]");
    }

    @Test
    void testPutNullPrefix() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            PrefixTrie<String> prefixLookup = createPrefixTrie(true);
            prefixLookup.add((String)null, "Something");
        });
        assertEquals("The prefix may not be null", exception.getMessage());
    }

    @Test
    void testPutNullValue() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            PrefixTrie<String> prefixLookup = createPrefixTrie(true);
            prefixLookup.add("Something", null);
        });
        assertEquals("The value may not be null", exception.getMessage());
    }

    @Test
    void testRemoveNullValue() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            PrefixTrie<String> prefixLookup = createPrefixTrie(true);
            prefixLookup.remove((String)null);
        });
        assertEquals("The prefix may not be null", exception.getMessage());
    }

    @Test
    void testRemoveEmptyPrefix() {
        PrefixTrie<String> prefixLookup = createPrefixTrie(true);

        // Initial filling
        assertEquals(null, prefixLookup.add("A", "ONE"));
        assertEquals(null, prefixLookup.add("a", "one"));

        assertEquals(null, prefixLookup.getShortestMatch(""));
        assertEquals(null, prefixLookup.getLongestMatch(""));
        assertEquals(null, prefixLookup.remove(""));
    }

    @Test
    void testPartialPrefixNoMatch() {
        PrefixTrie<String> prefixLookup = createPrefixTrie(true);

        // Initial filling
        assertEquals(null, prefixLookup.add("ABC", "ONE"));

        assertEquals(null, prefixLookup.getShortestMatch("ABD"));
        assertEquals(null, prefixLookup.getLongestMatch("ABD"));

        prefixLookup.remove("ABC");
        assertEquals(null, prefixLookup.getShortestMatch("ABD"));
        assertEquals(null, prefixLookup.getLongestMatch("ABD"));
    }

    @Test
    void testRemoveCaseSensitive() {
        PrefixTrie<String> prefixLookup = createPrefixTrie(true);

        // Initial filling
        assertEquals(null,      prefixLookup.add("A",   "ONE"));
        assertEquals(null,      prefixLookup.add("AB",  "TWO"));
        assertEquals(null,      prefixLookup.add("ABC", "THREE"));
        assertEquals(null,      prefixLookup.add("a",   "one"));
        assertEquals(null,      prefixLookup.add("ab",  "two"));
        assertEquals(null,      prefixLookup.add("abc", "three"));

        // Test uppercase
        assertEquals("ONE",     prefixLookup.getShortestMatch("A"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("AB"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("ABC"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("ABCD"));
        assertEquals("ONE",     prefixLookup.getLongestMatch("A"));
        assertEquals("TWO",     prefixLookup.getLongestMatch("AB"));
        assertEquals("THREE",   prefixLookup.getLongestMatch("ABC"));
        assertEquals("THREE",   prefixLookup.getLongestMatch("ABCD"));

        // Test lowercase
        assertEquals("one",     prefixLookup.getShortestMatch("a"));
        assertEquals("one",     prefixLookup.getShortestMatch("ab"));
        assertEquals("one",     prefixLookup.getShortestMatch("abc"));
        assertEquals("one",     prefixLookup.getShortestMatch("abcd"));
        assertEquals("one",     prefixLookup.getLongestMatch("a"));
        assertEquals("two",     prefixLookup.getLongestMatch("ab"));
        assertEquals("three",   prefixLookup.getLongestMatch("abc"));
        assertEquals("three",   prefixLookup.getLongestMatch("abcd"));

        // =====================

        // Now remove one entry (uppercase)
        assertEquals("ONE",     prefixLookup.remove("A"));

        // Test uppercase (CHANGED!)
        assertEquals(null,      prefixLookup.getShortestMatch("A"));
        assertEquals("TWO",     prefixLookup.getShortestMatch("AB"));
        assertEquals("TWO",     prefixLookup.getShortestMatch("ABC"));
        assertEquals(null,      prefixLookup.getLongestMatch("A"));
        assertEquals("TWO",     prefixLookup.getLongestMatch("AB"));
        assertEquals("THREE",   prefixLookup.getLongestMatch("ABC"));

        // Test lowercase (NOT changed)
        assertEquals("one",     prefixLookup.getShortestMatch("a"));
        assertEquals("one",     prefixLookup.getShortestMatch("ab"));
        assertEquals("one",     prefixLookup.getShortestMatch("abc"));
        assertEquals("one",     prefixLookup.getLongestMatch("a"));
        assertEquals("two",     prefixLookup.getLongestMatch("ab"));
        assertEquals("three",   prefixLookup.getLongestMatch("abc"));

        // =====================

        // Now re-add the removed entry
        assertEquals(null,      prefixLookup.add("A",   "ONE"));

        // Test uppercase (Original tests)
        assertEquals("ONE",     prefixLookup.getShortestMatch("A"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("AB"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("ABC"));
        assertEquals("ONE",     prefixLookup.getLongestMatch("A"));
        assertEquals("TWO",     prefixLookup.getLongestMatch("AB"));
        assertEquals("THREE",   prefixLookup.getLongestMatch("ABC"));

        // Test lowercase (Original tests)
        assertEquals("one",     prefixLookup.getShortestMatch("a"));
        assertEquals("one",     prefixLookup.getShortestMatch("ab"));
        assertEquals("one",     prefixLookup.getShortestMatch("abc"));
        assertEquals("one",     prefixLookup.getLongestMatch("a"));
        assertEquals("two",     prefixLookup.getLongestMatch("ab"));
        assertEquals("three",   prefixLookup.getLongestMatch("abc"));

        // =====================

        // Now remove one entry (lowercase)
        assertEquals("one",     prefixLookup.remove("a"));

        // Test uppercase (NOT changed)
        assertEquals("ONE",     prefixLookup.getShortestMatch("A"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("AB"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("ABC"));
        assertEquals("ONE",     prefixLookup.getLongestMatch("A"));
        assertEquals("TWO",     prefixLookup.getLongestMatch("AB"));
        assertEquals("THREE",   prefixLookup.getLongestMatch("ABC"));

        // Test lowercase (CHANGED!)
        assertEquals(null,      prefixLookup.getShortestMatch("a"));
        assertEquals("two",     prefixLookup.getShortestMatch("ab"));
        assertEquals("two",     prefixLookup.getShortestMatch("abc"));
        assertEquals(null,      prefixLookup.getLongestMatch("a"));
        assertEquals("two",     prefixLookup.getLongestMatch("ab"));
        assertEquals("three",   prefixLookup.getLongestMatch("abc"));
    }

    @Test
    void testRemoveCaseINSensitive() {
        PrefixTrie<String> prefixLookup = createPrefixTrie(false);

        // Initial filling
        assertEquals(null,      prefixLookup.add("A",   "ONE"));
        assertEquals(null,      prefixLookup.add("AB",  "TWO"));
        assertEquals(null,      prefixLookup.add("ABC", "THREE"));
        assertEquals("ONE",     prefixLookup.add("a",   "one"));
        assertEquals("TWO",     prefixLookup.add("ab",  "two"));
        assertEquals("THREE",   prefixLookup.add("abc", "three"));

        // Test uppercase
        assertEquals("one",     prefixLookup.get("A"));
        assertEquals("two",     prefixLookup.get("AB"));
        assertEquals("three",   prefixLookup.get("ABC"));
        assertEquals(null,      prefixLookup.get("ABCD"));
        assertEquals("one",     prefixLookup.getShortestMatch("A"));
        assertEquals("one",     prefixLookup.getShortestMatch("AB"));
        assertEquals("one",     prefixLookup.getShortestMatch("ABC"));
        assertEquals("one",     prefixLookup.getShortestMatch("ABCD"));
        assertEquals("one",     prefixLookup.getLongestMatch("A"));
        assertEquals("two",     prefixLookup.getLongestMatch("AB"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABC"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABCD"));

        // Test lowercase
        assertEquals("one",     prefixLookup.get("a"));
        assertEquals("two",     prefixLookup.get("ab"));
        assertEquals("three",   prefixLookup.get("abc"));
        assertEquals(null,      prefixLookup.get("abcd"));
        assertEquals("one",     prefixLookup.getShortestMatch("a"));
        assertEquals("one",     prefixLookup.getShortestMatch("ab"));
        assertEquals("one",     prefixLookup.getShortestMatch("abc"));
        assertEquals("one",     prefixLookup.getShortestMatch("abcd"));
        assertEquals("one",     prefixLookup.getLongestMatch("a"));
        assertEquals("two",     prefixLookup.getLongestMatch("ab"));
        assertEquals("three",   prefixLookup.getLongestMatch("abc"));
        assertEquals("three",   prefixLookup.getLongestMatch("abcd"));

        // Now remove non-existing entry (Totally unrelated)
        assertEquals(null,      prefixLookup.remove("Does not exist"));

        // Now remove non-existing entry (Partial match)
        assertEquals(null,      prefixLookup.remove("ABDDD"));

        // Now remove non-existing entry (Full prefix)
        assertEquals(null,      prefixLookup.remove("ABCD"));

        // Now remove one entry
        assertEquals("one",     prefixLookup.remove("A"));

        // Test uppercase (CHANGED!)
        assertEquals(null,      prefixLookup.get("A"));
        assertEquals("two",     prefixLookup.get("AB"));
        assertEquals("three",   prefixLookup.get("ABC"));
        assertEquals(null,      prefixLookup.get("ABCD"));
        assertEquals(null,      prefixLookup.getShortestMatch("A"));
        assertEquals("two",     prefixLookup.getShortestMatch("AB"));
        assertEquals("two",     prefixLookup.getShortestMatch("ABC"));
        assertEquals("two",     prefixLookup.getShortestMatch("ABCD"));
        assertEquals(null,      prefixLookup.getLongestMatch("A"));
        assertEquals("two",     prefixLookup.getLongestMatch("AB"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABC"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABCD"));

        // Test lowercase (CHANGED!)
        assertEquals(null,      prefixLookup.get("a"));
        assertEquals("two",     prefixLookup.get("ab"));
        assertEquals("three",   prefixLookup.get("abc"));
        assertEquals(null,      prefixLookup.get("abcd"));
        assertEquals(null,      prefixLookup.getShortestMatch("a"));
        assertEquals("two",     prefixLookup.getShortestMatch("ab"));
        assertEquals("two",     prefixLookup.getShortestMatch("abc"));
        assertEquals("two",     prefixLookup.getShortestMatch("abcd"));
        assertEquals(null,      prefixLookup.getLongestMatch("a"));
        assertEquals("two",     prefixLookup.getLongestMatch("ab"));
        assertEquals("three",   prefixLookup.getLongestMatch("abc"));
        assertEquals("three",   prefixLookup.getLongestMatch("abcd"));

        // Now re-add the removed entry
        assertEquals(null,      prefixLookup.add("A",   "ONE"));

        // Test uppercase (Original tests)
        assertEquals("ONE",     prefixLookup.get("A"));
        assertEquals("two",     prefixLookup.get("AB"));
        assertEquals("three",   prefixLookup.get("ABC"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("A"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("AB"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("ABC"));
        assertEquals("ONE",     prefixLookup.getLongestMatch("A"));
        assertEquals("two",     prefixLookup.getLongestMatch("AB"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABC"));

        // Test lowercase (Original tests)
        assertEquals("ONE",     prefixLookup.get("a"));
        assertEquals("two",     prefixLookup.get("ab"));
        assertEquals("three",   prefixLookup.get("abc"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("a"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("ab"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("abc"));
        assertEquals("ONE",     prefixLookup.getLongestMatch("a"));
        assertEquals("two",     prefixLookup.getLongestMatch("ab"));
        assertEquals("three",   prefixLookup.getLongestMatch("abc"));
    }

    private PrefixTrie<String> createSerializationInstance() {
        PrefixTrie<String> brandLookup = createPrefixTrie(false);

        brandLookup.add("RM-", "Nokia");
        brandLookup.add("GT-", "Samsung");
        return brandLookup;
    }

    private void verifySerializationInstance(PrefixTrie<String> instance) {
        assertEquals("Samsung", instance.getLongestMatch("gT-i8190N"));
        assertEquals("Samsung", instance.getLongestMatch("Gt-I8190n"));
        assertEquals("Nokia",   instance.getLongestMatch("rM-1092"));
        assertEquals("Nokia",   instance.getLongestMatch("Rm-1092"));
    }


    @Test
    void testIteratorEmpty() {
        PrefixTrie<String> prefixLookup = createPrefixTrie(false);
        Iterator<String> matches;

        matches = prefixLookup.getAllMatches("");
        assertFalse(matches.hasNext());
        assertThrows(NoSuchElementException.class, matches::next);

        matches = prefixLookup.getAllMatches("Anything");
        assertFalse(matches.hasNext());
        assertThrows(NoSuchElementException.class, matches::next);
    }

    @Test
    void testNPEOnNullInput() {
        PrefixTrie<String> prefixLookup = createPrefixTrie(false);
        // Just don't pass a null value.
        assertThrows(NullPointerException.class, () -> prefixLookup.getAllMatches((String) null));
        assertThrows(NullPointerException.class, () -> prefixLookup.getAllMatches((PrimitiveIterator.OfInt) null));
    }

    @Test
    void testIteratorMatchEmptyString() {
        PrefixTrie<String> prefixLookup = createPrefixTrie(false);
        Iterator<String> matches;

        // Only a single empty string
        prefixLookup.add("", "Result Empty");
        matches = prefixLookup.getAllMatches("");
        assertTrue(matches.hasNext());
        assertEquals("Result Empty", matches.next());
        assertFalse(matches.hasNext());
        assertThrows(NoSuchElementException.class, matches::next);

    }

    @Test
    void testIteratorZeroMatches() {
        // Normal value but no matches at all
        PrefixTrie<String> prefixLookup = createPrefixTrie(false);
        prefixLookup.add("ABC",       "Result Empty");
        Iterator<String> matches = prefixLookup.getAllMatches("XYZ");
        assertFalse(matches.hasNext());
        assertThrows(NoSuchElementException.class, matches::next);
    }

    @Test
    void testIteratorSingleMatch() {
        PrefixTrie<String> prefixLookup = createPrefixTrie(false);
        prefixLookup.add("A",       "Result A");
        prefixLookup.add("ABC",     "Result ABC");
        prefixLookup.add("ABCDE",   "Result ABCDE");
        prefixLookup.add("ABCDEFG", "Result ABCDEFG");

        Iterator<String> matches = prefixLookup.getAllMatches("aBXYZ");
        assertTrue(matches.hasNext());
        assertEquals("Result A", matches.next());
        assertFalse(matches.hasNext());

        assertThrows(NoSuchElementException.class, matches::next);
    }

    @Test
    void testIteratorNormalUse() {
        PrefixTrie<String> prefixLookup = createPrefixTrie(false);
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

}
