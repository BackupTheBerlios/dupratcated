/*
 * This file is part of Symphonie
 * Created : 17 mars 2005 14:25:05
 */

package fr.umlv.symphonie.util.dataexport.hssf;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.data.formula.SymphonieFormulaFactory;
import fr.umlv.symphonie.data.formula.lexer.LexerException;
import fr.umlv.symphonie.data.formula.parser.ParserException;

/**
 * Class provides static methods that can be used to convert
 * <code>Formula</code> into excel compatible <code>String</code> formulas.
 * <br>
 * Note that currently, only functions that takes cell groups as parameters are
 * supported.
 */
public final class FormulaConverter {

  // ----------------------------------------------------------------------------
  // Static fields
  // ----------------------------------------------------------------------------

  /** Pattern for escaping the '$' character */
  private static final Pattern dollarEscaper = Pattern.compile("\\$");

  /** The replacement escaped value for '$' */
  private static final String escapedDollar = "\\\\\\$";

  // ----------------------------------------------------------------------------
  // Static methods
  // ----------------------------------------------------------------------------

  /**
   * Returns the given formula as an excel formula
   * 
   * @param f
   *          The formula to convert
   * @param varMap
   *          The map used to convert variable values into excel cell references
   *          ex :<br>
   *          For a cell named "my cell" (the key) the map has to provide a
   *          corresponding reference like "$A$1" (the value).
   * @return The converted formula
   */
  public static final String toExcelString(Formula f, Map<String, String> varMap) {

    // Clean string and replace cell separator
    String result = f.toString();
    result = result.replaceAll("\\$\\{", "");
    result = result.replaceAll("\\}", "");
    result = result.replaceAll(",", ";");

    // Replace all variables found
    for (Map.Entry<String, String> entry : varMap.entrySet())
      if (result.contains(entry.getKey()))
        result = result.replaceAll(entry.getKey(), dollarEscaper.matcher(
            entry.getValue()).replaceAll(escapedDollar));

    // Return the excel formula
    return result;
  }

  /**
   * Converts an excel formula into a <code>Formula</code>.<br>
   * The converted <code>Formula</code> are parsed using
   * <code>SymphonieFormulaFactory</code>.
   * 
   * @param excelString
   *          The excel formula
   * @param name
   *          The <code>Formula</code> name
   * @param id
   *          The <code>Formula</code> internal id
   * @param column
   *          The index of the column that the <code>Formule</code> applies to
   * @param varMap
   *          The map used to convert excel cell references into variable values
   *          ex :<br>
   *          For a cell with reference "$B$6" (the key) the map has to provide
   *          a corresponding name like "your cell" (the value).
   * @return a <code>Formula</code> object
   * @see SymphonieFormulaFactory
   * @see Formula
   * @throws AssertionError
   *           If there's a parsing exception. If this occurrs it means that the
   *           method is not working properly and there's nothing left to do but
   *           to report the bug.
   */
  public static final Formula toFormula(String excelString, String name,
      int id, int column, Map<String, String> varMap) {

    // Replace separators
    String result = excelString.replaceAll("\\(", "\\(\\$\\{");
    result = result.replaceAll("\\)", "\\}\\)");
    result = result.replaceAll(";", ",");

    // Replace all variables found
    for (Map.Entry<String, String> entry : varMap.entrySet()) {

      // Replace value in cell block
      if (result.contains(entry.getKey() + ",")
          || result.contains(entry.getKey() + "}"))
        result = result.replaceAll(dollarEscaper.matcher(entry.getKey())
            .replaceAll(escapedDollar), entry.getValue());
      // Replace individual variable
      else if (result.contains(entry.getKey()))
        result = result.replaceAll(dollarEscaper.matcher(entry.getKey())
            .replaceAll(escapedDollar), "\\$\\{" + entry.getValue() + "\\}");
    }

    // Reparse and return formula
    try {
      return SymphonieFormulaFactory.parseFormula(name, result, id, column);
    } catch (ParserException e) {
      throw new AssertionError(e);
    } catch (LexerException e) {
      throw new AssertionError(e);
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }
}
