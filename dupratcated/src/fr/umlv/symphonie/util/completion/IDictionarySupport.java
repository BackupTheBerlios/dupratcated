/*
 * Created on 26 mars 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package fr.umlv.symphonie.util.completion;

import fr.umlv.symphonie.util.LookableCollection;

/**
 * @author spenasal
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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
