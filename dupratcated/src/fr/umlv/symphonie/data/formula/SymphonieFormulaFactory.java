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

  /** Formula analyzer */
  private static final FormulaAnalysis analyzer = new FormulaAnalysis(
      mappedValues, funcs);

  /**
   * Parses a <code>Formula</code> and returns it. <br>
   * This class statically stocks already parsed formulas so that they don't
   * need to be reparsed each time a user asks for them. <br>
   * The formulas that use variable mapped values are created using the internal
   * map. We do not ensure the integrity of the data that you put in it. Users
   * are encouraged to put the values in the map each time you are to call the
   * <code>getValue()</code> method in a formula.
   * 
   * @param name
   *          The name of the formula
   * @param unparsedFormula
   *          The formula code as a <code>String</code>
   * @param id
   *          The formula id
   * @param column
   *          The index of the column that the formula will be applied on
   * @return The parsed formula object
   * @see #putMappedValue(String, Number)
   * @throws ParserException
   *           if formula syntax is not correct
   * @throws LexerException
   *           if formula is misformatted
   * @throws IOException
   *           this exception is never thrown, unless there's in i/o error while
   *           reading RAM.
   */
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
        analyzer.setID(id);
        formula.apply(analyzer);

        // return the built formula
        f = (Formula) analyzer.getOut(formula);
        formulas.put(formulaKey, f);
      }

      return f;
    }
  }

  /**
   * Parses a <code>BooleanFormula</code> and returns it. <br>
   * The formulas aren't stocked, calling two times this routine with the same
   * arguments with parse and return two different formula objects. <br>
   * The formulas that use variable mapped values are created using the internal
   * map. We do not ensure the integrity of the data that you put in it. Users
   * are encouraged to put the values in the map each time you are to call the
   * <code>getValue()</code> method in a formula.
   * 
   * @param unparsedFormula
   *          The formula string to parse
   * @return a <code>BooleanFormula</code>
   * @see #putMappedValue(String, Number)
   * @throws ParserException
   *           if formula syntax is not correct
   * @throws LexerException
   *           if formula is misformatted
   * @throws IOException
   *           this exception is never thrown, unless there's in i/o error while
   *           reading RAM.
   */
  public BooleanFormula parseBooleanFormula(String unparsedFormula)
      throws ParserException, LexerException, IOException {
    synchronized (analyzer) {
      // Parse formula
      Lexer lexer = new Lexer(new PushbackReader(new StringReader(
          unparsedFormula)));
      Parser parser = new Parser(lexer);
      Start formula = parser.parse();

      // Set up analyzer
      analyzer.setDescription("boolean");
      analyzer.setColumn(-1);
      analyzer.setID(-1);
      formula.apply(analyzer);

      // return the built formula
      return (BooleanFormula) analyzer.getOut(formula);
    }
  }

  /**
   * Clears the internal variable value map
   */
  public static void clearMappedValues() {
    mappedValues.clear();
  }

  /**
   * Puts a variable value in the internal value map
   * 
   * @param key
   *          The key
   * @param value
   *          The value
   */
  public static void putMappedValue(String key, Number value) {
    mappedValues.put(key, value);
  }

  /**
   * Adds a function to the internal list, functions added can be used in
   * formulas. <br>
   * There are three functions that can be used without adding them using this
   * routine: <br>
   * <li>average
   * <li>max
   * <li>min
   * 
   * @param name
   *          The function name
   * @param func
   *          The function object
   */
  public void addFunction(String name, FormulaFunction func) {
    funcs.put(name, func);
  }

}
