/*
 * This file is part of Symphonie
 * Created : 20-mars-2005 22:19:31
 */
package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.StudentAverage;


/**
 * @author susmab
 *
 */
public class AdminStudentModel extends StudentModel {

//  private static AdminStudentModel instance = null;
  private final ExecutorService es = Executors.newSingleThreadExecutor();
  
  public AdminStudentModel(DataManager manager, ComponentBuilder builder){
    super(manager, builder);
  }
  
  
  public boolean isCellEditable(int rowIndex,int columnIndex){
    
    if (columnIndex == columnCount - 1)
      return false;
    
    Object o = matrix[rowIndex][columnIndex];
    
    if (o == null || o instanceof String)
      return false;
    
    if (o instanceof Course
        || o instanceof Mark
        || o instanceof StudentMark
        || o instanceof Float)
      return true;
    
    return false;
  }
  
  public void setValueAt(Object aValue,int rowIndex,int columnIndex){
    final Object o = matrix[rowIndex][columnIndex];
    final int row = rowIndex;
    final int column = columnIndex;
    
    if (o instanceof Course){
//      System.out.println("matiere.");
      final String value = (String)aValue;
      
      if (value.equals("") == false)
        es.execute(new Runnable(){
          public void run() {
            try {
              manager.changeCourseTitle((Course)o, value);
            } catch (DataManagerException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            
            try {
              EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                  AdminStudentModel.this.fireTableRowsUpdated(row, row);
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
    
    if (o instanceof StudentMark){
      final float value;
      try {
        value = Float.parseFloat((String)aValue);
      }catch (NumberFormatException e){
        return;
      }
      
      es.execute(new Runnable(){
        public void run() {
          try {
            manager.changeStudentMarkValue((StudentMark)o, value);
          } catch (DataManagerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          
          try {
            EventQueue.invokeAndWait(new Runnable() {
              public void run() {
                AdminStudentModel.this.fireTableRowsUpdated(row, row);
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
    
    if (o instanceof Mark){
      final String value = (String)aValue;
      
      if (value.equals("") == false){
        es.execute(new Runnable(){
          public void run() {
            try {
              manager.changeMarkDescription((Mark)o, value);
            } catch (DataManagerException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            
            try {
              EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                  AdminStudentModel.this.fireTableRowsUpdated(row, row);
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
    }
    
    if (o instanceof Float){
      final Object o2 = matrix[rowIndex - 1][columnIndex];
      
      if (o2 instanceof Mark){
        final float value;
        
        try{
          value = Float.parseFloat((String)aValue);
        }catch (NumberFormatException e){
          return;
        }
        
        es.execute(new Runnable(){
          public void run() {
            try {
              manager.changeMarkCoeff((Mark)o2, value);
            } catch (DataManagerException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            
            matrix[row][column] = value;
            matrix[row + 1][columnCount -1] = StudentAverage.getAverage(markMap.get(((Mark)o2).getCourse()).values());
            
            try {
              EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                  AdminStudentModel.this.fireTableRowsUpdated(row, row + 1);
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
    }
  }
  
  
//  /**
//   * @param args
//   */
//  public static void main(String[] args) {
//    JFrame frame = new JFrame ("test AdminStudentModel");
//    frame.setSize(800,600);
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    
//    DataManager dataManager = SQLDataManager.getInstance();
//    StudentModel studentModel = AdminStudentModel.getInstance(dataManager);
//    
//    Map<Integer, Student> studentMap = null;
//    try {
//      studentMap = dataManager.getStudents();
//    } catch (DataManagerException e) {
//      e.printStackTrace();
//      return;
//    }
//    
//    /*Student student = new Student(6, "Paul", "Ochon");
//    Student s = new Student (0, "Fabien", "Vallee");*/
//    
// //   dataManager.addCourse("Walibi", 0.1f);
//    
// //   Course c = dataManager.getCourses().get(2);
//    
// //   dataManager.addMark("projet", 0.1f, c);
//    
// //   dataManager.removeCourse(c);
//    
//    /*List<StudentMark> sgain = dataManager.getStudentMarks();
//    
//    StudentMark tmp = sgain.get(0);
//    StudentMark sm = new StudentMark(tmp.getStudent(), tmp.getMark(), 36f);
//    
//    System.out.println("position de la note recherchee (doit etre 0) : " + sgain.indexOf(sm));*/
//    
// //   dataManager.addStudent("Machin", "Chose");
//    
//    /*Map<Course, Map<Integer, StudentMark>> map = dataManager.getAllMarksByStudent(student);
//    
//    System.out.println("taille de la map : " + map.size());*/
//    
//    
//    
//    /*studentModel.setStudent(s);*/
//    
//    
//    
//    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//    
//    
//    /*
//     * table
//     */
//    
//    final JTable table = new JTable(studentModel);
//    table.setTableHeader(null);
//    
//    
//    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
//      
//      public java.awt.Component getTableCellRendererComponent(JTable table,Object value,
//          boolean isSelected,boolean hasFocus,int row,int column){
//        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        
//          label.setHorizontalAlignment(SwingConstants.CENTER);
//          
//          if (column == 0 || row % 4 == 0)
//            label.setFont(getFont().deriveFont(Font.BOLD));
//          
//          return label;
//      }
//    });
//    
//    
//    JScrollPane scroll1 = new JScrollPane(table);
//    
//    split.setRightComponent(scroll1);
//    
//    
//    
//    /*
//     * arbre
//     */
//    
//    StudentTreeModel treeModel = new StudentTreeModel(dataManager);
//    final JTree tree = new JTree(treeModel);
//    
//    tree.setCellRenderer(new DefaultTreeCellRenderer(){
//      
//      private final Icon leafIcon = new ImageIcon(StudentTreeModel.class.getResource("../view/icons/student.png"));
//      private final Icon rootIcon = new ImageIcon(StudentTreeModel.class.getResource("../view/icons/students.png"));
//      
//      public java.awt.Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus){
//
//        Font font = getFont();
//        
//        if (font != null){
//          font = font.deriveFont(Font.BOLD);
//          setFont(font);
//        }
//        
//        if (leaf){
//          setLeafIcon(leafIcon);
//        }
//        else {
//          setClosedIcon(rootIcon);
//          setOpenIcon(rootIcon);
//        }
//        
//        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
//        return this;
//      }
//    });
//    
//    JScrollPane pane = new JScrollPane(tree);
//    
//    split.setLeftComponent(pane);
//    
//    tree.addTreeSelectionListener(new TreeSelectionListener(){
//      
//      /* (non-Javadoc)
//       * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
//       */
//      public void valueChanged(TreeSelectionEvent e) {
//        Object o = tree.getLastSelectedPathComponent();
//        
//        if (o instanceof Student)
//          ((StudentModel)(table.getModel())).setStudent((Student)o);
//
//      }
//    });
//    
//    
//    frame.setContentPane(split);
//    
//    /*
//     * fin du main
//     */
//    
//    frame.setVisible(true);
//  }

}
