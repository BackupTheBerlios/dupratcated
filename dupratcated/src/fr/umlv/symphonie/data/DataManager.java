package fr.umlv.symphonie.data;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.sql.SQLException;

import fr.umlv.symphonie.util.Pair;


public interface DataManager
{    
	public Map<Integer, Student> getStudents();
	public Map<Integer, Course> getCourses();
	public Map<Integer, Mark> getMarks();
  public List<StudentMark> getStudentMarks();
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
	public void addStudent(String name, String lastName) throws SQLException;
	public void addStudents(List<String> listName, List<String> listLastName) throws SQLException, DataManagerException;
	public void removeStudent(Student s) throws SQLException;
	
	public void addCourse(String title, float coeff) throws SQLException;
	public void addCourses(List<String> listTitle, List<Float> listCoeff) throws SQLException, DataManagerException;
	public void removeCourse(Course c) throws SQLException;

  public int addTitle(String desc) throws SQLException;
  
	public void addMark(String desc, float coeff, Course c) throws SQLException;
	public void removeMark(Mark m) throws SQLException;
  
  
  /* methodes d'edition */
  public void changeStudentMarkValue(StudentMark studentMark, float newValue) throws SQLException;
  
  public void changeMarkCoeff(Mark mark, float newCoeff) throws SQLException;
  public void changeMarkDescription(Mark mark, String newDescription) throws SQLException;
  
}