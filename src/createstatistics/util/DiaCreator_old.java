//package extern;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
//import java.util.HashMap;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * To create the diagrams by calling the generator app.
// *
// * @author volleybase
// */
//public class DiaCreator {
//
//  private final static String LIN2 = "==========================================================";
//
//  private final static String CHROME = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
//  // private final static String URL_PN = "http://localhost:5005/index.html#generator/generatorimage/0000y9EsXrfvBghvOxRObDmQ1Rpr32xw";
//  private final static String URL_GENERATOR = "http://localhost:5005/index.html#generator/generatorimage:0000y9EsXrfvBghvOxRObDmQ1Rpr32xw";
//  private final static String WORK = "D:\\workdir\\work\\diagram";
//  private final static String WORK_JSON = "D:\\workdir\\work\\JSON";
//  // the directory for the input files of the diagram service
//  private static final String DIR_DIA_USERDATA = "D:\\workdir\\vb-statsone-backup\\_backup\\i%20selba2\\userdata\\";
//
//  private final static HashMap<String, String> URLS = new HashMap<>();
//
//  static {
//    URLS.put("br2_19", URL_GENERATOR);
//    URLS.put("br3g_19", URL_GENERATOR);
//    URLS.put("br3_19", URL_GENERATOR);
//    URLS.put("br4_19", URL_GENERATOR);
//
//    URLS.put("u15pn_19", URL_GENERATOR);
//    URLS.put("u17_19", URL_GENERATOR);
//
//    // URLS.put("test", "http://localhost:5005/index.html#generator/category/0000yOTyrTR3NSYgdW6KdzvO4KBr4Gdw");
//  }
//
//  @SuppressWarnings("SleepWhileInLoop")
//  public static boolean createDiagrams(String key) {
//    System.out.println("\n-- " + key + " --------------------------------------");
//    if (URLS.containsKey(key)) {
//      try {
//
//        // delete files
//        deleteFiles(key);
//        deleteWORK();
//
//        // create diagram files
//        ProcessBuilder builder = new ProcessBuilder(CHROME, URLS.get(key));
//        // Process process =;
//        builder.start();
//
//        // count files until not increasing
//        try {
//          Thread.sleep(5555);
//
//          int cur = 0,
//            times = 0;
//          while (times < 15) {
//
//            Thread.sleep(2000);
//
//            int c = countFiles(key);
//            if (c == cur) {
//              ++times;
//            } else {
//              cur = c;
//              times = 0;
//            }
//          }
//
//        } catch (InterruptedException ex) {
//          Logger.getLogger(DiaCreator.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        System.out.println(LIN2);
//        System.out.println(LIN2);
//        System.out.println(LIN2);
//
//        return true;
//      } catch (IOException ex) {
//        Logger.getLogger(DiaCreator.class.getName()).log(Level.SEVERE, null, ex);
//      }
//    }
//
//    return false;
//  }
//
//  private static void deleteFiles(String key) {
//    File dir = new File(getDl());
//    File[] files = dir.listFiles((File dir1, String name) -> name.startsWith(key + "_"));
//    for (File file : files) {
//      System.out.println("Delete: " + file.getName());
//      file.delete();
//    }
//  }
//
//  private static void deleteWORK() {
//    File dir = new File(WORK);
//    File[] files = dir.listFiles((File dir1, String name) -> true);
//
//    for (File file : files) {
//      System.out.println("Delete: " + file.getName());
//      file.delete();
//    }
//  }
//
//  private static int countFiles(String key) {
//    File dir = new File(getDl());
//    File[] files = dir.listFiles((File dir1, String name) -> name.startsWith(key + "_"));
//    return files.length;
//  }
//
//  private static String getDl() {
//    String home = System.getProperty("user.home");
//    return home + "/Downloads/";
//  }
//
//  /**
//   * Moves the resulting diagram files from download dir to the work dir to
//   * embed them into the statistic files.
//   *
//   * @param key The key of the diagram files to handle.
//   */
//  public static void moveDiagramFiles(String key) {
//    File dir = new File(getDl());
//    File[] files = dir.listFiles((File dir1, String name) -> name.startsWith(key + "_"));
//    for (File file : files) {
//      try {
//        String nam = file.getName();
//        int pos = nam.lastIndexOf(".");
//        nam = nam.substring(0, pos);
//        String str = WORK + "\\" + nam + ".svg";
//        Files.move(file.toPath(), new File(str).toPath(), StandardCopyOption.REPLACE_EXISTING);
//      } catch (IOException ex) {
//        Logger.getLogger(DiaCreator.class.getName()).log(Level.SEVERE, null, ex);
//      }
//    }
//  }
//
//  /**
//   * Moves the json files to examine them later...
//   */
//  public static void moveJsonDiagramFiles() {
//    File dirUserdata = new File(DIR_DIA_USERDATA);
//    File[] files = dirUserdata
//      .listFiles((File file, String name) -> name.startsWith("games") && name.endsWith(".json"));
//    for (File file : files) {
//      try {
//        String str = WORK_JSON + "\\" + file.getName();
//        Files.move(file.toPath(), new File(str).toPath(), StandardCopyOption.REPLACE_EXISTING);
//      } catch (IOException ex) {
//        Logger.getLogger(DiaCreator.class.getName()).log(Level.SEVERE, null, ex);
//      }
//    }
//  }
//}
