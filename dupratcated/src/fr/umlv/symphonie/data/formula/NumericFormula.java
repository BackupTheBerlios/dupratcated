
package fr.umlv.symphonie.data.formula;

/**
 * The abstract class <code>NumericFormula</code> is the superclass of classes
 * whose value type is a subtype of <code>Number</code>:
 * <p>
 * Though instances of
 * <code>BigDecimal, BigInteger, Byte, Double, Float, Integer, Long,</code>
 * and <code>Short</code> can be used as return types for
 * <code>NumericFormula</code>s, binary operators are only defined for
 * <code>Double</code> and <code>Integer</code> instances.
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
  public static boolean resultCanBeInteger(Number v1, Number v2) {
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
  public static boolean resultCanBeDouble(Number v1, Number v2) {
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
  private static boolean isValidType(Number n) {
    return (n instanceof Double) || (n instanceof Integer);
  }
}
