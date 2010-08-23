/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.asynchro;

import java.util.Map.Entry;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Kirilchuk V.E.
 */
public class SeparatelySynchronizedMapTest {

    SeparatelySynchronizedMap<Integer, Integer> map;
    private int initSize = 100;

    @Before
    public void init() {
        map = new SeparatelySynchronizedMap<Integer, Integer>(initSize);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOversize() {
        for (int i = 0; i < initSize; i++) {
            map.put(i, i);
        }
        //putting new value
        map.put(initSize+1, initSize+1);//exception
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testEntrySetIteratorRemove() {
        Set<Entry<Integer, Integer>> entrySet = map.entrySet();
        entrySet.iterator().remove();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testMapRemove() {
        map.remove(new Integer(2));
    }

    @Test
    public void testEntrySet() {
        Set<Entry<Integer, Integer>> entrySet = map.entrySet();
        assertTrue(entrySet.size() == 0);
        assertFalse(entrySet.iterator().hasNext());
        assertTrue(entrySet.isEmpty());

        for (int i = 0; i < 5; i++) {
            map.put(i, i+1);
        }
        
        assertTrue(entrySet.size() == 5);

        for (int i = 5; i < initSize; i++) {
            map.put(i, i+1);
        }

        assertTrue(entrySet.size() == initSize);

        int i = 0;
        for (Entry<Integer, Integer> entry : entrySet) {
            assertTrue(entry.getKey().equals(i));
            assertTrue(entry.getValue().equals(i+1));
            i++;
        }
        assertEquals((int)initSize, i);
    }

    @Test
    public void testPut() {
        Set<Entry<Integer, Integer>> entrySet = map.entrySet();

        for (int i = 0; i < 5; i++) {
            map.put(i, i+1);
        }

        int c = 0;
        for (Entry<Integer, Integer> entry : entrySet) {
            assertTrue(entry.getKey().equals(c));
            assertTrue(entry.getValue().equals(c+1));
            c++;
        }

        for (int i = 0; i < 5; i++) {
            map.put(i, i+2);
        }

        c = 0;
        for (Entry<Integer, Integer> entry : entrySet) {
            assertTrue(entry.getKey().equals(c));
            assertTrue(entry.getValue().equals(c+2));
            c++;
        }

    }

}
