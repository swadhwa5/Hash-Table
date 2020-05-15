package hw7;

/**
 * Ordered maps from comparable keys to arbitrary values.
 *
 * @param <K> Type for keys.
 * @param <V> Type for values.
 */
public interface OrderedMap<K extends Comparable<K>, V>
    extends Map<K, V> {
}
