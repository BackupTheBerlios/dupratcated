/*
 * This file is part of Symphonie
 * Created : 27-févr.-2005 15:26:50
 */
package fr.umlv.symphonie.model;

import java.util.*;

import javax.swing.table.AbstractTableModel;

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
        if (columnList.get(i) instanceof Mark)
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


  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
