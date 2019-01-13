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

interface PrefixTrie<P, E, V extends Serializable> extends Serializable {
    boolean doesNotHaveChildren();
    E getElement(P prefix, int elementIndex);
    PrefixTrie<String, Character, V> createPrefixTrie(boolean caseSensitive, int charIndex);
    void storeChild(E element, PrefixTrie<P, E, V> child);
    PrefixTrie<P, E, V> getChild(E element);

    V put(P prefix, V value);
    boolean containsPrefix(P prefix);
    V getShortestMatch(P input);
    V getLongestMatch(P input);
    void clear();
}
