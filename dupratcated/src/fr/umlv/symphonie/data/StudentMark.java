
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
}
