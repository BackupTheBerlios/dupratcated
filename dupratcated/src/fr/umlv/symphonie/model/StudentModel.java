/*
 * This file is part of Symphonie
 * Created : 15 février. 2005 17:57:09
 */

package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultKeyedValuesDataset;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.data.formula.SymphonieFormulaFactory;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.ExceptionDisplayDialog;
import fr.umlv.symphonie.util.StudentAverage;
import static fr.umlv.symphonie.view.SymphonieConstants.AVERAGE;
import static fr.umlv.symphonie.view.SymphonieConstants.COEFF;
import static fr.umlv.symphonie.view.SymphonieConstants.MARK;
import static fr.umlv.symphonie.view.SymphonieConstants.STUDENT_CHART_SENTENCE_1;
import static fr.umlv.symphonie.view.SymphonieConstants.STUDENT_CHART_SENTENCE_2;
import static fr.umlv.symphonie.view.SymphonieConstants.STUDENT_HEADER;
import fr.umlv.symphonie.view.cells.CellRendererFactory;

/**
 * Suitable data model for a table displaying a Student view
 * 
 * @author fvallee
 */
public class StudentModel extends AbstractSymphonieTableModel {

  /**
   * The student being represented, null if the model is empty
   */
  protected Student student = null;

  /**
   * A <code>Map</code> containing all courses and marks related to,
   * associated with the represented student.
   */
  protected Map<Course, Map<Integer, StudentMark>> markMap = null;

  /**
   * A <code>List</code> of all courses available in the database.
   */
  protected List<Course> courseList = new ArrayList<Course>();

  /**
   * Used for the filling of the dictionary.
   */
  private int lastRow = -1;

  /**
   * Creates an empty <code>StudentModel</code>
   * 
   * @param manager
   *          The <code>DataManager</code> which will be used to interact with
   *          database.
   * @param builder
   *          The <code>ComponentBuilder</code> which will provide
   *          internationalization.
   */
  public StudentModel(DataManager manager, ComponentBuilder builder) {
    super(manager, builder, CellRendererFactory.getStudentModelCellRenderer());
  }

  /**
   * Sets the student to represent in the model.
   * 
   * @param s
   *          the <code>Student</code> to represent.
   */
  public void setStudent(final Student s) {

    es.execute(new Runnable() {

      public void run() {

        synchronized (lock) {
          clear();
          student = s;

          if (student != null) {
            try {
              markMap = manager.getAllMarksByStudent(student);
            } catch (DataManagerException e) {
              ExceptionDisplayDialog.postException(e);
              return;
            }

            int n;
            int tmp = 0;
            for (Course c : markMap.keySet()) {

              if ((n = markMap.get(c).size()) > tmp) tmp = n;

              courseList.add(c);

              for (StudentMark sm : markMap.get(c).values())
                if (dictionary.contains(sm.getMark().getDesc()) == false)
                  dictionary.add(sm.getMark().getDesc());
            }

            columnCount = tmp + 2;
            rowCount = 4 * markMap.size();

            try {
              EventQueue.invokeAndWait(new Runnable() {

                public void run() {
                  StudentModel.this.fireTableStructureChanged();
                }

              });
            } catch (InterruptedException e1) {
              ExceptionDisplayDialog.postException(e1);
            } catch (InvocationTargetException e1) {
              ExceptionDisplayDialog.postException(e1);
            }
          }
        }
      }
    });

  }

  /**
   * Updates the data in the model.
   */
  public void update() {

    setStudent(student);
  }

  /**
   * Clears all data in the model.
   */
  public void clear() {
    columnCount = 0;
    rowCount = 0;
    student = null;
    courseList.clear();

    if (markMap != null) {

      for (Course c : markMap.keySet()) {
        for (StudentMark sm : markMap.get(c).values())
          dictionary.remove(sm.getMark().getDesc());
      }

      markMap = null;
    }

    fireTableStructureChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    fillFormulaMap(rowIndex);

    int index = rowIndex / 4;
    int line = rowIndex % 4;

    if (line == 3) return null;

    Course c = courseList.get(index);

    if (columnIndex == 0) {

      switch (line) {
        case 0: {
          return c;
        }
        case 1: {
          return builder.getValue(COEFF);
        }
        case 2: {
          return builder.getValue(MARK);
        }
      }
    }

    else if (columnIndex == columnCount - 1) {

      switch (line) {
        case 0: {
          return builder.getValue(AVERAGE);
        }

        case 2: {
          return StudentAverage.getAverage(markMap.get(c).values());
        }
      }

      return null;
    }

    int column = columnIndex - 1;

    Collection<StudentMark> collection = markMap.get(c).values();

    if (column >= collection.size()) return null;

    StudentMark[] tab = new StudentMark[1];

    tab = collection.toArray(tab);

    StudentMark sm = tab[column];

    switch (line) {

      case 0: {
        return sm.getMark();
      }

      case 1: {
        return sm.getCoeff();
      }

      case 2: {
        return sm;
      }
    }

    return null;
  }

  public ChartPanel getChartPanel(int step) {

    if (student != null) {

      float div = 0;

      DefaultKeyedValuesDataset pieDataset = new DefaultKeyedValuesDataset();

      for (Course c : markMap.keySet())
        div += c.getCoeff();

      for (Course c : markMap.keySet()) {
        pieDataset.setValue(c.getTitle() + " : "
            + StudentAverage.getAverage(markMap.get(c).values()),
            (c.getCoeff() / div));
      }

      JFreeChart pieChart = ChartFactory.createPieChart3D(builder
          .getValue(STUDENT_CHART_SENTENCE_1)
          + student
          + builder.getValue(STUDENT_CHART_SENTENCE_2)
          + StudentAverage.getAnnualAverage(markMap), pieDataset, false, false,
          false);
      pieChart.getPlot().setForegroundAlpha(0.35f);

      return new ChartPanel(pieChart);
    }

    return null;
  }

  public MessageFormat getHeaderMessageFormat() {
    return new MessageFormat(builder.getValue(STUDENT_HEADER) + student);
  }

  /**
   * Tells if the model is empty or not.
   * 
   * @return <code>true</code> if the model is empty, <code>false</code>
   *         otherwise.
   */
  public boolean isEmpty() {
    return (student == null);
  }

  /**
   * Fills the <code>Map</code> of values in
   * <code>SymphonieFormulaFactory</code> with all marks from all tests at a
   * given row int the model.
   * 
   * @param rowIndex
   *          the row which will determine the data to put in the map.
   */
  public void fillFormulaMap(int rowIndex) {
    int row = rowIndex / 4;

    if (lastRow != row) {
      lastRow = row;

      SymphonieFormulaFactory.clearMappedValues();

      for (StudentMark sm : markMap.get(courseList.get(row)).values())
        SymphonieFormulaFactory.putMappedValue(sm.getMark().getDesc(), sm
            .getValue());
    }
  }

  // public static void main(String[] args) throws DataManagerException {
  // JFrame frame = new JFrame ("test StudentModel");
  // frame.setSize(800,600);
  // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  //    
  // DataManager dataManager = SQLDataManager.getInstance();
  // StudentModel studentModel = new StudentModel(dataManager);
  //    
  // Map<Integer, Student> studentMap = null;
  // try {
  // studentMap = dataManager.getStudents();
  // } catch (DataManagerException e) {
  // e.printStackTrace();
  // return;
  // }
  //    
  // /*Student student = new Student(6, "Paul", "Ochon");
  // Student s = new Student (0, "Fabien", "Vallee");*/
  //    
  // // dataManager.addCourse("Walibi", 0.1f);
  //    
  // // Course c = dataManager.getCourses().get(2);
  //    
  // // dataManager.addMark("projet", 0.1f, c);
  //    
  // // dataManager.removeCourse(c);
  //    
  // /*List<StudentMark> sgain = dataManager.getStudentMarks();
  //    
  // StudentMark tmp = sgain.get(0);
  // StudentMark sm = new StudentMark(tmp.getStudent(), tmp.getMark(), 36f);
  //    
  // System.out.println("position de la note recherchee (doit etre 0) : " +
  // sgain.indexOf(sm));*/
  //    
  // // dataManager.addStudent("Machin", "Chose");
  //    
  // /*Map<Course, Map<Integer, StudentMark>> map =
  // dataManager.getAllMarksByStudent(student);
  //    
  // System.out.println("taille de la map : " + map.size());*/
  //    
  //    
  //    
  // /*studentModel.setStudent(s);*/
  //    
  //    
  //    
  // JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
  //    
  //    
  // /*
  // * table
  // */
  //    
  // final JTable table = new JTable(studentModel);
  // table.setTableHeader(null);
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
  // if (column == 0 || row % 4 == 0)
  // label.setFont(getFont().deriveFont(Font.BOLD));
  //          
  // return label;
  // }
  // });
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
  //    
  // StudentTreeModel treeModel = new StudentTreeModel(dataManager);
  // final JTree tree = new JTree(treeModel);
  //    
  // tree.setCellRenderer(new DefaultTreeCellRenderer(){
  //      
  // private final Icon leafIcon = new
  // ImageIcon(StudentTreeModel.class.getResource("../view/icons/student.png"));
  // private final Icon rootIcon = new
  // ImageIcon(StudentTreeModel.class.getResource("../view/icons/students.png"));
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
  // JScrollPane pane = new JScrollPane(tree);
  //    
  // split.setLeftComponent(pane);
  //    
  // tree.addTreeSelectionListener(new TreeSelectionListener(){
  //      
  // /* (non-Javadoc)
  // * @see
  // javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
  // */
  // public void valueChanged(TreeSelectionEvent e) {
  // Object o = tree.getLastSelectedPathComponent();
  //        
  // if (o instanceof Student)
  // ((StudentModel)(table.getModel())).setStudent((Student)o);
  //
  // }
  // });
  //    
  //    
  // frame.setContentPane(split);
  //    
  // /*
  // * fin du main
  // */
  //    
  // frame.setVisible(true);
  //    
  // System.out.println("zarma!!!");
  // }
}
