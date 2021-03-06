package createstatistics.data;

import createstatistics.util.Str;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The infos of a set.
 *
 * @author volleybase
 */
public class SetInfo {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  // The number of the set.
  public final int nr;
  // the scoring of team A
  public final List<Integer> scoringsA = new ArrayList<>();
  // the scoring of team B
  public final List<Integer> scoringsB = new ArrayList<>();
  // true if team A starts with serving, otherwise false
  public boolean startA;
  // TODO combine startA and isAServing
//  // the actions store
//  final XMap actions = new XMap();
  public final Map<String, List<ActionInfo>> actions = new HashMap<>();
  // the line up of team A
  public ArrayList<String> lineUpA = null;
  public ArrayList<String> lineUpAOrig = null;
  // the line up of team B
  public ArrayList<String> lineUpB = null;
  public ArrayList<String> lineUpBOrig = null;
  // flag for decision set
  public boolean decision = false;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="The constructor.">
  /**
   * Creates the infos of a set.
   *
   * @param nr The number of the set (usually 1 - 5).
   * @param startA Should team A start with service?
   */
  public SetInfo(int nr, boolean startA) {
    this.nr = nr;
    this.startA = startA;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="The methods.">
  /**
   * Adds a point info for team A.
   *
   * @param points The point info.
   * @return The points info itself.
   */
  public SetInfo addA(int points) {
    scoringsA.add(points);
    return this;
  }

  /**
   * Adds a point info for team B.
   *
   * @param points The point info.
   * @return The points info itself.
   */
  public SetInfo addB(int points) {
    scoringsB.add(points);
    return this;
  }

  public int maxPt() {
    return Math.max(
      scoringsA.get(scoringsA.size() - 1),
      scoringsB.get(scoringsB.size() - 1));
  }

  public int length() {
    return scoringsA.size() + scoringsB.size() - 1;
  }

  public boolean isAServing() {
    return scoringsA.size() > 0 && scoringsA.get(0) >= 0;
  }

  /**
   * Adds an additional action info.
   *
   * @param actioninfo The action to add.
   * @return The points info itself.
   */
  public SetInfo add(String actioninfo) {
    String[] parts = actioninfo.split(":");
    if (parts.length == 1) {

      // first part might be a key
      switch (parts[0]) {

        // check for decision set
        case "D":
          decision = true;
          break;

        default:
          err(actioninfo);
      }

    } else if (parts.length >= 3) {

      boolean found = false;
      String key;

      // first part might be a key
      switch (parts[0]) {

        // line up
        case "L":
        case "l":
          if (parts.length != 7) {
            err(actioninfo);
          }
          ArrayList<String> lineUp = new ArrayList<>();
          ArrayList<String> lineUpOrig = new ArrayList<>();
          for (int p = 1; p < parts.length; ++p) {
            lineUp.add(parts[p]);
            lineUpOrig.add(parts[p]);
          }
          if (parts[0].charAt(0) == 'L') {
            lineUpA = lineUp;
            lineUpAOrig = lineUpOrig;
          } else {
            lineUpB = lineUp;
            lineUpBOrig = lineUpOrig;
          }
          found = true;
          break;
      }

// old service - replaced by detecting from scorings
//      // look into 2nd part
//      if (!found) {
//        switch (parts[1]) {
//
//          // Service
//          case "S":
//          case "s":
//            key = (parts[1].equals("S") ? "A:" : "B:") + parts[0];
//            if (parts.length != 3) {
//              err(actioninfo);
//            }
//            if (key.charAt(0) == 'A' && lineUpA != null) {
//              throw new IllegalArgumentException("Do not mix lineup and service for team A!");
//            }
//            if (key.charAt(0) == 'B' && lineUpB != null) {
//              throw new IllegalArgumentException("Do not mix lineup and service for team B!");
//            }
//            add(new SetAction(key, SetAction.Type.SERVICE, parts[1].equals("S"), parts[2]));
//            found = true;
//            break;
//        }
//      }
//
      // still not found: look into 3rd part
      if (!found) {
        // change scoring for timeout or substitution of team b
        key = parts[2].equals("t") || parts[2].equals("w")
          ? (actKey(parts[1]) + ":" + actKey(parts[0]))
          : (actKey(parts[0]) + ":" + actKey(parts[1]));
        switch (parts[2]) {

          case "T":
          case "t":
            if (parts.length != 3) {
              err(actioninfo);
            }
            // add(new SetAction(key, SetAction.Type.TIMEOUT, parts[2].equals("T")));
            addAction(key, ActionInfo.Type.TIMEOUT, parts[2].equals("T"), "TO");
            break;

          case "W":
          case "w":
            if (parts.length != 4) {
              err(actioninfo);
            }
            //add(new SetAction(key, SetAction.Type.SUBSTITUTION, parts[2].equals("W"), parts[3]));
            addAction(key, ActionInfo.Type.SUBSTITUTION, parts[2].equals("W"), parts[3]);
            break;

          case "End":
            if (parts.length != 3) {
              err(actioninfo);
            }
            // add(new SetAction(key, SetAction.Type.END_OF_SET, true));
            addAction(key, ActionInfo.Type.END_OF_SET, true, null);
            break;

          default:
            err(actioninfo);
        }
      }
    } else {
      err(actioninfo);
    }

    return this;
  }

  private String actKey(String part) {
    return String.format("%3s", part).replace(" ", "0");
  }

  private String actKey(int score) {
    return String.format("%03d", score);
  }

  private void addAction(String key, ActionInfo.Type type, boolean teamA, String info) {
    ActionInfo ai = new ActionInfo();
    ai.type = type;
    ai.teamA = teamA;
    ai.info = info;

    if (actions.containsKey(key)) {
      actions.get(key).add(ai);
    } else {
      List<ActionInfo> infos = new ArrayList<>();
      infos.add(ai);
      actions.put(key, infos);
    }
  }

  /**
   * Returns the actions for a given scoring.
   *
   * @param a Points of team a.
   * @param b Points of team b.
   * @return The actions or null.
   */
  public List<ActionInfo> actions(int a, int b) {
    String key = String.format("%s:%s", actKey(a), actKey(b));
    return actions.get(key);
  }
//  List<SetAction> actions(int a, int b) {
//
//    List<SetAction> acts = new ArrayList<>();
//
//    String key = a + ":" + b;
//    List<SetAction> acts0 = actions.get(key);
//    if (acts0 != null) {
//      acts.addAll(acts0);
//    }
//
//    key = "A:" + a;
//    acts0 = actions.get(key);
//    if (acts0 != null) {
//      acts.addAll(acts0);
//    }
//
//    key = "B:" + b;
//    acts0 = actions.get(key);
//    if (acts0 != null) {
//      acts.addAll(acts0);
//    }
//
//    return acts.size() > 0 ? acts : null;
//  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Internal helper functions.">
//  /**
//   * The internal function to add the created action.
//   *
//   * @param action The action to add.
//   */
//  private void add(SetAction action) {
//    actions.add(action.key, action);
//  }
  /**
   * Throw an error if invalid action data found.
   *
   * @param actioninfo The invalid action info.
   */
  private static void err(String actioninfo) {
    throw new IllegalArgumentException("Invalid action: " + actioninfo + "!");
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Debug.">
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Satz: ").append(nr).append(Str.NL);

    if (lineUpA != null) {

      // team A
      boolean first = true;
      sb.append("A: ");
      for (String l : lineUpA) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append(l);
      }
      sb.append(Str.NL);

      // team A
      first = true;
      sb.append("B: ");
      for (String l : lineUpB) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append(l);
      }
      sb.append(Str.NL);
    }

    // points team B
    scoringsA.forEach((pt) -> {
      sb.append(fmt(pt)).append(" ");
    });

    // points team B
    sb.append(Str.NL);
    scoringsB.forEach((pt) -> {
      sb.append(fmt(pt)).append(" ");
    });

    // add optional actions
    if (!actions.isEmpty()) {
      sb.append(Str.NL).append(actions.toString());
    }

    // return result
    return sb.toString();
  }

  /**
   * Formats the points for text output - pad them with leading spaces.
   *
   * @param nr The number to format.
   * @return
   */
  private static String fmt(int nr) {
    if (nr < 0) {
      return "  ";
    }
    String nr2 = "    " + nr;
    return nr2.substring(nr2.length() - 2);
  }
  //</editor-fold>

  public void resetLineup() {
    lineUpA.clear();
    lineUpAOrig.forEach((player) -> {
      lineUpA.add(player);
    });
    lineUpB.clear();
    lineUpBOrig.forEach((player) -> {
      lineUpB.add(player);
    });
  }

  public void checkForAction(int pS, int pR, float position) {
    String key = isAServing()
      ? String.format("%03d:%03d", pS, pR)
      : String.format("%03d:%03d", pR, pS);
    List<ActionInfo> infos = actions.get(key);
    if (infos != null) {
      infos.forEach((ai) -> {
        ai.position = position;
      });
    }
  }

  /**
   * Checks for replacing a player.
   *
   * @param pS The current score of the serving team.
   * @param pR The current score of the receiving team.
   * @param teamS True for serving team, otherwise false.
   * @param player The player to check.
   * @return The current player or the new one.
   */
  public String checkForReplacement(int pS, int pR, boolean teamA, String player) {
    String pl0 = player;

    // handle another replacement during same serving, too
    String[] partsPl = player.split("/");
    String plTest = partsPl[partsPl.length - 1];
    String key = String.format("%03d:%03d", pS, pR);

    // get actions for current scoring
    List<ActionInfo> infos = actions.get(key);
    if (infos != null) {
      for (ActionInfo ai : infos) {
        // check for replacement
        if (ai.type == ActionInfo.Type.SUBSTITUTION && ai.teamA == teamA) {
          String[] parts = ai.info.split("/");
          // check player
          if (parts[1].equals(plTest)) {
            // found: take replacing player
            pl0 = parts[0];
          }
        }
      }
    }

    // result
    return pl0;
  }
}
