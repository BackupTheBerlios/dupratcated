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
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.SgainDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;



public class TeacherModel extends AbstractTableModel {

  private DataManager manager;
  private Course course = null;
  
  private int rowCount = 0;
  private int columnCount = 0;
  
  private Object[][] matrix = null;
  
  
  
  
  public TeacherModel(DataManager manager) {
    this.manager = manager;
  }
  
  
  
  
  
  
  public void setCourse(Course course) {
    
    clear();
    
    this.course = course;
    
    SortedMap<Mark, Float> markMap = manager.getTitlesByCourse(course);
    SortedMap<Student, Map<String, StudentMark>> studentAndMarkMap = manager.getAllMarksByCourse(course);
    
    columnCount = markMap.size() + 2;
    rowCount = studentAndMarkMap.size() + 3;
    
    matrix = new Object[rowCount][columnCount];
    
    matrix[0][0] = "Intitul�";
    matrix[1][0] = "Coeff";
    /*Mark[] markTab = new Mark[1];
    markMap.keySet().toArray(markTab);*/
    
    int column = 1;
    int row = 0;
    
    
    /*
     * On remplit la partie haute du modele
     * (celle avec les intitules et les coeff)
     */
    for (Mark m : markMap.keySet()){
      matrix[row][column] = m;
      matrix[row + 1][column] = markMap.get(m);
      column++;
    }
    
    matrix[row][column] = "Moyenne";
    matrix[row + 1][column] = "";
    
    blankRow(row+2,0 );
    row += 3;
    
    /* fin du remplissage de la partie haute */
    
    
    /*
     * On remplit le reste des donn�es
     * concernant les etudiants
     */
    for (Student s : studentAndMarkMap.keySet()){
      matrix[row][0] = s;
      for (column = 1 ; column < columnCount -1 ; column++){
        matrix[row][column] = (studentAndMarkMap.get(s)).get( ((Mark)matrix[0][column]).getDesc() );
      }
      
      matrix[row][columnCount-1] = getAverage(studentAndMarkMap.get(s).values());
      
      row++;
    }
  }
  
  
  private void clear() {
    course = null;
    rowCount = 0;
    columnCount = 0;
    matrix = null;
  }

  private void blankRow(int row, int column) {
    for (;column < columnCount-1 ; column++)
      matrix[row][column] = "";
    
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
    return matrix[rowIndex][columnIndex];
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame ("test TeacherModel");
    frame.setSize(800,600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    DataManager dataManager = new SgainDataManager();
    TeacherModel model = new TeacherModel(dataManager);
    
    Course course = new Course(1, "Java", 0.25f);

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
