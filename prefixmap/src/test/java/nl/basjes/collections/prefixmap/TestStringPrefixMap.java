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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestStringPrefixMap extends AbstractPrefixMapTests {

    @Override
    PrefixMap<String> createPrefixMap(boolean caseSensitive) {
        return new StringPrefixMap<>(caseSensitive);
    }

    @Test
    void testCaseINSensitiveLookup(){
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("ABC",    "Result ABC");
        prefixMap.put("ABCD",    "Result ABCD");
        // The ABCDE is missing !!!

        PrefixMap<String> prefixLookup = new StringPrefixMap<>(false);
        prefixLookup.putAll(prefixMap);

        prefixLookup.put("ABCDEF",  "Result ABCDEF");
        // These are 1 char per character
        prefixLookup.put("你", "Hello in Chinese");
        // These are 2 chars per character
        prefixLookup.put("🖖", "May the force be with you (🖖)");

        // ----------------------------------------------------
        // Shortest Match
        checkShortest(prefixLookup, "MisMatch", null);

        // These are 1 char per character
        checkShortest(prefixLookup, "你好",     "Hello in Chinese");

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
        checkLongest(prefixLookup, "你好",     "Hello in Chinese");

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
        checkContains(prefixLookup, "你",      true);
        checkContains(prefixLookup, "你好",    false);

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
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("ABC",    "Result ABC");
        prefixMap.put("ABCD",    "Result ABCD");
        // The ABCDE is missing !!!

        PrefixMap<String> prefixLookup = new StringPrefixMap<>(true);
        prefixLookup.putAll(prefixMap);

        prefixLookup.put("ABCDEF",  "Result ABCDEF");
        // These are 1 char per character
        prefixLookup.put("你", "Hello in Chinese");
        // These are 2 chars per character
        prefixLookup.put("🖖", "May the force be with you (🖖)");

        // ----------------------------------------------------
        // Shortest Match
        checkShortest(prefixLookup, "MisMatch", null);

        // These are 1 char per character
        checkShortest(prefixLookup, "你好",     "Hello in Chinese");
        // These are 2 chars per character
        checkShortest(prefixLookup, "🖖👹",     "May the force be with you (🖖)");

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
        checkLongest(prefixLookup, "你好",     "Hello in Chinese");
        // These are 2 chars per character
        checkLongest(prefixLookup, "🖖👹",    "May the force be with you (🖖)");

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
        checkContains(prefixLookup, "你",      true);
        checkContains(prefixLookup, "你好",    false);
        // These are 2 chars per character
        checkContains(prefixLookup, "🖖",     true);
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
        PrefixMap<String> prefixLookup = new StringPrefixMap<>(false);
        prefixLookup.put("A",       "Result A");
        prefixLookup.put("ABC",     "Result ABC");
        prefixLookup.put("ABCDE",   "Result ABCDE");
        prefixLookup.put("ABCDEFG", "Result ABCDEFG");

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
    void testCaseINSensitiveIterator() {
        PrefixMap<String> prefixLookup = new StringPrefixMap<>(false);
        prefixLookup.put("A",       "Result A");
        prefixLookup.put("ABC",     "Result ABC");
        prefixLookup.put("ABCDE",   "Result ABCDE");
        prefixLookup.put("ABCDEFG", "Result ABCDEFG");

        checkGetAllIterator(prefixLookup, "aBcDeF", "Result A", "Result ABC", "Result ABCDE");
    }

    @Test
    void verifyDocumentationExampleUsage() {
        // Parameter caseSensitive=false --> so lookups are caseINsensitive
        PrefixMap<String> brandLookup = new StringPrefixMap<>(false);

        brandLookup.put("RM-", "Nokia");
        brandLookup.put("GT-", "Samsung");

        String brandGT = brandLookup.getLongestMatch("GT-I8190N");   // --> "Samsung"
        String brandRM = brandLookup.getLongestMatch("RM-1092");     // --> "Nokia"

        assertEquals("Samsung", brandGT);
        assertEquals("Nokia", brandRM);
    }

}
