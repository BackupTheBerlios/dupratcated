/*
 * This file is part of Symphonie
 * Created : 3 mars 2005 00:29:10
 */

package fr.umlv.symphonie.view.dialog;

import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
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
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.LookableCollection;
import fr.umlv.symphonie.util.ComponentBuilder.ButtonType;
import fr.umlv.symphonie.util.completion.AutoCompleteSupport;
import fr.umlv.symphonie.util.completion.DictionaryKeyListener;
import fr.umlv.symphonie.util.completion.IDictionarySupport;
import fr.umlv.symphonie.view.Symphonie;
import fr.umlv.symphonie.view.SymphonieConstants;

/**
 * Dialog class that allows to add a formula in the teacher view
 * 
 * @see fr.umlv.symphonie.view.Symphonie
 * @author npersin, susmab, spenasal
 */
public class FormulaDialog implements IDictionarySupport {

  /** Internal dialog */
  private final JDialog editor;

  /** Listener for formula autocompletion */
  private DictionaryKeyListener completionListener;

  /**
   * Creates a new FormulaDialog for owner using the given builder
   * 
   * @param owner
   *          the parent symphonie instance
   * @param builder
   *          the builder for runtime text-changing
   */
  public FormulaDialog(Symphonie owner, ComponentBuilder builder) {
    editor = new JDialog(owner.getFrame(), true);
    editor.setTitle(builder.getValue(SymphonieConstants.FORMULADIALOG_TITLE));
    editor.setContentPane(makeContentPane(owner, builder));
    editor.setSize(480, 330);
    editor.setLocationRelativeTo(owner.getFrame());
    editor.setResizable(false);
  }

  /**
   * Creates the internal dialog content pane
   * 
   * @param The
   *          symphonie instance that owns the dialog
   * @param builder
   *          The builder for internationalization components
   * @return a JPanel
   */
  private JPanel makeContentPane(final Symphonie owner,
      final ComponentBuilder builder) {

    JPanel p = new JPanel(new GridBagLayout());
    final JTextArea area = new JTextArea("");
    completionListener = AutoCompleteSupport.addSupport(area, null, "[,${}()]");

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(15, 5, 5, 5);
    gbc.anchor = WEST;
    gbc.fill = NONE;
    gbc.insets.left = 20;
    p.add(builder.buildLabel(SymphonieConstants.CELLDIALOG_COLUMNNUMBER), gbc);

    final JTextField columnField = new JTextField("");
    gbc.gridwidth = REMAINDER;
    gbc.fill = NONE;
    gbc.insets.right = 20;
    gbc.insets.left = 5;
    gbc.weightx = 1.0;
    columnField.setColumns(4);
    p.add(columnField, gbc);

    gbc.insets.top = 5;
    gbc.insets.left = 20;
    gbc.weightx = 0.0;
    gbc.fill = NONE;
    gbc.gridwidth = RELATIVE;
    p.add(builder.buildLabel(SymphonieConstants.FORMULA_TITLE), gbc);

    final JTextField titleField = new JTextField("");
    gbc.weightx = 1.0;
    gbc.anchor = WEST;
    gbc.fill = HORIZONTAL;
    gbc.gridwidth = REMAINDER;
    gbc.insets.right = 50;
    gbc.insets.left = 5;
    gbc.weightx = 0.0;
    p.add(titleField, gbc);

    gbc.insets.top = 10;
    gbc.anchor = CENTER;
    gbc.fill = HORIZONTAL;
    gbc.gridwidth = REMAINDER;
    gbc.insets.right = 20;
    gbc.insets.left = 20;
    p.add(builder.buildLabel(SymphonieConstants.CELLDIALOG_TITREAREA), gbc);

    gbc.insets.top = 5;
    gbc.insets.bottom = 10;
    gbc.fill = BOTH;
    gbc.weighty = 1.0;
    p.add(new JScrollPane(area), gbc);

    /* BUTTONS *********** */
    JButton bok = (JButton) builder.buildButton(new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        int column;

        try {
          column = Integer.parseInt(columnField.getText());
        } catch (NumberFormatException e1) {
          JOptionPane.showMessageDialog(editor, "Invalid Column number");
          return;
        }

        String title = titleField.getText();
        String expression = area.getText();

        if (title.equals("") || expression.equals("")) {
          JOptionPane.showMessageDialog(null, builder
              .getValue(SymphonieConstants.CHANGEPASSDIALOG_ERROR_EMPTY),
              builder.getValue(SymphonieConstants.ERROR),
              JOptionPane.ERROR_MESSAGE);
          return;
        }
        owner.getCurrentTeacherModel().addFormula(expression, title, column);
        columnField.setText("");
        titleField.setText("");
        area.setText("");
        setVisible(false);
      }
    }, SymphonieConstants.BUTTON_OK, ButtonType.BUTTON);

    JButton bcancel = (JButton) builder.buildButton(new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        columnField.setText("");
        titleField.setText("");
        area.setText("");
        editor.setVisible(false);
      }
    }, SymphonieConstants.BUTTON_CANCEL, ButtonType.BUTTON);

    Box b = new Box(BoxLayout.X_AXIS);
    b.add(bok);
    b.add(Box.createHorizontalStrut(10));
    b.add(bcancel);
    b.add(Box.createHorizontalStrut(10));

    gbc.weighty = 0.0;
    gbc.weightx = 1.0;
    gbc.fill = NONE;
    gbc.anchor = EAST;
    p.add(b, gbc);

    return p;
  }

  /**
   * Sets dialog as modal
   * 
   * @param modal
   *          <code>true</code> or <code>false</code>
   */
  public void setModal(boolean modal) {
    editor.setModal(modal);
  }

  /**
   * Displays/hides dialog.
   * 
   * @param show
   *          <code>true</code> or <code>false</code>
   */
  public void setVisible(boolean show) {
    editor.setVisible(show);
  }

  public LookableCollection<String> getDictionary() {
    return completionListener.getDictionary();
  }

  public void setDictionary(LookableCollection<String> dictionary) {
    completionListener.setDictionary(dictionary);
  }
}
