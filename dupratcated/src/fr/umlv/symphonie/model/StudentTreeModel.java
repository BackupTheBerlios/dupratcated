/*
 * This file is part of Symphonie
 * Created : 07-mars-2005 18:19:11
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
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.util.ComponentBuilder;


/**
 * @author susmab
 *
 */
public class StudentTreeModel extends DefaultTreeModel {

  
  /*private final List<TreeModelListener> listenerList = new ArrayList<TreeModelListener>();*/
  
  private final String root = "Etudiants"; // a changer pour l'internationalisation ?
  protected final DataManager manager;
  protected final ComponentBuilder builder;
  protected List<Student> studentList = null;
  
  protected final ExecutorService es = Executors.newSingleThreadExecutor();
  
  protected final Object lock = new Object();
  
//  private static StudentTreeModel instance = null;
  
  public StudentTreeModel(DataManager manager, ComponentBuilder builder){
    super(null);
    this.manager = manager;
	this.builder = builder;
    update();
  }
  
  
//  public static StudentTreeModel getInstance(DataManager manager){
//    if (instance == null)
//      instance = new StudentTreeModel(manager);
//    
//    else instance.setManager(manager);
//    
//    return instance;
//  }
  
  
  
//  private void setManager(DataManager manager) {
//    this.manager = manager;
//  }


  public void update() {
    
    es.execute(new Runnable(){
      public void run(){
        synchronized (lock){
          
          if (studentList == null){
            try {
              studentList = manager.getStudentList();
            } catch (DataManagerException e) {
              System.out.println("error getting students list from database. Try updating the list.");
            }
          }
          
          else {
            try {
              manager.getStudentList();
            } catch (DataManagerException e) {
              System.out.println("error getting students list from database. Try updating the list.");
            }
          }
          
          try {
            EventQueue.invokeAndWait(new Runnable() {
              private final Object source = root;
              private Object[] path = new Object[1];
              
              public void run() {
                path[0] = source;
                
                int[] childIndices = new int[studentList.size()];
                
                for (int i = 0 ; i < studentList.size() ; i++)
                  childIndices[i] = i;
                
                Student[] children = new Student[1];
                children = studentList.toArray(children);
                
                StudentTreeModel.this.fireTreeStructureChanged(source, path, childIndices, children);
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
    if (studentList == null ||
        parent.equals(root) == false)
    return null;
    
    return studentList.get(index);
  }

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
   */
  public int getChildCount(Object parent) {
    if (studentList == null ||
        parent.equals(root) == false)
      return 0;
    
    return studentList.size();
  }

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
   */
  public boolean isLeaf(Object node) {
    if (root.equals(node))
      return false;
    return true;
  }

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
   */
  /*public void valueForPathChanged(TreePath path, Object newValue) {
    // TODO Auto-generated method stub

  }*/

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
   */
  public int getIndexOfChild(Object parent, Object child) {
    if (child instanceof Student == false
        || root.equals(parent) == false)
      return -1;
    
    return studentList.indexOf(child);
  }

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
   */
  /*public void addTreeModelListener(TreeModelListener l) {
    listenerList.add(l);
  }*/

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
   */
  /*public void removeTreeModelListener(TreeModelListener l) {
    listenerList.remove(l);
  }*/

  public void addStudent(final String name, final String lastName){
    es.execute(new Runnable(){
      public void run(){
        
        synchronized(lock){
          Student s = null;
          
          try{
            s = manager.addStudent(name, lastName);
          }catch(DataManagerException e){
            System.out.println(e.getMessage());
          }
          
          final Student student = s;
          
          try {
            EventQueue.invokeAndWait(new Runnable() {
              private Object source = root;
              private Object[] path = new Object[1];
              int[] childIndices = new int[1];
              Object[] children = new Student[1];
              
              public void run() {
                path[0] = source;
                childIndices[0] = studentList.indexOf(student);
                children[0] = student;
                
                StudentTreeModel.this.fireTreeNodesInserted(source, path, childIndices, children);
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
  
  public void removeStudent(final Student s){
    es.execute(new Runnable(){
      public void run(){
        synchronized (lock){
          
          final int n = studentList.indexOf(s);
          
          try{
            manager.removeStudent(s);
          }catch(DataManagerException e){
            System.out.println(e.getMessage());
          }
          
          try {
            EventQueue.invokeAndWait(new Runnable() {
              private Object source = root;
              private Object[] path = new Object[1];
              int[] childIndices = new int[1];
              Object[] children = new Student[1];
              
              public void run() {
                path[0] = source;
                childIndices[0] = n;
                children[0] = s;
                StudentTreeModel.this.fireTreeNodesRemoved(source, path, childIndices, children);
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
//  /**
//   * @param args
//   */
//  public static void main(String[] args) {
//    JFrame frame = new JFrame ("test StudentModel");
//    frame.setSize(800,600);
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    
//    DataManager dataManager = SQLDataManager.getInstance();
//    StudentTreeModel model = new StudentTreeModel(dataManager);
//    
//    JTree tree = new JTree(model);
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
//    frame.setContentPane(pane);
//    
//    frame.setVisible(true);
//  }

}
