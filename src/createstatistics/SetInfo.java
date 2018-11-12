package createstatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to store the actions of a set.
 *
 * @author volleybase
 */
class XMap {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  // the keys
  private final List<String> keys = new ArrayList<>();
  // the actions
  private final List<SetAction> actions = new ArrayList<>();
  // the index of the current item on iterating through the content
  private int index = 0;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="The methods.">
  /**
   * Adds a an action.
   *
   * @param key The key of the action.
   * @param action The action.
   */
  void add(String key, SetAction action) {
    keys.add(key);
    actions.add(action);
  }

  /**
   * Is the actions' store empty?
   *
   * @return True if empty, otherwise false.
   */
  boolean isEmpty() {
    return keys.isEmpty();
  }

  /**
   * Resets the iteration index.
   */
  void reset() {
    index = 0;
  }

  /**
   * Returns the next action if the key fits.
   *
   * @param key The key of the next action to handle.
   * @return The next action, or null if there is not any action for a given
   * key.
   */
  SetAction next(String key) {

    // if action for key found: return it, then increase index
    if (index < keys.size() && keys.get(index).equals(key)) {
      return actions.get(index++);
    }

    // nothing found
    return null;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Debug.">
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < keys.size(); ++i) {
      if (i > 0) {
        sb.append(Str.NL);
      }
      sb.append(keys.get(i)).append(": ").append(actions.get(i));
    }

    return sb.toString();
  }
  //</editor-fold>
}

/**
 * The infos of a set.
 *
 * @author volleybase
 */
class SetInfo {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  // the points of team A
  final List<Integer> pointsA = new ArrayList<>();
  // the points of team B
  final List<Integer> pointsB = new ArrayList<>();
  // true if team A starts with serving, otherwise false
  boolean startA;
  // the actions store
  final XMap actions = new XMap();
  // the line up of team A
  ArrayList<String> lineUpA = null;
  // the line up of team A
  ArrayList<String> lineUpB = null;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="The constructor.">
  /**
   * Creates the infos of a set.
   *
   * @param startA Should team A start with service?
   */
  SetInfo(boolean startA) {
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
  SetInfo addA(int points) {
    pointsA.add(points);
    return this;
  }

  /**
   * Adds a point info for team B.
   *
   * @param points The point info.
   * @return The points info itself.
   */
  SetInfo addB(int points) {
    pointsB.add(points);
    return this;
  }

  /**
   * Adds an additional action info.
   *
   * @param actioninfo The action to add.
   * @return The points info itself.
   */
  SetInfo add(String actioninfo) {
    String[] parts = actioninfo.split(":");
    if (parts.length >= 3) {

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
          // List<String> lineUp = Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length));
          ArrayList<String> lineUp = new ArrayList<>();
          for (int p = 1; p < parts.length; ++p) {
            lineUp.add(parts[p]);
          }
          if (parts[0].charAt(0) == 'L') {
            lineUpA = lineUp;
          } else {
            lineUpB = lineUp;
          }
          found = true;
          break;
      }

      // look into 2nd part
      if (!found) {
        switch (parts[1]) {

          // Service
          case "S":
          case "s":
            key = (parts[1].equals("S") ? "A:" : "B:") + parts[0];
            if (parts.length != 3) {
              err(actioninfo);
            }
            if (key.charAt(0) == 'A' && lineUpA != null) {
              throw new IllegalArgumentException("Do not mix lineup and service for team A!");
            }
            if (key.charAt(0) == 'B' && lineUpB != null) {
              throw new IllegalArgumentException("Do not mix lineup and service for team B!");
            }
            add(new SetAction(key, SetAction.Type.SERVICE, parts[1].equals("S"), parts[2]));
            found = true;
            break;
        }
      }

      // still not found: look into 3rd part
      if (!found) {
        key = parts[0] + ":" + parts[1];

        switch (parts[2]) {

          case "T":
          case "t":
            if (parts.length != 3) {
              err(actioninfo);
            }
            add(new SetAction(key, SetAction.Type.TIMEOUT, parts[2].equals("T")));
            break;

          case "W":
          case "w":
            if (parts.length != 4) {
              err(actioninfo);
            }
            add(new SetAction(key, SetAction.Type.SUBSTITUTION, parts[2].equals("W"), parts[3]));
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

  /**
   * Returns the actions for a given scoring.
   *
   * @param a Points of team a.
   * @param b Points of team b.
   * @return The actions or null.
   */
  List<SetAction> actions(int a, int b) {

    List<SetAction> acts = new ArrayList<>();
    SetAction action;
    do {
      String key = a + ":" + b;
      action = actions.next(key);
      if (action == null) {
        key = "A:" + a;
        action = actions.next(key);

        if (action == null) {
          key = "B:" + b;
          action = actions.next(key);
        }
      }

      if (action != null) {
        acts.add(action);
      }
    } while (action != null);

    return acts.size() > 0 ? acts : null;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Internal helper functions.">
  /**
   * The internal function to add the created action.
   *
   * @param action The action to add.
   */
  private void add(SetAction action) {
    actions.add(action.key, action);
  }

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
    for (Integer pt : pointsA) {
      sb.append(fmt(pt)).append(" ");
    }

    // points team B
    sb.append(Str.NL);
    for (Integer pt : pointsB) {
      sb.append(fmt(pt)).append(" ");
    }

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
}
