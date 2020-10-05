package svg.pps;

/**
 * An action to display in the diagram.
 *
 * @author volleybase
 */
public class ActionInfo {

  //<editor-fold defaultstate="collapsed" desc="The type of an action.">
  /**
   * The type of the action.
   */
  public enum Type {

    /**
     * To force end of set (e.g: tournament games might end at 25).
     */
    END_OF_SET,
    //    /**
    //     * L, l: line up/Aufstellung
    //     */
    //    LINEUP,
    /**
     * T, t: time out
     */
    TIMEOUT,
    /**
     * W, w: substitution/Wechsel
     */
    SUBSTITUTION
//    ,
//    /**
//     * S, s: Service
//     */
//    SERVICE
  }
  //</editor-fold>

  public Type type;
  public boolean teamA;
  public String info;
  public float position;

  @Override
  public String toString() {
    return String.format("%s: %s, %s", type, teamA ? "A" : "B", info);
  }
}
