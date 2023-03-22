package view;

import com.opencsv.CSVReader;
import controller.Features;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

/**
 * GUI that shows two charts with a line chart and a scatter plot.
 */
public interface CSVGUIView extends CSVView {

  /**
   * Adds the CSV name to a list of csv files.
   *
   * @param fileName is the name for the CVS.
   */
  void addCSVName(String fileName);

  /**
   * Highlights the given wavelength/Y-Axis value.
   *
   * @param values is the value that we want to highlight.
   */
  void highlightColumn(String[] values);

  /**
   * renders the given csv to the current view.
   *
   * @param file     is the csv itself
   * @param fileName is the name of the csv
   */
  void renderCSV(CSVReader file, String fileName);

  /**
   * Ends the view.
   */
  void end();

  /**
   * Adds in the features/controller to give request to.
   *
   * @param feature is the controller/feature used to request changes
   */
  void addFeatures(Features feature);

  /**
   * Sets the xValues to the combo box of the GUI if it exists.
   *
   * @param xValues is all the X-Values of the CSV
   */
  void setXValueLabel(List<String> xValues);

  /**
   * Returns the full view.
   *
   * @return Parent that has all the elements of this window/view
   */
  Parent getView();

  /**
   * Renders an error if something goes wrong and an error is given in the code.
   *
   * @param message is the error message
   */
  void renderError(String message);

  /**
   * Renders a warning if something is slightly off or unwanted gets inputted.
   *
   * @param message is the error message
   */
  void renderWarning(String message);

  /**
   * Gets the text from a textField and resets the text to an empty string.
   *
   * @param textField is the text-field we want to get the text of
   * @return the string value for the text-field
   */
  String getTextFromField(TextField textField);
}
