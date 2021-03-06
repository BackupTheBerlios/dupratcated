
package fr.umlv.symphonie.data.formula;

/**
 * The abstract class <code>NumericFormula</code> is the superclass of classes
 * whose value type is a subtype of <code>Number</code>:
 * <p>
 * Though instances of
 * <code>BigDecimal, BigInteger, Byte, Double, Float, Integer, Long,</code>
 * and <code>Short</code> can be used as return types for
 * <code>NumericFormula</code>s, binary operators are only defined for
 * <code>Float</code> and <code>Integer</code> instances.
 * </p>
 */
public abstract class NumericFormula implements Formula<Number> {

  /**
   * Returns true if both operands are <code>Integer</code> instances.
   * 
   * @param v1
   *          The first operand
   * @param v2
   *          The second operand
   * @return <code>(v1 instanceof Integer) && (v2 instanceof Integer)</code>
   */
  public static final boolean resultCanBeInteger(Number v1, Number v2) {
    return (v1 instanceof Integer) && (v2 instanceof Integer);
  }

  /**
   * Returns true if at least one of the operands is an instance of
   * <code>Double</code> and the other one of <code>Integer</code>.
   * 
   * @param v1
   *          The first operand
   * @param v2
   *          The second operand
   * @return true if at least one of the operands is an instance of
   *         <code>Double</code> and the other one of <code>Integer</code>
   *         or false otherwise.
   */
  public static final boolean resultCanBeDouble(Number v1, Number v2) {
    return NumericFormula.isValidType(v1) && NumericFormula.isValidType(v2);
  }

  /**
   * Returns true if parameter is of type <code>Integer</code> or
   * <code>Double</code>.
   * 
   * @param n
   *          The number to test
   * @return <code>(n instanceof Double) || (n instanceof Integer)</code>
   */
  private static final boolean isValidType(Number n) {
    return (n instanceof Float) || (n instanceof Integer);
  }

  public String getDescription() {
    return null;
  }

  /**
   * Returns -1, subclasses may override this method
   * 
   * @return -1
   */
  public int getColumn() {
    return -1;
  }

  /**
   * Always returns -1, you may override this method in subclasses or wrap it in
   * another formula instance
   * 
   * @return -1
   */
  public int getID() {
    return -1;
  }

  public boolean equals(Object obj) {
    if (obj instanceof NumericFormula) {
      NumericFormula nf = (NumericFormula) obj;
      return nf.getDescription().equals(getDescription())
          && nf.toString().equals(toString());
    }
    return false;
  }
}
