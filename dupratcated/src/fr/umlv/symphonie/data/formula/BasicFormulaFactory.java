
package fr.umlv.symphonie.data.formula;

import java.util.Map;

/**
 * Factory class for vending standard <code>Formula</code> objects. <br>
 * It defines convenient static methods to create instances from <i>int </i>,
 * <i>double </i>and <i>boolean </i> values, and from <code>Formula</code>
 * instances and binary operators.
 */
public class BasicFormulaFactory {

  /**
   * Returns a formula representing <i>value </i>
   * 
   * @param value
   *          This formula integer value
   * @return a <code>NumericFormula</code> object
   */
  public static NumericFormula integerInstance(final int value) {
    return new NumericFormula() {

      private final Integer val = new Integer(value);

      public Integer getValue() {
        return val;
      }
    };
  }

  /**
   * Returns a formula representing <i>value </i>
   * 
   * @param value
   *          This formula double value
   * @return a <code>NumericFormula</code> object
   */
  public static NumericFormula doubleInstance(final float value) {
    return new NumericFormula() {

      private final Float val = new Float(value);

      public Float getValue() {
        return val;
      }
    };
  }

  /**
   * Returns a formula representing <i>value </i>
   * 
   * @param value
   *          This formula boolean value
   * @return a <code>BooleanFormula</code> object
   */
  public static BooleanFormula booleanInstance(final boolean value) {
    return new BooleanFormula() {

      private final Boolean val = new Boolean(value);

      public Boolean getValue() {
        return val;
      }
    };
  }

  /**
   * Returns a formula whose value depends on a <code>Map</code>.<br>
   * If the number mapped to key changes, the value of the <code>Formula</code>
   * returned changes as well.
   * 
   * @param key
   *          The key
   * @param values
   *          The map containing the values
   * @return null if there's no mapped value to the given key or the
   *         <code>Number</code> associated to the key.
   */
  public static NumericFormula mappedInstance(final String key,
      final Map<String, Number> values) {
    return new NumericFormula() {

      public Number getValue() {
        return values.get(key);
      }
    };
  }

  /**
   * Performs the operation <i>op </i>between <i>left </i>and <i>right </i> an
   * returns a <code>Formula</code> object representing it's resulting value.
   * 
   * @param op
   *          The operation to perform
   * @param left
   *          The left operand
   * @param right
   *          The right operand
   * @return a <code>NumericFormula</code> object
   */
  public static NumericFormula numericOperatorInstance(
      final BinaryNumericOperator op, final NumericFormula left,
      final NumericFormula right) {
    return new NumericFormula() {

      public Number getValue() {
        return op.operate(left, right);
      }
    };
  }

  /**
   * Performs the relational operation <i>op </i>between <i>left </i>and
   * <i>right </i> an returns a <code>Formula</code> object representing it's
   * resulting value.
   * 
   * @param op
   *          The operation to perform
   * @param left
   *          The left operand
   * @param right
   *          The right operand
   * @return a <code>BooleanFormula</code> object
   */
  public static BooleanFormula relationalOperatorInstance(
      final BinaryRelationalOperator op, final NumericFormula left,
      final NumericFormula right) {
    return new BooleanFormula() {

      public Boolean getValue() {
        return op.operate(left, right);
      }
    };
  }

  /**
   * Performs the logical operation <i>op </i>between <i>left </i>and <i>right
   * </i> an returns a <code>Formula</code> object representing it's resulting
   * value.
   * 
   * @param op
   *          The operation to perform
   * @param left
   *          The left operand
   * @param right
   *          The right operand
   * @return a <code>BooleanFormula</code> object
   */
  public static BooleanFormula logicalOperatorInstance(
      final BinaryLogicalOperator op, final BooleanFormula left,
      final BooleanFormula right) {
    return new BooleanFormula() {

      public Boolean getValue() {
        return op.operate(left, right);
      }
    };
  }

  /**
   * Numeric formula of value 0.
   */
  public static final NumericFormula ZERO = integerInstance(0);
}
