/*
 * This file is part of Symphonie
 * Created : 20-février.-2005 16:13:25
 */

package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
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
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.data.formula.SymphonieFormulaFactory;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.LookableCollection;
import fr.umlv.symphonie.util.Pair;
import fr.umlv.symphonie.util.StudentAverage;
import fr.umlv.symphonie.util.completion.CompletionDictionary;
import fr.umlv.symphonie.util.completion.IDictionarySupport;
import fr.umlv.symphonie.view.cells.CellRendererFactory;
import fr.umlv.symphonie.view.cells.FormattableCellRenderer;
import fr.umlv.symphonie.view.cells.ObjectFormattingSupport;

import static fr.umlv.symphonie.view.SymphonieConstants.*;

/*
 * par convention il est decide que la derniere colonne est toujours la moyenne
 * il faudra donc verifier a l'ajout de nouvelle colonne de bien laisser la
 * moyenne a la fin
 */

public class TeacherModel extends AbstractTableModel implements
    ObjectFormattingSupport, IDictionarySupport {

  /**
   * Don't know what the fuck it is about
   */
  private final Map<String, Number> mappedValues = new HashMap<String, Number>();


  /**
   * A comparator for students. Used to sort maps.
   */
  protected final Comparator<Student> StudentComparator = new Comparator<Student>() {

    public int compare(Student o1, Student o2) {
      int n = o1.getLastName().compareToIgnoreCase(o2.getLastName());

      if (n == 0) n = o1.getName().compareToIgnoreCase(o2.getName());

      if (n == 0) return o1.getId() - o2.getId();

      return n;
    }
  };


  /**
   * The DataManager which handles database
   */
  protected final DataManager manager;
  
  /**
   * The ComponentBuilder, used to internationalize the current model.
   */
  protected final ComponentBuilder builder;
  
  /**
   * The course being represented, null if the model is empty
   */
  protected Course course = null;


  /**
   * Number of rows currently in the model
   */
  protected int rowCount = 0;
  
  /**
   * Number of column currently in the model
   */
  protected int columnCount = 0;


  /**
   * The list of columns in the model, except the first and the last ones.
   * It can contain <code>Formulas</code> and <code>Marks</code>
   */
  protected final List<Object> columnList = new ArrayList<Object>();
  
  /**
   * The list of <code>Student</code>. Used to represent lines if the model.
   */
  protected final List<Student> studentList = new ArrayList<Student>();

  /**
   * A Map of every <code>Mark</code> related to the current Course.
   */
  protected final Map<Integer, Mark> markMap = new HashMap<Integer, Mark>();
  
  /**
   * A sorted map keyed with each <code>Student</code> of the database. Each value for each <code>Student</code>
   * is a map keyed with the <code>Mark</code>s' available for the current <code>Course</code>, and the values are the
   * <code>StudentMark</code>s associated to the <code>Mark</code>s.
   */
  protected final SortedMap<Student, Map<Integer, StudentMark>> studentMarkMap = new TreeMap<Student, Map<Integer, StudentMark>>(
      StudentComparator);

  /**
   * Pool of threads containing only one. Used to launch threads interacting with the database.
   */
  protected final ExecutorService es = Executors.newSingleThreadExecutor();

  /**
   * An object used to be locked by each thread launched,
   * in order not to generate errors while interacting with the database.
   */
  protected final Object lock = new Object();

  /**
   * Used in the <code>getValueAt</code> method, to check which row is being accessed. 
   */
  private int lastRow = -1;

  /**
   * A <code>CompletionDictionary</code> used in order to provide auto-completion with this model.
   */
  protected final CompletionDictionary dictionary = new CompletionDictionary();

  /**
   * Constructs an empty TeacherModel.
   * @param manager The <code>DataManager</code> which will be used to interact with database.
   * @param builder The <code>ComponentBuilder</code> which will provide internationalization.
   */
  public TeacherModel(DataManager manager, ComponentBuilder builder) {
    this.manager = manager;
    this.builder = builder;

    fillDefaultDictionary();
  }

  /**
   * Fill the <code>CompletionDictionary</code> with default key words.
   */
  private void fillDefaultDictionary() {
    dictionary.add("average");
    dictionary.add("min");
    dictionary.add("max");
  }

  // static public TeacherModel getInstance(final DataManager manager) {
  // if (instance == null)
  // instance = new TeacherModel(manager);
  //
  // else
  // instance.setManager(manager);
  //
  // return instance;
  // }

  // /**
  // * @param manager
  // * The manager to set.
  // */
  // protected void setManager(DataManager manager) {
  // this.manager = manager;
  // }

  /**
   * Sets the <code>Course</code> to be represented by the model.
   * @param courseToAdd The <code>Course</code> to represent.
   */
  public void setCourse(final Course courseToAdd) {

    es.execute(new Runnable() {

      /*
       * (non-Javadoc)
       * 
       * @see java.lang.Runnable#run()
       */
      public void run() {

        synchronized (lock) {

          clear();

          course = courseToAdd;

          Pair<Map<Integer, Mark>, SortedMap<Student, Map<Integer, StudentMark>>> studentAndMarkMapPair = null;
          try {
            studentAndMarkMapPair = manager.getAllMarksByCourse(course);
          } catch (DataManagerException e) {
            System.out
                .println("error getting data from database for Teacher View.");
            e.printStackTrace();
          }

          markMap.putAll(studentAndMarkMapPair.getFirst());

          for (Mark m : markMap.values())
            dictionary.add(m.getDesc());

          studentMarkMap.putAll(studentAndMarkMapPair.getSecond());

          columnList.addAll(markMap.values());

          List<Formula> formulaList;

          try {
            formulaList = manager.getFormulasByCourse(course);
          } catch (DataManagerException e2) {
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

          columnCount = columnList.size() + 2;
          rowCount = studentMarkMap.size() + 3;

          // System.out.println("lignes : " + rowCount);
          // System.out.println("colonnes : " + columnCount);

          studentList.addAll(studentMarkMap.keySet());

          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                TeacherModel.this.fireTableStructureChanged();
              }

            });
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          } catch (InvocationTargetException e1) {
            e1.printStackTrace();
          }

        }
      }
    });

  }

  public void printCourse() {
    System.out.println("*********");
    System.out.println(course);
    System.out.println("*********");
  }

  /**
   * Updates the data in the model.
   */
  public void update() {
    
    if (course != null){
      setCourse(course);
    }
  }

  /**
   * Clears the model.
   */
  public void clear() {
    course = null;
    rowCount = 0;
    columnCount = 0;

    columnList.clear();
    studentList.clear();

    studentMarkMap.clear();

    for (Mark m : markMap.values()) {
      dictionary.remove(m.getDesc());
    }

    markMap.clear();

    fireTableStructureChanged();
  }

  /**
   * Returns the number of rows currently in the model.
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
     * cas de la ligne separatrice
     */
    if (rowIndex == 2) return null;

    fillFormulaMap(rowIndex);

    /*
     * cas de la colonne tout a gauche
     */
    if (columnIndex == 0) {
      if (rowIndex == 0) return builder.getValue(TITLE);
      if (rowIndex == 1) return builder.getValue(COEFF);
      return studentList.get(rowIndex - 3);
    }

    /*
     * cas de la colonne tout a droite
     */
    if (columnIndex == columnCount - 1) {
      if (rowIndex == 0) return builder.getValue(AVERAGE);
      if (rowIndex == 1) return null;

      return StudentAverage.getAverage(studentMarkMap.get(
          studentList.get(rowIndex - 3)).values());
    }

    Object o = columnList.get(columnIndex - 1);

    if (o instanceof Formula) {
      Formula f = (Formula) o;

      if (rowIndex == 0) {
        return f.getDescription();
      }

      if (rowIndex == 1) {
        return null;
      }

      return f.getValue();
    }

    else if (o instanceof Mark) {
      Mark m = (Mark) o;
      if (rowIndex == 0) return m;
      if (rowIndex == 1) return m.getCoeff();
      return studentMarkMap.get(studentList.get(rowIndex - 3)).get(m.getId())
          .getValue();
    }

    return null;

  }

  /**
   * Tells if a cell in the model is editable or not.
   * In the teacher model all values are editable, except the names of the students.
   * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {

    /*
     * Cas ou c'est pas editable :
     */
    if (columnIndex == 0 || rowIndex == 2 || columnIndex == columnCount - 1) return false;

    Object o = columnList.get(columnIndex - 1);

    if (o instanceof Formula && rowIndex >= 1) return false;

    return true;
  }

  /**
   * Sets value at a given cell.
   * 
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int,
   *      int)
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    final Object o = columnList.get(columnIndex - 1);
    final Object value = aValue;
    final int row = rowIndex;

    /*
     * cas des intitules :
     */
    if (rowIndex == 0) {

      if (o instanceof Formula) return; // changer ca

      es.execute(new Runnable() {

        public void run() {

          Mark m = (Mark) o;

          dictionary.remove(m.getDesc());

          try {
            manager.changeMarkDescription(m, (String) value);
          } catch (DataManagerException e) {
            System.out
                .println("Error while attempting to modify the test name to "
                    + value);
            e.printStackTrace();
          }

          dictionary.add(m.getDesc());

          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                TeacherModel.this.fireTableRowsUpdated(row, row);
              }

            });
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          } catch (InvocationTargetException e1) {
            e1.printStackTrace();
          }

        }
      });

      return;
    }

    /*
     * cas des coeffs
     */
    else if (rowIndex == 1) {

      if (o instanceof Formula) return; // changer ca

      es.execute(new Runnable() {

        public void run() {
          float newCoeff;

          try {
            newCoeff = Float.parseFloat((String) value);
          } catch (NumberFormatException e) {
            return;
          }
          try {
            manager.changeMarkCoeff((Mark) o, newCoeff);
          } catch (DataManagerException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
          }

          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                TeacherModel.this.fireTableRowsUpdated(1, rowCount - 1);
              }

            });
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          } catch (InvocationTargetException e1) {
            e1.printStackTrace();
          }

        }
      });

      return;
    }

    /*
     * cas des notes
     */
    if (o instanceof Formula) return;

    es.execute(new Runnable() {

      public void run() {
        float newValue;

        try {
          newValue = Float.parseFloat((String) value);
        } catch (NumberFormatException e) {
          return;
        }

        Mark m = (Mark) o;

        try {
          manager.changeStudentMarkValue(studentMarkMap.get(
              studentList.get(row - 3)).get(m.getId()), newValue);
        } catch (DataManagerException e) {
          System.out.println(e.getMessage());
          e.printStackTrace();
        }

        try {
          EventQueue.invokeAndWait(new Runnable() {

            public void run() {
              TeacherModel.this.fireTableRowsUpdated(row, row);
            }

          });
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        } catch (InvocationTargetException e1) {
          e1.printStackTrace();
        }

      }
    });
  }

  /**
   * Adds a formula in the interacted database and in the model.
   * @param expression The expression of the formula given by the user.
   * @param desc The title of the wanted formula.
   * @param column The column index where to put the formula.
   * (it will NEVER be added at the first or last column of the model).
   */
  public void addFormula(final String expression, final String desc,
      final int column) {

    es.execute(new Runnable() {

      public void run() {
        synchronized (lock) {

          try {
            manager.addTeacherFormula(expression, desc, course, column);
          } catch (DataManagerException e) {
            System.out.println(e.getMessage());
          }

          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                TeacherModel.this.update();
              }
            });
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          } catch (InvocationTargetException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
  }

  /**
   * Adds a new test in the interacted database and in the model.
   * @param desc The title of the new test.
   * @param coeff The coefficient of the new test.
   */
  public void addMark(final String desc, final float coeff) {

    es.execute(new Runnable() {

      public void run() {
        synchronized (lock) {

          if (course != null) {

            Mark m = null;

            try {
              m = manager.addMark(desc, coeff, course);
            } catch (DataManagerException e) {
              System.out.println(e.getMessage());
            }

            dictionary.add(m.getDesc());

            try {
              EventQueue.invokeAndWait(new Runnable() {

                public void run() {
                  TeacherModel.this.update();
                }

              });
            } catch (InterruptedException e1) {
              e1.printStackTrace();
            } catch (InvocationTargetException e1) {
              e1.printStackTrace();
            }
          }
        }
      }
    });
  }

  /**
   * Removes a column in the model. It will NEVER remove the first or last one.
   * @param columnIndex The index of the column to remove in the model.
   */
  public void removeColumn(int columnIndex) {
    if (columnIndex == 0 || columnIndex == columnCount - 1) return;

    columnIndex--;

    Object o = columnList.get(columnIndex);

    if (o instanceof Mark)
      removeMark((Mark) o);

    else
      removeFormula((Formula) o);

  }

  /**
   * Removes a given <code>Formula</code> from the database and the model. 
   * @param formula the <code>Formula</code> to remove.
   */
  private void removeFormula(final Formula formula) {
    es.execute(new Runnable() {

      public void run() {
        synchronized (lock) {
          try {
            manager.removeTeacherFormula(formula, course);
          } catch (DataManagerException e) {
            System.out.println(e.getMessage());
          }

          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                TeacherModel.this.update();
              }
            });
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          } catch (InvocationTargetException e1) {
            e1.printStackTrace();
          }
        }
      }
    });

  }

  /**
   * Removes a given <code>Mark</code> from the database and the model.
   * (the class <code>Mark</code> represents a test in the application).
   * @param mark the <code>Mark</code> to remove.
   */
  private void removeMark(final Mark mark) {
    es.execute(new Runnable() {

      public void run() {
        synchronized (lock) {

          try {
            manager.removeMark(mark);
          } catch (DataManagerException e) {
            System.out.println(e.getMessage());
          }

          dictionary.remove(mark.getDesc());

          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                TeacherModel.this.update();
              }
            });
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          } catch (InvocationTargetException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
  }

  /**
   * Returns the <code>CompletionDictionary</code> of the curent model.
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
   * Used to display the chart of the current <code>Course</code> being represented. 
   * @return a <code>Messageformat</code> containing the header message for the chart.
   */
  public MessageFormat getHeaderMessageFormat() {
    return new MessageFormat(builder.getValue(TEACHER_HEADER) + course);
  }

  /**
   * Used to initialize data in the <code>Map</code> array, used to construct the chart
   * for the current <code>Course</code>.
   * @param dataTab The <code>Map</code> array to initialize.
   * @param markMap The <code>Map<Integer, Mark></code> which contains all data to initialize the array.
   */
  private void initDataTab(Map<Integer, Integer>[] dataTab,
      Map<Integer, Mark> markMap) {

    for (int i = 0; i < dataTab.length; i++)
      dataTab[i] = new HashMap<Integer, Integer>();

    for (Map<Integer, Integer> map : dataTab) {
      for (int i : markMap.keySet()) {
        map.put(i, new Integer(0));
      }
    }
  }

  /**
   * Construct the <code>ChartPanel</code> containing the chart for the
   * <code>Course</code> being represented.
   * @param step The interval of marks for the chart.
   * @return the <code>ChartPanel</code> containing the chart created.
   */
  public ChartPanel getChartPanel(int step) {

    if (course != null) {
      Map<Integer, Integer>[] dataTab;

      int size = (20 % step == 0 ? 20 / step : 20 / step + 1);

      dataTab = new Map[size];

      initDataTab(dataTab, markMap);

      for (Map<Integer, StudentMark> map : studentMarkMap.values()) {
        for (StudentMark sm : map.values()) {
          int index = (int) sm.getValue() / step;

          if (index >= size) index = size - 1;

          int previousValue = dataTab[index].get(sm.getMark().getId());
          dataTab[index].put(sm.getMark().getId(), previousValue + 1);
        }
      }

      DefaultCategoryDataset dataset = new DefaultCategoryDataset();

      int low = 0;
      int hi = step;

      for (Map<Integer, Integer> map : dataTab) {
        for (int markId : map.keySet()) {
          dataset.addValue(map.get(markId), markMap.get(markId).getDesc(),
          /* builder.getValue(CHART_FROM) + */low + builder.getValue(CHART_TO)
              + hi);
        }

        low += step;
        hi += step;

        if (hi > 20) hi = 20;
      }

      JFreeChart barChart = ChartFactory.createBarChart3D(builder
          .getValue(TEACHER_CHART_MARK_FOR_COURSE)
          + course, builder.getValue(CHART_MARK_INTERVAL), builder
          .getValue(CHART_STUDENT_NUMBER), dataset, PlotOrientation.VERTICAL,
          true, true, false);
      barChart.getPlot().setForegroundAlpha(0.65f);

      return new ChartPanel(barChart);
    }

    return null;
  }

  // ----------------------------------------------------------------------------
  // Implement the ObjectFormattingSupport interface
  // ----------------------------------------------------------------------------

  /**
   * Spray's gonna comment this !
   */
  private final FormattableCellRenderer formatter = CellRendererFactory
      .getTeacherModelCellRenderer();

  /* (non-Javadoc)
   * @see fr.umlv.symphonie.view.cells.ObjectFormattingSupport#getFormattableCellRenderer()
   */
  public FormattableCellRenderer getFormattableCellRenderer() {
    return formatter;
  }

  /**
   * Tells if the current model is empty or not.
   * @return true if the model is empty, false else.
   */
  public boolean isEmpty() {
    return (course == null);
  }

  /**
   * Fills the <code>SymphonieFormulaFactory</code> map with each test's name and
   * his associated mark, in order to calculate a <code>Formula</code> at a given row.
   * @param rowIndex The index of the row to calculate.
   */
  private void fillFormulaMap(int rowIndex) {

    if (rowIndex >= 3 && lastRow != rowIndex) {

      SymphonieFormulaFactory.clearMappedValues();

      Student s = studentList.get(rowIndex - 3);

      for (StudentMark sm : studentMarkMap.get(s).values())
        SymphonieFormulaFactory.putMappedValue(sm.getMark().getDesc(), sm
            .getValue());

      lastRow = rowIndex;
    }
  }

  // public static void main(String[] args) throws DataManagerException,
  // IOException {
  // JFrame frame = new JFrame ("test TeacherModel");
  // frame.setSize(800,600);
  // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  //    
  // DataManager dataManager = SQLDataManager.getInstance();
  //    
  // // /*
  // // * test des etudiants
  // // */
  // // Map<Integer, Student> studentMap = dataManager.getStudents();
  // // System.out.println(studentMap.size() + " etudiants.");
  // // for (Student s : studentMap.values())
  // // System.out.println(s);
  // // System.out.println("\n\n");
  // // /*******************************/
  // //
  // //
  // // /*
  // // * test des matieres
  // // */
  // // Map<Integer, Course> courseMap = dataManager.getCourses();
  // // System.out.println(courseMap.size() + " matieres");
  // // for (Course c : courseMap.values()){
  // // System.out.println(c + " " + c.getCoeff());
  // // System.out.println(c.getId());
  // // }
  // // System.out.println("\n\n");
  // // /***************************/
  // //
  // // /*
  // // * test des epreuves
  // // */
  // // Map<Integer, Mark> markMap = dataManager.getMarks();
  // // System.out.println(markMap.size() + " epreuves");
  // // for (Mark m : markMap.values())
  // // System.out.println(m + " pour " + m.getCourse());
  // // System.out.println("\n\n");
  // // /******************************/
  // //
  // // /*
  // // * test des notes
  // // */
  // // List<StudentMark> list = dataManager.getStudentMarks();
  // // System.out.println(list.size() + " notes.");
  // //
  // // for (StudentMark sm : list){
  // // sm.printData();
  // // }
  // //
  // // System.out.println("\n\n");
  // // /******************************/
  //    
  //    
  // JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
  //    
  //    
  //    
  // /*
  // * table
  // */
  //    
  // final TeacherModel teacherModel = TeacherModel.getInstance(dataManager);
  //    
  // /*Course course = new Course (0, "java", 0.5f);
  //
  // teacherModel.setCourse(course);*/
  //    
  // final JTable table = new JTable(teacherModel);
  // table.setTableHeader(null);
  //    
  // HashMap<String, String> map =
  // TextualResourcesLoader.getResourceMap("language/symphonie", new Locale(
  // "french"), "ISO-8859-1");
  //    
  // ComponentBuilder builder = new ComponentBuilder(map);
  //    
  // // popup and buttons
  // final JPopupMenu pop =
  // builder.buildPopupMenu(SymphonieConstants.TEACHERVIEWPOPUP_TITLE);
  //    
  // pop.add(builder.buildButton(SymphonieActionFactory.getAddMarkAction(null,frame,
  // builder ), SymphonieConstants.ADDMARKDIALOG_TITLE,
  // ComponentBuilder.ButtonType.MENU_ITEM));
  // pop.add(builder.buildButton(SymphonieActionFactory.getTeacherAddFormulaAction(null,
  // frame, builder), SymphonieConstants.ADD_FORMULA,
  // ComponentBuilder.ButtonType.MENU_ITEM));
  // pop.add(builder.buildButton(SymphonieActionFactory.getTeacherUpdateAction(null),
  // SymphonieConstants.UPDATE, ComponentBuilder.ButtonType.MENU_ITEM));
  // pop.add(builder.buildButton(SymphonieActionFactory.getTeacherPrintAction(null,
  // table), SymphonieConstants.PRINT_MENU_ITEM,
  // ComponentBuilder.ButtonType.MENU_ITEM));
  // pop.add(builder.buildButton(SymphonieActionFactory.getTeacherChartAction(null,
  // frame), SymphonieConstants.DISPLAY_CHART,
  // ComponentBuilder.ButtonType.MENU_ITEM));
  //    
  // final AbstractButton removeColumn =
  // builder.buildButton(SymphonieActionFactory.getRemoveTeacherColumnAction(null,
  // table), SymphonieConstants.REMOVE_COLUMN,
  // ComponentBuilder.ButtonType.MENU_ITEM);
  // pop.add(removeColumn);
  // // end of popup
  //    
  //    
  //    
  //    
  //    
  // // listener for popup
  // table.addMouseListener(new MouseAdapter() {
  //
  // public void mousePressed(MouseEvent e) {
  // if (SwingUtilities.isRightMouseButton(e)) {
  // pop.show(e.getComponent(), e.getX(), e.getY());
  // }
  // }
  // });
  //    
  // // listener which saves point location
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
  // private int column;
  // public void mousePressed(MouseEvent e) {
  //        
  // if (SwingUtilities.isRightMouseButton(e)) {
  // column = table.columnAtPoint(e.getPoint());
  // if (column != table.getColumnCount() -1 && column > 0)
  // removeColumn.setEnabled(true);
  // else removeColumn.setEnabled(false);
  // }
  // }
  // });
  //    
  // table.setDefaultRenderer(Object.class,
  // CellRendererFactory.getTeacherModelCellRenderer(teacherModel.getFormattedObjects()));
  //    
  //    
  // JScrollPane scroll1 = new JScrollPane(table);
  //    
  // split.setRightComponent(scroll1);
  //    
  //    
  //    
  // /*
  // * arbre
  // */
  // CourseTreeModel courseModel = new CourseTreeModel(dataManager);
  //    
  // final JTree tree = new JTree(courseModel);
  //    
  // tree.setCellRenderer(new DefaultTreeCellRenderer(){
  //      
  // private final Icon leafIcon = new
  // ImageIcon(StudentTreeModel.class.getResource("../view/icons/course.png"));
  // private final Icon rootIcon = new
  // ImageIcon(StudentTreeModel.class.getResource("../view/icons/courses.png"));
  //      
  // public java.awt.Component getTreeCellRendererComponent(JTree tree,Object
  // value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus){
  //
  // Font font = getFont();
  //        
  // if (font != null){
  // font = font.deriveFont(Font.BOLD);
  // setFont(font);
  // }
  //        
  // if (leaf){
  // setLeafIcon(leafIcon);
  // }
  // else {
  // setClosedIcon(rootIcon);
  // setOpenIcon(rootIcon);
  // }
  //        
  // super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
  // hasFocus);
  // return this;
  // }
  // });
  //    
  //    
  // tree.addTreeSelectionListener(new TreeSelectionListener(){
  //      
  // private Object o;
  //
  // public void valueChanged(TreeSelectionEvent e) {
  // o = tree.getLastSelectedPathComponent();
  //        
  // if (o instanceof Course){
  // ((TeacherModel)table.getModel()).setCourse((Course)o);
  // /*((TeacherModel)table.getModel()).addMark("morpion", 0.0f);*/
  // /*CellFormat f = new CellFormat(BasicFormulaFactory.booleanInstance(true),
  // Color.RED, Color.CYAN);
  // for (int i = 3; i < 7; i++) {
  // Object ob = teacherModel.getValueAt(i, 0);
  // teacherModel.getFormattedObjects().put(ob, f);
  // }*/
  // }
  // }
  //      
  // });
  //    
  // JScrollPane pane = new JScrollPane(tree);
  //    
  // split.setLeftComponent(pane);
  //    
  // frame.setContentPane(split);
  //    
  // frame.setVisible(true);
  // }

}
