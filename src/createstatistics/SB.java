package createstatistics;

/**
 * A specialized string buffer.
 *
 * @author volleybase
 */
class SB {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  // the internal sting builder
  private final StringBuilder sb = new StringBuilder();
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="The constructors.">
  /**
   * Creates the extended string builder.
   */
  SB() {
  }

  /**
   * Creates the extended string builder.
   *
   * @param str The initial value.
   */
  SB(CharSequence str) {
    this();
    sb.append(str);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Getters.">
  /**
   * Checks if there isn't any content.
   *
   * @return True, if empty, otherwise false.
   */
  public boolean isEmpty() {
    return sb.length() > 0;
  }

  /**
   * Returns the length of the content.
   *
   * @return The length of the content.
   */
  public int length() {
    return sb.length();
  }

  /**
   * Returns the current content.
   *
   * @return The current content.
   */
  public CharSequence toStr() {
    return sb;
  }

  @Override
  public String toString() {
    return sb.toString();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="The methods.">
  /**
   * Appends a character sequence.
   *
   * @param str The value to append.
   * @return The extended string builder.
   */
  public SB append(CharSequence str) {
    sb.append(str);
    return this;
  }

  /**
   * Replaces a key by a numerical value.
   *
   * @param search The key to replace.
   * @param val The new value.
   * @return The extended string builder.
   */
  public SB replace(String search, int val) {
    return replace(search, val, false);
  }

  /**
   * Replaces a key by a numerical value.
   *
   * @param search The key to replace.
   * @param val The new value.
   * @param showNegativ True to show negative values, too, otherwise false to
   * replace the key with a non-breakable-space.
   * @return The extended string builder.
   */
  public SB replace(String search, int val, boolean showNegativ) {

    // int pos = sb.indexOf(search);
    String insert = val >= 0 || showNegativ ? "" + val : "&nbsp;";
    // sb.replace(pos, pos + search.length(), insert);
    replace(search, insert);

    return this;
  }

  /**
   * Replaces all occurences of a key by the given value.
   *
   * @param search The key to replace.
   * @param val The new value.
   * @return The extended string builder.
   */
  public SB replace(String search, String val) {

    try {
      int pos;
      do {
        pos = sb.indexOf(search);
        if (pos >= 0) {
          sb.replace(pos, pos + search.length(), val);
        }
      } while (pos >= 0);
    } catch (StringIndexOutOfBoundsException ex) {
      System.err.println(ex);
    }
    return this;
  }
  //</editor-fold>
}
