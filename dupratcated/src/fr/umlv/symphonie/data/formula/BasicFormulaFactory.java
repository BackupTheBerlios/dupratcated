
package fr.umlv.symphonie.data.formula;

import java.util.List;
import java.util.Map;

import fr.umlv.symphonie.data.formula.function.FormulaFunction;

/**
 * Factory class for vending standard <code>Formula</code> objects. <br>
 * It defines convenient static methods to create instances from <i>int </i>,
 * <i>double </i>and <i>boolean </i> values, and from <code>Formula</code>
 * instances and binary operators.
 */
public final class BasicFormulaFactory {

  /**
   * Default constructor
   */
  private BasicFormulaFactory() {
  }

  /**
   * Returns a formula representing <i>value </i>
   * 
   * @param value
   *          This formula integer value
   * @return a <code>NumericFormula</code> object
   */
  public static final NumericFormula integerInstance(final int value) {
    return new NumericFormula() {

      private final Integer val = new Integer(value);

      public Integer getValue() {
        return val;
      }

      public String toString() {
        return val.toString();
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
  public static final NumericFormula floatInstance(final float value) {
    return new NumericFormula() {

      private final Float val = new Float(value);

      public Float getValue() {
        return val;
      }

      public String toString() {
        return val.toString();
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
  public static final BooleanFormula booleanInstance(final boolean value) {
    return new BooleanFormula() {

      private final Boolean val = new Boolean(value);

      public Boolean getValue() {
        return val;
      }

      public String toString() {
        return val.toString();
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
  public static final NumericFormula mappedInstance(final String key,
      final Map<String, Number> values) {
    return new NumericFormula() {

      public Number getValue() {
        return values.get(key);
      }

      public String toString() {
        return "${" + key + "}";
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
  public static final NumericFormula numericOperatorInstance(
      final BinaryNumericOperator op, final NumericFormula left,
      final NumericFormula right) {
    return new NumericFormula() {

      public Number getValue() {
        return op.operate(left, right);
      }

      public String toString() {
        return left.toString() + op.toString() + right.toString();
      }
    };
  }

  /**
   * Returns a formula whose value is given by a function.
   * 
   * @see FormulaFunction
   * @param name
   *          The function name
   * @param f
   *          The function
   * @param params
   *          The parameters used by the function.
   * @return a <code>NumericFormula</code> object.
   */
  public static final NumericFormula functionInstance(final String name,
      final FormulaFunction f, final List<NumericFormula> params) {
    return new NumericFormula() {

      private String asString;

      public Number getValue() {
        return f.calculate(params);
      }

      public String toString() {
        if (asString == null) {
          StringBuilder b = new StringBuilder(name + "(${");
          String pS = null;
          int c = params.size() - 1;
          for (NumericFormula nf : params) {
            pS = nf.toString();
            if (pS.startsWith("$")) pS = pS.substring(2, pS.length() - 1);
            b.append(pS);
            if (c-- != 0) b.append(',');
          }
          b.append("})");
          asString = b.toString();
        }
        return asString;
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
  public static final BooleanFormula relationalOperatorInstance(
      final BinaryRelationalOperator op, final NumericFormula left,
      final NumericFormula right) {
    return new BooleanFormula() {

      public Boolean getValue() {
        return op.operate(left, right);
      }

      public String toString() {
        return left.toString() + op.toString() + right.toString();
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
  public static final BooleanFormula logicalOperatorInstance(
      final BinaryLogicalOperator op, final BooleanFormula left,
      final BooleanFormula right) {
    return new BooleanFormula() {

      public Boolean getValue() {
        return op.operate(left, right);
      }

      public String toString() {
        return left.toString() + op.toString() + right.toString();
      }
    };
  }

  /** Numeric formula of value 0. */
  public static final NumericFormula ZERO = integerInstance(0);

  /** Numeric formula of value 1. */
  public static final NumericFormula ONE = integerInstance(1);
}
