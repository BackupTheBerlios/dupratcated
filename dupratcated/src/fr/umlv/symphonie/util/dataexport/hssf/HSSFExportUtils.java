/*
 * This file is part of Symphonie
 * Created : 17 mars 2005 18:32:26
 */

package fr.umlv.symphonie.util.dataexport.hssf;

import java.util.Comparator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import fr.umlv.symphonie.data.Student;

/**
 * Defines static methods for easyning hssf exportation.
 */
public final class HSSFExportUtils {

  /**
   * Sole constructor
   */
  private HSSFExportUtils() {
  }

  // ----------------------------------------------------------------------------
  // Static fields
  // ----------------------------------------------------------------------------

  /** Coefficient description key */
  public static final String COEFF_KEY = "hssfdataexporter.coeff";

  /** Mark description key */
  public static final String MARK_KEY = "hssfdataexporter.mark";

  /** Mark title description key */
  public static final String MARK_TITLE_KEY = "hssfdataexporter.marktitle";

  /** Course title description key */
  public static final String COURSE_TITLE_KEY = "hssfdataexporter.marktitletitle";

  /** Average mark description key */
  public static final String AVERAGE_KEY = "hssfdataexporter.average";
  
  /** Average mark description key */
  public static final String COMMENT_KEY = "hssfdataexporter.comment";

  /** <code>Comparator</code> instance that compares <code>Students</code> */
  public static final Comparator<Student> STUDENT_COMPARATOR = new Comparator<Student>() {

    public int compare(Student o1, Student o2) {
      int n = o1.getLastName().compareToIgnoreCase(o2.getLastName());

      if (n == 0) n = o1.getName().compareToIgnoreCase(o2.getName());

      if (n == 0) return o1.getId() - o2.getId();

      return n;
    }
  };

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
  public static final HSSFRow getRow(HSSFSheet sheet, int rowNumber) {
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
  public static final HSSFCell getCell(HSSFRow row, short cellIndex) {
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
  public static final HSSFCell getCell(HSSFSheet sheet, int row, short column) {
    return getCell(getRow(sheet, row), column);
  }

  /**
   * Retrieves the cell at point (row, column) from the given sheet and applies
   * a style to it. <br>
   * If the cell doesn't exist it will be created with the given style.
   * 
   * @param sheet
   *          the sheet
   * @param row
   *          the cell row
   * @param column
   *          the cell column
   * @param style
   *          the cell style
   * @return the cell at point (row, column)
   */
  public static final HSSFCell getCell(HSSFSheet sheet, int row, short column,
      HSSFCellStyle style) {
    HSSFCell cell = getCell(sheet, row, column);
    cell.setCellStyle(style);
    return cell;
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
  public static final String getCellReference(int row, short column) {
    return "$" + getColumnName(column) + "$" + (row + 1);
  }

  /**
   * Returns a default name for the column using spreadsheet conventions: A, B,
   * C, ... Z, AA, AB, etc.
   * 
   * @param column
   *          the column index
   * @return The column name
   */
  public static String getColumnName(int column) {
    String result = "";
    for (; column >= 0; column = column / 26 - 1)
      result = (char) ((char) (column % 26) + 'A') + result;
    return result;
  }

  /**
   * Sets the column width to the width of a given string, the column width is
   * extremely lazily calculated by (str.lenght + 2) * 256. If the width of
   * <code>str</code> is smaller than the current column width the width won't
   * be modified.
   * 
   * @param column
   *          the column index
   * @param sheet
   *          the column sheet
   * @param str
   *          the text
   */
  public static final void setColumnWidth(short column, HSSFSheet sheet,
      String str) {

    int currWidth = sheet.getColumnWidth(column);
    int desiredWidth = (str.length() + 1) * 256;
    if (currWidth < desiredWidth)
      sheet.setColumnWidth(column, (short) desiredWidth);
  }
}
