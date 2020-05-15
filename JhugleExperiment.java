package hw7;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JhugleExperiment {

  // Update this to any other data file for benchmarking experiments.
  private static String getDataFile() {
    return "urls.txt";
  }

  // Change the return statement to instantiate Jhugle
  // with other implementations of Map.
  private static Jhugle createJhugle() {
    return new Jhugle(new HashMap<>());
  }

  /**
   * Execution starts here.
   *
   * @param args command-line arguments not used here.
   */
  public static void main(String[] args) {
    Jhugle jhUgle = createJhugle();
    Path dataFile = Paths.get("res", "src", getDataFile());

    SimpleProfiler.reset();
    SimpleProfiler.start();
    jhUgle.buildSearchEngine(dataFile.toFile());
    String description = String.format("Processed %s", getDataFile());
    SimpleProfiler.stop();
    System.out.println(SimpleProfiler.getStatistics(description));

    jhUgle.runSearchEngine();
  }
}
