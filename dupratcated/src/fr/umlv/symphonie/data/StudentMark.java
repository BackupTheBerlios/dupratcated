
package fr.umlv.symphonie.data;

public class StudentMark {

  private float value;
  private Student student;
  private Mark mark;

  public StudentMark(Student student, Mark mark, float value) {
    this.value = value;
    this.student = student;
    this.mark = mark;
  }

  public float getValue() {
    return value;
  }

  
  /**
   * @param value The value to set.
   */
  public void setValue(float value) {
    this.value = value;
  }
  
  public float getCoeff() {
    return mark.getCoeff();
  }

  public Student getStudent() {
    return student;
  }

  public Mark getMark() {
    return mark;
  }
  
  public Course getCourse() {
    return mark.getCourse();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    
    return "" + value;
  }
  
  

  public boolean equals(Object obj) {
    if (!(obj instanceof StudentMark))
      return false;
    
    StudentMark sm = (StudentMark)obj;
    
    if (student.getId() != sm.student.getId())
      return false;
    
    if (mark.getId() != sm.mark.getId())
      return false;
    
    return true;
  }
  
  public void printData(){
    System.out.println(getStudent() + " " + getValue() + " " + getMark() + " " + getCoeff() + " " + getCourse() + " " + getCourse().getCoeff());
  }
}
