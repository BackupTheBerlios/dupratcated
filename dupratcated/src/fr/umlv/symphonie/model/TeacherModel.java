/*
 * This file is part of Symphonie
 * Created : 20-f�vr.-2005 16:13:25
 */
package fr.umlv.symphonie.model;

import java.awt.Font;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.util.Pair;

/*
 * par convention il est decide
 * que la derniere colonne est toujours la moyenne
 * il faudra donc verifier a l'ajout de nouvelle colonne
 * de bien laisser la moyenne a la fin
 */

public class TeacherModel extends AbstractTableModel {

  /*
   * le datamanager et
   * la matiere
   */
  private DataManager manager;
  private Course course = null;
  
  /*
   * nombre de colonnes et
   * de lignes
   */
  private int rowCount = 0;
  private int columnCount = 0;
  
  /*
   * listes des tests
   * et des etudiants
   */
  private final List<Object> columnList = new ArrayList<Object>();
  private final List<Student> studentList = new ArrayList<Student>();
  
  /*
   * maps des etudiants et des tests
   * (le final devra servir pour les threads)
   */
  private final Map<Integer, Mark> markMap = new HashMap<Integer, Mark>();
  private final SortedMap<Student, Map<Integer, StudentMark>> studentMarkMap =
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
  

  
  
  
  
  public TeacherModel(DataManager manager) {
    this.manager = manager;
  }
  
  
  public void setCourse(Course course) {
    
    clear();
    
    this.course = course;
    
    /*Map<Integer, Mark> markMap = null;
    try {
      markMap = manager.getMarksByCourse(course);
    } catch (DataManagerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }*/
    
    Pair<Map<Integer, Mark>, SortedMap<Student, Map<Integer, StudentMark>>> studentAndMarkMapPair = null;
    try {
      studentAndMarkMapPair = manager.getAllMarksByCourse(course);
    } catch (DataManagerException e) {
      System.out.println("error getting data from database for Teacher View.");
      e.printStackTrace();
    }
    
    
    markMap.putAll(studentAndMarkMapPair.getFirst());
    studentMarkMap.putAll(studentAndMarkMapPair.getSecond());
    
    
    columnCount = markMap.size() + 2;
    rowCount = studentMarkMap.size() + 3;
    
    System.out.println("lignes : " + rowCount);
    System.out.println("colonnes : " + columnCount);
    
    

    
    
    columnList.addAll(markMap.values());
    
    /*Collections.sort(columnList, new Comparator<Mark>(){
      public int compare(Mark arg0,Mark arg1){
        int n = arg0.getDesc().compareToIgnoreCase(arg1.getDesc());
        
        if (n == 0){
          return arg0.getId() - arg1.getId();
        }
        
        return n;
      }
    });*/

    studentList.addAll(studentMarkMap.keySet());
  
    
    
    
    
    
    
//    matrix = new Object[rowCount][columnCount];
//    
//    matrix[0][0] = "Intitule";
//    matrix[1][0] = "Coeff";
//
//    
//    int column = 1;
//    int row = 0;
//    
//    
//    /*
//     * On remplit la partie haute du modele
//     * (celle avec les intitules et les coeff)
//     */
//    for (int n : markMap.keySet()){
//      matrix[row][column] = markMap.get(n);
//      matrix[row + 1][column] = markMap.get(n).getCoeff();
//      column++;
//    }
//    
//    matrix[row][column] = "Moyenne";
//    matrix[row + 1][column] = "";
//    
//    blankRow(row+2,0 );
//    row += 3;
//    
//    /* fin du remplissage de la partie haute */
//    
//    
//    
//    
//    SortedMap<Student, Map<Integer, StudentMark>> studentAndMarkMap = studentAndMarkMapPair.getSecond();
//    /*
//     * On remplit le reste des donn�es
//     * concernant les etudiants
//     */
//    for (Student s : studentAndMarkMap.keySet()){
//      matrix[row][0] = s;
//      for (column = 1 ; column < columnCount -1 ; column++){
//        matrix[row][column] = (studentAndMarkMap.get(s)).get( ((Mark)matrix[0][column]).getId() );
//      }
//      
//      matrix[row][columnCount-1] = getAverage(studentAndMarkMap.get(s).values());
//      
//      row++;
//    }
  }
  
  
  private void clear() {
    course = null;
    rowCount = 0;
    columnCount = 0;
    
    columnList.clear();
    studentList.clear();
    
    studentMarkMap.clear();
    markMap.clear();
  }

  /*private void blankRow(int row, int column) {
    for (;column < columnCount-1 ; column++)
      matrix[row][column] = "";
    
  }*/

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
      /*
       * blablah
       */
      return null;
    }
    
    else if (o instanceof Mark){
      Mark m = (Mark)o;
      
      if (rowIndex == 0)
        return m;
      if (rowIndex == 1)
        return m.getCoeff();
      return studentMarkMap.get(studentList.get(rowIndex - 3)).get(m.getId());
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
  
  
  public static void main(String[] args) throws DataManagerException {
    JFrame frame = new JFrame ("test TeacherModel");
    frame.setSize(800,600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    DataManager dataManager = new SQLDataManager();
    
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
//    /*
//     * test des matieres
//     */
//    Map<Integer, Course> courseMap = dataManager.getCourses();
//    System.out.println(courseMap.size() + " matieres");
//    for (Course c : courseMap.values())
//      System.out.println(c + " " + c.getCoeff());
//    System.out.println("\n\n");
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
    
    TeacherModel model = new TeacherModel(dataManager);
    
    Course course = new Course (0, "java", 0.5f);

    model.setCourse(course);
    
    JTable table = new JTable(model);
    table.setTableHeader(null);
    
    
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
      
      public java.awt.Component getTableCellRendererComponent(JTable table,Object value,
          boolean isSelected,boolean hasFocus,int row,int column){
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
          label.setHorizontalAlignment(SwingConstants.CENTER);
          
          if (column == 0 || row == 0)
            label.setFont(getFont().deriveFont(Font.BOLD));
          
          return label;
      }
    });
    
    
    JScrollPane scroll = new JScrollPane(table);
    
    frame.setContentPane(scroll);
    
    frame.setVisible(true);
  }
  
  

  

}
