/*
 * Copyright (c) 2017 Ian Clement.  All rights reserved.
 */

package ca.qc.johnabbott.cs406.collections.map;


import ca.qc.johnabbott.cs406.collections.list.ArrayList;
import ca.qc.johnabbott.cs406.collections.list.LinkedList;
import ca.qc.johnabbott.cs406.collections.list.List;
import ca.qc.johnabbott.cs406.serialization.Serializable;
import ca.qc.johnabbott.cs406.serialization.SerializationException;
import ca.qc.johnabbott.cs406.serialization.Serializer;
import ca.qc.johnabbott.cs406.serialization.util.SInteger;

import java.io.IOException;
import java.util.Iterator;

/**
 * Implements the map using open addressing.
 *
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 * @author TODO
 */
public class HashMap<K extends Serializable,V extends Serializable> implements Map<K,V>, Serializable {
    public static final byte SERIAL_ID = 0x07;

    private static final int DEFAULT_BUCKETS = 7;
    private static final double DEFAULT_REHASHING_THRESHOLD = 0.8;

    private static class Link<T> {
        public Link<T> next;
        public T element;
        public Link() {}
        public Link(T element) { this.element = element; }
    }

    /* Fields */

    /* store as array of link pointers */
    private Link<Entry<K,V>>[] buckets;

    private int size;
    private double threshold;  // rehashing threshold

    /* Constructors */

    public HashMap() {
        this(DEFAULT_BUCKETS, DEFAULT_REHASHING_THRESHOLD);
    }

    public HashMap(int totalBuckets) {
        this(totalBuckets, DEFAULT_REHASHING_THRESHOLD);
    }

    public HashMap(int totalBuckets, double threshold) {
        this.threshold = threshold;
        size = 0;
        buckets = (Link<Entry<K,V>>[]) new Link[totalBuckets];
    }

    /* Utility Methods */

    /**
     * Hash the key to determine its "bucket", i.e.: index in the `buckets` array.
     * @param key
     * @return the index in the buckets array.
     */
    private int hash(K key) {
        int index = key.hashCode() % buckets.length;
        if(index < 0) index += buckets.length; // adjust negative hashCode()
        return index;
    }

    /**
     * Get the next number of buckets. Start by doubling, then find the next prime number afterwards.
     * @return the next number of buckets.
     */
    private int nextNumberOfBuckets() {
        int n = buckets.length * 2 + 1; // doubling, ensuring that the next number is odd
        while(!isPrime(n))  // find the next prime number
            n += 2;
        return n;
    }

    /**
     * Check that a number is prime
     * (http://www.mkyong.com/java/how-to-determine-a-prime-number-in-java/)
     * @param n
     * @return true if n is prime, false otherwise.
     */
    private boolean isPrime(int n) {
        //check if n is a multiple of 2
        if (n%2==0) return false;
        //if not, then just check the odds
        for(int i=3;i*i<=n;i+=2) {
            if(n%i==0)
                return false;
        }
        return true;
    }

    /**
     * If necessary, resize the buckets array and rehash the entries.
     */
    private void rehash() {
        // only rehash when the load factor exceeds the limit
        if((double) size / (double) buckets.length < threshold)
            return;

        // allocation the new bucket array
        Link<Entry<K,V>>[] previous = buckets;
        buckets = (Link<Entry<K,V>>[]) new Link[nextNumberOfBuckets()];

        // loop through the previous buckets array and move all links to the new array.
        for(int i=0; i< previous.length; i++) {

            if(previous[i] != null) { // skip empty lists

                // move each link in the linked list to their new position in the larger bucket array
                // recycle the memory by relinking each link.
                // careful: list of keys that hashed to a bucket in the previous bucket size will not necessarily be hashed to the same bucket in the new size.

                Link<Entry<K,V>> current = previous[i];
                while(current != null) {
                    int index = hash(current.element.getKey());

                    // store next link since we overwrite `current.next` below
                    Link<Entry<K,V>> tmp = current.next;

                    // place at the head of the list
                    current.next = buckets[index];
                    buckets[index] = current;

                    current = tmp;
                }
            }
        }
    }

    /* Methods */

    @Override
    public void put(K key, V value) {
        // find index by hashing key
        int index = hash(key);

        // search for the key in the list
        Link<Entry<K,V>> current;
        for(current = buckets[index]; current != null && !current.element.getKey().equals(key); current = current.next)
            ;

        // key not found: add an entry to the "head" of the list
        if(current == null) {
            Link<Entry<K,V>> tmp = new Link<>(new Entry<K,V>(key, value));
            tmp.next = buckets[index];
            buckets[index] = tmp;
            size++;

            // added an entry -> rehash
            rehash();
        }
        else // key found: replace value
            current.element.setValue(value);
    }

    @Override
    public V get(K key) {
        // find index by hashing key
        int index = hash(key);

        // search list for the link containing key
        Link<Entry<K,V>> current;
        for(current = buckets[index]; current != null && !current.element.getKey().equals(key); current = current.next);

        // throw if key not found: precondition violated.
        if(current == null)
            throw new KeyNotFoundException();

        return current.element.getValue();
    }

    @Override
    public V remove(K key) {
        // find index by hashing key
        int index = hash(key);

        // search list for link containing key, keep track of previous link for deletion.
        Link<Entry<K,V>> current;
        Link<Entry<K,V>> previous = null;
        for(current = buckets[index]; current != null && !current.element.getKey().equals(key); current = current.next)
            previous = current;

        // key not found: precondition violated.
        if(current == null)
            throw new KeyNotFoundException();

        V tmp = current.element.getValue();
        if(previous == null) // remove buckets[i]
            buckets[index] = current.next;
        else             // remove within linked list
            previous.next = current.next;

        size--;

        return tmp;
    }

    @Override
    public void clear() {
        for(int i=0; i< buckets.length; i++)
            buckets[i] = null;
        // alternative: buckets = (Link[]) new Object[buckets.length];
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        // find index by hashing key
        int index = hash(key);

        // search list for link containing key
        Link<Entry<K,V>> current;
        for(current = buckets[index]; current != null && !current.element.getKey().equals(key); current = current.next)
            ;
        return current != null;
    }

    @Override
    public List<K> keys() {
        List<K> keys = new ArrayList<>();
        for(int i = 0; i < buckets.length; i++)
            for(Link<Entry<K, V>> current = buckets[i]; current != null; current = current.next)
                keys.add(current.element.getKey());
        return keys;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new MapIterator();
    }

    private class MapIterator implements Iterator<Entry<K,V>> {

        private int currentBucket;
        private Link<Entry<K,V>> current;

        public MapIterator() {

            currentBucket = -1;

            // starting from the first bucket, move `current` to the next available link
            // if the map is empty current will be `null'
            if(nextEmptyBucket())
                current = buckets[currentBucket];
        }

        private boolean nextEmptyBucket() {
            currentBucket++;
            while(currentBucket < buckets.length && buckets[currentBucket] == null)
                currentBucket++;
            return currentBucket < buckets.length;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Entry<K,V> next() {

            // to return the current element in the traversal
            Link<Entry<K,V>> tmp = current;

            // if the current list isn't done
            if(current.next != null)
                current = current.next;

            else {
                // move to the next bucket containing links.
                if(nextEmptyBucket())
                    current = buckets[currentBucket];
                else
                    current = null; // put current to NULL if there is no links left

            }

            return tmp.element;
        }

        @Override
        public void remove() {
            throw new RuntimeException("Not implemented");
        }
    }


    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Determine the size of the bucket array.
     * @return the size of the bucket array.
     * @precondition None.
     * @postcondition Returns the size of the bucket array.
     */
    public int getTotalBuckets() {
        return buckets.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for(int i = 0; i < buckets.length; i++)
            for(Link<Entry<K,V>> head = buckets[i]; head != null; head = head.next) {
                Entry<K, V> entry = head.element;
                sb.append(first ? "" : ", ").append(entry.getKey()).append(" => ").append(entry.getValue());
                first = false;
            }
        sb.append("}");
        return sb.toString();
    }

    public String toStringBuckets() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for(int i = 0; i < buckets.length; i++) {
            int count = 0;
            for (Link<Entry<K, V>> head = buckets[i]; head != null; head = head.next) count++;
            sb.append("\t" + count + ": [");

            boolean first = true;
            for (Link<Entry<K, V>> head = buckets[i]; head != null; head = head.next) {
                Entry<K, V> entry = head.element;
                sb.append(first ? "" : ", ").append(entry.getKey()).append(" => ").append(entry.getValue());
                first = false;
            }
            sb.append("]\n");
        }
        sb.append("}");

        return sb.toString();
    }

    @Override
    public byte getSerialId() {
        return SERIAL_ID;
    }

    @Override
    public void writeTo(Serializer s) throws IOException {
        // Write size and the number of buckets to serializer
        s.serialize(new SInteger(size));
        s.serialize(new SInteger(buckets.length));

        for(int i = 0; i < buckets.length; i++){
            // Write flag indicating if the bucket is null or not
            // 1 = not null, 0 = null
            if(buckets[i] != null) {
                s.serialize(new SInteger(1));

                // Add keys and values to lists
                LinkedList<K> keys = new LinkedList<>();
                LinkedList<V> values = new LinkedList<>();

                Link<Entry<K, V>> current = buckets[i];
                while (current != null) {
                    keys.add(current.element.getKey());
                    values.add(current.element.getValue());

                    current = current.next;
                }

                // Write the lists of keys and values to serializer
                s.serialize(keys);
                s.serialize(values);
            }
            else{
                s.serialize(new SInteger(0));
            }
        }
    }

    @Override
    public void readFrom(Serializer s) throws IOException, SerializationException {
        // Get size from serializer
        size = ((SInteger) s.deserialize()).get();

        // Get the number of buckets from serializer, and initialize the buckets
        int numBuckets = ((SInteger) s.deserialize()).get();
        buckets = new Link[numBuckets];

        // Loop through each bucket, checking flag to see if bucket contains anything
        for(int i = 0; i < numBuckets; i++){
            if(((SInteger)s.deserialize()).get() == 1){
                // Get lists of keys and values from serializer
                LinkedList<K> keys = (LinkedList<K>) s.deserialize();
                LinkedList<V> values = (LinkedList<V>) s.deserialize();

                // Add all the keys and values to links in the current bucket
                Link<Entry<K, V>> current = buckets[i] = new Link<>();

                // Iterate through the keys and values list, creating new links
                // and adding a map containing the keys and values form list
                Iterator<K> keyIter = keys.iterator();
                Iterator<V> valIter = values.iterator();
                while(keyIter.hasNext() && valIter.hasNext()){
                    current.element = new Entry<>(keyIter.next(), valIter.next());

                    //Avoid trailing link with no entry
                    if(keyIter.hasNext() && valIter.hasNext()) {
                        current = current.next = new Link<>();
                    }
                }
            }
        }

    }

    @Override
    public boolean immutable() {
        return false;
    }

}
