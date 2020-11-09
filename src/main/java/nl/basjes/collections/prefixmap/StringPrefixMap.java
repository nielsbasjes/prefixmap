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

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import nl.basjes.collections.PrefixMap;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * The StringPrefixMap is an implementation of PrefixMap where the assumption is that the
 * stored prefixes can be any character in a String.
 *
 * @param <V> The type of the value that is to be stored.
 */
@DefaultSerializer(DefaultSerializers.KryoSerializableSerializer.class)
public class StringPrefixMap<V extends Serializable> implements PrefixMap<V>, KryoSerializable, Serializable {
    private Boolean             caseSensitive;
    private PrefixTrie<V>       prefixTrie = null;
    private TreeMap<String, V>  allPrefixes;
    private int                 size = 0;

    // private constructor for serialization systems ONLY (like Kyro)
    protected StringPrefixMap() {
    }

    PrefixTrie<V> createTrie(boolean newCaseSensitive) {
        return new StringPrefixTrie<>(newCaseSensitive);
    }

    public StringPrefixMap(boolean newCaseSensitive) {
        caseSensitive = newCaseSensitive;
        prefixTrie = createTrie(caseSensitive);
        allPrefixes = new TreeMap<>();
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeBoolean(caseSensitive);
        kryo.writeClassAndObject(output, allPrefixes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(Kryo kryo, Input input) {
        caseSensitive = input.readBoolean();
        prefixTrie = createTrie(caseSensitive);
        allPrefixes = (TreeMap<String, V>) kryo.readClassAndObject(input);
        allPrefixes.forEach((k, v) -> prefixTrie.add(k, v));
        size = allPrefixes.size();
    }

    @Override
    public boolean containsPrefix(String prefix) {
        return prefixTrie.containsPrefix(prefix);
    }

    @Override
    public V put(String prefix, V value) {
        if (prefix == null) {
            throw new NullPointerException("The prefix may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }

        V previousValue = prefixTrie.add(prefix, value);
        if (previousValue == null) {
            size++;
            if (prefixTrie.caseSensitive()) {
                allPrefixes.put(prefix, value);
            } else {
                allPrefixes.put(prefix.toLowerCase(), value);
            }
        }
        return previousValue;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        prefixTrie.clear();
        allPrefixes.clear();
        size = 0;
    }

    @Override
    public V remove(String prefix) {
        if (prefix == null) {
            throw new NullPointerException("The prefix may not be null");
        }
        V oldValue = prefixTrie.remove(prefix);
        if (oldValue != null) {
            size--;
            allPrefixes.remove(prefix);
        }
        return oldValue;
    }

    @Override
    public V get(String prefix) {
        // NOTE: The 'allPrefixes'
        return prefixTrie.get(prefix);
    }

    @Override
    public V getShortestMatch(String input) {
        return prefixTrie.getShortestMatch(input);
    }

    @Override
    public V getLongestMatch(String input) {
        return prefixTrie.getLongestMatch(input);
    }

    @Override
    public Set<Map.Entry<String, V>> entrySet() {
        return allPrefixes.entrySet();
    }

    @Override
    public boolean containsKey(Object key) {
        return allPrefixes.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return allPrefixes.containsValue(value);
    }

    @Override
    public Set<String> keySet() {
        return allPrefixes.keySet();
    }

    @Override
    public Collection<V> values() {
        return allPrefixes.values();
    }
}
