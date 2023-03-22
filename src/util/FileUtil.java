package util;

import java.io.File;

/**
 * Possibly useful static utility methods for a file/file path.a
 */
public class FileUtil {

  /**
   * Given a file it will output the relative path of the file path.
   *
   * @param file is the file we want to find the relative path to
   * @return the relative path
   */
  public static String relativePath(File file) {
    File tempFile = new File("");
    String currPath = tempFile.getAbsolutePath(); // Program's directory
    String selectedPath = file.getAbsolutePath();
    if (currPath.length() == selectedPath.length()) {
      // Program directory and the given path is the same
      return "Already Set Here";
    } else {
      if (currPath.length() > selectedPath.length()) {
        // "Path changed to: " + selectedPath
        return shortenPath(selectedPath);
      } else {
        String newPath = selectedPath.substring(currPath.length() + 1);
        // "Path changed to: " + newPath
        return shortenPath(newPath);
      }
    }
  }

  /**
   * Shortens a path to 20 characters
   *
   * @param path is the path name.
   * @return the shortened path
   */
  public static String shortenPath(String path) {
    if (path.length() > 20) {
      path = path.substring(0, 19);
      return path + "...";
    } else {
      return path;
    }
  }
}
