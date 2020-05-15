package hw7;

@SuppressWarnings("All")
public class JdkHashMapTest extends MapTest {
  @Override
  protected Map<String, String> createMap() {
    return new JdkHashMap<>();
  }
}