/*
 * Created on 26 mars 2005
 */

package fr.umlv.symphonie.util.completion;

import fr.umlv.symphonie.util.LookableCollection;

/**
 * Interfaces for classes that can hold a dictionary
 * 
 * @author spenasal
 */
public interface IDictionarySupport {

  /**
   * Gets the current dictionary
   * 
   * @return Returns the dictionary.
   */
  public abstract LookableCollection<String> getDictionary();

  /**
   * Updates listener dictionary
   * 
   * @param dictionary
   *          The new dictionary.
   */
  public abstract void setDictionary(LookableCollection<String> dictionary);
}
