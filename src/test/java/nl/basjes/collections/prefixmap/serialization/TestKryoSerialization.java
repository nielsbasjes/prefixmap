/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2020 Niels Basjes
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
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import nl.basjes.collections.PrefixMap;
import nl.basjes.collections.prefixmap.PrefixMapKryoUtil;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestKryoSerialization extends AbstractSerializationTest {

    byte[] serialize(PrefixMap<String> instance) {
        Kryo             kryo             = new Kryo();
        PrefixMapKryoUtil.registerClassesWithKryo(kryo);

        ByteBufferOutput byteBufferOutput = new ByteBufferOutput(1_000_000, -1);
        kryo.writeClassAndObject(byteBufferOutput, instance);

        ByteBuffer buf = byteBufferOutput.getByteBuffer();
        byte[] arr = new byte[buf.position()];
        buf.rewind();
        buf.get(arr);

        return arr;
    }

    @SuppressWarnings("unchecked") // No way to check if the correct generic is used because of type erasure
    PrefixMap<String> deserialize(byte[] bytes) {
        Kryo            kryo            = new Kryo();
        PrefixMapKryoUtil.registerClassesWithKryo(kryo);

        ByteBufferInput byteBufferInput = new ByteBufferInput(bytes);
        Object instance =  kryo.readClassAndObject(byteBufferInput);
        assertTrue(instance instanceof PrefixMap);
        return (PrefixMap<String>) instance;
    }

}
