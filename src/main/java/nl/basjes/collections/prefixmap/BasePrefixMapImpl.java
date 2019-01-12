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
import java.util.TreeMap;

/**
 * The BasePrefixMapImpl is a base implementation of PrefixMap.
 * @param <P> The type of the prefix.
 * @param <C> The type of which the prefix is a list of.
 * @param <V> The type of the value that is to be stored.
 */
public class BasePrefixMapImpl<P, C, V> implements PrefixMap<P, C, V>, Serializable {

    class PrefixTrie implements Serializable {
        private TreeMap<C, PrefixTrie> childNodes;
        private int                            charIndex;
        private V                              theValue;

        PrefixTrie() {
            this(0);
        }

        PrefixTrie(int charIndex) {
            this.charIndex = charIndex;
        }

        V add(P prefix, V value) {
            V previousValue = theValue;
            if (charIndex == getLength(prefix)) {
                theValue = value;
                return previousValue;
            }

            C myChar = getElementAtIndex(prefix, charIndex);

            if (childNodes == null) {
                childNodes = new TreeMap<>();
            }

            PrefixTrie child = childNodes.computeIfAbsent(myChar, c -> new PrefixTrie(charIndex + 1));
            previousValue = child.add(prefix, value);
            return previousValue;
        }

        boolean containsPrefix(P prefix) {
            if (charIndex == getLength(prefix)) {
                return theValue != null;
            }

            if (childNodes == null) {
                return false;
            }

            char myChar = prefix.charAt(charIndex);

            PrefixTrie child = childNodes.get(myChar);
            if (child == null) {
                return false;
            }

            return child.containsPrefix(prefix);
        }

        V getShortestMatch(P input) {
            if (theValue != null ||
                charIndex == input.length() ||
                childNodes == null) {
                return theValue;
            }

            char myChar = input.charAt(charIndex);

            PrefixTrie child = childNodes.get(myChar);
            if (child == null) {
                return null;
            }

            return child.getShortestMatch(input);
        }

        V getLongestMatch(String input) {
            if (charIndex == input.length() || childNodes == null) {
                return theValue;
            }

            char myChar = input.charAt(charIndex);

            PrefixTrie child = childNodes.get(myChar);
            if (child == null) {
                return theValue;
            }

            V returnValue = child.getLongestMatch(input);
            return (returnValue == null) ? theValue : returnValue;
        }

        public void clear() {
            childNodes = null;
            theValue = null;
        }
    }

    private PrefixTrie prefixPrefixTrie;
    private int size = 0;

    public BasePrefixMapImpl() {
        prefixPrefixTrie = new PrefixTrie();
    }

    @Override
    public boolean containsPrefix(P prefix) {
        return prefixPrefixTrie.containsPrefix(prefix);
    }

    @Override
    public V put(P prefix, V value) {
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
    public void clear() {
        prefixPrefixTrie.clear();
        size = 0;
    }

    @Override
    public V getShortestMatch(P input) {
        return prefixPrefixTrie.getShortestMatch(input);
    }

    @Override
    public V getLongestMatch(P input) {
        return prefixPrefixTrie.getLongestMatch(input);
    }

}
