package createstatistics;

/**
 * Additional actions of a set (timeout, substitution, ...).
 *
 * @author volleybase
 */
class SetAction {

  //<editor-fold defaultstate="collapsed" desc="The type of an action.">
  /**
   * The type of the action.
   */
  enum Type {

    /**
     * To force end of set (e.g: tournament games might end at 25).
     */
    END_OF_SET,
    /**
     * L, l: line up/Aufstellung
     */
    LINEUP,
    /**
     * T, t: time out
     */
    TIMEOUT,
    /**
     * W, w: substitution/Wechsel
     */
    SUBSTITUTION,
    /**
     * S, s: Service
     */
    SERVICE
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  /**
   * The key - the current scoring info.
   */
  String key;

  /**
   * The type of the action.
   */
  Type type;

  /**
   * Team A or team B.
   */
  boolean teamA;

  /**
   * Additional info.
   */
  String info;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="The constructors.">
  /**
   * Creates an action.
   *
   * @param key The key created from scoring info.
   * @param type The type of the action.
   * @param teamA Team A or team B.
   */
  SetAction(String key, Type type, boolean teamA) {
    this(key, type, teamA, null);
  }

  /**
   * Creates an action.
   *
   * @param key The key created from scoring info.
   * @param type The type of the action.
   * @param teamA Team A or team B.
   * @param info The additional action info.
   */
  SetAction(String key, Type type, boolean teamA, String info) {
    this.key = key;
    this.type = type;
    this.teamA = teamA;
    this.info = info;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Debug.">
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    String t = "?";
    switch (type) {
      case TIMEOUT:
        t = teamA ? "T" : "t";
        break;
      case SUBSTITUTION:
        t = teamA ? "W" : "w";
        break;
      case SERVICE:
        t = teamA ? "S" : "s";
        break;
    }
    sb.append(key).append(":").append(t);
    if (info != null) {
      sb.append(":").append(info);
    }

    return sb.toString();
  }
  //</editor-fold>
}
