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
 * @author gortonhu
 */
class Generator {

  private static final String TPL_STATISTICS = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.html";
  private static final String TPL_MATCH = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.match.html";
  private static final String TPL_PLAYER = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.player.html";

  private static final String TPL_POINTS = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.points.html";
  private static final String TPL_POINTS_SET = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.set.html";
  private static final String TPL_POINT_A = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.point.a.html";
  private static final String TPL_POINT_B = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.point.b.html";
  private static final String TPL_TO_A = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.to.a.html";
  private static final String TPL_TO_B = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.to.b.html";
  private static final String TPL_SUBST_A = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.subst.a.html";
  private static final String TPL_SUBST_B = "D:/workdir/brueckl-hotvolleys-source/_work/statistics.subst.b.html";

  private static final String HTML_STATS = "D:/workdir/brueckl-hotvolleys-source/uld/statistics3.html";

  private List<Match> data;
  private final String tplStatistics;
  private final String tplMatch;
  private final String tplPlayer;

  private final String tplPoints;
  private final String tplPointsSet;
  private final String tplPointA;
  private final String tplPointB;
  private final String tplToA;
  private final String tplToB;
  private final String tplSubstA;
  private final String tplSubstB;

  Generator(List<Match> data) {
    this.data = data;

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
  }

  private static String readFile(String fn) {
    StringBuilder sb = new StringBuilder();
    Charset charset = Charset.forName("UTF-8");
    File file = new File(fn);
    try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
      String line;
      while ((line = reader.readLine()) != null) {
        //System.out.println(line);
        sb.append(line).append(Str.NL);
      }
    } catch (IOException x) {
      System.err.format("IOException: %s%n", x);
    }
    return sb.toString();
  }

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
      if (match.hasPoints()) {
        sbAllPts.append(tplPoints);

        int idxSet = 0;
        StringBuilder action;
        StringBuilder sbSets = new StringBuilder();
        for (SetPoints sp : match.points) {
          SB sbSet = new SB(tplPointsSet);
          StringBuilder sbPts = new StringBuilder();

          int lastA = 0,
            lastB = 0,
            idxPt = -1,
            ptA, ptB;

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
      if (match.hasPoints()) {
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
    result
      .replace("{{ids}}", sb.toString())
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

  private StringBuilder checkForAction(SetPoints sp, int ptA, int ptB) {
    StringBuilder result = null;
    List<Action> actions = sp.actions(ptA, ptB);
    if (actions != null) {
      result = new StringBuilder();
      for (Action action : actions) {
        SB sbAct = new SB();
        switch (action.type) {

          case SUBSTITUTION:
            sbAct.append(action.teamA ? tplSubstA : tplSubstB);
            String[] infos = action.info.split("/");
            if (infos.length == 2) {
              sbAct
                .replace("{{info1}}", infos[0])
                .replace("{{info2}}", infos[1]);
            }
            // sbAct.replace("{{info}}", action.info.replace("/", "<br>"));
            break;

          case TIMEOUT:
            sbAct.append(action.teamA ? tplToA : tplToB);
            break;
        }

        result.append(sbAct.toStr());
      }
    }

    return result;
  }
}
