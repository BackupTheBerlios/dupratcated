
package fr.umlv.symphonie.data.formula;

import java.util.LinkedList;
import java.util.Map;

import fr.umlv.symphonie.data.formula.analysis.AnalysisAdapter;
import fr.umlv.symphonie.data.formula.node.AAdditionAdditiveExpression;
import fr.umlv.symphonie.data.formula.node.ABooleanExpression;
import fr.umlv.symphonie.data.formula.node.ACellBlockExpression;
import fr.umlv.symphonie.data.formula.node.ACellEvaluatedExpression;
import fr.umlv.symphonie.data.formula.node.AComparativeLogicalAndExpression;
import fr.umlv.symphonie.data.formula.node.ADataBlockExpression;
import fr.umlv.symphonie.data.formula.node.ADivisionMultiplicativeExpression;
import fr.umlv.symphonie.data.formula.node.ADoubleLiteralExpression;
import fr.umlv.symphonie.data.formula.node.AEqualityComparativeExpression;
import fr.umlv.symphonie.data.formula.node.AEqualsEqualityExpression;
import fr.umlv.symphonie.data.formula.node.AEvaluatedNumericExpression;
import fr.umlv.symphonie.data.formula.node.AFunctionEvaluatedExpression;
import fr.umlv.symphonie.data.formula.node.AGreaterThanOrEqualsRelationalExpression;
import fr.umlv.symphonie.data.formula.node.AGreaterThanRelationalExpression;
import fr.umlv.symphonie.data.formula.node.AIdentifiers;
import fr.umlv.symphonie.data.formula.node.AIntegerLiteralExpression;
import fr.umlv.symphonie.data.formula.node.ALessThanOrEqualsRelationalExpression;
import fr.umlv.symphonie.data.formula.node.ALessThanRelationalExpression;
import fr.umlv.symphonie.data.formula.node.ALiteralComparativeExpression;
import fr.umlv.symphonie.data.formula.node.ALiteralNumericExpression;
import fr.umlv.symphonie.data.formula.node.ALogicalAndLogicalAndExpression;
import fr.umlv.symphonie.data.formula.node.ALogicalAndLogicalOrExpression;
import fr.umlv.symphonie.data.formula.node.ALogicalOrLogicalOrExpression;
import fr.umlv.symphonie.data.formula.node.AMinusUnaryExpression;
import fr.umlv.symphonie.data.formula.node.AModulusMultiplicativeExpression;
import fr.umlv.symphonie.data.formula.node.AMultiplicationMultiplicativeExpression;
import fr.umlv.symphonie.data.formula.node.AMultiplicativeAdditiveExpression;
import fr.umlv.symphonie.data.formula.node.ANotEqualsEqualityExpression;
import fr.umlv.symphonie.data.formula.node.ANumericExpression;
import fr.umlv.symphonie.data.formula.node.ANumericUnaryExpression;
import fr.umlv.symphonie.data.formula.node.AParenthethicComparativeExpression;
import fr.umlv.symphonie.data.formula.node.AParentheticNumericExpression;
import fr.umlv.symphonie.data.formula.node.ARelationalComparativeExpression;
import fr.umlv.symphonie.data.formula.node.ASeparatorSuffixedId;
import fr.umlv.symphonie.data.formula.node.ASubstractionAdditiveExpression;
import fr.umlv.symphonie.data.formula.node.AUnaryMultiplicativeExpression;
import fr.umlv.symphonie.data.formula.node.PSeparatorSuffixedId;
import fr.umlv.symphonie.data.formula.node.Start;
import fr.umlv.symphonie.data.formula.node.TBooleanLiteral;
import fr.umlv.symphonie.data.formula.node.TDoubleLiteral;
import fr.umlv.symphonie.data.formula.node.TIntegerLiteral;

public class FormulaAnalysis extends AnalysisAdapter {

  /**
   * The map used to create variable values
   */
  private Map<String, Number> mappedValues;

  /**
   * Creates a new formula analyser. <br>
   * 
   * @param mappedValues
   *          The map used to create variable values <br>
   *          Parameter can be null if your formula doesn't contain variables
   *          values, the map can be empty as the formulas aren't evaluated.
   *          <br>
   *          Anyway be sure to put the values in it when you are to call
   *          Formula#getValue(), if you don't do it you will probably get a
   *          <code>NullPointerException</code>.
   */
  public FormulaAnalysis(Map<String, Number> mappedValues) {
    this.mappedValues = mappedValues;
  }

  public Map<String, Number> getMappedValues() {
    return mappedValues;
  }

  /**
   * This method is provided for the user who wants to parse formulas that use
   * different maps for variable values with the same
   * <code>FormulaAnalysis</code> object. <br>
   * Note that formulas parsed so far won't use the new map, they will use the
   * map that was the current when they where created.
   * 
   * @param mappedValues
   *          The new map used to create variable values
   */
  public void setMappedValues(Map<String, Number> mappedValues) {
    this.mappedValues = mappedValues;
  }

  public void caseStart(Start node) {
    node.getPExpression().apply(this);
    setOut(node, getOut(node.getPExpression()));
  }

  public void caseANumericExpression(ANumericExpression node) {
    node.getAdditiveExpression().apply(this);
    setOut(node, getOut(node.getAdditiveExpression()));
  }

  public void caseABooleanExpression(ABooleanExpression node) {
    node.getLogicalOrExpression().apply(this);
    setOut(node, getOut(node.getLogicalOrExpression()));
  }

  public void caseAMultiplicativeAdditiveExpression(
      AMultiplicativeAdditiveExpression node) {
    node.getMultiplicativeExpression().apply(this);
    setOut(node, getOut(node.getMultiplicativeExpression()));
  }

  public void caseAAdditionAdditiveExpression(AAdditionAdditiveExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    NumericFormula f = BasicFormulaFactory.numericOperatorInstance(
        BinaryNumericOperator.ADDITION,
        (NumericFormula) getOut(node.getLeft()), (NumericFormula) getOut(node
            .getRight()));
    setOut(node, f);
  }

  public void caseASubstractionAdditiveExpression(
      ASubstractionAdditiveExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    NumericFormula f = BasicFormulaFactory.numericOperatorInstance(
        BinaryNumericOperator.SUBSTRACTION, (NumericFormula) getOut(node
            .getLeft()), (NumericFormula) getOut(node.getRight()));
    setOut(node, f);
  }

  public void caseAUnaryMultiplicativeExpression(
      AUnaryMultiplicativeExpression node) {
    node.getUnaryExpression().apply(this);
    setOut(node, getOut(node.getUnaryExpression()));
  }

  public void caseAMultiplicationMultiplicativeExpression(
      AMultiplicationMultiplicativeExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    NumericFormula f = BasicFormulaFactory.numericOperatorInstance(
        BinaryNumericOperator.MULTIPLICATION, (NumericFormula) getOut(node
            .getLeft()), (NumericFormula) getOut(node.getRight()));
    setOut(node, f);
  }

  public void caseADivisionMultiplicativeExpression(
      ADivisionMultiplicativeExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    NumericFormula f = BasicFormulaFactory.numericOperatorInstance(
        BinaryNumericOperator.DIVISION,
        (NumericFormula) getOut(node.getLeft()), (NumericFormula) getOut(node
            .getRight()));
    setOut(node, f);
  }

  public void caseAModulusMultiplicativeExpression(
      AModulusMultiplicativeExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    NumericFormula f = BasicFormulaFactory.numericOperatorInstance(
        BinaryNumericOperator.MODULUS, (NumericFormula) getOut(node.getLeft()),
        (NumericFormula) getOut(node.getRight()));
    setOut(node, f);
  }

  public void caseANumericUnaryExpression(ANumericUnaryExpression node) {
    node.getNumericExpression().apply(this);
    setOut(node, getOut(node.getNumericExpression()));
  }

  public void caseAMinusUnaryExpression(AMinusUnaryExpression node) {
    node.getNumericExpression().apply(this);
    NumericFormula f = BasicFormulaFactory.numericOperatorInstance(
        BinaryNumericOperator.SUBSTRACTION, BasicFormulaFactory.ZERO,
        (NumericFormula) getOut(node.getNumericExpression()));
    setOut(node, f);
  }

  public void caseAEvaluatedNumericExpression(AEvaluatedNumericExpression node) {
    node.getEvaluatedExpression().apply(this);
    setOut(node, getOut(node.getEvaluatedExpression()));
  }

  public void caseALiteralNumericExpression(ALiteralNumericExpression node) {
    node.getLiteralExpression().apply(this);
    setOut(node, getOut(node.getLiteralExpression()));
  }

  public void caseAParentheticNumericExpression(
      AParentheticNumericExpression node) {
    node.getAdditiveExpression().apply(this);
    setOut(node, getOut(node.getAdditiveExpression()));
  }

  public void caseAIntegerLiteralExpression(AIntegerLiteralExpression node) {
    node.getIntegerLiteral().apply(this);
    setOut(node, getOut(node.getIntegerLiteral()));
  }

  public void caseADoubleLiteralExpression(ADoubleLiteralExpression node) {
    node.getDoubleLiteral().apply(this);
    setOut(node, getOut(node.getDoubleLiteral()));
  }

  public void caseTIntegerLiteral(TIntegerLiteral node) {
    setOut(node, BasicFormulaFactory.integerInstance(Integer.parseInt(node
        .getText())));
  }

  public void caseTDoubleLiteral(TDoubleLiteral node) {
    setOut(node, BasicFormulaFactory.floatInstance(Float.parseFloat(node
        .getText())));
  }

  public void caseAFunctionEvaluatedExpression(AFunctionEvaluatedExpression node) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Fuck");
  }

  public void caseACellEvaluatedExpression(ACellEvaluatedExpression node) {
    setOut(node, BasicFormulaFactory.mappedInstance(node.getIdentifier().getText(), mappedValues));
  }

  public void caseADataBlockExpression(ADataBlockExpression node) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Fuck");
  }

  public void caseACellBlockExpression(ACellBlockExpression node) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Fuck");
  }

  public void caseALineColumnIds(AIdentifiers node) {
    // TODO Auto-generated method stub
    LinkedList<PSeparatorSuffixedId> l = node.getSeparatorSuffixedId();
    for (PSeparatorSuffixedId pssi : l)
      pssi.apply(this);
    node.getIdentifier().apply(this);
  }

  public void caseASeparatorSuffixedId(ASeparatorSuffixedId node) {
    // TODO Auto-generated method stub
    node.getIdentifier().apply(this);
  }

  public void caseALogicalAndLogicalOrExpression(
      ALogicalAndLogicalOrExpression node) {
    node.getLogicalAndExpression().apply(this);
    setOut(node, getOut(node.getLogicalAndExpression()));
  }

  public void caseALogicalOrLogicalOrExpression(
      ALogicalOrLogicalOrExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    BooleanFormula f = BasicFormulaFactory.logicalOperatorInstance(
        BinaryLogicalOperator.OR, (BooleanFormula) getOut(node.getLeft()),
        (BooleanFormula) getOut(node.getRight()));
    setOut(node, f);
  }

  public void caseAComparativeLogicalAndExpression(
      AComparativeLogicalAndExpression node) {
    node.getComparativeExpression().apply(this);
    setOut(node, getOut(node.getComparativeExpression()));
  }

  public void caseALogicalAndLogicalAndExpression(
      ALogicalAndLogicalAndExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    BooleanFormula f = BasicFormulaFactory.logicalOperatorInstance(
        BinaryLogicalOperator.AND, (BooleanFormula) getOut(node.getLeft()),
        (BooleanFormula) getOut(node.getRight()));
    setOut(node, f);
  }

  public void caseAEqualityComparativeExpression(
      AEqualityComparativeExpression node) {
    node.getEqualityExpression().apply(this);
    setOut(node, getOut(node.getEqualityExpression()));
  }

  public void caseARelationalComparativeExpression(
      ARelationalComparativeExpression node) {
    node.getRelationalExpression().apply(this);
    setOut(node, getOut(node.getRelationalExpression()));
  }

  public void caseALiteralComparativeExpression(
      ALiteralComparativeExpression node) {
    node.getBooleanLiteral().apply(this);
    setOut(node, getOut(node.getBooleanLiteral()));
  }

  public void caseTBooleanLiteral(TBooleanLiteral node) {
    setOut(node, BasicFormulaFactory.booleanInstance(Boolean.parseBoolean(node
        .getText())));
  }

  public void caseAParenthethicComparativeExpression(
      AParenthethicComparativeExpression node) {
    node.getLogicalOrExpression().apply(this);
    setOut(node, getOut(node.getLogicalOrExpression()));
  }

  public void caseAEqualsEqualityExpression(AEqualsEqualityExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    BooleanFormula f = BasicFormulaFactory.relationalOperatorInstance(
        BinaryRelationalOperator.EQUALS,
        (NumericFormula) getOut(node.getLeft()), (NumericFormula) getOut(node
            .getRight()));
    setOut(node, f);
  }

  public void caseANotEqualsEqualityExpression(ANotEqualsEqualityExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    BooleanFormula f = BasicFormulaFactory.relationalOperatorInstance(
        BinaryRelationalOperator.NOT_EQUALS, (NumericFormula) getOut(node
            .getLeft()), (NumericFormula) getOut(node.getRight()));
    setOut(node, f);
  }

  public void caseALessThanRelationalExpression(
      ALessThanRelationalExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    BooleanFormula f = BasicFormulaFactory.relationalOperatorInstance(
        BinaryRelationalOperator.LESS_THAN, (NumericFormula) getOut(node
            .getLeft()), (NumericFormula) getOut(node.getRight()));
    setOut(node, f);
  }

  public void caseALessThanOrEqualsRelationalExpression(
      ALessThanOrEqualsRelationalExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    BooleanFormula f = BasicFormulaFactory.relationalOperatorInstance(
        BinaryRelationalOperator.LESS_THAN_OR_EQUALS,
        (NumericFormula) getOut(node.getLeft()), (NumericFormula) getOut(node
            .getRight()));
    setOut(node, f);
  }

  public void caseAGreaterThanRelationalExpression(
      AGreaterThanRelationalExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    BooleanFormula f = BasicFormulaFactory.relationalOperatorInstance(
        BinaryRelationalOperator.GREATER_THAN, (NumericFormula) getOut(node
            .getLeft()), (NumericFormula) getOut(node.getRight()));
    setOut(node, f);
  }

  public void caseAGreaterThanOrEqualsRelationalExpression(
      AGreaterThanOrEqualsRelationalExpression node) {
    node.getLeft().apply(this);
    node.getRight().apply(this);
    BooleanFormula f = BasicFormulaFactory.relationalOperatorInstance(
        BinaryRelationalOperator.GREATER_THAN_OR_EQUALS,
        (NumericFormula) getOut(node.getLeft()), (NumericFormula) getOut(node
            .getRight()));
    setOut(node, f);
  }
}