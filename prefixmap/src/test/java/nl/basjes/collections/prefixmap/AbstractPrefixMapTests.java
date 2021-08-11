/*
 * Copyright (C) 2018-2020 Niels Basjes
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

import nl.basjes.collections.PrefixMap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class AbstractPrefixMapTests {

    abstract PrefixMap<String> createPrefixMap(boolean caseSensitive);

    protected void checkShortest(PrefixMap<String> prefixLookup, String prefix, String expected) {
        assertEquals(expected, prefixLookup.getShortestMatch(prefix),
            "Wrong 'ShortestMatch' result for '" + prefix + "'");
        assertEquals(expected, prefixLookup.getShortestMatch(prefix.toCharArray()),
            "Wrong 'ShortestMatch' result for '" + prefix + "' as char[]");
    }

    protected void checkLongest(PrefixMap<String> prefixLookup, String prefix, String expected) {
        assertEquals(expected, prefixLookup.getLongestMatch(prefix),
            "Wrong 'ShortestMatch' result for '" + prefix + "'");
        assertEquals(expected, prefixLookup.getLongestMatch(prefix.toCharArray()),
            "Wrong 'ShortestMatch' result for '" + prefix + "' as char[]");
    }

    protected void checkContains(PrefixMap<String> prefixLookup, String prefix, boolean expected) {
        assertEquals(expected, prefixLookup.containsPrefix(prefix),
            "Wrong 'ContainsPrefix' result for '" + prefix + "'");
        assertEquals(expected, prefixLookup.containsPrefix(prefix.toCharArray()),
            "Wrong 'ContainsPrefix' result for '" + prefix + "' as char[]");
    }

    protected void checkGetAllIterator(PrefixMap<String> prefixLookup, String prefix, String... expected) {
        List<String> result = new ArrayList<>();
        prefixLookup.getAllMatches(prefix).forEachRemaining(result::add);
        assertArrayEquals(expected, result.toArray(), "Wrong 'getAllMatches' result for '" + prefix + "'");

        result.clear();
        prefixLookup.getAllMatches(prefix.toCharArray()).forEachRemaining(result::add);
        assertArrayEquals(expected, result.toArray(), "Wrong 'getAllMatches' result for '" + prefix + "' as char[]");
    }

    @Test
    void testPutNullPrefix() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            PrefixMap<String> prefixLookup = createPrefixMap(true);
            prefixLookup.put(null, "Something");
        });
        assertEquals("The prefix may not be null", exception.getMessage());
    }

    @Test
    void testPutNullValue() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            PrefixMap<String> prefixLookup = createPrefixMap(true);
            prefixLookup.put("Something", null);
        });
        assertEquals("The value may not be null", exception.getMessage());
    }

    @Test
    void testRemoveNullValue() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            PrefixMap<String> prefixLookup = createPrefixMap(true);
            prefixLookup.remove(null);
        });
        assertEquals("The prefix may not be null", exception.getMessage());
    }

    @Test
    void testSize() {
        PrefixMap<String> prefixLookup = createPrefixMap(true);

        assertEquals(0,         prefixLookup.size());
        assertEquals(null,      prefixLookup.put("One",     "One"));
        assertEquals(1,         prefixLookup.size());
        assertEquals(null,      prefixLookup.put("Two",     "Two"));
        assertEquals(2,         prefixLookup.size());
        assertEquals("One",     prefixLookup.put("One",     "111"));
        assertEquals(2,         prefixLookup.size());
        assertEquals(null,      prefixLookup.put("Three",   "Three"));
        assertEquals(3,         prefixLookup.size());
        assertEquals("Two",     prefixLookup.put("Two",     "222"));
        assertEquals(3,         prefixLookup.size());
        assertEquals("Three",   prefixLookup.put("Three",   "333"));
        assertEquals(3,         prefixLookup.size());

        prefixLookup.clear();

        assertEquals(0,         prefixLookup.size());
        assertEquals(null,      prefixLookup.put("One",     "One"));
        assertEquals(1,         prefixLookup.size());
        assertEquals(null,      prefixLookup.put("Two",     "Two"));
        assertEquals(2,         prefixLookup.size());
        assertEquals("One",     prefixLookup.put("One",     "111"));
        assertEquals(2,         prefixLookup.size());
        assertEquals(null,      prefixLookup.put("Three",   "Three"));
        assertEquals(3,         prefixLookup.size());
        assertEquals("Two",     prefixLookup.put("Two",     "222"));
        assertEquals(3,         prefixLookup.size());
        assertEquals("Three",   prefixLookup.put("Three",   "333"));
        assertEquals(3,         prefixLookup.size());
    }

    @Test
    void testRemoveCaseSensitive() {
        PrefixMap<String> prefixLookup = createPrefixMap(true);

        // Initial filling
        assertEquals(null,      prefixLookup.put("A",   "ONE"));
        assertEquals(null,      prefixLookup.put("AB",  "TWO"));
        assertEquals(null,      prefixLookup.put("ABC", "THREE"));
        assertEquals(null,      prefixLookup.put("a",   "one"));
        assertEquals(null,      prefixLookup.put("ab",  "two"));
        assertEquals(null,      prefixLookup.put("abc", "three"));
        assertEquals(6,         prefixLookup.size());

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
        assertEquals(5,         prefixLookup.size());

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
        assertEquals(null,      prefixLookup.put("A",   "ONE"));
        assertEquals(6,         prefixLookup.size());

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
        assertEquals(5,         prefixLookup.size());

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
        PrefixMap<String> prefixLookup = createPrefixMap(false);

        // Initial filling
        assertEquals(null,      prefixLookup.put("A",   "ONE"));
        assertEquals(null,      prefixLookup.put("AB",  "TWO"));
        assertEquals(null,      prefixLookup.put("ABC", "THREE"));
        assertEquals("ONE",     prefixLookup.put("a",   "one"));
        assertEquals("TWO",     prefixLookup.put("ab",  "two"));
        assertEquals("THREE",   prefixLookup.put("abc", "three"));
        assertEquals(3,         prefixLookup.size());

        // Test uppercase
        assertEquals("one",     prefixLookup.get("A"));
        assertEquals("two",     prefixLookup.get("AB"));
        assertEquals("three",   prefixLookup.get("ABC"));
        assertEquals(null,   prefixLookup.get("ABCD"));
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
        assertEquals(null,   prefixLookup.get("abcd"));
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
        assertEquals(3,         prefixLookup.size());

        // Now remove non-existing entry (Partial match)
        assertEquals(null,      prefixLookup.remove("ABDDD"));
        assertEquals(3,         prefixLookup.size());

        // Now remove non-existing entry (Full prefix)
        assertEquals(null,      prefixLookup.remove("ABCD"));
        assertEquals(3,         prefixLookup.size());

        // Now remove one entry
        assertEquals("one",     prefixLookup.remove("A"));
        assertEquals(2,         prefixLookup.size());

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
        assertEquals(null,      prefixLookup.put("A",   "ONE"));
        assertEquals(3,         prefixLookup.size());

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

    private PrefixMap<String> createSerializationInstance() {
        PrefixMap<String> brandLookup = createPrefixMap(false);

        brandLookup.put("RM-", "Nokia");
        brandLookup.put("GT-", "Samsung");
        return brandLookup;
    }

    private void verifySerializationInstance(PrefixMap<String> instance) {
        assertEquals("Samsung", instance.getLongestMatch("gT-i8190N"));
        assertEquals("Samsung", instance.getLongestMatch("Gt-I8190n"));
        assertEquals("Nokia",   instance.getLongestMatch("rM-1092"));
        assertEquals("Nokia",   instance.getLongestMatch("Rm-1092"));
    }

    @Test
    void testEntrySet() {
        PrefixMap<String> instance = createSerializationInstance();
        verifySerializationInstance(instance);

        PrefixMap<String> instance2 = createPrefixMap(false);
        instance.forEach(instance2::put);
        verifySerializationInstance(instance2);
    }

}
