package json;

/**
 * A json base value.
 *
 * @author volleybase
 */
public interface JsonX {

  /**
   * Converts to JSON string.
   *
   * @return JSON data as string.
   */
  String stringify();

  /**
   * Converts to JSON string.
   *
   * @param indent The indentation.
   * @return JSON data as string.
   */
  String stringify(int indent);
}
