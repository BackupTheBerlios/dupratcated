/*
 * This file is part of Symphonie
 * Created : 15 février. 2005 17:57:09
 */

package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.table.AbstractTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultKeyedValuesDataset;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.data.formula.SymphonieFormulaFactory;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.StudentAverage;
import fr.umlv.symphonie.view.cells.CellRendererFactory;
import fr.umlv.symphonie.view.cells.FormattableCellRenderer;
import fr.umlv.symphonie.view.cells.ObjectFormattingSupport;

import static fr.umlv.symphonie.view.SymphonieConstants.*;

/**
 * Suitable data model for a table displaying a Student Courses
 * 
 * @author fvallee
 */
public class StudentModel extends AbstractTableModel implements
    ObjectFormattingSupport {

  protected final DataManager manager;
  protected final ComponentBuilder builder;
  protected Student student = null;
  protected int columnCount = 0;
  protected int rowCount = 0;
  protected Map<Course, Map<Integer, StudentMark>> markMap = null;
//  private static StudentModel instance = null;
  protected Object[][] matrix = null;

  protected final Object lock = new Object();

  /**
   * Pool de threads qui n'en contient qu'un seul et qui sert pour le
   * rafraîchissement du canal courant.
   */
  private final ExecutorService es = Executors.newSingleThreadExecutor();

  private int lastRow = -1;
  
  public StudentModel(DataManager manager, ComponentBuilder builder) {
    this.manager = manager;
    this.builder = builder;
  }

//  public static StudentModel getInstance(DataManager manager) {
//    if (instance == null)
//      instance = new StudentModel(manager);
//
//    else
//      instance.setManager(manager);
//
//    return instance;
//  }

//  protected void setManager(DataManager manager) {
//    this.manager = manager;
//  }

  public void setStudent(final Student s) {

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

          columnCount = tmp + 2;
          rowCount = 4 * markMap.size();

          matrix = new Object[rowCount][columnCount];

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
            matrix[row + 1][column] = builder.getValue(COEFF);
            matrix[row + 2][column] = builder.getValue(MARK);

            column++;

            /*
             * On remplit les cases de la matrice avec les notes
             */
            for (StudentMark sm : collection) {
              matrix[row][column] = sm.getMark();
              matrix[row + 1][column] = sm.getCoeff();
              matrix[row + 2][column] = sm;
              column++;
            }

            /*
             * On remplit le reste des cases de bon gros vide
             */
            blankRow(row, column);
            blankRow(row + 1, column);
            blankRow(row + 2, column);
            blankRow(row + 3, 0);

            matrix[row][columnCount - 1] = builder.getValue(AVERAGE);
            matrix[row + 1][columnCount - 1] = null;
            matrix[row + 2][columnCount - 1] = StudentAverage
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

    if (student != null) setStudent(student);
  }

  /**
   * @param row
   * @param column
   */
  protected void blankRow(int row, int column) {
    for (; column < columnCount - 1; column++)
      matrix[row][column] = null;

  }

  public void clear() {
    columnCount = 0;
    rowCount = 0;
    student = null;
    matrix = null;
    markMap = null;
    
    fireTableStructureChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getRowCount()
   */
  public int getRowCount() {
    return rowCount;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  public int getColumnCount() {
    return columnCount;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    fillFormulaMap(rowIndex);
    return matrix[rowIndex][columnIndex];
  }

  public ChartPanel getChartPanel() {
    
    if (student != null) {

		float div = 0;
		
		DefaultKeyedValuesDataset pieDataset = new DefaultKeyedValuesDataset();

		for (Course c : markMap.keySet())
			div += c.getCoeff();

		for (Course c : markMap.keySet()) {
					pieDataset.setValue(c.getTitle()
							+ " : "
							+ StudentAverage
									.getAverage(markMap.get(c).values()), (c
							.getCoeff() / div));
				}

			JFreeChart pieChart = ChartFactory.createPieChart3D("Notes de "
					+ student + " - Moyenne générale : "
					+ StudentAverage.getAnnualAverage(markMap), pieDataset,
					false, false, false);
			pieChart.getPlot().setForegroundAlpha(0.35f);

			return new ChartPanel(pieChart);
		}
    
    return null;
  }

  public MessageFormat getHeaderMessageFormat() {
    return new MessageFormat("Notes de " + student);
  }

  // ----------------------------------------------------------------------------
  // Implement the ObjectFormattingSupport interface
  // ----------------------------------------------------------------------------

  private final FormattableCellRenderer formatter = CellRendererFactory
      .getStudentModelCellRenderer();

  public FormattableCellRenderer getFormattableCellRenderer() {
    return formatter;
  }
  
  public boolean isEmpty(){
    return (student == null);
  }

  public void fillFormulaMap(int rowIndex){
    int row = rowIndex / 4;

    if (lastRow != row){
      lastRow = row;
      row *=4;
      
      SymphonieFormulaFactory.clearMappedValues();
      
      for (int i = 1 ; matrix[row][i] != null && i < columnCount - 1 ; i++)
        SymphonieFormulaFactory.putMappedValue( ((Mark)(matrix[row][i])).getDesc(), ((StudentMark)(matrix[row+2][i])).getValue() );
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
