/*
 * This file is part of Symphonie
 * Created : 27-févr.-2005 15:26:50
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

import fr.umlv.symphonie.data.*;
import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.util.*;


/**
 * @author susmab
 *
 */
public class JuryModel extends AbstractTableModel {

  private final DataManager manager;
  
  private int rowCount    = 0;
  private int columnCount = 0;
  private int globalTimeStamp = -1; // le globalTimeStamp pourrait etre la somme de
                                    // tous les timeStamp (a voir)
  
  private final List<Object> columnList = new ArrayList<Object>();
  private final List<Student> studentList = new ArrayList<Student>();
  private final Map<Student, Map<Course, Map<Integer, StudentMark>>> dataMap = new HashMap<Student, Map<Course, Map<Integer, StudentMark>>>();
  
  
  public JuryModel(DataManager manager) {
    this.manager = manager;
    update();
  }
  
  
  
  
  public void update() {
    Pair<Map<Integer, Course>, SortedMap<Student, Map<Course, Map<Integer, StudentMark>>>> allData = null;
    try {
      allData = manager.getAllStudentsMarks();
    } catch (DataManagerException e) {
      System.out.println("Error getting data for Jury View");
      e.printStackTrace();
    }
    
    columnList.addAll(allData.getFirst().values());
    studentList.addAll(allData.getSecond().keySet());
    dataMap.putAll(allData.getSecond());
    
    /*
     * ici ajouter les formules de la vue jury
     */
    
    dataMap.putAll(allData.getSecond());
    
    columnCount = columnList.size() + 4;
    rowCount = 3 + studentList.size();
  }




  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getRowCount()
   */
  public int getRowCount() {
    return rowCount;
  }

  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  public int getColumnCount() {
    return columnCount;
  }

  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
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
        return "Matiere";
      if (rowIndex == 1)
        return "Coeff";
      return studentList.get(rowIndex - 3);
    }
    
    
    /*
     * cas de la colonne tout a droite
     * (commentaires)
     */
    if (columnIndex == columnCount - 1){
      if (rowIndex == 0)
        return "Commentaires";
      if (rowIndex == 1)
        return null;
      
      return studentList.get(rowIndex - 3).getComment();
    }
    
    
    /*
     * cas de l'avant derniere colonne
     * (etudiants)
     */
    if (columnIndex == columnCount - 2){
      if (rowIndex <= 1)
        return null;
      
      return studentList.get(rowIndex - 3);
    }
    
    
    /*
     * cas de la colonne des moyennes
     * (avant avant derniere colonne)
     */
    
    if (columnIndex == columnCount -3){
      if (rowIndex == 0)
        return "Moyenne";
      if (rowIndex <= 2)
        return null;
      
      float result = 0;
      
      for (int i = 0; i < columnList.size() ; i++){
        if (columnList.get(i) instanceof Course)
          result += ((Course)columnList.get(i)).getCoeff() * (Float)getValueAt(rowIndex, i + 1);
      }
      return result;
    }
    
    
    /*
     * autres cas
     */
    Object o = columnList.get(columnIndex - 1);
    
    if (o instanceof Formula){
      /*
       * blablah
       */
      return null;
    }
    
    Course c = (Course)o;
    
    if(rowIndex == 0)
      return c;
    if(rowIndex == 1)
      return c.getCoeff();
    
    return getAverage(dataMap.get(studentList.get(rowIndex - 3)).get(c).values());
  }

  private float getAverage(Collection<StudentMark> name) {
    
    float result = 0;
    
    for (StudentMark sm : name){
      result += sm.getValue() * sm.getCoeff();
    }
    
    return result;
  }

  
  public boolean isCellEditable(int rowIndex,int columnIndex){
    if (columnIndex == columnCount - 1 && rowIndex > 2)
      return true;

    return false;
  }

  
  public void setValueAt(Object aValue,int rowIndex,int columnIndex){
    Student s = studentList.get(rowIndex - 3);
    
    try {
      manager.changeStudentComment(s, (String)aValue);
    } catch (DataManagerException e) {
      e.getMessage();
      e.printStackTrace();
    }
  }
  
  

  /**
   * @param args
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame ("test JuryModel");
    frame.setSize(800,600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    DataManager dataManager = new SQLDataManager();

    JuryModel model = new JuryModel(dataManager);
    
    JTable table = new JTable(model);
    table.setTableHeader(null);
    
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
      
      public java.awt.Component getTableCellRendererComponent(JTable table,Object value,
          boolean isSelected,boolean hasFocus,int row,int column){
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
          label.setHorizontalAlignment(SwingConstants.CENTER);
          
          if (column == 0 || row == 0 || column == table.getModel().getColumnCount() - 2)
            label.setFont(getFont().deriveFont(Font.BOLD));
          
          return label;
      }
    });
    
    
    JScrollPane scroll = new JScrollPane(table);
    
    frame.setContentPane(scroll);
    
    frame.setVisible(true);
  }

}
