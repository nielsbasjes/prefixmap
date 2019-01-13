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

import java.io.Serializable;

/**
 * The StringPrefixMap is an implementation of PrefixMap where the assumption is that the
 * stored prefixes can be any character in a String.
 *
 * @param <V> The type of the value that is to be stored.
 */
public class StringPrefixMap<V extends Serializable> implements PrefixMap<V>, Serializable {

    private PrefixTrie<V> prefixTrie;
    private int           size = 0;

    protected StringPrefixMap(PrefixTrie<V> prefixTrie) {
        this.prefixTrie = prefixTrie;
    }

    public StringPrefixMap(boolean caseSensitive) {
        prefixTrie = new StringPrefixTrie<>(caseSensitive);
    }

    @Override
    public boolean containsPrefix(String prefix) {
        return prefixTrie.containsPrefix(prefix);
    }

    @Override
    public V put(String prefix, V value) {
        V previousValue = prefixTrie.add(prefix, value);
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
    public void clear() {
        prefixTrie.clear();
        size = 0;
    }

    @Override
    public V remove(String prefix) {
        V oldValue = prefixTrie.remove(prefix);
        if (oldValue != null) {
            size--;
        }
        return oldValue;
    }

    @Override
    public V getShortestMatch(String input) {
        return prefixTrie.getShortestMatch(input);
    }

    @Override
    public V getLongestMatch(String input) {
        return prefixTrie.getLongestMatch(input);
    }

}
