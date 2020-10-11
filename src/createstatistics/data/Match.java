package createstatistics.data;

import createstatistics.util.Str;
import java.util.ArrayList;
import java.util.List;

/**
 * The info of a match.
 *
 * @author volleybase
 */
public class Match {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  /**
   * The index of the match.
   */
  public int index;
  /**
   * The date info.
   */
  public String date;

  /**
   * The info.
   */
  public String info;

  /**
   * The list of players.
   */
  public List<Player> players = new ArrayList<>();

  /**
   * The infos of the sets.
   */
  public List<SetInfo> setInfos = new ArrayList<>();
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Setters and getters.">
  /**
   * Adds a set info.
   *
   * @param set The set info to add.
   */
  public void add(SetInfo set) {
    setInfos.add(set);
  }

  /**
   * Checks it the match has at least one set info.
   *
   * @return True if any set info has been stored, otherwise false.
   */
  public boolean hasSet() {
    return setInfos.size() > 0;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Debug.">
  @Override
  public String toString() {

    // output buffer
    StringBuilder sb = new StringBuilder();

    // main info
    sb.append(Str.str(date, 10)).append(" ").append(info);

    // add players
    if (players.isEmpty()) {
      sb.append(Str.NL).append("Not any player...");
    }
    for (Player player : players) {
      sb.append(Str.NL).append(player.toString());
    }

    // add sets
    for (SetInfo set : setInfos) {
      sb.append(Str.NL).append(set.toString());
    }

    // done
    return sb.toString();
  }
  //</editor-fold>
}
