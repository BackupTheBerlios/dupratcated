/*
 * This file is part of Symphonie
 * Created : 08-mars-2005 0:54:56
 */
package fr.umlv.symphonie.model;

import java.awt.Font;
import java.util.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import fr.umlv.symphonie.data.*;


/**
 * @author susmab
 *
 */
public class CourseTreeModel implements TreeModel {

  
  private final List<TreeModelListener> listenerList = new ArrayList<TreeModelListener>();
  
  private final String root = "Matieres";
  private final DataManager manager;
  private List<Course> courseList = null;
  
  
  
  public CourseTreeModel(DataManager manager) {
    this.manager = manager;
    
    try {
      courseList = manager.getCoursesList();
    }catch (DataManagerException e){
      System.out.println("error getting courses from database. try updating course list later.");
    }
  }

  
  public void update(){
    if (courseList == null){
      try {
        courseList = manager.getCoursesList();
      } catch (DataManagerException e) {
        System.out.println("error getting courses list from database. Try updating the list.");
      }
    }
    
    else {
      try {
        manager.getCoursesList();
      } catch (DataManagerException e) {
        System.out.println("error getting courses list from database. Try updating the list.");
      }
    }
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

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
   */
  public void valueForPathChanged(TreePath path, Object newValue) {
  }

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
   */
  public int getIndexOfChild(Object parent, Object child) {
    if (root.equals(parent) == false
        || child instanceof Course == false)
    return -1;
    
    return courseList.indexOf(child);
  }

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
   */
  public void addTreeModelListener(TreeModelListener l) {
    listenerList.add(l);
  }

  /* (non-Javadoc)
   * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
   */
  public void removeTreeModelListener(TreeModelListener l) {
    listenerList.remove(l);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame ("test StudentModel");
    frame.setSize(800,600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    DataManager dataManager = SQLDataManager.getInstance();
    CourseTreeModel model = new CourseTreeModel(dataManager);
    
    JTree tree = new JTree(model);
    
    tree.setCellRenderer(new DefaultTreeCellRenderer(){
      
      private final Icon leafIcon = new ImageIcon(StudentTreeModel.class.getResource("../view/icons/course.png"));
      private final Icon rootIcon = new ImageIcon(StudentTreeModel.class.getResource("../view/icons/courses.png"));
      
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
    
    frame.setContentPane(pane);
    
    frame.setVisible(true);

  }

}
