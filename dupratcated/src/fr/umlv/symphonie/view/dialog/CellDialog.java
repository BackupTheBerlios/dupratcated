/*
 * This file is part of Symphonie
 * Created : 2 mars 2005 21:02:26
 */

package fr.umlv.symphonie.view.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.RELATIVE;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.awt.GridBagConstraints.WEST;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.umlv.symphonie.data.formula.BooleanFormula;
import fr.umlv.symphonie.data.formula.SymphonieFormulaFactory;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.LookableCollection;
import fr.umlv.symphonie.util.completion.AutoCompleteSupport;
import fr.umlv.symphonie.util.completion.DictionaryKeyListener;
import fr.umlv.symphonie.util.completion.IDictionarySupport;
import fr.umlv.symphonie.view.Symphonie;
import fr.umlv.symphonie.view.SymphonieConstants;
import fr.umlv.symphonie.view.cells.CellFormat;

/**
 * Dialog for cell formatting.
 * 
 * @author npersin, spenasal
 */
public class CellDialog implements IDictionarySupport {

  /** Internal dialog */
  private final JDialog dialog;

  /** User choosen cell format */
  private CellFormat format;

  /** Listener for formula autocompletion */
  private DictionaryKeyListener completionListener;

  /**
   * Creates a new CellDialog using the given builder
   * 
   * @param owner
   *          the symphonie instance parent of the dialog
   * @param builder
   *          the builder for runtime text-changing
   */
  public CellDialog(Symphonie owner, ComponentBuilder builder) {
    dialog = new JDialog(owner.getFrame(), true);
    dialog.setTitle(builder.getValue(SymphonieConstants.CELLDIALOG_TITRE));
    dialog.setContentPane(makeContentPane(owner, builder));
    dialog.setSize(350, 300);
    dialog.setLocationRelativeTo(owner.getFrame());
    dialog.setResizable(false);
  }

  /**
   * Creates the content pane of the internal dialog
   * 
   * @param owner
   *          the symphonie instance parent of the dialog
   * @param builder
   *          the builder for runtime text-changing
   * 
   * @return JPanel
   */
  private JPanel makeContentPane(final Symphonie owner,
      final ComponentBuilder builder) {

    JPanel p = new JPanel(new GridBagLayout());
    final JTextArea area = new JTextArea("");
    completionListener = AutoCompleteSupport.addSupport(area, null, "[,${}()]");
    final JLabel sample = builder
        .buildLabel(SymphonieConstants.CELLDIALOG_SAMPLE);
    final JPanel prevBgColor = new JPanel();
    prevBgColor.add(sample);

    final JButton fore = new JButton(new AbstractAction(" ") {

      public void actionPerformed(ActionEvent e) {
        Color foreground = JColorChooser.showDialog((Component) e.getSource(),
            builder.getValue(SymphonieConstants.CELLDIALOG_CHOOSER_TITRE),
            Color.BLACK);
        format.setForeground(foreground);
        sample.setForeground(foreground);
        ((JButton) e.getSource()).setBackground(foreground);
      }
    });
    fore.setFocusable(false);
    fore.setBackground(Color.BLACK);

    final JButton back = new JButton(new AbstractAction(" ") {

      public void actionPerformed(ActionEvent e) {
        Color background = JColorChooser.showDialog((Component) e.getSource(),
            builder.getValue(SymphonieConstants.CELLDIALOG_CHOOSER_TITRE),
            Color.WHITE);
        format.setBackground(background);
        prevBgColor.setBackground(background);
        ((JButton) e.getSource()).setBackground(background);
      }
    });
    back.setFocusable(false);
    back.setBackground(Color.WHITE);

    /* TEXT AREA **** */
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(10, 10, 5, 10);
    c.anchor = WEST;
    c.gridwidth = REMAINDER;
    p.add(builder.buildLabel(SymphonieConstants.CELLDIALOG_TITREAREA), c);
    c.gridwidth = REMAINDER;
    c.fill = BOTH;
    c.insets.top = 0;
    c.weightx = 1.0;
    c.weighty = 1.0;
    p.add(new JScrollPane(area), c);

    /* COLORS ******* */
    c.insets.top = 10;
    c.insets.left = 20;
    c.gridwidth = RELATIVE;
    c.fill = HORIZONTAL;
    c.weighty = 0.0;
    p.add(builder.buildLabel(SymphonieConstants.CELLDIALOG_TEXTCOLOR), c);
    c.fill = NONE;
    c.insets.left = 5;
    c.gridwidth = REMAINDER;
    p.add(fore, c);

    c.insets.top = 5;
    c.insets.left = 20;
    c.gridwidth = RELATIVE;
    c.fill = HORIZONTAL;
    p.add(builder.buildLabel(SymphonieConstants.CELLDIALOG_BGCOLOR), c);
    c.fill = NONE;
    c.insets.left = 5;
    c.gridwidth = REMAINDER;
    p.add(back, c);

    /* PREVIEW ******* */
    JPanel prev = getPrevPanel(prevBgColor, builder);
    c.fill = GridBagConstraints.HORIZONTAL;
    p.add(prev, c);

    /* BUTTONS *********** */
    JButton bok = (JButton) builder.buildButton(new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        BooleanFormula bf = null;
        try {
          bf = SymphonieFormulaFactory.parseBooleanFormula(area.getText());
        } catch (Exception e1) {
          owner.errDisplay.showException(e1);
          return;
        }
        format.setCondition(bf);
        dialog.setVisible(false);
        fore.setBackground(Color.BLACK);
        back.setBackground(Color.WHITE);
        area.setText("");
      }
    }, SymphonieConstants.BUTTON_OK, ComponentBuilder.ButtonType.BUTTON);

    JButton bcancel = (JButton) builder.buildButton(new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        fore.setBackground(Color.BLACK);
        back.setBackground(Color.WHITE);
        area.setText("");
      }
    }, SymphonieConstants.BUTTON_CANCEL, ComponentBuilder.ButtonType.BUTTON);

    Box b = new Box(BoxLayout.X_AXIS);
    b.add(bok);
    b.add(Box.createHorizontalStrut(10));
    b.add(bcancel);
    b.add(Box.createHorizontalStrut(10));

    c.weighty = 0.0;
    c.weightx = 1.0;
    c.fill = NONE;
    c.anchor = EAST;
    p.add(b, c);

    return p;
  }

  /**
   * Builds the preview panel
   * 
   * @param sample
   *          The sample component
   * @param builder
   *          The builder for internationalization
   * @return a JPanel
   */
  private JPanel getPrevPanel(JComponent sample, final ComponentBuilder builder) {
    JPanel p = new JPanel(new GridBagLayout());
    final TitledBorder border = new TitledBorder(builder
        .getValue(SymphonieConstants.CELLDIALOG_PREVIEW));

    builder.addChangeListener(p, new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        border
            .setTitle(builder.getValue(SymphonieConstants.CELLDIALOG_PREVIEW));
      }
    });

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.insets = new Insets(0, 20, 5, 20);
    p.setBorder(border);
    p.add(sample, gbc);
    return p;
  }

  /**
   * Sets dialog as modal
   * 
   * @param modal
   *          <code>true</code> or <code>false</code>
   */
  public void setModal(boolean modal) {
    dialog.setModal(modal);
  }

  /**
   * Displays/hides dialog. Calling this method clears user input
   * 
   * @param show
   *          <code>true</code> or <code>false</code>
   */
  public void setVisible(boolean show) {
    if (show) format = new CellFormat(null, null, null);
    dialog.setVisible(show);
  }

  /**
   * Returns the format choosen by the user
   * 
   * @return a <code>CellFormat</code> object
   */
  public CellFormat getUserFormat() {
    return format;
  }

  public LookableCollection<String> getDictionary() {
    return completionListener.getDictionary();
  }

  public void setDictionary(LookableCollection<String> dictionary) {
    completionListener.setDictionary(dictionary);
  }
}
