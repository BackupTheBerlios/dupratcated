/*
 * This file is part of Symphonie
 * Created on 26 mars 2005
 */

package fr.umlv.symphonie.util.completion;

import java.awt.event.KeyAdapter;

import fr.umlv.symphonie.util.LookableCollection;

/**
 * This class is a <code>KeyListener</code> that contains a dictionary,
 * actually a <code>LookableCollection</code> of <code>String</code>
 * 
 * @see fr.umlv.symphonie.util.LookableCollection
 * @see fr.umlv.symphonie.util.completion.CompletionDictionary
 * @author spenasal
 */
public class DictionaryKeyListener extends KeyAdapter implements IDictionarySupport {

  /** Contained dictionary */
  protected LookableCollection<String> dictionary;

  /**
   * Gets the current dictionary
   * 
   * @return Returns the dictionary.
   */
  public LookableCollection<String> getDictionary() {
    return dictionary;
  }

  /**
   * Updates listener dictionary
   * 
   * @param dictionary
   *          The new dictionary.
   */
  public void setDictionary(LookableCollection<String> dictionary) {
    this.dictionary = dictionary;
  }
}
