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
import java.util.Iterator;

interface PrefixTrie<V extends Serializable> extends Serializable {
    /**
     * <p>Add a new prefix and related value to the PrefixTrie.</p>
     * @param prefix The prefix for which we want to store the provided value
     * @param value The value that we want to store. The may NOT be null.
     * @return The previously stored value, null if no previous value was present.
     */
    V add(String prefix, V value);

    /**
     * <p>Determine if we have a value for the <code>exact</code> prefix. </p>
     * @param prefix The string for which we need to know if it is present
     * @return True if the exact prefix value is present, False otherwise
     */
    default boolean containsPrefix(String prefix) {
        return get(prefix) != null;
    }

    /**
     * <p>Determine if we have a value for the <code>exact</code> prefix. </p>
     * @param prefix The string for which we need to know if it is present
     * @return True if the exact prefix value is present, False otherwise
     */
    default boolean containsPrefix(char[] prefix) {
        return get(prefix) != null;
    }

    /**
     * <p>Return the value of the <code>exact</code> matching prefix. </p>
     * <p>The value returned is the stored prefix for which is true:
     * <code>input.equals(prefix)</code>.</p>
     * <p>Note that implementations may be constructed to match either
     * case sensitive or case insensitive.</p>
     *
     * @param input The string for which we need value of the stored prefix
     * @return The value, null if not found.
     */
    V get(String input);

    /**
     * <p>Return the value of the <code>exact</code> matching prefix. </p>
     * <p>The value returned is the stored prefix for which is true:
     * <code>input.equals(prefix)</code>.</p>
     * <p>Note that implementations may be constructed to match either
     * case sensitive or case insensitive.</p>
     *
     * @param input The char[] for which we need value of the stored prefix
     * @return The value, null if not found.
     */
    V get(char[] input);

    /**
     * <p>Return the value of the <code>shortest</code> matching prefix. </p>
     * <p>The value returned is the shortest stored prefix for which is true:
     * <code>input.startsWith(prefix)</code>.</p>
     * <p>Note that implementations may be constructed to match either
     * case sensitive or case insensitive.</p>
     *
     * @param input The string for which we need value of the stored prefix
     * @return The value, null if not found.
     */
    V getShortestMatch(String input);

    /**
     * <p>Return the value of the longest matching prefix.</p>
     * <p>The value returned is the longest stored prefix for which is true:
     * <code>input.startsWith(prefix)</code>.</p>
     * <p>Note that implementations may be constructed to match either
     * case sensitive or case insensitive.</p>
     *
     * @param input The string for which we need value of the stored prefix
     * @return The value, null if not found.
     */
    V getLongestMatch(String input);

    /**
     * <p>Returns List of all matches that have a value.</p>
     * <p>The list contains all non-null values for the prefix values where this is true:
     * <code>input.startsWith(prefix)</code>.</p>
     * <p>Note that implementations may be constructed to match either
     * case sensitive or case insensitive.</p>
     *
     * @param input The string for which we need value of the stored prefix
     * @return The list of values, an empty List if nothing is found.
     */
    Iterator<V> getAllMatches(String input);

    /**
     * <p>Return the value of the <code>shortest</code> matching prefix. </p>
     * <p>The value returned is the shortest stored prefix for which is true:
     * <code>input.startsWith(prefix)</code>.</p>
     * <p>Note that implementations may be constructed to match either
     * case sensitive or case insensitive.</p>
     *
     * @param input The string for which we need value of the stored prefix
     * @return The value, null if not found.
     */
    V getShortestMatch(char[] input);

    /**
     * <p>Return the value of the longest matching prefix.</p>
     * <p>The value returned is the longest stored prefix for which is true:
     * <code>input.startsWith(prefix)</code>.</p>
     * <p>Note that implementations may be constructed to match either
     * case sensitive or case insensitive.</p>
     *
     * @param input The string for which we need value of the stored prefix
     * @return The value, null if not found.
     */
    V getLongestMatch(char[] input);

    /**
     * <p>Returns List of all matches that have a value.</p>
     * <p>The list contains all non-null values for the prefix values where this is true:
     * <code>input.startsWith(prefix)</code>.</p>
     * <p>Note that implementations may be constructed to match either
     * case sensitive or case insensitive.</p>
     *
     * @param input The string for which we need value of the stored prefix
     * @return The list of values, an empty List if nothing is found.
     */
    Iterator<V> getAllMatches(char[] input);

    default V remove(String prefix) {
        throw new UnsupportedOperationException("The 'remove(String prefix)' method has not been implemented in " +
            this.getClass().getCanonicalName());
    }

    /**
     * Wipe all prefixes and values.
     */
    void clear();

    /**
     * Is the matching being done in a case sensitive way?
     * @return True if the case 'a'/'A' makes a difference, False if they should be considered the same.
     */
    boolean caseSensitive();

}
