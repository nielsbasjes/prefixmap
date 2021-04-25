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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ASCIIPrefixMapTest extends AbstractPrefixMapTests {

    @Override
    PrefixMap<String> createPrefixMap(boolean caseSensitive) {
        return new ASCIIPrefixMap<>(caseSensitive);
    }

    @Test
    void testPutNonASCIIPrefix() {
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("你好", "Hello in Chinese");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new ASCIIPrefixMap<>(false).putAll(prefixMap)
        );
        assertEquals("Only readable ASCII is allowed as prefix !!!", exception.getMessage());
    }

    @Test
    void testPutNonASCIIValue() {
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("Hello in Chinese", "你好");
        assertDoesNotThrow(
            () -> new ASCIIPrefixMap<>(false).putAll(prefixMap)
        );
    }

    @Test
    void testRemoveNonASCIIPrefix() {
        PrefixMap<String> prefixLookup = new ASCIIPrefixMap<>(false);
        prefixLookup.put("Something",    "To ensure not empty");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> prefixLookup.remove("你好")
        );
        assertEquals("Only readable ASCII is allowed as prefix !!!", exception.getMessage());
        assertEquals(1, prefixLookup.size());
    }

    @Test
    void testPutEmojiPrefix() {
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("🖖", "May the force be with you (🖖)");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new ASCIIPrefixMap<>(false).putAll(prefixMap)
        );
        assertEquals("Only readable ASCII is allowed as prefix !!!", exception.getMessage());
    }

    @Test
    void testPutEmojiValue() {
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("LLAP", "May the force be with you (🖖)");
        assertDoesNotThrow(() -> new ASCIIPrefixMap<>(false).putAll(prefixMap));
    }

    @Test
    void testRemoveEmojiPrefix() {
        PrefixMap<String> prefixLookup = new ASCIIPrefixMap<>(false);
        prefixLookup.put("Something",    "To ensure not empty");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> prefixLookup.remove("🖖")
        );
        assertEquals("Only readable ASCII is allowed as prefix !!!", exception.getMessage());
        assertEquals(1, prefixLookup.size());
    }

    @Test
    void testCaseINSensitiveLookup(){
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("ABC",    "Result ABC");
        prefixMap.put("ABCD",    "Result ABCD");
        // The ABCDE is missing !!!

        PrefixMap<String> prefixLookup = new ASCIIPrefixMap<>(false);
        prefixLookup.putAll(prefixMap);

        prefixLookup.put("ABCDEF",  "Result ABCDEF");

        // ----------------------------------------------------
        // Shortest Match
        checkShortest(prefixLookup, "MisMatch", null);
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
    }

    @Test
    void testCaseSensitiveLookup(){
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("ABC",    "Result ABC");
        prefixMap.put("ABCD",    "Result ABCD");
        // The ABCDE is missing !!!

        PrefixMap<String> prefixLookup = new ASCIIPrefixMap<>(true);
        prefixLookup.putAll(prefixMap);

        prefixLookup.put("ABCDEF",  "Result ABCDEF");

        // ----------------------------------------------------
        // Shortest Match
        checkShortest(prefixLookup, "MisMatch", null);
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
        checkLongest(prefixLookup, "你好",     null);

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
    }

    @Test
    void testOffsetLookup(){
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("BC",     "Result BC");
        prefixMap.put("BCD",    "Result BCD");

        PrefixMap<String> prefixLookup = new ASCIIPrefixMap<>(true);
        prefixLookup.putAll(prefixMap);

        checkShortest(prefixLookup, "A",        0, null);
        checkShortest(prefixLookup, "AB",       0, null);
        checkShortest(prefixLookup, "ABC",      0, null);
        checkShortest(prefixLookup, "ABCD",     1, "Result BC");
        checkShortest(prefixLookup, "ABCD",     2, null);
        checkShortest(prefixLookup, "ABCD",     3, null);
        checkShortest(prefixLookup, "ABCD",     4, null);

        checkLongest(prefixLookup,  "A",        0, null);
        checkLongest(prefixLookup,  "AB",       0, null);
        checkLongest(prefixLookup,  "ABC",      0, null);
        checkLongest(prefixLookup,  "ABCD",     1, "Result BCD");
        checkLongest(prefixLookup,  "ABCD",     2, null);
        checkLongest(prefixLookup,  "ABCD",     3, null);
        checkLongest(prefixLookup,  "ABCD",     4, null);
    }

}
