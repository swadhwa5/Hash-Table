package hw7;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


public class HashMapLinkedListChaining<K, V> implements Map<K, V> {

  // Represent an <key, value> tuple.
  private static class Entry<K, V> {
    K key;
    V value;
    Entry<K, V> next;

    Entry(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

  private LinkedList<Entry<K, V>>[] table;

  private int capacity;
  // size (capacity) of this table

  private int numElem;
  // the number of entries stored in this table

  private float loadFactor;

  /**
   * Construct a HashMap.
   */
  public HashMapLinkedListChaining() {
    numElem = 0;
    capacity = 13;
    // what is a good starting value for M?
    table = new LinkedList[capacity];
    loadFactor = (float)numElem / capacity;
  }


  private int hash(K k, int cap) throws IllegalArgumentException {
    if (k == null) {
      throw new IllegalArgumentException();
    }
    int val = k.hashCode();
    return Math.abs(val % cap);  // what if step1 < 0?
  }

  @Override
  public void insert(K k, V v) throws IllegalArgumentException {
    if (k == null || has(k)) {
      throw new IllegalArgumentException();
    }

    Entry<K, V> n = new Entry<>(k, v);
    int index = hash(k, capacity);
    if (table[index] == null) {
      table[index] = new LinkedList<>();
    }
    table[index].add(n);
    numElem++;
    loadFactor = (float)numElem / capacity;

    if (loadFactor > 0.5) {
      grow();
    }
  }

  private boolean prime(int p) {
    boolean flag = false;
    for (int i = 2; i <= p / 2; ++i) {
      if (p % i == 0) {
        flag = true;
        break;
      }
    }
    return flag;
  }

  private int newCapacity(int cap) {
    int temp = 2 * cap;
    int z = temp + 1;
    while (z >= 0) {
      if (prime(z)) {
        return z;
      } else {
        z++;
      }
    }
    return 0;
  }

  private void grow() {
    int newCapacity = newCapacity(capacity);
    LinkedList<Entry<K, V>>[] newTable;
    newTable = new LinkedList[newCapacity];
    for (int i = 0; i < capacity; i++) {
      if (table[i] != null) {
        for (Entry<K, V> e : table[i]) {
          int newIndex = hash(e.key, newCapacity);
          if (newTable[newIndex] == null) {
            newTable[newIndex] = new LinkedList<>();
          }
          newTable[newIndex].add(e);
        }
      }
    }
    table = newTable;
    loadFactor = (float)numElem / newCapacity;
    capacity = newCapacity;
  }

  @Override
  public V remove(K k) throws IllegalArgumentException {
    if (k == null || !has(k)) {
      throw new IllegalArgumentException();
    }
    Entry<K, V> curr = find(k);
    int index = hash(k, capacity);
    table[index].remove(curr);
    if (table[index].size() == 0) {
      table[index] = null;
    }
    V temp = curr.value;
    numElem--;
    loadFactor = (float) numElem / capacity;
    return temp;
  }


  @Override
  public void put(K k, V v) throws IllegalArgumentException {
    if (k == null || !has(k)) {
      throw new IllegalArgumentException();
    }
    Entry<K, V> curr = find(k);
    if (curr != null) {
      curr.value = v;
    }
  }

  @Override
  public V get(K k) throws IllegalArgumentException {
    if (k == null | !has(k)) {
      throw new IllegalArgumentException();
    }
    Entry<K, V> curr = find(k);
    if (curr != null) {
      return curr.value;
    }
    return null;
  }

  private Entry<K, V> find(K k) throws IllegalArgumentException {
    if (k == null) {
      throw new IllegalArgumentException();
    }
    int index = hash(k, capacity);

    if (table[index] == null) {
      return null;//empty list, key not present
    }
    Entry<K, V> curr = table[index].getFirst();
    while (curr != null) {
      if (curr.key.equals(k)) {
        return curr;
      }
      curr = curr.next;
    }
    return null;
  }

  @Override
  public boolean has(K k) {
    return find(k) != null;
  }

  @Override
  public int size() {
    return numElem;
  }


  @Override
  public Iterator<K> iterator() {
    ArrayList<K> keys = new ArrayList<>();
    for (int i = 0; i < capacity; i++) {
      if (table[i] != null) {
        for (Entry<K, V> e : table[i]) {
          keys.add(e.key);
        }
      }
    }
    return keys.iterator();
  }
}
