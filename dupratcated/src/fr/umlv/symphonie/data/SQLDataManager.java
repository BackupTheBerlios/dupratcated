
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
  
  
  private final Map<Integer, Student> studentMap = new HashMap<Integer, Student>();
  private int studentMapTimeStamp = -1;
  
  private final Map<Integer, Mark> markMap = new HashMap<Integer, Mark>();
  private int markMapTimeStamp = -1;
  
  private final Map<Integer, Course> courseMap = new HashMap<Integer, Course>();  
  private int courseMapTimeStamp = -1;
  
  private final List<StudentMark> studentMarkList = new ArrayList<StudentMark>();
  private int studentMarkListTimeStamp = -1;

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

  public static int createPrimaryKey(String table, String id)
      throws SQLException {

    ResultSet results = null;
    String request = "SELECT MAX(`" + id + "`) + 1 FROM `" + table + "`;";

    results = connectAndQuery(request);

    results.next();
    return results.getInt(1);
  }

  /*
   * methodes de base
   * 
   */
  public Map<Integer, Student> getStudents() {
    
    /*int n = getTimeStamp(TABLE_STUDENT);*/
    
    if (studentMapTimeStamp == -1 /*|| n > studentMapTimeStamp*/){
      
      ResultSet results = null;
      String request = "SELECT * FROM `" + TABLE_STUDENT + "`;";
      
      try {
        results = connectAndQuery(request);

        while (results.next()) {
          Student s = new Student(results.getInt(1), results.getString(2),
                                  results.getString(3), results.getString(4));
          
          if (studentMap.containsKey(s.getId()))
            studentMap.get(s.getId()).update(s);
          
          else studentMap.put(s.getId(), s);
        }
        /*studentMapTimeStamp = n;*/
        
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      }
    }
    
    return studentMap;
  }

  public Map<Integer, Course> getCourses() {
    
    /*int n = getTimeStamp(TABLE_COURSE);*/
    
    if (courseMapTimeStamp == -1 /*|| n > courseMapTimeStamp*/){

      ResultSet results = null;
      String request = "SELECT * FROM `" + TABLE_COURSE + "`;";

      try {
        results = connectAndQuery(request);

        while (results.next()) {
          
          Course c = new Course(results.getInt(1), results.getString(2), results.getFloat(3));
          
          if (courseMap.containsKey(c.getId()))
            courseMap.get(c.getId()).update(c);
          
          else courseMap.put(c.getId(), c);
        }
        
        /*courseMapTimeStamp = n;*/
        
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      } 
    }
    
    return courseMap;
  }

  public Map<Integer, Mark> getMarks() {
    /*int n = getTimeStamp(TABLE_TEST);*/
    
    if (markMapTimeStamp == -1 /*|| n > markMapTimeStamp*/){
      
      Map<Integer, Course> courseMap = getCourses();
      
      ResultSet results = null;
      String request = "SELECT " + COLUMN_ID_FROM_TABLE_TEST + " , " + COLUMN_COEFF_FROM_TABLE_TEST + " , "
                                 + COLUMN_DESC_FROM_TABLE_TITLE + " , " + COLUMN_ID_COURSE_FROM_TABLE_TEST + " "
                     + "FROM " + TABLE_TEST + " , " + TABLE_TITLE + " "
                     + "WHERE " + TABLE_TEST + "." + COLUMN_ID_TITLE_FROM_TABLE_TEST + " = " + TABLE_TITLE + "." + COLUMN_ID_FROM_TABLE_TITLE + " "
                     + ";";

      try {
        results = connectAndQuery(request);

        while (results.next()) {
          
          Mark m = new Mark(results.getInt(COLUMN_ID_FROM_TABLE_TEST), results.getString(COLUMN_DESC_FROM_TABLE_TITLE),
                            results.getFloat(COLUMN_COEFF_FROM_TABLE_TEST), courseMap.get(results.getInt(COLUMN_ID_COURSE_FROM_TABLE_TEST)));
          
          if (markMap.containsKey(m.getId()))
            markMap.get(m.getId()).update(m);
          else markMap.put(m.getId(), m);
        }
        
        /*markMapTimeStamp = n;*/
        
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      } 
    }
    
    return markMap;

  }
  
  public List<StudentMark> getStudentMarks(){
    /*int n = getTimeStamp(TABLE_HAS_MARK);*/
    
    if (studentMarkListTimeStamp == -1 /*|| n > studentMarkListTimeStamp*/){
      
      Map<Integer, Mark> courseMap = getMarks();
      Map<Integer, Student> studentMap = getStudents();
      
      ResultSet results = null;
      String request = "SELECT * "
                     + "FROM " + TABLE_HAS_MARK + " "
                     + ";";

      try {
        results = connectAndQuery(request);

        while (results.next()) {
          
          StudentMark sm = new StudentMark(studentMap.get(results.getInt(COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK)),
                                        markMap.get(results.getInt(COLUMN_ID_TEST_FROM_TABLE_HAS_MARK)),
                                        results.getFloat(COLUMN_MARK_FROM_TABLE_HAS_MARK));
          
          for(StudentMark sm2 : studentMarkList){                     //
            if (sm.getStudent().getId() == sm2.getStudent().getId()   // alors ca
             && sm.getMark().getId() == sm2.getMark().getId()         // c'est tres
             && sm.getValue() != sm2.getValue()){                     // moche mais
              sm2.setValue(sm.getValue());                            // bon
              break;                                                  //
            }
          }
        }
        
        /*studentMarkListTimeStamp = n;*/
        
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      } 
    }
    
    return studentMarkList;
    
  }
  
  
  
  public Map<Integer, Mark> getMarksByCourse(Course c)
      throws DataManagerException {

    Map<Integer, Mark> markMap = getMarks();
    Map<Integer, Mark> resultMap = new HashMap<Integer, Mark>();
    
    for(Mark m : markMap.values()){
      if (m.getCourse().getId() == c.getId())
        resultMap.put(m.getId(), m);
    }

    return resultMap;
    
    /*ResultSet results = null;

    String request = "select distinct " + COLUMN_ID_FROM_TABLE_TEST + ", "
        + COLUMN_DESC_FROM_TABLE_TITLE + ", " + COLUMN_COEFF_FROM_TABLE_TEST
        + " " + "from " + TABLE_TEST + ", " + TABLE_TITLE + " " + "where "
        + TABLE_TEST + "." + COLUMN_ID_COURSE_FROM_TABLE_TEST + " = "
        + c.getId() + " " + "and " + TABLE_TEST + "."
        + COLUMN_ID_TITLE_FROM_TABLE_TEST + " = " + TABLE_TITLE + "."
        + COLUMN_ID_FROM_TABLE_TITLE + ";";

    try {
      results = connectAndQuery(request);

      while (results.next()) {
        int id = results.getInt(COLUMN_ID_FROM_TABLE_TEST);
        map.put(new Integer(id), new Mark(id, results
            .getString(COLUMN_DESC_FROM_TABLE_TITLE), results
            .getFloat(COLUMN_COEFF_FROM_TABLE_TEST), c));
      }
    } catch (SQLException e) {
      throw new DataManagerException("Error with current query :\n" + request);
    }

    return map;*/
  }



  /*private Map<Integer, StudentMark> getMarksByStudentAndCourse(Student s,
      Course c, Map<Integer, Mark> titleMap) throws DataManagerException {

    
  }*/

  /*
   * methodes de la vue etudiant
   */
  public Map<Integer, StudentMark> getMarksByStudentAndCourse(Student s, Course c) throws DataManagerException {

    /*Map<Integer, Mark> markMap = getMarksByCourse(c);*/
    
    List<StudentMark> studentMarkList = getStudentMarks();
    Map<Integer, StudentMark> studentMarkMap = new HashMap<Integer, StudentMark>();

    for (StudentMark sm : studentMarkList){
      if (sm.getCourse().getId() == c.getId())
        studentMarkMap.put(sm.getMark().getId(), sm);
    }

    return studentMarkMap;
  }

  public Map<Course, Map<Integer, StudentMark>> getAllMarksByStudent(Student s)
      throws DataManagerException {

    Map<Integer, Course> courseMap = getCourses();
    Map<Course, Map<Integer, StudentMark>> map = new HashMap<Course, Map<Integer, StudentMark>>();

    for (Course c : courseMap.values()) {
      map.put(c, getMarksByStudentAndCourse(s, c));
    }

    return map;
  }



  /*
   * methodes de la vue professeur
   */
  public Pair<Map<Integer, Mark>, SortedMap<Student, Map<Integer, StudentMark>>> getAllMarksByCourse(
      Course c) throws DataManagerException {

    TreeMap<Student, Map<Integer, StudentMark>> map = new TreeMap<Student, Map<Integer, StudentMark>>(
        new Comparator<Student>() {

          public int compare(Student arg0, Student arg1) {
            int n = arg0.getLastName().compareToIgnoreCase(arg1.getLastName());

            if (n == 0) n = arg0.getName().compareToIgnoreCase(arg1.getName());

            if (n == 0) return arg0.getId() - arg1.getId();

            return n;
          }

        });

    // on recupere la liste des etudiants
    Map<Integer, Student> studentMap = getStudents();

    // on recupere la liste de chaque epreuve de la matiere
    Map<Integer, Mark> titleMap = getMarksByCourse(c);

    // pour chaque etudiant
    for (Student s : studentMap.values()) {

      // on ajoute a la map les etudiants et leurs notes
      // (version toutes les notes liées a la meme epreuve
      // pour faciliter l'édition de coeff)
      map.put(s, getMarksByStudentAndCourse(s, c));
    }

    // on renvoie la paire formee de la map et des intitules des epreuves
    // (la liste d'intitules sert pour la vue professeur,
    // pour editer directement les intitules lies a toutes les notes
    return new Pair<Map<Integer, Mark>, SortedMap<Student, Map<Integer, StudentMark>>>(
        titleMap, map);
  }
  
  
  /*
   * methodes servant a la vue jury
   */
  public Pair<Map<Integer, Course>, SortedMap<Student, Map<Course, Map<Integer, StudentMark>>>> getAllStudentsMarks()
      throws DataManagerException {

    Map<Integer, Student> studentMap = getStudents();
    Map<Integer, Course> courseMap = getCourses();
    
    SortedMap<Student, Map<Course, Map<Integer, StudentMark>>> sortedMap = new TreeMap<Student, Map<Course, Map<Integer, StudentMark>>>(
        new Comparator<Student>(){
          public int compare(Student arg0, Student arg1) {
            int n = arg0.getLastName().compareToIgnoreCase(arg1.getLastName());

            if (n == 0) n = arg0.getName().compareToIgnoreCase(arg1.getName());

            if (n == 0) return arg0.getId() - arg1.getId();

            return n;
          }
    });

    for (Student s : studentMap.values()) {
      sortedMap.put(s, getAllMarksByStudent(s));
    }

    return new Pair<Map<Integer, Course>, SortedMap<Student, Map<Course, Map<Integer, StudentMark>>>>(
        courseMap, sortedMap);
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

    String request = "INSERT INTO `" + TABLE_STUDENT + "` (`"
        + COLUMN_ID_FROM_TABLE_STUDENT + "`, `"
        + COLUMN_NAME_FROM_TABLE_STUDENT + "`, `"
        + COLUMN_LAST_NAME_FROM_TABLE_STUDENT + "`, `"
        + COLUMN_COMMENT_FROM_TABLE_STUDENT + "`) VALUES (" + key + ", '"
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
    String request = "INSERT INTO `" + TABLE_STUDENT + "` (`"
        + COLUMN_ID_FROM_TABLE_STUDENT + "`, `"
        + COLUMN_NAME_FROM_TABLE_STUDENT + "`, `"
        + COLUMN_LAST_NAME_FROM_TABLE_STUDENT + "`, `"
        + COLUMN_COMMENT_FROM_TABLE_STUDENT + "`) VALUES (?, ?, ?, NULL);";
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
    String request = "DELETE FROM `" + TABLE_STUDENT + "` WHERE `"
        + COLUMN_ID_FROM_TABLE_STUDENT + "`=" + s.getId() + ";";

    try {
      connectAndQuery(request);
      removeAllStudentMarksForStudent(s.getId());
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }
  }

  /**
   * @param studentId
   * @throws SQLException 
   */
  public void removeAllStudentMarksForStudent(int studentId)
      throws SQLException {
    String request = "delete from " + TABLE_HAS_MARK + " " + "where "
        + COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + " = " + studentId + ";";

    connectAndQuery(request);
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

    String request = "INSERT INTO `" + TABLE_COURSE + "` (`"
        + COLUMN_ID_FROM_TABLE_COURSE + "`, `" + COLUMN_TITLE_FROM_TABLE_COURSE
        + "`, `" + COLUMN_COEFF_FROM_TABLE_COURSE + "`) VALUES (" + key + ", '"
        + title + "', '" + coeff + "');";

    try {
      connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }
  }

  public void addCourses(List<String> listTitle, List<Float> listCoeff)
      throws SQLException, DataManagerException {
    String request = "INSERT INTO `" + TABLE_COURSE + "` (`"
        + COLUMN_ID_FROM_TABLE_COURSE + "`, `" + COLUMN_TITLE_FROM_TABLE_COURSE
        + "`, `" + COLUMN_COEFF_FROM_TABLE_COURSE + "`) VALUES (?, ?, ?);";
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
        preparedStatement.setFloat(3, listCoeff.get(i));
        preparedStatement.execute();
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      }
    }
  }

  public void removeCourse(Course c) throws SQLException {
    String request = "DELETE FROM `" + TABLE_COURSE + "` WHERE `"
        + COLUMN_ID_FROM_TABLE_COURSE + "`=" + c.getId() + ";";

    try {
      connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    Map<Integer, Mark> markMap = null;

    try {
      markMap = getMarksByCourse(c);
    } catch (DataManagerException e) {
      // erreur de ta mere
    }

    for (Mark m : markMap.values()) {
      removeMark(m);
    }

  }



  public int addTitle(String desc) throws SQLException {
    int key = 0;

    try {
      key = createPrimaryKey(TABLE_TITLE, COLUMN_ID_FROM_TABLE_TITLE);
    } catch (SQLException e) {
      System.out.println("Error with current query : createPrimaryKey("
          + TABLE_TITLE + ", " + COLUMN_ID_FROM_TABLE_TITLE + ")\n");
      e.printStackTrace();
    }

    String request = "INSERT INTO `" + TABLE_TITLE + "` (`"
        + COLUMN_ID_FROM_TABLE_TITLE + "`, `" + COLUMN_DESC_FROM_TABLE_TITLE
        + "`) VALUES (" + key + ", '" + desc + "');";

    try {
      connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    return key;
  }



  private int getKeyForTitle(String desc) throws SQLException {
    String request = "select " + COLUMN_ID_FROM_TABLE_TITLE + " " + "from "
        + TABLE_TITLE + " " + "where " + COLUMN_DESC_FROM_TABLE_TITLE + " = '"
        + desc + "';";

    ResultSet result = connectAndQuery(request);

    if (result.first() == false) return -1;

    return result.getInt(COLUMN_ID_FROM_TABLE_TITLE);
  }

  public void addMark(String desc, float coeff, Course c) throws SQLException {
    int titleKey = -1;

    try {
      titleKey = getKeyForTitle(desc);
    } catch (SQLException e) {
      // erreur de getKey
    }

    if (titleKey < 0) {
      try {
        titleKey = addTitle(desc);
      } catch (SQLException e) {
        // erreur d'ajout de l'intitule
      }
    }

    int markKey = -1;

    try {
      markKey = createPrimaryKey(TABLE_TEST, COLUMN_ID_FROM_TABLE_TEST);
    } catch (SQLException e) {
      // erreur de creation de cle
    }

    String request = "insert into " + TABLE_TEST + " " + "values (" + markKey
        + ", " + coeff + ", " + titleKey + ", " + c.getId() + ");";

    try {
      connectAndQuery(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    try {
      setDefaultMarkToAllStudents(markKey);
    } catch (SQLException e) {
      // erreur d'affectation des notes a zero
    }

  }


  /**
   * @param markKey
   */
  private void setDefaultMarkToAllStudents(int markKey) throws SQLException {
    Map<Integer, Student> studentMap = getStudents();
    PreparedStatement preparedStatement = null;

    String request = "insert into " + TABLE_HAS_MARK + "values (?, " + markKey
        + ", 0);";

    try {
      preparedStatement = connectAndPrepare(request);
    } catch (SQLException e) {
      System.out.println("Error with current query :\n" + request);
      e.printStackTrace();
    }

    for (Student s : studentMap.values()) {
      try {
        preparedStatement.setInt(1, s.getId());
        preparedStatement.execute();
      } catch (SQLException e) {
        System.out.println("Error with current query :\n" + request);
        e.printStackTrace();
      }
    }

  }

  public void removeMark(Mark m) throws SQLException {
    String request = "delete from " + TABLE_TEST + " " + "where "
        + COLUMN_ID_FROM_TABLE_TEST + " = " + m.getId() + ";";

    try {
      connectAndQuery(request);
      removeAllStudentMarksForMark(m.getId());
    } catch (SQLException e) {
      // erreur de suppression
    }/*catch (DataManagerException e){
     
     }*/

  }



  /**
   * @param id
   */
  public void removeAllStudentMarksForMark(int markId) throws SQLException {
    String request = "delete from " + TABLE_HAS_MARK + " " + "where "
        + COLUMN_ID_TEST_FROM_TABLE_HAS_MARK + " = " + markId + ";";

    connectAndQuery(request);
  }

  /* (non-Javadoc)
   * @see fr.umlv.symphonie.data.DataManager#changeStudentMarkValue(float)
   */
  public void changeStudentMarkValue(StudentMark studentMark, float newValue)
      throws SQLException {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see fr.umlv.symphonie.data.DataManager#changeMarkDescription(java.lang.String)
   */
  public void changeMarkDescription(Mark mark, String newDescription)
      throws SQLException {
    // TODO Auto-generated method stub

  }

  public void changeMarkCoeff(Mark mark, float newCoeff) throws SQLException {
    // TODO Auto-generated method stub
    
  }

}
