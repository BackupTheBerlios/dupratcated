/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.parser;

import fr.umlv.symphonie.data.formula.node.Token;

public class ParserException extends Exception {

  Token token;

  public ParserException(Token token, String message) {
    super(message);
    this.token = token;
  }

  public Token getToken() {
    return token;
  }
}
