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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

class StringPrefixTrie<V extends Serializable> implements PrefixTrie<V> {
    private final boolean                     caseSensitive;
    private final int                         charIndex;
    private TreeMap<Character, StringPrefixTrie<V>> childNodes;
    private V                                 theValue;

    StringPrefixTrie(boolean caseSensitive) {
        this(caseSensitive, 0);
    }

    StringPrefixTrie(boolean caseSensitive, int charIndex) {
        this.caseSensitive = caseSensitive;
        this.charIndex = charIndex;
    }

    @Override
    public V add(String prefix, V value) {
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

            StringPrefixTrie<V> child = childNodes.computeIfAbsent(lower, c -> new StringPrefixTrie<>(false, charIndex + 1));
            previousValue = child.add(prefix, value);
            childNodes.put(upper, child);
        }
        return previousValue;
    }

    @Override
    public V remove(String prefix) {
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

    // ==============================================================
    // GET

    @Override
    public V get(String prefix) {
        if (charIndex == prefix.length()) {
            return theValue;
        }

        if (childNodes == null) {
            return null;
        }

        char myChar = prefix.charAt(charIndex);

        PrefixTrie<V> child = childNodes.get(myChar);
        if (child == null) {
            return null;
        }

        return child.get(prefix);
    }


    @Override
    public V get(char[] prefix) {
        if (charIndex == prefix.length) {
            return theValue;
        }

        if (childNodes == null) {
            return null;
        }

        char myChar = prefix[charIndex];

        PrefixTrie<V> child = childNodes.get(myChar);
        if (child == null) {
            return null;
        }

        return child.get(prefix);
    }

    // ==============================================================
    // GET SHORTEST

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
    public V getShortestMatch(char[] input) {
        if (theValue != null ||
            charIndex == input.length ||
            childNodes == null) {
            return theValue;
        }

        char myChar = input[charIndex];

        PrefixTrie<V> child = childNodes.get(myChar);
        if (child == null) {
            return null;
        }

        return child.getShortestMatch(input);
    }

    // ==============================================================
    // GET LONGEST

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
    public V getLongestMatch(char[] input) {
        if (charIndex == input.length ||
            childNodes == null) {
            return theValue;
        }

        char myChar = input[charIndex];

        PrefixTrie<V> child = childNodes.get(myChar);
        if (child == null) {
            return theValue;
        }

        V returnValue = child.getLongestMatch(input);
        return (returnValue == null) ? theValue : returnValue;
    }

    // ==============================================================
    // GET ALL VIA ITERATOR

    public static class StringTrieIterator<V extends Serializable> implements Iterator<V> {
        private V next;
        private final char[] input;
        private StringPrefixTrie<V> node;

        StringTrieIterator(char[] input, StringPrefixTrie<V> node) {
            this.input = input;
            this.node = node;
            this.next = getNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public V next() {
            if (next == null) {
                throw new NoSuchElementException("Trying next() when hasNext() is false.");
            }
            V result = next;
            next = getNext();
            return result;
        }

        private V getNext() {
            if (node == null) {
                return null;
            }

            V theValue = node.theValue;

            // Are we at the last possible one for the given input?
            if (node.charIndex  == input.length ||
                node.childNodes == null) {
                node = null;
                return theValue;
            }

            // Find the next
            char myChar = input[node.charIndex]; // This will give us the ASCII value of the char

            StringPrefixTrie<V> child = node.childNodes.get(myChar);
            if (child == null) {
                node = null; // No more children, so this is where it ends.
                return theValue;
            }

            node = child;
            if (theValue == null) {
                return getNext();
            }
            return theValue;
        }

        @Override
        public String toString() {
            return "StringTrieIterator{" +
                "next=" + next +
                ", input=" + Arrays.toString(input) +
                ", node=" + node +
                '}';
        }
    }

    @Override
    public Iterator<V> getAllMatches(String input) {
        return new StringTrieIterator<>(input.toCharArray(), this);
    }

    @Override
    public Iterator<V> getAllMatches(char[] input) {
        return new StringTrieIterator<>(input, this);
    }

    // ==============================================================

    @Override
    public void clear() {
        childNodes = null;
        theValue = null;
    }

    @Override
    public boolean caseSensitive() {
        return caseSensitive;
    }
}
