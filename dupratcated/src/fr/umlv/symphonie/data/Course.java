
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
