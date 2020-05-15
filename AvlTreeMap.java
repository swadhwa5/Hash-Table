package hw7;

import java.util.*;

/**
 * Map implemented as an AvlTree.
 *
 * @param <K> Type for keys.
 * @param <V> Type for values.
 */
public class AvlTreeMap<K extends Comparable<K>, V>
    implements OrderedMap<K, V> {

  /*** Do not change variable name of 'root'. ***/
  private Node<K, V> root;
  private int size;
  private boolean balancedInsert;

  //does a single right rotation
  private Node<K, V> singleRight(Node<K, V> n) {
    Node<K, V> child = n.left;
    n.left = child.right;
    child.right = n;
    n.height = n.height - 2;
    return child;
  }

  //does a single left rotation
  private Node<K, V> singleLeft(Node<K, V> n) {
    Node<K, V> child = n.right;
    n.right = child.left;
    child.left = n;
    n.height = n.height - 2;
    return child;
  }

  //does a double right left rotation
  private Node<K, V> doubleRightLeft(Node<K, V> n) {
    n.right = singleRight(n.right);
    return singleLeft(n);
  }

  //does a double left right rotation
  private Node<K, V> doubleLeftRight(Node<K, V> n) {
    n.left = singleLeft(n.left);
    return singleRight(n);
  }

  //return the child of n that has the max height
  private Node<K, V> childWithMaxHeight(Node<K, V> n) {
    if (n.left == null) {
      return n.right;
    } else if (n.right == null) {
      return n.left;
    } else {
      if (n.left.height > n.right.height) {
        return n.left;
      } else {
        return n.right;
      }
    }
  }

  //balances the node n by deciding the type of rotation and rotating
  private Node<K, V> balance(Node<K, V> n) {
    Node<K, V> childToCompare = childWithMaxHeight(n);
    int bfMain = balanceFactor(n);
    int bfChild = balanceFactor(childToCompare);
    if (bfMain < 0) {
      if (bfChild == 1) {
        return doubleRightLeft(n);
      } else {
        return singleLeft(n);
      }
    } else if (bfMain > 0) {
      if (bfChild == -1) {
        return doubleLeftRight(n);
      } else {
        return singleRight(n);
      }
    }
    return null;
  }

  private int balanceFactor(Node<K, V> n) {

    if (n.left == null && n.right == null) {
      return 0;
    } else if (n.left == null) {
      return -1 - n.right.height;
    } else if (n.right == null) {
      return n.left.height - (-1);
    } else {
      return n.left.height - n.right.height;
    }
  }

  private boolean unBalanced(Node<K, V> n) {
    return (balanceFactor(n) < -1 | balanceFactor(n) > 1);
  }

  //recursively inserts a node in the left subtree and updates height
  private void doLeft(Node<K, V> n, K k, V v) {
    n.left = insert(n.left, k, v);
    if (n.left != null) {
      n.height = n.left.height + 1;
    }
  }

  //recursively inserts a node in the right subtree and updates height
  private void doRight(Node<K, V> n, K k, V v) {
    n.right = insert(n.right, k, v);
    if (n.right != null) {
      n.height = n.right.height + 1;
    }
  }

  //recursively goes down nodes to find where the new node has to be entered
  private Node<K, V> doRecursiveInsert(Node<K, V> n, K k, V v) {
    int cmp = k.compareTo(n.key);
    if (cmp < 0) {
      doLeft(n, k, v);
      if (unBalanced(n) & !balancedInsert) {
        n = balance(n);
        balancedInsert = true;
      }
    } else if (cmp > 0) {
      doRight(n, k, v);
      if (unBalanced(n) & !balancedInsert) {
        n = balance(n);
        balancedInsert = true;
      }
    } else {
      throw new IllegalArgumentException("duplicate key " + k);
    }
    return n;
  }

  // Insert given key and value into subtree rooted at given node;
  // return changed subtree with a new node added. Unlike in find()
  // above, doing this recursively *has* benefits: First we get
  // away with simpler code that doesn't need parent pointers,
  // second the recursive structure makes it easier to add fancy
  // tree balancing code (later).
  private Node<K, V> insert(Node<K, V> n, K k, V v) {
    if (n == null) {
      return new Node<>(k, v);
    }
    return doRecursiveInsert(n, k, v);
  }

  @Override
  public void insert(K k, V v) throws IllegalArgumentException {
    if (k == null) {
      throw new IllegalArgumentException("cannot handle null key");
    }
    balancedInsert = false;
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
      subtreeRoot.height = subtreeRoot.left.height - 1;
      subtreeRoot.left = remove(subtreeRoot.left, toRemove);
      if (unBalanced(subtreeRoot)) {
        subtreeRoot = balance(subtreeRoot);
      }
    } else {
      subtreeRoot.height = subtreeRoot.right.height - 1;
      subtreeRoot.right = remove(subtreeRoot.right, toRemove);
      if (unBalanced(subtreeRoot)) {
        subtreeRoot = balance(subtreeRoot);
      }
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

    // If it has two children, find the predecessor (max in left subtree),
    Node<K, V> toReplaceWith = max(node);
    // then copy its data to the given node (value change),
    node.key = toReplaceWith.key;
    node.value = toReplaceWith.value;
    // then remove the predecessor node (structural change).
    node.left = remove(node.left, toReplaceWith);

    return node;
  }

  // Return a node with maximum key in subtree rooted at given node.
  private Node<K, V> max(Node<K, V> node) {
    Node<K, V> curr = node.left;
    while (curr.right != null) {
      curr = curr.right;
    }
    return curr;
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

  // Return node for given key, throw an exception if the key is not
  // in the tree.
  private Node<K, V> findForSure(K k) {
    Node<K, V> n = find(k);
    if (n == null) {
      throw new IllegalArgumentException("cannot find key " + k);
    }
    return n;
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
    List<K> keys = new ArrayList<>();
    iteratorHelper(root, keys);
    return keys.iterator();
  }

  /*** Do not change this function's name or modify its code. ***/
  // Breadth first traversal that prints binary tree out level by level.
  // Each existing node is printed as follows: 'node.key:node.value'.
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
   * Just avoid changing any existing names or deleting any existing variables.
   * ***/
  // Inner node class, each holds a key (which is what we sort the
  // BST by) as well as a value. We don't need a parent pointer as
  // long as we use recursive insert/remove helpers.
  // Do not change the name of this class
  private static class Node<K, V> {
    /***  Do not change variable names in this section. ***/
    Node<K, V> left;
    Node<K, V> right;
    K key;
    V value;
    int height;

    /*** End of section. ***/

    // Constructor to make node creation easier to read.
    Node(K k, V v) {
      // left and right default to null
      key = k;
      value = v;
      height = 0;
    }

    // Just for debugging purposes.
    public String toString() {
      return "Node<key: " + key
          + "; value: " + value
          + ">";
    }
  }

}
