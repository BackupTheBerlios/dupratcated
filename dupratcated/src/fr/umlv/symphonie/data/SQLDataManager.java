
package fr.umlv.symphonie.data;

import java.util.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;

import fr.umlv.symphonie.util.Pair;

public class SQLDataManager extends SQLDataManagerConstants implements
    DataManager {

  
  /*
   * methodes internes
   * 
   */
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

  
  
  
  
  
  /*
   * methodes de base
   * 
   */
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

//  public Map<Integer, String> getTitles() {
//    HashMap<Integer, String> map = new HashMap<Integer, String>();
//    ResultSet results = null;
//    String request = "SELECT * FROM '" + TABLE_TITLE + "';";
//
//    try {
//      results = connectAndQuery(request);
//
//      while (results.next()) {
//        map.put(new Integer(results.getInt(0)), results.getString(1));
//      }
//    } catch (SQLException e) {
//      System.out.println("Error with current query :\n" + request);
//      e.printStackTrace();
//    }
//
//    return map;
//  }

  
  // ICI
  public Map<Integer, Mark> getTitlesByCourse(Course c) {
    
    Map<Integer, Mark> map = new HashMap<Integer, Mark>();
    
    ResultSet results = null;
    
    String request = "select distinct " + COLUMN_ID_FROM_TABLE_TEST + ", " + COLUMN_DESC_FROM_TABLE_TITLE + ", " + COLUMN_COEFF_FROM_TABLE_TEST + " " +
            "from " + TABLE_TEST + ", " + TABLE_TITLE + " " +
            "where " + TABLE_TEST + "." + COLUMN_ID_COURSE_FROM_TABLE_TEST + " = " + c.getId() + " " +
            "and " + TABLE_TEST + "." + COLUMN_ID_TITLE_FROM_TABLE_TEST + " = " + TABLE_TITLE + "." + COLUMN_ID_FROM_TABLE_TITLE + ";";
     
    try {
      results = connectAndQuery(request);

      while (results.next()) {
        int id = results.getInt(COLUMN_ID_FROM_TABLE_TEST);
        map.put(new Integer (id),
              new Mark(id, results.getString(COLUMN_DESC_FROM_TABLE_TITLE),results.getFloat(COLUMN_COEFF_FROM_TABLE_TEST), c));
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

  
  private Map<Integer, StudentMark> getMarksByStudentAndCourse(Student s, Course c, Map<Integer, Mark> titleMap){
    
    Map<Integer, StudentMark> markMap = new HashMap<Integer, StudentMark>();
    
    ResultSet results = null;
    
    String request = "SELECT " + COLUMN_ID_TEST_FROM_TABLE_HAS_MARK + ", " + COLUMN_MARK_FROM_TABLE_HAS_MARK + " " +
        "FROM " + TABLE_HAS_MARK + ", " + TABLE_TEST + " " +
        "WHERE " + COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + " = " + s.getId() + " " +
        "AND " + COLUMN_ID_TEST_FROM_TABLE_HAS_MARK + " = " + COLUMN_ID_FROM_TABLE_TEST + " " +
        "AND " + COLUMN_ID_COURSE_FROM_TABLE_TEST + " = " + c.getId() + ";";

    try {
      results = connectAndQuery(request);

      while (results.next()) {
        StudentMark studentMark = new StudentMark(s,
                                 titleMap.get(results.getInt(COLUMN_ID_TEST_FROM_TABLE_HAS_MARK)),
                                 results.getFloat(COLUMN_MARK_FROM_TABLE_HAS_MARK));
        
        markMap.put(studentMark.getMark().getId(), studentMark);
      }
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    return markMap;
  }
  
  

  /*
   * methodes de la vue etudiant
   */
  public Map<Integer, StudentMark> getMarksByStudentAndCourse(Student s, Course c) {
    
    
    /*HashMap<Integer, String> map = (HashMap<Integer, String>) getTitles();*/
    Map<Integer, Mark> titleMap = getTitlesByCourse(c);
    
    return getMarksByStudentAndCourse(s, c, titleMap);
  }
  
  
  
  public Map<Course, Map<Integer, StudentMark>> getAllMarksByStudent(Student s) {
    
    ArrayList<Course> list = (ArrayList<Course>) getCourses();
    HashMap<Course, Map<Integer, StudentMark>> map = new HashMap<Course, Map<Integer, StudentMark>>();

    for (Course c : list) {
      map.put(c, getMarksByStudentAndCourse(s, c));
    }

    return map;
  }

  
  
  /*public SortedMap<Student, Map<Integer, StudentMark>> getMarksByStudentsAndCourse(List<Student> students, Course c) {
    TreeMap<Student, Map<Integer, StudentMark>> resultMap = new TreeMap<Student, Map<Integer, StudentMark>>(new Comparator<Student>(){
      
      public int compare(Student arg0,Student arg1){
        int n = arg0.getLastName().compareToIgnoreCase(arg1.getLastName());
        
        if (n==0)
          n = arg0.getName().compareToIgnoreCase(arg1.getName());
        
        
        if (n==0)
          return arg0.getId() - arg1.getId();
        
        return n;
        }
    });
    return null;
  }*/
  
  
  
  /*
   * methodes de la vue professeur
   */
  public Pair<Map<Integer, Mark>,
              SortedMap<Student, Map<Integer, StudentMark>>> getAllMarksByCourse(Course c) {
    
    TreeMap<Student, Map<Integer, StudentMark>> map = new TreeMap<Student, Map<Integer, StudentMark>>(new Comparator<Student>(){
      
      public int compare(Student arg0,Student arg1){
        int n = arg0.getLastName().compareToIgnoreCase(arg1.getLastName());
        
        if (n==0)
          n = arg0.getName().compareToIgnoreCase(arg1.getName());
        
        if (n==0)
          return arg0.getId() - arg1.getId();
        
        return n; 
        }
      
    });
    
    // on recupere la liste des etudiants
    List<Student> list = getStudents();
    
    // on recupere la liste de chaque epreuve de la matiere
    Map<Integer, Mark> titleMap = getTitlesByCourse(c);
    
    // pour chaque etudiant
    for (Student s : list){
      
      // on ajoute a la map les etudiants et leurs notes
      // (version toutes les notes liées a la meme epreuve
      // pour faciliter l'édition de coeff)
      map.put(s, getMarksByStudentAndCourse(s, c, titleMap));
    }
    
    
    // on renvoie la paire formee de la map et des intitules des epreuves
    // (la liste d'intitules sert pour la vue professeur,
    // pour editer directement les intitules lies a toutes les notes
    return new Pair<Map<Integer, Mark>,
              SortedMap<Student, Map<Integer, StudentMark>>>(titleMap, map);
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  public Map<Student, Map<Course, Map<Integer, StudentMark>>> getAllStudentsMarks() {
    
    ArrayList<Student> list = (ArrayList<Student>) getStudents();
    HashMap<Student, Map<Course, Map<Integer, StudentMark>>> map = new HashMap<Student, Map<Course, Map<Integer, StudentMark>>>();

    for (Student s : list) {
      map.put(s, getAllMarksByStudent(s));
    }

    return map;
  }

  

  
  
  
  
  // ICI

  
  
 
  
  
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
    
    
    /*
     * penser a virer aussi les notes et tout
     */
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
    
    /*
     * penser a virer les notes aussi 
     */
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
    
    /*
     * penser a virer les notes et les intitules
     */
    
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
    /*
     * penser a virer les notes et les intitules
     */
  }

  public void addStudentMark(Student s, Course c, Mark m, float mark,
      float coeff) throws SQLException {
    String request = "INSERT INTO '" + TABLE_HAS_MARK + "' ('"
        + COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + "', '"
        + COLUMN_ID_TEST_FROM_TABLE_HAS_MARK + "', '"
        + COLUMN_MARK_FROM_TABLE_HAS_MARK + "') VALUES ('" + s.getId()
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
