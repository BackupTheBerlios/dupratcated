
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

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {

    return title;
  }
}
