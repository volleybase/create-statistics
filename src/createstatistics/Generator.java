package createstatistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * The html generator.
 *
 * @author volleybase
 */
class Generator {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  // file name of main html template
  private static final String TPL_STATISTICS = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.html";
  // file name of html fragment of a match
  private static final String TPL_MATCH = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.match.html";
  // file name of html fragment of a plyer's info
  private static final String TPL_PLAYER = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.player.html";
  // file name of html fragment of points info
  private static final String TPL_POINTS = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.points.html";
  // file name of html fragment of a set of the points info
  private static final String TPL_POINTS_SET = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.set.html";
  // file name of html fragment of a point for team A
  private static final String TPL_POINT_A = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.point.a.html";
  // file name of html fragment of a point for team B
  private static final String TPL_POINT_B = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.point.b.html";
  // file name of html fragment of time out of team A
  private static final String TPL_TO_A = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.to.a.html";
  // file name of html fragment of time out of team B
  private static final String TPL_TO_B = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.to.b.html";
  // file name of html fragment of a substitution of team A
  private static final String TPL_SUBST_A = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.subst.a.html";
  // file name of html fragment of a substitution of team B
  private static final String TPL_SUBST_B = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.subst.b.html";
  // file name of html fragment of a service of team A
  private static final String TPL_SERV_A = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.serv.a.html";
  // file name of html fragment of a service of team B
  private static final String TPL_SERV_B = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.serv.b.html";

  // the file name of the target file to write
  private static final String HTML_STATS = "D:/workdir/brueckl-hotvolleys-source/uld/statistics3.html";

  // the complete data - the internal model
  private final List<Match> data;

  // the loaded main template to fill
  private final String tplStatistics;
  // the loaded template of a match to fill
  private final String tplMatch;
  // the loaded template of a player to fill
  private final String tplPlayer;

  // the loaded template of all points to fill
  private final String tplPoints;
  // the loaded template of the points of a set to fill
  private final String tplPointsSet;
  // the loaded template of a point of team A to fill
  private final String tplPointA;
  // the loaded template of a point of team B to fill
  private final String tplPointB;
  // the loaded template of a time out of team A to fill
  private final String tplToA;
  // the loaded template of a time out of team B to fill
  private final String tplToB;
  // the loaded template of a substitution of team A to fill
  private final String tplSubstA;
  // the loaded template of a substitution of team B to fill
  private final String tplSubstB;
  // the loaded template of a service of team A to fill
  private final String tplServA;
  // the loaded template of a service of team B to fill
  private final String tplServB;

  // current rotation of team a
  private int rotationA = 0;
  // current rotation of team b
  private int rotationB = 0;
  // current team to serve(' ' - at start up of each set it is necessary to decide)
  private char teamToServe = ' ';
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="The constructor.">
  /**
   * Creates the output generator.
   *
   * @param data The data of all matches.
   */
  Generator(List<Match> data) {
    this.data = data;

    // load the template files
    tplStatistics = readFile(TPL_STATISTICS);
    tplMatch = readFile(TPL_MATCH);
    tplPlayer = readFile(TPL_PLAYER);

    tplPoints = readFile(TPL_POINTS);
    tplPointsSet = readFile(TPL_POINTS_SET);
    tplPointA = readFile(TPL_POINT_A);
    tplPointB = readFile(TPL_POINT_B);
    tplToA = readFile(TPL_TO_A);
    tplToB = readFile(TPL_TO_B);
    tplSubstA = readFile(TPL_SUBST_A);
    tplSubstB = readFile(TPL_SUBST_B);
    tplServA = readFile(TPL_SERV_A);
    tplServB = readFile(TPL_SERV_B);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Create the output.">
  /**
   * Creates the resulting statistics file.
   */
  void create() {

    // handle each match and collect their infos
    StringBuilder matches = new StringBuilder();
    List<String> ids = new ArrayList<>();
    int idxMatch = 0;
    for (Match match : data) {

      // create infos about players
      StringBuilder sbPlayers = new StringBuilder();
      for (Player player : match.players) {
        SB sbPlayer = new SB(tplPlayer);

        sbPlayer
          .replace("{{type}}", player.type)
          .replace("{{name}}", player.name)
          .replace("{{p1}}", player.p1).replace("{{m1}}", player.m1)
          .replace("{{p2}}", player.p2).replace("{{m2}}", player.m2)
          .replace("{{p3}}", player.p3).replace("{{m3}}", player.m3)
          .replace("{{p4}}", player.p4).replace("{{m4}}", player.m4)
          .replace("{{p5}}", player.p5).replace("{{m5}}", player.m5)
          .replace("{{pSum}}", player.pSum).replace("{{mSum}}", player.mSum)
          .replace("{{diff}}", player.diff).replace("{{quot}}", player.quot);

        sbPlayers.append(sbPlayer.toStr());
      }

      // check for points
      SB sbAllPts = new SB();
      if (match.hasSet()) {
        sbAllPts.append(tplPoints);

        int idxSet = 0;
        StringBuilder action;
        StringBuilder sbSets = new StringBuilder();
        for (SetInfo sp : match.setInfos) {
          SB sbSet = new SB(tplPointsSet);
          StringBuilder sbPts = new StringBuilder();

          int lastA = 0,
            lastB = 0,
            idxPt = -1,
            ptA, ptB;

          // reset team to serve and rotations
          teamToServe = ' ';
          rotationA = rotationB = 0;

          // 0:0
          if (null != (action = checkForAction(sp, 0, 0))) {
            sbPts.append(action);
          }
          sbPts.append(checkforService(sp, 0, 0, ' '));

          while (++idxPt >= 0) {
            boolean any = false;

            if (!sp.startA && idxPt < sp.pointsA.size()) {
              any = true;
              ptA = sp.pointsA.get(idxPt);
              while (lastA < ptA) {
                SB sbPt = new SB(tplPointA);
                sbPt.replace("{{point}}", ++lastA);
                sbPts.append(sbPt.toStr());

                if (null != (action = checkForAction(sp, lastA, lastB))) {
                  sbPts.append(action);
                }
                sbPts.append(checkforService(sp, lastA, lastB, 'A'));
              }
            }

            if (idxPt < sp.pointsB.size()) {
              any = true;
              ptB = sp.pointsB.get(idxPt);
              while (lastB < ptB) {
                SB sbPt = new SB(tplPointB);
                sbPt.replace("{{point}}", ++lastB);
                sbPts.append(sbPt.toStr());

                if (null != (action = checkForAction(sp, lastA, lastB))) {
                  sbPts.append(action);
                }
                sbPts.append(checkforService(sp, lastA, lastB, 'B'));
              }
            }

            if (sp.startA && idxPt < sp.pointsA.size()) {
              any = true;
              ptA = sp.pointsA.get(idxPt);
              while (lastA < ptA) {
                SB sbPt = new SB(tplPointA);
                sbPt.replace("{{point}}", ++lastA);
                sbPts.append(sbPt.toStr());

                if (null != (action = checkForAction(sp, lastA, lastB))) {
                  sbPts.append(action);
                }
                sbPts.append(checkforService(sp, lastA, lastB, 'A'));
              }
            }

            if (!any) {
              idxPt = -99;
            }
          }

          sbSet
            .replace("{{info}}", ++idxSet + ". Satz")
            .replace("{{points}}", sbPts.toString());
          sbSets.append(sbSet.toStr());
        }

        sbAllPts.replace("{{sets}}", sbSets.toString());
      }

      // finalize match info
      SB sbMatch = new SB(tplMatch);
      sbMatch
        .replace("{{index}}", ++idxMatch).replace("{{index}}", idxMatch)
        .replace("{{info}}", match.date + " " + match.info)
        .replace("{{players}}", sbPlayers.toString());

      ids.add("'m" + idxMatch + "'");
      if (match.hasSet()) {
        sbAllPts.replace("{{index}}", idxMatch).replace("{{index}}", idxMatch);
        ids.add("'mx" + idxMatch + "'");
      }
      sbMatch.replace("{{points}}", sbAllPts.toString());
      matches.append(sbMatch.toStr());
    }

    // inject matches into main html template
    SB result = new SB(tplStatistics);
    StringBuilder sb = new StringBuilder();
    for (String id : ids) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(id);
    }

    result.replace(
      "{{ids}}", sb.toString())
      .replace("{{matches}}", matches.toString());

    // write resulting file
    Charset charset = Charset.forName("UTF-8");
    String str = result.toString();
    File file = new File(HTML_STATS);
    try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), charset)) {
      writer.write(str, 0, str.length());
    } catch (IOException x) {
      System.err.format("IOException: %s%n", x);
    }
  }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Utility functions.">
  /**
   * Checks if there is an action stored for the current scoring.
   *
   * @param set The infos of the set.
   * @param ptA Current points of team A.
   * @param ptB Current points of team B.
   * @return The code to display the actions for the current scoring (might be
   * empty...).
   */
  private StringBuilder checkForAction(SetInfo set, int ptA, int ptB) {
    StringBuilder result = null;

    // get actions
    List<SetAction> actions = set.actions(ptA, ptB);
    if (actions != null) {

      // init buffer for result
      result = new StringBuilder();

      // handle each action
      for (SetAction action : actions) {

        // create buffer for an action
        SB sbAct = new SB();

        // what action type
        switch (action.type) {

          case SUBSTITUTION:
            // create info
            sbAct.append(action.teamA ? tplSubstA : tplSubstB);
            String[] infos = action.info.split("/");
            if (infos.length == 2) {
              sbAct
                .replace("{{info1}}", infos[0])
                .replace("{{info2}}", infos[1]);
            }

            // update line up (necessary for service info)
            ArrayList<String> lineUp = action.teamA ? set.lineUpA : set.lineUpB;
            if (lineUp != null) {
              int pos = lineUp.indexOf(infos[1]);
              if (pos == -1) {
                throw new IllegalArgumentException("Cannot find player " + infos[1] + " for substitution!");
              }
              lineUp.set(pos, infos[0]);
            }
            break;

          case TIMEOUT:
            // create info
            sbAct.append(action.teamA ? tplToA : tplToB);
            break;

          case SERVICE:
            // create info
            sbAct
              .append(action.teamA ? tplServA : tplServB)
              .replace("{{info}}", action.info);
            break;
        }

        // add create code for this action
        result.append(sbAct.toStr());
      }
    }

    // return result
    return result;
  }

  /**
   * Checks for changed service and add info if necessary.
   *
   * @param set The infos of a set.
   * @param lastA Scoring of team A.
   * @param lastB Scoring of team B.
   * @param scoringTeam Team to serve.
   * @return
   */
  CharSequence checkforService(SetInfo set, int lastA, int lastB, char scoringTeam) {

    // check for end of set
    if (Math.max(lastA, lastB) >= 25 && Math.abs(lastA - lastB) >= 2) {
      return "";
    }

    SB sb = new SB();
    switch (scoringTeam) {

      // first service
      case ' ':
        // check if team A has to do the first serve
        if (set.lineUpA != null && !set.pointsA.isEmpty() && set.pointsA.get(0) >= 0) {
          teamToServe = 'A';
          // create code to display
          sb
            .append(tplServA)
            .replace("{{info}}", set.lineUpA.get(rotationA));
        }

        // check if team B has to do the first serve
        if (set.lineUpB != null && !set.pointsB.isEmpty() && set.pointsB.get(0) >= 0) {
          teamToServe = 'B';
          // create code to display
          sb
            .append(tplServB)
            .replace("{{info}}", set.lineUpB.get(rotationB));
        }
        break;

      // check for change of service
      case 'A':
      case 'B':
        // if service has been changed
        if (teamToServe != ' ' && teamToServe != scoringTeam) {
          teamToServe = scoringTeam;

          // team A
          if (scoringTeam == 'A') {

            // update rotation
            if (++rotationA >= set.lineUpA.size()) {
              rotationA = 0;
            }

            // create code to display
            sb
              .append(tplServA)
              .replace("{{info}}", set.lineUpA.get(rotationA));

          } else { // team B

            // update rotation
            if (++rotationB >= set.lineUpB.size()) {
              rotationB = 0;
            }

            // create code to display
            sb
              .append(tplServB)
              .replace("{{info}}", set.lineUpB.get(rotationB));
          }
        }
        break;

      // error
      default:
        throw new IllegalArgumentException("Invalid team to score" + scoringTeam + "!");
    }

    // return result
    return sb.toStr();
  }

  /**
   * Reads a template file.
   *
   * @param filename The file name.
   * @return The file content.
   */
  private static String readFile(String filename) {
    StringBuilder sb = new StringBuilder();
    Charset charset = Charset.forName("UTF-8");
    File file = new File(filename);
    try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append(Str.NL);
      }
    } catch (IOException x) {
      System.err.format("IOException: %s%n", x);
    }

    return sb.toString();
  }
  //</editor-fold>
}
