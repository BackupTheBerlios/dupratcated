package fr.umlv.symphonie.data;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.sql.SQLException;

import fr.umlv.symphonie.util.Pair;


public interface DataManager
{    
	public Map<Integer, Student> getStudents()throws DataManagerException;
	public Map<Integer, Course> getCourses()throws DataManagerException;
	public Map<Integer, Mark> getMarks()throws DataManagerException;
  public List<StudentMark> getStudentMarks()throws DataManagerException;
	public Map<Integer, Mark> getMarksByCourse(Course c)throws DataManagerException;
	
  /* methodes servant a la vue etudiant */
	public Map<Integer, StudentMark> getMarksByStudentAndCourse(Student s, Course c)throws DataManagerException;
  public Map<Course, Map<Integer, StudentMark>> getAllMarksByStudent(Student s)throws DataManagerException;
  
  
  
  /* methodes servant a la vue professeur */
	public Pair<Map<Integer, Mark>,
              SortedMap<Student, Map<Integer, StudentMark>>> getAllMarksByCourse(Course c)throws DataManagerException;
	
  /* methodes servant a la vue jury */
	public Pair<Map<Integer, Course>, SortedMap<Student, Map<Course, Map<Integer, StudentMark>>>> getAllStudentsMarks()throws DataManagerException;
	

  /* methodes pour l'admin */
	public void addStudent(String name, String lastName) throws DataManagerException;
	public void addStudents(List<Pair<String, String>> namesList) throws DataManagerException;
	public void removeStudent(Student s) throws DataManagerException;
	
	public void addCourse(String title, float coeff) throws DataManagerException;
	public void addCourses(List<String> listTitle, List<Float> listCoeff) throws SQLException, DataManagerException;
	public void removeCourse(Course c) throws SQLException;

  public int addTitle(String desc) throws DataManagerException;
  
	public void addMark(String desc, float coeff, Course c) throws DataManagerException;
	public void removeMark(Mark m) throws SQLException;
  
  
  /* methodes d'edition */
  public void changeStudentComment(Student s, String newComment)throws DataManagerException;
  
  public void changeStudentMarkValue(StudentMark studentMark, float newValue) throws DataManagerException;
  
  public void changeMarkCoeff(Mark mark, float newCoeff) throws DataManagerException;
  public void changeMarkDescription(Mark mark, String newDescription) throws DataManagerException;
  
}