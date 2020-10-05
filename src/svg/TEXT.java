package svg;

/**
 * A text.
 *
 * @author volleybase
 */
public class TEXT extends XmlNodeStyled {

  /**
   * Creates a text.
   *
   * @param x The x position of the text.
   * @param y The y position of the text.
   * @param alignment The alignment of the text.
   * @param text The text.
   * @throws Exception
   */
  public TEXT(int x, int y, String alignment, String text) throws Exception {
    super("text");
    attr("x", "" + x);
    attr("y", "" + y);

    switch (alignment) {
      case "LT":
        style("text-anchor", "start");
        attr("dy", "0.95em");
        break;
      case "T":
        style("text-anchor", "middle");
        attr("dy", "0.95em");
        break;
      case "RT":
        style("text-anchor", "end");
        attr("dy", "0.95em");
        break;

      case "L":
        style("text-anchor", "start");
        attr("dy", "0.35em");
        break;
      case "C":
        style("text-anchor", "middle");
        attr("dy", "0.35em");
        break;
      case "R":
        style("text-anchor", "end");
        attr("dy", "0.35em");
        break;

      case "LB":
        style("text-anchor", "start");
        attr("dy", "-0.25em");
        break;
      case "B":
        style("text-anchor", "middle");
        attr("dy", "-0.25em");
        break;
      case "RB":
        style("text-anchor", "end");
        attr("dy", "-0.25em");
        break;

      default:
        throw new Exception("Invalid text alignment: " + alignment + "!");
    }

    text(text);
  }
}
