/*
 * This file is part of Symphonie
 * Created : 17 févr. 2005 21:56:43
 */

package fr.umlv.symphonie.util.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.umlv.symphonie.util.wizard.event.WizardEvent;
import fr.umlv.symphonie.util.wizard.event.WizardListenerAdapter;

/**
 * This class controls and display a wizard. <br>
 * To start the wizard call its show() method.
 * <p>
 * Wizards are non mutable objects, they only adapt to changes in models and
 * panels <br>
 * If you want to create a wizard you can either implement the
 * <code>WizardModel</code> interface or use the
 * <code>DefaultWizardModel</code>
 */
public class Wizard {

  /**
   * Internal dialog representing the wizard.
   */
  private final JDialog wizardDialog;

  /**
   * This wizard data model
   */
  private final WizardModel model;

  /**
   * Resulting wizard value
   * 
   * @see #WIZARD_CANCELLED
   * @see #WIZARD_FINISHED
   */
  private int result;

  /**
   * Action performed when user clicks the cancel button
   */
  private AbstractAction cancelAction;

  /**
   * Action performed when user clicks the help button
   */
  private AbstractAction helpAction;

  /**
   * Action performed when user clicks the previous button
   */
  private AbstractAction previousAction;

  /**
   * Action performed when user clicks the next button
   */
  private AbstractAction nextAction;

  /**
   * Action performed when user clicks the finish button
   */
  private AbstractAction finishAction;

  /**
   * Change listener that registers to each panel that the wizard displays.
   */
  private final ChangeListener panelListener = new ChangeListener() {

    public void stateChanged(ChangeEvent event) {
      updateActions(model.getCurrentPanel());
    }
  };

  /**
   * Creates a new wizard
   * 
   * @param owner
   *          The frame that owns the wizard
   * @param model
   *          The data model for the wizard
   * @param size
   *          The size of the wizard dialog
   */
  public Wizard(Frame owner, WizardModel model, Dimension size) {
    this.model = model;
    // Listen for cancel and finish events
    model.addWizardListener(new WizardListenerAdapter() {

      public void wizardCancelled(WizardEvent we) {
        close();
      }

      public void wizardFinished(WizardEvent we) {
        close();
      }
    });

    wizardDialog = new JDialog(owner, true);
    createActions();
    initDialog();
    setSize(size);
  }

  /**
   * Sets the wizard size. Wizards are not mouse-resizable, using this method is
   * the only way to do it.
   * 
   * @param size
   *          The new wizard dialog size
   */
  public void setSize(Dimension size) {
    wizardDialog.setResizable(true);
    wizardDialog.setPreferredSize(size);
    wizardDialog.setMinimumSize(size);
    wizardDialog.setSize(size);
    wizardDialog.setResizable(false);
  }

  /**
   * Starts current wizard
   * 
   * @see #WIZARD_CANCELLED
   * @see #WIZARD_FINISHED
   * @return The resulting value of the wizard
   */
  public int show() {
    model.start();
    wizardDialog.setTitle(model.getWizardTitle());
    wizardDialog.setVisible(true);
    return result;
  }

  /**
   * Closes current wizard
   */
  public void close() {
    wizardDialog.setVisible(false);
  }

  /**
   * Disables all dialog buttons
   */
  public void disableAllButtons() {
    cancelAction.setEnabled(false);
    finishAction.setEnabled(false);
    nextAction.setEnabled(false);
    previousAction.setEnabled(false);
    helpAction.setEnabled(false);
  }

  /**
   * Enables/disables the finish button
   * 
   * @param b
   *          The enable value
   */
  public void setEnabledFinish(boolean b) {
    finishAction.setEnabled(b);
  }

  /**
   * Initializes wizard dialog graphic components
   */
  private void initDialog() {

    final JLabel text = new JLabel();
    final JLabel title = new JLabel();
    title.setFont(title.getFont().deriveFont(Font.BOLD, 20.0f));

    // Top panel
    wizardDialog.add(getTopPanel(title, text), BorderLayout.NORTH);

    // Register as model listener for panel changes
    model.addWizardListener(new WizardListenerAdapter() {

      public void currentPanelChanged(WizardEvent we) {
        WizardPanel p = ((WizardModel) we.getSource()).getCurrentPanel();
        updateActions(p);
        title.setText((String) p.getTitle());
        text.setText((String) p.getDescription());
        setCentralPanel(p);
        p.removeChangeListener(panelListener);
        p.addChangeListener(panelListener);
      }
    });

    // Bottom panel
    wizardDialog.add(getBottomPanel(), BorderLayout.SOUTH);
  }

  /**
   * Sets the central panel for wizard. <br>
   * Any panel previously added is removed before the new one is set.
   * 
   * @param p
   *          The panel to set
   */
  private void setCentralPanel(WizardPanel p) {
    Component c = ((BorderLayout) wizardDialog.getContentPane().getLayout())
        .getLayoutComponent(BorderLayout.CENTER);
    if (c != null) wizardDialog.getContentPane().remove(c);
    wizardDialog.add(p.getPanelComponent(), BorderLayout.CENTER);
    wizardDialog.repaint();
  }

  /**
   * Creates navigation buttons' actions
   */
  private void createActions() {
    cancelAction = new AbstractAction(model.getCancelButtonText()) {

      public void actionPerformed(ActionEvent e) {
        model.cancel();
        result = WIZARD_CANCELLED;
      }
    };

    helpAction = new AbstractAction(model.getHelpButtonText()) {

      public void actionPerformed(ActionEvent e) {
        model.getCurrentPanel().help();
      }
    };

    nextAction = new AbstractAction(model.getNextButtonText()) {

      public void actionPerformed(ActionEvent e) {
        model.moveToNextPanel();
      }
    };

    previousAction = new AbstractAction(model.getPreviousButtonText()) {

      public void actionPerformed(ActionEvent e) {
        model.moveToPreviousPanel();
      }
    };

    finishAction = new AbstractAction(model.getFinishButtonText()) {

      public void actionPerformed(ActionEvent e) {
        model.finish();
        result = WIZARD_FINISHED;
      }
    };
  }

  /**
   * Enables or disables actions depending on given panel's state
   * 
   * @param p
   *          The panel to check
   */
  private void updateActions(WizardPanel p) {
    helpAction.setEnabled(model.hasHelp(p));
    previousAction.setEnabled(model.hasPreviousPanel());
    nextAction.setEnabled(model.hasNextPanel() && p.isValid());
    finishAction.setEnabled(model.canFinish(p));
  }

  /**
   * Creates the top panel that contains current <code>WizardPanel</code>
   * title and description.
   * 
   * @param title
   *          The title
   * @param text
   *          The description
   * @return The top panel
   */
  private JPanel getTopPanel(JLabel title, JLabel text) {
    JPanel labels = new JPanel(new GridBagLayout());
    labels.setBackground(Color.WHITE);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1.0;
    gbc.insets = new Insets(8, 6, 3, 10);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    labels.add(title, gbc);
    gbc.insets = new Insets(3, 18, 5, 10);
    labels.add(text, gbc);

    JPanel top = new JPanel(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    top.add(labels, gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.weightx = 0.0;
    gbc.fill = GridBagConstraints.NONE;
    top.add(new JLabel(model.getWizardIcon()), gbc);
    top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
    top.setBackground(Color.WHITE);
    return top;
  }

  /**
   * Creates the bottom panel that contains navigation buttons
   * 
   * @return The bottom panel
   */
  private JPanel getBottomPanel() {
    JButton cancel = new JButton(cancelAction);
    JButton help = new JButton(helpAction);
    JButton finish = new JButton(finishAction);
    JButton next = new JButton(nextAction);
    JButton previous = new JButton(previousAction);

    JPanel buttons = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;
    buttons.add(cancel, gbc);
    gbc.weightx = 1;
    buttons.add(help, gbc);
    gbc.anchor = GridBagConstraints.EAST;
    buttons.add(previous, gbc);
    gbc.weightx = 0;
    buttons.add(next, gbc);
    buttons.add(finish, gbc);

    JPanel bottom = new JPanel(null);
    bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
    bottom.add(new JSeparator());
    bottom.add(buttons);

    return bottom;
  }

  /**
   * Value returned when user cancles wizard
   */
  public static final int WIZARD_CANCELLED = 0;

  /**
   * Value returned when wizard finishes normally
   */
  public static final int WIZARD_FINISHED = 1;
}
