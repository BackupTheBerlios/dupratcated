
package fr.umlv.symphonie.data;

import java.util.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;

import com.sun.rowset.CachedRowSetImpl;

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
   */
  private static PreparedStatement connectAndPrepare(String request)throws SQLException {
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

  private static void connectAndUpdate(String request) throws SQLException {
    Connection connection = null;
    Statement statement = null;

    connection = ConnectionManager.createConnection();
    statement = connection.createStatement();
    
    statement.executeUpdate(request);
  }

  public static int createPrimaryKey(String table, String id) throws SQLException {

    ResultSet results = null;
    String request = "SELECT MAX(`" + id + "`) + 1 FROM `" + table + "`;";

    results = connectAndQuery(request);

    results.next();
    return results.getInt(1);
  }
  
  public static int getTimeStamp (String tableName)throws SQLException{
    String request = "select " + COLUMN_TABLE_NAME_FROM_TABLE_TIMESTAMP + ", " + COLUMN_TIMESTAMP_FROM_TABLE_TIMESTAMP + " " +
                     "from " + TABLE_TIMESTAMP + " " +
                     "where " + COLUMN_TABLE_NAME_FROM_TABLE_TIMESTAMP + " = '" + tableName + "' " +
                     ";";

    ResultSet result = connectAndQuery(request);
    
    CachedRowSetImpl rowSet = new CachedRowSetImpl();
    rowSet.populate(result);
    
    rowSet.first();

    return rowSet.getInt(result.findColumn(COLUMN_TIMESTAMP_FROM_TABLE_TIMESTAMP));
  }
  
  public void setNewTimestamp (String columnName, int newTimeStamp) throws SQLException{
    String request = "update " + TABLE_TIMESTAMP + " " +
                     "set " + COLUMN_TIMESTAMP_FROM_TABLE_TIMESTAMP + " = " + newTimeStamp + " " +
                     "where " + COLUMN_TABLE_NAME_FROM_TABLE_TIMESTAMP + " = '" + columnName + "' " +
                     ";";
    
    connectAndUpdate(request);
  }
  
  private int getKeyForTitle(String desc) throws SQLException {
    String request = "select " + COLUMN_ID_FROM_TABLE_TITLE + " " + "from "
        + TABLE_TITLE + " " + "where " + COLUMN_DESC_FROM_TABLE_TITLE + " = '"
        + desc + "';";

    ResultSet result = connectAndQuery(request);

    if (result.first() == false) return -1;

    return result.getInt(COLUMN_ID_FROM_TABLE_TITLE);
  }
  
  
  /*
   * methodes de base
   */
  
  private void syncStudentData() throws DataManagerException{
    ResultSet results = null;
    String request = "SELECT * FROM `" + TABLE_STUDENT + "`;";

    try {
      results = connectAndQuery(request);

      while (results.next()) {
        Student s = new Student(results.getInt(1), results.getString(2),
            results.getString(3), results.getString(4));

        if (studentMap.containsKey(s.getId()))
          studentMap.get(s.getId()).update(s);

        else
          studentMap.put(s.getId(), s);
      }
    } catch (SQLException e) {
      throw new DataManagerException("error synchronizing students from database.",
          e);
    }
  }
  
  private void syncStudentMarksData() throws DataManagerException {
    Map<Integer, Mark> courseMap = getMarks();
    Map<Integer, Student> studentMap = getStudents();

    ResultSet results = null;
    String request = "SELECT * " + "FROM " + TABLE_HAS_MARK + " " + ";";

    try {
      results = connectAndQuery(request);

      while (results.next()) {

        StudentMark sm = new StudentMark(studentMap.get(results
            .getInt(COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK)), markMap
            .get(results.getInt(COLUMN_ID_TEST_FROM_TABLE_HAS_MARK)), results
            .getFloat(COLUMN_MARK_FROM_TABLE_HAS_MARK));

        boolean isInList = false;

        for (StudentMark sm2 : studentMarkList) {                 //
          if (sm.getStudent().getId() == sm2.getStudent().getId() // alors ca
              && sm.getMark().getId() == sm2.getMark().getId()    // c'est tres
              && sm.getValue() != sm2.getValue()) {               // moche mais
            sm2.setValue(sm.getValue());                          // bon
            isInList = true;                                      //
            break;                                                //
          }
        }

        if (isInList == false) studentMarkList.add(sm);
      }
    } catch (SQLException e) {
      throw new DataManagerException("error synchronizing students' marks from database.", e);
    }
  }
  
  private void syncCourseData() throws DataManagerException {
    
    ResultSet results = null;
    String request = "SELECT * FROM `" + TABLE_COURSE + "`;";

    try {
      results = connectAndQuery(request);

      while (results.next()) {

        Course c = new Course(results.getInt(1), results.getString(2),
            results.getFloat(3));

        if (courseMap.containsKey(c.getId()))
          courseMap.get(c.getId()).update(c);

        else
          courseMap.put(c.getId(), c);
      }

      

    } catch (SQLException e) {
      throw new DataManagerException("error synchronizing courses from database.", e);
    }
  }
  
  private void syncMarkData() throws DataManagerException {
    Map<Integer, Course> courseMap = getCourses();

    ResultSet results = null;
    String request = "SELECT " + COLUMN_ID_FROM_TABLE_TEST + " , "
        + COLUMN_COEFF_FROM_TABLE_TEST + " , " + COLUMN_DESC_FROM_TABLE_TITLE
        + " , " + COLUMN_ID_COURSE_FROM_TABLE_TEST + " " + "FROM "
        + TABLE_TEST + " , " + TABLE_TITLE + " " + "WHERE " + TABLE_TEST
        + "." + COLUMN_ID_TITLE_FROM_TABLE_TEST + " = " + TABLE_TITLE + "."
        + COLUMN_ID_FROM_TABLE_TITLE + " " + ";";

    try {
      results = connectAndQuery(request);

      while (results.next()) {

        Mark m = new Mark(results.getInt(COLUMN_ID_FROM_TABLE_TEST), results
            .getString(COLUMN_DESC_FROM_TABLE_TITLE), results
            .getFloat(COLUMN_COEFF_FROM_TABLE_TEST), courseMap.get(results
            .getInt(COLUMN_ID_COURSE_FROM_TABLE_TEST)));

        if (markMap.containsKey(m.getId()))
          markMap.get(m.getId()).update(m);
        else
          markMap.put(m.getId(), m);
      }

      

    } catch (SQLException e) {
      throw new DataManagerException("error getting tests from database.", e);
    }
  }
  
  public Map<Integer, Student> getStudents() throws DataManagerException {

    int n;
    try {
      n = getTimeStamp(TABLE_STUDENT);
    } catch (SQLException e1) {
      n = studentMapTimeStamp;
    }
    
    if ( n > studentMapTimeStamp ) {
      syncStudentData();
      studentMapTimeStamp = n;
    }

    return studentMap;
  }

  public Map<Integer, Course> getCourses() throws DataManagerException {

    int n;
    try {
      n = getTimeStamp(TABLE_COURSE);
    } catch (SQLException e1) {
      n = courseMapTimeStamp-1;
    }

    if ( n > courseMapTimeStamp ) {
      syncCourseData();
      courseMapTimeStamp = n;
    }

    return courseMap;
  }

  public Map<Integer, Mark> getMarks() throws DataManagerException {
    int n;
    try {
      n = getTimeStamp(TABLE_TEST);
    } catch (SQLException e1) {
      n = markMapTimeStamp;
    }

    if ( n > markMapTimeStamp ) {
      syncMarkData();
      markMapTimeStamp = n;
    }

    return markMap;

  }

  public List<StudentMark> getStudentMarks() throws DataManagerException {
    
    int n;
    try {
      n = getTimeStamp(TABLE_HAS_MARK);
    } catch (SQLException e1) {
      n = studentMarkListTimeStamp;
    }

    if ( n > studentMarkListTimeStamp ) {
      syncStudentMarksData();
      studentMarkListTimeStamp = n;
    }

    return studentMarkList;
  }

  public Map<Integer, Mark> getMarksByCourse(Course c) throws DataManagerException {

    Map<Integer, Mark> markMap = getMarks();
    Map<Integer, Mark> resultMap = new HashMap<Integer, Mark>();

    for (Mark m : markMap.values()) {
      if (m.getCourse().getId() == c.getId()) resultMap.put(m.getId(), m);
    }

    return resultMap;

    /*
     * ResultSet results = null; String request = "select distinct " +
     * COLUMN_ID_FROM_TABLE_TEST + ", " + COLUMN_DESC_FROM_TABLE_TITLE + ", " +
     * COLUMN_COEFF_FROM_TABLE_TEST + " " + "from " + TABLE_TEST + ", " +
     * TABLE_TITLE + " " + "where " + TABLE_TEST + "." +
     * COLUMN_ID_COURSE_FROM_TABLE_TEST + " = " + c.getId() + " " + "and " +
     * TABLE_TEST + "." + COLUMN_ID_TITLE_FROM_TABLE_TEST + " = " + TABLE_TITLE +
     * "." + COLUMN_ID_FROM_TABLE_TITLE + ";"; try { results =
     * connectAndQuery(request); while (results.next()) { int id =
     * results.getInt(COLUMN_ID_FROM_TABLE_TEST); map.put(new Integer(id), new
     * Mark(id, results .getString(COLUMN_DESC_FROM_TABLE_TITLE), results
     * .getFloat(COLUMN_COEFF_FROM_TABLE_TEST), c)); } } catch (SQLException e) {
     * throw new DataManagerException("Error with current query :\n" + request); }
     * return map;
     */
  }


  private void updateStudentData (int supposedTimestamp) throws DataManagerException{
    /*
     * supposedTimestamp est la valeur 
     * qui devrait etre inseree dans
     * la table timestamp pour la cle
     * etudiants.
     * si elle y est deja ou si elle est
     * depasse, alors des modifs ont ete
     * apportees, et il faut synchroniser
     * les donnees.
     */
    int n;
    try {
      n = getTimeStamp(TABLE_STUDENT);
    } catch (SQLException e) {
      throw new DataManagerException("error getting timestamp for students.", e);
    }
    
    if (n >= supposedTimestamp){
      syncStudentData();
      supposedTimestamp = n + 1;
    }
    
    try {
      setNewTimestamp(TABLE_STUDENT, supposedTimestamp);
    } catch (SQLException e) {
      throw new DataManagerException("error setting new timestamp for students.", e);
    }
    
    studentMapTimeStamp = supposedTimestamp;
  }
  
  private void updateStudentMarksData (int supposedTimestamp) throws DataManagerException{
    int n;
    try {
      n = getTimeStamp(TABLE_HAS_MARK);
    } catch (SQLException e) {
      throw new DataManagerException("error getting timestamp for students' marks.", e);
    }
    
    if (n >= supposedTimestamp){
      syncStudentMarksData();
      supposedTimestamp = n + 1;
    }
    
    try {
      setNewTimestamp(TABLE_HAS_MARK, supposedTimestamp);
    } catch (SQLException e) {
      throw new DataManagerException("error setting new timestamp for students' marks.", e);
    }
    
    studentMarkListTimeStamp = supposedTimestamp;
  }
  
  private void updateCourseData (int supposedTimestamp) throws DataManagerException{
    int n;
    try {
      n = getTimeStamp(TABLE_COURSE);
    } catch (SQLException e) {
      throw new DataManagerException("error getting timestamp for courses.", e);
    }
    
    if (n >= supposedTimestamp){
      syncCourseData();
      supposedTimestamp = n + 1;
    }
    
    try {
      setNewTimestamp(TABLE_COURSE, supposedTimestamp);
    } catch (SQLException e) {
      throw new DataManagerException("error setting new timestamp for courses.", e);
    }
    
    courseMapTimeStamp = supposedTimestamp;
  }
  
  private void updateMarkData(int supposedTimestamp) throws DataManagerException{
    int n;
    try {
      n = getTimeStamp(TABLE_TEST);
    } catch (SQLException e) {
      throw new DataManagerException("error getting timestamp for tests.", e);
    }
    
    if (n >= supposedTimestamp){
      syncMarkData();
      supposedTimestamp = n + 1;
    }
    
    try {
      setNewTimestamp(TABLE_TEST, supposedTimestamp);
    } catch (SQLException e) {
      throw new DataManagerException("error setting new timestamp for tests.", e);
    }
    
    markMapTimeStamp = supposedTimestamp;
  }
  
  
  
  
  /*
   * methodes de la vue etudiant
   */
  public Map<Integer, StudentMark> getMarksByStudentAndCourse(Student s,
      Course c) throws DataManagerException {

    /* Map<Integer, Mark> markMap = getMarksByCourse(c); */

    List<StudentMark> studentMarkList = getStudentMarks();
    Map<Integer, StudentMark> studentMarkMap = new HashMap<Integer, StudentMark>();

    for (StudentMark sm : studentMarkList) {
      if (sm.getCourse().getId() == c.getId()
          && sm.getStudent().getId() == s.getId())
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
      // (version toutes les notes li�es a la meme epreuve
      // pour faciliter l'�dition de coeff)
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
        new Comparator<Student>() {

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

  
  
  
  
  /*
   * methodes servant a l'admin
   */
  public void addStudent(String name, String lastName) throws DataManagerException {
    int key = 0;

    try {
      key = createPrimaryKey(TABLE_STUDENT, COLUMN_ID_FROM_TABLE_STUDENT);
    } catch (SQLException e) {
      throw new DataManagerException("error creating primary key for new student " + lastName + " " + name, e);
    }

    String request = "INSERT INTO `" + TABLE_STUDENT + "` (`"
        + COLUMN_ID_FROM_TABLE_STUDENT + "`, `"
        + COLUMN_NAME_FROM_TABLE_STUDENT + "`, `"
        + COLUMN_LAST_NAME_FROM_TABLE_STUDENT + "`, `"
        + COLUMN_COMMENT_FROM_TABLE_STUDENT + "`) VALUES (" + key + ", '"
        + name + "', '" + lastName + "', NULL);";

    try {
      connectAndUpdate(request);
    } catch (SQLException e) {
      throw new DataManagerException("error inserting new student " + lastName + " " + name, e);
    }
    
    Student s = new Student(key, name, lastName);
    
    studentMap.put(key, s);
    
    try {
      updateStudentData(studentMapTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException("error updating data.", e);
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

  public void removeStudent(Student s) throws DataManagerException {
    
    /*
     * partie effacement de l'etudiant
     */
    
    // effacement de la base de donnees
    String request = "DELETE FROM `" + TABLE_STUDENT + "` WHERE `"
        + COLUMN_ID_FROM_TABLE_STUDENT + "`=" + s.getId() + ";";

    try {
      connectAndUpdate(request);
    }catch (SQLException e){
      throw new DataManagerException("error deleting student " + s + " from database.", e);
    }
    
    // mise a jour des donnees
    // (a cause des acces multiples) 
    try{
      updateStudentData(studentMapTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException ("error updating data for students.", e);
    }
    
    // effacement de la map locale
    Map<Integer, Student> studentMap = getStudents();
    studentMap.remove(s.getId());
    
    /*
     * partie effacement de ses notes
     */
    try {
      removeAllStudentMarksForStudent(s.getId());
    } catch (DataManagerException e) {
      throw new DataManagerException("error deleting all marks for student " + s, e);
    }
  }

  /**
   * @param studentId
   * @throws SQLException
   */
  private void removeAllStudentMarksForStudent(int studentId)throws DataManagerException {
    
    // effacement de la base de donnees
    String request = "delete from " + TABLE_HAS_MARK + " " + "where "
        + COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + " = " + studentId + ";";

    try {
      connectAndUpdate(request);
    } catch (SQLException e) {
      throw new DataManagerException("could not erase student's marks in database.", e);
    }
        
    // effacement de la liste des notes locale
    List<StudentMark> studentMarkList = getStudentMarks();
    
//    for (int i = 0 ; i < studentMarkList.size() ; i++){
//      if (studentMarkList.get(i).getStudent().getId() == studentId){
//        studentMarkList.remove(i);
//        i--; // necessaire pour bien parcourir la liste
//      }
//    }
    
    for (StudentMark sm : studentMarkList){
      if (sm.getStudent().getId() == studentId)
        studentMarkList.remove(sm);
    }
    
    // mise a jour des donnees
    // (a cause des acces multiples)
    try {
      updateStudentMarksData(studentMarkListTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException ("error updating data.", e);
    }
  }

  public void addCourse(String title, float coeff) throws DataManagerException {
    int key = 0;

    // on cree une cle pour la nouvelle matiere
    try {
      key = createPrimaryKey(TABLE_COURSE, COLUMN_ID_FROM_TABLE_COURSE);
    } catch (SQLException e) {
      throw new DataManagerException("error creating primary key for course " + title, e);
    }

    String request = "INSERT INTO `" + TABLE_COURSE + "` (`"
        + COLUMN_ID_FROM_TABLE_COURSE + "`, `" + COLUMN_TITLE_FROM_TABLE_COURSE
        + "`, `" + COLUMN_COEFF_FROM_TABLE_COURSE + "`) VALUES (" + key + ", '"
        + title + "', '" + coeff + "');";

    // on l'insere dans la table
    try {
      connectAndUpdate(request);
    } catch (SQLException e) {
      throw new DataManagerException("error inserting new course " + title + " into database.", e);
    }
    
    // mise a jour avec le timestamp
    try{
      updateCourseData(courseMapTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException("error updating data for courses.", e);
    }
    
    // ajout dans la map locale
    courseMap.put(key, new Course(key, title, coeff));
    
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
      connectAndUpdate(request);
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

  public int addTitle(String desc) throws DataManagerException {
    int key = 0;

    try {
      key = createPrimaryKey(TABLE_TITLE, COLUMN_ID_FROM_TABLE_TITLE);
    } catch (SQLException e) {
      throw new DataManagerException(
          "error creating new primary key for title " + desc, e);
    }

    String request = "INSERT INTO `" + TABLE_TITLE + "` (`"
        + COLUMN_ID_FROM_TABLE_TITLE + "`, `" + COLUMN_DESC_FROM_TABLE_TITLE
        + "`) VALUES (" + key + ", '" + desc + "');";

    try {
      connectAndUpdate(request);
    } catch (SQLException e) {
      throw new DataManagerException("error inserting new title " + desc, e);
    }

    return key;
  }

  public void addMark(String desc, float coeff, Course c)
      throws DataManagerException {
    int titleKey = -1;

    try {
      titleKey = getKeyForTitle(desc);
    } catch (SQLException e) {
      throw new DataManagerException("error getting key for title " + desc, e);
    }

    if (titleKey < 0) {
      try {
        titleKey = addTitle(desc);
      } catch (DataManagerException e) {
        throw new DataManagerException("error adding new Mark " + desc
            + " related to " + c, e);
      }
    }

    int markKey = -1;

    try {
      markKey = createPrimaryKey(TABLE_TEST, COLUMN_ID_FROM_TABLE_TEST);
    } catch (SQLException e) {
      throw new DataManagerException("error creating new primary key for mark "
          + desc + " related to " + c, e);
    }

    String request = "insert into " + TABLE_TEST + " " + "values (" + markKey
        + ", " + coeff + ", " + titleKey + ", " + c.getId() + ");";

    try {
      connectAndUpdate(request);
    } catch (SQLException e) {
      throw new DataManagerException ("error inserting new test " + desc + " related to " + c, e);
    }

    Mark m = new Mark(markKey, desc, coeff, c);
    markMap.put(m.getId(), m);
    
    try {
      updateMarkData(markMapTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException("error updating data.", e);
    }
    
    try {
      setDefaultMarkToAllStudents(markKey);
    } catch (DataManagerException e) {
      throw new DataManagerException("error initializing marks for new test "
          + desc + "related to " + c, e);
    }

  }

  /**
   * @param markKey
   */
  private void setDefaultMarkToAllStudents(int markKey) throws DataManagerException {
    Map<Integer, Student> studentMap = getStudents();
    PreparedStatement preparedStatement = null;

    String request = "insert into " + TABLE_HAS_MARK + "values (?, " + markKey
        + ", 0);";

    try {
      preparedStatement = connectAndPrepare(request);
    } catch (SQLException e) {
      throw new DataManagerException("error preparing statement for inserting default marks.", e);
    }

    for (Student s : studentMap.values()) {
      try {
        preparedStatement.setInt(1, s.getId());
        preparedStatement.executeUpdate();
      } catch (SQLException e) {
        throw new DataManagerException("error inserting default mark for student " + s);
      }
      studentMarkList.add(new StudentMark(s, markMap.get(markKey), 0f));
    }
    
    try{
      updateStudentMarksData(studentMarkListTimeStamp + 1);
    }catch(DataManagerException e){
      throw new DataManagerException("error updating data.", e);
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
    }/*
       * catch (DataManagerException e){ }
       */

  }

  /**
   * @param id
   */
  public void removeAllStudentMarksForMark(int markId) throws SQLException {
    String request = "delete from " + TABLE_HAS_MARK + " " + "where "
        + COLUMN_ID_TEST_FROM_TABLE_HAS_MARK + " = " + markId + ";";

    connectAndQuery(request);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.umlv.symphonie.data.DataManager#changeStudentMarkValue(float)
   */
  public void changeStudentMarkValue(StudentMark studentMark, float newValue)
      throws DataManagerException {
    
    System.out.println("new value to set : " + newValue);
    System.out.println(studentMark.getMark() + " " + studentMark.getCourse());
    
    String request = "update " + TABLE_HAS_MARK + " " + "set "
        + COLUMN_MARK_FROM_TABLE_HAS_MARK + " = " + newValue + " " + "where "
        + COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + " = "
        + studentMark.getStudent().getId() + " " + "and "
        + COLUMN_ID_TEST_FROM_TABLE_HAS_MARK + " = "
        + studentMark.getMark().getId() + " " + ";";

    try {
      connectAndUpdate(request);
    } catch (SQLException e) {
      throw new DataManagerException("unable to set new value for "
          + studentMark.getStudent() + " in " + studentMark.getMark()
          + " related to course " + studentMark.getCourse(), e);
    }

    studentMark.setValue(newValue);
    
    try{
      updateStudentMarksData(studentMarkListTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException("error updating data.", e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.umlv.symphonie.data.DataManager#changeMarkDescription(java.lang.String)
   */
  public void changeMarkDescription(Mark mark, String newDescription)throws DataManagerException {
    int markKey = -1;
    
    // on cherche si l'intitule est deja dans la base
    try {
      markKey = getKeyForTitle(newDescription);
    } catch (SQLException e) {
      throw new DataManagerException("error resolving primary key for title "
          + newDescription, e);
    }

    // s'il ne l'est pas on le rajoute dans la table des intitules
    if (markKey < 0) markKey = addTitle(newDescription);

    
    // on met a jour la reference dans la table
    String request = "update " + TABLE_TEST + " " + "set "
        + COLUMN_ID_TITLE_FROM_TABLE_TEST + " = " + markKey + " " + "where "
        + COLUMN_ID_FROM_TABLE_TEST + " = " + mark.getId() + " " + ";";

    try {
      connectAndUpdate(request);
    } catch (SQLException e) {
      throw new DataManagerException("error updating description for test "
          + mark + "related to " + mark.getCourse(), e);
    }

    // on met a jour dans les donnees locales
    mark.setDesc(newDescription);
    
    // on synchronise si besoin est
    try{
      updateMarkData(markMapTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException("error updating data.", e);
    }
  }

  public void changeMarkCoeff(Mark mark, float newCoeff)
      throws DataManagerException {

    String request = "UPDATE " + TABLE_TEST + " " + "SET "
        + COLUMN_COEFF_FROM_TABLE_TEST + " = '" + newCoeff + "' " + "WHERE "
        + COLUMN_ID_FROM_TABLE_TEST + " = '" + mark.getId() + "' " + ";";

    System.out.println(request);

    try {
      connectAndUpdate(request);
    } catch (SQLException e) {
      throw new DataManagerException("unable to change coefficient for " + mark
          + " related to " + mark.getCourse(), e);
    }

    mark.setCoeff(newCoeff);
    
    try{
      updateMarkData(markMapTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException("error updating data.", e);
    }
  }

  public void changeStudentComment(Student s, String newComment)
      throws DataManagerException {
    s.setComment(newComment);

    String request = "update " + TABLE_STUDENT + " " + "set "
        + COLUMN_COMMENT_FROM_TABLE_STUDENT + " = '" + newComment + "' "
        + "where " + COLUMN_ID_FROM_TABLE_STUDENT + " = " + s.getId() + ";";

    try {
      connectAndUpdate(request);
    } catch (SQLException e) {
      throw new DataManagerException("unable to set comment for student " + s,
          e);
    }
    
    try {
      updateStudentData(studentMapTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException("error updating data.", e);
    }

  }

}
