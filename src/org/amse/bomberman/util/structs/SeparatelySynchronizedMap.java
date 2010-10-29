package org.amse.bomberman.util.structs;

import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Special map realization for AsynchroSender.
 *
 * Number of protocol captions are limited, so we can
 * make a map based on an array which would contains messages.
 *
 * Sessions of clients must put messages in map and here
 * is the concurency problem. The key idea is to make each entry synchronized
 * separately from others. So, put operation blocks only concrete type of
 * message. This provides very good perfomance.
 *
 * But here comes another problem - we can`t fastly increase size of
 * the underlaying array... So, the decision was to block this future,
 * as we always now the number of maximum availiable captions.
 *
 * Remove operations are not supported by this map.
 * Contains is linear.
 *
 * @author Kirilchuk V.E.
 */
public class SeparatelySynchronizedMap<K, V> extends AbstractMap<K, V>{

    private Set<Map.Entry<K,V>> entrySet = null;
    
    private Map.Entry<K,V>[] entries;

    private volatile int size;

    /**
     * Constructs map with specified size of the underlaying array.
     * 
     * @param capacity capacity of the underlaying array.
     */
    @SuppressWarnings("unchecked")
    public SeparatelySynchronizedMap(int capacity) {        
        entries = (Map.Entry<K,V>[]) Array.newInstance(Map.Entry.class,capacity);
    }

    /**
     * Returnes the entry set for this map.
     * It doesn`t support any remove operations.
     */
    @Override
    public Set<Entry<K, V>> entrySet() {        
        return (entrySet!=null) ? entrySet : (entrySet = new EntrySet());
    }

    private class EntrySet extends AbstractSet<Map.Entry<K,V>> {

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
                int current = 0;

                @Override
                public boolean hasNext() {
                    return (current < size);
                }

                @Override
                public Entry<K, V> next() {
                    return entries[current++];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported.");
                }
            };
        }

        @Override
        public int size() {
            return size;
        }
    }

    private class SynchronizedEntry<K, V>  implements Map.Entry<K,V>  {

        private final K key;
        private volatile V value;

        SynchronizedEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public synchronized V setValue(V newValue) {
            V oldValue = this.value;
            this.value = newValue;
            return oldValue;
        }
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained
     * a mapping for the key, the old value is replaced by the specified value.
     * (A map m is said to contain a mapping for a key k
     * if and only if m.containsKey(k) would return true.)
     *
     * <p> Operation is linear by the number of elements in map.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return null if there wasn`t such key in map, or
     * previous value for specified key. Note that this value could be null!
     * @throws UnsupportedOperationException if you are put new value
     * but limit capacity is reached.
     */
    @Override
    public V put(K key, V value) {

        for (Entry<K, V> entry : entrySet()) {
            if(entry.getKey().equals(key)) {
                return entry.setValue(value);
            }
        }

        //else adding new enty
        addEntry(key,value);
        return null;
    }

    private synchronized void addEntry(K key, V value){
        if(!containsKey(key)) {
            if (size < entries.length) {
                entries[size] = new SynchronizedEntry<K, V>(key, value);
                size++;
            } else {
                throw new UnsupportedOperationException(
                        "This map is static sized. Can`t increase size.");
            }
        }
    }

    /**
     * Throws UnsupportedOperationException, cause this map doesn`t support
     * this type of modification.
     * @param key any object...
     * @return nothing, cause it throws error.
     */
    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("This map does not support remove.");
    }


}
