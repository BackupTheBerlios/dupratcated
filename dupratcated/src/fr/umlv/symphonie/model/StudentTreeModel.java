/*
 * This file is part of Symphonie
 * Created : 07-mars-2005 18:19:11
 */
package fr.umlv.symphonie.model;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;


/**
 * @author susmab
 *
 */
public class StudentTreeModel implements TreeModel {

  
  private final List<TreeModelListener> listenerList = new ArrayList<TreeModelListener>();
  
  private final String root = "Etudiants"; // a changer pour l'internationalisation ?
  private final DataManager manager;
  private List<Student> studentList = null;
  
  public StudentTreeModel(DataManager manager){
    this.manager = manager;
    try {
      studentList = manager.getStudentList();
    } catch (DataManagerException e) {
      System.out.println("error getting students list from database. Try updating the list.");
    }
  }
  
  
  public void update() {
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
  public void valueForPathChanged(TreePath path, Object newValue) {
    // TODO Auto-generated method stub

  }

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
    StudentTreeModel model = new StudentTreeModel(dataManager);
    
    JTree tree = new JTree(model);
    
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
    
    frame.setContentPane(pane);
    
    frame.setVisible(true);
  }

}
