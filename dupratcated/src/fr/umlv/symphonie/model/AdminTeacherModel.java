/*
 * This file is part of Symphonie
 * Created : 21-mars-2005 1:06:05
 */
package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.TextualResourcesLoader;
import fr.umlv.symphonie.view.PointSaver;
import fr.umlv.symphonie.view.SymphonieActionFactory;
import fr.umlv.symphonie.view.SymphonieConstants;
import fr.umlv.symphonie.view.cells.CellRendererFactory;


/**
 * @author susmab
 *
 */
public class AdminTeacherModel extends TeacherModel {

  private static AdminTeacherModel instance = null;
  
  
  private AdminTeacherModel (DataManager manager){
    super(manager);
  }
  
  public static AdminTeacherModel getInstance(DataManager manager){
    if (instance == null)
      instance = new AdminTeacherModel(manager);
    
    else instance.setManager(manager);
    
    return instance;
  }
  
  public boolean isCellEditable(int rowIndex,int columnIndex){
    
    if (columnIndex == 0 && rowIndex >= 3)
      return true;
	
    return super.isCellEditable(rowIndex, columnIndex);
  }
  
  public void setValueAt(Object aValue,int rowIndex,int columnIndex){
    if (columnIndex == 0){
      final Student s = studentList.get(rowIndex - 3);
      final int row = rowIndex;
	  
      StringTokenizer tokenizer = new StringTokenizer((String)aValue, ":");
      
      final String lastName = tokenizer.nextToken();
      if (tokenizer.hasMoreElements() == false)
        return;
      final String name = tokenizer.nextToken();
      
      es.execute(new Runnable(){
        public void run(){
          Map<Integer, StudentMark> tmpMap = studentMarkMap.get(s);
          studentMarkMap.remove(s);
          
          try {
            manager.changeStudentNameAndLastName(s, name, lastName);
          }catch (DataManagerException e){
            System.out.println(e.getMessage());
          }
		  
          Collections.sort(studentList, StudentComparator);
          studentMarkMap.put(s, tmpMap);
          
          
          try {
	          EventQueue.invokeAndWait(new Runnable() {

	            public void run() {
	              AdminTeacherModel.this.fireTableRowsUpdated(3, rowCount - 1);
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
    
    super.setValueAt(aValue, rowIndex, columnIndex);
  }
  
//  /**
//   * @param args
//   * @throws IOException 
//   */
//  public static void main(String[] args) throws IOException {
//    JFrame frame = new JFrame ("test AdminTeacherModel");
//    frame.setSize(800,600);
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    
//    DataManager dataManager = SQLDataManager.getInstance();
//    
////    /*
////     * test des etudiants
////     */
////    Map<Integer, Student> studentMap = dataManager.getStudents();
////    System.out.println(studentMap.size() + " etudiants.");
////    for (Student s : studentMap.values())
////      System.out.println(s);
////    System.out.println("\n\n");
////    /*******************************/
////    
////    
////     /*
////      * test des matieres
////      */
////     Map<Integer, Course> courseMap = dataManager.getCourses();
////     System.out.println(courseMap.size() + " matieres");
////     for (Course c : courseMap.values()){
////       System.out.println(c + " " + c.getCoeff());
////       System.out.println(c.getId());
////     }
////     System.out.println("\n\n");
////    /***************************/
////    
////    /*
////     * test des epreuves
////     */
////    Map<Integer, Mark> markMap = dataManager.getMarks();
////    System.out.println(markMap.size() + " epreuves");
////    for (Mark m : markMap.values())
////      System.out.println(m + " pour " + m.getCourse());
////    System.out.println("\n\n");
////    /******************************/
////    
////    /*
////     * test des notes
////     */
////    List<StudentMark> list = dataManager.getStudentMarks();
////    System.out.println(list.size() + " notes.");
////    
////    for (StudentMark sm : list){
////      sm.printData();
////    }
////    
////    System.out.println("\n\n");
////    /******************************/
//    
//    
//    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//    
//    
//    
//    /*
//     * table
//     */
//    
//    final TeacherModel teacherModel = AdminTeacherModel.getInstance(dataManager);
//    
//    /*Course course = new Course (0, "java", 0.5f);
//
//    teacherModel.setCourse(course);*/
//    
//    final JTable table = new JTable(teacherModel);
//    table.setTableHeader(null);
//    
//    HashMap<String, String> map = TextualResourcesLoader.getResourceMap("language/symphonie", new Locale(
//    "french"), "ISO-8859-1");
//    
//    ComponentBuilder builder = new ComponentBuilder(map);
//    
//    // popup and buttons
//    final JPopupMenu pop = builder.buildPopupMenu(SymphonieConstants.TEACHERVIEWPOPUP_TITLE);
//    
//    pop.add(builder.buildButton(SymphonieActionFactory.getAddMarkAction(null,frame, builder ), SymphonieConstants.ADDMARKDIALOG_TITLE, ComponentBuilder.ButtonType.MENU_ITEM));
//    pop.add(builder.buildButton(SymphonieActionFactory.getTeacherAddFormulaAction(null, frame, builder), SymphonieConstants.ADD_FORMULA, ComponentBuilder.ButtonType.MENU_ITEM));
//    pop.add(builder.buildButton(SymphonieActionFactory.getTeacherUpdateAction(null), SymphonieConstants.UPDATE, ComponentBuilder.ButtonType.MENU_ITEM));
//    pop.add(builder.buildButton(SymphonieActionFactory.getTeacherPrintAction(null, table), SymphonieConstants.PRINT_MENU_ITEM, ComponentBuilder.ButtonType.MENU_ITEM));
//    pop.add(builder.buildButton(SymphonieActionFactory.getTeacherChartAction(null, frame), SymphonieConstants.DISPLAY_CHART, ComponentBuilder.ButtonType.MENU_ITEM));
//    
//    final AbstractButton removeColumn = builder.buildButton(SymphonieActionFactory.getRemoveTeacherColumnAction(null, table), SymphonieConstants.REMOVE_COLUMN, ComponentBuilder.ButtonType.MENU_ITEM);
//    pop.add(removeColumn);
//    // end of popup
//    
//    
//    
//    
//    
//    // listener for popup
//    table.addMouseListener(new MouseAdapter() {
//
//      public void mousePressed(MouseEvent e) {
//        if (SwingUtilities.isRightMouseButton(e)) {
//          pop.show(e.getComponent(), e.getX(), e.getY());
//        }
//      }
//    });
//    
//    // listener which saves point location
//    table.addMouseListener(new MouseAdapter() {
//
//      public void mousePressed(MouseEvent e) {
//        if (SwingUtilities.isRightMouseButton(e)) {
//          PointSaver.setPoint(e.getPoint());
//        }
//      }
//    });
//    
//    // listener which disables buttons
//    table.addMouseListener(new MouseAdapter() {
//      private int column;
//      public void mousePressed(MouseEvent e) {
//        
//        if (SwingUtilities.isRightMouseButton(e)) {
//          column = table.columnAtPoint(e.getPoint());
//          if (column != table.getColumnCount() -1 && column > 0)
//            removeColumn.setEnabled(true);
//          else removeColumn.setEnabled(false);
//        }
//      }
//    });
//    
//    table.setDefaultRenderer(Object.class, CellRendererFactory.getTeacherModelCellRenderer(teacherModel.getFormattedObjects()));
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
//    CourseTreeModel courseModel = new CourseTreeModel(dataManager);
//    
//    final JTree tree = new JTree(courseModel);
//    
//    tree.setCellRenderer(new DefaultTreeCellRenderer(){
//      
//      private final Icon leafIcon = new ImageIcon(StudentTreeModel.class.getResource("../view/icons/course.png"));
//      private final Icon rootIcon = new ImageIcon(StudentTreeModel.class.getResource("../view/icons/courses.png"));
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
//    
//    tree.addTreeSelectionListener(new TreeSelectionListener(){
//      
//      private Object o;
//
//      public void valueChanged(TreeSelectionEvent e) {
//        o = tree.getLastSelectedPathComponent();
//        
//        if (o instanceof Course){
//          ((TeacherModel)table.getModel()).setCourse((Course)o);
//          /*((TeacherModel)table.getModel()).addMark("morpion", 0.0f);*/
//          /*CellFormat f = new CellFormat(BasicFormulaFactory.booleanInstance(true), Color.RED, Color.CYAN);
//          for (int i = 3; i < 7; i++) {
//            Object ob = teacherModel.getValueAt(i, 0);
//            teacherModel.getFormattedObjects().put(ob, f);
//          }*/
//        }
//      }
//      
//    });
//    
//    JScrollPane pane = new JScrollPane(tree);
//    
//    split.setLeftComponent(pane);
//    
//    frame.setContentPane(split);
//    
//    frame.setVisible(true);
//
//  }

}
