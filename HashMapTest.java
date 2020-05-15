package hw7;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HashMapTest extends MapTest {
  @Override
  protected Map<String, String> createMap() {
    return new HashMapCuckooHashing<>();
  }

  @Test//remove later
  public void worksheet() {
    map.insert("1111", null);
    map.insert("5005", null);
    map.insert("86", null);
    assertFalse(map.has("5557"));
    map.insert("2332", null);
    map.insert("8333", null);
    assertTrue(map.has("2332"));
    map.insert("700", null);
    map.remove("2332");
    assertTrue(map.has("8333"));
    map.insert("202", null);
  }

}
