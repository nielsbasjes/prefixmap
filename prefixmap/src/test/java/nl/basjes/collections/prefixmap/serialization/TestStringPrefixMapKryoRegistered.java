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
package nl.basjes.collections.prefixmap.serialization;

import com.esotericsoftware.kryo.Kryo;
import nl.basjes.collections.PrefixMap;
import nl.basjes.collections.prefixmap.StringPrefixMap;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestStringPrefixMapKryoRegistered {

    public static class ThisTest extends AbstractSerializeWithKryo {
        @Override
        Object createKryo() {
            Kryo kryo = (Kryo) super.createKryo();
            StringPrefixMap.configureKryo(kryo);
            return kryo;
        }

        @Override
        PrefixMap<String> createInstance() {
            return new StringPrefixMap<>(false);
        }
    }

    @Test
    public void serializeAndDeserialize() throws IOException, ClassNotFoundException {
        new ThisTest().serializeAndDeserialize();
    }

}
