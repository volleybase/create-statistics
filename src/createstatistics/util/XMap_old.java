//package createstatistics.util;
//
//import createstatistics.SetAction;
//import createstatistics.Str;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Set;
//
///**
// * A map.
// *
// * @author volleybase
// */
//public class XMap {
//
//  //<editor-fold defaultstate="collapsed" desc="The fields.">
//  private final HashMap<String, List<SetAction>> data = new HashMap<>();
//  //</editor-fold>
//
//  //<editor-fold defaultstate="collapsed" desc="The methods.">
//  /**
//   * Adds a an action.
//   *
//   * @param key The key of the action.
//   * @param action The action.
//   */
//  public void add(String key, SetAction action) {
//    List<SetAction> acts;
//
//    if (data.containsKey(key)) {
//      acts = data.get(key);
//    } else {
//      acts = new ArrayList<>();
//      data.put(key, acts);
//    }
//
//    acts.add(action);
//  }
//
//  /**
//   * Is the actions' store empty?
//   *
//   * @return True if empty, otherwise false.
//   */
//  public boolean isEmpty() {
//    return data.isEmpty();
//  }
//
//  /**
//   * Returns the actions for the given key.
//   *
//   * @param key The key of the next action to handle.
//   * @return The actions, or null if there is not any action for a given key.
//   */
//  public List<SetAction> get(String key) {
//
//    if (data.containsKey(key)) {
//      return Collections.unmodifiableList(data.get(key));
//    }
//
//    // nothing found
//    return null;
//  }
//
//  /**
//   * Returns the keys.
//   *
//   * @return The keys.
//   */
//  public Set<String> keys() {
//    return Collections.unmodifiableSet(data.keySet());
//  }
//  //</editor-fold>
//
//  //<editor-fold defaultstate="collapsed" desc="Debug.">
//  @Override
//  public String toString() {
//    StringBuilder sb = new StringBuilder();
//    ArrayList<String> keys = new ArrayList<>(data.keySet());
//    Collections.sort(keys);
//
//    for (int i = 0; i < keys.size(); ++i) {
//      if (i > 0) {
//        sb.append(Str.NL);
//      }
//      sb.append(keys.get(i)).append(": ");
//
//      List<SetAction> acts = data.get(keys.get(i));
//      boolean first = true;
//      for (SetAction act : acts) {
//        if (first) {
//          first = false;
//        } else {
//          sb.append("; ");
//        }
//        sb.append(act);
//      }
//    }
//
//    return sb.toString();
//  }
//  //</editor-fold>
//}
