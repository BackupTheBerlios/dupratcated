
package fr.umlv.symphonie.model;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.table.AbstractTableModel;

import org.jfree.chart.ChartPanel;

import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.LookableCollection;
import fr.umlv.symphonie.util.completion.CompletionDictionary;
import fr.umlv.symphonie.util.completion.IDictionarySupport;
import fr.umlv.symphonie.view.cells.FormattableCellRenderer;
import fr.umlv.symphonie.view.cells.ObjectFormattingSupport;

/**
 * Abstract class for symphonie table models, this class exists for minimizing
 * the effort of implementing the <code>TableModel</code> interface
 * 
 * @author spenasal
 */
public abstract class AbstractSymphonieTableModel extends AbstractTableModel
    implements ObjectFormattingSupport, IDictionarySupport {

  /** The ComponentBuilder, used to internationalize the current model. */
  protected final ComponentBuilder builder;

  /** The DataManager which handles database */
  protected final DataManager manager;

  /**
   * A <code>CompletionDictionary</code> used in order to provide
   * auto-completion with this model.
   */
  protected final CompletionDictionary dictionary = new CompletionDictionary();

  /**
   * Pool of one thread. Used each time interacting with the database, in order
   * not to freeze the application.
   */
  protected final ExecutorService es = Executors.newSingleThreadExecutor();

  /**
   * An object used to be locked by each thread launched, in order not to
   * generate errors while interacting with the database.
   */
  protected final Object lock = new Object();

  /** Number of rows currently in the model */
  protected int columnCount = 0;

  /** Number of column currently in the model */
  protected int rowCount = 0;

  /** A cellrenderer */
  protected final FormattableCellRenderer formatter;

  /**
   * Constructs a ready to use <code>AbstractSymphonieTableModel</code>.
   * 
   * @param manager
   *          The <code>DataManager</code> which will be used to interact with
   *          database.
   * @param builder
   *          The <code>ComponentBuilder</code> which will provide
   *          internationalization.
   * @param formatter
   *          A cell renderer
   */
  AbstractSymphonieTableModel(DataManager manager, ComponentBuilder builder,
      FormattableCellRenderer formatter) {
    this.manager = manager;
    this.builder = builder;
    this.formatter = formatter;
    fillDefaultDictionary();
  }

  public FormattableCellRenderer getFormattableCellRenderer() {
    return formatter;
  }

  public LookableCollection<String> getDictionary() {
    return dictionary;
  }

  public void setDictionary(LookableCollection<String> dictionary) {
  }

  /**
   * Returns the number of rows currently in the model.
   * 
   * @see javax.swing.table.TableModel#getRowCount()
   */
  public int getRowCount() {
    return rowCount;
  }

  /**
   * Returns the number of columns currently int the model.
   * 
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  public int getColumnCount() {
    return columnCount;
  }

  /**
   * Tells if the model is empty or not.
   * 
   * @return <code>true</code> if the model is empty, <code>false</code>
   *         otherwise.
   */
  abstract public boolean isEmpty();

  /**
   * Constructs a <code>ChartPanel</code> representing the percentage of all
   * courses in golbal average.
   * 
   * @param step
   *          The interval of marks for the chart.
   * @return the <code>ChartPanel</code> generated.
   */
  abstract public ChartPanel getChartPanel(int step);

  /**
   * Returns the header for the current model
   * 
   * @return a <code>MessageFormat</code>
   */
  abstract public MessageFormat getHeaderMessageFormat();

  /**
   * Updates the data in the model.
   */
  abstract public void update();

  /**
   * Clears all data in the model.
   */
  abstract public void clear();

  /**
   * Fills the dictionary with default key words for autocompletion.
   */
  protected void fillDefaultDictionary() {
    dictionary.add("average");
    dictionary.add("min");
    dictionary.add("max");
  }

  /**
   * Fills the <code>Map</code> of values in
   * <code>SymphonieFormulaFactory</code> with all marks from all tests at a
   * given row int the model.
   * 
   * @param rowIndex
   *          the row which will determine the data to put in the map.
   */
  abstract public void fillFormulaMap(int rowIndex);
}
