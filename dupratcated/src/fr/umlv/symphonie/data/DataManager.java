package fr.umlv.symphonie.data;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.sql.SQLException;


public interface DataManager
{    
	public List<Student> getStudents();
	public List<Course> getCourses();
	public Map<Integer, String> getTitles();
	public SortedMap<Mark, Float> getTitlesByCourse(Course c);
	
	public Map<String, StudentMark> getMarksByStudentAndCourse(Student s, Course c);
	public SortedMap<Student, Map<String, StudentMark>> getAllMarksByCourse(Course c);
	public Map<Course, Map<String, StudentMark>> getAllMarksByStudent(Student s);
	public Map<Student, Map<Course, Map<String, StudentMark>>> getAllStudentsMarks();
	
	public void addStudent(String name, String lastName) throws SQLException;
	public void addStudents(List<String> listName, List<String> listLastName) throws SQLException, DataManagerException;
	public void removeStudent(Student s) throws SQLException;
	public void removeStudents(List<Student> list) throws SQLException;
	
	public void addCourse(String title, float coeff) throws SQLException;
	public void addCourses(List<String> listTitle, List<Float> listCoeff) throws SQLException, DataManagerException;
	public void removeCourse(Course c) throws SQLException;
	public void removeCourses(List<Course> list) throws SQLException;
	
	public void addStudentMark(Student s, Course c, Mark t, float mark, float coeff) throws SQLException;
	public void addStudentMarks(List<Student> listS, List<Course> listC, List<Mark> listM, List<Float> listMark, List<Float> listCoeff) throws SQLException, DataManagerException;
	public void removeStudentMark (StudentMark sm) throws SQLException;
	public void removeStudentMarks(List<StudentMark> list) throws SQLException;
	
	public void addMark(String desc) throws SQLException;
	public void addMarks(List<String> list) throws SQLException;
	public void removeMark(Mark m) throws SQLException;
	public void removeMarks(List<Mark> list) throws SQLException;
}