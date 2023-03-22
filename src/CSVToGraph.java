import controller.CSVControllerV1;
import controller.Features;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.CSVModelV1;
import util.ImageUtil;
import view.CSVGUIView;
import view.CSVGUIViewV1;

/**
 * A Class that represents the whole app that creates a line chart and scatter plot for a csv. It
 * can be imported and edited or imported without edits and shown in a line chart. The scatter plot
 * will be
 */
public class CSVToGraph extends Application {

  /**
   * Launches the application for JavaFX, setting up the basic things for creating the GUI.
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Sets empty model, views and controller for the user to use.
   *
   * @param stage is the window in which all of this will be shown in
   */
  @Override
  public void start(Stage stage) {
    CSVModelV1 model = new CSVModelV1();
    CSVGUIView view = new CSVGUIViewV1();
    Features controller = new CSVControllerV1(model);
    controller.setView(view);

    stage.setTitle(
        "CSV to Graph created by Jonathan Yu             Contact Me at: yu.jona@northeastern.edu");
    stage.setScene(new Scene(view.getView()));
    stage.getIcons().add(ImageUtil.LOGO);
    stage.setMaximized(true);
    stage.show();
  }
}
