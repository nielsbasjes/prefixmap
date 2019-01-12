/*
 * Copyright (C) 2018-2018 Niels Basjes
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

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * The StringPrefixMap is an implementation of PrefixMap where the assumption is that the
 * stored prefixes can be any character in a String.
 * @param <V> The type of the value that is to be stored.
 */
public class StringPrefixMap<V extends Serializable> implements PrefixMap<String, Character, V>, Serializable {


    private PrefixTrie prefixPrefixTrie;
    private int size = 0;

    public StringPrefixMap(boolean caseSensitive) {
        prefixPrefixTrie = new PrefixTrie(caseSensitive);
    }

    @Override
    public boolean containsPrefix(String prefix) {
        return prefixPrefixTrie.containsPrefix(prefix);
    }

    @Override
    public V put(String prefix, V value) {
        if (prefix == null) {
            throw new NullPointerException("The prefix may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        V previousValue = prefixPrefixTrie.add(prefix, value);
        if (previousValue == null) {
            size++;
        }
        return previousValue;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Character getElementAtIndex(String prefix, int i) {
        return prefix.charAt(i);
    }

    @Override
    public void clear() {
        prefixPrefixTrie.clear();
        size = 0;
    }

    @Override
    public V getShortestMatch(String input) {
        return prefixPrefixTrie.getShortestMatch(input);
    }

    @Override
    public V getLongestMatch(String input) {
        return prefixPrefixTrie.getLongestMatch(input);
    }

}
