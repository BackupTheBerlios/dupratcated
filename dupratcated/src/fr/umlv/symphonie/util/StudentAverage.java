/*
 * This file is part of Symphonie
 * Created : 20-mars-2005 15:39:18
 */
package fr.umlv.symphonie.util;

import java.util.Collection;
import java.util.Map;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.StudentMark;


/**
 * @author susmab
 *
 */
public class StudentAverage {

  private StudentAverage(){
  }
  
  
  public static float getAverage(Collection<StudentMark> collection){
    float result = 0;
    
    if (collection == null)
      return 0;
    
    for (StudentMark sm : collection)
      result += sm.getValue() * sm.getCoeff();
    
    return result;
  }
  
  
  public static float getAnnualAverage(Map<Course, Map<Integer, StudentMark>> map){
    float result = 0;
    
    if (map == null)
      return 0;
    
    for (Course c : map.keySet())
      result += getAverage(map.get(c).values()) * c.getCoeff();
    
    return result;
  }
}
