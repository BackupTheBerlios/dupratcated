
package fr.umlv.symphonie.data;

import java.util.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class SQLDataManager extends SQLDataManagerConstants implements
    DataManager {

  private static PreparedStatement connectAndPrepare(String request)
      throws SQLException {
    Connection connection = null;
    PreparedStatement preparedStatement = null;

    connection = ConnectionManager.createConnection();
    preparedStatement = connection.prepareStatement(request);

    return preparedStatement;
  }

  private static ResultSet connectAndQuery(String request) throws SQLException {
    Connection connection = null;
    Statement statement = null;

    connection = ConnectionManager.createConnection();
    statement = connection.createStatement();

    return statement.executeQuery(request);
  }

  private static int createPrimaryKey(String table, String id)
      throws SQLException {
    ResultSet results = null;
    String request = "SELECT MAX('" + id + "') + 1 FROM '" + table + "';";

    try {
      results = connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    if (results.next()) return results.getInt(0);

    return 0;
  }

  public List<Student> getStudents() {
    ArrayList<Student> list = new ArrayList<Student>();
    ResultSet results = null;
    String request = "SELECT * FROM '" + TABLE_STUDENT + "';";

    try {
      results = connectAndQuery(request);

      while (results.next()) {
        list.add(new Student(results.getInt(0), results.getString(1), results
            .getString(2), results.getString(3)));
      }
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    return list;
  }

  public List<Course> getCourses() {
    ArrayList<Course> list = new ArrayList<Course>();
    ResultSet results = null;
    String request = "SELECT * FROM '" + TABLE_COURSE + "';";

    try {
      results = connectAndQuery(request);

      while (results.next()) {
        list.add(new Course(results.getInt(0), results.getString(1), results
            .getFloat(2)));
      }
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    return list;
  }

  public Map<Integer, String> getTitles() {
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    ResultSet results = null;
    String request = "SELECT * FROM '" + TABLE_TITLE + "';";

    try {
      results = connectAndQuery(request);

      while (results.next()) {
        map.put(new Integer(results.getInt(0)), results.getString(1));
      }
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    return map;
  }

  /*public List<StudentMark> getMarksByStudentAndCourse(Student s, Course c) {
    ArrayList<StudentMark> list = new ArrayList<StudentMark>();
    HashMap<Integer, String> map = (HashMap<Integer, String>) getTitles();
    ResultSet results = null;
    String request = "SELECT * FROM '" + TABLE_HAS_MARK + "' WHERE '"
        + COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + "'='" + s.getId() + "' AND '"
        + COLUMN_ID_COURSE_FROM_TABLE_HAS_MARK + "'='" + c.getId() + "';";

    try {
      results = connectAndQuery(request);

      while (results.next()) {
        list.add(new StudentMark(s, new Mark(results.getInt(2),
            map.get(new Integer (results.getInt(2))), c), results.getFloat(3), results.getFloat(4)));
      }
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    return list;
  }*/

  
  public Map<String, StudentMark> getMarksByStudentAndCourse(Student s, Course c) {
    
    Map<String, StudentMark> markMap = new HashMap<String, StudentMark>();
    HashMap<Integer, String> map = (HashMap<Integer, String>) getTitles();
    
    ResultSet results = null;
    
    String request = "SELECT * FROM '" + TABLE_HAS_MARK + "' WHERE '"
        + COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + "'='" + s.getId() + "' AND '"
        + COLUMN_ID_COURSE_FROM_TABLE_HAS_MARK + "'='" + c.getId() + "';";

    try {
      results = connectAndQuery(request);

      while (results.next()) {
        StudentMark studentMark = new StudentMark(s,
                                 new Mark(results.getInt(2), map.get(new Integer (results.getInt(2))), c),
                                 results.getFloat(3),
                                 results.getFloat(4));
        
        markMap.put(studentMark.getMark().getDesc(), studentMark);
      }
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    return markMap;
  }
  
  
  
  public Map<Course, Map<String, StudentMark>> getAllMarksByStudent(Student s) {
    
    ArrayList<Course> list = (ArrayList<Course>) getCourses();
    HashMap<Course, Map<String, StudentMark>> map = new HashMap<Course, Map<String, StudentMark>>();

    for (Course c : list) {
      map.put(c, getMarksByStudentAndCourse(s, c));
    }

    return map;
  }

  public Map<Student, Map<Course, Map<String, StudentMark>>> getAllStudentsMarks() {
    
    ArrayList<Student> list = (ArrayList<Student>) getStudents();
    HashMap<Student, Map<Course, Map<String, StudentMark>>> map = new HashMap<Student, Map<Course, Map<String, StudentMark>>>();

    for (Student s : list) {
      map.put(s, getAllMarksByStudent(s));
    }

    return map;
  }

  
  // ICI
  public SortedMap<Mark, Float> getTitlesByCourse(Course c) {
    
    TreeMap<Mark, Float> map = new TreeMap<Mark, Float>(new Comparator<Mark>(){
      public int compare(Mark arg0,Mark arg1){
        return arg0.getDesc().compareToIgnoreCase(arg1.getDesc());
      }
      
      public boolean equals(Object obj) {
        return false;
      }
    });
    
    
    ResultSet results = null;
    
    String request = "select distinct " + COLUMN_ID_MARK_FROM_TABLE_HAS_MARK + ", " + COLUMN_DESC_FROM_TABLE_TITLE + ", " + COLUMN_COEFF_FROM_TABLE_HAS_MARK + " " +
    				"from " + TABLE_HAS_MARK + ", " + TABLE_TITLE + " " +
    				"where " + TABLE_HAS_MARK + "." + COLUMN_ID_COURSE_FROM_TABLE_HAS_MARK + " = " + c.getId() + " " +
    				"order by " + COLUMN_DESC_FROM_TABLE_TITLE + ";";
     
    try {
      results = connectAndQuery(request);

      while (results.next()) {
        map.put(new Mark(results.getInt(COLUMN_ID_MARK_FROM_TABLE_HAS_MARK),
            results.getString(COLUMN_DESC_FROM_TABLE_TITLE),
            c),
            new Float(results.getFloat(COLUMN_COEFF_FROM_TABLE_HAS_MARK)));
      }
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    return map;
  }
  
  
  
  
  // ICI
  public SortedMap<Student, Map<String, StudentMark>> getAllMarksByCourse(Course c) {
    
    TreeMap<Student, Map<String, StudentMark>> map = new TreeMap<Student, Map<String, StudentMark>>(new Comparator<Student>(){
      
      public int compare(Student arg0,Student arg1){
        int n = arg0.getLastName().compareToIgnoreCase(arg1.getLastName());
        
        if (n==0)
          return arg0.getName().compareToIgnoreCase(arg1.getName());
        
        return 0; 
        }
      
      public boolean equals(Object obj) {
        return super.equals(obj);
      }
      
    });
    
    List<Student> list = getStudents();
    
    for (Student s : list){
      map.put(s, getMarksByStudentAndCourse(s, c));
    }
    
    return map;
  }
  
  
 
  
  
  public void addStudent(String name, String lastName) throws SQLException {
    int key = 0;

    try {
      key = createPrimaryKey(TABLE_STUDENT, COLUMN_ID_FROM_TABLE_STUDENT);
    } catch (SQLException e) {
      System.out.println("Error with current query : createPrimaryKey("
          + TABLE_STUDENT + ", " + COLUMN_ID_FROM_TABLE_STUDENT + ")\n");
      e.printStackTrace();
    }

    String request = "INSERT INTO '" + TABLE_STUDENT + "' ('"
        + COLUMN_ID_FROM_TABLE_STUDENT + "', '"
        + COLUMN_NAME_FROM_TABLE_STUDENT + "', '"
        + COLUMN_LAST_NAME_FROM_TABLE_STUDENT + "', '"
        + COLUMN_COMMENT_FROM_TABLE_STUDENT + "') VALUES ('" + key + "', '"
        + name + "', '" + lastName + "', NULL);";

    try {
      connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }
  }

  public void addStudents(List<String> listName, List<String> listLastName)
      throws SQLException, DataManagerException {
    String request = "INSERT INTO '" + TABLE_STUDENT + "' ('"
        + COLUMN_ID_FROM_TABLE_STUDENT + "', '"
        + COLUMN_NAME_FROM_TABLE_STUDENT + "', '"
        + COLUMN_LAST_NAME_FROM_TABLE_STUDENT + "', '"
        + COLUMN_COMMENT_FROM_TABLE_STUDENT
        + "') VALUES ('?', '?', '?', NULL);";
    PreparedStatement preparedStatement = null;
    int key = 0;
    int size = listName.size();

    if (size != listLastName.size()) {
      throw new DataManagerException(
          "the lists of addStudents(List<String> listName, List<String> listLastName) must have the same size.\n");
    }

    try {
      preparedStatement = connectAndPrepare(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    for (int i = 0; i < size; i++) {
      try {
        key = createPrimaryKey(TABLE_STUDENT, COLUMN_ID_FROM_TABLE_STUDENT);
      } catch (SQLException e) {
        System.out.println("Error with current query : createPrimaryKey("
            + TABLE_STUDENT + ", " + COLUMN_ID_FROM_TABLE_STUDENT + ")\n");
        e.printStackTrace();
      }

      try {
        preparedStatement.setInt(1, key);
        preparedStatement.setString(2, listName.get(i));
        preparedStatement.setString(3, listLastName.get(i));
        preparedStatement.execute();
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      }
    }
  }

  public void removeStudent(Student s) throws SQLException {
    String request = "DELETE FROM '" + TABLE_STUDENT + "' WHERE '"
        + COLUMN_ID_FROM_TABLE_STUDENT + "'='" + s.getId() + "';";

    try {
      connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }
  }

  public void removeStudents(List<Student> list) throws SQLException {
    String request = "DELETE FROM '" + TABLE_STUDENT + "' WHERE '"
        + COLUMN_ID_FROM_TABLE_STUDENT + "'='?';";
    PreparedStatement preparedStatement = null;

    try {
      preparedStatement = connectAndPrepare(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    for (Student s : list) {
      try {
        preparedStatement.setInt(1, s.getId());
        preparedStatement.execute();
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      }
    }
  }

  public void addCourse(String title, float coeff) throws SQLException {
    int key = 0;

    try {
      key = createPrimaryKey(TABLE_COURSE, COLUMN_ID_FROM_TABLE_COURSE);
    } catch (SQLException e) {
      System.out.println("Error with current query : createPrimaryKey("
          + TABLE_COURSE + ", " + COLUMN_ID_FROM_TABLE_COURSE + ")\n");
      e.printStackTrace();
    }

    String request = "INSERT INTO '" + TABLE_COURSE + "' ('"
        + COLUMN_ID_FROM_TABLE_COURSE + "', '" + COLUMN_TITLE_FROM_TABLE_COURSE
        + "', '" + COLUMN_COEFF_FROM_TABLE_COURSE + "') VALUES ('" + key
        + "', '" + title + "', '" + coeff + "');";

    try {
      connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }
  }

  public void addCourses(List<String> listTitle, List<Float> listCoeff)
      throws SQLException, DataManagerException {
    String request = "INSERT INTO '" + TABLE_COURSE + "' ('"
        + COLUMN_ID_FROM_TABLE_COURSE + "', '" + COLUMN_TITLE_FROM_TABLE_COURSE
        + "', '" + COLUMN_COEFF_FROM_TABLE_COURSE
        + "') VALUES ('?', '?', '?');";
    PreparedStatement preparedStatement = null;
    int key = 0;
    int size = listTitle.size();

    if (size != listCoeff.size()) {
      throw new DataManagerException(
          "the lists of addCourses(List<String> listTitle, List<float> listCoeff) must have the same size.\n");
    }

    try {
      preparedStatement = connectAndPrepare(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    for (int i = 0; i < size; i++) {
      try {
        key = createPrimaryKey(TABLE_COURSE, COLUMN_ID_FROM_TABLE_COURSE);
      } catch (SQLException e) {
        System.out.println("Error with current query : createPrimaryKey("
            + TABLE_COURSE + ", " + COLUMN_ID_FROM_TABLE_COURSE + ")\n");
        e.printStackTrace();
      }

      try {
        preparedStatement.setInt(1, key);
        preparedStatement.setString(2, listTitle.get(i));
        preparedStatement.setFloat(3, listCoeff.get(i).floatValue());
        preparedStatement.execute();
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      }
    }
  }

  public void removeCourse(Course c) throws SQLException {
    String request = "DELETE FROM '" + TABLE_COURSE + "' WHERE '"
        + COLUMN_ID_FROM_TABLE_COURSE + "'='" + c.getId() + "';";

    try {
      connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }
  }

  public void removeCourses(List<Course> list) throws SQLException {
    String request = "DELETE FROM '" + TABLE_COURSE + "' WHERE '"
        + COLUMN_ID_FROM_TABLE_COURSE + "'='?';";
    PreparedStatement preparedStatement = null;

    try {
      preparedStatement = connectAndPrepare(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    for (Course c : list) {
      try {
        preparedStatement.setInt(1, c.getId());
        preparedStatement.execute();
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      }
    }
  }

  public void addStudentMark(Student s, Course c, Mark m, float mark,
      float coeff) throws SQLException {
    String request = "INSERT INTO '" + TABLE_HAS_MARK + "' ('"
        + COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + "', '"
        + COLUMN_ID_COURSE_FROM_TABLE_HAS_MARK + "', '"
        + COLUMN_ID_MARK_FROM_TABLE_HAS_MARK + "', '"
        + COLUMN_MARK_FROM_TABLE_HAS_MARK + "', '"
        + COLUMN_COEFF_FROM_TABLE_HAS_MARK + "') VALUES ('" + s.getId()
        + "', '" + c.getId() + "', '" + m.getId() + "', '" + mark + "', '"
        + coeff + "');";

    try {
      connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }
  }

  public void addStudentMarks(List<Student> listS, List<Course> listC,
      List<Mark> listM, List<Float> listMark, List<Float> listCoeff)
      throws SQLException, DataManagerException {
    String request = "INSERT INTO '" + TABLE_HAS_MARK + "' ('"
        + COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + "', '"
        + COLUMN_ID_COURSE_FROM_TABLE_HAS_MARK + "', '"
        + COLUMN_ID_MARK_FROM_TABLE_HAS_MARK + "', '"
        + COLUMN_MARK_FROM_TABLE_HAS_MARK + "', '"
        + COLUMN_COEFF_FROM_TABLE_HAS_MARK
        + "') VALUES ('?', '?', '?', '?', '?');";
    PreparedStatement preparedStatement = null;
    int size = listS.size();

    if (size != listC.size() || size != listM.size() || size != listMark.size()
        || size != listCoeff.size()) {
      throw new DataManagerException(
          "the lists of addStudentMarks(List<Student> listS, List<Course> ListC, List<Mark> ListM, List<float> listMark, List<float> listCoeff) must have the same size.\n");
    }

    try {
      preparedStatement = connectAndPrepare(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    for (int i = 0; i < size; i++) {
      try {
        preparedStatement.setInt(1, listS.get(i).getId());
        preparedStatement.setInt(2, listC.get(i).getId());
        preparedStatement.setInt(3, listM.get(i).getId());
        preparedStatement.setFloat(4, listMark.get(i).floatValue());
        preparedStatement.setFloat(5, listCoeff.get(i).floatValue());
        preparedStatement.execute();
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      }
    }
  }

  public void removeStudentMark(StudentMark sm) throws SQLException {
    String request = "DELETE FROM '" + TABLE_HAS_MARK + "' WHERE '"
        + COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + "'='"
        + sm.getStudent().getId() + "' AND '"
        + COLUMN_ID_COURSE_FROM_TABLE_HAS_MARK + "'='"
        + sm.getMark().getCourse().getId() + "' AND '"
        + COLUMN_ID_MARK_FROM_TABLE_HAS_MARK + "'='" + sm.getMark().getId()
        + "';";

    try {
      connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }
  }

  public void removeStudentMarks(List<StudentMark> list) throws SQLException {
    String request = "DELETE FROM '" + TABLE_HAS_MARK + "' WHERE '"
        + COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + "'='?' AND '"
        + COLUMN_ID_COURSE_FROM_TABLE_HAS_MARK + "'='?' AND '"
        + COLUMN_ID_MARK_FROM_TABLE_HAS_MARK + "'='?';";
    PreparedStatement preparedStatement = null;

    try {
      preparedStatement = connectAndPrepare(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    for (StudentMark sm : list) {
      try {
        preparedStatement.setInt(1, sm.getStudent().getId());
        preparedStatement.setInt(2, sm.getMark().getCourse().getId());
        preparedStatement.setInt(3, sm.getMark().getId());
        preparedStatement.execute();
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      }
    }
  }

  public void addMark(String desc) throws SQLException {
    int key = 0;

    try {
      key = createPrimaryKey(TABLE_TITLE, COLUMN_ID_FROM_TABLE_TITLE);
    } catch (SQLException e) {
      System.out.println("Error with current query : createPrimaryKey("
          + TABLE_TITLE + ", " + COLUMN_ID_FROM_TABLE_TITLE + ")\n");
      e.printStackTrace();
    }

    String request = "INSERT INTO '" + TABLE_TITLE + "' ('"
        + COLUMN_ID_FROM_TABLE_TITLE + "', '" + COLUMN_DESC_FROM_TABLE_TITLE
        + "') VALUES ('" + key + "', '" + desc + "');";

    try {
      connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }
  }

  public void addMarks(List<String> list) throws SQLException {
    String request = "INSERT INTO '" + TABLE_TITLE + "' ('"
        + COLUMN_ID_FROM_TABLE_TITLE + "', '" + COLUMN_DESC_FROM_TABLE_TITLE
        + "') VALUES ('?', '?');";
    PreparedStatement preparedStatement = null;
    int key = 0;

    try {
      preparedStatement = connectAndPrepare(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    for (String desc : list) {
      try {
        key = createPrimaryKey(TABLE_TITLE, COLUMN_ID_FROM_TABLE_TITLE);
      } catch (SQLException e) {
        System.out.println("Error with current query : createPrimaryKey("
            + TABLE_TITLE + ", " + COLUMN_ID_FROM_TABLE_TITLE + ")\n");
        e.printStackTrace();
      }

      try {
        preparedStatement.setInt(1, key);
        preparedStatement.setString(2, desc);
        preparedStatement.execute();
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      }
    }
  }

  public void removeMark(Mark m) throws SQLException {
    String request = "DELETE FROM '" + TABLE_TITLE + "' WHERE '"
        + COLUMN_ID_FROM_TABLE_TITLE + "'='" + m.getId() + "';";

    try {
      connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }
  }

  public void removeMarks(List<Mark> list) throws SQLException {
    String request = "DELETE FROM '" + TABLE_TITLE + "' WHERE '"
        + COLUMN_ID_FROM_TABLE_TITLE + "'='?';";
    PreparedStatement preparedStatement = null;

    try {
      preparedStatement = connectAndPrepare(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    for (Mark m : list) {
      try {
        preparedStatement.setInt(1, m.getId());
        preparedStatement.execute();
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      }
    }
  }

  /* (non-Javadoc)
   * @see fr.umlv.symphonie.data.DataManager#changeStudentMarkValue(float)
   */
  public void changeStudentMarkValue(StudentMark studentMark, float newValue) throws SQLException {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see fr.umlv.symphonie.data.DataManager#changeMarkDescription(java.lang.String)
   */
  public void changeMarkDescription(Mark mark, String newDescription) throws SQLException {
    // TODO Auto-generated method stub
    
  }



}
