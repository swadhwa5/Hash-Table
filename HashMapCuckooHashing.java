package hw7;

import java.util.ArrayList;
import java.util.Iterator;

public class HashMapCuckooHashing<K, V> implements Map<K, V> {

  // Represent an <key, value> tuple.
  private static class Entry<K, V> {
    K key;
    V value;
    private int lastTable;

    Entry(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

  private Entry<K, V>[] table1;
  private Entry<K, V>[] table2;
  private int cuckooCount;
  private boolean rehashed;

  private int capacity;
  // size (capacity) of this table1


  private int numElem1;
  // the number of entries stored in this table1

  private int numElem2;
  // the number of entries stored in this table1

  private float loadFactor1;
  private float loadFactor2;

  /**
   * Construct a HashMap.
   */
  public HashMapCuckooHashing() {
    numElem1 = 0;
    numElem2 = 0;
    capacity = 13; // should this be constant or provided as an argument?
    // what is a good starting value for M?
    table1 = new Entry[capacity];
    table2 = new Entry[capacity];
    loadFactor1 = (float) numElem1 / capacity;
    loadFactor2 = (float) numElem2 / capacity;
    cuckooCount = 0;
    rehashed = false;
  }



  private int hash(K k, int cap, int tabID) throws IllegalArgumentException {
    if (k == null) {
      throw new IllegalArgumentException();
    }
    int val = k.hashCode();
    if (tabID == 1) {
      return Math.abs(val % cap);
    }
    return Math.abs((val / cap) % cap);
  }

  private int tableID(int i) {
    if (i == 1) {
      return 1;
    }
    return 2;
  }

  private void cuckooHashing(Entry<K, V>[] table1, Entry<K, V>[] table2, int count, Entry<K, V> e, int cap) {
    if (count > 5) {
      rehash();
      rehashed = true;
      return;
    }
    Entry<K, V>[] table;
    int tabID = (count + 1) % 2;
    int ind;//index
    if (tabID == 1) {
      table = table1;
      ind = hash(e.key, cap, 1);
    } else {
      table = table2;
      ind = hash(e.key, cap, 2);
    }
    if (table[ind] == null) {
      table[ind] = e;
      e.lastTable = tableID(tabID);
    } else {
      count++;
      Entry<K, V> temp = table[ind];
      table[ind] = e;
      cuckooHashing(table1, table2, count, temp, cap);
    }

  }


  @Override
  public void insert(K k, V v) throws IllegalArgumentException {
    if (k == null || has(k)) {
      throw new IllegalArgumentException();
    }
    cuckooCount = 0;
    rehashed = false;
    Entry<K, V> n = new Entry<>(k, v);
    cuckooHashing(table1, table2, cuckooCount, n, capacity);
    if (rehashed) {
      insert(k, v);
    }
    if (n.lastTable == 1) {
      numElem1++;
      loadFactor1 = (float) numElem1 / capacity;
      if (loadFactor1 > 0.5) {
        rehash();
      }
    }
    if (n.lastTable == 2) {
      numElem2++;
      loadFactor2 = (float) numElem2 / capacity;
      if (loadFactor2 > 0.5) {
        rehash();
      }
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

  private void rehash() {
    int newNumElem1 = 0;
    int newNumElem2 = 0;
    int newCapacity = newCapacity(capacity);
    Entry<K, V>[] newTable1 = new Entry[newCapacity];
    Entry<K, V>[] newTable2 = new Entry[newCapacity];
    for (int i = 0; i < capacity; i++) {
      if (table1[i] != null) {
        cuckooCount = 0;
        rehashed = false;
        Entry<K, V> n = new Entry<>(table1[i].key, table1[i].value);
        cuckooHashing(newTable1, newTable2, cuckooCount, n, newCapacity);
        if (rehashed) {
          insert(table1[i].key, table1[i].value);
        }
        if (n.lastTable == 1) {
          newNumElem1++;
          loadFactor1 = (float) newNumElem1 / newCapacity;
          if (loadFactor1 > 0.5) {
            rehash();
          }
        } else {
          newNumElem2++;
          loadFactor2 = (float) newNumElem2 / newCapacity;
          if (loadFactor2 > 0.5) {
            rehash();
          }
        }
      }
    }
    for (int j = 0; j < capacity; j++) {
      if (table2[j] != null) {
        cuckooCount = 0;
        Entry<K, V> n = new Entry<>(table2[j].key, table2[j].value);
        cuckooHashing(newTable1, newTable2, cuckooCount, n, newCapacity);
        if (rehashed) {
          insert(table2[j].key, table2[j].value);
        }
        if (n.lastTable == 1) {
          newNumElem1++;
          loadFactor1 = (float) newNumElem1 / newCapacity;
          if (loadFactor1 > 0.5) {
            rehash();
          }
        } else {
          newNumElem2++;
          loadFactor2 = (float) newNumElem2 / newCapacity;
          if (loadFactor2 > 0.5) {
            rehash();
          }
        }
      }
    }
    numElem1 = newNumElem1;
    numElem2 = newNumElem2;
    loadFactor1 = (float) newNumElem1 / newCapacity;
    loadFactor2 = (float) newNumElem2 / newCapacity;
    capacity = newCapacity;
    table1 = newTable1;
    table2 = newTable2;
  }

  @Override
  public V remove(K k) throws IllegalArgumentException {
    if (k == null || !has(k)) {
      throw new IllegalArgumentException();
    }
    int index1 = hash(k, capacity, 1);
    if (table1[index1] != null) {
      if (table1[index1].key == k) {
        V val = table1[index1].value;
        table1[index1] = null;
        return val;
      }
    }
    int index2 = hash(k, capacity, 2);
    if (table2[index2] != null) {
      if (table2[index2].key == k) {
        V val =  table2[index2].value;
        table2[index2] = null;
        return val;
      }
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
    int index1 = hash(k, capacity, 1);
    if (table1[index1] != null) {
      if (k.equals(table1[index1].key)) {
        return table1[index1];
      }
    }
    int index2 = hash(k, capacity, 2);
    if (table2[index2] != null) {
      if (k.equals(table2[index2].key)) {
        return table2[index2];
      }
    }
    return null;
  }

  @Override
  public boolean has(K k) {
    return find(k) != null;
  }

  @Override
  public int size() {
    return numElem1 + numElem2;
  }


  @Override
  public Iterator<K> iterator() {
    ArrayList<K> keys = new ArrayList<>();
    for (Entry<K, V> e : table1) {
      if (e != null) {
        keys.add(e.key);
      }
    }
    for (Entry<K, V> e : table2) {
      if (e != null) {
        keys.add(e.key);
      }
    }
    return keys.iterator();
  }
}


