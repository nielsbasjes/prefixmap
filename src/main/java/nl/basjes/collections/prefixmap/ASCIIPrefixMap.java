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

import java.io.Serializable;

/**
 * The ASCIIPrefixMap is an implementation of PrefixMap where the assumption is that the
 * stored prefixes only contain characters that are in the human readable range of the ASCII encoding.
 * Lookups using this one are about twice as fast as the StringPrefixMap
 *
 * @param <V> The type of the value that is to be stored.
 */
@DefaultSerializer(ASCIIPrefixMap.KryoSerializer.class)
public class ASCIIPrefixMap<V extends Serializable> extends StringPrefixMap<V> {

    public ASCIIPrefixMap(boolean caseSensitive) {
        super(caseSensitive);
    }

    PrefixTrie<V> createTrie(boolean caseSensitive) {
        return new ASCIIPrefixTrie<>(caseSensitive);
    }

}
