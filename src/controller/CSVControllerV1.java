package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import model.CSVModel;
import view.CSVGUIView;

public class CSVControllerV1 implements Features {

  private final CSVModel model;
  private CSVGUIView currView;
  private final List<CSVGUIView> views;

  public CSVControllerV1(CSVModel model) {
    this.model = model;
    this.views = new ArrayList<>();
  }

  @Override
  public void process() {
    // Does nothing as there is no new commands to run on csv file
  }

  @Override
  public void loadCSV(String filePath) {
    try {
      this.model.loadCSV(filePath);
      this.graphCSV(this.model.loadedCSV());
      this.currView.setXValueLabel(this.model.getColumn(0));
    } catch (IllegalArgumentException e) {
      this.currView.renderError(e.getMessage());
    }
  }

  @Override
  public void loadAndEditCSV(String filePath) {
    try {
      this.model.loadAndEditCSV(filePath);
      this.graphCSV(this.model.loadedCSV());
      this.currView.setXValueLabel(this.model.getColumn(0));
    } catch (IllegalArgumentException e) {
      this.currView.renderError(e.getMessage());
    }
  }

  @Override
  public void graphCSV(String fileName) {
    try {
      for (CSVGUIView view : this.views) {
        view.renderCSV(this.model.getCSV(fileName), fileName);
      }
    } catch (IllegalArgumentException e) {
      this.currView.renderError(e.getMessage());
    }
  }

  @Override
  public void selectColumn(String wavelength) {
    try {
      for (CSVGUIView view : this.views) {
        view.highlightColumn(this.model.getRow(wavelength));
      }
    } catch (IllegalArgumentException e) {
      this.currView.renderError(e.getMessage());
    }
  }

  @Override
  public void setView(CSVGUIView view) {
    this.views.add(view);
    this.currView = view;
    this.currView.addFeatures(this);
  }

  @Override
  public void saveCSV(String filePath, String fileName) {
    try {
      this.model.saveCSV(filePath, fileName);
      if (fileName.equals("")) {
        this.currView.renderMessage("CSV Saved!", null,
            "The new CSV has been saved to: " + filePath);
      } else {
        this.currView.renderMessage(fileName + " CSV Saved!", null,
            "The new CSV has been saved to: " + filePath);
      }
    } catch (IllegalArgumentException e) {
      this.currView.renderError(e.getMessage());
    }
  }

  @Override
  public void saveImage(String filePath, String imageName, BufferedImage image) {
    if (filePath.equals("")) {
      filePath = this.model.defaultPath();
    }
    // Image name should be LineChart.png or something if no specified name is given.
    String fileType = imageName.split("\\.")[1];
    String fullPath = filePath + this.model.loadedCSV() + " " + imageName;
    try {
      ImageIO.write(image, fileType, new File(fullPath));
      this.currView.renderMessage("Image Saved!", null,
          "The image " + imageName + " has been saved to: " + fullPath);
    } catch (IOException e) {
      this.currView.renderError("Failed to save image");
    }
  }

  @Override
  public void getColumnValues(String value) {
    try {
      for (CSVGUIView view : this.views) {
        view.setXValueLabel(this.model.getColumn(value));
      }
    } catch (IllegalArgumentException e) {
      this.currView.renderError(e.getMessage());
    }
  }
}
