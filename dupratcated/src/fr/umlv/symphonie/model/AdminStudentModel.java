/*
 * This file is part of Symphonie
 * Created : 20-mars-2005 22:19:31
 */
package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.ExceptionDisplayDialog;


/**
 * Suitable data model for a table displaying a Student view in admin mode.
 * @author susmab
 *
 */
public class AdminStudentModel extends StudentModel {
  
  /**
   * Constructs an empty <code>AdminStudentModel</code>.
   * @param manager the <code>DataManager</code> which will handle database.
   * @param builder the <code>ComponentBuilder</code> which will provide internationalization.
   */
  public AdminStudentModel(DataManager manager, ComponentBuilder builder){
    super(manager, builder);
  }
  
  
  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#isCellEditable(int, int)
   */
  public boolean isCellEditable(int rowIndex,int columnIndex){
    
    if (columnIndex == columnCount - 1)
      return false;
    
    
    int index = rowIndex / 4;
    int line = rowIndex % 4;
    
    if (line == 3) return false;
    
    
    if (columnIndex == 0){
      
      if (line == 0)
        return true;
      
      return false;
    }
    
    Course c = courseList.get(index);
    
    int column = columnIndex - 1;
    
    Collection<StudentMark> collection = markMap.get(c).values();
    
    if (column >= collection.size())
      return false;
    
    return true;
  }
  
  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
   */
  public void setValueAt(Object aValue,int rowIndex,int columnIndex){

    final int row = rowIndex;
    final int column = columnIndex;
    
    final int index = rowIndex / 4;
    final int line = rowIndex % 4;
    
    if (columnIndex == 0){
      final String value = (String)aValue;
      
      if (value.equals("") == false)
        es.execute(new Runnable(){
          public void run() {
            try {
              manager.changeCourseTitle(courseList.get(index), value);
            } catch (DataManagerException e) {
              ExceptionDisplayDialog.postException(e);
              return;
            }
            
            try {
              EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                  AdminStudentModel.this.fireTableRowsUpdated(row, row);
                }
              });
            } catch (InterruptedException e1) {
              ExceptionDisplayDialog.postException(e1);
              return;
            } catch (InvocationTargetException e1) {
              ExceptionDisplayDialog.postException(e1);
              return;
            }
            
          }
        });
      
      return;
    }
    
    final Course c = courseList.get(index);
    
    final int columnListIndex = columnIndex - 1;
    
    Collection<StudentMark> collection = markMap.get(c).values();
    
    StudentMark[] tab = new StudentMark[1];
    
    tab = collection.toArray(tab);
    
    final StudentMark sm = tab[columnListIndex];
    
    switch(line){
      
      case 0 : {
        final String value = (String)aValue;
        
        if (value.equals("") == false){
          es.execute(new Runnable(){
            public void run() {
              try {
                manager.changeMarkDescription(sm.getMark(), value);
              } catch (DataManagerException e) {
                ExceptionDisplayDialog.postException(e);
                return;
              }
              
              try {
                EventQueue.invokeAndWait(new Runnable() {
                  public void run() {
                    AdminStudentModel.this.fireTableRowsUpdated(row, row);
                  }
                });
              } catch (InterruptedException e1) {
                ExceptionDisplayDialog.postException(e1);
                return;
              } catch (InvocationTargetException e1) {
                ExceptionDisplayDialog.postException(e1);
                return;
              }
              
            }
          });
        
        return;
        }
      }
      
      case 1 : {

        final float value;

        try {
          value = Float.parseFloat((String) aValue);
        } catch (NumberFormatException e) {
          return;
        }

        es.execute(new Runnable() {

          public void run() {
            try {
              manager.changeMarkCoeff(sm.getMark(), value);
            } catch (DataManagerException e) {
              ExceptionDisplayDialog.postException(e);
              return;
            }

            try {
              EventQueue.invokeAndWait(new Runnable() {

                public void run() {
                  AdminStudentModel.this.fireTableRowsUpdated(row, row + 1);
                }
              });
            } catch (InterruptedException e1) {
              ExceptionDisplayDialog.postException(e1);
              return;
            } catch (InvocationTargetException e1) {
              ExceptionDisplayDialog.postException(e1);
              return;
            }
          }
        });

        return;
      }
      
      case 2 : {
        final float value;
        try {
          value = Float.parseFloat((String)aValue);
        }catch (NumberFormatException e){
          return;
        }
        
        es.execute(new Runnable(){
          public void run() {
            try {
              manager.changeStudentMarkValue(sm, value);
            } catch (DataManagerException e) {
              ExceptionDisplayDialog.postException(e);
              return;
            }
            
            try {
              EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                  AdminStudentModel.this.fireTableRowsUpdated(row, row);
                }
              });
            } catch (InterruptedException e1) {
              ExceptionDisplayDialog.postException(e1);
              return;
            } catch (InvocationTargetException e1) {
              ExceptionDisplayDialog.postException(e1);
              return;
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
