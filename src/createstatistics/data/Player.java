package createstatistics.data;

import createstatistics.util.Str;

/**
 * The info of a player.
 *
 * @author volleybase
 */
public class Player {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  /**
   * The type of the player.
   */
  public String type;

  /**
   * The name of the player.
   */
  public String name;

  /**
   * The points of set 1 (-1 if not played).
   */
  public int p1;

  /**
   * The failures of set 1 (-1 if not played).
   */
  public int m1;

  /**
   * The points of set 2 (-1 if not played).
   */
  public int p2;

  /**
   * The failures of set 2 (-1 if not played).
   */
  public int m2;

  /**
   * The points of set 3 (-1 if not played).
   */
  public int p3;

  /**
   * The failures of set 31 (-1 if not played).
   */
  public int m3;

  /**
   * The points of set 4 (-1 if not played).
   */
  public int p4;

  /**
   * The failures of set 4 (-1 if not played).
   */
  public int m4;

  /**
   * The points of set 5 (-1 if not played).
   */
  public int p5;

  /**
   * The failures of set 5 (-1 if not played).
   */
  public int m5;

  /**
   * The sum of the points.
   */
  public String pSum;

  /**
   * The sum of the failures.
   */
  public String mSum;

  /**
   * The points minus the failures.
   */
  public String diff;

  /**
   * The quotient of points and failures.
   */
  public String quot;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Debug.">
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
  //</editor-fold>
}
