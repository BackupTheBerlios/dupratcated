/*
 * This file is part of Symphonie
 * Created on 10 mars 2005
 */

package fr.umlv.symphonie.view.dialog;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.ComponentBuilder.ButtonType;
import fr.umlv.symphonie.util.identification.IdentificationException;
import fr.umlv.symphonie.view.Symphonie;
import fr.umlv.symphonie.view.SymphonieConstants;

/**
 * Dialog that allows user to change administration password
 * 
 * @author npersin, spenasal
 */
public class ChangePassDialog {

  /** Internal dialog */
  private final JDialog editor;

  /**
   * Creates a new ChangePassDialog for owner using the given builder
   * 
   * @param owner
   *          the parent symphonie instance
   * @param builder
   *          the builder for runtime text-changing
   */
  public ChangePassDialog(Symphonie owner, ComponentBuilder builder) {
    editor = new JDialog(owner.getFrame(), true);
    editor.setTitle(builder.getValue(SymphonieConstants.PWD_MENU_ITEM));
    editor.setContentPane(makeContentPane(builder, owner));
    editor.getRootPane().setBorder(
        BorderFactory.createLineBorder(editor.getBackground(), 10));
    editor.pack();
    editor.setLocationRelativeTo(owner.getFrame());
    editor.setResizable(false);
  }

  /**
   * Creates the dialog content pane
   * 
   * @param builder
   *          the builder for runtime text-changing
   * @param s
   *          the parent symphonie instance
   * @return a JPanel
   */
  private JPanel makeContentPane(final ComponentBuilder builder,
      final Symphonie s) {

    JPanel p = new JPanel(new GridLayout(4, 4, 10, 10));

    final JTextField old = new JPasswordField("");
    final JTextField newpass = new JPasswordField("");
    final JTextField verif = new JPasswordField("");

    p.add(builder.buildLabel(SymphonieConstants.CHANGEPASSDIALOG_OLD));
    p.add(old);
    p.add(builder.buildLabel(SymphonieConstants.CHANGEPASSDIALOG_NEW));
    p.add(newpass);
    p.add(builder.buildLabel(SymphonieConstants.CHANGEPASSDIALOG_VERIF));
    p.add(verif);

    /* BUTTONS *********** */
    JButton bok = (JButton) builder.buildButton(new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        if ((!old.getText().equals("")) && (!newpass.getText().equals(""))
            && (!verif.getText().equals(""))) {
          if (!newpass.getText().equals(verif.getText())) {
            JOptionPane.showMessageDialog(null, builder
                .getValue(SymphonieConstants.CHANGEPASSDIALOG_ERROR_MATCH),
                builder.getValue(SymphonieConstants.ERROR),
                JOptionPane.ERROR_MESSAGE);
          } else {

            try {
              s.getIdentificationStrategy().changePassword(old.getText(),
                  newpass.getText());
            } catch (IdentificationException e) {
              s.errDisplay.showException(e);
            }
            setVisible(false);
            old.setText("");
            newpass.setText("");
            verif.setText("");
            setVisible(false);
          }

        } else {
          JOptionPane.showMessageDialog(null, builder
              .getValue(SymphonieConstants.CHANGEPASSDIALOG_ERROR_EMPTY),
              builder.getValue(SymphonieConstants.ERROR),
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }, SymphonieConstants.BUTTON_OK, ButtonType.BUTTON);

    JButton bcancel = (JButton) builder.buildButton(new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        old.setText("");
        newpass.setText("");
        verif.setText("");
        setVisible(false);
      }
    }, SymphonieConstants.BUTTON_CANCEL, ButtonType.BUTTON);

    p.add(bok);
    p.add(bcancel);

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
}
