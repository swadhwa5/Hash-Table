package hw7;

import java.util.ArrayList;
import java.util.Iterator;

public class HashMapQuadraticProbing<K, V> implements Map<K, V> {

  // Represent an <key, value> tuple.
  private static class Entry<K, V> {
    K key;
    V value;
    boolean removed;

    Entry(K key, V value) {
      this.key = key;
      this.value = value;
      removed = false;
    }
  }

  private Entry<K, V>[] table;

  private int capacity;
  // size (capacity) of this table

  private int numElem;
  // the number of entries stored in this table

  private float loadFactor;

  /**
   * Construct a HashMap.
   */
  public HashMapQuadraticProbing() {
    numElem = 0;
    capacity = 29;
    table = new Entry[capacity];
    loadFactor = (float) numElem / capacity;
  }


  private int hash(K k, int cap) throws IllegalArgumentException {
    if (k == null) {
      throw new IllegalArgumentException();
    }
    int val = k.hashCode();
    return Math.abs(val % cap);
  }

  private void quadraticProbing(Entry<K, V>[] t, int c, int in, Entry<K, V> e) {
    for (int i = 0; i < c; i++) {
      int temp = (in + i * i) % c;
      if (t[temp] == null) {
        t[temp] = e;
        break;
      } else if (t[temp].removed) {
        t[temp] = e;
        break;
      }
    }
  }

  @Override
  public void insert(K k, V v) throws IllegalArgumentException {
    if (k == null || has(k)) {
      throw new IllegalArgumentException();
    }

    Entry<K, V> n = new Entry<>(k, v);
    int index = hash(k, capacity);

    quadraticProbing(table, capacity, index, n);

    numElem++;
    loadFactor = (float) numElem / capacity;
    if (loadFactor > 0.5) {
      grow();
    }
  }

  private boolean prime(int p) {
    boolean flag = true;
    for (int i = 2; i <= p / 2; ++i) {
      if (p % i == 0) {
        flag = false;
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
    Entry<K, V>[] newTable;
    newTable = new Entry[newCapacity];
    for (int i = 0; i < capacity; i++) {
      if (table[i] != null) {
        int newIndex = hash(table[i].key, newCapacity);
        Entry<K, V> n = new Entry<>(table[i].key, table[i].value);
        quadraticProbing(newTable, newCapacity, newIndex, n);
      }
    }
    loadFactor = (float) numElem / newCapacity;
    capacity = newCapacity;
    table = newTable;
  }

  @Override
  public V remove(K k) throws IllegalArgumentException {
    if (k == null || !has(k)) {
      throw new IllegalArgumentException();
    }
    Entry<K, V> found = find(k);
    if (found != null) {
      found.removed = true;
      numElem--;
      loadFactor = (float) numElem / capacity;
      return found.value;
    }
    return null;
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
    int i = 0;
    int index = hash(k, capacity);
    int temp = index;
    while (table[temp] != null) {
      if (k.equals(table[temp].key) & !table[temp].removed) {
        return table[temp];
      }
      i++;
      temp = (index + i * i) % capacity;
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
    for (Entry<K, V> e : table) {
      if (e != null) {
        keys.add(e.key);
      }
    }
    return keys.iterator();
  }
}

