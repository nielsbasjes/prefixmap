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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestStringPrefixTrie extends AbstractPrefixTrieTests {

    @Override
    PrefixTrie<String> createPrefixTrie(boolean caseSensitive) {
        return new StringPrefixTrie<>(caseSensitive);
    }

    @Test
    void testCaseINSensitiveLookup(){
        PrefixTrie<String> prefixLookup = new StringPrefixTrie<>(false);
        prefixLookup.add("ABC",    "Result ABC");
        prefixLookup.add("ABCD",    "Result ABCD");
        // The ABCDE is missing !!!
        prefixLookup.add("ABCDEF",  "Result ABCDEF");
        // These are 1 char per character
        prefixLookup.add("你", "Hello in Chinese");
        // These are 2 chars per character
        prefixLookup.add("🖖", "May the force be with you (🖖)");

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
        PrefixTrie<String> prefixLookup = new StringPrefixTrie<>(true);
        prefixLookup.add("ABC",    "Result ABC");
        prefixLookup.add("ABCD",    "Result ABCD");
        // The ABCDE is missing !!!
        prefixLookup.add("ABCDEF",  "Result ABCDEF");
        // These are 1 char per character
        prefixLookup.add("你", "Hello in Chinese");
        // These are 2 chars per character
        prefixLookup.add("🖖", "May the force be with you (🖖)");

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
    void testCaseINSensitiveIterator() {
        PrefixTrie<String> prefixLookup = new StringPrefixTrie<>(false);
        prefixLookup.add("A",       "Result A");
        prefixLookup.add("ABC",     "Result ABC");
        prefixLookup.add("ABCDE",   "Result ABCDE");
        prefixLookup.add("ABCDEFG", "Result ABCDEFG");

        checkGetAllIterator(prefixLookup, "aBcDeF", "Result A", "Result ABC", "Result ABCDE");
    }

    @Test
    void testCaseNonASCIIIterator() {
        PrefixTrie<String> prefixLookup = new StringPrefixTrie<>(false);
        prefixLookup.add("",         "Empty");
        prefixLookup.add("你",       "One Chinese 'letter'");
        prefixLookup.add("你好",      "Hello in Chinese");
        prefixLookup.add("你好DE",    "Chinese DE");
        prefixLookup.add("🖖",       "Result 🖖");
        prefixLookup.add("🖖B",       "Result 🖖B");
        prefixLookup.add("A",        "Result A");
        prefixLookup.add("ABC",      "Result ABC");
        prefixLookup.add("ABCDE",    "Result ABCDE");
        prefixLookup.add("ABCDEFG",  "Result ABCDEFG");
        prefixLookup.add("ABC🖖",    "Result ABC🖖");
        prefixLookup.add("ABC🖖EF",  "Result ABC🖖EF");
        prefixLookup.add("ABC你",     "Result ABC你");
        prefixLookup.add("ABC你EF",  "Result ABC你EF");

        checkGetAllIterator(prefixLookup, "",           "Empty");
        checkGetAllIterator(prefixLookup, "aB",         "Empty", "Result A");
        checkGetAllIterator(prefixLookup, "aBc",        "Empty", "Result A", "Result ABC");
        checkGetAllIterator(prefixLookup, "aBc🖖",       "Empty", "Result A", "Result ABC", "Result ABC🖖");
        checkGetAllIterator(prefixLookup, "aBc🖖e",       "Empty", "Result A", "Result ABC", "Result ABC🖖");
        checkGetAllIterator(prefixLookup, "aBc🖖eF",     "Empty", "Result A", "Result ABC", "Result ABC🖖", "Result ABC🖖EF");
        checkGetAllIterator(prefixLookup, "aBc🖖eFgH",   "Empty", "Result A", "Result ABC", "Result ABC🖖", "Result ABC🖖EF");

        checkGetAllIterator(prefixLookup, "🖖",          "Empty", "Result 🖖");
        checkGetAllIterator(prefixLookup, "🖖BcDe",      "Empty", "Result 🖖", "Result 🖖B");

        checkGetAllIterator(prefixLookup, "你好",          "Empty", "One Chinese 'letter'", "Hello in Chinese");
        checkGetAllIterator(prefixLookup, "你好DeF",       "Empty", "One Chinese 'letter'", "Hello in Chinese", "Chinese DE");
        checkGetAllIterator(prefixLookup, "你🖖DeFg",      "Empty", "One Chinese 'letter'");
    }

    @Test
    void verifyDocumentationExampleUsage() {
        // Parameter caseSensitive=false --> so lookups are caseINsensitive
        PrefixTrie<String> brandLookup = new StringPrefixTrie<>(false);

        brandLookup.add("RM-", "Nokia");
        brandLookup.add("GT-", "Samsung");

        String brandGT = brandLookup.getLongestMatch("GT-I8190N");   // --> "Samsung"
        String brandRM = brandLookup.getLongestMatch("RM-1092");     // --> "Nokia"

        assertEquals("Samsung", brandGT);
        assertEquals("Nokia", brandRM);
    }

    private static final Logger LOG = LogManager.getFormatterLogger("FOO");

    @Test
    void experimentWithCodePoints() {
        // Parameter caseSensitive=false --> so lookups are caseINsensitive
        PrefixTrie<String> prefixLookup = new StringPrefixTrie<>(false);
        prefixLookup.add("",         "Empty");
        prefixLookup.add("你",       "One Chinese 'letter'");
        prefixLookup.add("你好",      "Hello in Chinese");
        prefixLookup.add("你好DE",    "Chinese DE");
        prefixLookup.add("🖖",       "Result 🖖");
        prefixLookup.add("🖖B",       "Result 🖖B");
        prefixLookup.add("A",        "Result A");
        prefixLookup.add("ABC",      "Result ABC");

//        String foo = "你🖖好DE";
        LOG.info("{}", prefixLookup);
    }

}
