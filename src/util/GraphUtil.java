package util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Axis.TickMark;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class GraphUtil {

  public static String DARK_MODE_COMBOBOX_STYLE = "-fx-text-fill: #B0B0B0;"
      + "-fx-background-color: #B0B0B0;";
  public static String DARK_MODE_BUTTON_STYLE = "-fx-background-color: #494949;"
      + "-fx-text-fill: white;";

  public static String DARK_MODE_TEXT_FIELD_STYLE = "-fx-background-color: #484848;"
      + "-fx-text-fill: #C8C8C8;";

  public static String DARK_MODE_PANE = "-fx-background-color: #57595D;";
  public static String DARK_MODE_PROGRESS_BAR = "-fx-border-color: #686868";
  public static String DARK_MODE_PROGRESS_LABEL = "-fx-text-fill: #C8C8C8";

  public static Insets DEFAULT_INSET = new Insets(5, 5, 5, 5);

  public static Callback<ListView<String>, ListCell<String>> COMBOBOX_LIST_VIEW_STYLE = new Callback<>() {
    @Override
    public ListCell<String> call(ListView<String> param) {
      return new ListCell<>() {
        {
          // Same as: .combo-box .list-cell changes in a css file
          setStyle("-fx-background-color: #B0B0B0;" + "-fx-border-color: #989898;");
        }

        @Override
        public void updateItem(String item,
            boolean empty) {
          super.updateItem(item, empty);
          setText(item);
        }
      };
    }
  };

  /**
   * Sets up a window to get all the info in changing a graph. Nodes: ComboBox, change Chart
   * textField, xIncrement textField, xLabel textField, yIncrement textField, yLabel textField.
   *
   * @return a list of all the scene nodes
   */
  public static Stage setGraphInfo(Stage stage, List<XYChart<Number, Number>> graphs,
      List<String> graphNames, List<Image> images, boolean lightMode) {
    Stage askForAxisInfo = Objects.requireNonNullElseGet(stage, Stage::new);
    askForAxisInfo.setTitle("Setting Graph Info");
    askForAxisInfo.getIcons()
        .add(ImageUtil.LINE_CHART_ICON);
    VBox centerPanel = new VBox(5);
    centerPanel.setPadding(DEFAULT_INSET);
    centerPanel.setAlignment(Pos.CENTER);
    final String[] lastSelectedTick = new String[]{"", ""};
    final String[] lastSelectedData = new String[]{""};
    final HashMap<Integer, Double> xTickChange = new HashMap<>();
    final HashMap<Integer, Double> yTickChange = new HashMap<>();
    final HashMap<String, Data<Number, Number>> dataStored = new HashMap<>();
    final HashMap<Data<Number, Number>, Double> xChange = new HashMap<>();
    final HashMap<Data<Number, Number>, Double> yChange = new HashMap<>();

    // Info label
    Label info = new Label("If any text boxes left empty will be automatically set to "
        + System.lineSeparator()
        + "whatever the CSV file has labelled. Only the Scatter Plot can have it's data changed."
        + System.lineSeparator()
        + "That is because on default the data's x value is set the wavelength"
        + System.lineSeparator()
        + "The Line Chart may have too much data to easily change/");
    info.setPadding(DEFAULT_INSET);
    centerPanel.getChildren().add(info);

    // Combo Box panel
    HBox chartPanel = hBoxCentered();
    // Combo Box / Drop Down Menu
    ComboBox<String> charts = new ComboBox<>();
    String graphName;
    Image lastImage = null;
    HashMap<String, Image> imageStorage = new HashMap<>();
    HashMap<String, XYChart<Number, Number>> chartStorage = new HashMap<>();
    for (int i = 0; i < graphNames.size(); i++) {
      XYChart<Number, Number> currChart = graphs.get(i);
      if (currChart != null) {
        graphName = graphNames.get(i);
        charts.getItems().add(graphName);
        chartStorage.put(graphName, currChart);
        Image currImage = images.get(i);
        if (currImage != null) {
          imageStorage.put(graphName, currImage);
          lastImage = currImage;
        } else {
          imageStorage.put(graphName, lastImage);
        }
      }
    }
    charts.setValue(charts.getItems().get(0));
    Label selectedChart = new Label("Selected: " + graphNames.get(0));
    chartPanel.getChildren().addAll(charts, selectedChart);
    centerPanel.getChildren().add(chartPanel);

    // Graph Title Panel
    HBox graphTitlePanel = hBoxCentered();
    // Changing Graph title
    TextField changeTitle = textField("Graph Title", lightMode);
    // Advance Changes check button
    CheckBox advSettings = new CheckBox("Advanced Changes");
    advSettings.setManaged(false);
    advSettings.setVisible(false);
    graphTitlePanel.getChildren().addAll(changeTitle, advSettings);
    centerPanel.getChildren().add(graphTitlePanel);

    // X Axis pane
    HBox xAxisInfo = hBoxCentered();
    // Pane for changing Y features
    HBox changeX = hBoxCentered();
    // ComboBox for x ticks, used if advanced changes are selected
    final ComboBox<String> xTicks = new ComboBox<>();
    xTicks.setVisible(false);
    xTicks.managedProperty().bind(xTicks.visibleProperty());

    //Text field for increment X
    final TextField askForX = textField("Increment for X-Axis", lightMode);
    changeX.getChildren().add(askForX);
    //Text field for the name X
    final TextField labelForX = textField("Title for the X-Axis", lightMode);
    xAxisInfo.getChildren().addAll(xTicks, changeX, labelForX);

    // Y Axis pane
    HBox yAxisInfo = hBoxCentered();
    // Pane for changing Y features
    HBox changeY = hBoxCentered();
    // ComboBox for y ticks, used if advanced changes are selected
    final ComboBox<String> yTicks = new ComboBox<>();
    yTicks.visibleProperty().bind(xTicks.visibleProperty());
    yTicks.managedProperty().bind(yTicks.visibleProperty());
    // Text field for the increment Y
    final TextField askForY = textField("Increment for Y-Axis", lightMode);
    changeY.getChildren().add(askForY);
    // Text field for the name Y
    final TextField labelForY = textField("Title for the Y-Axis", lightMode);
    yAxisInfo.getChildren().addAll(yTicks, changeY, labelForY);

    // Data Change Pane
    HBox dataChange = hBoxCentered();
    // X data change
    HBox allDataChange = hBoxCentered();
    final ComboBox<String> dataBox = new ComboBox<>();
    dataBox.setVisible(false);
    dataBox.managedProperty().bind(dataBox.visibleProperty());
    final TextField xDataTo = textField("Change X-Data to", lightMode);
    xDataTo.visibleProperty().bind(dataBox.visibleProperty());
    xDataTo.managedProperty().bind(xDataTo.visibleProperty());
    final TextField yDataTo = textField("Change Y-Data to", lightMode);
    yDataTo.visibleProperty().bind(dataBox.visibleProperty());
    yDataTo.managedProperty().bind(yDataTo.visibleProperty());
    if (charts.getItems().size() > 1) {
      // Processes the data to show which ones can be changed aka only the scatter plot.
      for (Series<Number, Number> numberSeries : graphs.get(1).getData()) {
        ObservableList<Data<Number, Number>> dataList = numberSeries.getData();
        for (Data<Number, Number> data : dataList) {
          String dataName =
              "(" + data.getXValue() + "," + data.getYValue() + ")" + " " + data.getExtraValue();
          dataBox.getItems().add(dataName);
          dataStored.put(dataName, data);
        }
      }
      dataBox.setValue(dataBox.getItems().get(0));
    }
    allDataChange.getChildren().addAll(dataBox, xDataTo, yDataTo);
    dataChange.getChildren().addAll(allDataChange);

    // Data Change Pane
    VBox rangeChange = new VBox(5);
    rangeChange.setAlignment(Pos.CENTER);
    // X data change
    HBox xRange = new HBox(5);
    xRange.setAlignment(Pos.CENTER);
    final TextField xStart = textField("Start X to", lightMode);
    final TextField xEnd = textField("End X to", lightMode);
    xRange.getChildren().addAll(xStart, xEnd);
    // Y data Change
    HBox yRange = new HBox(5);
    yRange.setAlignment(Pos.CENTER);
    final TextField yStart = textField("Start Y to", lightMode);
    final TextField yEnd = textField("End Y to", lightMode);
    yRange.getChildren().addAll(yStart, yEnd);
    rangeChange.getChildren().addAll(xRange, yRange);

    Button submit = new Button("Submit");
    submit.setOnAction((evt) -> {
      if (charts.getValue() == null) {
        renderWarning("Please Select a chart to change!");
      } else {
        storeAnswerStringToDouble(dataBox, xDataTo, lastSelectedData, dataStored, xChange);
        storeAnswerStringToDouble(dataBox, yDataTo, lastSelectedData, dataStored, yChange);
        for (Entry<Data<Number, Number>, Double> entry : xChange.entrySet()) {
          entry.getKey().setXValue(entry.getValue());
        }
        for (Entry<Data<Number, Number>, Double> entry : yChange.entrySet()) {
          entry.getKey().setYValue(entry.getValue());
        }

        XYChart<Number, Number> currChart = chartStorage.get(charts.getValue());
        NumberAxis oldY = (NumberAxis) currChart.getYAxis();
        NumberAxis oldX = (NumberAxis) currChart.getXAxis();
        if (!changeTitle.getText().equals("")) {
          currChart.setTitle(changeTitle.getText());
        }
        ObservableList<TickMark<Number>> oldXTicks = oldX.getTickMarks();
        ObservableList<TickMark<Number>> oldYTicks = oldY.getTickMarks();
        if (xTicks.isVisible() || yTicks.isVisible()) {
          for (Entry<Integer, Double> entry : xTickChange.entrySet()) {
            if (entry.getKey() != -1) {
              oldXTicks.get(entry.getKey()).setValue(entry.getValue());
            }
          }
          for (Entry<Integer, Double> entry : yTickChange.entrySet()) {
            if (entry.getKey() != -1) {
              oldYTicks.get(entry.getKey()).setValue(entry.getValue());
            }
          }
          double x = textFieldAsDouble(askForX);
          double y = textFieldAsDouble(askForY);
          if (x != Double.MAX_VALUE) {
            oldXTicks.get(xTicks.getItems().indexOf(xTicks.getValue())).setValue(x);
          }
          if (y != Double.MAX_VALUE) {
            oldYTicks.get(yTicks.getItems().indexOf(yTicks.getValue())).setValue(y);
          }
        } else {
          double incX = textFieldAsDouble(askForX);
          double incY = textFieldAsDouble(askForY);
          double startX = textFieldAsDouble(xStart);
          double startY = textFieldAsDouble(yStart);
          double endX = textFieldAsDouble(xEnd);
          double endY = textFieldAsDouble(yEnd);
          if (incX != Double.MAX_VALUE) {
            oldX.setTickUnit(incX);
          }
          if (incY != Double.MAX_VALUE) {
            oldY.setTickUnit(incY);
          }
          if (startX != Double.MAX_VALUE) {
            oldX.setLowerBound(startX);
          }
          if (startY != Double.MAX_VALUE) {
            oldY.setLowerBound(startY);
          }
          if (endX != Double.MAX_VALUE) {
            oldX.setUpperBound(endX);
          }
          if (endY != Double.MAX_VALUE) {
            oldY.setUpperBound(endY);
          }
          if (!labelForX.getText().equals("")) {
            oldX.setLabel(labelForX.getText());
          }
          if (!labelForY.getText().equals("")) {
            oldY.setLabel(labelForY.getText());
          }
        }
        askForAxisInfo.close();
      }
    });

    // Disabled as the tick mark change does not work rn
    advSettings.setOnAction((evt) -> {
      if (advSettings.isSelected()) {
        XYChart<Number, Number> currChart = getChart(charts, chartStorage);
        NumberAxis xAxis = (NumberAxis) currChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) currChart.getYAxis();
        ObservableList<TickMark<Number>> oldXTicks = xAxis.getTickMarks();
        ObservableList<TickMark<Number>> oldYTicks = yAxis.getTickMarks();
        askForX.setPromptText("Change X-Axis To");
        askForY.setPromptText("Change Y-Axis to");
        xTicks.getItems().removeAll(xTicks.getItems());
        yTicks.getItems().removeAll(yTicks.getItems());
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
          if (i < oldXTicks.size()) {
            String xText = "X Tick at " + oldXTicks.get(i).getValue();
            xTicks.getItems().add(xText);
          }
          if (i < oldYTicks.size()) {
            String yText = "Y Tick at " + oldYTicks.get(i).getValue();
            yTicks.getItems().add(yText);
          }
          if (i >= oldXTicks.size() && i >= oldYTicks.size()) {
            break;
          }
        }
        xTicks.setVisible(true);
        xTicks.setValue(xTicks.getItems().get(0));
        lastSelectedTick[0] = xTicks.getValue();
        yTicks.setValue(yTicks.getItems().get(0));
        lastSelectedTick[1] = yTicks.getValue();
      } else {
        xTicks.setVisible(false);
      }
      askForAxisInfo.sizeToScene();
    });

    // ActonEvent for Charts Combo Box
    charts.setOnAction((evt) -> {
      String val = charts.getValue();
      if (val != null && chartStorage.getOrDefault(val, null) != null) {
        selectedChart.setText("Selected: " + val);
        askForAxisInfo.getIcons().removeAll(askForAxisInfo.getIcons());
        askForAxisInfo.getIcons().add(imageStorage.get(val));
        if (advSettings.isSelected()) {
          advSettings.getOnAction().handle(null);
        }
        dataBox.setVisible(charts.getItems().indexOf(val) == 1);
        askForAxisInfo.sizeToScene();
      }
    });

    // ActionEvent for dataChange ComboBox
    dataBox.setOnAction((evt) -> {
      storeAnswerStringToDouble(dataBox, xDataTo, lastSelectedData, dataStored, xChange);
      storeAnswerStringToDouble(dataBox, yDataTo, lastSelectedData, dataStored, yChange);
    });

    // ActionEvent for xTicks ComboBox
    xTicks.setOnAction(
        (evt) -> storedTickChange(xTicks, askForX, lastSelectedTick, 0, xTickChange));

    // ActionEvent for yTicks ComboBox
    yTicks.setOnAction(
        (evt) -> storedTickChange(yTicks, askForY, lastSelectedTick, 1, yTickChange));

    centerPanel.getChildren().addAll(xAxisInfo, yAxisInfo, rangeChange, dataChange, submit);
    if (!lightMode) {
      xTicks.setStyle(DARK_MODE_COMBOBOX_STYLE);
      centerPanel.setStyle("-fx-background-color: #707070;");
      dataBox.setStyle(DARK_MODE_COMBOBOX_STYLE);
      submit.setStyle(DARK_MODE_BUTTON_STYLE);
      charts.setStyle(DARK_MODE_COMBOBOX_STYLE);
      xTicks.setStyle(
          "-fx-background-color: #B0B0B0; ");
      dataBox.setCellFactory(COMBOBOX_LIST_VIEW_STYLE);
      charts.setCellFactory(COMBOBOX_LIST_VIEW_STYLE);
    } else {
      xTicks.setStyle(null);
      centerPanel.setStyle(null);
      dataBox.setStyle(null);
      submit.setStyle(null);
      charts.setStyle(null);
    }
    askForAxisInfo.setScene(new Scene(centerPanel));
    askForAxisInfo.setResizable(false);
    return askForAxisInfo;
  }

  public static Thread loadingBar(List<String[]> data, XYChart<Number, Number> chart,
      boolean lightMode) {
    final String[] labels = data.get(0);
    final int maxRows = data.size();
    final int maxCols = labels.length;
    Task<List<Data<Number, Number>>> createDataPoint = new Task<>() {
      final DecimalFormat percentage = new DecimalFormat("##.##");

      @Override
      protected List<Data<Number, Number>> call() {
        ArrayList<Data<Number, Number>> dataPoints = new ArrayList<>();
        for (int row = 1; row < maxRows; row++) { // not top row
          String[] rowData = data.get(row);
          int finalRow = row;
          Platform.runLater(() -> {
            updateProgress(finalRow + 1, maxRows);
            updateMessage("Loading " + percentage.format(getProgress() * 100) + "%");
            for (int col = 1; col < maxCols; col++) { // not wave length data
              XYChart.Data<Number, Number> datapoint = new XYChart.Data<>(
                  Double.valueOf(rowData[0]),
                  Double.valueOf(rowData[col]));
              datapoint.setExtraValue(labels[col]);
              dataPoints.add(datapoint);
            }
          });
        }
        return dataPoints;
      }
    };

    // Progress bar
    ProgressBar pBar = new ProgressBar();
    pBar.setPadding(new Insets(5, 5, 5, 5));
    pBar.progressProperty().bind(createDataPoint.progressProperty());
    // Label for progress bar
    Label statusLabel = new Label();
    statusLabel.setText("Loading 0.0%");
    statusLabel.setPadding(new Insets(5, 5, 5, 5));
    statusLabel.textProperty().bind(createDataPoint.messageProperty());
    // Stage and scene of loading
    VBox root = new VBox(5, statusLabel, pBar);
    root.setAlignment(Pos.CENTER);
    Stage loadingStage = new Stage();
    loadingStage.setTitle("Loading...");
    loadingStage.getIcons().add(ImageUtil.LOADING_ICON);
    loadingStage.setScene(new Scene(root, 90, 70));
    loadingStage.sizeToScene();
    loadingStage.setResizable(false);
    loadingStage.show();

    if (!lightMode) {
      root.setStyle(DARK_MODE_PANE);
      pBar.setStyle(DARK_MODE_PROGRESS_BAR);
      statusLabel.setStyle(DARK_MODE_PROGRESS_LABEL);
    }

    createDataPoint.setOnSucceeded((evt) -> {
      List<Data<Number, Number>> points = createDataPoint.getValue();
      for (int i = 0; i < points.size(); i++) {
        int seriesNumber = i % chart.getData().size();
        Data<Number, Number> point = points.get(i);
        chart.getData().get(seriesNumber).getData()
            .add(point);
        Node node = point.getNode();
        node.setVisible(false);
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        renderError(e.getMessage());
      }
      Platform.runLater(loadingStage::hide);
    });
    Thread loading = new Thread(createDataPoint);
    loading.setDaemon(true);
    return loading;
  }

  public static XYChart<Number, Number> getChart(ComboBox<String> chartBox,
      Map<String, XYChart<Number, Number>> storage) {
    String name = chartSelected(chartBox);
    XYChart<Number, Number> currChart = storage.getOrDefault(name, null);
    if (currChart == null) {
      renderWarning("There is no " + name + " loaded");
      throw new IllegalArgumentException("There is no " + name + " loaded");
    }
    return currChart;
  }

  public static String chartSelected(ComboBox<String> chartBox) {
    String name = chartBox.getValue();
    if (name == null) {
      renderWarning("Please select a graph before you check advanced changes");
      throw new IllegalArgumentException("Please select a graph before you check advanced changes");
    }
    return name;
  }

  /**
   * Checks if a string is a double.
   *
   * @param s is the string in question
   * @return the double
   */
  public static double stringAsDouble(String s) {
    try {
      if (s.equals("")) {
        return Double.MAX_VALUE; // Flag for when there is no string
      }
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      renderWarning("'" + s + "' is not a valid number, please reenter a number");
      return Double.MAX_VALUE;
    }
  }

  /**
   * Checks if a textField's text is a double.
   *
   * @param tf is the text field
   * @return the double in the text-field
   */
  public static double textFieldAsDouble(TextField tf) {
    return stringAsDouble(tf.getText());
  }

  /**
   * Sets up a simple alert Dialogue that is shown and waited
   *
   * @param at      is the alert type that will show
   * @param title   is the title of the window/alert
   * @param header  is the header message which usually is null
   * @param message is the message you want to tell the user
   * @return the Alert just in case some changes are wanted.
   */
  public static Alert alertDialogue(AlertType at, String title, String header, String message) {
    Alert dialogue = new Alert(at);
    dialogue.setWidth(200);
    dialogue.setTitle(title);
    dialogue.setHeaderText(header);
    dialogue.setContentText(message);
    return dialogue;
  }

  /**
   * Renders an informative message to the user
   *
   * @param title   is the title of the window/alert
   * @param header  is the header message which usually is null
   * @param message is the message you want to tell the user
   * @return the Alert just in case some changes are wanted.
   */
  public static Alert renderMessage(String title, String header, String message) {
    return alertDialogue(AlertType.INFORMATION, title, header, message);
  }

  /**
   * Renders an error message to the user
   *
   * @param message is the error message, usually the message of an exception
   * @return the Alert just in case some changes are wanted.
   */
  public static Alert renderError(String message) {
    return alertDialogue(AlertType.ERROR, "Error!", null, message);
  }

  /**
   * Renders a warning message to the user, similar to the render error however with a warning sign
   *
   * @param message is the warning message, usually the message of an exception like the
   * @return the Alert just in case some changes are wanted.
   */
  public static Alert renderWarning(String message) {
    return alertDialogue(AlertType.WARNING, "Warning!", null, message);
  }

  /**
   * Default textField used in this program.
   *
   * @param prompt is the prompt name
   * @return the textField with default configurations
   */
  public static TextField textField(String prompt, boolean lightMode) {
    TextField tf = new TextField();
    tf.setPromptText(prompt);
    tf.setFocusTraversable(false);
    if (!lightMode) {
      tf.setStyle(DARK_MODE_TEXT_FIELD_STYLE);
    } else {
      tf.setStyle(null);
    }
    return tf;
  }

  /**
   * Default HBox that has alignment centered.
   *
   * @return HBox with the default configurations
   */
  public static HBox hBoxCentered() {
    HBox hb = new HBox(5);
    hb.setAlignment(Pos.CENTER);
    return hb;
  }

  private static void storedTickChange(ComboBox<String> box, TextField tf,
      String[] lastSelected, int selectedVal, Map<Integer, Double> toStorage) {
    if (!tf.getText().equals("")) {
      try {
        double val = textFieldAsDouble(tf);
        if (val != Double.MAX_VALUE) {
          toStorage.put(box.getItems().indexOf(lastSelected[1]),
              val);
          lastSelected[selectedVal] = box.getValue();
          tf.setText("");
        }
      } catch (IllegalArgumentException e) {
        box.setValue(lastSelected[1]);
        renderWarning(e.getMessage());
      }
    }
  }

  // Used to store answer for specifically change the data points
  private static <T> void storeAnswerStringToDouble(ComboBox<String> box, TextField tf,
      String[] lastSelected, Map<String, T> fromStorage,
      Map<T, Double> toStorage) {
    if (!tf.getText().equals("")) {
      try {
        double val = textFieldAsDouble(tf);
        if (val != Double.MAX_VALUE) {
          toStorage.put(fromStorage.get(box.getValue()),
              val);
          lastSelected[0] = box.getValue();
          tf.setText("");
        }
      } catch (IllegalArgumentException e) {
        box.setValue(lastSelected[0]);
        renderWarning(e.getMessage());
      }
    }
  }
}
