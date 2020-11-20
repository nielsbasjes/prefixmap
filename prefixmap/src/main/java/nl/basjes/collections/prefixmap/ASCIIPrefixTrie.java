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
import java.lang.reflect.Array;

class ASCIIPrefixTrie<V extends Serializable> implements PrefixTrie<V> {
    private ASCIIPrefixTrie<V>[] childNodes;
    private boolean              caseSensitive;
    private int                  charIndex;
    private V                    theValue;

    // private constructor for serialization systems ONLY (like Kyro)
    @SuppressWarnings("unused")
    private ASCIIPrefixTrie() {
    }

    ASCIIPrefixTrie(boolean caseSensitive) {
        this(caseSensitive, 0);
    }

    ASCIIPrefixTrie(boolean caseSensitive, int charIndex) {
        this.caseSensitive = caseSensitive;
        this.charIndex = charIndex;
    }

    @SuppressWarnings("unchecked") // Creating the array of generics is tricky
    @Override
    public V add(String prefix, V value) {
        V previousValue = theValue;
        if (charIndex == prefix.length()) {
            theValue = value;
            return previousValue;
        }

        char myChar = prefix.charAt(charIndex); // This will give us the ASCII value of the char
        if (myChar < 32 || myChar > 126) {
            throw new IllegalArgumentException("Only readable ASCII is allowed as prefix !!!");
        }

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
            char lower = Character.toLowerCase(myChar);
            char upper = Character.toUpperCase(myChar);

            if (childNodes[lower] == null) {
                childNodes[lower] = new ASCIIPrefixTrie<>(false, charIndex + 1);
            }
            previousValue = childNodes[lower].add(prefix, value);
            childNodes[upper] = childNodes[lower];
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
        if (myChar < 32 || myChar > 126) {
            throw new IllegalArgumentException("Only readable ASCII is allowed as prefix !!!");
        }

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

    @Override
    public boolean containsPrefix(String prefix) {
        return get(prefix) != null;
    }

    @Override
    public V get(String prefix) {
        if (charIndex == prefix.length()) {
            return theValue;
        }

        if (childNodes == null) {
            return null;
        }

        char myChar = prefix.charAt(charIndex); // This will give us the ASCII value of the char
        if (myChar < 32 || myChar > 126) {
            return null; // Cannot store these, so is false.
        }

        ASCIIPrefixTrie<V> child = childNodes[myChar];
        if (child == null) {
            return null;
        }

        return child.get(prefix);
    }

    @Override
    public V getShortestMatch(String input) {
        if (theValue != null ||
            charIndex == input.length() ||
            childNodes == null) {
            return theValue;
        }

        char myChar = input.charAt(charIndex); // This will give us the ASCII value of the char
        if (myChar < 32 || myChar > 126) {
            return null; // Cannot store these, so this is where it ends.
        }

        ASCIIPrefixTrie<V> child = childNodes[myChar];
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

        char myChar = input.charAt(charIndex); // This will give us the ASCII value of the char
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
