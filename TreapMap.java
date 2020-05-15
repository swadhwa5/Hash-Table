package hw7;

import java.util.*;

/**
 * Map implemented as a Treap.
 *
 * @param <K> Type for keys.
 * @param <V> Type for values.
 */
public class TreapMap<K extends Comparable<K>, V>
    implements OrderedMap<K, V> {

  /*** Do not change variable name of 'rand'. ***/
  private static Random rand;
  /*** Do not change variable name of 'root'. ***/
  private Node<K, V> root;
  private int size;
  private int priority;

  /**
   * Make a TreapMap.
   */
  public TreapMap() {
    rand = new Random();
  }

  /**
   * Make a TreapMap with a seed.
   * @param a is the seed
   */
  public TreapMap(int a) {

    rand = new Random(a);
  }

  private Node<K, V> singleRight(Node<K, V> n) {
    Node<K, V> child = n.left;
    n.left = child.right;
    child.right = n;
    return child;
  }

  private Node<K, V> singleLeft(Node<K, V> n) {
    Node<K, V> child = n.right;
    n.right = child.left;
    child.left = n;
    return child;
  }


  private Node<K, V> insert(Node<K, V> n, K k, V v) {
    if (n == null) {
      return new Node<>(k, v);
    }

    int cmp = k.compareTo(n.key);
    if (cmp < 0) {
      n.left = insert(n.left, k, v);
      if (n.left.priority < n.priority) {
        n = singleRight(n);
      }
    } else if (cmp > 0) {
      n.right = insert(n.right, k, v);
      if (n.right.priority < n.priority) {
        n = singleLeft(n);
      }
    } else {
      throw new IllegalArgumentException("duplicate key " + k);
    }

    return n;
  }

  @Override
  public void insert(K k, V v) throws IllegalArgumentException {
    if (k == null) {
      throw new IllegalArgumentException("cannot handle null key");
    }
    root = insert(root, k, v);
    size++;
  }

  @Override
  public V remove(K k) throws IllegalArgumentException {
    Node<K, V> node = findForSure(k);
    root = remove(root, node);
    size--;
    return node.value;
  }

  // Remove node with given key from subtree rooted at given node;
  // Return changed subtree with given key missing.
  // Doing this recursively makes it easier to
  // add fancy tree balancing code later.
  private Node<K, V> remove(Node<K, V> subtreeRoot, Node<K, V> toRemove) {
    int cmp = subtreeRoot.key.compareTo(toRemove.key);
    if (cmp == 0) {
      return remove(subtreeRoot);
    } else if (cmp > 0) {
      subtreeRoot.left = remove(subtreeRoot.left, toRemove);
    } else {
      subtreeRoot.right = remove(subtreeRoot.right, toRemove);
    }

    return subtreeRoot;
  }

  // Remove given node and return the remaining tree (structural change).
  private Node<K, V> remove(Node<K, V> node) {
    // Easy if the node has 0 or 1 child.
    if (node.right == null) {
      return node.left;
    } else if (node.left == null) {
      return node.right;
    }

    //key found: change priority to +INF and rotate down
    double inf = Double.POSITIVE_INFINITY;
    node.priority = (int)inf;
    return balanceForRemove(node);
  }

  private boolean leaf(Node<K, V> n) {
    return (n.left == null & n.right == null);
  }

  private Node<K, V> balanceForRemove(Node<K, V> n) {
    if (n.left.priority < n.right.priority) {
      n = singleRight(n);
      n.right = remove(n.right);
    } else if (n.right.priority < n.left.priority) {
      n = singleLeft(n);
      n.left = remove(n.left);
    }
    return n;
  }

  @Override
  public void put(K k, V v) throws IllegalArgumentException {
    Node<K, V> n = findForSure(k);
    n.value = v;
  }

  @Override
  public V get(K k) throws IllegalArgumentException {
    Node<K, V> n = findForSure(k);
    return n.value;
  }

  // Return node for given key. This one is iterative, but a recursive
  // one would also work. It's just that there's no real advantage to
  // using recursion for this operation.
  private Node<K, V> find(K k) {
    if (k == null) {
      throw new IllegalArgumentException("cannot handle null key");
    }
    Node<K, V> n = root;
    while (n != null) {
      int cmp = k.compareTo(n.key);
      if (cmp < 0) {
        n = n.left;
      } else if (cmp > 0) {
        n = n.right;
      } else {
        return n;
      }
    }
    return null;
  }

  // Return node for given key, throw an exception if the key is not
  // in the tree.
  private Node<K, V> findForSure(K k) {
    Node<K, V> n = find(k);
    if (n == null) {
      throw new IllegalArgumentException("cannot find key " + k);
    }
    return n;
  }

  @Override
  public boolean has(K k) {
    if (k == null) {
      return false;
    }
    return find(k) != null;
  }

  @Override
  public int size() {

    return size;
  }

  // Recursively add keys from subtree rooted at given node into the
  // given list in order.
  private void iteratorHelper(Node<K, V> n, List<K> keys) {
    if (n == null) {
      return;
    }
    iteratorHelper(n.left, keys);
    keys.add(n.key);
    iteratorHelper(n.right, keys);
  }

  @Override
  public Iterator<K> iterator() {
    List<K> keys = new ArrayList<K>();
    iteratorHelper(root, keys);
    return keys.iterator();
  }

  /*** Do not change this function's name or modify its code. ***/
  // Breadth first traversal that prints binary tree out level by level.
  // Each existing node is printed as follows:
  // 'node.key:node.value:node.priority'.
  // Non-existing nodes are printed as 'null'.
  // There is a space between all nodes at the same level.
  // The levels of the binary tree are separated by new lines.
  // Returns empty string if the root is null.
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    Queue<Node<K, V>> q = new LinkedList<>();

    q.add(root);
    boolean onlyNullChildrenAdded = root == null;
    while (!q.isEmpty() && !onlyNullChildrenAdded) {
      onlyNullChildrenAdded = true;

      int levelSize = q.size();
      while (levelSize-- > 0) {
        boolean nonNullChildAdded = handleNextNodeToString(q, s);
        if (nonNullChildAdded) {
          onlyNullChildrenAdded = false;
        }
        s.append(" ");
      }

      s.deleteCharAt(s.length() - 1);
      s.append("\n");
    }

    return s.toString();
  }

  /*** Do not change this function's name or modify its code. ***/
  // Helper function for toString() to build String for a single node
  // and add its children to the queue.
  // Returns true if a non-null child was added to the queue, false otherwise
  private boolean handleNextNodeToString(Queue<Node<K, V>> q, StringBuilder s) {
    Node<K, V> n = q.remove();
    if (n != null) {
      s.append(n.key);
      s.append(":");
      s.append(n.value);
      s.append(":");
      s.append(n.priority);

      q.add(n.left);
      q.add(n.right);

      return n.left != null || n.right != null;
    } else {
      s.append("null");

      q.add(null);
      q.add(null);

      return false;
    }
  }

  /*** Do not change the name of the Node class.
   * Feel free to add whatever you want to the Node class (e.g. new fields).
   * Just avoid changing what we've provided already.
   * ***/
  // Inner node class, each holds a key (which is what we sort the
  // BST by) as well as a value. We don't need a parent pointer as
  // long as we use recursive insert/remove helpers. Since this
  // is a node class for a Treap we also include a priority field.
  private static class Node<K, V> {
    /***  Do not change variable names in this section. ***/
    Node<K, V> left;
    Node<K, V> right;
    K key;
    V value;
    int priority;

    /*** End of section. ***/

    // Constructor to make node creation easier to read.
    Node(K k, V v) {
      // left and right default to null
      key = k;
      value = v;
      priority = generateRandomInteger();
    }

    // Use this function to generate random values
    // to use as node priorities as you insert new
    // nodes into your TreapMap.
    private int generateRandomInteger() {
      return rand.nextInt();
    }

    // Just for debugging purposes.
    public String toString() {
      return "Node<key: " + key
          + "; value: " + value
          + ">";
    }
  }
}
