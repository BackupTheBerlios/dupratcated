
package fr.umlv.symphonie.data;

/**
 * Class represents an existing mark for a given course
 * 
 * @author susmab, laurent garcia
 */
public class Mark {

  /** Mark unique id number */
  private int id;

  /** Mark description (or name) like <code>Oral</code>,<code>Project</code> */
  private String desc;

  /** Course this mark belongs to */
  private Course course;

  /** Weight of the mark in the general Course average mark */
  private float coeff;

  /**
   * Creates a new Mark
   * 
   * @param id
   *          The id number
   * @param desc
   *          The mark description
   * @param coeff
   *          The mark coeff
   * @param course
   *          The course this mark belongs to
   */
  public Mark(int id, String desc, float coeff, Course course) {
    this.id = id;
    this.desc = desc;
    this.coeff = coeff;
    this.course = course;
  }

  public int getId() {
    return id;
  }

  public String getDesc() {
    return desc;
  }

  public Course getCourse() {
    return course;
  }

  public float getCoeff() {
    return coeff;
  }

  public void setCoeff(float coeff) {
    this.coeff = coeff;
  }

  /**
   * Updates mark's title
   * 
   * @param desc
   *          The desc to set.
   */
  public void setDesc(String desc) {
    this.desc = desc;
  }

  /**
   * Updates current mark data from the given mark
   * 
   * @param m
   *          The mark where to extract data from
   */
  public void update(Mark m) {
    desc = m.getDesc();
    coeff = m.getCoeff();
    course = m.getCourse();
  }

  public String toString() {
    return desc;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Mark)) return false;
    Mark m = (Mark) obj;
    return (m.getId() == id);
  }
}
