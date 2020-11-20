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

import com.esotericsoftware.kryo.Kryo;

import java.lang.reflect.Array;

public final class PrefixMapKryoUtil {

    private PrefixMapKryoUtil() {
    }

    /**
     * For Kryo there is a desire to register all classes that must be serialized.
     * Since many classes are not accessible outside of the module this method should
     * be called to take care of this.
     * @param kryo The kryo instance to register all used classes with
     */
    public static void registerClassesWithKryo(Kryo kryo) {
        kryo.register(ASCIIPrefixMap.class);
        kryo.register(ASCIIPrefixTrie.class);
        kryo.register(Array.newInstance(ASCIIPrefixTrie.class, 0).getClass());
        kryo.register(StringPrefixMap.class);
        kryo.register(StringPrefixTrie.class);
        kryo.register(java.util.TreeMap.class);
    }

}
