package svg;

/**
 * A rectangle.
 *
 * @author volleybase
 */
public class RECT extends XmlNodeStyled {

  /**
   * Creates a rectangle.
   *
   * @param x The x position of the rectangle.
   * @param y The y position of the rectangle.
   * @param width The width of the rectangle.
   * @param height The height of the rectangle.
   */
  public RECT(int x, int y, int width, int height) {
    super("rect");
    attr("x", "" + x);
    attr("y", "" + y);
    attr("width", "" + width);
    attr("height", "" + height);
  }
}
