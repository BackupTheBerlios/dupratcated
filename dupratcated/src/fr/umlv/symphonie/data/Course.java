
package fr.umlv.symphonie.data;

public class Course {

  private int id;
  private String title;
  private float coeff;

  public Course(int id, String title, float coeff) {
    this.id = id;
    this.title = title;
    this.coeff = coeff;
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public float getCoeff() {
    return coeff;
  }
  
  /**
   * @param desc the title to set.
   */
  public void setTitle(String title) {
    this.title = title;
  }
  
  /**
   * @param coeff the coeff to set.
   */
  public void setCoeff(float coeff) {
    this.coeff = coeff;
  }

  public void update(Course c){
    title = c.getTitle();
    coeff = c.getCoeff();
  }  
  
  public boolean equals(Object obj) {
    if (!(obj instanceof Course))
      return false;
    
    Course c = (Course)obj;
    
    return id == c.id;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {

    return title;
  }
}
