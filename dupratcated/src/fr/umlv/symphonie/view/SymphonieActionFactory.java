/*
 * This file is part of Symphonie
 * Created : 17 févr. 2005 22:35:15
 */

package fr.umlv.symphonie.view;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;

import fr.umlv.symphonie.data.ConnectionManager;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.model.JuryModel;
import fr.umlv.symphonie.model.TeacherModel;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.wizard.Wizard;

public class SymphonieActionFactory {

  /* FILE MENU ACTIONS ***************************************** */

  /**
   * Creates and action that starts a wizard
   * 
   * @param icon
   *          The action SMALL_ICON
   * @param wizard
   *          The wizard to start
   * @return an <code>AbstractAction</code>
   */
  public static AbstractAction getWizardAction(Icon icon, final Wizard wizard) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        wizard.show();
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  /**
   * Creates an action that exits the main program
   * 
   * @param icon
   *          The action SMALL_ICON
   * @return an <code>AbstractAction</code>
   */
  public static AbstractAction getExitAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        ConnectionManager.closeConnection();
        System.gc();
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

  /* WINDOW ACTIONS ***************************************** */

  /**
   * Creates an action that changes view
   * 
   * @param s
   *          The <code>Symphonie</code> instance
   * @param view
   *          The view to se
   * @return an <code>AbstractAction</code>
   */
  public static AbstractAction getModeChangeAction(final Symphonie s,
      final Symphonie.View view) {
    return new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        s.setCurrentView(view);
      }
    };
  }

  /**
   * Creates an action that changes language
   * 
   * @param resources
   *          The new language map
   * @param builder
   *          The builder a.k.a language manager
   * @return an <code>AbstractAction</code>
   */
  public static AbstractAction getLanguageChangeAction(
      final HashMap<String, String> resources, final ComponentBuilder builder) {
    return new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        builder.setNameMap(resources);
      }
    };
  }

  public static AbstractAction getFormulaAction(Icon icon, final JFrame frame,
      final ComponentBuilder builder) {
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
      final JFrame frame, final ComponentBuilder builder) {
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
      final ComponentBuilder builder) {

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

  public static AbstractAction getDBAction(Icon icon, final JFrame frame,
      final ComponentBuilder builder) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        DatabaseDialog dbd = new DatabaseDialog(frame, builder);
        dbd.setVisible(true);
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getPwdAction(Icon icon, final JFrame frame,
      final ComponentBuilder builder) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        ChangePassDialog cpd = new ChangePassDialog(frame, builder);
        cpd.setVisible(true);
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  /* TEACHER VIEW ACTIONS ********************************** */
  public static AbstractAction getAddMarkAction(Icon icon, final JFrame frame,
      final ComponentBuilder builder) {
    AbstractAction a = new AbstractAction() {

      private final AddMarkDialog amd = new AddMarkDialog(frame, builder);

      public void actionPerformed(ActionEvent e) {
        amd.setModal(true);
        amd.setVisible(true);
      }
    };

    a.putValue(Action.SMALL_ICON, icon);

    return a;
  }

  public static AbstractAction getTeacherAddFormulaAction(Icon icon,
      final JFrame frame, final ComponentBuilder builder) {
    AbstractAction a = new AbstractAction() {

      private final FormulaDialog dialog = new FormulaDialog(frame, builder);

      public void actionPerformed(ActionEvent e) {
        dialog.setModal(true);
        dialog.setVisible(true);
      }
    };

    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public static AbstractAction getRemoveTeacherColumnAction(Icon icon,
      final JTable table) {
    AbstractAction a = new AbstractAction() {

      private Point p;
      private int columnIndex;

      public void actionPerformed(ActionEvent e) {
        p = PointSaver.getPoint();

        if (p != null) {
          columnIndex = table.columnAtPoint(p);
          TeacherModel.getInstance(SQLDataManager.getInstance()).removeColumn(
              columnIndex);
          PointSaver.reset();
        }
      }
    };

    a.putValue(Action.SMALL_ICON, icon);

    return a;
  }

  public static AbstractAction getTeacherUpdateAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        TeacherModel.getInstance(SQLDataManager.getInstance()).update();
      }
    };

    a.putValue(Action.SMALL_ICON, icon);

    return a;
  }

  /* JURY VIEW ACTIONS ************************************* */
  public static AbstractAction getJuryAddFormulaAction(Icon icon,
      final JFrame frame, final ComponentBuilder builder) {
    AbstractAction a = new AbstractAction() {

      private final JuryFormulaDialog dialog = new JuryFormulaDialog(frame,
          builder);

      public void actionPerformed(ActionEvent e) {
        dialog.setModal(true);
        dialog.setVisible(true);
      }
    };

    a.putValue(Action.SMALL_ICON, icon);

    return a;
  }

  public static AbstractAction getRemoveJuryColumnAction(Icon icon,
      final JTable table) {
    AbstractAction a = new AbstractAction() {

      private Point p;
      private int columnIndex;

      public void actionPerformed(ActionEvent e) {
        p = PointSaver.getPoint();

        if (p != null) {
          columnIndex = table.columnAtPoint(p);
          JuryModel.getInstance(SQLDataManager.getInstance()).removeColumn(
              columnIndex);
          PointSaver.reset();
        }
      }
    };

    a.putValue(Action.SMALL_ICON, icon);

    return a;
  }

  public static AbstractAction getJuryUpdateAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        JuryModel.getInstance(SQLDataManager.getInstance()).update();
      }
    };

    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
}
