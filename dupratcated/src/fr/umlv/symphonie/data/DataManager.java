package fr.umlv.symphonie.data;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.sql.SQLException;

import fr.umlv.symphonie.util.Pair;


public interface DataManager
{    
	public List<Student> getStudents();
	public List<Course> getCourses();
	/*public Map<Integer, String> getTitles();*/
	public Map<Integer, Mark> getTitlesByCourse(Course c);
	
  /* methodes servant a la vue etudiant */
	public Map<Integer, StudentMark> getMarksByStudentAndCourse(Student s, Course c);
  public Map<Course, Map<Integer, StudentMark>> getAllMarksByStudent(Student s);
  
  
  
  /* methodes servant a la vue professeur */
  // public SortedMap<Student, Map<Integer, StudentMark>> getMarksByStudentsAndCourse (List<Student> students, Course c);
	public Pair<Map<Integer, Mark>,
              SortedMap<Student, Map<Integer, StudentMark>>> getAllMarksByCourse(Course c);
	
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
	public Map<Student, Map<Course, Map<Integer, StudentMark>>> getAllStudentsMarks();
	
  
  
  
 
	public void addStudent(String name, String lastName) throws SQLException;
	public void addStudents(List<String> listName, List<String> listLastName) throws SQLException, DataManagerException;
	public void removeStudent(Student s) throws SQLException;
	public void removeStudents(List<Student> list) throws SQLException;
	
	public void addCourse(String title, float coeff) throws SQLException;
	public void addCourses(List<String> listTitle, List<Float> listCoeff) throws SQLException, DataManagerException;
	public void removeCourse(Course c) throws SQLException;
	public void removeCourses(List<Course> list) throws SQLException;
	
//	public void addStudentMark(Student s, Course c, Mark t, float mark, float coeff) throws SQLException;
//	public void addStudentMarks(List<Student> studentList, int markKey, float value ) throws SQLException, DataManagerException;
//	public void removeStudentMark (StudentMark sm) throws SQLException;
//	public void removeStudentMarks(List<StudentMark> list) throws SQLException;
	
  public int addTitle(String desc) throws SQLException;
//  public void addTitles(List<String> list) throws SQLException;
  
	public void addMark(String desc, float coeff, Course c) throws SQLException;
//	public void addMarks(List<String> list, Course c) throws SQLException;
	public void removeMark(Mark m) throws SQLException;
//	public void removeMarks(List<Mark> list) throws SQLException;
  
  
  public void changeStudentMarkValue(StudentMark studentMark, float newValue) throws SQLException;
  public void changeMarkDescription(Mark mark, String newDescription) throws SQLException;
  
}