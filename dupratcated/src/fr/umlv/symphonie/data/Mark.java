
package fr.umlv.symphonie.data;

public class Mark {

  private int id;
  private String desc;
  private Course course;

  public Mark(int id, String desc, Course course) {
    this.id = id;
    this.desc = desc;
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
  
  
  /**
   * @param desc The desc to set.
   */
  public void setDesc(String desc) {
    this.desc = desc;
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
