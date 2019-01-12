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

package nl.basjes.collections.utils;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringIterableTest {

    @Test
    public void Normal(){
        Iterable<Character> characterIterable = new StringIterable("Hello World!");
        Iterator<Character> characterIterator = characterIterable.iterator();

        nextCharacterIs(characterIterator, 'H');
        nextCharacterIs(characterIterator, 'e');
        nextCharacterIs(characterIterator, 'l');
        nextCharacterIs(characterIterator, 'l');
        nextCharacterIs(characterIterator, 'o');
        nextCharacterIs(characterIterator, ' ');
        nextCharacterIs(characterIterator, 'W');
        nextCharacterIs(characterIterator, 'o');
        nextCharacterIs(characterIterator, 'r');
        nextCharacterIs(characterIterator, 'l');
        nextCharacterIs(characterIterator, 'd');
        nextCharacterIs(characterIterator, '!');
        noMoreCharacters(characterIterator);
    }

    @Test
    public void Empty(){
        Iterable<Character> characterIterable = new StringIterable("");
        Iterator<Character> characterIterator = characterIterable.iterator();

        noMoreCharacters(characterIterator);
    }

    private void nextCharacterIs(Iterator<Character> iterator, char letter) {
        assertTrue(iterator.hasNext());
        assertEquals(Character.valueOf(letter), iterator.next());
    }

    private void noMoreCharacters(Iterator<Character> iterator) {
        assertFalse(iterator.hasNext());
    }

}
