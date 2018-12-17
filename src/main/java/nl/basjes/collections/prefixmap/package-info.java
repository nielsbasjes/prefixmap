/*
 * Copyright (C) 2018-2018 Niels Basjes
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
/**
 * This package contains implementations of the {@link nl.basjes.collections.PrefixMap} interface.
 * A PrefixMap holds a single values for each String prefix.
 * <p>
 * The following implementations are provided in the package:
 * <ul>
 *   <li>StringPrefixMap - An implementation using a String as the prefix.
 *   <li>ASCIIPrefixMap - A faster implementation where only readable ASCII characters are allowed as the prefix.
 * </ul>
 * </p>
 */
package nl.basjes.collections.prefixmap;
