/*
 * This file is part of Symphonie
 * Created : 14 mars 2005 19:03:25
 */

package fr.umlv.symphonie.data.formula;

/**
 * Factory class for creating Formulas. Instances returned by the factory will
 * be either <code>NumericFormula</code> s or <code>BooleanFormula</code>s.
 */
public final class SymphonieFormulaFactory {

  /**
   * Default constructor
   */
  private SymphonieFormulaFactory() {
  }

  // ----------------------------------------------------------------------------
  // Static members
  // ----------------------------------------------------------------------------

  public static final Formula parseFormula(String name, String unparsedFormula,
      int id) {
    return null;
  }
  
}
