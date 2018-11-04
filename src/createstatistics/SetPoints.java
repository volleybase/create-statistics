package createstatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Additional actions of a set (timeout, substitution).
 *
 * @author gortonhu
 */
class Action {

  /**
   * The type of the action.
   */
  enum Type {
    TIMEOUT,
    SUBSTITUTION
  }

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

  /**
   * Creates an action.
   *
   * @param key The key created from scoring info.
   * @param type The type of the action.
   * @param teamA Team A or team B.
   */
  Action(String key, Type type, boolean teamA) {
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
  Action(String key, Type type, boolean teamA, String info) {
    this.key = key;
    this.type = type;
    this.teamA = teamA;
    this.info = info;
  }

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
    }
    sb.append(key).append(":").append(t);
    if (info != null) {
      sb.append(":").append(info);
    }

    return sb.toString();
  }
}

/**
 * The points of a set.
 *
 * @author gortonhu
 */
class SetPoints {

  final List<Integer> pointsA = new ArrayList<>();
  final List<Integer> pointsB = new ArrayList<>();
  boolean startA;
  final HashMap<String, List<Action>> actions = new HashMap<>();

  SetPoints(boolean startA) {
    this.startA = startA;
  }

  SetPoints addA(int points) {
    pointsA.add(points);
    return this;
  }

  SetPoints addB(int points) {
    pointsB.add(points);
    return this;
  }

  SetPoints add(String actioninfo) {
    String[] parts = actioninfo.split(":");
    if (parts.length >= 3) {
      String key = parts[0] + ":" + parts[1];

      switch (parts[2]) {

        case "T":
        case "t":
          if (parts.length != 3) {
            err(actioninfo);
          }
          add(new Action(key, Action.Type.TIMEOUT, parts[2].equals("T")));
          break;

        case "W":
        case "w":
          if (parts.length != 4) {
            err(actioninfo);
          }
          add(new Action(key, Action.Type.SUBSTITUTION, parts[2].equals("W"), parts[3]));
          break;

        default:
          err(actioninfo);
      }
    } else {
      err(actioninfo);
    }

    return this;
  }

  private void add(Action action) {

    // the list of action for the given key
    List<Action> acts = null;

    // action for this key already added: use this list
    if (this.actions.containsKey(action.key)) {
      acts = this.actions.get(action.key);
    } else {
      // else: create new list and add it
      acts = new ArrayList<>();
      this.actions.put(action.key, acts);
    }

    // add new action to list for current key
    acts.add(action);
  }

  private static void err(String actioninfo) {
    throw new IllegalArgumentException("Invalid action: " + actioninfo + "!");
  }

  /**
   * Returns the actions for a given scoring.
   *
   * @param a Points of team a.
   * @param b Points of team b.
   * @return The actions or null.
   */
  List<Action> actions(int a, int b) {

    String key = a + ":" + b;
    if (this.actions.containsKey(key)) {
      return this.actions.get(key);
    }

    return null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    // points team A
    if (startA) {
      sb.append("   ");
    }
    for (Integer pt : pointsA) {
      sb.append(fmt(pt)).append(" ");
    }

    // points team B
    sb.append(Str.NL);
    if (!startA) {
      sb.append("   ");
    }
    for (Integer pt : pointsB) {
      sb.append(fmt(pt)).append(" ");
    }

    // add optional actions
    if (!actions.isEmpty()) {
      for (Map.Entry<String, List<Action>> entry : actions.entrySet()) {
        sb.append(Str.NL).append(entry.getKey()).append(": ");
        boolean first = true;
        for (Action action : entry.getValue()) {
          if (first) {
            first = false;
          } else {
            sb.append(", ");
          }
          sb.append(action.toString());
        }
      }
    }

    return sb.toString();
  }

  private static String fmt(int nr) {
    String nr2 = "    " + nr;
    return nr2.substring(nr2.length() - 2);
  }
}
