/*
 * This file is part of Symphonie
 * Created : 18 févr. 2005 09:31:20
 */

package fr.umlv.symphonie.util.wizard;

import java.util.ArrayList;
import java.util.HashMap;

import fr.umlv.symphonie.util.wizard.event.WizardEvent;

/**
 * Default implementation of a wizard model, it works as a queue of panels, first
 * in first shown.
 */
public class DefaultWizardModel extends AbstractWizardModel {

  /**
   * List of panels
   */
  protected final ArrayList<WizardPanel> panels = new ArrayList<WizardPanel>();

  /**
   * Current panel
   */
  protected int currentPanel;
  
  /**
   * Data for panels
   */
  protected final HashMap<Object, Object> interPanelData = new HashMap<Object, Object>(); 

  /**
   * Creates a new WizardModel with the given panel as current panel
   * 
   * @param firstPanel
   *          The first panel
   */
  public DefaultWizardModel(WizardPanel firstPanel) {
    panels.add(firstPanel);
    firstPanel.setData(interPanelData);
    currentPanel = 0;
  }
  
  /**
   * Default constructor.<br>
   * Can only be used by derived types.
   */
  protected DefaultWizardModel() {
    currentPanel = -1;
  }

  /**
   * Adds a panel to the model
   * 
   * @param panel
   *          The panel to add
   */
  public void addPanel(WizardPanel panel) {
    panels.add(panel);
  }

  public WizardPanel getCurrentPanel() {
    return panels.get(currentPanel);
  }

  public boolean hasNextPanel() {
    return currentPanel < (panels.size() - 1);
  }

  public boolean hasPreviousPanel() {
    return currentPanel > 0;
  }

  public void moveToNextPanel() {
    WizardPanel p = panels.get(currentPanel);
    if (hasNextPanel() && p.isValid()) {
      currentPanel++;
      getCurrentPanel().setData(interPanelData);
      fireCurrentPanelChanged(new WizardEvent(this, p));
    } else
      throw new RuntimeException("Cannot move to next panel");
  }

  public void moveToPreviousPanel() {
    if (!hasPreviousPanel())
      throw new RuntimeException("Cannot move to previous panel");
    WizardEvent we = new WizardEvent(this, panels.get(currentPanel));
    currentPanel--;
    fireCurrentPanelChanged(we);
  }

  public boolean canFinish(WizardPanel panel) {
    return panel.canFinish() && panel.isValid();
  }

  public boolean hasHelp(WizardPanel panel) {
    return panel.hasHelp();
  }

  public void start() {
    currentPanel = 0;
    getCurrentPanel().setData(interPanelData);
    fireCurrentPanelChanged(new WizardEvent(this, getCurrentPanel()));
  }
  
  public String getCancelButtonText() {
    return DEFAULT_CANCEL_BUTTON_TEXT;
  }

  public String getFinishButtonText() {
    return DEFAULT_FINISH_BUTTON_TEXT;
  }

  public String getHelpButtonText() {
    return DEFAULT_HELP_BUTTON_TEXT;
  }

  public String getNextButtonText() {
    return DEFAULT_NEXT_BUTTON_TEXT;
  }

  public String getPreviousButtonText() {
    return DEFAULT_PREVIOUS_BUTTON_TEXT;
  }

  public String getWizardTitle() {
    return DEFAULT_WIZARD_TITLE;
  }

  /**
   * Cancel
   */
  private static final String DEFAULT_CANCEL_BUTTON_TEXT = "Cancel";

  /**
   * Finish
   */
  private static final String DEFAULT_FINISH_BUTTON_TEXT = "Finish";

  /**
   * Help
   */
  private static final String DEFAULT_HELP_BUTTON_TEXT = "Help";

  /**
   * Next >
   */
  private static final String DEFAULT_NEXT_BUTTON_TEXT = "Next >";

  /**
   * &lt; Previous
   */
  private static final String DEFAULT_PREVIOUS_BUTTON_TEXT = "< Previous";

  /**
   * Symphony Wizard
   */
  private static final String DEFAULT_WIZARD_TITLE = "Symphony Wizard";
}
