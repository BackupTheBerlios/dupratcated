/*
 * This file is part of Symphonie
 * Created : 08-mars-2005 0:54:56
 */
package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;


/**
 * @author susmab
 *
 */
public class CourseTreeModel extends DefaultTreeModel {

  
  private final List<TreeModelListener> listenerList = new ArrayList<TreeModelListener>();
  
  protected final String root = "Matieres";
  protected DataManager manager;
  protected List<Course> courseList = null;
  
//  private static CourseTreeModel instance = null;
  protected final ExecutorService es = Executors.newSingleThreadExecutor();
  protected final Object lock = new Object();
  
  public CourseTreeModel(DataManager manager) {
    super(null);
    this.manager = manager;
    
    update();
  }

//  public static CourseTreeModel getInstance(DataManager manager){
//    if (instance == null)
//      instance = new CourseTreeModel(manager);
//    
//    else instance.setManager(manager);
//    
//    return instance;
//  }
  
  
  
//  private void setManager(DataManager manager) {
//    this.manager = manager;    
//  }

  public void update(){
    
    es.execute(new Runnable() {

      public void run() {
        synchronized (lock) {
          if (courseList == null) {
            try {
              courseList = manager.getCoursesList();
            } catch (DataManagerException e) {
              System.out
                  .println("error getting courses list from database. Try updating the list.");
            }
          }

          else {
            try {
              manager.getCoursesList();
            } catch (DataManagerException e) {
              System.out.println("error getting courses list from database. Try updating the list.");
            }
          }
          
          try {
            EventQueue.invokeAndWait(new Runnable() {

              private final Object source = root;
              private Object[] path = new Object[1];
              
              public void run() {
                path[0] = source;
                
                int[] childIndices = new int[courseList.size()];
                
                for (int i = 0 ; i < courseList.size() ; i++)
                  childIndices[i] = i;
                
                Course[] children = new Course[1];
                children = courseList.toArray(children);
                
                CourseTreeModel.this.fireTreeStructureChanged(source, path, childIndices, children);
              }
            });
          } catch (InterruptedException e1) {
            System.out.println("exception interrupted");
            e1.printStackTrace();
          } catch (InvocationTargetException e1) {
            System.out.println("exception invocation");
            e1.printStackTrace();
          }
        }
      }
    });
  }
  
  
  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#getRoot()
   */
  public Object getRoot() {
    return root;
  }

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
   */
  public Object getChild(Object parent, int index) {
    if (root.equals(parent) == false
        || courseList == null)
    return null;
    
    return courseList.get(index);
  }

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
   */
  public int getChildCount(Object parent) {
    if (root.equals(parent) == false
        || courseList == null)
      return 0;
    
    return courseList.size();
  }

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
   */
  public boolean isLeaf(Object node) {
    if (root.equals(node))
      return false;
    
    return true;
  }

//  /* (non-Javadoc)
//   * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
//   */
//  public void valueForPathChanged(TreePath path, Object newValue) {
//  }

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
   */
  public int getIndexOfChild(Object parent, Object child) {
    if (root.equals(parent) == false
        || child instanceof Course == false)
    return -1;
    
    return courseList.indexOf(child);
  }

  
  public void removeCourse(final Course c){
    es.execute(new Runnable(){
      public void run(){
        synchronized (lock){
          
          final int n = courseList.indexOf(c);
          
          try{
            manager.removeCourse(c);
          }catch(DataManagerException e){
            System.out.println(e.getMessage());
          }
          
          try {
            EventQueue.invokeAndWait(new Runnable() {
              private Object source = root;
              private Object[] path = new Object[1];
              int[] childIndices = new int[1];
              Object[] children = new Course[1];
              
              public void run() {
                path[0] = source;
                childIndices[0] = n;
                children[0] = c;
                CourseTreeModel.this.fireTreeNodesRemoved(source, path, childIndices, children);
              }
            });
          } catch (InterruptedException e1) {
            System.out.println("exception interrupted");
            e1.printStackTrace();
          } catch (InvocationTargetException e1) {
            System.out.println("exception invocation");
            e1.printStackTrace();
          }
        }
      }
    });
  }

  
  public void addCourse(final String desc, final float coeff){
    es.execute(new Runnable(){
      public void run(){
        
        synchronized(lock){
          Course c = null;
          
          try{
            c = manager.addCourse(desc, coeff);
          }catch(DataManagerException e){
            System.out.println(e.getMessage());
          }
          
          final Course course = c;
          
          try {
            EventQueue.invokeAndWait(new Runnable() {
              private Object source = root;
              private Object[] path = new Object[1];
              int[] childIndices = new int[1];
              Object[] children = new Course[1];
              
              public void run() {
                path[0] = source;
                childIndices[0] = courseList.indexOf(course);
                children[0] = course;
                
                CourseTreeModel.this.fireTreeNodesInserted(source, path, childIndices, children);
              }
            });
          } catch (InterruptedException e1) {
            System.out.println("exception interrupted");
            e1.printStackTrace();
          } catch (InvocationTargetException e1) {
            System.out.println("exception invocation");
            e1.printStackTrace();
          }
        }
      }
    });
  }
//  /* (non-Javadoc)
//   * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
//   */
//  public void addTreeModelListener(TreeModelListener l) {
//    listenerList.add(l);
//  }

//  /* (non-Javadoc)
//   * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
//   */
//  public void removeTreeModelListener(TreeModelListener l) {
//    listenerList.remove(l);
//  }

//  /**
//   * @param args
//   */
//  public static void main(String[] args) {
//    JFrame frame = new JFrame ("test StudentModel");
//    frame.setSize(800,600);
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    
//    DataManager dataManager = SQLDataManager.getInstance();
//    CourseTreeModel model = new CourseTreeModel(dataManager);
//    
//    JTree tree = new JTree(model);
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
//    JScrollPane pane = new JScrollPane(tree);
//    
//    frame.setContentPane(pane);
//    
//    frame.setVisible(true);
//
//  }

}
