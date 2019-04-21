package json;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A json object.
 *
 * @author volleybase
 */
public class Object implements JsonX {

  HashMap<String, JsonX> values = new HashMap<>();

  public void add(String key, int value) {
    add(key, new Value(value));
  }

  public void add(String key, float value) {
    add(key, new Value(value));
  }

  public void add(String key, String value) {
    add(key, new Value(value));
  }

  public void add(String key, JsonX value) {
    values.put(key, value);
  }

  @Override
  public String stringify() {
    return stringify(0);
  }

  @Override
  public String stringify(int indent) {
    StringBuilder sb = new StringBuilder();
    sb.append("{\n");

    boolean first = true;
    for (Map.Entry<String, JsonX> entry : values.entrySet()) {
      if (!first) {
        sb.append(",\n");
      } else {
        first = false;
      }
      if (indent > 0) {
        sb.append(String.join("", Collections.nCopies(indent, " ")));
      }
      sb.append("\"")
        .append(entry.getKey())
        .append("\"")
        .append(": ")
        .append(entry.getValue().stringify(indent + 2));
    }
    sb.append("\n");

    if (indent > 1) {
      sb.append(String.join("", Collections.nCopies(indent - 2, " ")));
    }
    sb.append("}");

    return sb.toString();
  }
}
