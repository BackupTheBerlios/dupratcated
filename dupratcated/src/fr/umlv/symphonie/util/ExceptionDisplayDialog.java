/*
 * This file is part of Symphonie
 * Created : 13 mars 2005 09:43:27
 */

package fr.umlv.symphonie.util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Dialog for standardly display <code>Exception</code> s
 */
public final class ExceptionDisplayDialog {

  /**
   * The internal dialog
   */
  private final JDialog dialog;

  /**
   * Text area to display the exception stack trace
   */
  private final JTextArea stackTrace = new JTextArea();

  /**
   * Label where the exception name is to be displayed
   */
  private final JLabel exName = new JLabel("Exception");

  /**
   * Action for showing stack trace
   */
  private final AbstractAction show;

  /**
   * Action for hiding stack trace
   */
  private final AbstractAction hide;

  /**
   * Dimension when stack trace is not shown
   */
  private final Dimension little = new Dimension(450, 127);
  /**
   * Dimension when stack trace is not shown
   */
  private final Dimension big = new Dimension(450, 320);

  /**
   * Creates a new <code>ExceptionDisplayDialog</code>
   * 
   * @param owner
   *          the dialog owner
   * @param b
   *          a <code>ComponentBuilder</code> for internationalization
   */
  public ExceptionDisplayDialog(JFrame owner, final ComponentBuilder b) {
    dialog = new JDialog(owner, true);
    stackTrace.setVisible(true);
    dialog.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets.top = 15;
    gbc.insets.left = 10;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    JLabel lb = b.buildLabel("exceptiondialog.message");
    Icon i = UIManager.getIcon("OptionPane.errorIcon");
    lb.setIcon(i);
    dialog.add(lb, gbc);
    gbc.insets.top = 0;
    gbc.insets.left = 20 + i.getIconWidth();
    dialog.add(exName, gbc);

    final JScrollPane sp = new JScrollPane(stackTrace);
    sp.setVisible(false);
    final JButton detail = new JButton();
    show = new AbstractAction(b.getValue("exceptiondialog.showdetail")) {

      public void actionPerformed(ActionEvent ev) {
        sp.setVisible(true);
        dialog.setPreferredSize(big);
        detail.setAction(hide);
        dialog.pack();
      }
    };
    detail.setAction(show);
    hide = new AbstractAction(b.getValue("exceptiondialog.hidedetail")) {

      public void actionPerformed(ActionEvent ev) {
        sp.setVisible(false);
        dialog.setPreferredSize(little);
        detail.setAction(show);
        dialog.pack();
      }
    };
    b.addChangeListener(detail, new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        hide.putValue(Action.NAME, b.getValue("exceptiondialog.hidedetail"));
        show.putValue(Action.NAME, b.getValue("exceptiondialog.showdetail"));
      }
    });
    gbc.insets.right = 10;
    gbc.insets.left = 5;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridwidth = GridBagConstraints.RELATIVE;
    gbc.weightx = 1.0;
    gbc.insets.bottom = 10;
    dialog.add(detail, gbc);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = 0.0;
    dialog.add(b.buildButton(new AbstractAction("ok") {

      public void actionPerformed(ActionEvent e) {
        dialog.setVisible(false);
      }
    }, "bok", ComponentBuilder.ButtonType.BUTTON), gbc);
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1.0;
    gbc.weightx = 1.0;
    gbc.insets.top = 5;
    gbc.insets.left = 10;
    dialog.add(sp, gbc);
    dialog.setPreferredSize(little);
    dialog.pack();
  }

  /**
   * Displays an exception in the dialog
   * 
   * @param e
   *          the exception to display
   */
  public void showException(Throwable e) {
    StringWriter w = new StringWriter();
    e.printStackTrace(new PrintWriter(w));
    stackTrace.setEditable(true);
    stackTrace.setText(w.getBuffer().toString());
    stackTrace.setEditable(false);
    exName.setText(e.getClass().getSimpleName());
    dialog.setTitle(exName.getText() + " : " + e.getMessage());
    dialog.setVisible(true);
  }

  /**
   * Closes dialog
   */
  public void hide() {
    dialog.setVisible(false);
  }
}
