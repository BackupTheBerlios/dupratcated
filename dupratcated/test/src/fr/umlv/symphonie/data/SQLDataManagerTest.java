/*
 * This file is part of Symphonie
 * Created : 25 févr. 2005 00:24:27
 */
package fr.umlv.symphonie.data;

import static fr.umlv.symphonie.data.SQLDataManagerConstants.TABLE_COURSE;
import static fr.umlv.symphonie.data.SQLDataManagerConstants.TABLE_HAS_MARK;
import static fr.umlv.symphonie.data.SQLDataManagerConstants.TABLE_STUDENT;
import static fr.umlv.symphonie.data.SQLDataManagerConstants.TABLE_TEST;

import java.sql.SQLException;
import java.util.ArrayList;

import junit.framework.TestCase;




/**
 * @author Propriétaire
 *
 */
public class SQLDataManagerTest extends TestCase {

  private final static SQLDataManager manager = SQLDataManager.getInstance();
  
  private static Student student = null;
  private static Course course = null;
  private static Mark mark = null;
  
  
  
  
  public void testGetStudentList() throws SQLException, DataManagerException {
    int n = manager.sizeOfTable(TABLE_STUDENT);
    
    assertEquals(n, manager.getStudentList().size());
  }

  public void testGetCoursesList() throws SQLException, DataManagerException {
    int n = manager.sizeOfTable(TABLE_COURSE);
    
    assertEquals(n, manager.getCoursesList().size());
  }

  public void testGetMarks() throws SQLException, DataManagerException {
    int n = manager.sizeOfTable(TABLE_TEST);
    
    assertEquals(n, manager.getMarks().size());
  }

  public void testGetStudentMarks() throws SQLException, DataManagerException {
    int n = manager.sizeOfTable(TABLE_HAS_MARK);
    
    assertEquals(n, manager.getStudentMarks().size());
  }

  public void testAddStudent() throws DataManagerException, SQLException {
    
    int studentCount = manager.sizeOfTable(TABLE_STUDENT);
    int testsCount = manager.sizeOfTable(TABLE_TEST);
    int studentMarksCount = manager.sizeOfTable(TABLE_HAS_MARK);
    
    student = manager.addStudent("test", "test");
    
    assertEquals(studentCount + 1, manager.sizeOfTable(TABLE_STUDENT));
    assertEquals(studentMarksCount + testsCount, manager.sizeOfTable(TABLE_HAS_MARK));
  }



  public void testAddCourse() throws SQLException, DataManagerException {
    int n = manager.sizeOfTable(TABLE_COURSE);
    
    course = manager.addCourse("testCourse", 0f);
    
    assertEquals(n + 1, manager.sizeOfTable(TABLE_COURSE));
  }



  public void testAddMark() throws SQLException, DataManagerException {
    int testsCount = manager.sizeOfTable(TABLE_TEST);
    int studentMarksCount = manager.sizeOfTable(TABLE_HAS_MARK);
    int studentCount = manager.sizeOfTable(TABLE_STUDENT);
    
    if (course != null){
    mark = manager.addMark("testMark", 0f, course);
    
    assertEquals(testsCount + 1, manager.sizeOfTable(TABLE_TEST));
    assertEquals(studentMarksCount + studentCount, manager.sizeOfTable(TABLE_HAS_MARK));
    
    }
  }



  public void testChangeStudentMarkValue() throws DataManagerException {
    StudentMark sm;
    
    if (student != null && course != null) {
      sm = new ArrayList<StudentMark>(manager.getMarksByStudentAndCourse(
          student, course).values()).get(0);
      float oldValue = sm.getValue();
      
      manager.changeStudentMarkValue(sm,oldValue + 99);
      assertEquals(oldValue + 99, sm.getValue());
    }
  }

  public void testChangeMarkDescriptionAndCoeff() throws DataManagerException {
    if (mark != null){
      String newTitle = "testNewTitle";
      float newCoeff = 99f;
      
      manager.changeMarkDescriptionAndCoeff(mark, newTitle, newCoeff);
      
      assertEquals(newTitle, mark.getDesc());
      assertEquals(true, newCoeff == mark.getCoeff());
    }
  }

  public void testChangeMarkDescription() throws DataManagerException {
    if (mark != null){
      String newTitle = "testNewTitle2";
      
      manager.changeMarkDescription(mark, newTitle);
      
      assertEquals(newTitle, mark.getDesc());
    }
  }

  public void testChangeMarkCoeff() throws DataManagerException {
    if (mark != null){
      float newCoeff = 999f;
      
      manager.changeMarkCoeff(mark, newCoeff);
      
      assertEquals(true, newCoeff == mark.getCoeff());
    }
  }

  public void testChangeStudentNameAndLastNameAndComment() throws DataManagerException {
    if (student != null){
      String newName = "newName";
      String newLastName = "newLastName";
      String newComment = "newComment";
      
      manager.changeStudentNameAndLastNameAndComment(student, newName, newLastName, newComment);
      
      assertEquals(newName, student.getName());
      assertEquals(newLastName, student.getLastName());
      assertEquals(newComment, student.getComment());
    }
  }

  public void testChangeStudentComment() throws DataManagerException {
    if (student != null){
      String newComment = "newComment2";
      
      manager.changeStudentComment(student, newComment);
      
      assertEquals(newComment, student.getComment());
    }
  }

  public void testChangeStudentName() throws DataManagerException {
    if (student != null){
      String newName = "newName2";
      
      manager.changeStudentName(student, newName);
      
      assertEquals(newName, student.getName());
    }
  }

  public void testChangeStudentLastName() throws DataManagerException {
    if (student != null){
      String newLastName = "newLastName2";
      
      manager.changeStudentLastName(student, newLastName);
      
      assertEquals(newLastName, student.getLastName());
    }
  }

  public void testChangeStudentNameAndLastName() throws DataManagerException {
    if (student != null){
      String newName = "newName3";
      String newLastName = "newLastName3";
      
      manager.changeStudentNameAndLastName(student, newName, newLastName);
      
      assertEquals(newName, student.getName());
      assertEquals(newLastName, student.getLastName());
    }
  }

  public void testChangeCourseTitleAndCoeff() throws DataManagerException {
    if (course != null){
      String newTitle = "newTitle";
      float newCoeff = 99f;
      
      manager.changeCourseTitleAndCoeff(course, newTitle, newCoeff);
      
      assertEquals(newTitle, course.getTitle());
      assertEquals(newCoeff == course.getCoeff(), true);
    }
  }

  public void testChangeCourseTitle() throws DataManagerException {
    if (course != null){
      String newTitle = "newTitle2";
      
      manager.changeCourseTitle(course, newTitle);
      
      assertEquals(newTitle, course.getTitle());
    }
  }

  public void testChangeCourseCoeff() throws DataManagerException {
    if (course != null){
      float newCoeff = 999f;
      
      manager.changeCourseCoeff(course, newCoeff);
      
      assertEquals(true, newCoeff == course.getCoeff());
    }
  }
  
  public void testRemoveStudent() throws SQLException, DataManagerException {
    int studentCount = manager.sizeOfTable(TABLE_STUDENT);
    int testsCount = manager.sizeOfTable(TABLE_TEST);
    int studentMarksCount = manager.sizeOfTable(TABLE_HAS_MARK);
    
    if (student != null) {
      manager.removeStudent(student);

      assertEquals(studentCount - 1, manager.sizeOfTable(TABLE_STUDENT));
      assertEquals(studentMarksCount - testsCount, manager
          .sizeOfTable(TABLE_HAS_MARK));
    }
  }
  
  public void testRemoveMark() throws SQLException, DataManagerException {
    int testsCount = manager.sizeOfTable(TABLE_TEST);
    int studentMarksCount = manager.sizeOfTable(TABLE_HAS_MARK);
    int studentCount = manager.sizeOfTable(TABLE_STUDENT);
    
    if (mark != null){
      manager.removeMark(mark);
      
      assertEquals(testsCount - 1, manager.sizeOfTable(TABLE_TEST));
      assertEquals(studentMarksCount - studentCount, manager.sizeOfTable(TABLE_HAS_MARK));
    }
  }
  
  public void testRemoveCourse() throws DataManagerException, SQLException {
    int n = manager.sizeOfTable(TABLE_COURSE);
    
    if (course != null){
      manager.removeCourse(course);
      
      assertEquals(n -1, manager.sizeOfTable(TABLE_COURSE));
    }
  }
}
