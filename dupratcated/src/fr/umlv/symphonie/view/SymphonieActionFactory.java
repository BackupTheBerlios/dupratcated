/*
 * This file is part of Symphonie Created : 17 févr. 2005 22:35:15
 */

package fr.umlv.symphonie.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;

import fr.umlv.symphonie.util.SymphonieComponentBuilder;
import fr.umlv.symphonie.util.wizard.DefaultWizardModel;
import fr.umlv.symphonie.util.wizard.Wizard;
import fr.umlv.symphonie.util.wizard.WizardPanel;

public class SymphonieActionFactory {

  /* FILE ACTIONS ***************************************** */
  public static AbstractAction getExitAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        System.exit(0);
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getPrintAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {

      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getImportAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        WizardPanel wp = WizardPanelFactory.getImportPanel();
        DefaultWizardModel dwm = new DefaultWizardModel(wp);
        Wizard wiz = new Wizard(null, dwm, new Dimension(500, 400));
        System.out.println("wiz.show : " + wiz.show());
      }

    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getExportAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        WizardPanel wp = WizardPanelFactory.getExportPanel();
        DefaultWizardModel dwm = new DefaultWizardModel(wp);
        dwm.addPanel(WizardPanelFactory.getLastExportPanel());
        Wizard wiz = new Wizard(null, dwm, new Dimension(500, 400));
        System.out.println("wiz.show : " + wiz.show());

      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  /* WINDOW ACTIONS ***************************************** */
  public static AbstractAction getModeAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getModeChangeAction(String view, ImageIcon icon,final JTabbedPane tab,final int position) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        
        System.out.println(tab.getTitleAt(position));
        tab.setSelectedComponent(tab.getComponentAt(position)); 
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getLanguageAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getLanguageChangeAction(
      final HashMap<String, String> resources,
      final SymphonieComponentBuilder builder) {
    return new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        builder.setNameMap(resources);
      }
    };
  }

  public static AbstractAction getFormulaAction(Icon icon, final JFrame frame,
      final SymphonieComponentBuilder builder) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        FormulaDialog fd = new FormulaDialog(frame, builder);
        fd.setVisible(true);
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getFormulaCellAction(Icon icon,
      final JFrame frame, final SymphonieComponentBuilder builder) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        CellDialog cd = new CellDialog(frame, builder);
        cd.setVisible(true);
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getColumnAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getLineAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getConnectAction(Icon icon,
      final SymphonieComponentBuilder builder) {

    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        /* Datas */
        Object[] message = new Object[2];
        message[0] = builder
            .getValue(SymphonieConstants.ADMIN_JOPTIONPANE_CONTENT);
        message[1] = new JPasswordField();

        /* Options (buttons' names) */
        String option[] = {
            builder.getValue(SymphonieConstants.ADMIN_JOPTIONPANE_BCONNECT),
            builder.getValue(SymphonieConstants.ADMIN_JOPTIONPANE_BCANCEL) };

        int result = JOptionPane.showOptionDialog(null, // parent frame
            message, (builder
                .getValue(SymphonieConstants.ADMIN_JOPTIONPANE_TITLE)),// Title
            JOptionPane.DEFAULT_OPTION, // type of dialog
            JOptionPane.QUESTION_MESSAGE, // type of icone
            null, // optional icone
            option, // buttons
            message[1] // object with the default focus
            );

        if (result == 0) {
          String pwd = new String(((JPasswordField) message[1]).getPassword());
          System.out.println(pwd);
        }
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getDBAction(Icon icon,final JFrame frame, final SymphonieComponentBuilder builder ) {
    AbstractAction a = new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
        DatabaseDialog dbd = new DatabaseDialog(frame, builder);
        dbd.setVisible(true);
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getPwdAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

}
