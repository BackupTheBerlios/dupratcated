/*
 * This file is part of Symphonie Created : 1 mars 2005 23:46:07
 */

package fr.umlv.symphonie.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.umlv.symphonie.util.wizard.WizardPanel;

public class WizardPanelFactory {

  public static WizardPanel getImportPanel() {
    final JPanel p = new JPanel(new GridBagLayout());
    final JLabel l = new JLabel("File");
    final JTextField f = new JTextField();
    final Action browse = new AbstractAction("browse") {

      public void actionPerformed(ActionEvent e) {

      }
    };
    final JButton b = new JButton(browse);

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = GridBagConstraints.RELATIVE;
    c.gridy = GridBagConstraints.RELATIVE;
    c.weighty = 0.0;
    c.weightx = 2.0;
    
    c.fill = GridBagConstraints.EAST;
    p.add(l,c);     
    c.fill = GridBagConstraints.BOTH;
    c.gridwidth = GridBagConstraints.RELATIVE;    
    p.add(f,c);   
    c.fill = GridBagConstraints.WEST;
    c.gridwidth = GridBagConstraints.REMAINDER;
    p.add(b, c);


    final WizardPanel wp = new WizardPanel() {

      public boolean isValid() {
        // Put some data in context
        data.put("help", "There's nothing left to do ...");
        return f.getText().equals(null);
      }

      public boolean canFinish() {
        boolean bool =  (f.getText().equals(null));
        System.out.println("can finish : " + bool);
        return bool;
      }

      public JComponent getPanelComponent() {
        return p;
      }

      public void help() {
      }

      public String getTitle() {
        return "File System";
      }

      public String getDescription() {
        return "Select a file to import";
      }
    };

    ActionListener check = new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        JTextField field = (JTextField) e.getSource();
        if (field.getText().length() == 0) wp.firePanelChanged();
      }
    };

    f.addActionListener(check);
    return wp;
  }

  public static WizardPanel getExportPanel() {
    final JPanel p = new JPanel(new GridBagLayout());
    final JComboBox cb =new JComboBox();
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = GridBagConstraints.RELATIVE;
    c.gridy = GridBagConstraints.RELATIVE;
    c.weighty = 0.0;
    c.weightx = 2.0;
    c.insets.left = 20;
    c.insets.right = 20;
    
    c.gridwidth = GridBagConstraints.RELATIVE;    
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.WEST;
    p.add(new JLabel("Format"),c);

    c.gridwidth = GridBagConstraints.REMAINDER;
    p.add(cb,c);

    final WizardPanel wp = new WizardPanel() {

      public boolean isValid() {
        // Put some data in context
        data.put("help", "There's nothing left to do ...");
        return false;
      }

      public boolean canFinish() {

        return true;
      }

      public JComponent getPanelComponent() {
        return p;
      }

      public void help() {
      }

      public String getTitle() {
        return "File System";
      }

      public String getDescription() {
        return "Select the extension of the file";
      }
    };

    ActionListener check = new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        JTextField field = (JTextField) e.getSource();
        if (field.getText().length() == 0) wp.firePanelChanged();
      }
    };

    cb.addActionListener(check);
    return wp;
  }
  
  public static WizardPanel getLastExportPanel() {
    final JPanel p = new JPanel(new GridBagLayout());
    final JLabel l = new JLabel("File");
    final JTextField f = new JTextField();
    final Action browse = new AbstractAction("browse") {

      public void actionPerformed(ActionEvent e) {

      }
    };
    final JButton b = new JButton(browse);

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = GridBagConstraints.RELATIVE;
    c.gridy = GridBagConstraints.RELATIVE;
    c.weighty = 0.0;
    c.weightx = 2.0;
    
    c.fill = GridBagConstraints.EAST;
    p.add(l,c);     
    c.fill = GridBagConstraints.BOTH;
    c.gridwidth = GridBagConstraints.RELATIVE;    
    p.add(f,c);   
    c.fill = GridBagConstraints.WEST;
    c.gridwidth = GridBagConstraints.REMAINDER;
    p.add(b, c);
    WizardPanel wp = new WizardPanel() {

      public boolean isValid() {
        return true;
      }

      public boolean hasHelp() {
        return true;
      }

      public void help() {
        JOptionPane.showMessageDialog(p, "Previous panel data :\n"
            + data.get("help"));
      }

      public boolean canFinish() {
        return true;
      }

      public String getTitle() {
        return "Save as";
      }

      public String getDescription() {
        return "Don't put the extension of the file";
      }

      public JComponent getPanelComponent() {
        return p;
      }
    };

    return wp;
  }
}
