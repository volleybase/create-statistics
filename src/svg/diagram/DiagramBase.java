package svg.diagram;

import svg.RECT;
import svg.SVG;

/**
 * The base class of the diagrams.
 *
 * @author volleybase
 */
public abstract class DiagramBase {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  // the x axis
  protected XAxis xaxis = null;
  // the y axis
  protected YAxis yaxis = null;
  // the sizing factor
  protected final int factor = 30;
  // the resulting width
  protected int width = 0;
  // the resulting height
  protected int height = 0;

  // origin - x position
  protected int origin_x = 0;
  // origin - y position
  protected int origin_y = 0;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Svg output.">
  /**
   * Creates the svg code.
   *
   * @return The created svg code.
   * @throws Exception
   */
  public abstract String svg() throws Exception;

  /**
   * Creates the background.
   *
   * @return The main svg.
   * @throws Exception
   */
  protected SVG createBackground() throws Exception {
    SVG svg = new SVG(width, height);
    RECT rect = new RECT(0, 0, width, height);
    rect.style("fill", "gold");
    svg.add(rect);

    return svg;
  }
  //</editor-fold>
}
