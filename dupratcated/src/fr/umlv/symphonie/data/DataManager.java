package fr.umlv.symphonie.data;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.util.Pair;

/**
 * Interface which set the standard methods for interacting with
 * all kind of data implementation in Symphonie.
 * Some return types of some methods may sound complicated, but they are crucial
 * to the design of the application, which is elaborated in order to share the
 * same data-structure between all views.
 * @author susmab
 *
 */
public interface DataManager {
	/**
   * Used to get all students from database. 
	 * @return a <code>Map</code> of <code>Student</code>s, keyed by their id.
	 * @throws DataManagerException
	 */
	public Map<Integer, Student> getStudents() throws DataManagerException;

	/**
   * Used to get all students from database, in a <code>List</code>.
	 * @return a <code>List</code> of <code>Student</code>s.
	 * @throws DataManagerException
	 */
	public List<Student> getStudentList() throws DataManagerException;

	/**
   * Used to get all courses form database. 
	 * @return a <code>Map</code> of <code>Course</code> keyed by their id.
	 * @throws DataManagerException
	 */
	public Map<Integer, Course> getCourses() throws DataManagerException;

	/**
   * Used to get all courses from database, in a <code>List</code>.
	 * @return a <code>List</code> of <code>Course</code>s.
	 * @throws DataManagerException
	 */
	public List<Course> getCoursesList() throws DataManagerException;

	/**
   * Used to get all tests from database.
	 * @return a <code>Map</code> of <code>Mark</code>s keyed by their id.
	 * @throws DataManagerException
	 */
	public Map<Integer, Mark> getMarks() throws DataManagerException;

	/**
   * Used to get all marks from database.
	 * @return a <code>List</code> of <code>StudentMark</code>s.
	 * @throws DataManagerException
	 */
	public List<StudentMark> getStudentMarks() throws DataManagerException;

  /**
   * Used to get all formulas from the database related to a given course.
   * @param c The <code>Course</code> to get formulas.
   * @return a <code>List</code> of <code>Formula</code>s.
   * @throws DataManagerException
   */
  public List<Formula> getFormulasByCourse(Course c) throws DataManagerException;
  
  /**
   * Used to get all formulas from database associated to the teacher's view.
   * @return a <code>List</code> of <code>Formula</code>s.
   * @throws DataManagerException
   */
  public List<Formula> getJuryFormulas() throws DataManagerException;
  
	/**
   * Used to get all tests related to a given course.
   * (the <code>Mark</code> class represents a test).
	 * @param c The <code>Course</code> of which the tests are requested.
	 * @return a <code>Map</code> of <code>Mark</code>, keyed by their id.
	 * @throws DataManagerException
	 */
	public Map<Integer, Mark> getMarksByCourse(Course c)
			throws DataManagerException;

	/**
	 * For the student view.
   * Used to get all marks for a given course and an given student.
   * (the <code>StudentMark</code> class represents a mark).
	 * @param s the <code>Student</code> for which the marks are requested.
	 * @param c the <code>Course</code> for which the student's marks are requested.
	 * @return a <code>Map</code> of <code>StudentMark</code>, keyed by the tests' id they are associated to.
	 * @throws DataManagerException
	 */
	public Map<Integer, StudentMark> getMarksByStudentAndCourse(Student s,
			Course c) throws DataManagerException;

	/**
	 * For the student view.
   * Used to get all marks for a given student.
   * (the <code>StudentMark</code> class represents a mark).
	 * @param s the <code>Student</code> for which the marks are requested.
	 * @return a <code>Map</code> keyed with all courses, and for each one is associated a <code>Map</code>
   * of <code>StudentMark</code>, keyed with tests' ids.
	 * @throws DataManagerException
	 */
	public Map<Course, Map<Integer, StudentMark>> getAllMarksByStudent(Student s)
			throws DataManagerException;

	/**
	 * For the teacher view.
   * Used to get all marks for all students, for a given course.
   * The result is a pair of data, in order to keep a <code>Map</code> of all tests for the course,
   * which are all related to the marks. It is useful for the <code>TeacherModel</code>.
	 * @param c the <code>Course</code> of which the marks are requested.
	 * @return a <code>Pair</code>.
   * The first part is a <code>Map</code> of all tests for the course, keyed by their id.
   * The second part is a <code>Map</code> keyed with all students. For each student is associated
   * a <code>Map</code> of all their marks for the given course, keyed with the id of the test they are related to.
	 * @throws DataManagerException
	 */
	public Pair<Map<Integer, Mark>, SortedMap<Student, Map<Integer, StudentMark>>> getAllMarksByCourse(
			Course c) throws DataManagerException;
	
	/**
	 * For the jury view.
   * Used to get all marks for all students, in all courses available in the database.
   * The result is a pair of data, in order to keep a <code>Map</code> of all courses available.
   * It is useful for the <code>JuryModel</code>.
	 * @return a <code>Pair</code>.
   * The first part is a <code>Map</code> of all courses in the database, keyed with their id.
   * The second part is a <code>SortedMap</code> keyed with all students. For each student is associated
   * a <code>Map</code> of their marks, keyed by each course.
	 * @throws DataManagerException
	 */
	public Pair<Map<Integer, Course>, SortedMap<Student, Map<Course, Map<Integer, StudentMark>>>> getAllStudentsMarks()
			throws DataManagerException;

	
	/**
	 * For the admin
   * Adds a new student in the database, given a name and lastname. 
	 * @param name the name of the new student.
	 * @param lastName the last name of the new student.
   * @return the newly created <code>Student</code>.
	 * @throws DataManagerException
	 */
	public Student addStudent(String name, String lastName)
			throws DataManagerException;

	/**
	 * For the admin.
   * Adds new students in the database.
	 * @param namesList a list of <code>Pair<String, String></code>.
   * The first element is the name of a new student, the second one his last name.
	 * @throws DataManagerException
	 */
	public void addStudents(List<Pair<String, String>> namesList)
			throws DataManagerException;

	/**
	 * For the admin.
   * Removes a given student from the database. 
	 * @param s the <code>Student</code> to remove.
	 * @throws DataManagerException
	 */
	public void removeStudent(Student s) throws DataManagerException;

	/**
	 * For the admin.
   * Adds a new course in the database, given a title and a coefficient.
	 * @param title the title of the new course.
	 * @param coeff the coefficient of the new course.
   * @return the newly created <code>Course</code>.
	 * @throws DataManagerException
	 */
	public Course addCourse(String title, float coeff)
			throws DataManagerException;

	/**
	 * For the admin.
   * Adds new Courses in the database.
	 * @param courseList a <code>List</code> of <code>Pair<String, String></code>.
   * The first element of each pair is the title of a new course, the second one its coefficient.
	 * @throws DataManagerException
	 */
	public void addCourses(List<Pair<String, Float>> courseList)
			throws DataManagerException;

	/**
	 * For the admin.
   * Removes a given course from the database.
	 * @param c the <code>Course</code> to remove.
	 * @throws DataManagerException
	 */
	public void removeCourse(Course c) throws DataManagerException;

	/**
   * Adds a new title in the database.
	 * @param desc the new title to add.
	 * @throws DataManagerException
	 */
	public int addTitle(String desc) throws DataManagerException;

	/**
	 * Adds a new test in the database, given a title, coefficient and the course it is related to.
	 * @param desc the title of the new test to add.
	 * @param coeff the coefficient of the new test to add.
	 * @param c the <code>Course</code> the new test is related to.
	 * @throws DataManagerException
	 */
	public Mark addMark(String desc, float coeff, Course c)
			throws DataManagerException;

	/**
	 * Remove a given test from the database.
	 * @param m the test to remove.
	 * @throws DataManagerException
	 */
	public void removeMark(Mark m) throws DataManagerException;
	
  
  /**
   * Adds a formula in the database, given an expression, a title, a column index and
   * the <code>Course</code> it is going to be related to.
   * @param expression the expression of the formula to add.
   * @param desc the title of the firmula to add.
   * @param course the course the formula will be associated to.
   * @param column the requested column number the formula should be located in the teacher's view.
   */
  public void addTeacherFormula(String expression, String desc, Course course, int column)throws DataManagerException;
  
  /**
   * Removes a given formula from the database, given its related course.
   * @param f the <code>Formula</code> to remove.
   * @param c the <code>Course</code> the formula is related to.
   * @throws DataManagerException
   */
  public void removeTeacherFormula(Formula f, Course c) throws DataManagerException;
  
  /**
   * Adds a formula in the database, related to the jury's view.
   * @param expression the expression of the formula to add.
   * @param desc the title of the formula to add.
   * @param column the column index where the formula should be in the jury's view.
   * @throws DataManagerException
   */
  public void addJuryFormula(String expression, String desc, int column) throws DataManagerException;
    
  /**
   * Removes a given formual of the jury's view from the database.
   * @param f the <code>Formula</code> to remove.
   * @throws DataManagerException
   */
  public void removeJuryFormula(Formula f) throws DataManagerException;
  
	/**
	 * Changes a given student his name, last name and the comment he's related in the jury's view.
	 * @param s the <code>Student</code> who will be updated.
	 * @param newName the new name of the student.
	 * @param newLastName the new last name of the student.
	 * @param newComment the new comment of the student.
	 * @throws DataManagerException
	 */
	public void changeStudentNameAndLastNameAndComment(Student s, String newName, String newLastName, String newComment)
			throws DataManagerException;

  /**
   * Changes a given student's his name and last name.
   * @param s the <code>Student</code> who will be updated.
   * @param newName the new name of the student.
   * @param newLastName the new last name of the student.
   * @throws DataManagerException
   */
  public void changeStudentNameAndLastName(Student s, String newName, String newLastName) throws DataManagerException;
  
	/**
	 * Changes a given student his comment in the jury(s view.
	 * @param s the <code>Student</code> who will be updated.
	 * @param newComment the new comment for the student.
	 * @throws DataManagerException
	 */
	public void changeStudentComment(Student s, String newComment)
			throws DataManagerException;
	
	/**
	 * Changes a given student his name.
	 * @param s the <code>Student</code> who will be updated.
	 * @param newname the new name for the student.
	 * @throws DataManagerException
	 */
	public void changeStudentName(Student s, String newName)
			throws DataManagerException;
	
	/**
	 * Changes a given student his last name.
	 * @param s the <code>Student</code> who will be updated.
	 * @param newLastName
	 * @throws DataManagerException
	 */
	public void changeStudentLastName(Student s, String newLastName)
			throws DataManagerException;

	/**
	 * Changes a given mark its value.
	 * @param sm the <code>StudentMark</code> which will be updated.
	 * @param newValue the new value for the mark.
	 * @throws DataManagerException
	 */
	public void changeStudentMarkValue(StudentMark sm, float newValue)
			throws DataManagerException;
	
	/**
	 * Changes a given test its description and coefficient.
   * (the class <code>Mark</code> represents a test).
	 * @param m the <code>Mark</code> which will be updated.
	 * @param newDescription the new title for the test.
	 * @param newCoeff the new coefficient for the test.
	 * @throws DataManagerException
	 */
	public void changeMarkDescriptionAndCoeff(Mark m, String newDescription,  float newCoeff)
			throws DataManagerException;

	/**
	 * Changes a given test its coeff.
   * (the class <code>Mark</code> represents a test).
	 * @param m the <code>Mark</code> which will be updated.
	 * @param newCoeff the new coeff for the test.
	 * @throws DataManagerException
	 */
	public void changeMarkCoeff(Mark m, float newCoeff)
			throws DataManagerException;

	/**
	 * Changes a test's title.
   * (the class <code>Mark</code> represents a test).
	 * @param m the <code>Mark</code> which will be updated.
	 * @param newDescription the new title for the test.
	 * @throws DataManagerException
	 */
	public void changeMarkDescription(Mark m, String newDescription)
			throws DataManagerException;
	
	/**
	 * Changes a given course its title and coefficient.
	 * @param c the <code>Course</code> which will be updated.
	 * @param newTitle the new title for the course.
	 * @param newCoeff the new coeff for the course.
	 * @throws DataManagerException
	 */
	public void changeCourseTitleAndCoeff(Course c, String newTitle, float newCoeff)
			throws DataManagerException;

	/**
	 * Changes a given course its title.
	 * @param c the <code>Course</code> which will be updated.
	 * @param newTitle the new title for the course.
	 * @throws DataManagerException
	 */
	public void changeCourseTitle(Course c, String newTitle)
			throws DataManagerException;

	/**
	 * Changes a given course its coefficient.
	 * @param c the <code>Course</code> which will be updated.
	 * @param newCoeff the new coefficient for the course.
	 * @throws DataManagerException
	 */
	public void changeCourseCoeff(Course c, float newCoeff)
			throws DataManagerException;


}