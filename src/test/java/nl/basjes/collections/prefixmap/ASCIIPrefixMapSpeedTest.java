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

import nl.basjes.collections.PrefixMap;

import java.util.Map;

public class ASCIIPrefixMapSpeedTest extends AbstractPrefixMapSpeedTests {

    @Override
    protected PrefixMap<String, Character, String> create(Map<String, String> prefixMap) {
        PrefixMap<String, Character, String> result = new ASCIIPrefixMap<>(false);
        result.putAll(prefixMap);
        return result;
    }

    @Override
    long getIterations() {
        return 10_000_000;
    }
}
