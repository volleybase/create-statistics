package createstatistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A utility class to store the actions of a set.
 *
 * @author volleybase
 */
class XMap {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  private final HashMap<String, List<SetAction>> data = new HashMap<>();
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="The methods.">
  /**
   * Adds a an action.
   *
   * @param key The key of the action.
   * @param action The action.
   */
  void add(String key, SetAction action) {
    List<SetAction> acts;

    if (data.containsKey(key)) {
      acts = data.get(key);
    } else {
      acts = new ArrayList<>();
      data.put(key, acts);
    }

    acts.add(action);
  }

  /**
   * Is the actions' store empty?
   *
   * @return True if empty, otherwise false.
   */
  boolean isEmpty() {
    return data.isEmpty();
  }

  /**
   * Returns the actions for the given key.
   *
   * @param key The key of the next action to handle.
   * @return The actions, or null if there is not any action for a given key.
   */
  List<SetAction> get(String key) {

    if (data.containsKey(key)) {
      return Collections.unmodifiableList(data.get(key));
    }

    // nothing found
    return null;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Debug.">
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    ArrayList<String> keys = new ArrayList<>(data.keySet());
    Collections.sort(keys);

    for (int i = 0; i < keys.size(); ++i) {
      if (i > 0) {
        sb.append(Str.NL);
      }
      sb.append(keys.get(i)).append(": ");

      List<SetAction> acts = data.get(keys.get(i));
      boolean first = true;
      for (SetAction act : acts) {
        if (first) {
          first = false;
        } else {
          sb.append("; ");
        }
        sb.append(act);
      }
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
  // The number of the set.
  final int nr;
  // the scoring of team A
  final List<Integer> scoringsA = new ArrayList<>();
  // the scoring of team B
  final List<Integer> scoringsB = new ArrayList<>();
  // true if team A starts with serving, otherwise false
  boolean startA;
  // the actions store
  final XMap actions = new XMap();
  // the line up of team A
  ArrayList<String> lineUpA = null;
  // the line up of team A
  ArrayList<String> lineUpB = null;
  // flag for decision set
  boolean decision = false;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="The constructor.">
  /**
   * Creates the infos of a set.
   *
   * @param nr The number of the set (usually 1 - 5).
   * @param startA Should team A start with service?
   */
  SetInfo(int nr, boolean startA) {
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
  SetInfo addA(int points) {
    scoringsA.add(points);
    return this;
  }

  /**
   * Adds a point info for team B.
   *
   * @param points The point info.
   * @return The points info itself.
   */
  SetInfo addB(int points) {
    scoringsB.add(points);
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
    if (parts.length == 1) {

      // first part might be a key
      switch (parts[0]) {
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

          case "End":
            if (parts.length != 3) {
              err(actioninfo);
            }
            add(new SetAction(key, SetAction.Type.END_OF_SET, true));
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

    String key = a + ":" + b;
    List<SetAction> acts0 = actions.get(key);
    if (acts0 != null) {
      acts.addAll(acts0);
    }

    key = "A:" + a;
    acts0 = actions.get(key);
    if (acts0 != null) {
      acts.addAll(acts0);
    }

    key = "B:" + b;
    acts0 = actions.get(key);
    if (acts0 != null) {
      acts.addAll(acts0);
    }

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
    for (Integer pt : scoringsA) {
      sb.append(fmt(pt)).append(" ");
    }

    // points team B
    sb.append(Str.NL);
    for (Integer pt : scoringsB) {
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
