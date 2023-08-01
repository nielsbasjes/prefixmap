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

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.PrimitiveIterator;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TestPrefixTrieAllMethods {

    private static final class DummyPrefixTrie implements PrefixTrie<String> {
        @Override
        public String add(PrimitiveIterator.OfInt prefix, String value) {
            return null;
        }

        @Override
        public boolean containsPrefix(PrimitiveIterator.OfInt prefix) {
            return false;
        }

        @Override
        public String get(PrimitiveIterator.OfInt input) {
            return null;
        }

        @Override
        public String getShortestMatch(PrimitiveIterator.OfInt input) {
            return null;
        }

        @Override
        public String getLongestMatch(PrimitiveIterator.OfInt input) {
            return null;
        }

        @Override
        public Iterator<String> getAllMatches(PrimitiveIterator.OfInt input) {
            return Collections.emptyIterator();
        }

        @Override
        public void clear() {
        }

        @Override
        public boolean caseSensitive() {
            return false;
        }
    }

    @Test
    void testRemoveTrieNotImplemented() {
        assertThrows(UnsupportedOperationException.class, () -> {
            PrefixTrie<String> dummyPrefixTrie = new DummyPrefixTrie();
            dummyPrefixTrie.remove("Something");
        });
    }

}
