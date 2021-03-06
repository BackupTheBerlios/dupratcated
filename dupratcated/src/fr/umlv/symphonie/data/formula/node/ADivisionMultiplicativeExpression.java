/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.node;

import fr.umlv.symphonie.data.formula.analysis.Analysis;

public final class ADivisionMultiplicativeExpression extends
    PMultiplicativeExpression {

  private PMultiplicativeExpression _left_;
  private TDivisionOperator _divisionOperator_;
  private PUnaryExpression _right_;

  public ADivisionMultiplicativeExpression() {
  }

  public ADivisionMultiplicativeExpression(PMultiplicativeExpression _left_,
      TDivisionOperator _divisionOperator_, PUnaryExpression _right_) {
    setLeft(_left_);

    setDivisionOperator(_divisionOperator_);

    setRight(_right_);

  }

  public Object clone() {
    return new ADivisionMultiplicativeExpression(
        (PMultiplicativeExpression) cloneNode(_left_),
        (TDivisionOperator) cloneNode(_divisionOperator_),
        (PUnaryExpression) cloneNode(_right_));
  }

  public void apply(Switch sw) {
    ((Analysis) sw).caseADivisionMultiplicativeExpression(this);
  }

  public PMultiplicativeExpression getLeft() {
    return _left_;
  }

  public void setLeft(PMultiplicativeExpression node) {
    if (_left_ != null) {
      _left_.parent(null);
    }

    if (node != null) {
      if (node.parent() != null) {
        node.parent().removeChild(node);
      }

      node.parent(this);
    }

    _left_ = node;
  }

  public TDivisionOperator getDivisionOperator() {
    return _divisionOperator_;
  }

  public void setDivisionOperator(TDivisionOperator node) {
    if (_divisionOperator_ != null) {
      _divisionOperator_.parent(null);
    }

    if (node != null) {
      if (node.parent() != null) {
        node.parent().removeChild(node);
      }

      node.parent(this);
    }

    _divisionOperator_ = node;
  }

  public PUnaryExpression getRight() {
    return _right_;
  }

  public void setRight(PUnaryExpression node) {
    if (_right_ != null) {
      _right_.parent(null);
    }

    if (node != null) {
      if (node.parent() != null) {
        node.parent().removeChild(node);
      }

      node.parent(this);
    }

    _right_ = node;
  }

  public String toString() {
    return "" + toString(_left_) + toString(_divisionOperator_)
        + toString(_right_);
  }

  void removeChild(Node child) {
    if (_left_ == child) {
      _left_ = null;
      return;
    }

    if (_divisionOperator_ == child) {
      _divisionOperator_ = null;
      return;
    }

    if (_right_ == child) {
      _right_ = null;
      return;
    }

  }

  void replaceChild(Node oldChild, Node newChild) {
    if (_left_ == oldChild) {
      setLeft((PMultiplicativeExpression) newChild);
      return;
    }

    if (_divisionOperator_ == oldChild) {
      setDivisionOperator((TDivisionOperator) newChild);
      return;
    }

    if (_right_ == oldChild) {
      setRight((PUnaryExpression) newChild);
      return;
    }

  }
}
