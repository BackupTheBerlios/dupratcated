/*
 * This file is part of Symphonie
 * Created : 16 mars 2005 21:09:50
 */

package fr.umlv.symphonie.util.dataexport.hssf;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.dataexport.DataExporter;
import fr.umlv.symphonie.util.dataexport.DataExporterException;

/**
 * Implementation of a <code>DataExporter</code> that writes Excel horrible
 * files. <br>
 * This implementations uses the jakarta POI library
 */
public class HSSFDataExporter implements DataExporter {

  /**
   * Builder for internationalization of generated horrible spreadsheets
   */
  private final ComponentBuilder bu;

  /**
   * Creates a new export with the given internationalization manager
   * 
   * @param bu
   *          The manager
   */
  public HSSFDataExporter(ComponentBuilder bu) {
    this.bu = bu;
  }

  public void exportStudentView(String documentName, DataManager dm, Student s)
      throws DataExporterException {

    // Try to retrieve data
    Map<Course, Map<Integer, StudentMark>> markMap = null;
    try {
      markMap = dm.getAllMarksByStudent(s);
    } catch (DataManagerException e) {
      throw new DataExporterException("Error while retrieving exportable data",
          e);
    }

    HSSFWorkbook wkbook = new HSSFWorkbook();
    HSSFSheet sheet = wkbook.createSheet(s.getLastName() + ", " + s.getName());
    HSSFCellStyle boldCell = wkbook.createCellStyle();
    HSSFFont bold = wkbook.createFont();
    bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    boldCell.setFont(bold);
    boldCell.setAlignment(HSSFCellStyle.ALIGN_CENTER);

    int maxCols = HSSFDataExporter.getMaxColumnIndex(markMap) + 2;

    int iRow = 0;
    short iCol;
    HSSFCell cell;
    Collection<StudentMark> marks = null;
    for (Course c : markMap.keySet()) {

      iCol = 0;
      marks = markMap.get(c).values();

      cell = HSSFDataExporter.getCell(sheet, iRow, iCol);
      cell.setCellStyle(boldCell);
      cell.setCellValue(c.toString());
      cell = HSSFDataExporter.getCell(sheet, iRow + 1, iCol);
      cell.setCellStyle(boldCell);
      cell.setCellValue(bu.getValue(HSSFDataExporter.COEFF_KEY));
      cell = HSSFDataExporter.getCell(sheet, iRow + 2, iCol);
      cell.setCellStyle(boldCell);
      cell.setCellValue(bu.getValue(HSSFDataExporter.MARK_KEY));
      iCol++;

      StringBuilder averageFormula = new StringBuilder("((");
      for (StudentMark mark : marks) {
        cell = HSSFDataExporter.getCell(sheet, iRow, iCol);
        cell.setCellStyle(boldCell);
        cell.setCellValue(mark.getMark().toString());

        averageFormula.append('(').append(
            HSSFDataExporter.getCellReference(iRow + 1, iCol)).append('*');
        HSSFDataExporter.getCell(sheet, iRow + 1, iCol).setCellValue(
            mark.getCoeff());
        averageFormula
            .append(HSSFDataExporter.getCellReference(iRow + 2, iCol)).append(
                ')');
        HSSFDataExporter.getCell(sheet, iRow + 2, iCol).setCellValue(
            mark.getValue());
        iCol++;
        if (iCol <= marks.size()) averageFormula.append('+');
      }
      averageFormula.append(")*100)/100");
      System.out.println(averageFormula);

      cell = HSSFDataExporter.getCell(sheet, iRow, (short) (maxCols));
      cell.setCellStyle(boldCell);
      cell.setCellValue(bu.getValue(HSSFDataExporter.AVERAGE_KEY));
      HSSFDataExporter.getCell(sheet, iRow + 2, (short) (maxCols))
          .setCellFormula(averageFormula.toString());

      iRow += 4;
    }

    try {
      FileOutputStream fos = new FileOutputStream(documentName);
      wkbook.write(fos);
    } catch (FileNotFoundException e1) {
      throw new DataExporterException("Error while accessing file "
          + documentName, e1);
    } catch (IOException e1) {
      throw new DataExporterException(
          "Error while writting generated spreadsheet", e1);
    }
  }

  public void exportTeacherView(String documentName, DataManager dm, Course c)
      throws DataExporterException {
    throw new UnsupportedOperationException();
  }

  public void exportJuryView(String documentName, DataManager dm)
      throws DataExporterException {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------------
  // Static fields
  // ----------------------------------------------------------------------------

  /** Coefficient description key */
  public static final String COEFF_KEY = "hssfdataexporter.coeff";

  /** Mark description key */
  public static final String MARK_KEY = "hssfdataexporter.mark";

  /** Average mark description key */
  public static final String AVERAGE_KEY = "hssfdataexporter.average";

  // ----------------------------------------------------------------------------
  // Static methods
  // ----------------------------------------------------------------------------

  /**
   * Attempts to retrieve a row from the given sheet, if the row doesn't exist
   * it will be created. <br>
   * Row indexes start at 0
   * 
   * @param sheet
   *          The horrible sheet
   * @param rowNumber
   *          The row number
   * @return a horrible spreadsheet row
   */
  private static final HSSFRow getRow(HSSFSheet sheet, int rowNumber) {
    HSSFRow row = sheet.getRow(rowNumber);
    return (row == null) ? sheet.createRow(rowNumber) : row;
  }

  /**
   * Attempts to retrieve a cell from the given row, if the cell doesn't exist
   * it will be created. <br>
   * Cell indexes start at 0
   * 
   * @param row
   *          The horrible row
   * @param cellIndex
   *          The cell index
   * @return a horrible spreadsheet cell
   */
  private static final HSSFCell getCell(HSSFRow row, short cellIndex) {
    HSSFCell cell = row.getCell(cellIndex);
    return (cell == null) ? row.createCell(cellIndex) : cell;
  }

  /**
   * Retrieves the cell at point (row, column) from the given sheet
   * 
   * @param sheet
   *          the sheet
   * @param row
   *          the cell row
   * @param column
   *          the cell column
   * @return the cell at point (row, column)
   */
  private static final HSSFCell getCell(HSSFSheet sheet, int row, short column) {
    return HSSFDataExporter
        .getCell(HSSFDataExporter.getRow(sheet, row), column);
  }

  /**
   * Returns the cell reference in excel format ex : for cell (1, 1) will return
   * $A$1. <br>
   * Both rows and columns start at 0.
   * 
   * @param row
   *          the row number
   * @param column
   *          the column number
   * @return the string reference
   */
  private static final String getCellReference(int row, short column) {
    return "$" + HSSFDataExporter.getColumnName(column) + "$" + (row + 1);
  }

  /**
   * Returns a default name for the column using spreadsheet conventions: A, B,
   * C, ... Z, AA, AB, etc.
   * 
   * @param column
   *          the column index
   * @return The column name
   */
  private static String getColumnName(int column) {
    String result = "";
    for (; column >= 0; column = column / 26 - 1)
      result = (char) ((char) (column % 26) + 'A') + result;
    return result;
  }

  /**
   * Calculates the max column index needed to correctly display all the marks
   * 
   * @param marks
   *          The marks map
   * @return the biggest index
   */
  private static final int getMaxColumnIndex(
      Map<Course, Map<Integer, StudentMark>> marks) {
    int n = 0;
    int tmp = 0;
    for (Course c : marks.keySet())
      if ((n = marks.get(c).size()) > tmp) tmp = n;
    return n;
  }

  public static void main(String[] args) throws DataManagerException,
      DataExporterException {
    DataManager dm = new SQLDataManager();
    Student s = dm.getStudentList().get(2);
    HashMap<String, String> peuma = new HashMap<String, String>();
    peuma.put(HSSFDataExporter.COEFF_KEY, "Coeficiente");
    peuma.put(HSSFDataExporter.AVERAGE_KEY, "Promedio");
    peuma.put(HSSFDataExporter.MARK_KEY, "Nota");
    new HSSFDataExporter(new ComponentBuilder(peuma)).exportStudentView(
        "C:\\test.xls", dm, s);
  }
}
