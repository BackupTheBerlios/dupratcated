/*
 * This file is part of Symphonie
 * Created : 21-mars-2005 1:06:05
 */
package fr.umlv.symphonie.model;

import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.formula.Formula;


/**
 * @author susmab
 *
 */
public class AdminTeacherModel extends TeacherModel {

  private static AdminTeacherModel instance = null;
  
  
  private AdminTeacherModel (DataManager manager){
    super(manager);
  }
  
  public static AdminTeacherModel getInstance(DataManager manager){
    if (instance == null)
      instance = new AdminTeacherModel(manager);
    
    else instance.setManager(manager);
    
    return instance;
  }
  
  public boolean isCellEditable(int rowIndex,int columnIndex){
    
    if (columnIndex == 0 && rowIndex >= 3)
      return true;
    
    /*
     * Cas ou c'est pas editable :
     */
    if (columnIndex == 0 ||
        rowIndex == 2 ||
        columnIndex == columnCount -1 /*&& rowIndex <= 1*/ )
      return false;
    

    Object o = columnList.get(columnIndex - 1);

    if (o instanceof Formula && rowIndex == 1) return false;


    return true;
  }
  
  public void setValueAt(Object aValue,int rowIndex,int columnIndex){
    
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
