
package fr.umlv.symphonie.data;

public class StudentMark {

  private float value;
  private float coeff;
  private Student student;
  private Mark mark;

  public StudentMark(Student student, Mark mark, float studentMark, float coeff) {
    this.value = studentMark;
    this.coeff = coeff;
    this.student = student;
    this.mark = mark;
  }

  public float getValue() {
    return value;
  }

  public float getCoeff() {
    return coeff;
  }

  public Student getStudent() {
    return student;
  }

  public Mark getMark() {
    return mark;
  }
  
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    
    return "" + value;
  }
}
