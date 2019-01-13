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

import java.io.Serializable;
import java.util.TreeMap;

class StringPrefixTrie<V extends Serializable> extends AbstractStringPrefixTrie<V> {
    private TreeMap<Character, PrefixTrie<String, Character, V>>  childNodes;

    StringPrefixTrie(boolean caseSensitive) {
        super(caseSensitive);
    }

    StringPrefixTrie(boolean caseSensitive, int charIndex) {
        super(caseSensitive, charIndex);
    }

    @Override
    public boolean doesNotHaveChildren() {
        return childNodes == null;
    }

    @Override
    public Character getElement(String prefix, int elementIndex) {
        return prefix.charAt(elementIndex);
    }

    public PrefixTrie<String, Character, V> createPrefixTrie(boolean caseSensitive, int charIndex) {
        return new StringPrefixTrie<>(caseSensitive, charIndex);
    }

    @Override
    public void storeChild(Character element, PrefixTrie<String, Character, V> child) {
        if (childNodes == null) {
            childNodes = new TreeMap<>();
        }
        childNodes.put(element, child);
    }

    @Override
    public PrefixTrie<String, Character, V> getChild(Character element) {
        if (childNodes == null) {
            return null;
        }
        return childNodes.get(element);
    }

    @Override
    public void clear() {
        super.clear();
        childNodes = null;
    }
}
