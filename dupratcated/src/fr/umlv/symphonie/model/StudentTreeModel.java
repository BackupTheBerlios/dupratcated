/*
 * This file is part of Symphonie
 * Created : 07-mars-2005 18:19:11
 */
package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.tree.DefaultTreeModel;

import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.ExceptionDisplayDialog;


/**
 * Model for student's view. Displays all students.
 * @author susmab
 *
 */
public class StudentTreeModel extends DefaultTreeModel {

  
  /**
   * The root node of the model.
   */
  private final String root = "Etudiants"; // a changer pour l'internationalisation ?
  
  /**
   * The DataManager which handles database.
   */
  protected final DataManager manager;
  
  /**
   * The ComponentBuilder, used to internationalize the current model.
   */
  protected final ComponentBuilder builder;
  
  /**
   * The list of all students.
   */
  protected List<Student> studentList = null;
  
  /**
   * A pool of one thread. Used to launch threads which interact with the database.
   */
  protected final ExecutorService es = Executors.newSingleThreadExecutor();
  
  /**
   * An object used to be locked by each thread launched,
   * in order not to generate errors while interacting with the database.
   */
  protected final Object lock = new Object();
  
  
  /**
   * Constructs an empty <code>StudentTreeModel</code>.
   * @param manager The <code>DataManager</code> which will be used to interact with database.
   * @param builder The <code>ComponentBuilder</code> which will provide internationalization.
   */
  public StudentTreeModel(DataManager manager, ComponentBuilder builder){
    super(null);
    this.manager = manager;
	this.builder = builder;
    update();
  }


  /**
   * Updates the data of the model.
   */
  public void update() {
    
    es.execute(new Runnable(){
      public void run(){
        synchronized (lock){
          
          if (studentList == null){
            try {
              studentList = manager.getStudentList();
            } catch (DataManagerException e) {
              ExceptionDisplayDialog.postException(e);
              return;
            }
          }
          
          else {
            try {
              manager.getStudentList();
            } catch (DataManagerException e) {
              ExceptionDisplayDialog.postException(e);
              return;
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
   * Gets the root of the model.
   * @see javax.swing.tree.TreeModel#getRoot()
   */
  public Object getRoot() {
    return root;
  }

  /**
   * Used to get a certain child of a given node.
   * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
   */
  public Object getChild(Object parent, int index) {
    if (studentList == null ||
        parent.equals(root) == false)
    return null;
    
    return studentList.get(index);
  }

  /**
   * Returns the number of children a node has.
   * In this model, only the root has children.
   * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
   */
  public int getChildCount(Object parent) {
    if (studentList == null ||
        parent.equals(root) == false)
      return 0;
    
    return studentList.size();
  }

  /**
   * Tells if a node is a leaf or not.
   * In this model, all nodes except root are leaves.
   * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
   */
  public boolean isLeaf(Object node) {
    if (root.equals(node))
      return false;
    return true;
  }

  /**
   * Gets the index of a given node and his child.
   * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
   */
  public int getIndexOfChild(Object parent, Object child) {
    if (child instanceof Student == false
        || root.equals(parent) == false)
      return -1;
    
    return studentList.indexOf(child);
  }


  /**
   * Adds a new student in the database and in the model.
   * @param name The name of the new student.
   * @param lastName The lastname of the new student.
   */
  public void addStudent(final String name, final String lastName){
    es.execute(new Runnable(){
      public void run(){
        
        synchronized(lock){
          Student s = null;
          
          try{
            s = manager.addStudent(name, lastName);
          }catch(DataManagerException e){
            ExceptionDisplayDialog.postException(e);
            return;
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
   * Removes a given student from database and model.
   * @param s The student to remove.
   */
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
