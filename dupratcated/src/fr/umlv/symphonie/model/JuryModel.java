/*
 * This file is part of Symphonie
 * Created : 27-févr.-2005 15:26:50
 */

package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.table.AbstractTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.data.formula.SymphonieFormulaFactory;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.ExceptionDisplayDialog;
import fr.umlv.symphonie.util.LookableCollection;
import fr.umlv.symphonie.util.Pair;
import fr.umlv.symphonie.util.StudentAverage;
import fr.umlv.symphonie.util.completion.CompletionDictionary;
import fr.umlv.symphonie.util.completion.IDictionarySupport;
import fr.umlv.symphonie.view.cells.CellRendererFactory;
import fr.umlv.symphonie.view.cells.FormattableCellRenderer;
import fr.umlv.symphonie.view.cells.ObjectFormattingSupport;

import static fr.umlv.symphonie.view.SymphonieConstants.*;

/**
 * The model which represents jury's view
 * @author susmab
 * 
 */
/**
 * @author MARYSE
 *
 */
public class JuryModel extends AbstractTableModel implements ObjectFormattingSupport, IDictionarySupport {

  /**
   * The DataManager which handles database
   */
  protected final DataManager manager;
  
  /**
   * The ComponentBuilder, used to internationalize the current model.
   */
  protected final ComponentBuilder builder;

  /**
   * Number of rows currently in the model
   */
  protected int rowCount = 0;
  
  /**
   * Number of columns currently in the model
   */
  protected int columnCount = 0;
  
  /**
   * Used to check timestamps.
   */
  protected int globalTimeStamp = -1; // le globalTimeStamp pourrait etre la
                                      // somme de
  // tous les timeStamp (a voir)

  /**
   * The list of columns in the model, except the first and the last ones.
   * It can contain <code>Formulas</code>s and <code>Course</code>s
   */
  protected final List<Object> columnList = new ArrayList<Object>();
  
  /**
   * The list of <code>Student</code>. Used to represent lines of the model.
   */
  protected final List<Student> studentList = new ArrayList<Student>();
  
  /**
   * The map containing all students, and all marks of each student for each course.
   */
  protected final Map<Student, Map<Course, Map<Integer, StudentMark>>> dataMap = new HashMap<Student, Map<Course, Map<Integer, StudentMark>>>();

  /**
   * A map containing all courses, keyed by their id.
   */
  protected final Map<Integer, Course> courseMap = new HashMap<Integer, Course>();

  /**
   * Pool of only one thread. Used to launch theads intercating with the database.
   */
  protected final ExecutorService es = Executors.newSingleThreadExecutor();

  /**
   * An object used to be locked by each thread launched,
   * in order not to generate errors while interacting with the database.
   */
  protected final Object lock = new Object();

//  private static JuryModel instance = null;

  /**
   * Used in the <code>getValueAt</code> method, to check which row is being accessed.
   */
  private int lastRow = -1;
  
  /**
   * A <code>CompletionDictionary</code> used in order to provide auto-completion with the model.
   */
  protected CompletionDictionary dictionary = new CompletionDictionary();
  
  /**
   * Constructs an empty <code>JuryModel</code>.
   * @param manager The <code>DataManager</code> which will be used to interact with database.
   * @param builder The <code>ComponentBuilder</code> which will provide internationalization.
   */
  public JuryModel(DataManager manager, ComponentBuilder builder) {
    this.manager = manager;
    this.builder = builder;
    
    fillDefaultDictionary();
    
    update();
  }

  /**
   * Fill the <code>CompletionDictionary</code> with default key words.
   */
  private void fillDefaultDictionary() {
    dictionary.add("average");
    dictionary.add("min");
    dictionary.add("max");
  }


  /**
   * Updates the data in the model.
   */
  public void update() {

    es.execute(new Runnable() {

      public void run() {

        synchronized (lock) {

          clear();

          Pair<Map<Integer, Course>, SortedMap<Student, Map<Course, Map<Integer, StudentMark>>>> allData = null;
          try {
            allData = manager.getAllStudentsMarks();
          } catch (DataManagerException e) {
            ExceptionDisplayDialog.postException(e);
            return;
          }

          columnList.addAll(allData.getFirst().values());
          studentList.addAll(allData.getSecond().keySet());
          dataMap.putAll(allData.getSecond());
          courseMap.putAll(allData.getFirst());
          
          for (Course c : courseMap.values())
            dictionary.add(c.getTitle());
          

          List<Formula> formulaList;

          try {
            formulaList = manager.getJuryFormulas();
          } catch (DataManagerException e) {
            ExceptionDisplayDialog.postException(e);
            formulaList = null;
          }

          if (formulaList != null) {

            int column;

            for (Formula f : formulaList) {
              column = f.getColumn();
              if (column < 0)
                columnList.add(0, f);

              else if (column > columnList.size())
                columnList.add(f);

              else
                columnList.add(column - 1, f);
            }

          }

          dataMap.putAll(allData.getSecond());

          columnCount = columnList.size() + 4;
          rowCount = 3 + studentList.size();

          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                JuryModel.this.fireTableStructureChanged();
              }
            });
          } catch (InterruptedException e) {
              ExceptionDisplayDialog.postException(e);
              return;
          } catch (InvocationTargetException e) {
            ExceptionDisplayDialog.postException(e);
            return;
          }
        }
      }
    });

  }

  /* (non-Javadoc)
   * @see fr.umlv.symphonie.util.completion.IDictionarySupport#getDictionary()
   */
  public LookableCollection<String> getDictionary() {
    return dictionary;
  }

  /**
   * Throws an <code>UnsupportedOperationException</code>
   * 
   * @param dictionary
   *          Useless
   */
  public void setDictionary(LookableCollection<String> dictionary) {
    throw new UnsupportedOperationException("Cannot override dictionary");
  }
  
  /**
   * Clears the model.
   */
  public void clear() {
    rowCount = 0;
    columnCount = 0;
    columnList.clear();
    studentList.clear();
    dataMap.clear();
    
    for (Course c : courseMap.values())
      dictionary.remove(c.getTitle());
    
    courseMap.clear();
    
    fireTableStructureChanged();
  }

  /**
   * (Returns the number of rows currently in the model.
   * @see javax.swing.table.TableModel#getRowCount()
   */
  public int getRowCount() {
    return rowCount;
  }

  /**
   * Returns the number of columns currently int the model.
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  public int getColumnCount() {
    return columnCount;
  }

  /**
   * Returns values contained in the model.
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  public Object getValueAt(int rowIndex, int columnIndex) {

    /*
     * case of the separate line
     */
    if (rowIndex == 2) return null;

    fillFormulaMap(rowIndex);
    
    /*
     * case of the first column
     */
    if (columnIndex == 0) {
      if (rowIndex == 0) return builder.getValue(COURSE);
      if (rowIndex == 1) return builder.getValue(COEFF);
      return studentList.get(rowIndex - 3);
    }

    /*
     * case of the last column (comments)
     */
    if (columnIndex == columnCount - 1) {
      if (rowIndex == 0) return builder.getValue(COMMENT);
      if (rowIndex == 1) return null;

      return studentList.get(rowIndex - 3).getComment();
    }

    /*
     * case of the before-last column (students)
     */
    if (columnIndex == columnCount - 2) {
      if (rowIndex <= 1) return null;

      return studentList.get(rowIndex - 3);
    }

    /*
     * case of the averages column (before-before-last column)
     */

    if (columnIndex == columnCount - 3) {
      if (rowIndex == 0) return builder.getValue(AVERAGE);
      if (rowIndex <= 2) return null;

      return StudentAverage.getAnnualAverage(dataMap.get(studentList
          .get(rowIndex - 3)));
    }

    /*
     * other cases
     */
    Object o = columnList.get(columnIndex - 1);

    /*
     * case of a formula
     */
    if (o instanceof Formula) {
      Formula f = (Formula) o;

      if (rowIndex == 0) return f.getDescription();

      if (rowIndex == 1) return null;

      return f.getValue();
    }

    /*
     * case of a course
     */
    
    Course c = (Course) o;

    if (rowIndex == 0) return c;
    if (rowIndex == 1) return c.getCoeff();

    return StudentAverage.getAverage(dataMap.get(studentList.get(rowIndex - 3))
        .get(c).values());
  }


  /**
   * Tells if a cell in the model is editable or not.
   * In the jury model, all you can edit is the comments for each student.
   * @see javax.swing.table.TableModel#isCellEditable(int, int)
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if (columnIndex == columnCount - 1 && rowIndex > 2) return true;

    return false;
  }

  /**
   * Sets a value at a given cell.
   * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    if (columnIndex != columnCount - 1) return;

    final Student s = studentList.get(rowIndex - 3);
    final String comment = (String)aValue;
    
    es.execute(new Runnable(){
        public void run() {
          synchronized(lock){
            try {
              manager.changeStudentComment(s, comment);
            } catch (DataManagerException e) {
              ExceptionDisplayDialog.postException(e);
              return;
            }
          }
        }
    });
    

  }

  /**
   * Removes a given column from the database and the model.
   * If the given column is not a formula, nothing will be removed.
   * @param columnIndex The index in the model of the column to remove. 
   */
  public void removeColumn(int columnIndex) {

    if (columnIndex >= columnCount - 3) return;

    Object o = columnList.get(columnIndex - 1);

    if (o instanceof Formula) {
      final Formula f = (Formula) o;
      es.execute(new Runnable() {

        public void run() {

          synchronized (lock) {

            try {
              manager.removeJuryFormula(f);
            } catch (DataManagerException e) {
              ExceptionDisplayDialog.postException(e);
              return;
            }

            try {
              EventQueue.invokeAndWait(new Runnable() {

                public void run() {
                  JuryModel.this.update();
                }
              });
            } catch (InterruptedException e) {
              ExceptionDisplayDialog.postException(e);
              return;
            } catch (InvocationTargetException e) {
              ExceptionDisplayDialog.postException(e);
              return;
            }
          }
        }
      });
    }
  }

  /**
   * Tells if a column in a the model is a formula.
   * @param columnIndex the index in the model of the column to test.
   * @return true if the column is a formula, false else.
   */
  public boolean isColumnFormula(int columnIndex) {
    if (columnIndex == 0 || columnIndex >= columnCount - 3) return false;

    Object o = columnList.get(columnIndex - 1);

    if (o instanceof Formula) return true;

    return false;
  }

  /**
   * Used to construct the chart representing the averages of all students.
   * @return A <code>MessageFormat</code> containing the header message of the chart.
   */
  public MessageFormat getHeaderMessageFormat() {
    return new MessageFormat(builder.getValue(JURY_HEADER));
  }


  /**
   * Construct the <code>ChartPanel</code> containing the chart for the
   * jury view.
   * @param step The interval of averages for the chart.
   * @return the <code>ChartPanel</code> containing the chart created.
   */
  public ChartPanel getChartPanel(int step) {

    Map<Integer, Integer>[] dataTab;

    int size = (20 % step == 0 ? 20 / step : 20 / step + 1);

    dataTab = new Map[size];

    initDataTabForJury(dataTab, courseMap);

    for (Map<Course, Map<Integer, StudentMark>> map : dataMap.values()) {
      for (Course c : map.keySet()) {
        int index = (int) StudentAverage.getAverage(map.get(c).values()) / step;

        if (index >= size) index = size - 1;

        int previousValue = dataTab[index].get(c.getId());
        dataTab[index].put(c.getId(), previousValue + 1);
      }
    }

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    int low = 0;
    int hi = step;

    for (Map<Integer, Integer> map : dataTab) {
      for (int courseId : map.keySet()) {
        dataset.addValue(map.get(courseId), courseMap.get(courseId).getTitle(),
            /*builder.getValue(CHART_FROM) +*/ low + builder.getValue(CHART_TO) + hi);
      }

      low += step;
      hi += step;
      
      if (hi > 20)
        hi = 20;
    }

    JFreeChart barChart = ChartFactory.createBarChart3D(
        builder.getValue(JURY_CHART_STUDENTS_AVERAGES), builder.getValue(CHART_MARK_INTERVAL), builder.getValue(CHART_STUDENT_NUMBER),
        dataset, PlotOrientation.VERTICAL, true, true, true);
    barChart.getPlot().setForegroundAlpha(0.65f);

    return new ChartPanel(barChart);
  }

  /**
   * Used to initialize data in the <code>Map</code> array, used to construct the chart
   * for the jury view.
   * @param dataTab The <code>Map</code> array to initialize.
   * @param courseMap The <code>Map<Integer, Course></code> which contains all data to initialize the array.
   */
  private void initDataTabForJury(Map<Integer, Integer>[] dataTab,
      Map<Integer, Course> courseMap) {

    for (int i = 0; i < dataTab.length; i++) {
      dataTab[i] = new HashMap<Integer, Integer>();
    }

    for (Map<Integer, Integer> map : dataTab) {
      for (int i : courseMap.keySet()) {
        map.put(i, 0);
      }
    }
  }

  /**
   * Adds a formula in the interacted database and in the model.
   * @param expression The expression of the formula given by the user.
   * @param desc The title of the wanted formula.
   * @param column The column index where to put the formula.
   */
  public void addFormula(final String expression, final String desc,
      final int column) {
    es.execute(new Runnable() {

      public void run() {
        synchronized (lock) {

          try {
            manager.addJuryFormula(expression, desc, column);
          } catch (DataManagerException e) {
        ExceptionDisplayDialog.postException(e);
    return;
          }

          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                JuryModel.this.update();
              }
            });
          } catch (InterruptedException e) {
            ExceptionDisplayDialog.postException(e);
            return;
          } catch (InvocationTargetException e) {
            ExceptionDisplayDialog.postException(e);
            return;
          }
        }
      }
    });
  }

  
  /**
   * Fills the <code>SymphonieFormulaFactory</code> map with each test's name and
   * his associated mark, in order to calculate a <code>Formula</code> at a given row.
   * @param rowIndex The index of the row to calculate.
   */
  private void fillFormulaMap(int rowIndex){
    
    if (rowIndex >= 3 && lastRow != rowIndex) {

      SymphonieFormulaFactory.clearMappedValues();

      Student s = studentList.get(rowIndex - 3);

      for (Course c : dataMap.get(s).keySet()) {
        SymphonieFormulaFactory.putMappedValue(c.getTitle(), StudentAverage
            .getAverage(dataMap.get(s).get(c).values()));
      }

      lastRow = rowIndex;
    }
  }
  
  // ----------------------------------------------------------------------------
  // Implement the ObjectFormattingSupport interface
  // ----------------------------------------------------------------------------

  /**
   * 
   */
  private final FormattableCellRenderer formatter = CellRendererFactory
      .getJuryModelCellRenderer();

  /* (non-Javadoc)
   * @see fr.umlv.symphonie.view.cells.ObjectFormattingSupport#getFormattableCellRenderer()
   */
  public FormattableCellRenderer getFormattableCellRenderer() {
    return formatter;
  }

  
  
  // /**
  // * @param args
  // * @throws IOException
  // */
  // public static void main(String[] args) throws IOException {
  // JFrame frame = new JFrame ("test JuryModel");
  // frame.setSize(800,600);
  // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  //    
  // DataManager dataManager = SQLDataManager.getInstance();
  //
  // final JuryModel model = JuryModel.getInstance(dataManager);
  //    
  // final JTable table = new JTable(model);
  // table.setTableHeader(null);
  //    
  //    
  // HashMap<String, String> map =
  // TextualResourcesLoader.getResourceMap("language/symphonie", new Locale(
  // "french"), "ISO-8859-1");
  //    
  // ComponentBuilder builder = new ComponentBuilder(map);
  //    
  // // popup and buttons
  // final JPopupMenu pop =
  // builder.buildPopupMenu(SymphonieConstants.JURYVIEWPOPUP_TITLE);
  //    
  // pop.add(builder.buildButton(SymphonieActionFactory.getJuryAddFormulaAction(null,
  // frame, builder),SymphonieConstants.ADD_FORMULA,
  // ComponentBuilder.ButtonType.MENU_ITEM));
  // pop.add(builder.buildButton(SymphonieActionFactory.getJuryUpdateAction(null),
  // SymphonieConstants.UPDATE, ComponentBuilder.ButtonType.MENU_ITEM));
  // pop.add(builder.buildButton(SymphonieActionFactory.getJuryPrintAction(null,
  // table), SymphonieConstants.PRINT_MENU_ITEM,
  // ComponentBuilder.ButtonType.MENU_ITEM));
  // pop.add(builder.buildButton(SymphonieActionFactory.getJuryChartAction(null,
  // frame), SymphonieConstants.DISPLAY_CHART,
  // ComponentBuilder.ButtonType.MENU_ITEM));
  //    
  // final AbstractButton removeColumn =
  // builder.buildButton(SymphonieActionFactory.getRemoveJuryColumnAction(null,
  // table), SymphonieConstants.REMOVE_COLUMN,
  // ComponentBuilder.ButtonType.MENU_ITEM);
  // pop.add(removeColumn);
  //    
  // // listener which displays the popup
  // table.addMouseListener(new MouseAdapter() {
  //
  // public void mousePressed(MouseEvent e) {
  // if (SwingUtilities.isRightMouseButton(e)) {
  // pop.show(e.getComponent(), e.getX(), e.getY());
  // }
  // }
  // });
  //    
  // // listener which saves the cursor location
  // table.addMouseListener(new MouseAdapter() {
  //
  // public void mousePressed(MouseEvent e) {
  // if (SwingUtilities.isRightMouseButton(e)) {
  // PointSaver.setPoint(e.getPoint());
  // }
  // }
  // });
  //    
  // // listener which disables buttons
  // table.addMouseListener(new MouseAdapter() {
  //
  // public void mousePressed(MouseEvent e) {
  // if (SwingUtilities.isRightMouseButton(e)) {
  // if (model.isColumnFormula(table.columnAtPoint(e.getPoint())))
  // removeColumn.setEnabled(true);
  // else removeColumn.setEnabled(false);
  // }
  // }
  // });
  //    
  //    
  //    
  //    
  //    
  //    
  // table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
  //      
  // public java.awt.Component getTableCellRendererComponent(JTable table,Object
  // value,
  // boolean isSelected,boolean hasFocus,int row,int column){
  // JLabel label = (JLabel)super.getTableCellRendererComponent(table, value,
  // isSelected, hasFocus, row, column);
  //        
  // label.setHorizontalAlignment(SwingConstants.CENTER);
  //          
  // if (column == 0 || row == 0 || column == table.getModel().getColumnCount()
  // - 2)
  // label.setFont(getFont().deriveFont(Font.BOLD));
  //          
  // return label;
  // }
  // });
  //    
  //    
  // JScrollPane scroll = new JScrollPane(table);
  //    
  // frame.setContentPane(scroll);
  //    
  // frame.setVisible(true);
  // }

}
