package model;

import com.opencsv.CSVReader;
import java.util.List;

/**
 * Model that handles CSV files
 */
public interface CSVModel {

  /**
   * Should load a CSV file from the path and edit it to every other column being saved and the
   * second column being put at the very end.
   *
   * @param filePath is the given CSV file.
   */
  void loadAndEditCSV(String filePath);

  /**
   * Load a CSV file from the path.
   *
   * @param filePath is the given CSV file.
   */
  void loadCSV(String filePath);

  /**
   * Saves a CSV file in the given filePath, if it is null it will save where the image was loaded.
   *
   * @param filePath is the desired path where this new CSV will be saved.
   */
  void saveCSV(String filePath, String fileName);

  /**
   * Returns the rows of the file.
   */
  int getRows();

  /**
   * Returns the rows of the file.
   */
  int getCols();

  /**
   * Returns the given CSV if it exits.
   *
   * @param fileName is the name of the desired csv
   * @return the given csv as a bufferedReader
   * @throws IllegalArgumentException if the file name does not match any stored files
   */
  CSVReader getCSV(String fileName) throws IllegalArgumentException;

  /**
   * Puts a file inside the model's storage.
   *
   * @param fileName is the alias the file will go by
   * @param file     is the file itself
   */
  void putInStorage(String fileName, CSVReader file);

  /**
   * Gets the row for a specific wavelength/row value.
   *
   * @param row is the column
   * @return a String Array that holds the csv values
   */
  String[] getRow(String row);

  /**
   * Gets the list of values of the csv file at the given column value. Starts with 0.
   *
   * @param col is the column number we want the values of
   * @return the list of first column strings in the csv
   */
  List<String> getColumn(String col);

  /**
   * Gets the list of values of the csv file at the given column value. Starts with 0.
   *
   * @param colVal is the column number we want the values of
   * @return the list of first column strings in the csv
   */
  List<String> getColumn(int colVal);

  /**
   * Gives back the loaded CSV name
   *
   * @return the String for the loaded CSV
   */
  String loadedCSV();

  /**
   * Gets the set default path.
   *
   * @return the default path aka where the loaded image came from
   */
  String defaultPath();
}
