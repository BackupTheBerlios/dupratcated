/*
 * This file is part of Symphonie
 * Created : 8 mars 2005 17:41:49
 */

package fr.umlv.symphonie.data.formula.function;

import java.util.List;

import fr.umlv.symphonie.data.formula.NumericFormula;

/**
 * Function that calculates the average value from values within a given list.
 */
public class AverageFunction implements FormulaFunction {

  public Number calculate(List<NumericFormula> params) {
    float av = 0;
    int times = 0;
    for (NumericFormula f : params) {
      av += f.getValue().floatValue();
      times++;
    }
    av /= times;
    av *= 100;
    return Math.round(av) / (float) 100;
  }

}
