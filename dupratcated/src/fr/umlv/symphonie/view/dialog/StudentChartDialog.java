/*
 * This file is part of Symphonie
 * Created : 20-mars-2005 18:57:25
 */

package fr.umlv.symphonie.view.dialog;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;

import fr.umlv.symphonie.model.StudentModel;
import fr.umlv.symphonie.view.Symphonie;

/**
 * Dialog class that displays Student charts
 * 
 * @author susmab
 */
public class StudentChartDialog {

  /** Internal dialog */
  protected final JDialog editor;

  /** Internal dialog panel */
  protected final JPanel panel = new JPanel(new BorderLayout());

  /**
   * Creates a new student chart dialog
   * 
   * @param owner
   *          The symphonie instance owner of the dialog
   */
  public StudentChartDialog(Symphonie owner) {
    editor = new JDialog(owner.getFrame(), true);
    editor.setContentPane(panel);
    editor.getRootPane().setBorder(
        BorderFactory.createLineBorder(editor.getBackground(), 5));
    editor.pack();
    editor.setSize(400, 300);
    editor.setLocationRelativeTo(owner.getFrame());
  }

  /**
   * Updates current dialog chart
   * 
   * @param model
   *          The source model
   */
  public void setChart(StudentModel model) {
    panel.removeAll();
    ChartPanel chart = model.getChartPanel();
    if (chart != null) panel.add(chart);
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
