package view;

import com.opencsv.CSVReader;
import controller.Features;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.GraphUtil;
import util.ImageUtil;

public class CSVGUIViewV1 implements CSVGUIView {

  private final BorderPane mainPanel;
  private final HBox centerPanel;
  private final Button importEditButton;
  private final Button importButton;
  private final Button saveToButton;
  private final Button quickSaveButton;
  private final Button quickSaveImage;
  private final Button saveImageButton;
  private final TextField saveName;
  private final ComboBox<String> wavelengths;
  private final ComboBox<String> fileTypes;
  private final ComboBox<String> selectGraph;
  private LineChart<Number, Number> initGraph;
  private ScatterChart<Number, Number> selectedGraph;
  private boolean animated;
  private boolean lightMode;
  private final String css;

  public CSVGUIViewV1() {
    this.css = Objects.requireNonNull(getClass().getResource("/style/MainGUIWindow.css"))
        .toExternalForm();
    // Layout and Scene
    this.mainPanel = new BorderPane();
    this.mainPanel.setPrefHeight(900);
    this.mainPanel.setPrefWidth(1800);
    this.mainPanel.getStylesheets().add(this.css);

    //Top Panel
    HBox topPane = new HBox();
    topPane.setPadding(new Insets(15, 12, 15, 12));
    topPane.setAlignment(Pos.CENTER);
    topPane.getStyleClass().add("topPane");

    // Import Panel
    HBox importPanel = new HBox(20);
    importPanel.getStyleClass().add("roundBorder");

    // Import and Edit Button
    this.importEditButton = new Button("Import & Edit CSV");
    importPanel.getChildren().add(this.importEditButton);

    // Import Button with no edits
    this.importButton = new Button("Import CSV (no Edits)");
    importPanel.getChildren().add(this.importButton);
    topPane.getChildren().add(importPanel);
    topPane.spacingProperty()
        .bind(this.mainPanel.widthProperty().divide(2).subtract(importPanel.widthProperty()));
    // Save Panel
    HBox savePanel = new HBox(20);
    savePanel.getStyleClass().add("roundBorder");

    // Save Buttons for csv and images
    this.saveToButton = new Button("Save CSV To");
    this.saveImageButton = new Button("Save Image To");
    this.quickSaveButton = new Button("Quick Save CSV");
    this.quickSaveImage = new Button("Quick Save Image");

    // Save Name textField
    this.saveName = GraphUtil.textField("Save Name", lightMode);

    // Save Option Button
    Button saveCSVOptions = new Button("Save CSV Options");
    saveCSVOptions.setOnMouseClicked((evt) -> {
      if (this.initGraph != null) {
        this.saveWindow("Saving CSV", this.saveToButton, this.quickSaveButton, this.lightMode);
      } else {
        this.renderError("There is CSV/Graph loaded");
      }
    });
    Button saveImageOptions = new Button("Save Image Options");
    saveImageOptions.setOnMouseClicked((evt) -> {
      if (this.initGraph != null) {
        this.saveWindow("Saving Graph Images", this.saveImageButton,
            this.quickSaveImage, this.lightMode);
      } else {
        this.renderError("There is CSV/Graph loaded");
      }
    });
    savePanel.getChildren().addAll(saveImageOptions, saveCSVOptions);
    topPane.getChildren().add(savePanel);

    this.mainPanel.setTop(topPane);

    // Center Panel
    this.centerPanel = new HBox(10);
    this.centerPanel.setPadding(new Insets(10, 10, 10, 10));
    this.centerPanel.setAlignment(Pos.CENTER_LEFT);
    this.centerPanel.getStyleClass().add("insetPane");
    this.mainPanel.setCenter(this.centerPanel);

    // Bottom Panel
    HBox bottomPane = new HBox(20);
    bottomPane.setPadding(new Insets(15, 12, 15, 12));
    bottomPane.setAlignment(Pos.CENTER);
    bottomPane.spacingProperty().bind(this.mainPanel.widthProperty().divide(3)
        .subtract(this.mainPanel.widthProperty().divide(7)));
    bottomPane.getStyleClass().add("bottomPane");

    // Check Boxes
    HBox checkPane = GraphUtil.hBoxCentered();
    CheckBox animateGraph = new CheckBox("Animate Graph");
    animateGraph.setOnAction((evt) -> this.animated = animateGraph.isSelected());
    CheckBox lightMode = new CheckBox("Light Mode");
    checkPane.getChildren().addAll(animateGraph, lightMode);
    bottomPane.getChildren().add(checkPane);

    // Graph Pane
    HBox graphPane = new HBox(5);
    graphPane.getStyleClass().add("roundBorder");
    // ComboBox for wavelengths
    this.wavelengths = new ComboBox<>();

    // Change Graph Button
    Button graphChange = new Button("Change Graph Info");
    graphChange.setOnAction((evt) -> this.setGraph());
    graphPane.getChildren().addAll(this.wavelengths, graphChange);
    bottomPane.getChildren().add(graphPane);

    // Information/Help Button
    Button infoButton = new Button("Info/Help");
    infoButton.setOnAction((evt) -> this.info());
    bottomPane.getChildren().add(infoButton);
    this.mainPanel.setBottom(bottomPane);

    this.fileTypes = new ComboBox<>();
    this.fileTypes.getItems().addAll(".png", ".jpg", ".jpeg", ".bmp");
    this.fileTypes.setValue(this.fileTypes.getItems().get(0));
    this.selectGraph = new ComboBox<>();
    this.selectGraph.getItems().addAll("Left Graph", "Right Graph", "Both Graphs");
    this.selectGraph.setValue(this.selectGraph.getItems().get(0));

    this.animated = false;

    lightMode.setOnAction((evt) -> {
      this.lightMode = lightMode.isSelected();
      if (this.lightMode) {
        this.mainPanel.getStylesheets().remove(css);
        this.centerPanel.getStyleClass().remove("insetPane");
        graphPane.getStyleClass().remove("roundBorder");
        savePanel.getStyleClass().remove("roundBorder");
        topPane.getStyleClass().remove("topPane");
        importPanel.getStyleClass().remove("roundBorder");
      } else {
        this.mainPanel.getStylesheets().add(css);
        this.centerPanel.getStyleClass().add("insetPane");
        graphPane.getStyleClass().add("roundBorder");
        bottomPane.getStyleClass().add("bottomPane");
        savePanel.getStyleClass().add("roundBorder");
        topPane.getStyleClass().add("topPane");
        importPanel.getStyleClass().add("roundBorder");
      }
    });
  }

  @Override
  public void addCSVName(String fileName) {
    // Does not work since it will not be supported
  }

  @Override
  public void highlightColumn(String[] values) {
    XYChart.Series<Number, Number> pastSeries = null;
    if (this.selectedGraph != null) { // Gets rid of the past graph, makes a new one in its place
      this.centerPanel.getChildren().remove(1);
      pastSeries = this.selectedGraph.getData().get(0);
    }

    NumberAxis x = new NumberAxis();
    NumberAxis y = new NumberAxis();
    this.selectedGraph = new ScatterChart<>(x, y);
    XYChart.Series<Number, Number> series = new XYChart.Series<>();

    this.selectedGraph.setPrefHeight(this.centerPanel.getHeight());
    this.selectedGraph.setPrefWidth(this.centerPanel.getWidth() / 2);
    this.selectedGraph.setLegendVisible(false);
    this.selectedGraph.setTitle(this.wavelengths.getValue() + this.initGraph.getXAxis().getLabel());

    this.selectedGraph.getData().add(series);
    for (int row = 1; row < values.length; row++) {
      XYChart.Data<Number, Number> datapoint = new XYChart.Data<>(Double.valueOf(values[0]),
          Double.valueOf(values[row]));
      series.getData()
          .add(datapoint);
      datapoint.setExtraValue("Run " + row);
    }
    this.centerPanel.getChildren().add(this.selectedGraph);
    this.selectedGraph.prefHeightProperty().bind(this.centerPanel.heightProperty().divide(2));
    this.selectedGraph.prefWidthProperty().bind(this.centerPanel.widthProperty().divide(2));
    if (pastSeries != null) {
      for (XYChart.Data<Number, Number> newData : series.getData()) {
        for (XYChart.Data<Number, Number> oldData : pastSeries.getData()) {
          if (newData.getExtraValue().equals(oldData.getExtraValue())) {
            newData.setXValue(oldData.getXValue());
          }
        }
      }
    }
  }

  @Override
  public void renderCSV(CSVReader file, String fileName) {
    int spot = 0;
    if (this.initGraph != null) { // Gets rid of the past graph, makes a new one in its place
      spot = this.centerPanel.getChildren().indexOf(this.initGraph);
      this.selectedGraph = null; // resets the selected graph
    }

    // Retries the data
    List<String[]> data;
    try {
      data = file.readAll();
    } catch (Exception e) {
      this.renderError("Cannot read file");
      return;
    }

    // Creates the axes
    NumberAxis xAxisLine = new NumberAxis();
    NumberAxis yAxisLine = new NumberAxis();

    // All the labels / Categories
    String[] labels = data.get(0);

    // General already know settings for x-axis
    xAxisLine.setLabel(labels[0].split(":")[1]);
    yAxisLine.setLabel(labels[1].split(":")[1]);
    xAxisLine.setAutoRanging(false);
    xAxisLine.setLowerBound(Double.parseDouble(data.get(1)[0]) - 7);
    xAxisLine.setUpperBound(Double.parseDouble(data.get(data.size() - 1)[0] + 7));

    this.initGraph = new LineChart<>(xAxisLine, yAxisLine);
    this.initGraph.setAnimated(animated);
    this.initGraph.setAlternativeColumnFillVisible(true);

    // Sets the Labels / Connects the columns as a single line.
    for (int row = 1; row < labels.length; row++) {
      XYChart.Series<Number, Number> series = new XYChart.Series<>();
      series.setName("Run " + row);
      this.initGraph.getData().add(series);
    }

    // Asks for a loading bar and updating the code
    GraphUtil.loadingBar(data, this.initGraph, this.lightMode).start();

    this.wavelengths.setValue("No" + labels[0].split(":")[1] + " Selected");
    this.wavelengths.getItems().add("No" + labels[0].split(":")[1] + " Selected");

    // Puts the line chart on the central pane to see an "animation" of the data being put on it
    this.centerPanel.getChildren().add(spot, this.initGraph);
    this.initGraph.prefHeightProperty().bind(this.centerPanel.heightProperty().divide(2));
    this.initGraph.prefWidthProperty().bind(this.centerPanel.widthProperty().divide(2));
    Platform.runLater(() -> {
    });
  }

  @Override
  public void addFeatures(Features features) {
    importEditButton.setOnAction((evt) -> {
      File selected = this.askForFile(new FileChooser.ExtensionFilter("cvs files", "*.csv"));
      if (selected != null) {
        features.loadAndEditCSV(selected.getPath());
      }
    });

    importButton.setOnAction((evt) -> {
      File selected = this.askForFile(new FileChooser.ExtensionFilter("cvs files", "*.csv"));
      if (selected != null) {
        features.loadCSV(selected.getPath());
      }
    });

    saveToButton.setOnAction((evt) -> {
      File selected = askForDirectory();
      if (selected != null) {
        features.saveCSV(selected.getAbsolutePath() + "\\", this.getTextFromField(this.saveName));
      }
    });

    saveImageButton.setOnAction((evt) -> {
      BufferedImage img;
      try {
        img = this.getImage();
      } catch (IllegalArgumentException e) {
        this.renderError(e.getMessage());
        return;
      }
      File selected = askForDirectory();
      if (selected != null && img != null) {
        String name = this.getTextFromField(this.saveName);
        if (name.equals("")) {
          features.saveImage(selected.getAbsolutePath() + "\\",
              this.selectGraph.getValue() + this.fileTypes.getValue(),
              img);
        } else {
          features.saveImage(selected.getAbsolutePath() + "\\",
              name + this.fileTypes.getValue(),
              img);
        }
      }
    });

    quickSaveButton.setOnAction(
        (evt) -> features.saveCSV("", this.getTextFromField(this.saveName)));

    quickSaveImage.setOnAction((evt) -> {
      BufferedImage img;
      try {
        img = this.getImage();
      } catch (IllegalArgumentException e) {
        this.renderError(e.getMessage());
        return;
      }
      if (img != null) {
        features.saveImage("", this.selectGraph.getValue() + this.fileTypes.getValue(), img);
      }
    });

    wavelengths.setOnAction((evt) -> {
      int itemIndex = this.wavelengths.getItems().indexOf(this.wavelengths.getValue());
      if (itemIndex > 0) {
        features.selectColumn(this.wavelengths.getValue());
      }
    });
  }

  @Override
  public void setXValueLabel(List<String> xValues) {
    String removeThis = xValues.get(0);
    this.wavelengths.getItems().addAll(xValues);
    this.wavelengths.getItems().remove(removeThis);
  }

  @Override
  public String getTextFromField(TextField textField) {
    String text = textField.getText();
    textField.setText("");
    return text;
  }

  @Override
  public void end() {
    Platform.exit();
  }

  @Override
  public void renderMessage(String title, String header, String message) {
    GraphUtil.renderMessage(title, header, message).showAndWait();
  }

  @Override
  public void renderError(String message) {
    GraphUtil.renderError(message).showAndWait();
  }

  @Override
  public void renderWarning(String message) {
    GraphUtil.renderWarning(message).showAndWait();
  }

  @Override
  public Parent getView() {
    return this.mainPanel;
  }

  /**
   * Changes the selected graph Nodes: ComboBox, change Chart textField, xIncrement textField,
   * xLabel textField, yIncrement textField, yLabel textField.
   */
  private void setGraph() {
    ArrayList<XYChart<Number, Number>> graphs = new ArrayList<>(
        Arrays.asList(this.initGraph, this.selectedGraph));
    ArrayList<String> graphNames = new ArrayList<>(
        Arrays.asList("Line Chart (Left)", "Scatter Plot (Right)"));
    ArrayList<Image> images = new ArrayList<>(
        Arrays.asList(ImageUtil.LINE_CHART_ICON, ImageUtil.SCATTER_CHART_ICON));
    if (this.initGraph != null) {
      Stage graphSetter = GraphUtil.setGraphInfo(null, graphs, graphNames, images, lightMode);
      graphSetter.initModality(Modality.APPLICATION_MODAL);
      graphSetter.show();
    } else {
      this.renderWarning("No CSV file loaded, No graph to change");
    }
  }

  /**
   * Saving window that will let the user save either the csv or image. If it is an image the left
   * graph, right graph or the both together can be saved. Names can be given to them, or it may be
   * quick saved.
   */
  private void saveWindow(String title, Button saveTo, Button quickSave, boolean lightMode) {
    Stage saveImageWindow = new Stage();
    saveImageWindow.setTitle(title);
    saveImageWindow.getIcons().add(ImageUtil.SAVING);
    if (this.selectedGraph == null) {
      this.selectGraph.getItems().removeAll("Right Graph", "Both Graphs");
    } else if (this.selectGraph.getItems().size() == 1) {
      this.selectGraph.getItems().addAll("Right Graph", "Both Graphs");
    }
    // Middle pane for all the
    VBox pane = new VBox(5);
    // Label for saving info
    Label info = new Label(
        "Saving an image or csv without a name will automatically be given a name");
    info.setPadding(GraphUtil.DEFAULT_INSET);
    pane.getChildren().add(info);
    // Save name pane
    HBox saveNameInfo = GraphUtil.hBoxCentered();
    saveNameInfo.setPadding(GraphUtil.DEFAULT_INSET);
    saveNameInfo.getChildren().removeAll(saveNameInfo.getChildren());
    saveNameInfo.getChildren().add(this.saveName);
    // Saving button panel
    HBox saveButtonPane = GraphUtil.hBoxCentered();
    saveButtonPane.setPadding(GraphUtil.DEFAULT_INSET);

    if (title.equals("Saving CSV")) {
      this.saveName.setPromptText("CSV Name");
    }
    if (title.equals("Saving Graph Images")) {
      this.saveName.setPromptText("Image Name");
      saveNameInfo.getChildren().addAll(this.fileTypes, this.selectGraph);
    }
    saveButtonPane.getChildren().addAll(saveTo, quickSave);
    pane.getChildren().addAll(saveNameInfo, saveButtonPane);

    if (!lightMode) {
      pane.getStylesheets().add(this.css);
      pane.getStyleClass().add("insetPane");
    }

    saveImageWindow.setScene(new Scene(pane));
    saveImageWindow.initModality(Modality.APPLICATION_MODAL);
    saveImageWindow.setResizable(false);
    saveImageWindow.show();
  }

  private File askForDirectory() {
    final DirectoryChooser dirChooser = new DirectoryChooser();
    dirChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
    Stage newWindow = new Stage();
    newWindow.setTitle("Choose a Directory");
    dirChooser.setTitle("Choose a Directory");
    return dirChooser.showDialog(newWindow);
  }

  private File askForFile(ExtensionFilter filter) {
    final FileChooser fChooser = new FileChooser();
    fChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
    fChooser.getExtensionFilters().add(filter);
    Stage newWindow = new Stage();
    newWindow.setTitle("Choose a File");
    fChooser.setTitle("Choose a File");
    return fChooser.showOpenDialog(newWindow);
  }

  private BufferedImage getImage() throws IllegalArgumentException {
    WritableImage snapshot;
    switch (this.selectGraph.getValue().split(" ")[0]) {
      case "Left" -> snapshot = this.initGraph.snapshot(new SnapshotParameters(), null);
      case "Right" -> snapshot = this.selectedGraph.snapshot(new SnapshotParameters(), null);
      case "Both" -> snapshot = this.centerPanel.snapshot(new SnapshotParameters(), null);
      default -> throw new IllegalArgumentException("No Graph Selected");
    }

    return javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, new BufferedImage(
        (int) Math.round(snapshot.getWidth()), (int) Math.round(snapshot.getHeight()),
        BufferedImage.TYPE_INT_ARGB));
  }

  private void info() {
    this.renderMessage("Info", null, "Animating the graph may cause a missing tick label."
        + System.lineSeparator()
        + "Quick saves are let you quickly save the image or csv to where the loaded csv was from."
        + "The edits are currently only where the last run data is the first two columns, "
        + System.lineSeparator()
        + "and every other run has a wavelength (or x) that is the same as the first wavelength (or x), "
        + "with the output data on every other column."
        + System.lineSeparator()
        + "When you set the x value for the scatter plot, it will keep the x"
        + "value for the next wavelengths you choose.");
  }
}
