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

package nl.basjes.collections.prefixmap.serialization;

import nl.basjes.collections.PrefixMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public abstract class AbstractSerializationTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSerializationTest.class);

    abstract byte[] serialize(PrefixMap<String> instance) throws IOException;

    abstract PrefixMap<String> deserialize(byte[] bytes) throws IOException, ClassNotFoundException;

    abstract PrefixMap<String> createInstance();

    public void serializeAndDeserialize() throws IOException, ClassNotFoundException {
        serializeAndDeserialize(createInstance());
    }

    private void serializeAndDeserialize(PrefixMap<String> prefixMapBefore) throws IOException, ClassNotFoundException {
        LOG.info("Create");

        fillPrefixMap(prefixMapBefore);

        LOG.info("Validating");

        verifyCaseINSensitive(prefixMapBefore);

        long serializeStartNs = System.nanoTime();
        byte[] bytes = serialize(prefixMapBefore);
        long serializeStopNs = System.nanoTime();

        LOG.info("Serialize took {} ns ({} ms) into {} bytes",
            serializeStopNs - serializeStartNs,
            (serializeStopNs - serializeStartNs) / 1_000_000,
            bytes.length);

        long deserializeStartNs = System.nanoTime();
        PrefixMap<String> prefixMapAfter = deserialize(bytes);
        long deserializeStopNs = System.nanoTime();

        LOG.info("Deserialize took {} ns ({} ms)",
            deserializeStopNs - deserializeStartNs,
            (deserializeStopNs - deserializeStartNs) / 1_000_000);

        verifyCaseINSensitive(prefixMapAfter);

        LOG.info("Ok");
    }

    public void fillPrefixMap(PrefixMap<String> prefixLookup) {
        prefixLookup.put("a", "one");
        prefixLookup.put("ab", "two");
        prefixLookup.put("abc", "three");
    }

    public void verifyCaseINSensitive(PrefixMap<String> prefixLookup) {
        assertEquals(3, prefixLookup.size());

        // Test uppercase
        assertEquals("one",     prefixLookup.get("A"));
        assertEquals("two",     prefixLookup.get("AB"));
        assertEquals("three",   prefixLookup.get("ABC"));
        assertNull(prefixLookup.get("ABCD"));

        assertEquals("one",     prefixLookup.getShortestMatch("A"));
        assertEquals("one",     prefixLookup.getShortestMatch("AB"));
        assertEquals("one",     prefixLookup.getShortestMatch("ABC"));
        assertEquals("one",     prefixLookup.getShortestMatch("ABCD"));

        assertEquals("one",     prefixLookup.getLongestMatch("A"));
        assertEquals("two",     prefixLookup.getLongestMatch("AB"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABC"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABCD"));

        // Test lowercase
        assertEquals("one",     prefixLookup.get("a"));
        assertEquals("two",     prefixLookup.get("ab"));
        assertEquals("three",   prefixLookup.get("abc"));
        assertNull(prefixLookup.get("abcd"));

        assertEquals("one",     prefixLookup.getShortestMatch("a"));
        assertEquals("one",     prefixLookup.getShortestMatch("ab"));
        assertEquals("one",     prefixLookup.getShortestMatch("abc"));
        assertEquals("one",     prefixLookup.getShortestMatch("abcd"));

        assertEquals("one",     prefixLookup.getLongestMatch("a"));
        assertEquals("two",     prefixLookup.getLongestMatch("ab"));
        assertEquals("three",   prefixLookup.getLongestMatch("abc"));
        assertEquals("three",   prefixLookup.getLongestMatch("abcd"));

        // Test mixed upper/lowercase
        assertEquals("two",     prefixLookup.get("aB"));
        assertEquals("two",     prefixLookup.get("Ab"));

        assertEquals("three",   prefixLookup.get("Abc"));
        assertEquals("three",   prefixLookup.get("ABc"));
        assertEquals("three",   prefixLookup.get("abC"));
        assertEquals("three",   prefixLookup.get("aBC"));

        assertNull(prefixLookup.get("Abcd"));
        assertNull(prefixLookup.get("ABcd"));
        assertNull(prefixLookup.get("abCd"));
        assertNull(prefixLookup.get("aBCd"));
        assertNull(prefixLookup.get("AbcD"));
        assertNull(prefixLookup.get("ABcD"));
        assertNull(prefixLookup.get("abCD"));
        assertNull(prefixLookup.get("aBCD"));

        assertEquals("one",     prefixLookup.getShortestMatch("aB"));
        assertEquals("one",     prefixLookup.getShortestMatch("Ab"));

        assertEquals("one",     prefixLookup.getShortestMatch("Abc"));
        assertEquals("one",     prefixLookup.getShortestMatch("ABc"));
        assertEquals("one",     prefixLookup.getShortestMatch("abC"));
        assertEquals("one",     prefixLookup.getShortestMatch("aBC"));

        assertEquals("one",     prefixLookup.getShortestMatch("Abcd"));
        assertEquals("one",     prefixLookup.getShortestMatch("ABcd"));
        assertEquals("one",     prefixLookup.getShortestMatch("abCd"));
        assertEquals("one",     prefixLookup.getShortestMatch("aBCd"));
        assertEquals("one",     prefixLookup.getShortestMatch("AbcD"));
        assertEquals("one",     prefixLookup.getShortestMatch("ABcD"));
        assertEquals("one",     prefixLookup.getShortestMatch("abCD"));
        assertEquals("one",     prefixLookup.getShortestMatch("aBCD"));

        assertEquals("two",     prefixLookup.getLongestMatch("aB"));
        assertEquals("two",     prefixLookup.getLongestMatch("Ab"));

        assertEquals("three",   prefixLookup.getLongestMatch("Abc"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABc"));
        assertEquals("three",   prefixLookup.getLongestMatch("abC"));
        assertEquals("three",   prefixLookup.getLongestMatch("aBC"));

        assertEquals("three",   prefixLookup.getLongestMatch("Abcd"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABcd"));
        assertEquals("three",   prefixLookup.getLongestMatch("abCd"));
        assertEquals("three",   prefixLookup.getLongestMatch("aBCd"));
        assertEquals("three",   prefixLookup.getLongestMatch("AbcD"));
        assertEquals("three",   prefixLookup.getLongestMatch("ABcD"));
        assertEquals("three",   prefixLookup.getLongestMatch("abCD"));
        assertEquals("three",   prefixLookup.getLongestMatch("aBCD"));
    }

}
