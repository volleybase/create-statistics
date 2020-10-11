package createstatistics;

import createstatistics.data.ActionInfo;
import createstatistics.data.Match;
import createstatistics.data.SetInfo;
import createstatistics.util.SB;
import createstatistics.util.Str;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The html generator.
 *
 * @author volleybase
 */
class Generator {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  private static final String LIN2 = "==========================================================";
  private static final String LIN = "----------------------------------------------------------";
  private static final String LINERR = ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";

  private static final String TPL_DIR = "D:/workdir/brueckl-hotvolleys-source/_work/statistics/";
  // file name of main html template
  private static final String TPL_STATISTICS = "statistics.html";
  // file name of html fragment of a match
  private static final String TPL_MATCH = "statistics.match.html";
  // file name of html fragment of a plyer's info
  private static final String TPL_PLAYER = "statistics.player.html";
  // file name of html fragment of points info
  private static final String TPL_POINTS = "statistics.points.html";
  // file name of html fragment of a set of the points info
  private static final String TPL_POINTS_SET = "statistics.set.html";
  // file name of html fragment of a point for team A
  private static final String TPL_POINT_A = "statistics.point.a.html";
  // file name of html fragment of a point for team B
  private static final String TPL_POINT_B = "statistics.point.b.html";
  // file name of html fragment of time out of team A
  private static final String TPL_TO_A = "statistics.to.a.html";
  // file name of html fragment of time out of team B
  private static final String TPL_TO_B = "statistics.to.b.html";
  // file name of html fragment of a substitution of team A
  private static final String TPL_SUBST_A = "statistics.subst.a.html";
  // file name of html fragment of a substitution of team B
  private static final String TPL_SUBST_B = "statistics.subst.b.html";
  // file name of html fragment of a service of team A
  private static final String TPL_SERV_A = "statistics.serv.a.html";
  // file name of html fragment of a service of team B
  private static final String TPL_SERV_B = "statistics.serv.b.html";
  // file name of html fragment of diagrams container
  private static final String TPL_DIAGRAMS = "statistics.diagrams.html";
  // file name of html fragment of a diagram
  private static final String TPL_DIAGRAM = "statistics.diagram.html";

  // the directory containing the diagrams
  // private static final String DIR_DIA_WORK = "D:\\workdir\\work\\diagram\\";
  // the directory for the input files of the diagram service
  private static final String DIR_DIA_USERDATA = "D:\\workdir\\vb-statsone-backup\\_backup\\i%20selba2\\userdata\\";

  // the charset for writing files
  private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

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
  // the loaded template of the diagram container
  private final String tplDiagrams;
  // the loaded template of a diagram
  private final String tplDiagram;

  // current rotation of team a
  private int rotationA = 0;
  // current rotation of team b
  private int rotationB = 0;
  // current team to serve(' ' - at start up of each set it is necessary to decide)
  private char teamToServe = ' ';
  // the flag to force end of set
  private boolean endOfSet;
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
    tplStatistics = readTplFile(TPL_STATISTICS);
    tplMatch = readTplFile(TPL_MATCH);
    tplPlayer = readTplFile(TPL_PLAYER);

    tplPoints = readTplFile(TPL_POINTS);
    tplPointsSet = readTplFile(TPL_POINTS_SET);
    tplPointA = readTplFile(TPL_POINT_A);
    tplPointB = readTplFile(TPL_POINT_B);
    tplToA = readTplFile(TPL_TO_A);
    tplToB = readTplFile(TPL_TO_B);
    tplSubstA = readTplFile(TPL_SUBST_A);
    tplSubstB = readTplFile(TPL_SUBST_B);
    tplServA = readTplFile(TPL_SERV_A);
    tplServB = readTplFile(TPL_SERV_B);

    tplDiagram = readTplFile(TPL_DIAGRAM);
    tplDiagrams = readTplFile(TPL_DIAGRAMS);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Create the output.">
  /**
   * Creates the resulting statistics file.
   *
   * @param target The file to create for theses group of statistics.
   */
  void create(String target, String keyBack, String keyDiagram) throws Exception {

    // handle each match and collect their infos
    StringBuilder matches = new StringBuilder();
    List<String> ids = new ArrayList<>();
    //data.stream().map((match) -> {
    for (Match match : data) {

      // debug output of read match
      // System.out.println(LIN2);
      // System.out.println("create match: " + match.date + " " + match.info);
      // if (match.date.equals("05.Feb")) {
      //   System.out.println(LIN2);
      //   System.out.println(LIN2);
      // }
      //
      // create infos about players
      StringBuilder sbPlayers = new StringBuilder();
      match.players.stream().map((player) -> {
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
        return sbPlayer;
      }).forEachOrdered((sbPlayer) -> {
        sbPlayers.append(sbPlayer.toStr());
      });

      // check for points and diagrams
      SB sbAllPts = new SB();
      StringBuilder sbDiasP = null; // Punkte (Verlauf)
      StringBuilder sbDiasV = null; // Vorsprung
      if (match.hasSet()) {
        sbAllPts.append(tplPoints);

        int idxSet = 0;
        StringBuilder action;
        StringBuilder sbSets = new StringBuilder();
        for (SetInfo set : match.setInfos) {

          // System.out.println(LIN);
          // System.out.println("Satz: " + set.nr);
          SB sbSet = new SB(tplPointsSet);
          StringBuilder sbPts = new StringBuilder();

          int lastA = 0,
            lastB = 0,
            idxPt = -1,
            ptA, ptB;

          // reset team to serve and rotations
          teamToServe = ' ';
          rotationA = rotationB = 0;
          endOfSet = false;

          // 0:0
          if (null != (action = checkForAction(set, 0, 0))) {
            sbPts.append(action);
          }
          if (!endOfSet) {
            sbPts.append(checkforService(set, 0, 0, ' '));
          }

          while (++idxPt >= 0 && !endOfSet) {
            boolean any = false;

            if (lastA == 23) {
              // just for debugging...
              lastA = lastA;
            }

            if (!set.startA && idxPt < set.scoringsA.size()) {
              any = true;
              ptA = set.scoringsA.get(idxPt);
              while (lastA < ptA) {
                SB sbPt = new SB(tplPointA);
                sbPt.replace("{{point}}", ++lastA);
                sbPts.append(sbPt.toStr());

                if (null != (action = checkForAction(set, lastA, lastB))) {
                  sbPts.append(action);
                }
                if (!endOfSet) {
                  sbPts.append(checkforService(set, lastA, lastB, 'A'));
                }
              }
            }

            if (idxPt < set.scoringsB.size()) {
              any = true;
              ptB = set.scoringsB.get(idxPt);
              while (lastB < ptB) {
                SB sbPt = new SB(tplPointB);
                sbPt.replace("{{point}}", ++lastB);
                sbPts.append(sbPt.toStr());

                if (null != (action = checkForAction(set, lastA, lastB))) {
                  sbPts.append(action);
                }
                if (!endOfSet) {
                  sbPts.append(checkforService(set, lastA, lastB, 'B'));
                }
              }
            }

            if (set.startA && idxPt < set.scoringsA.size()) {
              any = true;
              ptA = set.scoringsA.get(idxPt);
              while (lastA < ptA) {
                SB sbPt = new SB(tplPointA);
                sbPt.replace("{{point}}", ++lastA);
                sbPts.append(sbPt.toStr());

                if (null != (action = checkForAction(set, lastA, lastB))) {
                  sbPts.append(action);
                }
                if (!endOfSet) {
                  sbPts.append(checkforService(set, lastA, lastB, 'A'));
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

          // handle previously created diagrams
          try {

            // points per service
            String fnDiaP = GeneratorDiagram.filenamePpS(keyDiagram, match.index, set.nr);
            if (fnDiaP != null) {
              String fullFnDia = GeneratorDiagram.DIR_DIA_WORK + fnDiaP;
              File file = new File(fullFnDia);
              if (file.isFile()) {
                List<String> lines = Files.readAllLines(file.toPath(), CHARSET_UTF8);

                SB sbDia = new SB(tplDiagram);
                sbDia.replace("{{diagram}}", String.join("", lines));

                if (sbDiasP == null) {
                  sbDiasP = new StringBuilder();
                }
                sbDiasP.append(sbDia.toStr());
              }
            }

            // if differences diagram exists, embed it
            String fnDiaV = GeneratorDiagram.filenameDiff(keyDiagram, match.index, set.nr);
            if (fnDiaV.length() > 0) {
              String fullFnDia = GeneratorDiagram.DIR_DIA_WORK + fnDiaV;
              File file = new File(fullFnDia);
              if (file.isFile()) {
                List<String> lines = Files.readAllLines(file.toPath(), CHARSET_UTF8);

                SB sbDia = new SB(tplDiagram);
                sbDia.replace("{{diagram}}", String.join("", lines));

                if (sbDiasV == null) {
                  sbDiasV = new StringBuilder();
                }
                sbDiasV.append(sbDia.toStr());
              }
            }

          } catch (IOException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
          }
        }

        sbAllPts.replace("{{sets}}", sbSets.toString());
      }

      // finalize match info
      SB sbMatch = new SB(tplMatch);
      sbMatch
        .replace("{{index}}", match.index)
        .replace("{{info}}", match.date + " " + match.info)
        .replace("{{players}}", sbPlayers.toString());
      ids.add("'m" + match.index + "'");
      if (match.hasSet()) {
        sbAllPts.replace("{{index}}", match.index);
        ids.add("'mx" + match.index + "'");
      }
      sbMatch.replace("{{points}}", sbAllPts.toString());

      // embed diagrams or delete placeholder
      StringBuilder sbDias = new StringBuilder();
      // points per service
      if (sbDiasP != null) {
        ids.add("'mdp" + match.index + "'");
        SB sbDias2 = new SB(tplDiagrams);
        sbDias2.replace("{{diagrams}}", sbDiasP.toString());
        sbDias2.replace("{{type}}", "dp");
        sbDias2.replace("{{index}}", match.index);
        sbDias2.replace("{{text}}", "Punkte (Verlauf)");
        sbDias.append(sbDias2);
      }

      // differences
      if (sbDiasV != null) {
        ids.add("'mdv" + match.index + "'");
        SB sbDias2 = new SB(tplDiagrams);
        sbDias2.replace("{{diagrams}}", sbDiasV.toString());
        sbDias2.replace("{{type}}", "dv");
        sbDias2.replace("{{index}}", match.index);
        sbDias2.replace("{{text}}", "Punkte (Vorsprung)");
        sbDias.append(sbDias2);
      }

      sbMatch.replace("{{diagrams}}", sbDias.toString());
//      return sbMatch;
//
//    }).forEachOrdered((sbMatch) -> {
//      // add match to main data
//      matches.append(sbMatch.toStr());
//    });
      matches.append(sbMatch.toStr());
    }

    // inject matches into main html template
    SB result = new SB(tplStatistics);
    // set back links
    result.replace("{{back}}", keyBack);

    // handle the ids of the expanders
    StringBuilder sb = new StringBuilder();
    ids.forEach((id) -> {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(id);
    });
    result
      .replace("{{ids}}", sb.toString())
      .replace("{{matches}}", matches.toString());

    // write resulting file
    String str = result.toString();
    File file = new File(target);
    try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), CHARSET_UTF8)) {
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
  private StringBuilder checkForAction(SetInfo set, int ptA, int ptB) throws Exception { //, json.Object diaActions) {
    StringBuilder result = null;

    // get actions
    // List<SetAction> actions = set.actions(ptA, ptB);
    List<ActionInfo> actions = set.actions(ptA, ptB);
    if (actions != null) {

      // init buffer for result
      result = new StringBuilder();

      // handle each action
      // for (SetAction action : actions) {
      for (ActionInfo ai : actions) {

        // create buffer for an action
        SB sbAct = new SB();

        // what action type
        switch (ai.type) {

          case END_OF_SET:
            endOfSet = true;
            return null;

          case SUBSTITUTION:
            // create info
            sbAct.append(ai.teamA ? tplSubstA : tplSubstB);
            String[] infos = ai.info.split("/");
            if (infos.length == 2) {
              sbAct
                .replace("{{info1}}", infos[0])
                .replace("{{info2}}", infos[1]);
            }

            // update line up (necessary for service info)
            ArrayList<String> lineUp = ai.teamA ? set.lineUpA : set.lineUpB;
            if (lineUp != null) {
              int pos = lineUp.indexOf(infos[1]);
              if (pos == -1) {
                //throw new IllegalArgumentException("Cannot find player " + infos[1] + " for substitution in set " + set.nr + " (" + action.key + ")!");
                System.out.println(LINERR);
                System.out.println("Cannot find player " + infos[1] + " for substitution in set " + set.nr + " (" + ai.info + ")!");
              } else {
                lineUp.set(pos, infos[0]);
              }
            }
            break;

          case TIMEOUT:
            // create info
            sbAct.append(ai.teamA ? tplToA : tplToB);
            break;

//          case SERVICE:
//            // create info
//            sbAct
//              .append(action.teamA ? tplServA : tplServB)
//              .replace("{{info}}", action.info);
//            break;
          default:
            throw new Exception("Invalid action: " + ai.info + "!");
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
    // check for end of decision set
    if (set.decision && Math.max(lastA, lastB) >= 15 && Math.abs(lastA - lastB) >= 2) {
      return "";
    }

    SB sb = new SB();
    switch (scoringTeam) {

      // first service
      case ' ':
        // check if team A has to do the first serve
        if (set.lineUpA != null && !set.scoringsA.isEmpty() && set.scoringsA.get(0) >= 0) {
          teamToServe = 'A';
          // create code to display
          sb
            .append(tplServA)
            .replace("{{info}}", set.lineUpA.get(rotationA));
        }

        // check if team B has to do the first serve
        if (set.lineUpB != null && !set.scoringsB.isEmpty() && set.scoringsB.get(0) >= 0) {
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
  private static String readTplFile(String filename) {
    StringBuilder sb = new StringBuilder();
    Charset charset = Charset.forName("UTF-8");
    File file = new File(TPL_DIR + filename);
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

  //<editor-fold defaultstate="collapsed" desc="Diagram functions - old - not used - to be removed.">
  //  void deleteUserData() {
  //    File dir = new File(DIR_DIA_USERDATA);
  //    File[] files = dir.listFiles(new FilenameFilter() {
  //      @Override
  //      public boolean accept(File dir, String name) {
  //        return name.startsWith("games");
  //      }
  //    });
  //
  //    for (File file : files) {
  //      System.out.println("Delete: " + file.getName());
  //      file.delete();
  //    }
  //  }
  //
  //  /**
  //   * Creates the diagrams.
  //   *
  //   * @param targetJSON The json file to write.
  //   * @param keyDiagram The (main) key of the diagrams.
  //   */
  //  void createJsonForDiagrams(String targetDia, String keyDiagram) {
  //    json.Array diaData = new json.Array();
  //    for (Match match : data) {
  //
  //      // create match info
  //      json.Object diaMatch = new json.Object();
  //      diaData.add(diaMatch);
  //      diaMatch.add("date", match.date);
  //      // diaMatch.add("time", "");
  //      diaMatch.add("info", match.info);
  //
  //      // handle each set
  //      for (SetInfo set : match.setInfos) {
  //        System.out.println(LIN);
  //        System.out.println("Satz: " + set.nr);
  //
  //        // create and add diagram data of current set
  //        json.Object diaSet = createDiaSet(set);
  //        diaMatch.add("set" + set.nr, diaSet);
  //        json.Object diaActions = new json.Object();
  //        diaSet.add("actions", diaActions);
  //        if (!keyDiagram.isEmpty()) {
  //          String keyDia = keyDiagram + '_' + match.index + '_' + set.nr;
  //          diaSet.add("key", keyDia);
  //        }
  //
  //        // add the actions
  //        Set<String> keys = set.actions.keys();
  //        for (String key : keys) {
  //          List<SetAction> actions = set.actions.get(key);
  //          json.Array diaActs = new json.Array();
  //
  //          for (SetAction action : actions) {
  //            switch (action.type) {
  //              case TIMEOUT:
  //                diaActs.add(action.teamA ? "T" : "t");
  //                break;
  //              case SUBSTITUTION:
  //                diaActs.add((action.teamA ? "W" : "w") + ":" + action.info);
  //                break;
  //            }
  //          }
  //
  //          if (!diaActs.isEmpty()) {
  //            diaActions.add(key, diaActs);
  //          }
  //        }
  //      }
  //    }
  //
  //    // write resulting file
  //    String str = diaData.stringify();
  //    File file = new File(targetDia);
  //    try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), CHARSET_UTF8)) {
  //      writer.write(str, 0, str.length());
  //    } catch (IOException x) {
  //      System.err.format("IOException: %s%n", x);
  //    }
  //  }
  //
  //  /**
  //   * Creates the diagram info of a set.
  //   *
  //   * @param set The set info.
  //   * @return The diagram info.
  //   */
  //  private static json.Object createDiaSet(SetInfo set) {
  //    json.Object diaSet = new json.Object();
  //    diaSet.add("service", set.startA ? "a" : "b");
  //
  //    diaSet.add("a", createDiaInfo(set.lineUpA, set.scoringsA));
  //    diaSet.add("b", createDiaInfo(set.lineUpB, set.scoringsB));
  //
  //    List<Integer> scoreSrv = set.startA ? set.scoringsA : set.scoringsB,
  //      scoreRec = set.startA ? set.scoringsB : set.scoringsA;
  //    int lenS = scoreSrv.size(),
  //      lenR = scoreRec.size(),
  //      len = Math.max(lenS, lenR),
  //      sc,
  //      dif = 0,
  //      maxDiff = 0,
  //      lastS = 0,
  //      lastR = 0;
  //    json.Array difs = new json.Array();
  //    diaSet.add("differences", difs);
  //    if (lenS > 0) {
  //      dif = lastS = scoreSrv.get(0);
  //      difs.add(lastS);
  //    }
  //
  //    for (int i = 1; i < len; ++i) {
  //      if (i < lenR) {
  //        sc = scoreRec.get(i);
  //        dif -= sc - lastR;
  //        lastR = sc;
  //        difs.add(dif);
  //        maxDiff = Math.max(maxDiff, Math.abs(dif));
  //      }
  //      if (i < lenS) {
  //        sc = scoreSrv.get(i);
  //        dif += sc - lastS;
  //        lastS = sc;
  //        difs.add(dif);
  //        maxDiff = Math.max(maxDiff, Math.abs(dif));
  //      }
  //    }
  //    diaSet.add("maxdifference", maxDiff);
  //
  //    return diaSet;
  //  }
  //
  //  /**
  //   * Creates the diagram info of a team.
  //   *
  //   * @param lineUp The line up.
  //   * @param scorings The scoring.
  //   * @return The diagram info of a team.
  //   */
  //  private static json.Object createDiaInfo(List<String> lineUp, List<Integer> scorings) {
  //
  //    json.Object diaInfo = new json.Object();
  //
  //    // lineup
  //    json.Array luA = new json.Array();
  //    diaInfo.add("lineup", luA);
  //    if (lineUp != null && !lineUp.isEmpty()) {
  //      lineUp.forEach((per) -> {
  //        luA.add(per);
  //      });
  //    }
  //
  //    // points
  //    json.Array points = new json.Array();
  //    diaInfo.add("points", points);
  //    scorings.forEach((pt) -> {
  //      points.add(pt);
  //    });
  //
  //    return diaInfo;
  //  }
  //</editor-fold>
//
  //<editor-fold defaultstate="collapsed" desc="Create diagrams.">
  /**
   * Creates the diagrams.
   *
   * @param keyDiagram
   */
  void createDiagrams(String keyDiagram) throws Exception {
    // handle each match: create the diagrams
    for (Match match : data) {
      GeneratorDiagram.createPointsPerService(match, keyDiagram);
      GeneratorDiagram.createDifference(match, keyDiagram);
    }
  }
  //</editor-fold>
}
