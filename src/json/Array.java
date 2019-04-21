package json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A json array.
 *
 * @author volleybase
 */
public class Array implements JsonX {

  private final List<JsonX> values = new ArrayList<>();

  public void add(int value) {
    values.add(new Value(value));
  }

  public void add(float value) {
    values.add(new Value(value));
  }

  public void add(String value) {
    values.add(new Value(value));
  }

  public void add(JsonX value) {
    values.add(value);
  }

  @Override
  public String stringify() {
    return stringify(0);
  }

  @Override
  public String stringify(int indent) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");

    int ind = 0;

    // check for additional empty line if first item is an object
    if (!values.isEmpty()) {
      JsonX val1 = values.get(0);
      if (val1 instanceof json.Object) {
        sb.append("\n");
        sb.append(String.join("", Collections.nCopies(indent + 2, " ")));
        ind = 4;
      }
    }

    boolean first = true;
    for (JsonX val : values) {
      if (!first) {
        sb.append(", ");
      } else {
        first = false;
      }
      sb.append(val.stringify(indent + ind));
    }

    // check for additional empty line if last item is an object
    if (!values.isEmpty()) {
      JsonX valLast = values.get(values.size() - 1);
      if (valLast instanceof json.Object) {
        sb.append("\n");
        if (indent > 0) {
          sb.append(String.join("", Collections.nCopies(indent, " ")));
        }
      }
    }

    sb.append("]");
    return sb.toString();
  }
}
