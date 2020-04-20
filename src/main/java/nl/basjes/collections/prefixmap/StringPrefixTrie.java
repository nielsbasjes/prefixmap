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

import java.io.Serializable;
import java.util.TreeMap;

class StringPrefixTrie<V extends Serializable> implements PrefixTrie<V> {
    protected TreeMap<Character, PrefixTrie<V>> childNodes;
    private   boolean                           caseSensitive;
    private   int                               charIndex;
    private   V                                 theValue;

    // private constructor for serialization systems ONLY (like Kyro)
    private StringPrefixTrie() {
    }

    StringPrefixTrie(boolean caseSensitive) {
        this(caseSensitive, 0);
    }

    StringPrefixTrie(boolean caseSensitive, int charIndex) {
        this.caseSensitive = caseSensitive;
        this.charIndex = charIndex;
    }

    @Override
    public V add(String prefix, V value) {
        if (prefix == null) {
            throw new NullPointerException("The prefix may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }

        V previousValue = theValue;
        if (charIndex == prefix.length()) {
            theValue = value;
            return previousValue;
        }

        char myChar = prefix.charAt(charIndex);

        if (childNodes == null) {
            childNodes = new TreeMap<>();
        }

        if (caseSensitive) {
            PrefixTrie<V> child = childNodes.computeIfAbsent(myChar, c -> new StringPrefixTrie<>(true, charIndex + 1));
            previousValue = child.add(prefix, value);
        } else {
            // If case INsensitive we build the tree
            // and we link the same child to both the
            // lower and uppercase entries in the child array.
            char lower = Character.toLowerCase(myChar);
            char upper = Character.toUpperCase(myChar);

            PrefixTrie<V> child = childNodes.computeIfAbsent(lower, c -> new StringPrefixTrie<>(false, charIndex + 1));
            previousValue = child.add(prefix, value);
            childNodes.put(upper, child);
        }
        return previousValue;
    }

    @Override
    public V remove(String prefix) {
        if (prefix == null) {
            throw new NullPointerException("The prefix may not be null");
        }

        if (charIndex == prefix.length()) {
            V previousValue = theValue;
            theValue = null;
            return previousValue;
        }

        if (childNodes == null) {
            return null;
        }

        char myChar = prefix.charAt(charIndex); // This will give us the ASCII value of the char

        if (!caseSensitive) {
            // If case INsensitive we only follow the lower case one.
            myChar = Character.toLowerCase(myChar);
        }

        PrefixTrie<V> child = childNodes.get(myChar);
        if (child == null) {
            return null;
        }
        return child.remove(prefix);
    }

    @Override
    public boolean containsPrefix(String prefix) {
        if (charIndex == prefix.length()) {
            return theValue != null;
        }

        if (childNodes == null) {
            return false;
        }

        char myChar = prefix.charAt(charIndex);

        PrefixTrie<V> child = childNodes.get(myChar);
        if (child == null) {
            return false;
        }

        return child.containsPrefix(prefix);
    }

    @Override
    public V getShortestMatch(String input) {
        if (theValue != null ||
            charIndex == input.length() ||
            childNodes == null) {
            return theValue;
        }

        char myChar = input.charAt(charIndex);

        PrefixTrie<V> child = childNodes.get(myChar);
        if (child == null) {
            return null;
        }

        return child.getShortestMatch(input);
    }

    @Override
    public V getLongestMatch(String input) {
        if (charIndex == input.length() || childNodes == null) {
            return theValue;
        }

        char myChar = input.charAt(charIndex);

        PrefixTrie<V> child = childNodes.get(myChar);
        if (child == null) {
            return theValue;
        }

        V returnValue = child.getLongestMatch(input);
        return (returnValue == null) ? theValue : returnValue;
    }

    @Override
    public void clear() {
        childNodes = null;
        theValue = null;
    }
}
