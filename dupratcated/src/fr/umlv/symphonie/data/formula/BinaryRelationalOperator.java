
package fr.umlv.symphonie.data.formula;

/**
 * <i>enum </i>whose constants represent the basic relational operations :<br>
 * <ol>
 * <li>Equality
 * <li>Unequality
 * <li>Less than
 * <li>Less than or equals
 * <li>Greater than
 * <li>Greater than or equals
 * </ol>
 * and whose operate method performs the operation an returns the
 * <code>Boolean</code> result.
 */
public enum BinaryRelationalOperator {

  EQUALS {

    Boolean operate(NumericFormula v1, NumericFormula v2) {
      Number nv1 = v1.getValue();
      Number nv2 = v2.getValue();
      if (NumericFormula.resultCanBeInteger(nv1, nv2)) {
        return new Boolean(nv1.intValue() == nv2.intValue());
      } else if (NumericFormula.resultCanBeDouble(nv1, nv2)) {
        return new Boolean(nv1.floatValue() == nv2.floatValue());
      } else {
        throw new IllegalStateException(invalidTypeMessage + "+ : ("
            + nv1.getClass().getName() + ", " + nv2.getClass().getName() + ')');
      }
    }
  },
  NOT_EQUALS {

    Boolean operate(NumericFormula v1, NumericFormula v2) {
      return new Boolean(!BinaryRelationalOperator.EQUALS.operate(v1, v2)
          .booleanValue());
    }
  },
  LESS_THAN {

    Boolean operate(NumericFormula v1, NumericFormula v2) {
      Number nv1 = v1.getValue();
      Number nv2 = v2.getValue();
      if (NumericFormula.resultCanBeInteger(nv1, nv2)) {
        return new Boolean(nv1.intValue() < nv2.intValue());
      } else if (NumericFormula.resultCanBeDouble(nv1, nv2)) {
        return new Boolean(nv1.floatValue() < nv2.floatValue());
      } else {
        throw new IllegalStateException(invalidTypeMessage + "* : ("
            + nv1.getClass().getName() + ", " + nv2.getClass().getName() + ')');
      }
    }
  },
  LESS_THAN_OR_EQUALS {

    Boolean operate(NumericFormula v1, NumericFormula v2) {
      boolean r = BinaryRelationalOperator.LESS_THAN.operate(v1, v2)
          .booleanValue()
          || BinaryRelationalOperator.EQUALS.operate(v1, v2).booleanValue();
      return new Boolean(r);
    }
  },
  GREATER_THAN {

    Boolean operate(NumericFormula v1, NumericFormula v2) {
      Number nv1 = v1.getValue();
      Number nv2 = v2.getValue();
      if (NumericFormula.resultCanBeInteger(nv1, nv2)) {
        return new Boolean(nv1.intValue() > nv2.intValue());
      } else if (NumericFormula.resultCanBeDouble(nv1, nv2)) {
        return new Boolean(nv1.floatValue() > nv2.floatValue());
      } else {
        throw new IllegalStateException(invalidTypeMessage + "* : ("
            + nv1.getClass().getName() + ", " + nv2.getClass().getName() + ')');
      }
    }
  },
  GREATER_THAN_OR_EQUALS {

    Boolean operate(NumericFormula v1, NumericFormula v2) {
      boolean r = BinaryRelationalOperator.GREATER_THAN.operate(v1, v2)
          .booleanValue()
          || BinaryRelationalOperator.EQUALS.operate(v1, v2).booleanValue();
      return new Boolean(r);
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
   * @return The resulting <code>Boolean</code>.
   */
  abstract Boolean operate(NumericFormula v1, NumericFormula v2);
}
