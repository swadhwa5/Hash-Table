package hw7;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JhugleTest {

  private SearchEngine jhUgle;

  private static void assertRightOutput(List<String> output, List<String> sln) {
    assertEquals("Incorrect number of lines output", sln.size(), output.size());
    for (String s : output) {
      assertTrue("solution missing " + s, sln.contains(s));
    }

    for (String s : sln) {
      assertTrue("solution has additional url " + s, output.contains(s));
    }
  }

  private File getDataFile(String filename) {
    Path dataFile = Paths.get("res", "src", filename);
    return dataFile.toFile();
  }

  // Change the return statement to instantiate Jhugle
  // with other implementations of Map.
  private SearchEngine createSearchEngine() {
    return new Jhugle(new JdkHashMap<>());
  }

  @Before
  public void setUp() {
    jhUgle = createSearchEngine();
  }


  @Test
  public void simpleRun() {
    String filename = "jhu.txt";
    String query = "school ? !";
    List<String> sln = Arrays.asList(
        "https://www.jhu.edu/academics/#%21/bachelors",
        "http://studentaffairs.jhu.edu/viceprovost/consumer-information/",
        "https://www.jhu.edu/academics/",
        "http://parents.jhu.edu/"
    );

    jhUgle.buildSearchEngine(getDataFile(filename));
    List<String> urls = jhUgle.queryWithoutRunning(query);
    assertRightOutput(urls, sln);
  }

  // Note feel free to add more tests here, but you are **not** required to.
}