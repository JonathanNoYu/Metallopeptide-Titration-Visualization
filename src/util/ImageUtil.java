package util;

import java.util.Objects;
import javafx.scene.image.Image;

public class ImageUtil {

  public static Image LINE_CHART_ICON = new Image(Objects.requireNonNull(
      Thread.currentThread().getContextClassLoader().getResourceAsStream("LineChartGraph.png")));
  public static Image SCATTER_CHART_ICON = new Image(Objects.requireNonNull(
      Thread.currentThread().getContextClassLoader().getResourceAsStream("ScatterChartGraph.png")));
  public static Image LOADING_ICON = new Image(Objects.requireNonNull(
      Thread.currentThread().getContextClassLoader().getResourceAsStream("loading circle.png")));
  public static Image LOGO = new Image(Objects.requireNonNull(
      Thread.currentThread().getContextClassLoader().getResourceAsStream("logo.png")));
  public static Image SAVING = new Image(Objects.requireNonNull(
      Thread.currentThread().getContextClassLoader().getResourceAsStream("saving.png")));
}
