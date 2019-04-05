package createstatistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * The entry point to create the statistics from excel file Statistk.xlsx.
 *
 * @author volleybase
 */
public class Main {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  // statistics source file
  private static final String FN_SOURCE_BR3 = "D:/workdir/brueckl-hotvolleys-source/_work/Statistik.xlsx";
  private static final String FN_SOURCE_U17 = "D:/workdir/brueckl-hotvolleys-source/_work/Statistik17.xlsx";
  private static final String FN_SOURCE_U15AR = "D:/workdir/brueckl-hotvolleys-source/_work/Statistik15AR.xlsx";

  // the file names of the target files to write
  private static final String HTML_STATS_BR3 = "D:/workdir/brueckl-hotvolleys-source/uld/statistics3.html";
  private static final String HTML_STATS_U15AR = "D:/workdir/brueckl-hotvolleys-source/uld/statistics3AR.html";
  private static final String HTML_STATS_U15FD = "D:/workdir/brueckl-hotvolleys-source/uld/statistics4FD.html";
  private static final String HTML_STATS_U17 = "D:/workdir/brueckl-hotvolleys-source/u17/statistics.html";
  private static final String HTML_STATS_MPO = "D:/workdir/brueckl-hotvolleys-source/lld/statistics.html";

  // POI - formula evaluator
  private static FormulaEvaluator evaluator;
  // POI - a sheet
  private static XSSFSheet SHEET;
  // the current row to read
  private static int ROW;

  // the internal data model
  private static List<Match> DATA;
  //</editor-fold>

  /**
   * The entry point.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // createStats(FN_SOURCE_BR3, HTML_STATS_BR3, "br3g", "Spiele");
    // createStats(FN_SOURCE_U17, HTML_STATS_U17, "u17", "Spiele");

    // createStats(FN_SOURCE_U15AR, HTML_STATS_MPO, "br2", "SpieleMPO");
    createStats(FN_SOURCE_U15AR, HTML_STATS_U15AR, "br3", "SpieleAR");
    // createStats(FN_SOURCE_U15AR, HTML_STATS_U15FD, "br4", "SpieleUL");
  }

  private static void createStats(String source, String target, String back, String sheet) {

    // init data
    ROW = 0;
    DATA = new ArrayList<>();

    // to read from file
    FileInputStream fileInput = null;
    // POI - the workbook
    XSSFWorkbook workbook = null;

    try {
      //<editor-fold defaultstate="collapsed" desc="Open the sheet for reading.">
      // open file
      // fileInput = new FileInputStream(new File(FN));
      fileInput = new FileInputStream(new File(source));

      // get workbook from file content, prepare formula evaluator
      workbook = new XSSFWorkbook(fileInput);
      evaluator = workbook.getCreationHelper().createFormulaEvaluator();

      // get sheet
      SHEET = workbook.getSheet(sheet);
      if (SHEET == null) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Cannot find sheet " + sheet + "!");
        workbook.close();
        fileInput.close();
        return;
      }
      //</editor-fold>

      // read all matches and collect their data
      Match match;
      do {
        match = readMatch();
        if (match != null) {
          DATA.add(match);
        }
      } while (match != null);

      // generate of the output
      Generator generator = new Generator(DATA);
      generator.create(target, back);

    } catch (FileNotFoundException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Throwable thrown) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, thrown);
    } finally {
      //<editor-fold defaultstate="collapsed" desc="clean up">
      try {
        if (workbook != null) {
          workbook.close();
        }
      } catch (IOException ioex) {
      }

      try {
        if (fileInput != null) {
          fileInput.close();
        }
      } catch (IOException ioex) {
      }
      //</editor-fold>
    }
  }

  //<editor-fold defaultstate="collapsed" desc="The main functions to read the data from the sheet.">
  /**
   * Try to read the data of a match.
   *
   * @return The data of a match or null if nothing found.
   */
  static Match readMatch() {

    // the row where to continue with reading
    int startRow = ROW;
    // the resulting data
    Match match = null;
    // POI - a row in a sheet
    XSSFRow row = null;
    // PO - a cell
    Cell cell0 = null;
    // the search or exit flag
    boolean search = true;

    // check for rows with placeholders and ignore them
    //  (might be necessary because of multiple actions)
    while (search) {

      // current cell
      cell0 = null;
      // reset search flag - set it later if data found for current row
      search = false;

      // try to get next row
      row = SHEET.getRow(ROW);
      if (row != null) {

        // try to get cell 1
        cell0 = row.getCell(0);
        if (cell0 != null) {

          // get the type of the cell
          CellType type = cell0.getCellType();

          // string -> read cell value
          if (type == CellType.STRING) {
            String cur = cell0.getStringCellValue();

            // "." is a placeholder -> continue with next row
            if (cur.equals(".")) {
              ++ROW;
              ++startRow;
              search = true;
            }
          }
        }
      }
    }

    // if cell exists and is not empty: start new match
    if (cell0 != null && cell0.getCellType() != CellType.BLANK) {
      match = new Match();

      // must be a date - read match info from cell 1 and 2
      Date date = cell0.getDateCellValue();
      DateFormat formatter = new SimpleDateFormat("dd.MMM");
      match.date = formatter.format(date);
      match.info = row.getCell(1).getStringCellValue();

      // ignore header of sheet
      ROW += 3;
      // the data of all players
      Player player;
      do {
        // try to read data of the next player
        player = readPlayer();
        // if found: add them
        if (player != null) {
          match.players.add(player);
        }
      } while (player != null);

      // try to read the points
      startRow += 3;
      // 1st column of pints
      int col = 18;
      // 1st column of actions
      int colAct = 27;

      // read the points
      SetInfo set;
      int nr = 0;
      do {
        // try to read the optional points
        set = readSetInfos(++nr, startRow, col);
        if (set != null) {

          // try to read the optional actions
          readActions(set, startRow, ++colAct);

          // add the points of a set to the match
          match.add(set);

          // increase column
          col += 2;
        }
      } while (set != null);

      // debug output of read match
      String LIN = "----------------------------------------------------------";
      System.out.println(LIN);
      System.out.println("match: " + match.date + " " + match.info);
      System.out.println(LIN);
      System.out.println(match.toString());
      System.out.println(LIN);
    }

    // resurn result
    return match;
  }

  /**
   * Reads the data of a player.
   *
   * @return The data of a player or null.
   */
  static Player readPlayer() {
    // the result
    Player player = null;

    // get next row of data
    XSSFRow row = SHEET.getRow(ROW++);
    if (row != null) {

      // type (footer(=sum); plus, medium, minus)
      String type = "none";

      // try to get content of first two cells
      Cell cell0 = row.getCell(0);
      CellType t0 = CellType.BLANK;
      Cell cell1 = row.getCell(1);
      CellType t1 = CellType.BLANK;
      if (cell0 != null) {
        t0 = cell0.getCellType();
      }
      if (cell1 != null) {
        t1 = cell1.getCellType();
      }

      // any data found for player: create it
      if (t0 != CellType.BLANK || t1 != CellType.BLANK) {
        player = new Player();

        // set player name
        player.name = t0 == CellType.STRING ? cell0.getStringCellValue() : "";
        // footer has an empty name
        if (player.name.length() == 0) {
          type = "footer";
        }

        player.p1 = getInt(row, 1);
        player.m1 = getInt(row, 2);
        player.p2 = getInt(row, 3);
        player.m2 = getInt(row, 4);
        player.p3 = getInt(row, 5);
        player.m3 = getInt(row, 6);
        player.p4 = getInt(row, 7);
        player.m4 = getInt(row, 8);
        player.p5 = getInt(row, 9);
        player.m5 = getInt(row, 10);

        player.pSum = getStr(row, 12);
        player.mSum = getStr(row, 13);
        player.diff = getStr(row, 14);
        player.quot = getXNum(row, 15);

        int typ = getInt(row, 16);
        switch (typ) {
          case 1:
            type = "plus";
            break;
          case 2:
            type = "medium";
            break;
          case 3:
            type = "minus";
            break;
        }
        player.type = type;
      }
    }

    return player;
  }

  /**
   * Reads the infos of a set.
   *
   * @param nr The number of the set (usually 1 - 5).
   * @param row The row where to start reading.
   * @param col The column where to start reading.
   * @return The data.
   */
  static SetInfo readSetInfos(int nr, int row, int col) {

    // result
    SetInfo set = null;

    // get first row
    XSSFRow r = SHEET.getRow(row++);
    if (r != null) {

      // get points for team a and b
      int ptA = getInt(r, col);
      int ptB = getInt(r, col + 1);
      if (ptA >= 0 || ptB >= 0) {

        // create data
        set = new SetInfo(nr, ptA >= 0);
        // add first point info
        set.addA(ptA);
        set.addB(ptB);
        do {
          // read next data row
          r = SHEET.getRow(row++);

          // read points of team a and store then
          ptA = getInt(r, col);
          if (ptA > 0) {
            set.addA(ptA);
          }

          // read points of team b and store then
          ptB = getInt(r, col + 1);
          if (ptB > 0) {
            set.addB(ptB);
          }

          // contine with reading the points as long as there are data
        } while (ptA > 0 && ptB > 0);
      }
    }

    // return result
    return set;
  }

  /**
   * Reads the actions.
   *
   * @param set The infos of a set.
   * @param row The row where to start reading.
   * @param col The column where to start reading.
   */
  static void readActions(SetInfo set, int row, int col) {
    XSSFRow r;

    do {
      // read next data row
      r = SHEET.getRow(row++);
      if (r != null) {

        // get info
        // if found, add them, otherwise stop searching for data
        String info = getStr(r, col);
        if (info.isEmpty()) {
          r = null;
        } else {
          set.add(info);
        }
      }

      // read as long as data can be found
    } while (r != null);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Utility functions to read from sheet.">
  /**
   * Reads a number.
   *
   * @param row The row of the cell to read.
   * @param column The column of the cell to read.
   * @return The read number or -1.
   */
  private static int getInt(XSSFRow row, int column) {

    // init with default value
    int val = -1;

    // get the cell to read from
    if (row != null) {
      XSSFCell cell = row.getCell(column);
      if (cell != null) {

        // get type of cell
        CellType type = cell.getCellType();

        // eval formula
        if (type == CellType.FORMULA) {
          type = evaluator.evaluateFormulaCell(cell);
        }

        // get numeric value
        if (type == CellType.NUMERIC) {
          val = (int) cell.getNumericCellValue();
        }
      }
    }

    // return result
    return val;
  }

  /**
   * Returns a content as string even if it is a number.
   *
   * @param row The row of the cell to read.
   * @param column The column of the cell to read.
   * @return The read value.
   */
  private static String getXNum(XSSFRow row, int column) {

    // init result
    String result = "";

    // get cell to read from
    XSSFCell cell = row.getCell(column);
    if (cell != null) {

      // try to evaluate (keeps value if not a formula)
      switch (evaluator.evaluateFormulaCell(cell)) {

        // read number and format it
        case NUMERIC:
          double dbl = cell.getNumericCellValue();
          DecimalFormat df = new DecimalFormat("#0.000");
          result = df.format(dbl);
          break;

        // read string
        case STRING:
          result = cell.getStringCellValue();
          break;

//        case CELL_TYPE_BOOLEAN:
//          System.out.println(cell.getBooleanCellValue());
//          break;
//        case Cell.CELL_TYPE_BLANK:
//          break;
//        case Cell.CELL_TYPE_ERROR:
//          System.out.println(cell.getErrorCellValue());
//          break;
//
//        // CELL_TYPE_FORMULA will never occur
//        case Cell.CELL_TYPE_FORMULA:
//          break;
      }
    }

    // return result
    return result;
  }

  /**
   * Returns the content of a cell as string.
   *
   * @param row The row of the cell to read.
   * @param column The column of the cell to read.
   * @return The read value.
   */
  private static String getStr(XSSFRow row, int column) {

    // init result
    String result = "";

    // get cell
    XSSFCell cell = row.getCell(column);
    if (cell != null) {

      // which type
      switch (cell.getCellType()) {

        // formula: get value
        case FORMULA:
          result = cell.getRawValue();
          break;

        // string: get value
        case STRING:
          result = cell.getStringCellValue();
          break;
      }
    }

    // return result
    return result;
  }
  //</editor-fold>
}
