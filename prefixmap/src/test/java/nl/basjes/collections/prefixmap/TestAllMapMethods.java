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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestAllMapMethods {

    @Test
    void checkASCIIPrefixMap(){
        PrefixMap<String> prefixMap = new ASCIIPrefixMap<>(true);
        prefixMap.put("A", "AA");

        assertTrue(prefixMap.containsKey("A"));
        assertTrue(prefixMap.keySet().contains("A"));
        assertTrue(prefixMap.containsValue("AA"));
        assertTrue(prefixMap.values().contains("AA"));
    }

    @Test
    void checkStringPrefixMap(){
        PrefixMap<String> prefixMap = new StringPrefixMap<>(true);
        prefixMap.put("A", "AA");

        assertTrue(prefixMap.containsKey("A"));
        assertTrue(prefixMap.keySet().contains("A"));
        assertTrue(prefixMap.containsValue("AA"));
        assertTrue(prefixMap.values().contains("AA"));
    }

    @Test
    void checkBasePrefixMap(){
        PrefixMap<String> prefixMap = new PrefixMap<>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean containsPrefix(String prefix) {
                return false;
            }

            @Override
            public String put(String prefix, String value) {
                return null;
            }

            @Override
            public Set<Entry<String, String>> entrySet() {
                return null;
            }

            @Override
            public String get(String prefix) {
                return null;
            }

            @Override
            public String get(char[] prefix) {
                return null;
            }

            @Override
            public String getShortestMatch(String input, int startOffet) {
                return null;
            }

            @Override
            public String getLongestMatch(String input, int startOffet) {
                return null;
            }

            @Override
            public String getShortestMatch(char[] input, int startOffet) {
                return null;
            }

            @Override
            public String getLongestMatch(char[] input, int startOffet) {
                return null;
            }
        };

        assertThrows(UnsupportedOperationException.class, () -> prefixMap.remove(1));
        assertThrows(UnsupportedOperationException.class, () -> prefixMap.remove("X"));
        assertThrows(UnsupportedOperationException.class, () -> prefixMap.containsKey("X"));
        assertThrows(UnsupportedOperationException.class, () -> prefixMap.containsValue("X"));
        assertThrows(UnsupportedOperationException.class, () -> prefixMap.get(1));
        assertThrows(UnsupportedOperationException.class, () -> prefixMap.keySet());
        assertThrows(UnsupportedOperationException.class, () -> prefixMap.values());
    }

}
