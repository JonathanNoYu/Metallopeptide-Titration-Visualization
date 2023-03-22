package model;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class that works to load and/or edit a csv file.
 */
public class CSVModelV1 implements CSVModel {

  private final Map<String, CSVReader> storedFiles;
  private String defaultPath;
  private CSVReader file;
  private List<String[]> data;
  private List<String> waveLengths;
  private String fileName;

  /**
   * Main constructor that creates an empty model.
   */
  public CSVModelV1() {
    this.storedFiles = new HashMap<>();
  }

  @Override
  public void loadAndEditCSV(String filePath) {
    this.waveLengths = new ArrayList<>();
    this.setDefaultPathInfo(filePath);
    CSVReader csv;
    String newFile = this.defaultPath + this.fileName + " (Edited).csv";
    try {
      CSVWriter writer = new CSVWriter(new FileWriter(newFile));
      csv = new CSVReader(new FileReader(filePath));
      ArrayList<String[]> oldData = new ArrayList<>(csv.readAll());
      ArrayList<String[]> newData = new ArrayList<>();
      int maxCol = oldData.get(0).length;
      for (String[] arr : oldData) {
        String[] newArr = new String[arr.length - ((arr.length / 2) - 1)];
        int newArrCol = 0;
        for (int col = 0; col < maxCol; col++) {
          if (newArrCol == 0) { // First column is wavelengths
            newArr[0] = arr[col];
            newArrCol++;
          } else if (col == 1) { // latest run is second so move to end
            newArr[newArr.length - 1] = arr[col];
          } else if (col % 2 == 1) { // value from the wavelength
            newArr[newArrCol] = arr[col];
            newArrCol++;
          }
        }
        newData.add(newArr);
      }
      this.data = newData;
      writer.writeAll(this.data);
      writer.close();
      this.file = new CSVReader(new FileReader(newFile));
    } catch (IOException e) {
      throw new IllegalArgumentException("No such file as: " + fileName + " At: " + filePath);
    } catch (CsvException e) {
      throw new RuntimeException(e);
    }
    this.storedFiles.put(this.fileName, this.file);
  }

  @Override
  public void loadCSV(String filePath) {
    this.waveLengths = new ArrayList<>();
    this.setDefaultPathInfo(filePath);
    CSVReader csv;
    try {
      csv = new CSVReader(new FileReader(filePath));
      this.data = csv.readAll();
      this.file = new CSVReader(new FileReader(filePath));
    } catch (IOException e) {
      throw new IllegalArgumentException("No such file as: " + fileName + " At: " + filePath);
    } catch (CsvException e) {
      throw new RuntimeException(e);
    }
    this.storedFiles.put(this.fileName, this.file);
  }

  @Override
  public void saveCSV(String filePath, String fileName) {
    if (this.data == null || this.fileName == null) {
      throw new IllegalArgumentException("No Loaded CSV to save");
    }
    if (filePath.equals("")) {
      filePath = this.defaultPath;
    }
    String name = this.fileName;
    if (fileName.equals("")) {
      if (!this.fileName.contains(" (Edited)")) {
        name += " (Edited)";
      }
    } else {
      name = fileName;
    }
    name = name.split("\\.")[0];
    String newFile = filePath + name + ".csv";
    try {
      CSVWriter writer = new CSVWriter(new FileWriter(newFile));
      writer.writeAll(this.data);
      writer.close();
    } catch (IOException e) {
      throw new IllegalArgumentException("New File failed to save at path " + filePath);
    }
  }

  @Override
  public int getRows() {
    return this.data.size();
  }

  @Override
  public int getCols() {
    return this.data.get(0).length;
  }

  @Override
  public CSVReader getCSV(String fileName) throws IllegalArgumentException {
    if (fileName.equals(this.fileName)) {
      return this.file;
    } else {
      for (Entry<String, CSVReader> entry : this.storedFiles.entrySet()) {
        if (entry.getKey().equals(fileName)) {
          return entry.getValue();
        }
      }
      throw new IllegalArgumentException("There is no file name by: " + fileName);
    }
  }

  @Override
  public void putInStorage(String fileName, CSVReader file) {
    this.storedFiles.put(fileName, file);
  }

  @Override
  public String[] getRow(String row) {
    for (int rowVal = 0; rowVal < this.waveLengths.size(); rowVal++) {
      if (this.waveLengths.get(rowVal).equals(row)) {
        return this.data.get(rowVal);
      }
    }
    throw new IllegalArgumentException("No such wavelength as " + row);
  }

  @Override
  public String loadedCSV() {
    return this.fileName;
  }

  @Override
  public String defaultPath() {
    return this.defaultPath;
  }

  @Override
  public List<String> getColumn(String col) {
    String[] firstRow = this.data.get(0);
    for (int colVal = 0; colVal < firstRow.length; colVal++) {
      if (firstRow[colVal].equals(col)) {
        for (String[] arr : this.data) {
          this.waveLengths.add(arr[colVal]);
        }
        return this.waveLengths;
      }
    }
    throw new IllegalArgumentException("No Column with " + col);
  }

  @Override
  public List<String> getColumn(int colVal) {
    if (this.data.get(0).length < colVal) {
      throw new IllegalArgumentException("No Column value at " + colVal);
    }
    for (String[] arr : this.data) {
      this.waveLengths.add(arr[colVal]);
    }
    return this.waveLengths;
  }


  // Sets the default path, file name and fileType, if fileType is not csv throws an error.
  private void setDefaultPathInfo(String filePath) throws IllegalArgumentException {
    String[] fileDir = filePath.split("\\\\"); // image\res\yes\ppm.yes -> image res yes ppm.yes
    String[] file = fileDir[fileDir.length - 1].split("\\."); // -> ppm yes
    this.fileName = file[0]; // ppm
    if (!file[1].equals("csv")) {
      throw new IllegalArgumentException("File is not a csv: " + this.fileName + " At:" + filePath);
    }
    StringBuilder newPath = new StringBuilder();
    for (int i = 0; i < fileDir.length - 1; i++) {
      newPath.append(fileDir[i]).append("\\");
    }
    this.defaultPath = newPath.toString();
  }
}
