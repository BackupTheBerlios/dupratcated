/*
 * This file is part of Symphonie
 * Created : 25 févr. 2005 00:24:27
 */
package fr.umlv.symphonie.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;


/**
 * @author Propriétaire
 *
 */
public class SQLDataManagerTest extends TestCase {

  SQLDataManager dm = new SQLDataManager();
  
  public void testCreatePrimaryKey() throws SQLException, DataManagerException {
    int key1 = -1;
    int key2 = -1;
    Student t = null;

    key1 = SQLDataManager.createPrimaryKey("student", "id_student");
    t = new Student(key1, "Laurent", "Baka");
    dm.addStudent(t.getName(), t.getLastName());
    key2 = SQLDataManager.createPrimaryKey("student", "id_student");
    dm.removeStudent(t);

    assertEquals(key1, (key2 - 1));
  }
  
  public void testAddStudent() throws SQLException, DataManagerException {
    int count = 1;
    Student t1 = null;
    ArrayList<Student> all = (ArrayList<Student>)dm.getStudents();
   
    t1 = new Student(SQLDataManager.createPrimaryKey("student", "id_student"), "Laurent",
        "Baka");
    dm.addStudent(t1.getName(), t1.getLastName());
  
    for (Student t : all) {    
      if (t.getId() == t1.getId() 
          && t.getName().equals(t1.getName())
          && t.getLastName().equals(t1.getLastName())) {
        dm.removeStudent(t1);
        count--;
      }
    }

    assertEquals(0, count);
  }

  public void testAddStudents() throws SQLException, DataManagerException {
    int count = 2;
    int id = -1;
    Student t1 = null;
    Student t2 = null;
    ArrayList<Student> all = null;
    ArrayList<String> listName = new ArrayList<String>();
    ArrayList<String> listLastName = new ArrayList<String>();

    id = SQLDataManager.createPrimaryKey("student", "id_student");
    t1 = new Student(id, "Laurent", "Baka");
    t2 = new Student(id + 1, "Fabien", "Kyubi");

    listName.add(t1.getName());
    listName.add(t2.getName());

    listLastName.add(t1.getLastName());
    listLastName.add(t2.getLastName());

    dm.addStudents(listName, listLastName);

    all = (ArrayList<Student>) dm.getStudents();

    for (Student t : all) {
      if (t.getId() == t1.getId()
          && t.getName().equals(t1.getName())
          && t.getLastName().equals(t1.getLastName())) {
        dm.removeStudent(t1);
        count--;
      } else if (t.getId() == t2.getId()
          && t.getName().equals(t2.getName())
          && t.getLastName().equals(t2.getLastName())) {
        dm.removeStudent(t2);
        count--;
      }
    }

    assertEquals(0, count);
  }

  public void testRemoveStudent() throws SQLException, DataManagerException {
    Student t1 = null;
    ArrayList<Student> all = null;

    t1 = new Student(SQLDataManager.createPrimaryKey("student", "id_student"), "Laurent",
        "Baka");
    dm.addStudent(t1.getName(), t1.getLastName());
    dm.removeStudent(t1);

    all = (ArrayList<Student>) dm.getStudents();

    for (Student t : all) {
      assertFalse(t.getId() == t1.getId());
    }
  }
  
  public void testRemoveAllStudentMarksForStudent() {
  }

  public void testAddCourse() throws SQLException, DataManagerException {
    int count = 1;
    Course t1 = null;
    ArrayList<Course> all = null;

    t1 = new Course(SQLDataManager.createPrimaryKey("course", "id_course"), "Java",
        (float) 6.0);

    dm.addCourse(t1.getTitle(), t1.getCoeff());
    all = (ArrayList<Course>) dm.getCourses();

    for (Course t : all) {
      if (t.getId() == t1.getId()
          && t.getTitle().equals(t1.getTitle())
          && t.getCoeff() == t1.getCoeff()) {
        dm.removeCourse(t1);
        count--;
      }
    }

    assertEquals(0, count);
  }

  public void testAddCourses() throws SQLException, DataManagerException {
    int count = 2;
    int id = -1;
    Course t1 = null;
    Course t2 = null;
    ArrayList<Course> all = null;
    ArrayList<String> listTitle = new ArrayList<String>();
    ArrayList<Float> listCoeff = new ArrayList<Float>();

    id = SQLDataManager.createPrimaryKey("student", "id_student");
    t1 = new Course(id, "Java", (float) 6.0);
    t2 = new Course(id + 1, "C++", (float) 6.0);

    listTitle.add(t1.getTitle());
    listTitle.add(t2.getTitle());

    listCoeff.add(t1.getCoeff());
    listCoeff.add(t2.getCoeff());

    dm.addCourses(listTitle, listCoeff);

    all = (ArrayList<Course>) dm.getCourses();

    for (Course t : all) {
      if (t.getId() == t1.getId() 
          && t.getTitle().equals(t1.getTitle())
          && t.getCoeff() == t1.getCoeff()) {
        dm.removeCourse(t1);
        count--;
      } else if (t.getId() == t2.getId()
          && t.getTitle().equals(t2.getTitle())
          && t.getCoeff() == t2.getCoeff()) {
        dm.removeCourse(t2);
        count--;
      }
    }

    assertEquals(0, count);
  }

  public void testRemoveCourse() throws SQLException, DataManagerException {
    Course t1 = null;
    ArrayList<Course> all = null;

    t1 = new Course(SQLDataManager.createPrimaryKey("course", "id_course"), "Java",
        (float) 6.0);
    dm.addCourse(t1.getTitle(), t1.getCoeff());
    dm.removeCourse(t1);

    all = (ArrayList<Course>) dm.getCourses();

    for (Course t : all) {
      assertFalse(t.getId() == t1.getId());
    }
  }

  public void testAddTitle() throws DataManagerException, SQLException {
    int count = 1;
    Course c1 = null;
    Mark t1 = null;
    Map<Integer, Mark> all = null;

    c1 = new Course(SQLDataManager.createPrimaryKey("course", "id_course"), "Java",
        (float) 6.0);
    t1 = new Mark(SQLDataManager.createPrimaryKey("title", "id_title"), "Project", (float)0.5, c1);

    dm.addTitle(t1.getDesc());
    all = dm.getMarksByCourse(c1);

    for (Mark t : all.values()) {
      if (t.getId() == t1.getId()
          && t.getDesc().equals(t1.getDesc())) {
        dm.removeMark(t1);
        count--;
      }
    }

    assertEquals(0, count);
  }

  public void testAddMark() {
  }

  public void testRemoveMark() {
  }
  
  public void testRemoveAllStudentMarksForMark() {
  }

  public void testChangeStudentMarkValue() {
  }

  public void testChangeMarkDescription() {
  }

}
