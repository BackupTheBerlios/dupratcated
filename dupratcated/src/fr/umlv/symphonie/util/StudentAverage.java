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
 * A class used to calculate the average of a student. Can calculate an average
 * for course, or global average.
 * 
 * @author susmab
 * 
 */
public class StudentAverage {

  /**
   * Private constructor.
   */
  private StudentAverage() {
  }

  /**
   * Used to get the average of a student for a given course.
   * 
   * @param collection
   *          the <code>Collection</code> of all <code>StudentMarks</code> s
   *          in the given course.
   * @return the average for the marks contained in the <code>Collection</code>.
   *         Involves the coefficients.
   */
  public static float getAverage(Collection<StudentMark> collection) {
    float result = 0;
    float div = 0;

    if (collection == null || collection.isEmpty()) return 0;

    for (StudentMark sm : collection) {
      result += sm.getValue() * sm.getCoeff();
      div += sm.getCoeff();
    }

    return Math.round(100 * (result / div)) / 100f;
  }

  /**
   * Used to calculate the global average of a student, by all his marks in
   * every courses.
   * 
   * @param map
   *          a <code>Map</code> of all marks for the student, keyed by the
   *          courses.
   * @return the annual average for the student. Involves the coefficients.
   */
  public static float getAnnualAverage(
      Map<Course, Map<Integer, StudentMark>> map) {
    float result = 0;
    float div = 0;

    if (map == null || map.isEmpty()) return 0;

    for (Course c : map.keySet()) {
      result += getAverage(map.get(c).values()) * c.getCoeff();
      div += c.getCoeff();
    }

    return Math.round(100 * (result / div)) / 100f;
  }
}
