/*
 * This file is part of Symphonie
 * Created : 14 mars 2005 19:03:25
 */

package fr.umlv.symphonie.data.formula;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.HashMap;

import fr.umlv.symphonie.data.formula.function.AverageFunction;
import fr.umlv.symphonie.data.formula.function.FormulaFunction;
import fr.umlv.symphonie.data.formula.function.MaxFunction;
import fr.umlv.symphonie.data.formula.function.MinFunction;
import fr.umlv.symphonie.data.formula.lexer.Lexer;
import fr.umlv.symphonie.data.formula.lexer.LexerException;
import fr.umlv.symphonie.data.formula.node.Start;
import fr.umlv.symphonie.data.formula.parser.Parser;
import fr.umlv.symphonie.data.formula.parser.ParserException;

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

  /** Map for variable instances */
  private static final HashMap<String, Number> mappedValues = new HashMap<String, Number>();

  /** Map for functions */
  private static final HashMap<String, FormulaFunction> funcs = new HashMap<String, FormulaFunction>();

  /** Map for already created <code>Formula</code> */
  private static final HashMap<String, Formula> formulas = new HashMap<String, Formula>();

  // Add the standard funcs
  static {
    funcs.put("min", new MinFunction());
    funcs.put("max", new MaxFunction());
    funcs.put("average", new AverageFunction());
  }

  private static final FormulaAnalysis analyzer = new FormulaAnalysis(
      mappedValues, funcs);

  public static final Formula parseFormula(String name, String unparsedFormula,
      int id, int column) throws ParserException, LexerException, IOException {
    
    synchronized (analyzer) {
      String formulaKey = name + "," + id;
      Formula f = formulas.get(formulaKey);
      if (f == null) {
        // Parse formula
        Lexer lexer = new Lexer(new PushbackReader(new StringReader(
            unparsedFormula)));
        Parser parser = new Parser(lexer);
        Start formula = parser.parse();

        // Set up analyzer
        analyzer.setDescription(name);
        analyzer.setColumn(column);
        formula.apply(analyzer);

        // return the built formula
        f = (Formula) analyzer.getOut(formula);
        formulas.put(formulaKey, f);
      }
      
      return f;
    }
  }

  public void clearMappedValues() {
    mappedValues.clear();
  }

  public void putMappedValue(String key, Number value) {
    mappedValues.put(key, value);
  }
  
  public void addFunction(String name, FormulaFunction func) {
    funcs.put(name, func);
  }

}
