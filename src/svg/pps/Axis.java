package svg.pps;

import java.util.List;
import svg.G;
import svg.LINE;
import svg.TEXT;

/**
 * An axis.
 *
 * @author volleybase
 */
public abstract class Axis {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  //protected final int factor = 25;
  // The minimum value.
  protected int min = 0;
  // The maximum value.
  protected int max = 0;
  // the labels
  protected List<String> labels = null;
  // draw the labels at the tick or in the area between the ticks
  protected boolean areaLabels = false;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Labels.">
  /**
   * Adds the labels for the ticks.
   *
   * @param labels The labels.
   * @return The axis itself.
   */
  public Axis labels(List<String> labels) {
    return labels(labels, false);
  }

  /**
   * Adds the labels.
   *
   * @param labels The labels.
   * @param area True to create area labels, false to draw them at the ticks.
   * @return The axis itself.
   */
  public Axis labels(List<String> labels, boolean area) {
    this.labels = labels;
    areaLabels = area;
    max = this.labels.size() - 1;
    return this;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Svg output.">
  /**
   * Creates the svg code of the axis.
   *
   * @param x The x position.
   * @param y The y position.
   * @param factor The scaling factor.
   * @return The svg code.
   * @throws Exception
   */
  public abstract G svg(int x, int y, int factor) throws Exception;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Styling.">
  /**
   * Styles a line.
   *
   * @param line The line to style.
   */
  protected void styles(LINE line) {
    line.style("stroke", "#111")
      .style("stroke-width", "1")
      .style("stroke-linecap", "round")
      .style("stroke-linejoin", "round");
  }

  /**
   * Styles a text.
   *
   * @param text The text to style.
   */
  protected void styles(TEXT text) {
    text.style("fill", "#111")
      .style("stroke", "none")
      .style("font", "12px sans-serif");
  }
  //</editor-fold>
}
