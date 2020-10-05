package svg;

/**
 * The main svg node.
 *
 * @author volleybase
 */
public class SVG extends XmlNode {

  // <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" height="1160" width="820" viewBox="0 0 820 1160">
  public SVG(int width, int height) {
    super("svg");
    attr("xmlns", "http://www.w3.org/2000/svg");
    attr("xmlns:xlink", "http://www.w3.org/1999/xlink");
    attr("width", "" + width);
    attr("height", "" + height);
    attr("viewBox", "0 0 " + width + " " + height);
  }
}
