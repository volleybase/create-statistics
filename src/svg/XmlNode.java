package svg;

import java.util.ArrayList;
import java.util.List;

/**
 * A xml node.
 *
 * @author volleybase
 */
public class XmlNode {

  private final String tag;
  private String text = null;
  private List<XmlNode> content = null;
  private List<String> attrKeys = null;
  private List<String> attrVals = null;

  public XmlNode(String tag) {
    this.tag = tag;
  }

  /**
   * Adds an attribute.
   *
   * @param key The key.
   * @param value The value.
   * @return The node itself.
   */
  public final XmlNode attr(String key, String value) {
    // lazy creation of attributes store
    if (attrKeys == null) {
      attrKeys = new ArrayList<>();
      attrVals = new ArrayList<>();
    }

    // if attributes exists: set new value
    if (hasAttr(key)) {
      int pos = attrKeys.indexOf(key);
      attrVals.set(pos, value);

    } else {
      // else: add new attribute
      attrKeys.add(key);
      attrVals.add(value);
    }

    return this;
  }

  protected boolean hasAttr(String key) {
    return attrKeys != null && attrKeys.contains(key);
  }

  protected String getAttr(String key) {
    if (hasAttr(key)) {
      int pos = attrKeys.indexOf(key);
      return attrVals.get(pos);
    }

    return null;
  }

  /**
   * Adds a content.
   *
   * @param node The content node.
   * @return The node itself.
   * @throws java.lang.Exception
   */
  public XmlNode add(XmlNode node) throws Exception {
    // check
    if (text != null) {
      throw new Exception("Its not allowed to add text content and xml nodes!");
    }

    // lazy creation of attributes store
    if (content == null) {
      content = new ArrayList<>();
    }

    // add content
    content.add(node);

    return this;
  }

  /**
   * Sets the text content.
   *
   * @param text The text content.
   * @return The node itself.
   * @throws java.lang.Exception
   */
  public final XmlNode text(String text) throws Exception {
    // check
    if (content != null) {
      throw new Exception("Its not allowed to add text content and xml nodes!");
    }

    // sets the text content
    this.text = text;

    return this;
  }

  /**
   * Creates the xml output.
   *
   * @return The xml output.
   */
  public String out() {
    return out(0);
  }

  public String out(int indent) {
    StringBuilder sb = new StringBuilder();

    // open tag
    sb.append(indent(indent));
    sb.append("<").append(tag);

    // attributes
    if (attrKeys != null) {
      for (int i = 0, i2 = attrKeys.size(); i < i2; ++i) {
        sb.append(" ").append(attrKeys.get(i)).append("=\"").append(attrVals.get(i)).append("\"");
      }
    }

    boolean anyContent = false;

    // text
    if (text != null) {
      anyContent = true;
      sb.append(">").append(escape(text));
    } else if (content != null) {
      anyContent = true;
      sb.append(">\n");
      content.forEach((node) -> {
        sb.append(node.out(indent + 2)).append("\n");
      });
    }

    // close
    if (anyContent) {
      if (content != null) {
        sb.append(indent(indent));
      }
      sb.append("</").append(tag).append(">");
    } else {
      sb.append(" />");
    }

    return sb.toString();
  }

  private static final String SPACES = "                                                                                         ";

  private static String indent(int indent) {
    return SPACES.substring(0, indent);
  }

  /**
   * Escapes the text.
   *
   * @param txt The text to escape.
   * @return The escaped text.
   */
  private static String escape(String txt) {
    return txt;
  }
}
