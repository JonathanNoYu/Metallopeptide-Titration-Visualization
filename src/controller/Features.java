package controller;

import java.awt.image.BufferedImage;
import view.CSVGUIView;

public interface Features {

  /**
   * processes the files.
   */
  void process();

  /**
   * Loads/adds in a new csv file and auto edits it.
   *
   * @param filePath is the file path to the csv
   */
  void loadAndEditCSV(String filePath);

  /**
   * Load a CSV file from the path.
   *
   * @param filePath is the given CSV file.
   */
  void loadCSV(String filePath);

  /**
   * Graphs the given CSV if it exists
   *
   * @param fileName is the name of the CSV file.
   */
  void graphCSV(String fileName);

  /**
   * It will emphasize the selected column.
   *
   * @param wavelength is the wavelength/Y-axis value we want to highlight
   */
  void selectColumn(String wavelength);

  /**
   * Sets the view to this view.
   *
   * @param view is place we want to add our features to.
   */
  void setView(CSVGUIView view);

  /**
   * Saves the currently loaded csv to the file path.
   *
   * @param filePath the given file path, if empty will be set to where the loaded csv was
   */
  void saveCSV(String filePath, String imgName);

  /**
   * Saves the given image to the given file path, if empty it will be set to where the loaded csv
   * was.
   *
   * @param filePath  is the desired file path
   * @param imageName is the image name you want to save it as, if empty or null set to the chart
   *                  name
   * @param image     is the image that wants to be saved
   */
  void saveImage(String filePath, String imageName, BufferedImage image);

  /**
   * Gets the column values for the given value.
   *
   * @param value is the first column value for the csv
   */
  void getColumnValues(String value);
}
