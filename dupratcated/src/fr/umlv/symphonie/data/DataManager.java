package fr.umlv.symphonie.data;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.util.Pair;

public interface DataManager {
	/**	 * 
	 * @return 
	 * @throws DataManagerException
	 */
	public Map<Integer, Student> getStudents() throws DataManagerException;

	/**
	 * @return
	 * @throws DataManagerException
	 */
	public List<Student> getStudentList() throws DataManagerException;

	/**
	 * @return
	 * @throws DataManagerException
	 */
	public Map<Integer, Course> getCourses() throws DataManagerException;

	/**
	 * @return
	 * @throws DataManagerException
	 */
	public List<Course> getCoursesList() throws DataManagerException;

	/**
	 * @return
	 * @throws DataManagerException
	 */
	public Map<Integer, Mark> getMarks() throws DataManagerException;

	/**
	 * @return
	 * @throws DataManagerException
	 */
	public List<StudentMark> getStudentMarks() throws DataManagerException;

  public List<Formula> getFormulasByCourse(Course c) throws DataManagerException;
  
  public List<Formula> getJuryFormulas() throws DataManagerException;
  
	/**
	 * @param c
	 * @return
	 * @throws DataManagerException
	 */
	public Map<Integer, Mark> getMarksByCourse(Course c)
			throws DataManagerException;

	/**
	 * for the student view
	 * @param s
	 * @param c
	 * @return
	 * @throws DataManagerException
	 */
	public Map<Integer, StudentMark> getMarksByStudentAndCourse(Student s,
			Course c) throws DataManagerException;

	/**
	 * for the student view
	 * @param s
	 * @return
	 * @throws DataManagerException
	 */
	public Map<Course, Map<Integer, StudentMark>> getAllMarksByStudent(Student s)
			throws DataManagerException;

	/**
	 * for the teacher view
	 * @param c
	 * @return
	 * @throws DataManagerException
	 */
	public Pair<Map<Integer, Mark>, SortedMap<Student, Map<Integer, StudentMark>>> getAllMarksByCourse(
			Course c) throws DataManagerException;
	
	/**
	 * for the jury view
	 * @return
	 * @throws DataManagerException
	 */
	public Pair<Map<Integer, Course>, SortedMap<Student, Map<Course, Map<Integer, StudentMark>>>> getAllStudentsMarks()
			throws DataManagerException;

	
	/**
	 * for the admin 
	 * @param name
	 * @param lastName
	 * @throws DataManagerException
	 */
	public Student addStudent(String name, String lastName)
			throws DataManagerException;

	/**
	 * for the admin 
	 * @param namesList
	 * @throws DataManagerException
	 */
	public void addStudents(List<Pair<String, String>> namesList)
			throws DataManagerException;

	/**
	 * for the admin 
	 * @param s
	 * @throws DataManagerException
	 */
	public void removeStudent(Student s) throws DataManagerException;

	/**
	 * for the admin 
	 * @param title
	 * @param coeff
	 * @throws DataManagerException
	 */
	public Course addCourse(String title, float coeff)
			throws DataManagerException;

	/**
	 * for the admin 
	 * @param courseList
	 * @throws DataManagerException
	 */
	public void addCourses(List<Pair<String, Float>> courseList)
			throws DataManagerException;

	/**
	 * for the admin 
	 * @param c
	 * @throws DataManagerException
	 */
	public void removeCourse(Course c) throws DataManagerException;

	/**
	 * for the admin 
	 * @param desc
	 * @throws DataManagerException
	 */
	public int addTitle(String desc) throws DataManagerException;

	/**
	 * for the admin 
	 * @param desc
	 * @param coeff
	 * @param c
	 * @throws DataManagerException
	 */
	public Mark addMark(String desc, float coeff, Course c)
			throws DataManagerException;

	/**
	 * for the admin 
	 * @param m
	 * @throws DataManagerException
	 */
	public void removeMark(Mark m) throws DataManagerException;
	
  
  /**
   * @param f
   * @param course
   * @param column
   */
  public void addTeacherFormula(String expression, String desc, Course course, int column)throws DataManagerException;
  
  public void removeTeacherFormula(Formula f, Course c) throws DataManagerException;
  
  /**
   * @param f
   * @param column
   * @throws DataManagerException
   */
  public void addJuryFormula(String expression, String desc, int column) throws DataManagerException;
    
  public void removeJuryFormula(Formula f) throws DataManagerException;
  
	/**
	 * to edit 
	 * @param s
	 * @param newName
	 * @param newLastName
	 * @param newComment
	 * @throws DataManagerException
	 */
	public void changeStudentNameAndLastNameAndComment(Student s, String newName, String newLastName, String newComment)
			throws DataManagerException;

  public void changeStudentNameAndLastName(Student s, String newName, String newLastName) throws DataManagerException;
  
	/**
	 * to edit 
	 * @param s
	 * @param newComment
	 * @throws DataManagerException
	 */
	public void changeStudentComment(Student s, String newComment)
			throws DataManagerException;
	
	/**
	 * to edit 
	 * @param s
	 * @param newname
	 * @throws DataManagerException
	 */
	public void changeStudentName(Student s, String newName)
			throws DataManagerException;
	
	/**
	 * to edit 
	 * @param s
	 * @param newLastName
	 * @throws DataManagerException
	 */
	public void changeStudentLastName(Student s, String newLastName)
			throws DataManagerException;

	/**
	 * to edit 
	 * @param sm
	 * @param newValue
	 * @throws DataManagerException
	 */
	public void changeStudentMarkValue(StudentMark sm, float newValue)
			throws DataManagerException;
	
	/**
	 * to edit 
	 * @param m
	 * @param newDescription
	 * @param newCoeff
	 * @throws DataManagerException
	 */
	public void changeMarkDescriptionAndCoeff(Mark m, String newDescription,  float newCoeff)
			throws DataManagerException;

	/**
	 * to edit 
	 * @param m
	 * @param newCoeff
	 * @throws DataManagerException
	 */
	public void changeMarkCoeff(Mark m, float newCoeff)
			throws DataManagerException;

	/**
	 * to edit 
	 * @param m
	 * @param newDescription
	 * @throws DataManagerException
	 */
	public void changeMarkDescription(Mark m, String newDescription)
			throws DataManagerException;
	
	/**
	 * to edit 
	 * @param c
	 * @param newTitle
	 * @param newCoeff
	 * @throws DataManagerException
	 */
	public void changeCourseTitleAndCoeff(Course c, String newTitle, float newCoeff)
			throws DataManagerException;

	/**
	 * to edit 
	 * @param c
	 * @param newTitle
	 * @throws DataManagerException
	 */
	public void changeCourseTitle(Course c, String newTitle)
			throws DataManagerException;

	/**
	 * to edit 
	 * @param c
	 * @param newCoeff
	 * @throws DataManagerException
	 */
	public void changeCourseCoeff(Course c, float newCoeff)
			throws DataManagerException;


}