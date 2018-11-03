package createstatistics;

/**
 * A specialized string buffer.
 *
 * @author gortonhu
 */
class SB {

  private final StringBuilder sb = new StringBuilder();

  SB() {
  }

  SB(CharSequence str) {
    this();
    sb.append(str);
  }

  SB append(CharSequence str) {
    sb.append(str);
    return this;
  }

  SB replace(String search, int val) {
    return replace(search, val, false);
  }

  SB replace(String search, int val, boolean showNegativ) {

    int pos = sb.indexOf(search);
    String insert = val >= 0 || showNegativ ? "" + val : "&nbsp;";
    sb.replace(pos, pos + search.length(), insert);

    return this;
  }

  SB replace(String search, String val) {

    try {
      int pos = sb.indexOf(search);
      sb.replace(pos, pos + search.length(), val);
    } catch (StringIndexOutOfBoundsException ex) {
      System.err.println(ex);
    }
    return this;
  }

  CharSequence toStr() {
    return sb;
  }

  @Override
  public String toString() {
    return sb.toString();
  }
}
