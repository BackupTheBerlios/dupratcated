
package fr.umlv.symphonie.data.formula;

public abstract class BooleanFormula implements Formula<Boolean> {

  public String getDescription() {
    return null;
  }

  /**
   * Always returns -1 because the <code>BooleanFormula</code> may not be used
   * for column values.
   */
  public int getColumn() {
    return -1;
  }

  public boolean equals(Object obj) {
    if (obj instanceof BooleanFormula) {
      BooleanFormula bf = (BooleanFormula) obj;
      return bf.getDescription().equals(getDescription())
          && bf.toString().equals(toString());
    }
    return false;
  }
}
