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

  private static final String FN = "D:/workdir/brueckl-hotvolleys-source/_work/Statistik.xlsx";
  private static final String SHEET_NAME = "Spiele";

  private static FormulaEvaluator evaluator;
  private static XSSFSheet SHEET;
  private static final List<Match> DATA = new ArrayList<>();
  private static int ROW = 0;

  /**
   * The entry point.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    try {
      // open file
      FileInputStream file = new FileInputStream(new File(FN));

      // get workbook from file content
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      evaluator = workbook.getCreationHelper().createFormulaEvaluator();

      // get sheet
      SHEET = workbook.getSheet(SHEET_NAME);
      if (SHEET == null) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Cannot find sheet " + SHEET_NAME + "!");
        workbook.close();
        file.close();
        return;
      }

      Match match;
      do {
        match = readMatch();
        if (match != null) {
          DATA.add(match);
        }
      } while (match != null);

      Generator generator = new Generator(DATA);
      generator.create();

      // clean up
      workbook.close();
      file.close();

    } catch (FileNotFoundException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  static Match readMatch() {
    int startRow = ROW;
    Match match = null;
    XSSFRow row = null;
    Cell cell0 = null;
    boolean search = true;

    while (search) {
      cell0 = null;
      search = false;
      row = SHEET.getRow(ROW);
      if (row != null) {
        cell0 = row.getCell(0);
        if (cell0 != null) {
          CellType type = cell0.getCellType();
          if (type == CellType.STRING) {
            String cur = cell0.getStringCellValue();
            if (cur.equals(".")) {
              ++ROW;
              ++startRow;
              search = true;
            }
          }
        }
      }
    }

    if (cell0 != null && cell0.getCellType() != CellType.BLANK) {
      match = new Match();

      Date date = cell0.getDateCellValue();
      DateFormat formatter = new SimpleDateFormat("dd.MMM");
      match.date = formatter.format(date);
      match.info = row.getCell(1).getStringCellValue();

      ROW += 3;
      Player player;
      do {
        player = readPlayer();
        if (player != null) {
          match.players.add(player);
        }
      } while (player != null);

      startRow += 3;
      int col = 18;
      SetPoints sp;
      do {
        sp = readPoints(startRow, col);
        if (sp != null) {
          match.addPoints(sp);
          col += 2;
        }
      } while (sp != null);

      System.out.println("match: " + match.date + " " + match.info);
    }

    // System.out.println(match.toString());
    return match;
  }

  static Player readPlayer() {
    Player player = null;

    //System.out.println(ROW);
    XSSFRow row = SHEET.getRow(ROW++);
    if (row != null) {
      String type = "none";
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
      if (t0 != CellType.BLANK || t1 != CellType.BLANK) {
        player = new Player();
        player.name = t0 == CellType.STRING ? cell0.getStringCellValue() : "";
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

  static SetPoints readPoints(int row, int col) {
    SetPoints sp = null;

    XSSFRow r = SHEET.getRow(row++);
    if (r != null) {
      int ptA = getInt(r, col);
      int ptB = getInt(r, col + 1);
      if (ptA >= 0 || ptB >= 0) {
        sp = new SetPoints(ptA >= 0);
        sp.addA(Math.max(ptA, 0));
        sp.addB(Math.max(ptB, 0));
        do {
          r = SHEET.getRow(row++);

          ptA = getInt(r, col);
          if (ptA > 0) {
            sp.addA(ptA);
          }

          ptB = getInt(r, col + 1);
          if (ptB > 0) {
            sp.addB(ptB);
          }

        } while (ptA > 0 && ptB > 0);
      }
    }

    return sp;
  }

  static int getInt(XSSFRow row, int col) {
    int val = -1;

    if (row != null) {
      XSSFCell cell = row.getCell(col);
      if (cell != null) {
        CellType type = cell.getCellType();
        if (type == CellType.FORMULA) {
          type = evaluator.evaluateFormulaCell(cell);
        }
        if (type == CellType.NUMERIC) {
          val = (int) cell.getNumericCellValue();
        }
      }
    }

    return val;
  }

  static String getXNum(XSSFRow row, int col) {
    String result = "";

    XSSFCell cell = row.getCell(col);
    if (cell != null) {
      switch (evaluator.evaluateFormulaCell(cell)) {
        case NUMERIC:
          double dbl = cell.getNumericCellValue();
          DecimalFormat df = new DecimalFormat("#0.000");
          result = df.format(dbl);
          break;
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

    return result;
  }

  static String getStr(XSSFRow row, int col) {
    String val = "";

    XSSFCell cell = row.getCell(col);
    if (cell != null && cell.getCellType() == CellType.FORMULA) {
      val = cell.getRawValue();
    }

    return val;
  }
}
