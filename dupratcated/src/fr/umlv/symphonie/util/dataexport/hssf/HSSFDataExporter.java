/*
 * This file is part of Symphonie
 * Created : 16 mars 2005 21:09:50
 */

package fr.umlv.symphonie.util.dataexport.hssf;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.data.formula.SymphonieFormulaFactory;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.ExceptionDisplayDialog;
import fr.umlv.symphonie.util.Pair;
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

  // ----------------------------------------------------------------------------
  // DataExporter interface implementation
  // ----------------------------------------------------------------------------

  public void exportStudentView(String documentName, DataManager dm, Student s)
      throws DataExporterException {

    // Try to retrieve data
    Map<Course, Map<Integer, StudentMark>> markMap = null;
    try {
      markMap = dm.getAllMarksByStudent(s);
    } catch (DataManagerException e) {
      throw new DataExporterException(
          "Error while retrieving exportable data for student " + s, e);
    }

    // Create workbook and sheet
    HSSFWorkbook wkbook = new HSSFWorkbook();
    HSSFSheet sheet = wkbook.createSheet(s.getLastName() + ", " + s.getName());

    // Create cell styles
    HSSFCellStyle twoDecimalsStyle = wkbook.createCellStyle();
    twoDecimalsStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));

    HSSFCellStyle boldCell = wkbook.createCellStyle();
    HSSFFont bold = wkbook.createFont();
    bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    boldCell.setFont(bold);
    boldCell.setAlignment(HSSFCellStyle.ALIGN_CENTER);

    // Calculate max column count
    int maxCols = getMaxColumnIndex(markMap) + 2;

    int iRow = 0;
    short iCol;
    HSSFCell cell;
    Collection<StudentMark> marks = null;
    String strValue;

    // Put all courses in sheet
    for (Course c : markMap.keySet()) {

      iCol = 0;
      marks = markMap.get(c).values();

      // Course title cell
      strValue = c.toString();
      cell = HSSFExportUtils.getCell(sheet, iRow, iCol, boldCell);
      cell.setCellValue(strValue);
      HSSFExportUtils.setColumnWidth(iCol, sheet, strValue);

      // Coeff title cell
      strValue = bu.getValue(HSSFExportUtils.COEFF_KEY);
      cell = HSSFExportUtils.getCell(sheet, iRow + 1, iCol, boldCell);
      cell.setCellValue(strValue);
      HSSFExportUtils.setColumnWidth(iCol, sheet, strValue);

      // Mark title cell
      strValue = bu.getValue(HSSFExportUtils.MARK_KEY);
      cell = HSSFExportUtils.getCell(sheet, iRow + 2, iCol, boldCell);
      cell.setCellValue(strValue);
      HSSFExportUtils.setColumnWidth(iCol, sheet, strValue);

      iCol++;

      StringBuilder averageFormula = new StringBuilder();

      // Put marks' values
      for (StudentMark mark : marks) {

        // Mark title
        strValue = mark.getMark().toString();
        cell = HSSFExportUtils.getCell(sheet, iRow, iCol, boldCell);
        cell.setCellValue(strValue);
        HSSFExportUtils.setColumnWidth(iCol, sheet, strValue);

        averageFormula.append('(');
        averageFormula.append(HSSFExportUtils.getCellReference(iRow + 1, iCol));
        averageFormula.append('*');

        // Coeff value
        cell = HSSFExportUtils.getCell(sheet, iRow + 1, iCol, twoDecimalsStyle);
        cell.setCellValue(mark.getCoeff());

        averageFormula.append(HSSFExportUtils.getCellReference(iRow + 2, iCol));
        averageFormula.append(')');

        // Mark value
        cell = HSSFExportUtils.getCell(sheet, iRow + 2, iCol, twoDecimalsStyle);
        cell.setCellValue(mark.getValue());

        iCol++;

        if (iCol <= marks.size()) averageFormula.append('+');
      }

      // Put average title
      strValue = bu.getValue(HSSFExportUtils.AVERAGE_KEY);
      cell = HSSFExportUtils.getCell(sheet, iRow, (short) (maxCols), boldCell);
      cell.setCellValue(strValue);
      HSSFExportUtils.setColumnWidth((short) maxCols, sheet, strValue);

      // Put average formula
      cell = HSSFExportUtils.getCell(sheet, iRow + 2, (short) (maxCols),
          twoDecimalsStyle);
      cell.setCellFormula(averageFormula.toString());

      iRow += 4;
    }

    // Write workbook on disc
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

    try {

      // Retrieve data
      Pair<Map<Integer, Mark>, SortedMap<Student, Map<Integer, StudentMark>>> studentAndMarkPair = dm
          .getAllMarksByCourse(c);

      HashMap<Integer, Mark> marx = new HashMap<Integer, Mark>();
      marx.putAll(studentAndMarkPair.getFirst());
      SortedMap<Student, Map<Integer, StudentMark>> studentMarx = studentAndMarkPair
          .getSecond();
      studentMarx.putAll(studentAndMarkPair.getSecond());

      ArrayList<Object> columns = new ArrayList<Object>(marx.values());
      List<Formula> formulas;
      try {
        formulas = dm.getFormulasByCourse(c);
      } catch (DataManagerException e1) {
        formulas = null;
      }

      if (formulas != null)
        HSSFDataExporter.insertFormulaColumns(columns, formulas);

      int rowCount = studentMarx.size() + 3;
      short columnCount = (short) (marx.size() + 2 + formulas.size());

      short[] formulaColumns = new short[formulas.size()];
      short[] markColumns = new short[marx.values().size()];
      ArrayList<Pair<String, Short>> columNames = new ArrayList<Pair<String, Short>>();

      int iFormula = 0;
      int iMark = 0;
      short iCol = 1;
      for (Object o : columns) {
        if (o instanceof Formula) {
          formulaColumns[iFormula++] = iCol;
          columNames.add(new Pair<String, Short>(
              ((Formula) o).getDescription(), iCol));
        } else if (o instanceof Mark) {
          markColumns[iMark++] = iCol;
          columNames.add(new Pair<String, Short>(((Mark) o).getDesc(), iCol));
        } else
          throw new DataExporterException(new IllegalStateException(
              "Invalid column object"));
        iCol++;
      }

      // Create workbook and sheet
      HSSFWorkbook wkbook = new HSSFWorkbook();
      HSSFSheet sheet = wkbook.createSheet(c.getTitle());

      // Create cell styles
      HSSFCellStyle twoDecimalsStyle = wkbook.createCellStyle();
      twoDecimalsStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
      HSSFCellStyle boldCell = wkbook.createCellStyle();
      HSSFFont bold = wkbook.createFont();
      bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
      boldCell.setFont(bold);
      boldCell.setAlignment(HSSFCellStyle.ALIGN_CENTER);

      // Create header
      String strValue = bu.getValue(HSSFExportUtils.MARK_TITLE_KEY);
      HSSFExportUtils.getCell(sheet, 0, (short) 0, boldCell).setCellValue(
          strValue);
      HSSFExportUtils.setColumnWidth((short) 0, sheet, strValue);

      strValue = bu.getValue(HSSFExportUtils.COEFF_KEY);
      HSSFExportUtils.getCell(sheet, 1, (short) 0, boldCell).setCellValue(
          strValue);
      HSSFExportUtils.setColumnWidth((short) 0, sheet, strValue);

      strValue = bu.getValue(HSSFExportUtils.AVERAGE_KEY);
      HSSFExportUtils.getCell(sheet, 0, (short) (columnCount - 1), boldCell)
          .setCellValue(strValue);
      HSSFExportUtils.setColumnWidth((short) 0, sheet, strValue);

      // Put students
      int iRow = 3;
      for (Student s : studentMarx.keySet()) {
        strValue = s.getLastName() + ", " + s.getName();
        HSSFExportUtils.getCell(sheet, iRow++, (short) 0, boldCell)
            .setCellValue(strValue);
        HSSFExportUtils.setColumnWidth((short) 0, sheet, strValue);
      }

      // Put marks
      Mark m;
      for (short col : markColumns) {
        m = (Mark) columns.get(col - 1);

        // Put mark title
        HSSFExportUtils.getCell(sheet, 0, col, boldCell).setCellValue(
            m.getDesc());
        HSSFExportUtils.setColumnWidth(col, sheet, m.getDesc());

        // Put mark coeff
        HSSFExportUtils.getCell(sheet, 1, col, twoDecimalsStyle).setCellValue(
            m.getCoeff());

        // Put values
        iRow = 3;
        for (Map<Integer, StudentMark> mism : studentMarx.values()) {
          HSSFExportUtils.getCell(sheet, iRow++, col, twoDecimalsStyle)
              .setCellValue(mism.get(m.getId()).getValue());
        }
      }

      // Put formulas
      HashMap<String, String> referenceMap = new HashMap<String, String>();
      iRow = 3;
      String excelFormula;
      for (short col : formulaColumns) {

        // Put formula title
        Formula f = (Formula) columns.get(col - 1);
        HSSFExportUtils.getCell(sheet, 0, col, boldCell).setCellValue(
            f.getDescription());
        HSSFExportUtils.setColumnWidth(col, sheet, f.getDescription());

        // Fill cells
        for (; iRow < rowCount; iRow++) {
          // Put excel references in a map
          referenceMap.clear();
          for (Pair<String, Short> p : columNames) {
            referenceMap.put(p.getFirst(), HSSFExportUtils.getCellReference(
                iRow, p.getSecond().shortValue()));
          }

          // Convert formula
          excelFormula = FormulaConverter.toExcelString(f, referenceMap);

          // Put the formula
          HSSFExportUtils.getCell(sheet, iRow, col, twoDecimalsStyle)
              .setCellFormula(excelFormula);
        }
      }

      // Make average mark columns
      iRow = 3;
      iCol = (short) (columnCount - 1);
      String averageFormula;
      for (; iRow < rowCount; iRow++) {
        averageFormula = HSSFDataExporter.getAverageFormula(iRow, markColumns);
        HSSFExportUtils.getCell(sheet, iRow, iCol, twoDecimalsStyle)
            .setCellFormula(averageFormula);
      }

      // Write workbook on disc
      FileOutputStream fos = new FileOutputStream(documentName);
      wkbook.write(fos);

    } catch (FileNotFoundException e) {
      throw new DataExporterException("Error while accessing file "
          + documentName, e);
    } catch (DataManagerException e) {
      throw new DataExporterException(
          "Error while retrieving exportable data for course " + c, e);
    } catch (IOException e) {
      throw new DataExporterException(
          "Error while writting generated spreadsheet", e);
    }

  }

  public void exportJuryView(String documentName, DataManager dm)
      throws DataExporterException {

    try {

      // Set up data
      Pair<Map<Integer, Course>, SortedMap<Student, Map<Course, Map<Integer, StudentMark>>>> allData = dm
          .getAllStudentsMarks();
      ArrayList<Object> columns = new ArrayList<Object>(allData.getFirst()
          .values());
      HashMap<Student, Map<Course, Map<Integer, StudentMark>>> data = new HashMap<Student, Map<Course, Map<Integer, StudentMark>>>(
          allData.getSecond());
      ArrayList<Student> students = new ArrayList<Student>(data.keySet());

      // Get formulas
      List<Formula> formulas;
      try {
        formulas = dm.getJuryFormulas();
      } catch (DataManagerException e2) {
        formulas = null;
      }

      if (formulas != null)
        HSSFDataExporter.insertFormulaColumns(columns, formulas);

      // Create workbook and sheet
      HSSFWorkbook wkbook = new HSSFWorkbook();
      HSSFSheet sheet = wkbook.createSheet(bu.getValue("jury"));

      // Create cell styles
      HSSFCellStyle twoDecimalsStyle = wkbook.createCellStyle();
      twoDecimalsStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
      HSSFCellStyle boldCell = wkbook.createCellStyle();
      HSSFFont bold = wkbook.createFont();
      bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
      boldCell.setFont(bold);
      boldCell.setAlignment(HSSFCellStyle.ALIGN_CENTER);

      // Set up necessary data
      short[] formulaColumns = new short[formulas.size()];
      HashMap<Course, Short> courseColumns = new HashMap<Course, Short>();
      ArrayList<Pair<String, Short>> columNames = new ArrayList<Pair<String, Short>>();

      int iFormula = 0;
      short iCol = 1;
      String strValue;
      for (Object o : columns) {
        if (o instanceof Formula) {
          formulaColumns[iFormula++] = iCol;
          strValue = ((Formula) o).getDescription();
        } else if (o instanceof Course) {
          courseColumns.put((Course) o, iCol);
          strValue = ((Course) o).getTitle();
          HSSFExportUtils.getCell(sheet, 1, iCol, twoDecimalsStyle)
              .setCellValue(((Course) o).getCoeff());
        } else
          throw new DataExporterException(new IllegalStateException(
              "Invalid column object"));

        columNames.add(new Pair<String, Short>(strValue, iCol));

        // Put headers at the same time
        HSSFExportUtils.getCell(sheet, 0, iCol, boldCell)
            .setCellValue(strValue);
        HSSFExportUtils.setColumnWidth(iCol, sheet, strValue);

        iCol++;
      }

      short columnCount = (short) (columns.size() + 4);
      int rowCount = 3 + students.size();

      // Create header
      strValue = bu.getValue(HSSFExportUtils.COURSE_TITLE_KEY);
      HSSFExportUtils.getCell(sheet, 0, (short) 0, boldCell).setCellValue(
          strValue);
      HSSFExportUtils.setColumnWidth((short) 0, sheet, strValue);

      strValue = bu.getValue(HSSFExportUtils.COEFF_KEY);
      HSSFExportUtils.getCell(sheet, 1, (short) 0, boldCell).setCellValue(
          strValue);
      HSSFExportUtils.setColumnWidth((short) 0, sheet, strValue);

      strValue = bu.getValue(HSSFExportUtils.AVERAGE_KEY);
      HSSFExportUtils.getCell(sheet, 0, (short) (columnCount - 3), boldCell)
          .setCellValue(strValue);
      HSSFExportUtils
          .setColumnWidth((short) (columnCount - 3), sheet, strValue);
      
      strValue = bu.getValue(HSSFExportUtils.COMMENT_KEY);
      HSSFExportUtils.getCell(sheet, 0, (short) (columnCount - 1), boldCell)
          .setCellValue(strValue);
      HSSFExportUtils
          .setColumnWidth((short) (columnCount - 1), sheet, strValue);

      // Put students columns
      int iRow = 3;
      Map<Course, Map<Integer, StudentMark>> sData;
      float average;
      StringBuilder globalAverage;
      int courseCount;
      Set<Course> courseSet;
      for (Student s : students) {

        strValue = s.getLastName() + ", " + s.getName();
        HSSFExportUtils.getCell(sheet, iRow, (short) 0, boldCell).setCellValue(
            strValue);
        HSSFExportUtils.setColumnWidth((short) 0, sheet, strValue);
        HSSFExportUtils.getCell(sheet, iRow, (short) (columnCount - 2),
            boldCell).setCellValue(strValue);
        HSSFExportUtils.setColumnWidth((short) (columnCount - 2), sheet,
            strValue);

        strValue = s.getComment();
        HSSFExportUtils.getCell(sheet, iRow, (short) (columnCount - 1))
            .setCellValue(strValue);
        HSSFExportUtils.setColumnWidth((short) (columnCount - 1), sheet,
            strValue);

        // Put average marks
        sData = data.get(s);
        globalAverage = new StringBuilder();
        courseSet = courseColumns.keySet();
        courseCount = courseSet.size();
        for (Course c : courseSet) {
          courseCount--;
          iCol = courseColumns.get(c).shortValue();
          globalAverage.append('(');
          average = HSSFDataExporter.getAverageValue(sData.get(c).values());
          globalAverage.append(HSSFExportUtils.getCellReference(iRow, iCol));
          HSSFExportUtils.getCell(sheet, iRow, iCol, twoDecimalsStyle)
              .setCellValue(average);
          globalAverage.append('*');
          globalAverage.append(HSSFExportUtils.getCellReference(1, iCol));
          globalAverage.append(')');
          if (courseCount != 0) globalAverage.append('+');
        }

        HSSFExportUtils.getCell(sheet, iRow, (short) (columnCount - 3),
            twoDecimalsStyle).setCellFormula(globalAverage.toString());

        iRow++;
      }

      // Put formulas
      HashMap<String, String> referenceMap = new HashMap<String, String>();
      iRow = 3;
      String excelFormula;
      Formula f;
      for (short col : formulaColumns) {

        f = (Formula) columns.get(col - 1);

        // Fill cells
        for (; iRow < rowCount; iRow++) {

          // Put excel references in a map
          referenceMap.clear();
          for (Pair<String, Short> p : columNames) {
            referenceMap.put(p.getFirst(), HSSFExportUtils.getCellReference(
                iRow, p.getSecond().shortValue()));
          }

          // Convert formula
          excelFormula = FormulaConverter.toExcelString(f, referenceMap);

          // Put the formula
          HSSFExportUtils.getCell(sheet, iRow, col, twoDecimalsStyle)
              .setCellFormula(excelFormula);
        }
      }
      
      // Write workbook on disc
      FileOutputStream fos = new FileOutputStream(documentName);
      wkbook.write(fos);

    } catch (FileNotFoundException e) {
      throw new DataExporterException("Error while accessing file "
          + documentName, e);
    } catch (DataManagerException e) {
      throw new DataExporterException(
          "Error while retrieving exportable data for jury", e);
    } catch (IOException e) {
      throw new DataExporterException(
          "Error while writting generated spreadsheet", e);
    }
  }

  // ----------------------------------------------------------------------------
  // Static methods
  // ----------------------------------------------------------------------------

  /**
   * Returns the average mark of a <code>Collection</code> of
   * <code>StudentMark</code>
   * 
   * @param marx
   *          The marks
   * @return The average mark
   */
  private static final float getAverageValue(Collection<StudentMark> marx) {
    float result = 0;
    for (StudentMark sm : marx) {
      result += sm.getValue() * sm.getCoeff();
    }
    return result;
  }

  /**
   * Creates a formula that calculates the average mark of the given row
   * 
   * @param row
   *          The sheet row
   * @param markColumns
   *          The columns that are to be used for calculating the average
   * @return An excel formula
   */
  private static String getAverageFormula(int row, short[] columns) {
    StringBuilder formula = new StringBuilder();
    for (int i = columns.length - 1; i >= 0; i--) {
      formula.append('(');
      formula.append(HSSFExportUtils.getCellReference(row, columns[i]));
      formula.append('*').append(
          HSSFExportUtils.getCellReference(1, columns[i]));
      formula.append(')');
      if (i != 0) formula.append('+');
    }
    return formula.toString();
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

  /**
   * Insert formulas by column number in the given list of objects that
   * represent columns
   * 
   * @param columns
   *          The column list
   * @param formulas
   *          The formula list
   */
  private static final void insertFormulaColumns(ArrayList<Object> columns,
      List<Formula> formulas) {
    int formulaColumn;
    for (Formula f : formulas) {
      formulaColumn = f.getColumn();
      if (formulaColumn < 0)
        columns.add(0, f);
      else if (formulaColumn > columns.size())
        columns.add(f);
      else
        columns.add(formulaColumn - 1, f);
    }
  }

  public static void main(String[] args) {
    ComponentBuilder b = null;
    try {
      SymphonieFormulaFactory.parseFormula("fcuk", new String("${examen}*2"
          .getBytes(), "ascii"), 45, 2);
      DataManager dm = SQLDataManager.getInstance();
      Course c = dm.getCoursesList().get(1);
      HashMap<String, String> peuma = new HashMap<String, String>();
      peuma.put(HSSFExportUtils.COEFF_KEY, "Coefficient");
      peuma.put(HSSFExportUtils.AVERAGE_KEY, "Moyenne");
      peuma.put(HSSFExportUtils.MARK_KEY, "Note");
      peuma.put(HSSFExportUtils.MARK_TITLE_KEY, "Intitulé");
      peuma.put(HSSFExportUtils.COURSE_TITLE_KEY, "Matière");
      peuma.put(HSSFExportUtils.COMMENT_KEY, "Lâche tes comms");
      peuma
          .put("exceptiondialog.message", "Une erreur inopinée est survenue :");
      peuma.put("exceptiondialog.hidedetail", "<< Détails");
      peuma.put("exceptiondialog.showdetail", "Détails >>");
      peuma.put("bok", "OK");
      peuma.put("jury", "Petit Pois Grand-Jury");
      b = new ComponentBuilder(peuma);
      new HSSFDataExporter(b).exportJuryView("/home/mif001/spenasal/jury.xls",
          dm);
    } catch (Exception e) {
      e.printStackTrace();
      new ExceptionDisplayDialog(null, b).showException(e);
    }
  }
}
