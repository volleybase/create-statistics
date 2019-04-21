package json;

/**
 * A json primitive.
 */
public class Value implements JsonX {

  public enum Type {
    NULL,
    INTEGER,
    NUMBER,
    STRING,
    // ?? BOOLEAN,
    ARRAY,
    OBJECT
  }

  private Type type = Type.NULL;
  private int iNumber;
  private float number;
  private String string;

  public Value(int value) {
    set(value);
  }

  public Value(float value) {
    set(value);
  }

  public Value(String value) {
    set(value);
  }

  public final void set(int value) {
    type = Type.INTEGER;
    iNumber = value;
  }

  public final void set(float value) {
    type = Type.NUMBER;
    number = value;
  }

  public final void set(String value) {
    type = Type.STRING;
    string = value;
  }

  public void set(Value value) throws Exception {
    switch (value.type) {
      case NULL:
        type = Type.NULL;
        break;
      case INTEGER:
        set(value.iNumber);
        break;
      case NUMBER:
        set(value.number);
        break;
      case STRING:
        set(value.string);
        break;
      default:
        throw new Exception("Invalid type!");
    }
  }

  @Override
  public String stringify() {
    return stringify(0);
  }

  @Override
  public String stringify(int indent) {

    switch (type) {
      case NULL:
        return "";

      case INTEGER:
        return Integer.toString(iNumber);

      case NUMBER:
        return Float.toString(number);

      case STRING:
        return "\"" + string + "\"";
    }

    return "error";
  }
}
