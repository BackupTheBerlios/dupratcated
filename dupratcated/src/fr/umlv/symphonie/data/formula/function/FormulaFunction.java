/*
 * This file is part of Symphonie
 * Created : 8 mars 2005 17:39:37
 */

package fr.umlv.symphonie.data.formula.function;

import java.util.List;

import fr.umlv.symphonie.data.formula.NumericFormula;

/**
 * Interface defining a function that can be used in a NumericFormula
 */
public interface FormulaFunction {

  /**
   * Calculates the function value from the given parameters
   * 
   * @param params
   *          The formulas used to calculate the function value
   * @return The function value
   */
  public Number calculate(List<NumericFormula> params);
}
