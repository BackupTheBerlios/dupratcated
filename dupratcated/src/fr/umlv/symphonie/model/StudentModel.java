/*
 * This file is part of Symphonie
 * Created : 15 f�vr. 2005 17:57:09
 */
package fr.umlv.symphonie.model;

import java.util.*;

import java.awt.Font;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import fr.umlv.symphonie.data.*;


/**
 * @author fvallee
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StudentModel extends AbstractTableModel {

	private final DataManager manager;
  private Student student = null;
  private int columnNumber = 0;
  private int rowNumber = 0;
  Object[][] matrix = null;
  
  
  
  public StudentModel(DataManager manager) {
    this.manager = manager;
  }
  
  
  public void setStudent (Student s){
    
    clear();
    
    student = s;
    
    Map<Course, Map<Integer, StudentMark>> markMap = null;
    try {
      markMap = manager.getAllMarksByStudent(student);
    } catch (DataManagerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    
    int n;
    for (Course c : markMap.keySet()){
      if ( ( n = markMap.get(c).size() ) > columnNumber)
        columnNumber = n;
    }
    
    columnNumber += 2;
    rowNumber = 4 * markMap.size();
    
    matrix = new Object[rowNumber][columnNumber];
    
    int row = 0;
    int column;
    Collection<StudentMark> collection = null;

    for (Course c : markMap.keySet()){
      
      column = 0;
      collection = markMap.get(c).values();
      
      /*
       * On met les donnees dans la premiere colonne
       * (matiere, coeff, note)
       */
      matrix[row][column] = c;
      matrix[row+1][column] = "coeff";
      matrix[row+2][column] = "note";
      
      column++;
      
      /*
       * On remplit les cases de la matrice avec les notes
       */
      for (StudentMark mark : collection){
        matrix[row][column] = mark.getMark();
        matrix[row+1][column] = mark.getCoeff();
        matrix[row+2][column] = mark;
        column++;
      }
      
      /*
       * On remplit le reste des cases
       * de bon gros vide
       */
      blankRow(row, column);
      blankRow(row + 1, column);
      blankRow(row + 2, column);
      blankRow(row + 3, 0);
      
      matrix[row][columnNumber-1] = "moyenne";
      matrix[row+1][columnNumber-1] = "";
      matrix[row+2][columnNumber-1] = getAverage(collection);
      
      row +=4;
      
    }
    fireTableStructureChanged();
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


  /**
   * @param row
   * @param column
   */
  private void blankRow(int row, int column) {
    for (;column < columnNumber-1 ; column++)
      matrix[row][column] = "";
    
  }


  public void clear(){
    columnNumber = 0;
    rowNumber = 0;
    student = null;
    matrix = null;
  }
  
  
  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getRowCount()
   */
  public int getRowCount() {
    return rowNumber;
  }

  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  public int getColumnCount() {
    return columnNumber;
  }

  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    return matrix[rowIndex][columnIndex];
  }

  
  public static void main(String[] args) {
    JFrame frame = new JFrame ("test StudentModel");
    frame.setSize(800,600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    DataManager dataManager = new SQLDataManager();
    StudentModel model = new StudentModel(dataManager);
    
    Map<Integer, Student> studentMap = dataManager.getStudents();
    
    Student student = studentMap.get(0);
    

    
    
    model.setStudent(student);
    
    JTable table = new JTable(model);
    table.setTableHeader(null);
    
    
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
      
      public java.awt.Component getTableCellRendererComponent(JTable table,Object value,
          boolean isSelected,boolean hasFocus,int row,int column){
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
          label.setHorizontalAlignment(SwingConstants.CENTER);
          
          if (column == 0 || row % 4 == 0)
            label.setFont(getFont().deriveFont(Font.BOLD));
          
          return label;
      }
    });
    
    
    JScrollPane scroll = new JScrollPane(table);
    
    frame.setContentPane(scroll);
    
    frame.setVisible(true);
  }
  
  
}
