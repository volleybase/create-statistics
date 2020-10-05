package svg;

/**
 * A circle.
 *
 * @author volleybase
 */
public class CIRCLE extends XmlNodeStyled {

  /**
   * Creates a circle.
   *
   * @param cx The x position of the center of the circle.
   * @param cy The y position of the center of the circle.
   * @param r The radius of the circle.
   */
  public CIRCLE(int cx, int cy, int r) {
    super("circle");
    attr("cx", "" + cx);
    attr("cy", "" + cy);
    attr("r", "" + r);
  }
}
