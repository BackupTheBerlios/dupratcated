/*
 * This file is part of Symphonie
 * Created : 8 mars 2005 19:18:28
 */
package fr.umlv.symphonie.data.formula.function;

import java.util.List;

import fr.umlv.symphonie.data.formula.NumericFormula;


/**
 * @author npersin, spenasal
 */
public class MaxFunction implements FormulaFunction {

  public Number calculate(List<NumericFormula> params) {
    Number min = null;
    for (NumericFormula f : params) {
      if ((min == null) || (f.getValue().floatValue() > min.floatValue()))
        min = f.getValue();
    }
    return min;
  }

}
