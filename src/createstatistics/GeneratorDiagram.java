package createstatistics;

//<editor-fold defaultstate="collapsed" desc="The imports.">
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import svg.pps.Diagram;

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

  private static void oldCreateDias() throws Exception {
    // the data of the diagram
    /*   0   0         1
      5	 1   2  5   1  3
      7  2   5  6   2  5
      8  4   7  7   3  6
     16  5   9  9  14  9
     17  6  18 10  16 11
     18  7  20 13  17 13
     21  8  21 14  18 15
     22  9  23 15  21 17
     23 11  24 17  22 20
     25     25 18  23 22
                   24 23
                   25    */
    Integer[] pa = null, pb = null;
    int maxY = 27; // TODO max y
    String[] actions = null;
    for (int i = 1; i < 4; ++i) {
      switch (i) {
        // set 1
        case 1:
          pa = new Integer[]{-1, 5, 7, 8, 16, 17, 18, 21, 22, 23, 27};
          pb = new Integer[]{0, 1, 2, 4, 5, 6, 7, 8, 9, 11};
          actions = new String[]{
            "L:Alex:Bojana:Vali:StefieM:StefieR:Sabsi",
            "l:8:2:14:13:6:11",
            "2:0:W:B1/Bojana",
            //"2:0:w:B2/Boj",
            //"0:2:W:B3/Boj",
            //"0:2:w:B4/Boj",
            "3:0:W:Bojana/B1",
            "1:7:t",
            "4:13:t",
            "21:8:W:Celi/Bojana",
            "9:22:w:10/6"
          };
          break;

        // set 2
        case 2:
          pa = new Integer[]{0, 2, 5, 7, 9, 18, 20, 21, 23, 24, 25};
          pb = new Integer[]{-1, 5, 6, 7, 9, 10, 13, 14, 15, 17, 18};
          actions = new String[]{
            "l:14:8:2:11:13:10",
            "L:Alex:Bojana:Vali:StefieM:StefieR:Sabsi",
            "0:5:T",
            "2:0:w:18/8",
            "9:9:t",
            "9:9:w:6/14",
            "10:14:t",
            "16:23:w:14/6",
            "24:17:W:Dragi/StefieM"
          //"24:17:w:Dragi2/StefieM",
          //"17:24:W:Dragi3/StefieM",
          //"17:24:w:Dragi4/StefieM"
          };
          break;

        // set 3
        case 3:
          pa = new Integer[]{-1, 1, 2, 3, 14, 16, 17, 18, 21, 22, 23, 24, 25};
          pb = new Integer[]{1, 3, 5, 6, 9, 11, 13, 15, 17, 20, 22, 23};
          actions = new String[]{
            "L:Alex:Bojana:Vali:StefieM:StefieR:Sabsi",
            "l:8:2:11:13:10:14",
            "6:7:w:6/10",
            "6:9:t",
            "6:14:w:10/6",
            "18:15:T",
            "19:15:T",
            "21:15:T",
            "21:16:T",
            "22:17:W:Dragi/StefieM",
            "23:22:T"
          };
          break;
      }

//      // any valid data
//      if (pa != null && pb != null && pa.length > 0 && pb.length > 0) {
//        ActionFilter filter = new ActionFilter(actions, pa, pb);
//
//        // prepare names for x axis
//        List<String> namesA = null, namesB = null;
//
//        int lastS = 0, curS, lastR = 0, curR;
//        int idx = -1,
//          maxS = filter.pS.length, maxR = filter.pR.length,
//          max = Math.max(maxS, maxR);
//        float positionBase = -1f;
//        filter.checkForAction(0, 0, true, 0f);
//        filter.checkForAction(0, 0, false, 0f);
//
//        while (++idx < max) {
//
//          // handle team reception (ignore first -1)
//          if (idx > 0 && idx < maxR) {
//            ++positionBase;
//
//            int idxLR = -1;
//            if (filter.lineupR() != null) {
//              if (namesB == null) {
//                namesB = new ArrayList<>();
//              }
//              // check for replacement of serving player
//              idxLR = idx % filter.lineupR().size();
//              String player = filter.lineupR().get(idxLR);
//              String player2 = filter.checkForReplacement(lastS, lastR, false, player);
//              namesB.add(player2);
//              if (!player2.equals(player)) {
//                filter.lineupR().set(idxLR, player2);
//              }
//            }
//
//            boolean first = true;
//            curR = filter.pR[idx];
//            int lastR0 = lastR + 1;
//            while (curR > lastR) {
//              ++lastR;
//              float pos = curR - lastR0 == 0 ? 0f : (lastR - lastR0) / (float) (curR - lastR0);
//              filter.checkForAction(lastS, lastR, false, positionBase + pos);
//              filter.checkForAction(lastS, lastR, true, positionBase + pos);
//
//              // checks for replacement of serving player
//              if (namesB != null && filter.lineupR() != null) {
//                int idxLast = namesB.size() - 1;
//                String player = namesB.get(idxLast);
//                String player2 = filter.checkForReplacement(lastS, lastR, false, player);
//                if (!player.equals(player2)) {
//                  if (first) {
//                    namesB.set(idxLast, player2);
//                  } else {
//                    namesB.set(idxLast, player + "/" + player2);
//                  }
//                  filter.lineupR().set(idxLR, player2);
//                }
//              }
//              first = false;
//            }
//          }
//
//          // handle team service
//          if (idx < maxS) {
//            ++positionBase;
//
//            int idxLS = -1;
//            if (filter.lineupS() != null) {
//              if (namesA == null) {
//                namesA = new ArrayList<>();
//              }
//              // check for replacement of serving player
//              idxLS = idx % filter.lineupS().size();
//              String player = filter.lineupS().get(idxLS);
//              String player2 = filter.checkForReplacement(lastS, lastR, true, player);
//              namesA.add(player2);
//              if (!player2.equals(player)) {
//                filter.lineupS().set(idxLS, player2);
//              }
//            }
//
//            boolean first = true;
//            curS = filter.pS[idx];
//            int lastS0 = lastS + 1;
//            while (curS > lastS) {
//              ++lastS;
//              if (namesA != null && filter.lineupS() != null) {
//                int idxLast = namesA.size() - 1;
//                String player = namesA.get(idxLast);
//                String player2 = filter.checkForReplacement(lastS, lastR, true, player);
//                if (!player.equals(player2)) {
//                  if (first) {
//                    namesA.set(idxLast, player2);
//                  } else {
//                    namesA.set(idxLast, player + "/" + player2);
//                  }
//                  filter.lineupS().set(idxLS, player2);
//                }
//              }
//
//              float pos = curS - lastS0 == 0 ? 0f : (lastS - lastS0) / (float) (curS - lastS0);
//              filter.checkForAction(lastS, lastR, false, positionBase + pos);
//              filter.checkForAction(lastS, lastR, true, positionBase + pos);
//
//              first = false;
//            }
//          }
//        }
//
//        createDia(pa, pb, maxY, namesA, namesB, filter.infos, "br1gd", 1, i);
//      }
    }
  }

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

      // create diagram
      String keyDia = GeneratorDiagram.filenamePpS(key, match.index, si.nr);
      createDia(si, maxY, namesA, namesB, keyDia);
    }
  }

  private static void createDia(SetInfo si, int maxY,
    List<String> namesA, List<String> namesB, String key) throws Exception {

    // create diagram
    Diagram dia = new Diagram(si, maxY, namesA, namesB);
    output(dia, key);
  }

  /**
   * Writes the resulting svg file.
   *
   * @param dia The diagram to write.
   * @param key The key of the league.
   * @param game The index of the game.
   * @param set The number of the set.
   * @throws Exception
   */
  private static void output(Diagram dia, String key) throws Exception {
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

  static String filenamePpS(String keyDiagram, int match, int set) {
    return keyDiagram.isEmpty() ? ""
      : // (keyDiagram + "_" + match + "_" + set + ".svg");
      String.format("%s_%d_%d.service.svg", keyDiagram, match, set);
  }
}
