package fr.umlv.symphonie.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.sql.rowset.CachedRowSet;

import com.mysql.jdbc.UpdatableResultSet;
import com.sun.rowset.CachedRowSetImpl;

import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.data.formula.SymphonieFormulaFactory;
import fr.umlv.symphonie.data.formula.lexer.LexerException;
import fr.umlv.symphonie.data.formula.parser.ParserException;
import fr.umlv.symphonie.util.Pair;
import static fr.umlv.symphonie.data.SQLDataManagerConstants.*;

public class SQLDataManager implements
		DataManager {

	private final Map<Integer, Student> studentMap = new HashMap<Integer, Student>();

	private final List<Student> studentList = new ArrayList<Student>();

	private int studentMapTimeStamp = -1;

	private final Map<Integer, Mark> markMap = new HashMap<Integer, Mark>();

	private int markMapTimeStamp = -1;

	private final Map<Integer, Course> courseMap = new HashMap<Integer, Course>();

	private final List<Course> courseList = new ArrayList<Course>();

	private int courseMapTimeStamp = -1;

	private final List<StudentMark> studentMarkList = new ArrayList<StudentMark>();

	private int studentMarkListTimeStamp = -1;

  
  /*
   * ici
   */            //id_course //liste de formules
  private final Map<Integer, List<Formula>> teacherFormulaMap = new HashMap<Integer, List<Formula>>();
  private int teacherFormulaMapTimeStamp = -1;
  
  private final List<Formula> juryFormulaList = new ArrayList<Formula>();
  private int juryFormulaListTimeStamp = 1;
  
	private final Comparator<Student> studentComparator = new Comparator<Student>() {
		public int compare(Student arg0, Student arg1) {
			int n = arg0.getLastName().compareToIgnoreCase(arg1.getLastName());

			if (n == 0)
				n = arg0.getName().compareToIgnoreCase(arg1.getName());

			if (n == 0)
				n = arg0.getId() - arg1.getId();

			return n;
		}
	};

	private final Comparator<Course> courseComparator = new Comparator<Course>() {
		public int compare(Course o1, Course o2) {
			return o1.getTitle().compareToIgnoreCase(o2.getTitle());
		}
	};

  private static SQLDataManager instance = null;
  
  
  private SQLDataManager (){
  }
  
  public static SQLDataManager getInstance(){
    if (instance == null)
      instance = new SQLDataManager();
    
    return instance;
  }
  
  
  
  
  
  
	/*
	 * methodes internes
	 */
	private static PreparedStatement connectAndPrepare(String request)
			throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		connection = ConnectionManager.createConnection();
		preparedStatement = connection.prepareStatement(request);

		return preparedStatement;
	}

	private static CachedRowSet connectAndQuery(String request)
			throws SQLException {
		Connection connection = null;
		Statement statement = null;

		connection = ConnectionManager.createConnection();
		statement = connection.createStatement();

		CachedRowSetImpl rowSet = new CachedRowSetImpl();
		rowSet.populate(statement.executeQuery(request));
		return rowSet;
	}

	private static void connectAndUpdate(String request) throws SQLException {
		Connection connection = null;
		Statement statement = null;

		connection = ConnectionManager.createConnection();
		statement = connection.createStatement();

		statement.executeUpdate(request);
	}

	public static int createPrimaryKey(String table, String id)
			throws SQLException {

		CachedRowSet results = null;
		String request = "SELECT MAX(" + id + ") + 1 FROM " + table + ";";

		results = connectAndQuery(request);

		results.next();
		return results.getInt(1);
	}

	public static int getTimeStamp(String tableName) throws SQLException {
		String request = "select " + COLUMN_TABLE_NAME_FROM_TABLE_TIMESTAMP
				+ ", " + COLUMN_TIMESTAMP_FROM_TABLE_TIMESTAMP + " " + "from "
				+ TABLE_TIMESTAMP + " " + "where "
				+ COLUMN_TABLE_NAME_FROM_TABLE_TIMESTAMP + " = '" + tableName
				+ "' " + ";";

		CachedRowSet result = connectAndQuery(request);

		CachedRowSetImpl rowSet = new CachedRowSetImpl();
		rowSet.populate(result);

		rowSet.first();

		return rowSet.getInt(result
				.findColumn(COLUMN_TIMESTAMP_FROM_TABLE_TIMESTAMP));
	}

	public void setNewTimestamp(String columnName, int newTimeStamp)
			throws SQLException {
		String request = "update " + TABLE_TIMESTAMP + " " + "set "
				+ COLUMN_TIMESTAMP_FROM_TABLE_TIMESTAMP + " = " + newTimeStamp
				+ " " + "where " + COLUMN_TABLE_NAME_FROM_TABLE_TIMESTAMP
				+ " = '" + columnName + "' " + ";";

		connectAndUpdate(request);
	}

	private int getKeyForTitle(String desc) throws SQLException {
		String request = "select " + COLUMN_ID_FROM_TABLE_TITLE + " " + "from "
				+ TABLE_TITLE + " " + "where " + COLUMN_DESC_FROM_TABLE_TITLE
				+ " = '" + desc + "';";

		CachedRowSet result = connectAndQuery(request);

		if (result.first() == false)
			return -1;

		return result.getInt(COLUMN_ID_FROM_TABLE_TITLE);
	}

	/*
	 * methodes de base
	 */
	private void syncStudentData() throws DataManagerException {
		CachedRowSet results = null;
		String request = "SELECT * FROM " + TABLE_STUDENT + ";";
		Map<Integer, Student> tmpMap = new HashMap<Integer, Student>();

		try {
			results = connectAndQuery(request);

			while (results.next()) {
				Student s = new Student(results.getInt(1),
						results.getString(2), results.getString(3), results
								.getString(4));

				tmpMap.put(s.getId(), s);

				// if (studentMap.containsKey(s.getId()))
				// studentMap.get(s.getId()).update(s);
				//
				// else
				// studentMap.put(s.getId(), s);
			}
		} catch (SQLException e) {
			throw new DataManagerException(
					"error synchronizing students from database.", e);
		}

		for (int i : tmpMap.keySet()) {
			if (studentMap.containsKey(i))
				studentMap.get(i).update(tmpMap.get(i));
			else {
				studentMap.put(i, tmpMap.get(i));
				studentList.add(tmpMap.get(i));
				Collections.sort(studentList, studentComparator);
			}
		}

		for (int i : studentMap.keySet()) {
			if (tmpMap.containsKey(i) == false) {
				studentList.remove(studentMap.get(i));
				studentMap.remove(i);
			}
		}
	}

	private void syncStudentMarksData() throws DataManagerException {

		List<StudentMark> tmpList = new ArrayList<StudentMark>();
		Map<Integer, Mark> courseMap = getMarks();
		Map<Integer, Student> studentMap = getStudents();

		CachedRowSet results = null;
		String request = "SELECT * " + "FROM " + TABLE_HAS_MARK + " " + ";";

		try {
			results = connectAndQuery(request);

			while (results.next()) {

				StudentMark sm = new StudentMark(studentMap.get(results
						.getInt(COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK)),
						markMap.get(results
								.getInt(COLUMN_ID_TEST_FROM_TABLE_HAS_MARK)),
						results.getFloat(COLUMN_MARK_FROM_TABLE_HAS_MARK));

				tmpList.add(sm);

				// boolean isInList = false;
				//
				// for (StudentMark sm2 : studentMarkList) { //
				// if (sm.getStudent().getId() == sm2.getStudent().getId() //
				// alors ca
				// && sm.getMark().getId() == sm2.getMark().getId() // c'est
				// tres
				// && sm.getValue() != sm2.getValue()) { // moche mais
				// sm2.setValue(sm.getValue()); // bon
				// isInList = true; //
				// break; //
				// }
				// }
				//
				// if (isInList == false) studentMarkList.add(sm);
			}
		} catch (SQLException e) {
			throw new DataManagerException(
					"error synchronizing students' marks from database.", e);
		}

		int n;
		for (StudentMark sm : tmpList) {
			n = studentMarkList.indexOf(sm);

			if (n != -1)
				studentMarkList.get(n).setValue(sm.getValue());
			else
				studentMarkList.add(sm);
		}

		for (int i = 0; i < studentMarkList.size(); i++) {
			n = tmpList.indexOf(studentMarkList.get(i));

			if (n == -1) {
				studentMarkList.remove(i);
				i--;
			}
		}

	}

	private void syncCourseData() throws DataManagerException {

		CachedRowSet results = null;
		String request = "SELECT * FROM " + TABLE_COURSE + ";";
		Map<Integer, Course> tmpMap = new HashMap<Integer, Course>();

		try {
			results = connectAndQuery(request);

			while (results.next()) {

				Course c = new Course(results.getInt(1), results.getString(2),
						results.getFloat(3));

				tmpMap.put(c.getId(), c);

				// if (courseMap.containsKey(c.getId()))
				// courseMap.get(c.getId()).update(c);
				//
				// else
				// courseMap.put(c.getId(), c);
			}
		} catch (SQLException e) {
			throw new DataManagerException(
					"error synchronizing courses from database.", e);
		}

		for (int i : tmpMap.keySet()) {
			if (courseMap.containsKey(i))
				courseMap.get(i).update(tmpMap.get(i));
			else {
				courseMap.put(i, tmpMap.get(i));
				courseList.add(tmpMap.get(i));
				Collections.sort(courseList, courseComparator);
			}
		}

		for (int i : courseMap.keySet()) {
			if (tmpMap.containsKey(i) == false) {
				courseList.remove(courseMap.get(i));
				courseMap.remove(i);
			}
		}
	}

	private void syncMarkData() throws DataManagerException {

		Map<Integer, Course> courseMap = getCourses();
		Map<Integer, Mark> tmpMap = new HashMap<Integer, Mark>();

		CachedRowSet results = null;
		String request = "SELECT " + COLUMN_ID_FROM_TABLE_TEST + " , "
				+ COLUMN_COEFF_FROM_TABLE_TEST + " , "
				+ COLUMN_DESC_FROM_TABLE_TITLE + " , "
				+ COLUMN_ID_COURSE_FROM_TABLE_TEST + " " + "FROM " + TABLE_TEST
				+ " , " + TABLE_TITLE + " " + "WHERE " + TABLE_TEST + "."
				+ COLUMN_ID_TITLE_FROM_TABLE_TEST + " = " + TABLE_TITLE + "."
				+ COLUMN_ID_FROM_TABLE_TITLE + " " + ";";

		try {
			results = connectAndQuery(request);

			while (results.next()) {

				Mark m = new Mark(results.getInt(COLUMN_ID_FROM_TABLE_TEST),
						results.getString(COLUMN_DESC_FROM_TABLE_TITLE),
						results.getFloat(COLUMN_COEFF_FROM_TABLE_TEST),
						courseMap.get(results
								.getInt(COLUMN_ID_COURSE_FROM_TABLE_TEST)));

				// if (markMap.containsKey(m.getId()))
				// markMap.get(m.getId()).update(m);
				// else
				// markMap.put(m.getId(), m);

				tmpMap.put(m.getId(), m);
			}
		} catch (SQLException e) {
			throw new DataManagerException(
					"error getting tests from database.", e);
		}

		for (int i : tmpMap.keySet()) {
			if (markMap.containsKey(i))
				markMap.get(i).update(tmpMap.get(i));
			else
				markMap.put(i, tmpMap.get(i));
		}

		for (int i : markMap.keySet()) {
			if (tmpMap.containsKey(i) == false)
				markMap.remove(i);
		}
	}

  /*
   * ici
   */
  private void syncTeacherFormulaData() throws DataManagerException{
    Map<Integer, List<Formula>> tmpMap = new HashMap<Integer, List<Formula>>();
    
    String request = "select " + COLUMN_ID_FORMULA_FROM_TABLE_FORMULA + ", " + 
                     COLUMN_DESC_FROM_TABLE_TITLE + ", " + 
                     COLUMN_ID_COURSE_FROM_TABLE_TEACHER_FORMULA + ", " +
                     COLUMN_EXPRESSION_FROM_TABLE_FORMULA + ", " +
                     COLUMN_COLUMN_FROM_TABLE_FORMULA + " " +
                     "from " + TABLE_TEACHER_FORMULA + ", " + TABLE_TITLE + " " +
                     "where " + TABLE_TITLE + "." + COLUMN_ID_FROM_TABLE_TITLE + " = " + TABLE_TEACHER_FORMULA + "." + COLUMN_ID_COURSE_FROM_TABLE_TEACHER_FORMULA + " " +
                     "order by " + COLUMN_COLUMN_FROM_TABLE_FORMULA + " " +
                     ";";
    
    CachedRowSet result = null;
    int courseKey;
    
    try {
      result = connectAndQuery(request);
      
      while (result.next()){
        
        Formula f;
        try {
          f = SymphonieFormulaFactory.parseFormula(result
              .getString(COLUMN_DESC_FROM_TABLE_TITLE), result
              .getString(COLUMN_EXPRESSION_FROM_TABLE_FORMULA), result
              .getInt(COLUMN_ID_FORMULA_FROM_TABLE_FORMULA), result
              .getInt(COLUMN_COLUMN_FROM_TABLE_FORMULA));
        } catch (ParserException e1) {
          continue;
        } catch (LexerException e1) {
          continue;
        } catch (IOException e1) {
          continue;
        }
        
        courseKey = result.getInt(COLUMN_ID_COURSE_FROM_TABLE_TEACHER_FORMULA);
        
        List<Formula> list = tmpMap.get(courseKey);

        if (list == null) {
          list = new ArrayList<Formula>();
          tmpMap.put(courseKey, list);
        }
        list.add(f);
    }
    }catch(SQLException e){
      throw new DataManagerException("error getting teacher formulas from database", e);
    }
    
    List<Formula> tmpList;
    List<Formula> localList;
    
    // for each list contained in tmpMap
    for (int i : tmpMap.keySet()){
      tmpList = tmpMap.get(i);
      
      // if the list is not null
      if (tmpList != null){
        
        // take the corresponding list in local map
        localList = teacherFormulaMap.get(i);
        
        // if the local list is null just
        // copy the tmp list
        if (localList == null)
          teacherFormulaMap.put(i, tmpList);
        
        // else insert all formulas from tmp list
        // into local list
        else {
          for (Formula f : tmpList)
            if (localList.indexOf(f) == -1)
              localList.add(f); // changer cette ligne
        }
      }
    }
    
    // for each list contained in local map
    for (int i : teacherFormulaMap.keySet()){
      localList = teacherFormulaMap.get(i);
      
      // if the list is not null
      if (localList != null){
        
        // take the corresponding list in tmpMap
        tmpList = tmpMap.get(i);
        
        // if the tmp list is null just
        // remove the local list from local map
        if (tmpList == null){
          teacherFormulaMap.remove(i);
        }
        
        // else remove every formula from local list
        // that is not in tmp list
        else {
          for (int j = 0 ; j < localList.size() ; j++)
            if (tmpList.contains(localList.get(j)) == false){
              localList.remove(j);
              j--;
            }
        }
      }
    }
  }
  
  
  private void syncJuryFormulaData() throws DataManagerException{
    
    String request = "select " + COLUMN_ID_FORMULA_FROM_TABLE_FORMULA + ", " + 
    COLUMN_DESC_FROM_TABLE_TITLE + ", " + 
    COLUMN_EXPRESSION_FROM_TABLE_FORMULA + ", " +
    COLUMN_COLUMN_FROM_TABLE_FORMULA + " " +
    "from " + TABLE_JURY_FORMULA + ", " + TABLE_TITLE + " " +
    "where " + TABLE_TITLE + "." + COLUMN_ID_FROM_TABLE_TITLE + " = " + TABLE_TEACHER_FORMULA + "." + COLUMN_ID_COURSE_FROM_TABLE_TEACHER_FORMULA + " " +
    "order by " + COLUMN_COLUMN_FROM_TABLE_FORMULA + " " +
    ";";

    CachedRowSet result = null;
    
    List<Formula> tmpList = new ArrayList<Formula>();
    
    try {
      // get result from query
      result = connectAndQuery(request);
      
      // for each row from result
      while (result.next()){
      
        // build new formula
        Formula f;
        try {
          f = SymphonieFormulaFactory.parseFormula(result
              .getString(COLUMN_DESC_FROM_TABLE_TITLE), result
              .getString(COLUMN_EXPRESSION_FROM_TABLE_FORMULA), result
              .getInt(COLUMN_ID_FORMULA_FROM_TABLE_FORMULA), result
              .getInt(COLUMN_COLUMN_FROM_TABLE_FORMULA));
        } catch (ParserException e1) {
          continue;
        } catch (LexerException e1) {
          continue;
        } catch (IOException e1) {
          continue;
        }

        // add into tmp list
        tmpList.add(f);
    }
    }catch(SQLException e){
      throw new DataManagerException("error getting jury formulas from database", e);
    }
    
    int n;
    
    // for each formula from tmp list
    for (Formula f : tmpList){
      n = juryFormulaList.indexOf(f);
      
      // if it is not present in local list
      if (n < 0){
        // add it
        juryFormulaList.add(f);
      }
      else {
        // mettre a jour les donnees
        // mais je crois qu'on va pas le faire
      }
    }
    
    // for each formula from local list
    for (int i = 0 ; i < juryFormulaList.size() ; i++){
      n = tmpList.indexOf(juryFormulaList.get(i));
      
      // if it is not in tmp list
      if (n < 0){
        // remove it
        juryFormulaList.remove(i);
        i--;
      }
    }
  }
  
  
	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#getStudents()
	 */
	public Map<Integer, Student> getStudents() throws DataManagerException {

		int n;
		try {
			n = getTimeStamp(TABLE_STUDENT);
		} catch (SQLException e1) {
			n = studentMapTimeStamp;
		}

		if (n > studentMapTimeStamp) {
			syncStudentData();
			studentMapTimeStamp = n;
		}

		return studentMap;
	}

	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#getStudentList()
	 */
	public List<Student> getStudentList() throws DataManagerException {
		int n;

		try {
			n = getTimeStamp(TABLE_STUDENT);
		} catch (SQLException e) {
			n = studentMapTimeStamp;
		}

		if (n > studentMapTimeStamp) {
			syncStudentData();
			studentMapTimeStamp = n;
		}

		return studentList;
	}

	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#getCourses()
	 */
	public Map<Integer, Course> getCourses() throws DataManagerException {

		int n;
		try {
			n = getTimeStamp(TABLE_COURSE);
		} catch (SQLException e1) {
			n = courseMapTimeStamp - 1;
		}

		if (n > courseMapTimeStamp) {
			syncCourseData();
			courseMapTimeStamp = n;
		}

		return courseMap;
	}

	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#getCoursesList()
	 */
	public List<Course> getCoursesList() throws DataManagerException {
		int n;
		try {
			n = getTimeStamp(TABLE_COURSE);
		} catch (SQLException e1) {
			n = courseMapTimeStamp - 1;
		}

		if (n > courseMapTimeStamp) {
			syncCourseData();
			courseMapTimeStamp = n;
		}

		return courseList;
	}

	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#getMarks()
	 */
	public Map<Integer, Mark> getMarks() throws DataManagerException {
		int n;
		try {
			n = getTimeStamp(TABLE_TEST);
		} catch (SQLException e1) {
			n = markMapTimeStamp;
		}

		if (n > markMapTimeStamp) {
			syncMarkData();
			markMapTimeStamp = n;
		}

		return markMap;

	}

	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#getStudentMarks()
	 */
	public List<StudentMark> getStudentMarks() throws DataManagerException {

		int n;
		try {
			n = getTimeStamp(TABLE_HAS_MARK);
		} catch (SQLException e1) {
			n = studentMarkListTimeStamp;
		}

		if (n > studentMarkListTimeStamp) {
			syncStudentMarksData();
			studentMarkListTimeStamp = n;
		}

		return studentMarkList;
	}

  
  /*
   * la
   */
	private Map<Integer, List<Formula>> getTeacherFormulas() throws DataManagerException {
    int n;
    
    try {
      n = getTimeStamp(TABLE_TEACHER_FORMULA);
    }catch (SQLException e){
      n = teacherFormulaMapTimeStamp;
    }
    
    if (n > teacherFormulaMapTimeStamp) {
      syncTeacherFormulaData();
      teacherFormulaMapTimeStamp = n;
    }
    
    return teacherFormulaMap;
  }
  
  
  /*
   * la
   */
  public List<Formula> getFormulasByCourse(Course c) throws DataManagerException{
    Map<Integer, List<Formula>> tmpMap = getTeacherFormulas();
    
    return tmpMap.get(c.getId());
  }
  
  
  public List<Formula> getJuryFormulas() throws DataManagerException{
    int n;
    
    try {
      n = getTimeStamp(TABLE_JURY_FORMULA);
    }catch (SQLException e){
      n = juryFormulaListTimeStamp;
    }
    
    if (n > juryFormulaListTimeStamp) {
      syncJuryFormulaData();
      juryFormulaListTimeStamp = n;
    }
    
    return juryFormulaList;
  }
  
  
  
  /* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#getMarksByCourse(fr.umlv.symphonie.data.Course)
	 */
	public Map<Integer, Mark> getMarksByCourse(Course c)
			throws DataManagerException {
    
		Map<Integer, Mark> markMap = getMarks();
		Map<Integer, Mark> resultMap = new HashMap<Integer, Mark>();

		for (Mark m : markMap.values()) {
			if (m.getCourse().getId() == c.getId())
				resultMap.put(m.getId(), m);
		}

		return resultMap;
	}

	private void updateStudentData(int supposedTimestamp)
			throws DataManagerException {
		/*
		 * supposedTimestamp est la valeur qui devrait etre inseree dans la
		 * table timestamp pour la cle etudiants. si elle y est deja ou si elle
		 * est depasse, alors des modifs ont ete apportees, et il faut
		 * synchroniser les donnees.
		 */
		int n;
		try {
			n = getTimeStamp(TABLE_STUDENT);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error getting timestamp for students.", e);
		}

		if (n >= supposedTimestamp) {
			syncStudentData();
			supposedTimestamp = n + 1;
		}

		try {
			setNewTimestamp(TABLE_STUDENT, supposedTimestamp);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error setting new timestamp for students.", e);
		}

		studentMapTimeStamp = supposedTimestamp;
	}

	private void updateStudentMarksData(int supposedTimestamp)
			throws DataManagerException {
		int n;
		try {
			n = getTimeStamp(TABLE_HAS_MARK);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error getting timestamp for students' marks.", e);
		}

		if (n >= supposedTimestamp) {
			syncStudentMarksData();
			supposedTimestamp = n + 1;
		}

		try {
			setNewTimestamp(TABLE_HAS_MARK, supposedTimestamp);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error setting new timestamp for students' marks.", e);
		}

		studentMarkListTimeStamp = supposedTimestamp;
	}

	private void updateCourseData(int supposedTimestamp)
			throws DataManagerException {
		int n;
		try {
			n = getTimeStamp(TABLE_COURSE);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error getting timestamp for courses.", e);
		}

		if (n >= supposedTimestamp) {
			syncCourseData();
			supposedTimestamp = n + 1;
		}

		try {
			setNewTimestamp(TABLE_COURSE, supposedTimestamp);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error setting new timestamp for courses.", e);
		}

		courseMapTimeStamp = supposedTimestamp;
	}

	private void updateMarkData(int supposedTimestamp)
			throws DataManagerException {
		int n;
		try {
			n = getTimeStamp(TABLE_TEST);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error getting timestamp for tests.", e);
		}

		if (n >= supposedTimestamp) {
			syncMarkData();
			supposedTimestamp = n + 1;
		}

		try {
			setNewTimestamp(TABLE_TEST, supposedTimestamp);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error setting new timestamp for tests.", e);
		}

		markMapTimeStamp = supposedTimestamp;
	}

	private void updateTeacherFormulaData(int supposedTimestamp) throws DataManagerException{
   int n;
   
   try {
     n = getTimeStamp(TABLE_TEACHER_FORMULA);
   }catch (SQLException e){
     throw new DataManagerException("error getting timestamp for teacher formulas.", e);
   }
   
   if (n >= supposedTimestamp){
     syncTeacherFormulaData();
     supposedTimestamp = n + 1;
   }
   
   try {
     setNewTimestamp(TABLE_TEACHER_FORMULA, supposedTimestamp);
   } catch (SQLException e) {
     throw new DataManagerException("error setting new timestamp for teacher formulas.", e);
   }
   
   teacherFormulaMapTimeStamp = supposedTimestamp;
  }
  
  private void updateJuryFormulaData(int supposedTimestamp) throws DataManagerException{
    int n;
    
    try {
      n = getTimeStamp(TABLE_JURY_FORMULA);
    }catch (SQLException e){
      throw new DataManagerException("error getting timestamp for jury formulas.", e);
    }
    
    if (n >= supposedTimestamp){
      syncJuryFormulaData();
      supposedTimestamp = n + 1;
    }
    
    try {
      setNewTimestamp(TABLE_JURY_FORMULA, supposedTimestamp);
    } catch (SQLException e) {
      throw new DataManagerException("error setting new timestamp for jury formulas.", e);
    }
    
    juryFormulaListTimeStamp = supposedTimestamp;
  }
  
  /*
	 * methodes de la vue etudiant
	 */
	public Map<Integer, StudentMark> getMarksByStudentAndCourse(Student s,
			Course c) throws DataManagerException {

		/* Map <Integer, Mark> markMap = getMarksByCourse(c); */

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
		/*
		 * new Comparator <Student>() {
		 * 
		 * public int compare(Student arg0, Student arg1) { int n =
		 * arg0.getLastName().compareToIgnoreCase(arg1.getLastName());
		 * 
		 * if (n == 0) n = arg0.getName().compareToIgnoreCase(arg1.getName());
		 * 
		 * if (n == 0) return arg0.getId() - arg1.getId();
		 * 
		 * return n; }
		 *  }
		 */studentComparator);

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
		/*
		 * new Comparator <Student>() {
		 * 
		 * public int compare(Student arg0, Student arg1) { int n =
		 * arg0.getLastName().compareToIgnoreCase(arg1.getLastName());
		 * 
		 * if (n == 0) n = arg0.getName().compareToIgnoreCase(arg1.getName());
		 * 
		 * if (n == 0) return arg0.getId() - arg1.getId();
		 * 
		 * return n; } }
		 */studentComparator);

		for (Student s : studentMap.values()) {
			sortedMap.put(s, getAllMarksByStudent(s));
		}

		return new Pair<Map<Integer, Course>, SortedMap<Student, Map<Course, Map<Integer, StudentMark>>>>(
				courseMap, sortedMap);
	}

	/*
	 * methodes servant a l'admin
	 */
	public void addStudent(String name, String lastName)
			throws DataManagerException {
		int key = 0;

		// on cree une cle pour l'etudiant
		try {
			key = createPrimaryKey(TABLE_STUDENT, COLUMN_ID_FROM_TABLE_STUDENT);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error creating primary key for new student " + lastName
							+ " " + name, e);
		}

		String request = "INSERT INTO " + TABLE_STUDENT + " ("
				+ COLUMN_ID_FROM_TABLE_STUDENT + ", "
				+ COLUMN_NAME_FROM_TABLE_STUDENT + ", "
				+ COLUMN_LAST_NAME_FROM_TABLE_STUDENT + ", "
				+ COLUMN_COMMENT_FROM_TABLE_STUDENT + ") VALUES (" + key
				+ ", '" + name + "', '" + lastName + "', NULL);";

		// on l'insere dans la base
		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("error inserting new student "
					+ lastName + " " + name, e);
		}

		// on l'insere dans les donnees locales
		Student s = new Student(key, name, lastName);

		/* Map <Integer, Student> studentMap = getStudents(); */
		studentMap.put(key, s);

		/* List <Student> studentList = getStudentList(); */
		studentList.add(s);
		Collections.sort(studentList, studentComparator);

		// on update le tout
		try {
			updateStudentData(studentMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}

		// on lui ajoute des notes par defaut
		// dans toutes les matieres
		try {
			addDefaultMarksForStudent(s);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
	}

	private void addDefaultMarksForStudent(Student s)
			throws DataManagerException {
		Map<Integer, Mark> markMap = getMarks();
		PreparedStatement preparedStatement = null;

		String request = "insert into " + TABLE_HAS_MARK + " " + "values ( "
				+ s.getId() + ", ?, 0 );";

		try {
			preparedStatement = connectAndPrepare(request);
		} catch (SQLException e) {
			throw new DataManagerException("error preparing multi request.", e);
		}

		List<StudentMark> studentMarkList = getStudentMarks();

		for (int key : markMap.keySet()) {
			try {
				preparedStatement.setInt(1, key);
				preparedStatement.executeUpdate();
				studentMarkList.add(new StudentMark(s, markMap.get(key), 0f));
			} catch (SQLException e) {
				throw new DataManagerException(
						"error performing prepared statement.", e);
			}
		}

		try {
			updateStudentMarksData(studentMarkListTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
	}

	public void addStudents(List<Pair<String, String>> namesList)
			throws DataManagerException {

		String request = "INSERT INTO " + TABLE_STUDENT + " ("
				+ COLUMN_ID_FROM_TABLE_STUDENT + ", "
				+ COLUMN_NAME_FROM_TABLE_STUDENT + ", "
				+ COLUMN_LAST_NAME_FROM_TABLE_STUDENT + ", "
				+ COLUMN_COMMENT_FROM_TABLE_STUDENT
				+ ") VALUES (?, ?, ?, NULL);";

		PreparedStatement preparedStatement = null;
		int key = 0;

		try {
			preparedStatement = connectAndPrepare(request);
		} catch (SQLException e) {
			throw new DataManagerException("error preparing multi request.", e);
		}

		Map<Integer, Student> studentMap = getStudents(); // map des etudiants
		List<Student> studentList = getStudentList(); // liste des etudiants
		List<Student> studentToAddList = new ArrayList<Student>(); // liste des
																	// etudiants
																	// qu'on
																	// rajoute

		for (Pair<String, String> p : namesList) {

			// on cree une cle pour chaque etudiant
			try {
				key = createPrimaryKey(TABLE_STUDENT,
						COLUMN_ID_FROM_TABLE_STUDENT);
			} catch (SQLException e) {
				throw new DataManagerException(
						"error creating new primary key for student "
								+ p.getFirst() + " " + p.getSecond(), e);
			}

			// on ajoute chaque etudiant dans la base
			try {
				preparedStatement.setInt(1, key);
				preparedStatement.setString(2, p.getFirst());
				preparedStatement.setString(3, p.getSecond());
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				throw new DataManagerException(
						"error performing prepared statement.", e);
			}

			Student s = new Student(key, p.getFirst(), p.getSecond());

			// on l'ajoute dans la map et dans la liste locale
			studentMap.put(s.getId(), s);
			studentList.add(s);
			Collections.sort(studentList, studentComparator);

			// et on l'ajoute dans la liste des nouveaux etudiants
			// afin de leur rajouter leurs notes
			studentToAddList.add(s);

		}

		// mises a jour des donnees etudiants
		try {
			updateStudentData(studentMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}

		// ajout des notes a tous les nouveaux etudiants
		try {
			addDefaultMarksForStudents(studentList);
		} catch (DataManagerException e) {
			throw new DataManagerException(
					"error adding default marks to new students.", e);
		}
	}

	private void addDefaultMarksForStudents(List<Student> studentList)
			throws DataManagerException {
		Map<Integer, Mark> markMap = getMarks();
		PreparedStatement preparedStatement = null;

		String request = "insert into " + TABLE_HAS_MARK + " "
				+ "values ( ?, ?, 0 );";

		try {
			preparedStatement = connectAndPrepare(request);
		} catch (SQLException e) {
			throw new DataManagerException("error preparing multi request.", e);
		}

		List<StudentMark> studentMarkList = getStudentMarks();
		for (Student s : studentList) {

			for (int key : markMap.keySet()) {
				try {
					preparedStatement.setInt(1, s.getId());
					preparedStatement.setInt(2, key);
					preparedStatement.executeUpdate();
					studentMarkList
							.add(new StudentMark(s, markMap.get(key), 0f));
				} catch (SQLException e) {
					throw new DataManagerException(
							"error performing prepared statement.", e);
				}
			}
		}

		try {
			updateStudentMarksData(studentMarkListTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
	}

	public void removeStudent(Student s) throws DataManagerException {

		/*
		 * partie effacement de l'etudiant
		 */

		// effacement de la base de donnees
		String request = "DELETE FROM " + TABLE_STUDENT + " WHERE "
				+ COLUMN_ID_FROM_TABLE_STUDENT + "=" + s.getId() + ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("error deleting student " + s
					+ " from database.", e);
		}

		// effacement de la map locale
		Map<Integer, Student> studentMap = getStudents();
		studentMap.remove(s.getId());

		// effacement de la liste locale
		List<Student> studentList = getStudentList();
		studentList.remove(s);

		// mise a jour des donnees
		// (a cause des acces multiples)
		try {
			updateStudentData(studentMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data for students.",
					e);
		}

		/*
		 * partie effacement de ses notes
		 */
		try {
			removeAllStudentMarksForStudent(s.getId());
		} catch (DataManagerException e) {
			throw new DataManagerException(
					"error deleting all marks for student " + s, e);
		}
	}

	/**
	 * @param studentId
	 * @throws SQLException
	 */
	private void removeAllStudentMarksForStudent(int studentId)
			throws DataManagerException {

		// effacement de la base de donnees
		String request = "delete from " + TABLE_HAS_MARK + " " + "where "
				+ COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + " = " + studentId
				+ ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException(
					"could not erase student's marks in database.", e);
		}

		// effacement de la liste des notes locale
		List<StudentMark> studentMarkList = getStudentMarks();

		for (int i = 0; i < studentMarkList.size(); i++) {
			if (studentMarkList.get(i).getStudent().getId() == studentId) {
				studentMarkList.remove(i);
				i--; // necessaire pour bien parcourir la liste
			}
		}

		// mise a jour des donnees
		// (a cause des acces multiples)
		try {
			updateStudentMarksData(studentMarkListTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
	}

	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#addCourse(java.lang.String, float)
	 */
	public void addCourse(String title, float coeff)
			throws DataManagerException {
		int key = 0;

		// on cree une cle pour la nouvelle matiere
		try {
			key = createPrimaryKey(TABLE_COURSE, COLUMN_ID_FROM_TABLE_COURSE);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error creating primary key for course " + title, e);
		}

		String request = "INSERT INTO " + TABLE_COURSE + " ("
				+ COLUMN_ID_FROM_TABLE_COURSE + ", "
				+ COLUMN_TITLE_FROM_TABLE_COURSE + ", "
				+ COLUMN_COEFF_FROM_TABLE_COURSE + ") VALUES (" + key + ", '"
				+ title + "', '" + coeff + "');";

		// on l'insere dans la table
		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("error inserting new course "
					+ title + " into database.", e);
		}

		Course c = new Course(key, title, coeff);

		// ajout dans la map locale
		/* Map <Integer, Course> courseMap = getCourses(); */
		courseMap.put(key, c);

		/* List <Course> courseList = getCoursesList(); */
		courseList.add(c);
		Collections.sort(courseList, courseComparator);

		// mise a jour avec le timestamp
		try {
			updateCourseData(courseMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data for courses.",
					e);
		}
	}

	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#addCourses(java.util.List)
	 */
	public void addCourses(List<Pair<String, Float>> coursesToAddList)
			throws DataManagerException {

		PreparedStatement preparedStatement = null;
		int key = 0;

		String request = "INSERT INTO " + TABLE_COURSE + " ("
				+ COLUMN_ID_FROM_TABLE_COURSE + ", "
				+ COLUMN_TITLE_FROM_TABLE_COURSE + ", "
				+ COLUMN_COEFF_FROM_TABLE_COURSE + ") VALUES (?, ?, ?);";

		/* Map <Integer, Course> courseMap = getCourses(); */

		try {
			preparedStatement = connectAndPrepare(request);
		} catch (SQLException e) {
			throw new DataManagerException("error preparing multi request.", e);
		}

		for (Pair<String, Float> p : coursesToAddList) {
			try {
				key = createPrimaryKey(TABLE_COURSE,
						COLUMN_ID_FROM_TABLE_COURSE);
			} catch (SQLException e) {
				throw new DataManagerException(
						"error creating primary key for course " + p.getFirst(),
						e);
			}

			try {
				preparedStatement.setInt(1, key);
				preparedStatement.setString(2, p.getFirst());
				preparedStatement.setFloat(3, p.getSecond());
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				throw new DataManagerException("error performing request.", e);
			}

			Course c = new Course(key, p.getFirst(), p.getSecond());

			courseMap.put(c.getId(), c);
			courseList.add(c);
			Collections.sort(courseList, courseComparator);
		}

		try {
			updateCourseData(courseMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
	}

	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#removeCourse(fr.umlv.symphonie.data.Course)
	 */
	public void removeCourse(Course c) throws DataManagerException {

		// on efface d'abord les tests associes a cette matiere
		Map<Integer, Mark> markMap = getMarksByCourse(c);
		System.out.println(markMap.size() + " tests pour la matiere " + c);

		for (Mark m : markMap.values()) {
			removeMark(m);
		}

    // on efface ensuite de la base de donnee
    // toutes ses formules
    removeAllFormulasForCourse(c);
    
    
    
		// on la retire ensuite de la map locale
		/* Map <Integer, Course> courseMap = getCourses(); */
		courseMap.remove(c.getId());
		courseList.remove(c);

		// on l'efface de la base
		String request = "DELETE FROM " + TABLE_COURSE + " WHERE "
				+ COLUMN_ID_FROM_TABLE_COURSE + "=" + c.getId() + ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("error deleting course " + c
					+ " from database.", e);
		}

		// mise a jour des donnees
		try {
			updateCourseData(courseMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
	}

	private void removeAllFormulasForCourse(Course c) throws DataManagerException{
    
    String request = "delete from " + TABLE_TEACHER_FORMULA + " " +
                     "where " + COLUMN_ID_COURSE_FROM_TABLE_TEACHER_FORMULA + " = " + c.getId() +" " +
                     ";";
    
    // delete from database
    try{
      connectAndUpdate(request);
    }catch(SQLException e){
      throw new DataManagerException ("error deleting formulas for course " + c, e);
    }
    
    // delete from local map
    teacherFormulaMap.remove(c.getId());
    
    // update data
    try{
      updateJuryFormulaData(juryFormulaListTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException("error updating data for teacher formulas.", e);
    }
    
  }

  /* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#addTitle(java.lang.String)
	 */
	public int addTitle(String desc) throws DataManagerException {
		int key = 0;

		try {
			key = createPrimaryKey(TABLE_TITLE, COLUMN_ID_FROM_TABLE_TITLE);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error creating new primary key for title " + desc, e);
		}

		String request = "INSERT INTO " + TABLE_TITLE + " ("
				+ COLUMN_ID_FROM_TABLE_TITLE + ", "
				+ COLUMN_DESC_FROM_TABLE_TITLE + ") VALUES (" + key + ", '"
				+ desc + "');";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("error inserting new title " + desc,
					e);
		}

		return key;
	}

	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#addMark(java.lang.String, float, fr.umlv.symphonie.data.Course)
	 */
	public void addMark(String desc, float coeff, Course c)
			throws DataManagerException {
		int titleKey = -1;

		try {
			titleKey = getKeyForTitle(desc);
		} catch (SQLException e) {
			throw new DataManagerException("error getting key for title "
					+ desc, e);
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
			throw new DataManagerException(
					"error creating new primary key for mark " + desc
							+ " related to " + c, e);
		}

		String request = "insert into " + TABLE_TEST + " " + "values ("
				+ markKey + ", " + coeff + ", " + c.getId() + ", " + titleKey
				+ ");";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("error inserting new test " + desc
					+ " related to " + c, e);
		}

		Mark m = new Mark(markKey, desc, coeff, c);

		/*Map<Integer, Mark> markMap = getMarks();*/
		markMap.put(m.getId(), m);

		try {
			updateMarkData(markMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}

		try {
			setDefaultMarkToAllStudents(markKey);
		} catch (DataManagerException e) {
			throw new DataManagerException(
					"error initializing marks for new test " + desc
							+ "related to " + c, e);
		}

	}

	/**
	 * @param markKey
	 */
	private void setDefaultMarkToAllStudents(int markKey)
			throws DataManagerException {

		PreparedStatement preparedStatement = null;

		String request = "insert into " + TABLE_HAS_MARK + " values (?, "
				+ markKey + ", 0);";

		try {
			preparedStatement = connectAndPrepare(request);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error preparing statement for inserting default marks.", e);
		}

		Map<Integer, Student> studentMap = getStudents();
		List<StudentMark> studentMarkList = getStudentMarks();
		for (Student s : studentMap.values()) {
			try {
				preparedStatement.setInt(1, s.getId());
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				throw new DataManagerException(
						"error inserting default mark for student " + s);
			}
			studentMarkList.add(new StudentMark(s, markMap.get(markKey), 0f));
		}

		try {
			updateStudentMarksData(studentMarkListTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
	}

	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#removeMark(fr.umlv.symphonie.data.Mark)
	 */
	public void removeMark(Mark m) throws DataManagerException {

		// effacement de la base
		String request = "delete from " + TABLE_TEST + " " + "where "
				+ COLUMN_ID_FROM_TABLE_TEST + " = " + m.getId() + ";";

		try {
			connectAndUpdate(request);
			removeAllStudentMarksForMark(m.getId());
		} catch (SQLException e) {
			throw new DataManagerException(
					"error deleting data from database.", e);
		}

		// effacement de la map locale
		Map<Integer, Mark> markMap = getMarks();
		markMap.remove(m.getId());

		// mise a jour des donnees
		try {
			updateMarkData(markMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}

		// suppression de toutes les notes
		try {
			removeAllStudentMarksForMark(m.getId());
		} catch (DataManagerException e) {
			throw new DataManagerException("error deleting all marks for test "
					+ m + " referring to course " + m.getCourse(), e);
		}
	}


	/**
	 * @param markId
	 * @throws DataManagerException
	 */
	public void removeAllStudentMarksForMark(int markId)
			throws DataManagerException {

		// on les retire de la BDD
		String request = "delete from " + TABLE_HAS_MARK + " " + "where "
				+ COLUMN_ID_TEST_FROM_TABLE_HAS_MARK + " = " + markId + ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error deleting references into database.", e);
		}

		// on les retire des donnees en local
		List<StudentMark> studentMarkList = getStudentMarks();

		for (int i = 0; i < studentMarkList.size(); i++) {
			StudentMark sm = studentMarkList.get(i);

			if (sm.getMark().getId() == markId) {
				studentMarkList.remove(i);
				i--;
			}
		}

		// mise a jour des donnees
		try {
			updateStudentMarksData(studentMarkListTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
	}

  
  /* (non-Javadoc)
   * @see fr.umlv.symphonie.data.DataManager#addFormula(fr.umlv.symphonie.data.formula.Formula, fr.umlv.symphonie.data.Course, int)
   */
  public void addTeacherFormula(String expression, String desc, Course course, int column) throws DataManagerException {
    
    
    int formulaKey = -1;

    // create new primary key for the new formula to add
    try {
      formulaKey = createPrimaryKey(TABLE_TEACHER_FORMULA, COLUMN_ID_FORMULA_FROM_TABLE_FORMULA);
    } catch (SQLException e) {
      throw new DataManagerException(
          "error creating new primary key for formula " + desc
              + " related to " + course, e);
    }
    
    
    // create new formula
    Formula f = null;
    try {
      f = SymphonieFormulaFactory.parseFormula(desc, expression, formulaKey, column);
    } catch (ParserException e1) {
      return;
    } catch (LexerException e1) {
      return;
    } catch (IOException e1) {
      return;
    }
    
    
    int titleKey = -1;
    
    // get key for the formula title
    try {
      titleKey = getKeyForTitle(desc);
    } catch (SQLException e) {
      throw new DataManagerException("error getting key for title "
          + desc, e);
    }

    // if there's no key, create one
    if (titleKey < 0) {
      try {
        titleKey = addTitle(desc);
      } catch (DataManagerException e) {
        throw new DataManagerException("error adding new Formula " + desc
            + " related to " + course, e);
      }
    }


    // insert into database
    String request = "insert into " + TABLE_TEACHER_FORMULA + " " +
                     "values ( " + formulaKey + ", " + titleKey + ", " + course.getId() + ", '" + expression + "', " + column + ")" +
                     ";";
    
    try {
      connectAndUpdate(request);
    }catch (SQLException e){
      throw new DataManagerException ("error inserting new formula", e);
    }
    

    // add formula into local data
    List<Formula> list = teacherFormulaMap.get(course.getId());
    
    if (list == null){
      list = new ArrayList<Formula>();
      teacherFormulaMap.put(course.getId(), list);
    }
    
    list.add(f);
    
    // update data
    try{
      updateTeacherFormulaData(teacherFormulaMapTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException ("error updating data for teacher formulas.", e);
    }
  }
  
  
  public void removeTeacherFormula(Formula f, Course c) throws DataManagerException{
    String request = "remove from " + TABLE_TEACHER_FORMULA + " " +
                     "where " + COLUMN_ID_FORMULA_FROM_TABLE_FORMULA + " = " + f.getID() + " " +
                     ";";
    
    // delete from database
    try{
      connectAndUpdate(request);
    }catch(SQLException e){
      throw new DataManagerException("error deleting formula from database.", e);
    }
    
    
    // delete from local map
    List<Formula> list = teacherFormulaMap.get(c.getId());
    
    if (list == null)
      throw new DataManagerException("no such formula for course " + c);
    
    list.remove(f);
    
    try{
      updateTeacherFormulaData(teacherFormulaMapTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException("error updating data for teacher formulas.");
    }
  }
  
  
  /* (non-Javadoc)
   * @see fr.umlv.symphonie.data.DataManager#addJuryFormula(fr.umlv.symphonie.data.formula.Formula, int)
   */
  public void addJuryFormula(String expression, String desc, int column) throws DataManagerException {
    int formulaKey = -1;

    // create new primary key for the new formula to add
    try {
      formulaKey = createPrimaryKey(TABLE_JURY_FORMULA, COLUMN_ID_FORMULA_FROM_TABLE_FORMULA);
    } catch (SQLException e) {
      throw new DataManagerException(
          "error creating new primary key for formula " + desc
              , e);
    }
    
    
    // create new formula
    Formula f = null;
    try {
      f = SymphonieFormulaFactory.parseFormula(desc, expression, formulaKey, column);
    } catch (ParserException e1) {
      return;
    } catch (LexerException e1) {
      return;
    } catch (IOException e1) {
      return;
    }
    
    
    int titleKey = -1;
    
    // get key for the formula title
    try {
      titleKey = getKeyForTitle(desc);
    } catch (SQLException e) {
      throw new DataManagerException("error getting key for title "
          + desc, e);
    }

    // if there's no key, create one
    if (titleKey < 0) {
      try {
        titleKey = addTitle(desc);
      } catch (DataManagerException e) {
        throw new DataManagerException("error adding new Formula " + desc
            , e);
      }
    }


    // insert into database
    String request = "insert into " + TABLE_JURY_FORMULA + " " +
                     "values ( " + formulaKey + ", " + titleKey + ", '" + expression + "', " + column + ")" +
                     ";";
    
    try {
      connectAndUpdate(request);
    }catch (SQLException e){
      throw new DataManagerException ("error inserting new formula", e);
    }
    

    // add formula into local data
    juryFormulaList.add(f);
    
    // update data
    try{
      updateJuryFormulaData(juryFormulaListTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException ("error updating data for jury formulas.", e);
    }
    
  }
  
  public void removeJuryFormula(Formula f) throws DataManagerException{
    String request = "remove from " + TABLE_JURY_FORMULA + " " +
                     "where " + COLUMN_ID_FORMULA_FROM_TABLE_FORMULA + " = " + f.getID() + " " +
                     ";";
    
    // delete from database
    try{
      connectAndUpdate(request);
    }catch(SQLException e){
      throw new DataManagerException("error deleting formula from database.", e);
    }
    
    
    // delete from local map
    juryFormulaList.remove(f);
    
    try{
      updateJuryFormulaData(juryFormulaListTimeStamp + 1);
    }catch (DataManagerException e){
      throw new DataManagerException("error updating data for jury formulas.");
    }
  }
  
  
	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.data.DataManager#changeStudentMarkValue(float)
	 */
	public void changeStudentMarkValue(StudentMark studentMark, float newValue)
			throws DataManagerException {

		// System.out.println("new value to set : " + newValue);
		// System.out.println(studentMark.getMark() + " " +
		// studentMark.getCourse());

		String request = "update " + TABLE_HAS_MARK + " " + "set "
				+ COLUMN_MARK_FROM_TABLE_HAS_MARK + " = " + newValue + " "
				+ "where " + COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK + " = "
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

		try {
			updateStudentMarksData(studentMarkListTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}

	}
	
	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#changeMarkDescriptionAndCoeff(fr.umlv.symphonie.data.Mark, java.lang.String, float)
	 */
	public void changeMarkDescriptionAndCoeff(Mark m, String newDescription, float newCoeff) throws DataManagerException {
		int markKey = -1;

		// on cherche si l'intitule est deja dans la base
		try {
			markKey = getKeyForTitle(newDescription);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error resolving primary key for title " + newDescription,
					e);
		}

		// s'il ne l'est pas on le rajoute dans la table des intitules
		if (markKey < 0)
			markKey = addTitle(newDescription);

		// on met a jour la reference dans la table
		String request = "update " + TABLE_TEST + " " + "set "
				+ COLUMN_ID_TITLE_FROM_TABLE_TEST + " = " + markKey + " " 
				+ "AND " + COLUMN_COEFF_FROM_TABLE_TEST + " = " + newCoeff + " "
				+ "where " + COLUMN_ID_FROM_TABLE_TEST + " = " + m.getId()
				+ " " + ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error updating description for test " + m
							+ "related to " + m.getCourse(), e);
		}

		// on met a jour dans les donnees locales
		m.setDesc(newDescription);
		m.setCoeff(newCoeff);

		// on synchronise si besoin est
		try {
			updateMarkData(markMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}

		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.data.DataManager#changeMarkDescription(java.lang.String)
	 */
	public void changeMarkDescription(Mark mark, String newDescription)
			throws DataManagerException {
		int markKey = -1;

		// on cherche si l'intitule est deja dans la base
		try {
			markKey = getKeyForTitle(newDescription);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error resolving primary key for title " + newDescription,
					e);
		}

		// s'il ne l'est pas on le rajoute dans la table des intitules
		if (markKey < 0)
			markKey = addTitle(newDescription);

		// on met a jour la reference dans la table
		String request = "update " + TABLE_TEST + " " + "set "
				+ COLUMN_ID_TITLE_FROM_TABLE_TEST + " = " + markKey + " "
				+ "where " + COLUMN_ID_FROM_TABLE_TEST + " = " + mark.getId()
				+ " " + ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException(
					"error updating description for test " + mark
							+ "related to " + mark.getCourse(), e);
		}

		// on met a jour dans les donnees locales
		mark.setDesc(newDescription);

		// on synchronise si besoin est
		try {
			updateMarkData(markMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.data.DataManager#changeStudentName(fr.umlv.symphonie.data.Student,
	 *      java.lang.String)
	 */
	public void changeMarkCoeff(Mark mark, float newCoeff)
			throws DataManagerException {

		String request = "UPDATE " + TABLE_TEST + " " + "SET "
				+ COLUMN_COEFF_FROM_TABLE_TEST + " = '" + newCoeff + "' "
				+ "WHERE " + COLUMN_ID_FROM_TABLE_TEST + " = '" + mark.getId()
				+ "' " + ";";

		// System.out.println(request);

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("unable to change coefficient for "
					+ mark + " related to " + mark.getCourse(), e);
		}

		mark.setCoeff(newCoeff);

		try {
			updateMarkData(markMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#changeStudentNameAndLastNameAndComment(fr.umlv.symphonie.data.Student, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void changeStudentNameAndLastNameAndComment(Student s, String newName, String newLastName, String newComment) throws DataManagerException {
		s.setName(newName);
		s.setLastName(newLastName);
		s.setComment(newComment);

		String request = "update " + TABLE_STUDENT + " " + "set "
				+ COLUMN_COMMENT_FROM_TABLE_STUDENT + " = '" + newComment + "' " 
				+ "AND " + COLUMN_NAME_FROM_TABLE_STUDENT + " = '" + newName + "' " 
				+ "AND " + COLUMN_LAST_NAME_FROM_TABLE_STUDENT + " = '" + newLastName + "' " 
				+ "where " + COLUMN_ID_FROM_TABLE_STUDENT + " = "
				+ s.getId() + ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("unable to set comment for student "
					+ s, e);
		}

		try {
			updateStudentData(studentMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.data.DataManager#changeStudentName(fr.umlv.symphonie.data.Student,
	 *      java.lang.String)
	 */
	public void changeStudentComment(Student s, String newComment)
			throws DataManagerException {
		s.setComment(newComment);

		String request = "update " + TABLE_STUDENT + " " + "set "
				+ COLUMN_COMMENT_FROM_TABLE_STUDENT + " = '" + newComment
				+ "' " + "where " + COLUMN_ID_FROM_TABLE_STUDENT + " = "
				+ s.getId() + ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("unable to set comment for student "
					+ s, e);
		}

		try {
			updateStudentData(studentMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.data.DataManager#changeStudentName(fr.umlv.symphonie.data.Student,
	 *      java.lang.String)
	 */
	public void changeStudentName(Student s, String newName)
			throws DataManagerException {
		s.setName(newName);

		String request = "update " + TABLE_STUDENT + " " + "set "
				+ COLUMN_NAME_FROM_TABLE_STUDENT + " = '" + newName
				+ "' " + "where " + COLUMN_ID_FROM_TABLE_STUDENT + " = "
				+ s.getId() + ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("unable to set comment for student "
					+ s, e);
		}

		try {
			updateStudentData(studentMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.data.DataManager#changeStudentLastName(fr.umlv.symphonie.data.Student,
	 *      java.lang.String)
	 */
	public void changeStudentLastName(Student s, String newLastName)
			throws DataManagerException {
		s.setLastName(newLastName);

		String request = "update " + TABLE_STUDENT + " " + "set "
				+ COLUMN_LAST_NAME_FROM_TABLE_STUDENT + " = '" + newLastName
				+ "' " + "where " + COLUMN_ID_FROM_TABLE_STUDENT + " = "
				+ s.getId() + ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("unable to set comment for student "
					+ s, e);
		}

		try {
			updateStudentData(studentMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}

	}
	
	/* (non-Javadoc)
	 * @see fr.umlv.symphonie.data.DataManager#changeCourseTitleAndCoeff(fr.umlv.symphonie.data.Course, java.lang.String, float)
	 */
	public void changeCourseTitleAndCoeff(Course c, String newTitle, float newCoeff) throws DataManagerException {
		c.setTitle(newTitle);
		c.setCoeff(newCoeff);

		String request = "update " + TABLE_COURSE + " " + "set "
				+ COLUMN_TITLE_FROM_TABLE_COURSE + " = '" + newTitle + "' "
				+ "AND " + COLUMN_COEFF_FROM_TABLE_COURSE + " = " + newCoeff + " "
				+ "where " + COLUMN_ID_FROM_TABLE_COURSE + " = " + c.getId()
				+ ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("unable to set title for course "
					+ c, e);
		}

		try {
			updateCourseData(courseMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.data.DataManager#changeCourseTitle(fr.umlv.symphonie.data.Course,
	 *      java.lang.String)
	 */
	public void changeCourseTitle(Course c, String newTitle)
			throws DataManagerException {
		c.setTitle(newTitle);

		String request = "update " + TABLE_COURSE + " " + "set "
				+ COLUMN_TITLE_FROM_TABLE_COURSE + " = '" + newTitle + "' "
				+ "where " + COLUMN_ID_FROM_TABLE_COURSE + " = " + c.getId()
				+ ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("unable to set title for course "
					+ c, e);
		}

		try {
			updateCourseData(courseMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.data.DataManager#changeCourseCoeff(fr.umlv.symphonie.data.Course,
	 *      float)
	 */
	public void changeCourseCoeff(Course c, float newCoeff)
			throws DataManagerException {
		c.setCoeff(newCoeff);

		String request = "update " + TABLE_COURSE + " " + "set "
				+ COLUMN_COEFF_FROM_TABLE_COURSE + " = " + newCoeff + " "
				+ "where " + COLUMN_ID_FROM_TABLE_COURSE + " = " + c.getId()
				+ ";";

		try {
			connectAndUpdate(request);
		} catch (SQLException e) {
			throw new DataManagerException("unable to set title for course "
					+ c, e);
		}

		try {
			updateCourseData(courseMapTimeStamp + 1);
		} catch (DataManagerException e) {
			throw new DataManagerException("error updating data.", e);
		}

	}


}
