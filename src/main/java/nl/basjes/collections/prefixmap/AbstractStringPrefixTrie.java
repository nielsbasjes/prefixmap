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

abstract class AbstractStringPrefixTrie<V extends Serializable> implements PrefixTrie<String, Character, V> {
    private boolean                              caseSensitive;
    private int                                  charIndex;
    private V                                    theValue;

    AbstractStringPrefixTrie(boolean caseSensitive) {
        this(caseSensitive, 0);
    }

    AbstractStringPrefixTrie(boolean caseSensitive, int charIndex) {
        this.caseSensitive = caseSensitive;
        this.charIndex = charIndex;
    }

    @Override
    public V put(String prefix, V value) {
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

        Character myChar = getElement(prefix, charIndex);

        if (caseSensitive) {
            PrefixTrie<String, Character, V> child = getChild(myChar);
            if (child == null) {
                child = createPrefixTrie(true, charIndex + 1);
                storeChild(myChar, child);
            }
            previousValue = child.put(prefix, value);
        } else {
            // If case INsensitive we build the tree
            // and we link the same child to both the
            // lower and uppercase entries in the child array.
            Character lower = Character.toLowerCase(myChar);
            Character upper = Character.toUpperCase(myChar);

            PrefixTrie<String, Character, V> child = getChild(lower);
            if (child == null) {
                child = createPrefixTrie(false, charIndex + 1);
                storeChild(lower, child);
                storeChild(upper, child);
            }
            previousValue = child.put(prefix, value);
        }
        return previousValue;
    }

    @Override
    public boolean containsPrefix(String prefix) {
        if (charIndex == prefix.length()) {
            return theValue != null;
        }

        if (doesNotHaveChildren()) {
            return false;
        }

        Character myChar = getElement(prefix, charIndex);

        PrefixTrie<String, Character, V> child = getChild(myChar);
        if (child == null) {
            return false;
        }

        return child.containsPrefix(prefix);
    }

    @Override
    public V getShortestMatch(String input) {
        if (theValue != null ||
            charIndex == input.length() ||
            doesNotHaveChildren()) {
            return theValue;
        }

        Character myChar = getElement(input, charIndex);

        PrefixTrie<String, Character, V> child = getChild(myChar);
        if (child == null) {
            return null;
        }

        return child.getShortestMatch(input);
    }

    @Override
    public V getLongestMatch(String input) {
        if (charIndex == input.length() ||
            doesNotHaveChildren()) {
            return theValue;
        }

        Character myChar = input.charAt(charIndex);

        PrefixTrie<String, Character, V> child = getChild(myChar);
        if (child == null) {
            return theValue;
        }

        V returnValue = child.getLongestMatch(input);
        return (returnValue == null) ? theValue : returnValue;
    }

    @Override
    public void clear() {
        theValue = null;
    }
}
