
package fr.umlv.symphonie.data.formula;

/**
 * Interface defines a simple expression whose return value type can be
 * parametrized.
 */
public interface Formula<T> {

  /**
   * Evaluate this formula.
   * 
   * @return This formula's value of type T
   */
  public T getValue();

  /**
   * Returns this formula description
   * 
   * @return a <code>String</code>
   */
  public String getDescription();

  /**
   * Returns the column that this <code>Formula</code> is applied on
   * 
   * @return an <code>int</code>
   */
  public int getColumn();

  /**
   * Returns this formula's ID
   * 
   * @return an <code>int</code>
   */
  public int getID();
}
