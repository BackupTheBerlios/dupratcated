/*
 * This file is part of Symphonie
 * Created : 15 fï¿½vr. 2005 17:57:09
 */
package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultKeyedValuesDataset;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.util.StudentAverage;


/**
 * @author fvallee
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StudentModel extends AbstractTableModel {

	protected DataManager manager;
  protected Student student = null;
  protected int columnNumber = 0;
  protected int rowNumber = 0;
  protected Map<Course, Map<Integer, StudentMark>> markMap = null;
  private static StudentModel instance = null;
  protected Object[][] matrix = null;
  
  protected final Object lock = new Object();
  
  /**
   * Pool de threads qui n'en contient qu'un seul et qui sert pour le
   * rafraîchissement du canal courant.
   */
  private final ExecutorService es = Executors.newSingleThreadExecutor();
  
  private StudentModel(DataManager manager) {
    this.manager = manager;
  }
  
  public static StudentModel getInstance(DataManager manager){
    if (instance == null)
      instance = new StudentModel(manager);
    
    else instance.setManager(manager);
    
    return instance;
  }
  
  
  private void setManager(DataManager manager){
    this.manager = manager;
  }
  
  public void setStudent (final Student s){
    
      es.execute(new Runnable() {

        public void run() {

          synchronized (lock) {
          clear();
          student = s;
          try {
            markMap = manager.getAllMarksByStudent(student);
          } catch (DataManagerException e) {
            System.out.println(e.getMessage());
            return;
          }

          int n;
          int tmp = 0;
          for (Course c : markMap.keySet()) {
            if ((n = markMap.get(c).size()) > tmp) tmp = n;
          }

          columnNumber = tmp + 2;
          rowNumber = 4 * markMap.size();

          matrix = new Object[rowNumber][columnNumber];

          int row = 0;
          int column;
          Collection<StudentMark> collection = null;

          for (Course c : markMap.keySet()) {

            column = 0;
            collection = markMap.get(c).values();

            /*
             * On met les donnees dans la premiere colonne (matiere, coeff,
             * note)
             */
            matrix[row][column] = c;
            matrix[row + 1][column] = "coeff";
            matrix[row + 2][column] = "note";

            column++;

            /*
             * On remplit les cases de la matrice avec les notes
             */
            for (StudentMark mark : collection) {
              matrix[row][column] = mark.getMark();
              matrix[row + 1][column] = mark.getCoeff();
              matrix[row + 2][column] = mark;
              column++;
            }

            /*
             * On remplit le reste des cases de bon gros vide
             */
            blankRow(row, column);
            blankRow(row + 1, column);
            blankRow(row + 2, column);
            blankRow(row + 3, 0);

            matrix[row][columnNumber - 1] = "moyenne";
            matrix[row + 1][columnNumber - 1] = "";
            matrix[row + 2][columnNumber - 1] = StudentAverage
                .getAverage(collection);

            row += 4;

          }

          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                StudentModel.this.fireTableStructureChanged();
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
  
  
  public void update() {
    
    if (student != null)
      setStudent(student);
  }



  /**
   * @param row
   * @param column
   */
  protected void blankRow(int row, int column) {
    for (;column < columnNumber-1 ; column++)
      matrix[row][column] = "";
    
  }


  protected void clear(){
    columnNumber = 0;
    rowNumber = 0;
    student = null;
    matrix = null;
  }
  
  
  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getRowCount()
   */
  public int getRowCount() {
    return rowNumber;
  }

  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  public int getColumnCount() {
    return columnNumber;
  }

  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    return matrix[rowIndex][columnIndex];
  }

  public ChartPanel getChartPanel(){
    
    DefaultKeyedValuesDataset pieDataset = new DefaultKeyedValuesDataset();
    
    for (Course c : markMap.keySet()){
      pieDataset.setValue(c.getTitle() + " : " + StudentAverage.getAverage(markMap.get(c).values()), (c.getCoeff() * 100));
    }
    
    JFreeChart pieChart = ChartFactory.createPieChart3D("Notes de " + student + "- Moyenne générale : " + StudentAverage.getAnnualAverage(markMap), pieDataset, false, false, false);
    pieChart.getPlot().setForegroundAlpha(0.35f);
    
    return new ChartPanel(pieChart);
  }
  
  public MessageFormat getHeaderMessageFormat(){
    return new MessageFormat("Notes de " + student);
  }
  
  
  
  
  
  
  
  public static void main(String[] args) throws DataManagerException {
    JFrame frame = new JFrame ("test StudentModel");
    frame.setSize(800,600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    DataManager dataManager = SQLDataManager.getInstance();
    StudentModel studentModel = new StudentModel(dataManager);
    
    Map<Integer, Student> studentMap = null;
    try {
      studentMap = dataManager.getStudents();
    } catch (DataManagerException e) {
      e.printStackTrace();
      return;
    }
    
    /*Student student = new Student(6, "Paul", "Ochon");
    Student s = new Student (0, "Fabien", "Vallee");*/
    
 //   dataManager.addCourse("Walibi", 0.1f);
    
 //   Course c = dataManager.getCourses().get(2);
    
 //   dataManager.addMark("projet", 0.1f, c);
    
 //   dataManager.removeCourse(c);
    
    /*List<StudentMark> sgain = dataManager.getStudentMarks();
    
    StudentMark tmp = sgain.get(0);
    StudentMark sm = new StudentMark(tmp.getStudent(), tmp.getMark(), 36f);
    
    System.out.println("position de la note recherchee (doit etre 0) : " + sgain.indexOf(sm));*/
    
 //   dataManager.addStudent("Machin", "Chose");
    
    /*Map<Course, Map<Integer, StudentMark>> map = dataManager.getAllMarksByStudent(student);
    
    System.out.println("taille de la map : " + map.size());*/
    
    
    
    /*studentModel.setStudent(s);*/
    
    
    
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    
    
    /*
     * table
     */
    
    final JTable table = new JTable(studentModel);
    table.setTableHeader(null);
    
    
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
      
      public java.awt.Component getTableCellRendererComponent(JTable table,Object value,
          boolean isSelected,boolean hasFocus,int row,int column){
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
          label.setHorizontalAlignment(SwingConstants.CENTER);
          
          if (column == 0 || row % 4 == 0)
            label.setFont(getFont().deriveFont(Font.BOLD));
          
          return label;
      }
    });
    
    
    JScrollPane scroll1 = new JScrollPane(table);
    
    split.setRightComponent(scroll1);
    
    
    
    /*
     * arbre
     */
    
    StudentTreeModel treeModel = new StudentTreeModel(dataManager);
    final JTree tree = new JTree(treeModel);
    
    tree.setCellRenderer(new DefaultTreeCellRenderer(){
      
      private final Icon leafIcon = new ImageIcon(StudentTreeModel.class.getResource("../view/icons/student.png"));
      private final Icon rootIcon = new ImageIcon(StudentTreeModel.class.getResource("../view/icons/students.png"));
      
      public java.awt.Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus){

        Font font = getFont();
        
        if (font != null){
          font = font.deriveFont(Font.BOLD);
          setFont(font);
        }
        
        if (leaf){
          setLeafIcon(leafIcon);
        }
        else {
          setClosedIcon(rootIcon);
          setOpenIcon(rootIcon);
        }
        
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        return this;
      }
    });
    
    JScrollPane pane = new JScrollPane(tree);
    
    split.setLeftComponent(pane);
    
    tree.addTreeSelectionListener(new TreeSelectionListener(){
      
      /* (non-Javadoc)
       * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
       */
      public void valueChanged(TreeSelectionEvent e) {
        Object o = tree.getLastSelectedPathComponent();
        
        if (o instanceof Student)
          ((StudentModel)(table.getModel())).setStudent((Student)o);

      }
    });
    
    
    frame.setContentPane(split);
    
    /*
     * fin du main
     */
    
    frame.setVisible(true);
    
    System.out.println("zarma!!!");
  }
  
  
}
