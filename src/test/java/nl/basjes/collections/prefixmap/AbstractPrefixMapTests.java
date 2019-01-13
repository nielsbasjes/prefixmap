/*
 * Copyright (C) 2018-2019 Niels Basjes
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public abstract class AbstractPrefixMapTests {

    abstract PrefixMap<String> createPrefixMap(boolean caseSensitive);

    @Rule
    public final transient ExpectedException expectedEx = ExpectedException.none();

    protected void checkShortest(PrefixMap<String> prefixLookup, String prefix, String expected) {
        assertEquals("Wrong 'ShortestMatch' result for '" + prefix + "'", expected, prefixLookup.getShortestMatch(prefix));
    }

    protected void checkLongest(PrefixMap<String> prefixLookup, String prefix, String expected) {
        assertEquals("Wrong 'LongestMatch' result for '" + prefix + "'", expected, prefixLookup.getLongestMatch(prefix));
    }

    protected void checkContains(PrefixMap<String> prefixLookup, String prefix, boolean expected) {
        assertEquals("Wrong 'Contains' result for '" + prefix + "'", expected, prefixLookup.containsPrefix(prefix));
    }

    @Test
    public void testPutNullPrefix() {
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("The prefix may not be null");
        PrefixMap<String> prefixLookup = createPrefixMap(true);
        prefixLookup.put(null, "Something");
    }

    @Test
    public void testPutNullValue() {
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("The value may not be null");
        PrefixMap<String> prefixLookup = createPrefixMap(true);
        prefixLookup.put("Something", null);
    }

    @Test
    public void testRemoveNullValue() {
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("The prefix may not be null");
        PrefixMap<String> prefixLookup = createPrefixMap(true);
        prefixLookup.remove(null);
    }

    @Test
    public void testSize() {
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
    public void testRemoveCaseSensitive() {
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
        assertEquals("ONE",     prefixLookup.getLongestMatch("A"));
        assertEquals("TWO",     prefixLookup.getLongestMatch("AB"));
        assertEquals("THREE",   prefixLookup.getLongestMatch("ABC"));

        // Test lowercase
        assertEquals("one",     prefixLookup.getShortestMatch("a"));
        assertEquals("one",     prefixLookup.getShortestMatch("ab"));
        assertEquals("one",     prefixLookup.getShortestMatch("abc"));
        assertEquals("one",     prefixLookup.getLongestMatch("a"));
        assertEquals("two",     prefixLookup.getLongestMatch("ab"));
        assertEquals("three",   prefixLookup.getLongestMatch("abc"));

        // Now remove one entry
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
    }

    @Test
    public void testRemoveCaseINSensitive() {
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
        assertEquals("one",     prefixLookup.getShortestMatch("A"));
        assertEquals("one",     prefixLookup.getShortestMatch("AB"));
        assertEquals("one",     prefixLookup.getShortestMatch("ABC"));
        assertEquals("one",     prefixLookup.getLongestMatch("A"));
        assertEquals("two",     prefixLookup.getLongestMatch("AB"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABC"));

        // Test lowercase
        assertEquals("one",     prefixLookup.getShortestMatch("a"));
        assertEquals("one",     prefixLookup.getShortestMatch("ab"));
        assertEquals("one",     prefixLookup.getShortestMatch("abc"));
        assertEquals("one",     prefixLookup.getLongestMatch("a"));
        assertEquals("two",     prefixLookup.getLongestMatch("ab"));
        assertEquals("three",   prefixLookup.getLongestMatch("abc"));

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
        assertEquals(null,      prefixLookup.getShortestMatch("A"));
        assertEquals("two",     prefixLookup.getShortestMatch("ABC"));
        assertEquals("two",     prefixLookup.getShortestMatch("AB"));
        assertEquals(null,      prefixLookup.getLongestMatch("A"));
        assertEquals("two",     prefixLookup.getLongestMatch("AB"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABC"));

        // Test lowercase (CHANGED!)
        assertEquals(null,      prefixLookup.getShortestMatch("a"));
        assertEquals("two",     prefixLookup.getShortestMatch("ab"));
        assertEquals("two",     prefixLookup.getShortestMatch("abc"));
        assertEquals(null,      prefixLookup.getLongestMatch("a"));
        assertEquals("two",     prefixLookup.getLongestMatch("ab"));
        assertEquals("three",   prefixLookup.getLongestMatch("abc"));

        // Now re-add the removed entry
        assertEquals(null,      prefixLookup.put("A",   "ONE"));
        assertEquals(3,         prefixLookup.size());

        // Test uppercase (Original tests)
        assertEquals("ONE",     prefixLookup.getShortestMatch("A"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("AB"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("ABC"));
        assertEquals("ONE",     prefixLookup.getLongestMatch("A"));
        assertEquals("two",     prefixLookup.getLongestMatch("AB"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABC"));

        // Test lowercase (Original tests)
        assertEquals("ONE",     prefixLookup.getShortestMatch("a"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("ab"));
        assertEquals("ONE",     prefixLookup.getShortestMatch("abc"));
        assertEquals("ONE",     prefixLookup.getLongestMatch("a"));
        assertEquals("two",     prefixLookup.getLongestMatch("ab"));
        assertEquals("three",   prefixLookup.getLongestMatch("abc"));
    }

}
