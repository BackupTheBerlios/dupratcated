/*
 * This file is part of Symphonie
 * Created : 22 mars 2005 20:24:15
 */
package fr.umlv.symphonie.view.dialog;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.ComponentBuilder.ButtonType;
import fr.umlv.symphonie.view.Symphonie;
import fr.umlv.symphonie.view.SymphonieConstants;


/**
 * @author fvallee
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddStudentDialog {
  /** Internal dialog */
  protected final JDialog editor;

  /**
   * Creates a new dialog instance for the given owner
   * 
   * @param owner
   *          The symphonie instance owner of the dialog
   * @param builder
   *          The builder for internationalizable components
   */
  public AddStudentDialog(Symphonie owner, ComponentBuilder builder) {
    editor = new JDialog(owner.getFrame(), true);
    editor.setTitle(builder.getValue(SymphonieConstants.ADD_STUDENT_TITLE));
    editor.setContentPane(makeContentPane(owner, builder));
    editor.getRootPane().setBorder(
        BorderFactory.createLineBorder(editor.getBackground(), 5));
    editor.pack();
    editor.setLocationRelativeTo(owner.getFrame());
    editor.setResizable(false);
  }

  /**
   * Creates the dialog content pane
   * 
   * @param owner
   *          The symphonie instance owner of the dialog
   * @param builder
   *          The builder for internationalizable components
   * @return a <code>JPanel</code>
   */
  private JPanel makeContentPane(final Symphonie owner,
      final ComponentBuilder builder) {
    JPanel p = new JPanel(new GridLayout(3, 2, 10, 10));

    final JTextField lastNameField = new JTextField();
    final JTextField nameField = new JTextField();

    p.add(builder.buildLabel(SymphonieConstants.ADD_STUDENT_NAME));
    p.add(nameField);
    p.add(builder.buildLabel(SymphonieConstants.ADD_STUDENT_LASTNAME));
    p.add(lastNameField);

    /* BUTTONS *********** */
    JButton bok = (JButton) builder.buildButton(SymphonieConstants.BUTTON_OK,
        ButtonType.BUTTON);
    JButton bcancel = (JButton) builder.buildButton(
        SymphonieConstants.BUTTON_CANCEL, ButtonType.BUTTON);

    bok.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent event) {

        if (nameField.getText().equals("") || lastNameField.getText().equals("")) {
          JOptionPane.showMessageDialog(null, builder
              .getValue(SymphonieConstants.CHANGEPASSDIALOG_ERROR_EMPTY),
              builder.getValue(SymphonieConstants.ERROR),
              JOptionPane.ERROR_MESSAGE);
        } else {
          String name = nameField.getText();
          String lastName = lastNameField.getText();
          
          editor.setVisible(false);
          lastNameField.setText("");
          nameField.setText("");
          owner.getStudentTreeModel().addStudent(name, lastName);
        }
      }
    });

    bcancel.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent event) {
        editor.setVisible(false);
        lastNameField.setText("");
        nameField.setText("");
      }
    });

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
   * Displays/hides dialog. Calling this method clears user input
   * 
   * @param show
   *          <code>true</code> or <code>false</code>
   */
  public void setVisible(boolean show) {
    editor.setVisible(show);
  }

}
