/*
 * This file is part of Symphonie
 * Created : 20-fï¿½vr.-2005 16:13:25
 */
package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeCellRenderer;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.data.formula.SymphonieFormulaFactory;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.Pair;
import fr.umlv.symphonie.util.TextualResourcesLoader;
import fr.umlv.symphonie.view.PointSaver;
import fr.umlv.symphonie.view.SymphonieActionFactory;
import fr.umlv.symphonie.view.SymphonieConstants;
import fr.umlv.symphonie.view.cells.CellFormat;
import fr.umlv.symphonie.view.cells.CellRendererFactory;

/*
 * par convention il est decide
 * que la derniere colonne est toujours la moyenne
 * il faudra donc verifier a l'ajout de nouvelle colonne
 * de bien laisser la moyenne a la fin
 */

public class TeacherModel extends AbstractTableModel {

  private final Map<String, Number> mappedValues = new HashMap<String, Number>();
  
  private static TeacherModel instance = null;
  
  /*
   * le datamanager et
   * la matiere
   */
  protected DataManager manager;
  protected Course course = null;
  
  /*
   * nombre de colonnes et
   * de lignes
   */
  protected int rowCount = 0;
  protected int columnCount = 0;
  
  /*
   * listes des tests
   * et des etudiants
   */
  protected final List<Object> columnList = new ArrayList<Object>();
  protected final List<Student> studentList = new ArrayList<Student>();
  
  /*
   * maps des etudiants et des tests
   * (le final devra servir pour les threads)
   */
  protected final Map<Integer, Mark> markMap = new HashMap<Integer, Mark>();
  protected final SortedMap<Student, Map<Integer, StudentMark>> studentMarkMap =
    new TreeMap<Student, Map<Integer, StudentMark>>(new Comparator<Student>(){
      public int compare(Student o1, Student o2) {
        int n = o1.getLastName().compareToIgnoreCase(o2.getLastName());
        
        if (n == 0)
          n = o1.getName().compareToIgnoreCase(o2.getName());
        
        if (n == 0)
          return o1.getId() - o2.getId();
        
        return n;
      }
    });
  
  
  private final HashMap<Object, CellFormat> formattedObjects = new HashMap<Object, CellFormat>();

  
  /**
   * Pool de threads qui n'en contient qu'un seul et qui sert pour le
   * rafraîchissement du canal courant.
   */
  private final ExecutorService es = Executors.newSingleThreadExecutor();
  
  
  protected final Object lock = new Object();
  
  private TeacherModel(DataManager manager) {
    this.manager = manager;
  }
  
  static public TeacherModel getInstance(final DataManager manager){
    if (instance == null)
      instance = new TeacherModel(manager);
    
    else
      instance.setManager(manager);
    
    return instance;
  }
  
  /**
   * @param manager The manager to set.
   */
  private void setManager(DataManager manager) {
    this.manager = manager;
  }
  
  public HashMap<Object, CellFormat> getFormattedObjects() {
    return formattedObjects;
  }
  
  public void setCourse(final Course courseToAdd) {
    
    
    es.execute(new Runnable(){
      
      /* (non-Javadoc)
       * @see java.lang.Runnable#run()
       */
      public void run() {
        
        synchronized (lock) {

          clear();
    
          course = courseToAdd;
          
          Pair<Map<Integer, Mark>, SortedMap<Student, Map<Integer, StudentMark>>> studentAndMarkMapPair = null;
          try {
            studentAndMarkMapPair = manager.getAllMarksByCourse(course);
          } catch (DataManagerException e) {
            System.out
                .println("error getting data from database for Teacher View.");
            e.printStackTrace();
          }

          markMap.putAll(studentAndMarkMapPair.getFirst());
          studentMarkMap.putAll(studentAndMarkMapPair.getSecond());

          columnList.addAll(markMap.values());

          List<Formula> formulaList;

          try {
            formulaList = manager.getFormulasByCourse(course);
          } catch (DataManagerException e2) {
            formulaList = null;
          }

          if (formulaList != null) {
            
            int column;

            for (Formula f : formulaList) {
              column = f.getColumn();
              if (column < 0)
                columnList.add(0, f);

              else if (column > columnList.size())
                columnList.add(f);

              else
                columnList.add(column - 1, f);
            }

          }

          columnCount = columnList.size() + 2;
          rowCount = studentMarkMap.size() + 3;
          
//          System.out.println("lignes : " + rowCount);
//          System.out.println("colonnes : " + columnCount);

          studentList.addAll(studentMarkMap.keySet());

          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                TeacherModel.this.fireTableStructureChanged();
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
  
  public void printCourse(){
    System.out.println("*********");
    System.out.println(course);
    System.out.println("*********");
  }
  
  public void update(){
    setCourse(course);
  }
  
  protected void clear() {
    course = null;
    rowCount = 0;
    columnCount = 0;
    
    columnList.clear();
    studentList.clear();
    
    studentMarkMap.clear();
    markMap.clear();
  }

  /**
   * @param list
   * @return
   */
  private float getAverage(Collection<StudentMark> collection) {
    
    float result = 0;
    
    for (StudentMark studentMark : collection)
      result += studentMark.getCoeff() * studentMark.getValue();
    
    return result;
  }


  public int getRowCount() {
    return rowCount;
  }

  public int getColumnCount() {
    return columnCount;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    
    /*
     * cas de la ligne separatrice
     */
    if (rowIndex == 2)
      return null;
    
    /*
     * cas de la colonne tout a gauche
     */
    if (columnIndex == 0){
      if (rowIndex == 0)
        return "Intitule";
      if (rowIndex == 1)
        return "Coeff";
      return studentList.get(rowIndex - 3);
    }
    
    /*
     * cas de la colonne tout a droite
     */
    if (columnIndex == columnCount -1){
      if (rowIndex == 0)
        return "Moyenne";
      if (rowIndex == 1)
        return null;
      
      return getAverage(studentMarkMap.get(studentList.get(rowIndex -3)).values());
    }
    
    Object o = columnList.get(columnIndex -1);
    
    if(o instanceof Formula){
      Formula f = (Formula)o;
      
      if (rowIndex == 0){
        return f.getDescription();
      }
      
      if (rowIndex == 1){
        return null;
      }
        
      Student s = studentList.get(rowIndex - 3);
      
      SymphonieFormulaFactory.clearMappedValues();
      
      for (StudentMark sm : studentMarkMap.get(s).values())
        SymphonieFormulaFactory.putMappedValue(sm.getMark().getDesc(), sm.getValue());
      
      return f.getValue();
    }
    
    else if (o instanceof Mark){
      Mark m = (Mark)o;
      
      if (rowIndex == 0)
        return m;
      if (rowIndex == 1)
        return m.getCoeff();
      return studentMarkMap.get(studentList.get(rowIndex - 3)).get(m.getId()).getValue();
    }
    
    return null;
    
    
  }

  
  
  /* (non-Javadoc)
   * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    
    /*
     * Cas ou c'est pas editable :
     */
    if (columnIndex == 0 ||
        rowIndex == 2 ||
        columnIndex == columnCount -1 /*&& rowIndex <= 1*/ )
      return false;
    
    else {
      Object o = columnList.get(columnIndex - 1);
      
      if (o instanceof Formula && rowIndex == 1)
        return false;
    }

    return true;
  }
  
  
  
  /* (non-Javadoc)
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    Object o = columnList.get(columnIndex - 1);
    
    
    /*
     * cas des intitules :
     */
    if (rowIndex == 0){
      
      
      if (o instanceof Formula)
        return; // changer ca
      
      try {
        manager.changeMarkDescription((Mark)o, (String)aValue);
      } catch (DataManagerException e) {
        System.out.println("Error while attempting to modify the mark name to " + aValue);
        e.printStackTrace();
      }
      return;
    }
    
    /*
     * cas des coeffs
     */
    else if (rowIndex == 1){
      
      if (o instanceof Formula)
        return; // changer ca
      
      
      float newCoeff;
      
      try {
        newCoeff = Float.parseFloat((String)aValue);
      }catch (NumberFormatException e){
        return;
      }
      try {
        manager.changeMarkCoeff((Mark)o, newCoeff);
      }catch (DataManagerException e){
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
      fireTableRowsUpdated(1, rowCount - 1);
      return;
    }
    
    /*
     * cas des notes
     */
    if (o instanceof Formula)
      return;
    
    float newValue;
    
    try {
      newValue = Float.parseFloat((String)aValue);
    }catch (NumberFormatException e){
      return;
    }
    
    Mark m = (Mark)o;
    
    try {
      manager.changeStudentMarkValue(studentMarkMap.get(studentList.get(rowIndex - 3)).get(m.getId()), newValue);
    } catch (DataManagerException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
    
    fireTableRowsUpdated(rowIndex, rowIndex);
  }
  
  
  public void addFormula(final String expression, final String desc, final int column){
    
    es.execute(new Runnable() {

      public void run() {
        synchronized (lock) {

          try {
            manager.addTeacherFormula(expression, desc, course, column);
          } catch (DataManagerException e) {
            System.out.println(e.getMessage());
          }

          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                TeacherModel.this.update();
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
  
  public void addMark(final String desc, final float coeff){
    
    es.execute(new Runnable() {

      public void run() {
        synchronized (lock) {

          printCourse();
          
          if (course != null){
            try {
              manager.addMark(desc, coeff, course);
            } catch (DataManagerException e) {
              System.out.println(e.getMessage());
            }
            
            try {
              EventQueue.invokeAndWait(new Runnable() {

                public void run() {
                  TeacherModel.this.update();
                }

              });
            } catch (InterruptedException e1) {
              e1.printStackTrace();
            } catch (InvocationTargetException e1) {
              e1.printStackTrace();
            }
          }
        }
      }
    });
  }
  
  public void removeColumn(int columnIndex){
    if (columnIndex == 0 || columnIndex == columnCount -1)
      return;
    
    columnIndex--;
    
    Object o = columnList.get(columnIndex);
    
    if (o instanceof Mark)
      removeMark((Mark)o);
    
    else removeFormula((Formula)o);
    
  }
  
  /**
   * @param formula
   */
  private void removeFormula(Formula formula) {
    
    
  }

  /**
   * @param mark
   */
  private void removeMark(final Mark mark) {
    es.execute(new Runnable() {

      public void run() {
        synchronized (lock) {
          try{
            manager.removeMark(mark);
          }catch (DataManagerException e){
            System.out.println(e.getMessage());
          }
          
          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                TeacherModel.this.update();
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

  public static void main(String[] args) throws DataManagerException, IOException {
    JFrame frame = new JFrame ("test TeacherModel");
    frame.setSize(800,600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    DataManager dataManager = SQLDataManager.getInstance();
    
//    /*
//     * test des etudiants
//     */
//    Map<Integer, Student> studentMap = dataManager.getStudents();
//    System.out.println(studentMap.size() + " etudiants.");
//    for (Student s : studentMap.values())
//      System.out.println(s);
//    System.out.println("\n\n");
//    /*******************************/
//    
//    
//     /*
//      * test des matieres
//      */
//     Map<Integer, Course> courseMap = dataManager.getCourses();
//     System.out.println(courseMap.size() + " matieres");
//     for (Course c : courseMap.values()){
//       System.out.println(c + " " + c.getCoeff());
//       System.out.println(c.getId());
//     }
//     System.out.println("\n\n");
//    /***************************/
//    
//    /*
//     * test des epreuves
//     */
//    Map<Integer, Mark> markMap = dataManager.getMarks();
//    System.out.println(markMap.size() + " epreuves");
//    for (Mark m : markMap.values())
//      System.out.println(m + " pour " + m.getCourse());
//    System.out.println("\n\n");
//    /******************************/
//    
//    /*
//     * test des notes
//     */
//    List<StudentMark> list = dataManager.getStudentMarks();
//    System.out.println(list.size() + " notes.");
//    
//    for (StudentMark sm : list){
//      sm.printData();
//    }
//    
//    System.out.println("\n\n");
//    /******************************/
    
    
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    
    
    
    /*
     * table
     */
    
    final TeacherModel teacherModel = TeacherModel.getInstance(dataManager);
    
    /*Course course = new Course (0, "java", 0.5f);

    teacherModel.setCourse(course);*/
    
    final JTable table = new JTable(teacherModel);
    table.setTableHeader(null);
    
    HashMap<String, String> map = TextualResourcesLoader.getResourceMap("language/symphonie", new Locale(
    "french"), "ISO-8859-1");
    
    ComponentBuilder builder = new ComponentBuilder(map);
    
    final JPopupMenu pop = builder.buildPopupMenu(SymphonieConstants.TEACHERVIEWPOPUP_TITLE);
    
    pop.add(builder.buildButton(SymphonieActionFactory.getAddMarkAction(null,frame, builder ), SymphonieConstants.ADDMARKDIALOG_TITLE, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(SymphonieActionFactory.getRemoveTeacherColumnAction(null,table, builder), SymphonieConstants.REMOVE_COLUMN, ComponentBuilder.ButtonType.MENU_ITEM));
    
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          pop.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });
    
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          PointSaver.setPoint(e.getPoint());
        }
      }
    });
    
    table.setDefaultRenderer(Object.class, CellRendererFactory.getTeacherModelCellRenderer(teacherModel.getFormattedObjects()));
    
    
    JScrollPane scroll1 = new JScrollPane(table);
    
    split.setRightComponent(scroll1);
    
    
    
    /*
     * arbre
     */
    CourseTreeModel courseModel = new CourseTreeModel(dataManager);
    
    final JTree tree = new JTree(courseModel);
    
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
    
    
    tree.addTreeSelectionListener(new TreeSelectionListener(){
      
      private Object o;

      public void valueChanged(TreeSelectionEvent e) {
        o = tree.getLastSelectedPathComponent();
        
        if (o instanceof Course){
          ((TeacherModel)table.getModel()).setCourse((Course)o);
          /*((TeacherModel)table.getModel()).addMark("morpion", 0.0f);*/
          /*CellFormat f = new CellFormat(BasicFormulaFactory.booleanInstance(true), Color.RED, Color.CYAN);
          for (int i = 3; i < 7; i++) {
            Object ob = teacherModel.getValueAt(i, 0);
            teacherModel.getFormattedObjects().put(ob, f);
          }*/
        }
      }
      
    });
    
    JScrollPane pane = new JScrollPane(tree);
    
    split.setLeftComponent(pane);
    
    frame.setContentPane(split);
    
    frame.setVisible(true);
  }
  
  

  

}
