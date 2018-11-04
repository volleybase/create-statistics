package createstatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * A match's info.
 *
 * @author gortonhu
 */
/**
 * The info of a match.
 *
 * @author gortonhu
 */
class Match {

  String date;
  String info;
  List<Player> players = new ArrayList<>();
  List<SetPoints> points = new ArrayList<>();

  void addPoints(SetPoints sp) {
    points.add(sp);
  }

  boolean hasPoints() {
    return points.size() > 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(Str.str(date, 10)).append(" ").append(info);

    for (Player player : players) {
      sb.append(Str.NL).append(player.toString());
    }

    for (SetPoints sp : points) {
      sb.append(Str.NL).append(sp.toString());
    }

    return sb.toString();
  }
}
