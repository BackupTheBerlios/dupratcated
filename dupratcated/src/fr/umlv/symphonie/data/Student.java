
package fr.umlv.symphonie.data;

public class Student {

  private int id;
  private String name;
  private String lastName;
  private String comment;

  public Student(int id, String name, String lastName) {
    this.id = id;
    this.name = name;
    this.lastName = lastName;
    comment = null;
  }

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

  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Student))
      return false;
    
    Student s = (Student)obj;
    
    return id == s.id;
  }
  

  public void update(Student s){
    name = s.getName();
    lastName = s.getLastName();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return lastName + " " + name;
  }
}