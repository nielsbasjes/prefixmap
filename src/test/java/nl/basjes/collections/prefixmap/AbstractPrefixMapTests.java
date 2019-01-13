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

    abstract PrefixMap<String, Character, String> createPrefixMap(boolean caseSensitive);

    @Rule
    public final transient ExpectedException expectedEx = ExpectedException.none();

    protected void checkShortest(PrefixMap<String, Character, String> prefixLookup, String prefix, String expected) {
        assertEquals("Wrong 'ShortestMatch' result for '" + prefix + "'", expected, prefixLookup.getShortestMatch(prefix));
    }

    protected void checkLongest(PrefixMap<String, Character, String> prefixLookup, String prefix, String expected) {
        assertEquals("Wrong 'LongestMatch' result for '" + prefix + "'", expected, prefixLookup.getLongestMatch(prefix));
    }

    protected void checkContains(PrefixMap<String, Character, String> prefixLookup, String prefix, boolean expected) {
        assertEquals("Wrong 'Contains' result for '" + prefix + "'", expected, prefixLookup.containsPrefix(prefix));
    }

    @Test
    public void testNullPrefix() {
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("The prefix may not be null");
        PrefixMap<String, Character, String> prefixLookup = createPrefixMap(true);
        prefixLookup.put(null, "Something");
    }

    @Test
    public void testNullValue() {
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("The value may not be null");
        PrefixMap<String, Character, String> prefixLookup = createPrefixMap(true);
        prefixLookup.put("Something", null);
    }

    @Test
    public void testSize() {
        PrefixMap<String, Character, String> prefixLookup = createPrefixMap(true);

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

}
