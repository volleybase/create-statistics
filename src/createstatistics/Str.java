package createstatistics;

/**
 * String utilities.
 *
 * @author gortonhu
 */
class Str {

  static final String NL = System.lineSeparator();

  static String nnn(int nr) {
    String val = "   " + nr;
    return val.substring(val.length() - 3);
  }

  static String str(String str, int len) {
    String val = "          " + str;
    return val.substring(val.length() - len);
  }
}
