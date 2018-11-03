package createstatistics;

/**
 * A players info.
 *
 * @author gortonhu
 */
/**
 * The info of a player.
 *
 * @author gortonhu
 */
class Player {

  String type;
  String name;
  int p1;
  int m1;
  int p2;
  int m2;
  int p3;
  int m3;
  int p4;
  int m4;
  int p5;
  int m5;
  String pSum;
  String mSum;
  String diff;
  String quot;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(Str.str(name, 10))
      .append(Str.nnn(p1)).append(Str.nnn(m1))
      .append(Str.nnn(p2)).append(Str.nnn(m2))
      .append(Str.nnn(p3)).append(Str.nnn(m3))
      .append(Str.nnn(p4)).append(Str.nnn(m4))
      .append(Str.nnn(p5)).append(Str.nnn(m5))
      .append(Str.str(pSum, 3)).append(Str.str(mSum, 3))
      .append(Str.str(diff, 5)).append(Str.str(quot, 6));

    return sb.toString();
  }
}
