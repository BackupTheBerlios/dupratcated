/*
 * This file is part of Symphonie
 * Created : 23 mars 2005 17:35:13
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
 */
public class AddCourseDialog {

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
  public AddCourseDialog(Symphonie owner, ComponentBuilder builder) {
    editor = new JDialog(owner.getFrame(), true);
    editor.setTitle(builder.getValue(SymphonieConstants.ADDCOURSE));
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

    final JTextField descField = new JTextField();
    final JTextField coeffField = new JTextField(3);

    p.add(builder.buildLabel(SymphonieConstants.ADDMARKDIALOG_DESC));
    p.add(descField);
    p.add(builder.buildLabel(SymphonieConstants.ADDMARKDIALOG_COEFF));
    p.add(coeffField);

    /* BUTTONS *********** */
    JButton bok = (JButton) builder.buildButton(SymphonieConstants.BUTTON_OK,
        ButtonType.BUTTON);
    JButton bcancel = (JButton) builder.buildButton(
        SymphonieConstants.BUTTON_CANCEL, ButtonType.BUTTON);

    bok.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent event) {

        if (descField.getText().equals("") || coeffField.getText().equals("")) {
          JOptionPane.showMessageDialog(null, builder
              .getValue(SymphonieConstants.CHANGEPASSDIALOG_ERROR_EMPTY),
              builder.getValue(SymphonieConstants.ERROR),
              JOptionPane.ERROR_MESSAGE);
        } else {
          String desc = descField.getText();
          
          float coeff;
          
          try{
            coeff = Float.parseFloat(coeffField.getText());
          }catch (NumberFormatException e){
            coeff = 0f;
          }
          
          editor.setVisible(false);
          descField.setText("");
          coeffField.setText("");
          owner.getCourseTreeModel().addCourse(desc, coeff);
        }
      }
    });

    bcancel.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent event) {
        editor.setVisible(false);
        descField.setText("");
        coeffField.setText("");
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
