package createstatistics;

//<editor-fold defaultstate="collapsed" desc="The imports.">
import createstatistics.data.Match;
import createstatistics.data.SetInfo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import svg.diagram.DiagramBase;
import svg.diagram.DiagramDiff;
import svg.diagram.DiagramPps;

//<editor-fold defaultstate="collapsed" desc="Action filter tool - not used.">
//class ActionFilter {
//
//  //<editor-fold defaultstate="collapsed" desc="The fields.">
//  private String keyA;
//  private String keyB;
//  private final boolean _isAServing;
//  //private List<String> actions;
//  private final XMap actions;
//  private final List<String> _lineupS;
//  private final List<String> _lineupR;
//  Integer[] pS;
//  Integer[] pR;
//  List<ActionInfo> infos = new ArrayList<>();
//  //</editor-fold>
//
//  //<editor-fold defaultstate="collapsed" desc="Constructors.">
////  public ActionFilter(String[] actions, Integer[] pa, Integer[] pb) {
////    this(Arrays.asList(actions), pa, pb);
////  }
//  public ActionFilter(List<String> actions, Integer[] pa, Integer[] pb) {
//    // public ActionFilter(XMap actions, Integer[] pa, Integer[] pb) {
//    this.actions = actions;
//    this._isAServing = pa[0] >= 0;
//
//    pS = _isAServing ? pa : pb;
//    pR = _isAServing ? pb : pa;
//
//    _lineupS = lineup(_isAServing ? "L:" : "l:");
//    _lineupR = lineup(_isAServing ? "l:" : "L:");
//  }
//  //</editor-fold>
//
//  //<editor-fold defaultstate="collapsed" desc="Which team is the serving team?">
//  /**
//   * Is team A the serving team?
//   *
//   * @return True if team A is the serving team, otherwise false.
//   */
//  public boolean isAServing() {
//    return _isAServing;
//  }
//  //</editor-fold>
//
//  //<editor-fold defaultstate="collapsed" desc="Lineup.">
//  public List<String> lineupS() {
//    return _lineupS;
//  }
//
//  public List<String> lineupR() {
//    return _lineupR;
//  }
//
//  private List<String> lineup(String key) {
//    for (String act : actions.keys()) {
//      if (act.startsWith(key)) {
//        String[] parts = act.split(":");
//        if (parts.length > 1) {
//          List<String> lineup = new ArrayList<>();
//          for (int i = 1; i < parts.length; ++i) {
//            lineup.add(parts[i]);
//          }
//          return lineup;
//        }
//      }
//    }
//    return null;
//  }
//
//  /**
//   * Checks for replacing a player.
//   *
//   * @param pS The current score of the serving team.
//   * @param pR The current score of the receiving team.
//   * @param teamS Check the serving team or the receiving one.
//   * @param player The player to check.
//   * @return The current player or the new one.
//   */
//  public String checkForReplacement(int pS, int pR, boolean teamS, String player) {
//    String pl0 = player;
//    // handle another replacement during same serving, too
//    String[] partsPl = player.split("/");
//    String plTest = partsPl[partsPl.length - 1];
//    String change = teamS ^ _isAServing ? "w" : "W";
//    String key = teamS
//      ? String.format("%d:%d:", pS, pR) : String.format("%d:%d:", pR, pS);
//    // handle each action
//    for (String act : actions) {
//      // scoring
//      if (act.startsWith(key)) {
//        String[] parts = act.split(":");
//        // check for replacement
//        if (parts.length == 4 && parts[2].equals(change)) {
//          parts = parts[3].split("/");
//          // check player
//          if (parts.length == 2 && parts[1].equals(plTest)) {
//            // found: take replacing player
//            pl0 = parts[0];
//          }
//        }
//      }
//    }
//
//    // result
//    return pl0;
//  }
//  //</editor-fold>
//
//  //<editor-fold defaultstate="collapsed" desc="Actions.">
//  public void checkForAction(int pS, int pR, boolean teamS, float position) {
//    String change = teamS ^ _isAServing ? "w" : "W";
//    String to = teamS ^ _isAServing ? "t" : "T";
//    String key = teamS
//      ? String.format("%d:%d:", pS, pR) : String.format("%d:%d:", pR, pS);
//
//    // handle each action
//    for (String act : actions) {
//      // scoring
//      if (act.startsWith(key)) {
//        String[] parts = act.split(":");
//
//        // check for replacement
//        if (parts.length == 4 && parts[2].equals(change)) {
//          ActionInfo ai = new ActionInfo();
//          ai.position = position;
//          ai.teamA = !(teamS ^ _isAServing);
//          ai.info = parts[3];
//
//          infos.add(ai);
//          System.out.format("action %s: %s %5.3f %s%n", change, ai.info,
//            ai.position, ai.teamA ? "A" : "B");
//
//          // else: check for timeout
//        } else if (parts.length == 3 && parts[2].equals(to)) {
//          ActionInfo ai = new ActionInfo();
//          ai.position = position;
//          ai.teamA = !(teamS ^ _isAServing);
//          ai.info = "TO";
//
//          infos.add(ai);
//          System.out.format("action %s: %5.3f %s%n", to,
//            ai.position, ai.teamA ? "A" : "B");
//        }
//      }
//    }
//  }
//  //</editor-fold>
//}
//</editor-fold>
//
/**
 * The generator of the diagrams.
 *
 * @author volleybase
 */
public class GeneratorDiagram {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  // the directory containing the diagrams
  static final String DIR_DIA_WORK = "D:\\workdir\\work\\diagram\\";
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Points-per-service diagram.">
  /**
   * Creates the points-per-service diagrams for the given match.
   *
   * @param match The match data.
   * @param key The main key.
   * @throws Exception
   */
  static void createPointsPerService(Match match, String key) throws Exception {

    // get max y of match
    int maxY = -1;
    for (SetInfo si : match.setInfos) {
      maxY = Math.max(maxY, si.scoringsA.get(si.scoringsA.size() - 1));
      maxY = Math.max(maxY, si.scoringsB.get(si.scoringsB.size() - 1));
    }

    // create diagram for each set
    for (SetInfo si : match.setInfos) {

      // prepare xaxis(areas, names) and actions
      si.resetLineup();
      List<Integer> scoringsA = si.isAServing() ? si.scoringsA : si.scoringsB,
        scoringsB = si.isAServing() ? si.scoringsB : si.scoringsA;
      List<String> lineupA = si.isAServing() ? si.lineUpAOrig : si.lineUpBOrig,
        lineupB = si.isAServing() ? si.lineUpBOrig : si.lineUpAOrig;
      List<String> namesA = null, namesB = null;

      int lastS = 0, curS, lastR = 0, curR;
      int idx = -1,
        maxS = scoringsA.size(), maxR = scoringsB.size(),
        max = Math.max(maxS, maxR);
      float positionBase = -1f;
      si.checkForAction(0, 0, 0f);

      while (++idx < max) {

        // handle team reception (ignore first -1)
        if (idx > 0 && idx < maxR) {
          ++positionBase;

          int idxLR = -1;
          if (lineupB != null) {
            if (namesB == null) {
              namesB = new ArrayList<>();
            }
            // check for replacement of serving player
            idxLR = idx % lineupB.size();
            String player = lineupB.get(idxLR);
            String player2 = si.checkForReplacement(lastS, lastR, false, player);
            namesB.add(player2);
            if (!player2.equals(player)) {
              lineupB.set(idxLR, player2);
            }
          }

          boolean first = true;
          curR = scoringsB.get(idx); // filter.pR[idx];
          int lastR0 = lastR + 1;
          while (curR > lastR) {
            ++lastR;
            float pos = curR - lastR0 == 0 ? 0f : (lastR - lastR0) / (float) (curR - lastR0);
            si.checkForAction(lastS, lastR, positionBase + pos);

            // checks for replacement of serving player
            if (namesB != null && lineupB != null) {
              int idxLast = namesB.size() - 1;
              String player = namesB.get(idxLast);
              String player2 = si.checkForReplacement(lastS, lastR, false, player);
              if (!player.equals(player2)) {
                if (first) {
                  namesB.set(idxLast, player2);
                } else {
                  namesB.set(idxLast, player + "/" + player2);
                }
                lineupB.set(idxLR, player2);
              }
            }
            first = false;
          }
        }

        // handle team service
        if (idx < maxS) {
          ++positionBase;

          int idxLS = -1;
          if (lineupA != null) {
            if (namesA == null) {
              namesA = new ArrayList<>();
            }
            // check for replacement of serving player
            idxLS = idx % lineupA.size();
            String player = lineupA.get(idxLS);
            String player2 = si.checkForReplacement(lastS, lastR, true, player);
            namesA.add(player2);
            if (!player2.equals(player)) {
              lineupA.set(idxLS, player2);
            }
          }

          boolean first = true;
          curS = scoringsA.get(idx);
          int lastS0 = lastS + 1;
          while (curS > lastS) {
            ++lastS;
            if (namesA != null && lineupA != null) {
              int idxLast = namesA.size() - 1;
              String player = namesA.get(idxLast);
              String player2 = si.checkForReplacement(lastS, lastR, true, player);
              if (!player.equals(player2)) {
                if (first) {
                  namesA.set(idxLast, player2);
                } else {
                  namesA.set(idxLast, player + "/" + player2);
                }
                lineupA.set(idxLS, player2);
              }
            }

            float pos = curS - lastS0 == 0 ? 0f : (lastS - lastS0) / (float) (curS - lastS0);
            si.checkForAction(lastS, lastR, positionBase + pos);

            first = false;
          }
        }
      }

      // create a diagram
      String keyDia = GeneratorDiagram.filenamePpS(key, match.index, si.nr);
      // createDia(si, maxY, namesA, namesB, keyDia);
      DiagramPps dia = new DiagramPps(si, maxY, namesA, namesB);
      output(dia, keyDia);
    }
  }

//  private static void createDia(SetInfo si, int maxY,
//    List<String> namesA, List<String> namesB, String key) throws Exception {
//
//    Diagram dia = new Diagram(si, maxY, namesA, namesB);
//    output(dia, key);
//  }
  /**
   * Writes the resulting svg file.
   *
   * @param dia The diagram to write.
   * @param key The key of the league.
   * @param game The index of the game.
   * @param set The number of the set.
   * @throws Exception
   */
  private static void output(DiagramBase dia, String key) throws Exception {
    String svg = "<?xml version=\"1.0\" standalone=\"no\"?>\n" + dia.svg();
    String filename = String.format("%s%s", DIR_DIA_WORK, key);

    // write resulting file
    File file = new File(filename);
    // the charset for writing files
    Charset charset = Charset.forName("UTF-8");
    try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), charset)) {
      writer.write(svg, 0, svg.length());
    } catch (IOException x) {
      System.err.format("IOException: %s%n", x);
    }

    System.out.format("Written %s%n", filename);
  }
  //</editor-fold>

  static void createDifference(Match match, String key) throws Exception {

    // calculate y axis (max-diff +/-)
    int maxY = 0, minY = 0;
    for (int s = 0; s < match.setInfos.size(); ++s) {
      SetInfoHelper si = new SetInfoHelper(match.setInfos.get(s));
      int curS = 0, curR = 0;

      for (int idx = 0; idx < si.maxSize; ++idx) {

        // team reception
        if (idx > 0 && idx < si.sizeR) {
          curR = si.scoreR.get(idx);
          int diff = (curS - curR) * (si.isAServing ? 1 : -1);
          maxY = Math.max(maxY, diff);
          minY = Math.min(minY, diff);
        }

        // team service
        if (idx < si.sizeS) {
          curS = si.scoreS.get(idx);
          int diff = (curS - curR) * (si.isAServing ? 1 : -1);
          maxY = Math.max(maxY, diff);
          minY = Math.min(minY, diff);
        }
      }
    }

    // prepare y axis
    List<String> labelsY = new ArrayList<>();
    for (int i = minY; i <= maxY; ++i) {
      labelsY.add(String.format("%d", i));
    }

    // handle each set
    for (int s = 0; s < match.setInfos.size(); ++s) {
      SetInfoHelper si = new SetInfoHelper(match.setInfos.get(s));

      String keyDia = GeneratorDiagram.filenameDiff(key, match.index, si.nr);
      DiagramDiff dia = new DiagramDiff(si.sizeR + si.sizeS - 1, labelsY, -minY);
      dia.setData(si.scoreS, si.scoreR, si.isAServing);
      output(dia, keyDia);
    }
  }

  //<editor-fold defaultstate="collapsed" desc="The filenames.">
  /**
   * Creates the filename of a points-per-service diagram.
   *
   * @param keyDiagram The main key of the diagrams.
   * @param match The number of the match.
   * @param set The number of the set.
   * @return The creates filename.
   */
  public static String filenamePpS(String keyDiagram, int match, int set) {
    return keyDiagram.isEmpty() ? ""
      : String.format("%s_%d_%d.service.svg", keyDiagram, match, set);
  }

  /**
   * Creates the filename of a difference diagram.
   *
   * @param keyDiagram The main key of the diagrams.
   * @param match The number of the match.
   * @param set The number of the set.
   * @return The creates filename.
   */
  public static String filenameDiff(String keyDiagram, int match, int set) {
    return keyDiagram.isEmpty() ? ""
      : String.format("%s_%d_%d.difference.svg", keyDiagram, match, set);
  }
  //</editor-fold>
}

class SetInfoHelper {

  // The set info
  //private final SetInfo si;
  boolean isAServing;
  int nr;

  List<Integer> scoreR;
  List<Integer> scoreS;
  int sizeR;
  int sizeS;
  int maxSize;

  SetInfoHelper(SetInfo si) {
    //this.si = si;
    isAServing = si.isAServing();
    nr = si.nr;

    scoreR = si.scoringsA.get(0) == -1 ? si.scoringsA : si.scoringsB;
    scoreS = si.scoringsA.get(0) == -1 ? si.scoringsB : si.scoringsA;

    sizeR = scoreR.size();
    sizeS = scoreS.size();
    maxSize = Math.max(sizeR, sizeS);
  }
}
