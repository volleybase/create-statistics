package svg;

/**
 * A line.
 *
 * @author volleybase
 */
public class LINE extends XmlNodeStyled {

  /**
   * Creates the line.
   *
   * @param x1 The x position of the start of the line.
   * @param y1 The y position of the start of the line.
   * @param x2 The x position of the end of the line.
   * @param y2 The y position of the end of the line.
   */
  public LINE(int x1, int y1, int x2, int y2) {
    super("line");
    attr("x1", "" + x1);
    attr("y1", "" + y1);
    attr("x2", "" + x2);
    attr("y2", "" + y2);
  }
}
