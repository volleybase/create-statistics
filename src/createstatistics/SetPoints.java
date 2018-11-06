package createstatistics;

import java.util.ArrayList;
import java.util.List;

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
}

class XMap {

  private final List<String> keys = new ArrayList<>();
  private final List<Action> actions = new ArrayList<>();
  private int index = 0;

  void add(String key, Action action) {
    keys.add(key);
    actions.add(action);
  }

  boolean isEmpty() {
    return keys.isEmpty();
  }

  void reset() {
    index = 0;
  }

  Action next(String key) {

    // if action for key found: return it, then increase index
    if (index < keys.size() && keys.get(index).equals(key)) {
      return actions.get(index++);
    }

    // nothing found
    return null;
  }

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
  // final HashMap<String, List<Action>> actions = new HashMap<>();
  final XMap actions = new XMap();

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

      boolean found = false;
      String key = "";

      switch (parts[1]) {
        case "S":
        case "s":
          key = (parts[1].equals("S") ? "A:" : "B:") + parts[0];
          if (parts.length != 3) {
            err(actioninfo);
          }
          add(new Action(key, Action.Type.SERVICE, parts[1].equals("S"), parts[2]));
          found = true;
          break;
      }

      if (!found) {
        key = parts[0] + ":" + parts[1];

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
      }
    } else {
      err(actioninfo);
    }

    return this;
  }

  private void add(Action action) {

//    // the list of action for the given key
//    List<Action> acts = null;
//
//    // action for this key already added: use this list
//    if (this.actions.containsKey(action.key)) {
//      acts = this.actions.get(action.key);
//    } else {
//      // else: create new list and add it
//      acts = new ArrayList<>();
//      this.actions.put(action.key, acts);
//    }
//
//    // add new action to list for current key
//    acts.add(action);
    actions.add(action.key, action);
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

//    if (this.actions.containsKey(key)) {
//      return this.actions.get(key);
//    }
    List<Action> acts = new ArrayList<Action>();
    Action action;
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
//      for (Map.Entry<String, List<Action>> entry : actions.entrySet()) {
//        sb.append(Str.NL).append(entry.getKey()).append(": ");
//        boolean first = true;
//        for (Action action : entry.getValue()) {
//          if (first) {
//            first = false;
//          } else {
//            sb.append(", ");
//          }
//          sb.append(action.toString());
//        }
//      }
      sb.append(Str.NL).append(actions.toString());
    }

    return sb.toString();
  }

  private static String fmt(int nr) {
    String nr2 = "    " + nr;
    return nr2.substring(nr2.length() - 2);
  }
}
