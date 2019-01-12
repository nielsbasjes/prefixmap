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

public class PrefixTrie<P, C, V> implements Serializable {
    private TreeMap<C, PrefixTrie>         childNodes;
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
        if (charIndex == prefix.length()) {
            theValue = value;
            return previousValue;
        }

        char myChar = prefix.charAt(charIndex);

        if (childNodes == null) {
            childNodes = new TreeMap<>();
        }

        if (caseSensitive) {
            PrefixTrie child = childNodes.computeIfAbsent(myChar, c -> new PrefixTrie(true, charIndex + 1));
            previousValue = child.add(prefix, value);
        } else {
            // If case INsensitive we build the tree
            // and we link the same child to both the
            // lower and uppercase entries in the child array.
            char lower = Character.toLowerCase(myChar);
            char upper = Character.toUpperCase(myChar);

            PrefixTrie child = childNodes.computeIfAbsent(lower, c -> new PrefixTrie(false, charIndex + 1));
            previousValue = child.add(prefix, value);
            childNodes.put(upper, child);
        }
        return previousValue;
    }

    boolean containsPrefix(String prefix) {
        if (charIndex == prefix.length()) {
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

    V getShortestMatch(String input) {
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
