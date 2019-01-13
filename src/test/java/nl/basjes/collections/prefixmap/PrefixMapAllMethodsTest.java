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
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PrefixMapAllMethodsTest {

    @Test
    public void testIsEmpty() {
        PrefixMap<String> stringPrefixMap = new StringPrefixMap<>(false);

        assertTrue(stringPrefixMap.isEmpty());
        stringPrefixMap.put("Foo", "Bar");
        assertFalse(stringPrefixMap.isEmpty());
        stringPrefixMap.clear();
        assertTrue(stringPrefixMap.isEmpty());
    }

    // We implement the least possible
    private static class DummyPrefixMap implements PrefixMap<String> {
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
        public String getShortestMatch(String input) {
            return null;
        }

        @Override
        public String getLongestMatch(String input) {
            return null;
        }
    };

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveNotImplemented() {
        PrefixMap<String> dummyPrefixMap = new DummyPrefixMap();
        dummyPrefixMap.remove("Something");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClearNotImplemented() {
        PrefixMap<String> dummyPrefixMap = new DummyPrefixMap();
        dummyPrefixMap.clear();
    }

    private static class DummyPrefixTrie implements PrefixTrie<String> {
        @Override
        public String add(String prefix, String value) {
            return null;
        }

        @Override
        public boolean containsPrefix(String prefix) {
            return false;
        }

        @Override
        public String getShortestMatch(String input) {
            return null;
        }

        @Override
        public String getLongestMatch(String input) {
            return null;
        }

        @Override
        public void clear() {

        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveTrieNotImplemented() {
        PrefixTrie<String> dummyPrefixTrie = new DummyPrefixTrie();
        dummyPrefixTrie.remove("Something");
    }

}
