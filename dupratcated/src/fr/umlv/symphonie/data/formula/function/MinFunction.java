/*
 * This file is part of Symphonie
 * Created : 8 mars 2005 17:45:24
 */

package fr.umlv.symphonie.data.formula.function;

import java.util.List;

import fr.umlv.symphonie.data.formula.NumericFormula;

/**
 * Function that calculates the minimum value from values within a given list.
 */
public class MinFunction implements FormulaFunction {

  public Number calculate(List<NumericFormula> params) {
    Number min = null;
    for (NumericFormula f : params) {
      if ((min == null) || (f.getValue().floatValue() < min.floatValue()))
        min = f.getValue();
    }
    return min;
  }

}
