
package fr.umlv.symphonie.data;

/**
 * Class represents an existing student
 * 
 * @author susmab, laurent garcia
 */
public class Student {

  /** Student unique id number */
  private int id;

  /** Student's name */
  private String name;

  /** Student's last name */
  private String lastName;

  /** Student Jury's advice, can be null */
  private String comment;

  /**
   * Creates a new Student
   * 
   * @param id
   *          The student id
   * @param name
   *          The student name
   * @param lastName
   *          The student last name
   */
  public Student(int id, String name, String lastName) {
    this(id, name, lastName, null);
  }

  /**
   * Creates a new Student
   * 
   * @param id
   *          The student id
   * @param name
   *          The student name
   * @param lastName
   *          The student last name
   * @param comment
   *          The jury's advice, can be null
   */
  public Student(int id, String name, String lastName, String comment) {
    this.id = id;
    this.name = name;
    this.lastName = lastName;
    this.comment = comment;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getLastName() {
    return lastName;
  }

  public String getComment() {
    return comment;
  }

  /**
   * Updates comment for current student
   * 
   * @param comment
   *          The new jury's advice
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Updates name for current student
   * 
   * @param name
   *          The new student name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Updates last name for current student
   * 
   * @param name
   *          The new student last name
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Student)) return false;
    Student s = (Student) obj;
    return id == s.id;
  }

  /**
   * Updates current student data from the given one
   * 
   * @param s
   *          The student where to extract infos from
   */
  public void update(Student s) {
    name = s.getName();
    lastName = s.getLastName();
  }

  public String toString() {
    return lastName + " " + name;
  }
}
