
package fr.umlv.symphonie.data.formula;

/**
 * <i>enum </i>whose constants represent the basic arithmetic operations :<br>
 * <ol>
 * <li>Addition
 * <li>Substraction
 * <li>Multiplication
 * <li>Division
 * <li>Modulus
 * </ol>
 * and whose operate method performs the operation an returns the resulting
 * <code>Number</code>.
 */
public enum BinaryNumericOperator {

  ADDITION {

    Number operate(NumericFormula v1, NumericFormula v2) {
      Number nv1 = v1.getValue();
      Number nv2 = v2.getValue();
      if (NumericFormula.resultCanBeInteger(nv1, nv2)) {
        return new Integer(nv1.intValue() + nv2.intValue());
      } else if (NumericFormula.resultCanBeDouble(nv1, nv2)) {
        return new Float(nv1.floatValue() + nv2.floatValue());
      } else {
        throw new IllegalStateException(invalidTypeMessage + "+ : ("
            + nv1.getClass().getName() + ", " + nv2.getClass().getName() + ')');
      }
    }
  },
  SUBSTRACTION {

    Number operate(NumericFormula v1, NumericFormula v2) {
      Number nv1 = v1.getValue();
      Number nv2 = v2.getValue();
      if (NumericFormula.resultCanBeInteger(nv1, nv2)) {
        return new Integer(nv1.intValue() - nv2.intValue());
      } else if (NumericFormula.resultCanBeDouble(nv1, nv2)) {
        return new Float(nv1.floatValue() - nv2.floatValue());
      } else {
        throw new IllegalStateException(invalidTypeMessage + "- : ("
            + nv1.getClass().getName() + ", " + nv2.getClass().getName() + ')');
      }
    }
  },
  MULTIPLICATION {

    Number operate(NumericFormula v1, NumericFormula v2) {
      Number nv1 = v1.getValue();
      Number nv2 = v2.getValue();
      if (NumericFormula.resultCanBeInteger(nv1, nv2)) {
        return new Integer(nv1.intValue() * nv2.intValue());
      } else if (NumericFormula.resultCanBeDouble(nv1, nv2)) {
        return new Float(nv1.floatValue() * nv2.floatValue());
      } else {
        throw new IllegalStateException(invalidTypeMessage + "* : ("
            + nv1.getClass().getName() + ", " + nv2.getClass().getName() + ')');
      }
    }
  },
  DIVISION {

    Number operate(NumericFormula v1, NumericFormula v2) {
      Number nv1 = v1.getValue();
      Number nv2 = v2.getValue();

      if (nv2.floatValue() == 0.0)
        throw new IllegalStateException("Division by zero");

      if (NumericFormula.resultCanBeInteger(nv1, nv2)) {
        return new Integer(nv1.intValue() / nv2.intValue());
      } else if (NumericFormula.resultCanBeDouble(nv1, nv2)) {
        return new Float(nv1.floatValue() / nv2.floatValue());
      } else {
        throw new IllegalStateException(invalidTypeMessage + "/ ("
            + nv1.getClass().getName() + ", " + nv2.getClass().getName() + ')');
      }
    }
  },
  MODULUS {

    Number operate(NumericFormula v1, NumericFormula v2) {
      Number nv1 = v1.getValue();
      Number nv2 = v2.getValue();
      if (NumericFormula.resultCanBeInteger(nv1, nv2)) {
        return new Integer(nv1.intValue() % nv2.intValue());
      } else {
        throw new IllegalStateException(invalidTypeMessage + "% : ("
            + nv1.getClass().getName() + ", " + nv2.getClass().getName() + ')');
      }
    }
  };

  /**
   * Message used in thrown exceptions.
   */
  private static String invalidTypeMessage = "Invalid Formula types for operator ";

  /**
   * Performs an operation between two <code>NumericFormula</code> type
   * operands.
   * 
   * @param v1
   *          Left operand
   * @param v2
   *          Right operand
   * @return The operation result as a <code>Number</code>.
   */
  abstract Number operate(NumericFormula v1, NumericFormula v2);
}
