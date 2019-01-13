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
import java.lang.reflect.Array;

class ASCIIPrefixTrie<V extends Serializable> extends AbstractStringPrefixTrie<V> {
    private PrefixTrie<String, Character, V>[] childArray;

    ASCIIPrefixTrie(boolean caseSensitive) {
        this(caseSensitive, 0);
    }

    ASCIIPrefixTrie(boolean caseSensitive, int charIndex) {
        super(caseSensitive, charIndex);
    }
    @Override
    public boolean doesNotHaveChildren() {
        return childArray == null;
    }

    public PrefixTrie<String, Character, V> createPrefixTrie(boolean caseSensitive, int charIndex) {
        return new ASCIIPrefixTrie<>(caseSensitive, charIndex);
    }

    @Override
    public Character getElement(String prefix, int elementIndex) {
        return prefix.charAt(elementIndex); // This will give us the ASCII value of the char
    }

    @SuppressWarnings("unchecked") // Creating the array of generics is tricky
    @Override
    public void storeChild(Character element, PrefixTrie<String, Character, V> child) {
        if (element < 32 || element > 126) {
            throw new IllegalArgumentException("Only readable ASCII is allowed as key !!!");
        }
        if (childArray == null) {
            childArray = (PrefixTrie<String, Character, V>[]) Array.newInstance(PrefixTrie.class, 128);
        }
        childArray[element] = child;
    }

    @Override
    public PrefixTrie<String, Character, V> getChild(Character element) {
        if (childArray == null || element < 32 || element > 126) {
            return null;
        }
        return childArray[element];
    }

    @Override
    public void clear() {
        childArray = null;
    }
}
