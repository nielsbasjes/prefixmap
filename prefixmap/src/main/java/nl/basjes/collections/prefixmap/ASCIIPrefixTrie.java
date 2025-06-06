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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

class ASCIIPrefixTrie<V extends Serializable> implements PrefixTrie<V> {
    private final boolean        caseSensitive;
    private final int            charIndex;
    private ASCIIPrefixTrie<V>[] childNodes;
    private V                    theValue;

    ASCIIPrefixTrie(boolean caseSensitive) {
        this(caseSensitive, 0);
    }

    ASCIIPrefixTrie(boolean caseSensitive, int charIndex) {
        this.caseSensitive = caseSensitive;
        this.charIndex = charIndex;
    }

    public static void throwOnInvalidASCIIChar(int myChar) {
        if (isInvalidASCIIChar(myChar)) {
            throw new IllegalArgumentException("Only readable ASCII is allowed as prefix !!!");
        }
    }

    public static boolean isInvalidASCIIChar(int myChar) {
        return (myChar < 32 || myChar > 126);
    }

    @SuppressWarnings("unchecked") // Creating the array of generics is tricky
    @Override
    public V add(PrimitiveIterator.OfInt prefix, V value) {
        V previousValue = theValue;

        if (!prefix.hasNext()) {
            theValue = value;
            return previousValue;
        }

        int myChar = prefix.nextInt(); // This will give us the ASCII value of the char
        throwOnInvalidASCIIChar(myChar);

        if (childNodes == null) {
            childNodes = (ASCIIPrefixTrie<V>[]) Array.newInstance(ASCIIPrefixTrie.class, 128);
        }

        if (caseSensitive) {
            if (childNodes[myChar] == null) {
                childNodes[myChar] = new ASCIIPrefixTrie<>(true, charIndex + 1);
            }
            previousValue = childNodes[myChar].add(prefix, value);
        } else {
            // If case INsensitive we build the tree
            // and we link the same child to both the
            // lower and uppercase entries in the child array.
            int lower = Character.toLowerCase(myChar);
            int upper = Character.toUpperCase(myChar);

            if (childNodes[lower] == null) {
                childNodes[lower] = new ASCIIPrefixTrie<>(false, charIndex + 1);
            }
            previousValue = childNodes[lower].add(prefix, value);
            childNodes[upper] = childNodes[lower];
        }
        return previousValue;
    }

    @Override
    public V remove(PrimitiveIterator.OfInt  prefix) {
        if (!prefix.hasNext()) {
            V previousValue = theValue;
            theValue = null;
            return previousValue;
        }

        if (childNodes == null) {
            return null;
        }

        int myChar = prefix.nextInt(); // This will give us the ASCII value of the char
        throwOnInvalidASCIIChar(myChar);

        if (!caseSensitive) {
            // If case INsensitive we only follow the lower case one.
            myChar = Character.toLowerCase(myChar);
        }

        PrefixTrie<V> child = childNodes[myChar];
        if (child == null) {
            return null;
        }
        return child.remove(prefix);
    }

    // ==============================================================
    // GET
    @Override
    public V get(PrimitiveIterator.OfInt prefix) {
        if (!prefix.hasNext()) {
            return theValue;
        }

        if (childNodes == null) {
            return null;
        }

        int myChar = prefix.nextInt(); // This will give us the ASCII value of the char
        if (isInvalidASCIIChar(myChar)) {
            return null; // Cannot store these, so is false.
        }

        ASCIIPrefixTrie<V> child = childNodes[myChar];
        if (child == null) {
            return null;
        }

        return child.get(prefix);
    }

    // ==============================================================
    // GET SHORTEST
    @Override
    public V getShortestMatch(PrimitiveIterator.OfInt input) {
        if (theValue != null ||
            !input.hasNext() ||
            childNodes == null) {
            return theValue;
        }

        int myChar = input.nextInt(); // This will give us the ASCII value of the char
        if (isInvalidASCIIChar(myChar)) {
            return null; // Cannot store these, so this is where it ends.
        }

        ASCIIPrefixTrie<V> child = childNodes[myChar];
        if (child == null) {
            return null;
        }

        return child.getShortestMatch(input);
    }

    // ==============================================================
    // GET LONGEST

    @Override
    public V getLongestMatch(PrimitiveIterator.OfInt input) {
        if (!input.hasNext() ||
            childNodes == null) {
            return theValue;
        }

        int myChar = input.nextInt(); // This will give us the ASCII value of the char
        if (myChar < 32 || myChar > 126) {
            return theValue; // Cannot store these, so this is where it ends.
        }

        ASCIIPrefixTrie<V> child = childNodes[myChar];
        if (child == null) {
            return theValue;
        }

        V returnValue = child.getLongestMatch(input);
        return (returnValue == null) ? theValue : returnValue;
    }

    // ==============================================================
    // GET ALL VIA ITERATOR

    public static class ASCIITrieIterator<V extends Serializable> implements Iterator<V> {
        private V next;
        private final PrimitiveIterator.OfInt input;
        private ASCIIPrefixTrie<V> node;

        ASCIITrieIterator(PrimitiveIterator.OfInt input, ASCIIPrefixTrie<V> node) {
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
            if (!input.hasNext() ||
                node.childNodes == null) {
                node = null;
                return theValue;
            }

            // Find the next
            int myChar = input.nextInt(); // This will give us the ASCII value of the char
            if (myChar < 32 || myChar > 126) {
                node = null; // Cannot store these, so this is where it ends.
                return theValue;
            }

            ASCIIPrefixTrie<V> child = node.childNodes[myChar];
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
    }

    @Override
    public Iterator<V> getAllMatches(PrimitiveIterator.OfInt input) {
        return new ASCIITrieIterator<>(input, this);
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
