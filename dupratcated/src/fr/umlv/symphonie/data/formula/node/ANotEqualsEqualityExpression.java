/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.node;

import fr.umlv.symphonie.data.formula.analysis.Analysis;

public final class ANotEqualsEqualityExpression extends PEqualityExpression {

  private PAdditiveExpression _left_;
  private TNotEquals _notEquals_;
  private PAdditiveExpression _right_;

  public ANotEqualsEqualityExpression() {
  }

  public ANotEqualsEqualityExpression(PAdditiveExpression _left_,
      TNotEquals _notEquals_, PAdditiveExpression _right_) {
    setLeft(_left_);

    setNotEquals(_notEquals_);

    setRight(_right_);

  }

  public Object clone() {
    return new ANotEqualsEqualityExpression(
        (PAdditiveExpression) cloneNode(_left_),
        (TNotEquals) cloneNode(_notEquals_),
        (PAdditiveExpression) cloneNode(_right_));
  }

  public void apply(Switch sw) {
    ((Analysis) sw).caseANotEqualsEqualityExpression(this);
  }

  public PAdditiveExpression getLeft() {
    return _left_;
  }

  public void setLeft(PAdditiveExpression node) {
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

  public TNotEquals getNotEquals() {
    return _notEquals_;
  }

  public void setNotEquals(TNotEquals node) {
    if (_notEquals_ != null) {
      _notEquals_.parent(null);
    }

    if (node != null) {
      if (node.parent() != null) {
        node.parent().removeChild(node);
      }

      node.parent(this);
    }

    _notEquals_ = node;
  }

  public PAdditiveExpression getRight() {
    return _right_;
  }

  public void setRight(PAdditiveExpression node) {
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
    return "" + toString(_left_) + toString(_notEquals_) + toString(_right_);
  }

  void removeChild(Node child) {
    if (_left_ == child) {
      _left_ = null;
      return;
    }

    if (_notEquals_ == child) {
      _notEquals_ = null;
      return;
    }

    if (_right_ == child) {
      _right_ = null;
      return;
    }

  }

  void replaceChild(Node oldChild, Node newChild) {
    if (_left_ == oldChild) {
      setLeft((PAdditiveExpression) newChild);
      return;
    }

    if (_notEquals_ == oldChild) {
      setNotEquals((TNotEquals) newChild);
      return;
    }

    if (_right_ == oldChild) {
      setRight((PAdditiveExpression) newChild);
      return;
    }

  }
}