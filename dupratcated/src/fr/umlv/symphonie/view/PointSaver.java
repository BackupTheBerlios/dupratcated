/*
 * This file is part of Symphonie
 * Created : 18-mars-2005 1:09:14
 */
package fr.umlv.symphonie.view;

import java.awt.Point;


/**
 * @author susmab
 *
 */
public class PointSaver {

  private static Point point = null;
  
  public static Point getPoint(){
    return point;
  }
  
  
  public static void setPoint(Point p){
    point = p;
  }
  
  public static void reset(){
    point = null;
  }
}
