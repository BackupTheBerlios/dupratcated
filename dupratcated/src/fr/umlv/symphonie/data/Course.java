
package fr.umlv.symphonie.data;

/**
 * Class represents an existing course
 * 
 * @author susmab, laurent garcia
 */
public class Course {

  /** Unique id */
  private int id;

  /**
   * Course title (or name) such as <code>Java</code> or
   * <code>Mathematics</code>
   */
  private String title;

  /** Course weight in the general average student mark */
  private float coeff;

  /**
   * Creates a new course
   * 
   * @param id
   *          Course unique id
   * @param title
   *          Course title
   * @param coeff
   *          Course coefficient
   */
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
   * Updates course title
   * 
   * @param title
   *          the title to set.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Updates course coeff
   * 
   * @param coeff
   *          the coeff to set.
   */
  public void setCoeff(float coeff) {
    this.coeff = coeff;
  }

  /**
   * Updates current course from the given one
   * 
   * @param c
   *          The course where to get the information from
   */
  public void update(Course c) {
    title = c.getTitle();
    coeff = c.getCoeff();
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Course)) return false;
    Course c = (Course) obj;
    return id == c.id;
  }

  public String toString() {
    return title;
  }
}
