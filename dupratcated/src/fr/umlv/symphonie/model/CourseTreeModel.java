/*
 * This file is part of Symphonie
 * Created : 08-mars-2005 0:54:56
 */

package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultTreeModel;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.ExceptionDisplayDialog;

/**
 * Model for teacher's view. Displays all courses available.
 * 
 * @author susmab
 */
public class CourseTreeModel extends DefaultTreeModel {

  /**
   * The root of the tree
   */
  protected String root;

  /**
   * The DataManager which handles database.
   */
  protected final DataManager manager;

  /**
   * The list of all courses.
   */
  protected List<Course> courseList = null;

  /**
   * A pool of one thread. Used to launch threads whic interact with the
   * database.
   */
  protected final ExecutorService es = Executors.newSingleThreadExecutor();

  /**
   * An object used to be locked by each thread launched, in order not to
   * generate errors while interacting with the database.
   */
  protected final Object lock = new Object();

  /**
   * Cosntructs an empty <code>CourseTreeModel</code>.
   * 
   * @param manager
   *          The <code>DataManager</code> which will be used to interact with
   *          database.
   * @param builder
   *          The <code>ComponentBuilder</code> which will provide
   *          internationalization.
   */
  public CourseTreeModel(DataManager manager, final ComponentBuilder builder) {
    super(null);
    this.manager = manager;
    root = builder.getValue("tree.courses");
    builder.addChangeListener(new JLabel(), new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        root = builder.getValue("tree.courses");
        update();
      }
    });

    update();
  }

  /**
   * Updates the data in the model.
   */
  public void update() {

    es.execute(new Runnable() {

      public void run() {
        synchronized (lock) {
          if (courseList == null) {
            try {
              courseList = manager.getCoursesList();
            } catch (DataManagerException e) {
              ExceptionDisplayDialog.postException(e);
              return;
            }
          }

          else {
            try {
              manager.getCoursesList();
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

                int[] childIndices = new int[courseList.size()];

                for (int i = 0; i < courseList.size(); i++)
                  childIndices[i] = i;

                Course[] children = new Course[1];
                children = courseList.toArray(children);

                CourseTreeModel.this.fireTreeStructureChanged(source, path,
                    childIndices, children);
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
   * Used to get the root of the model.
   * 
   * @see javax.swing.tree.TreeModel#getRoot()
   */
  public Object getRoot() {
    return root;
  }

  /**
   * Used to get a certain child of a given node.
   * 
   * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
   */
  public Object getChild(Object parent, int index) {
    if (root.equals(parent) == false || courseList == null) return null;

    return courseList.get(index);
  }

  /**
   * Returns the number of children a node has. In this model, only the root has
   * children.
   * 
   * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
   */
  public int getChildCount(Object parent) {
    if (root.equals(parent) == false || courseList == null) return 0;

    return courseList.size();
  }

  /**
   * Tells if a node is a leaf or not. In this model, all nodes except root are
   * leaves.
   * 
   * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
   */
  public boolean isLeaf(Object node) {
    if (root.equals(node)) return false;

    return true;
  }

  /**
   * Gets the index of a given node and his child.
   * 
   * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
   *      java.lang.Object)
   */
  public int getIndexOfChild(Object parent, Object child) {
    if (root.equals(parent) == false || child instanceof Course == false)
      return -1;

    return courseList.indexOf(child);
  }

  /**
   * Removes a given course from the database and the model. Re-sorts the tree
   * then.
   * 
   * @param c
   *          the <code>Course</code> to remove.
   */
  public void removeCourse(final Course c) {
    es.execute(new Runnable() {

      public void run() {
        synchronized (lock) {

          final int n = courseList.indexOf(c);

          try {
            manager.removeCourse(c);
          } catch (DataManagerException e) {
            ExceptionDisplayDialog.postException(e);
            return;
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
                CourseTreeModel.this.fireTreeNodesRemoved(source, path,
                    childIndices, children);
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
   * Adds a course into the database and in the model.
   * 
   * @param desc
   *          The title of the new course.
   * @param coeff
   *          The coeff of the new course.
   */
  public void addCourse(final String desc, final float coeff) {
    es.execute(new Runnable() {

      public void run() {

        synchronized (lock) {
          Course c = null;

          try {
            c = manager.addCourse(desc, coeff);
          } catch (DataManagerException e) {
            ExceptionDisplayDialog.postException(e);
            return;
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

                CourseTreeModel.this.fireTreeNodesInserted(source, path,
                    childIndices, children);
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

  // /**
  // * @param args
  // */
  // public static void main(String[] args) {
  // JFrame frame = new JFrame ("test StudentModel");
  // frame.setSize(800,600);
  // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  //    
  // DataManager dataManager = SQLDataManager.getInstance();
  // CourseTreeModel model = new CourseTreeModel(dataManager);
  //    
  // JTree tree = new JTree(model);
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
  // JScrollPane pane = new JScrollPane(tree);
  //    
  // frame.setContentPane(pane);
  //    
  // frame.setVisible(true);
  //
  // }

}
