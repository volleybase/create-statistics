package svg;

/**
 * A styled node.
 *
 * @author volleybase
 */
public class XmlNodeStyled extends XmlNode {

  // to collect the styles
  private StringBuilder styles = null;

  /**
   * Creates the styled node.
   *
   * @param tag The tag.
   */
  public XmlNodeStyled(String tag) {
    super(tag);
  }

  /**
   * Sets the style class.
   *
   * @param clazz The style class to use.
   * @return The node itself.
   */
  public XmlNodeStyled clazz(String clazz) {
    attr("class", clazz);
    return this;
  }

  /**
   * Adds a style.
   *
   * @param key The name of the style.
   * @param value The value of the style.
   * @return The node itself.
   */
  public final XmlNodeStyled style(String key, String value) {
    if (styles == null) {
      styles = new StringBuilder();
    } else {
      styles.append(" ");
    }

    styles.append(key).append(": ").append(value).append(";");

    attr("style", styles.toString());

    return this;
  }
}
