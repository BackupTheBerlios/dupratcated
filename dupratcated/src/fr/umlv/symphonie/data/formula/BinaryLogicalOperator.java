
package fr.umlv.symphonie.data.formula;

/**
 * <i>enum </i>whose constants represent two basic logical operations :<br>
 * <ol>
 * <li>And
 * <li>Or
 * </ol>
 * and whose operate method performs the operation an returns the
 * <code>Boolean</code> result.
 */
public enum BinaryLogicalOperator {

  AND {

    Boolean operate(BooleanFormula v1, BooleanFormula v2) {
      return new Boolean(v1.getValue().booleanValue()
          && v2.getValue().booleanValue());
    }

    public String toString() {
      return "&&";
    }
  },
  OR {

    Boolean operate(BooleanFormula v1, BooleanFormula v2) {
      return new Boolean(v1.getValue().booleanValue()
          || v2.getValue().booleanValue());
    }

    public String toString() {
      return "||";
    }
  };

  /**
   * Performs an operation between two <code>BooleanFormula</code> type
   * operands.
   * 
   * @param v1
   *          Left operand
   * @param v2
   *          Right operand
   * @return The resultin <code>Boolean</code>.
   */
  abstract Boolean operate(BooleanFormula v1, BooleanFormula v2);
}
