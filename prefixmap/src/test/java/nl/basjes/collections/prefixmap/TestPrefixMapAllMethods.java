/*
 * Copyright (C) 2018-2025 Niels Basjes
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

import java.util.Collections;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestPrefixMapAllMethods {

    @Test
    void testIsEmpty() {
        PrefixMap<String> stringPrefixMap = new StringPrefixMap<>(false);

        assertTrue(stringPrefixMap.isEmpty());
        stringPrefixMap.put("Foo", "Bar");
        assertFalse(stringPrefixMap.isEmpty());
        stringPrefixMap.clear();
        assertTrue(stringPrefixMap.isEmpty());
    }

    // We implement the least possible
    private static final class DummyPrefixMap implements PrefixMap<String> {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean containsPrefix(PrimitiveIterator.OfInt prefix) {
            return false;
        }

        @Override
        public String put(String prefix, String value) {
            return null;
        }

        @Override
        public Set<Entry<String, String>> entrySet() {
            return Collections.emptySet();
        }

        @Override
        public String get(String prefix) {
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
    }

    @Test
    void testRemoveNotImplemented() {
        assertThrows(UnsupportedOperationException.class,
            () -> new DummyPrefixMap().remove("Something")
        );
    }

    @Test
    void testClearNotImplemented() {
        assertThrows(UnsupportedOperationException.class,
            () -> new DummyPrefixMap().clear()
        );
    }

}
