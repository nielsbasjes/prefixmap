/*
 * Copyright (C) 2018-2025 Niels Basjes
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

package nl.basjes.collections.prefixmap.speed;

import nl.basjes.collections.PrefixMap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static nl.basjes.collections.prefixmap.speed.PrefixMapSpeedTestData.FULL_PREFIX_MAP;
import static nl.basjes.collections.prefixmap.speed.PrefixMapSpeedTestData.PREFIX_MAP;
import static nl.basjes.collections.prefixmap.speed.PrefixMapSpeedTestData.TEST_MODELS;


public abstract class AbstractPrefixMapSpeedTests {

    protected abstract PrefixMap<String> create(Map<String, String> prefixMap);

    abstract long getIterations();

    public void runLookupSpeedTest(Map<String, String> prefixes) {
        int testBatchSize = 100000;
        List<String> testModels = new ArrayList<>(testBatchSize);

        Iterator<String> iterator = TEST_MODELS.iterator();
        for (int i = 0; i < testBatchSize; i++){
            if (!iterator.hasNext()) {
                iterator = TEST_MODELS.iterator();
            }
            testModels.add(iterator.next());
        }

        PrefixMap<String> prefixMap = create(prefixes);

        long iterations = getIterations();

        // Heat it up
        for (int i = 0; i< 5; i++) {
            for (String model: testModels) {
                prefixMap.getLongestMatch(model);
            }
        }

        long start = System.nanoTime();

        for (int i = 0; i< (iterations/testModels.size()); i++) {
            for (String model: testModels) {
                prefixMap.getLongestMatch(model);
            }
        }
        long stop = System.nanoTime();
        System.out.println("Speed stats " + prefixMap.getClass().getSimpleName() +
                "\t (" + prefixes.size() + "\t rules): " +
                iterations + " runs took " +
                ((stop - start)/1000000) + "ms --> " +
                ((stop - start)/iterations) + "ns each (=" +
                (((stop - start)/iterations)/1000) + "us) .");
    }

    @Test
    public void testBaseLookupSpeed() {
        runLookupSpeedTest(PREFIX_MAP);
    }

    @Test
    public void testHugePrefixSetLookupSpeed() {
        runLookupSpeedTest(FULL_PREFIX_MAP);
    }

}
