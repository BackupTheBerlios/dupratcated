
package fr.umlv.symphonie.data;

public class Mark {

  private int id;
  private String desc;
  private Course course;
  private float coeff;

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
   * @param desc The desc to set.
   */
  public void setDesc(String desc) {
    this.desc = desc;
  }
  
  
  public void update(Mark m){
    desc = m.getDesc();
    coeff = m.getCoeff();
    course = m.getCourse(); // pas sur que cette ligne serve
  }
  
  
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    
    return desc;
  }
  
  
  
  
  @Override
  public boolean equals(Object obj) {
    System.out.println("sa mere !");
    
    if (! (obj instanceof Mark))
      return false;
    
    Mark m = (Mark)obj;
    
    return (m.getId() == id);
  }
}
