package createstatistics.util;

/**
 * String utilities.
 *
 * @author volleybase
 */
public class Str {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  /**
   * An abbreviation for new line.
   */
  public static final String NL = System.lineSeparator();
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="The methods used to format the debug output.">
  /**
   * Pads a given number with spaces to fill 3 characters.
   *
   * @param nr The number to pad.
   * @return Th epadded number.
   */
  public static String nnn(int nr) {
    String val = "   " + nr;
    return val.substring(val.length() - 3);
  }

  /**
   * Pads a given value with spaces.
   *
   * @param str The value to pad.
   * @param len The length of the result.
   * @return The padded value.
   */
  public static String str(String str, int len) {
    String val = "          " + str;
    return val.substring(val.length() - len);
  }
  //</editor-fold>
}
