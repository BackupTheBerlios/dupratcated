/*
 * This file is part of Symphonie
 * Created : 17 févr. 2005 22:35:15
 */

package fr.umlv.symphonie.view;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import fr.umlv.symphonie.data.ConnectionManager;
import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.model.JuryModel;
import fr.umlv.symphonie.model.StudentModel;
import fr.umlv.symphonie.model.TeacherModel;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.identification.IdentificationException;
import fr.umlv.symphonie.util.identification.IdentificationStrategy;
import fr.umlv.symphonie.util.wizard.Wizard;
import fr.umlv.symphonie.view.cells.CellFormat;
import fr.umlv.symphonie.view.cells.ObjectFormattingSupport;

public class SymphonieActionFactory {

  /** Symphonie instance used by the factory */
  protected final Symphonie symphonie;

  /** The builder for internationalization */
  protected final ComponentBuilder builder;

  public SymphonieActionFactory(Symphonie symphonie, ComponentBuilder builder) {
    this.symphonie = symphonie;
    this.builder = builder;
  }

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
  public AbstractAction getWizardAction(Icon icon, final Wizard wizard) {
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
  public AbstractAction getExitAction(Icon icon) {
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

  /**
   * Creates an action that prints the current symphonie view
   * 
   * @param icon
   *          The action SMALL_ICON
   * @param symph
   *          The symphonie instance
   * @return an AbstractAction
   */
  public AbstractAction getPrintAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        symphonie.getCurrentView().print(symphonie, event);
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
  public AbstractAction getModeChangeAction(final Symphonie.View view) {
    return new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        symphonie.setCurrentView(view);
      }
    };
  }

  /**
   * Creates an action that changes language
   * 
   * @param s
   *          The <code>Symphonie</code> instance
   * @param langue
   *          The language to set
   * @return an <code>AbstractAction</code>
   */
  public AbstractAction getLanguageChangeAction(final Symphonie.Language langue) {
    return new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        symphonie.setCurrentLanguage(langue);
      }
    };
  }

  /**
   * Creates an action that displays the current symphonie view chart
   * 
   * @param icon
   *          An icon for the action
   * @param builder
   *          The builder for internationalization
   * @return an AbstractAction
   */
  public AbstractAction getChartDisplayAction(Icon icon,
      final ComponentBuilder builder) {
    final AbstractAction a = new AbstractAction(null, icon) {

      public void actionPerformed(ActionEvent e) {
        symphonie.getCurrentView().displayChart(symphonie, e);
      }
    };
    a.putValue(Action.SHORT_DESCRIPTION, builder
        .getValue(SymphonieConstants.DISPLAY_CHART));
    builder.addChangeListener(new JLabel(), new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        a.putValue(Action.SHORT_DESCRIPTION, builder
            .getValue(SymphonieConstants.DISPLAY_CHART));
      }
    });
    return a;
  }

  /**
   * Actions that displays a dialog that allows user to conditionally format
   * cells
   * 
   * @param icon
   *          the action SMALL_ICON
   * @return an AbstractAction
   */
  public AbstractAction getFormulaCellAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      private final CellDialog cd = new CellDialog(symphonie, builder);

      public void actionPerformed(ActionEvent event) {
        cd.setVisible(true);
        CellFormat cf = cd.getUserFormat();
        AbstractTableModel atm = null;
        switch (symphonie.getCurrentView().ordinal()) {
          case 0:
            atm = symphonie.getCurrentStudentModel();
            break;
          case 1:
            atm = symphonie.getCurrentTeacherModel();
            break;
          case 2:
            atm = symphonie.getCurrentJuryModel();
            break;
          case 3:
            throw new IllegalStateException("Illegal view number");
        }

        ((ObjectFormattingSupport) atm).getFormattableCellRenderer()
            .addFormatedObject(symphonie.selectedCell.getFirst(), cf);
        Point p = symphonie.selectedCell.getSecond();
        atm.fireTableCellUpdated(p.x, p.y);
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  /**
   * Creates an action that prompts a login dialog
   * 
   * @param icon
   *          The Action SMALL_ACTION
   * @param builder
   *          The builder for internationalization
   * @param is
   *          The loggin service
   * @param s
   *          The symphonie instance
   * @return an AbstractAction
   */
  public AbstractAction getConnectAction(Icon icon,
      final IdentificationStrategy is) {

    AbstractAction a = new AbstractAction() {

      private final Object[] message = { null, new JPasswordField() };
      private final String option[] = new String[2];

      public void actionPerformed(ActionEvent event) {
        /* Datas */
        message[0] = builder
            .getValue(SymphonieConstants.ADMIN_JOPTIONPANE_CONTENT);

        /* Options (buttons' names) */
        option[0] = builder
            .getValue(SymphonieConstants.ADMIN_JOPTIONPANE_BCONNECT);
        option[1] = builder
            .getValue(SymphonieConstants.ADMIN_JOPTIONPANE_BCANCEL);

        int result = JOptionPane.showOptionDialog(symphonie.getFrame(),
            message, builder
                .getValue(SymphonieConstants.ADMIN_JOPTIONPANE_TITLE),
            JOptionPane.DEFAULT_OPTION, // type of dialog
            JOptionPane.QUESTION_MESSAGE, // type of icone
            null, // optional icon
            option, // buttons
            message[1] // object with the default focus
            );

        if (result == 0) {
          String pwd = new String(((JPasswordField) message[1]).getPassword());
          try {
            is.identify(pwd);
          } catch (IdentificationException e) {
            symphonie.errDisplay.showException(e);
          }
          ((JPasswordField) message[1]).setText("");
        }
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  /**
   * Creates an action that logs user out
   * 
   * @param icon
   *          The action SMALL_ICON
   * @return an <code>AbstractAction</code>
   */
  public AbstractAction getLogoutAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        symphonie.getIdentificationStrategy().logout();
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  /**
   * Creates an action that prompts a DB config dialog
   * 
   * @param icon
   *          The SMALL_ICON property
   * @param frame
   *          The frame owner of the dialog
   * @return an AbstractAction
   */
  public AbstractAction getDBAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      private final DatabaseDialog dbd = new DatabaseDialog(symphonie
          .getFrame());

      public void actionPerformed(ActionEvent event) {
        dbd.setVisible(true);
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  /**
   * Creates an Action that prompts a password changing dialog
   * 
   * @param icon
   *          The SMALL_ICON property
   * @param frame
   *          The frame owner of the dialog
   * @param builder
   *          The builder for internationalization
   * @return an AbstractAction
   */
  public AbstractAction getPwdAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      private final ChangePassDialog cpd = new ChangePassDialog(symphonie,
          builder);

      public void actionPerformed(ActionEvent event) {
        cpd.setVisible(true);
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  /* STUDENT VIEW ACTION *********************************** */
  public AbstractAction getStudentUpdateAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        symphonie.getCurrentStudentModel().update();
      }
    };

    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  /**
   * Creates an action that prints the student view. This is a singleton action
   * 
   * @param icon
   *          The action SMALL_ICON
   * @param table
   *          The student table
   * @param s
   *          The symphonie instance
   * @return an AbstractAction
   */
  public AbstractAction getStudentPrintAction(Icon icon, final JTable table) {
    if (studentPrintAction == null) {
      studentPrintAction = new AbstractAction() {

        public void actionPerformed(ActionEvent e) {
          try {
            table.print(JTable.PrintMode.FIT_WIDTH, ((StudentModel) table
                .getModel()).getHeaderMessageFormat(), null);
          } catch (PrinterException e1) {
            symphonie.errDisplay.showException(e1);
          }
        }
      };

      studentPrintAction.putValue(Action.SMALL_ICON, icon);
    }
    return studentPrintAction;
  }

  /** getStudentPrintAction singleton instance */
  protected AbstractAction studentPrintAction;

  /**
   * Creates an action that displays the student view chart. This is a singleton
   * action.
   * 
   * @param icon
   *          The action SMALL_ICON
   * @return an AbstractAction
   */
  public AbstractAction getStudentChartAction(Icon icon) {
    if (studentChartAction == null) {
      studentChartAction = new AbstractAction() {

        private final StudentChartDialog dialog = new StudentChartDialog(
            symphonie);

        public void actionPerformed(ActionEvent e) {
          dialog.setChart(symphonie.getCurrentStudentModel());
          dialog.setModal(true);
          dialog.setVisible(true);
        }
      };
      studentChartAction.putValue(Action.SMALL_ICON, icon);
    }
    return studentChartAction;
  }

  /** getStudentChartAction singleton instance */
  protected AbstractAction studentChartAction;

  public AbstractAction getRemoveStudentAction(Icon icon, final JTree tree) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        Object o = tree.getLastSelectedPathComponent();

        if (o instanceof Student) {
          symphonie.getStudentTreeModel().removeStudent((Student) o);
          symphonie.getCurrentStudentModel().clear();
        }
      }
    };

    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public AbstractAction getAddStudentAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      AddStudentDialog dialog = new AddStudentDialog(symphonie, builder);

      public void actionPerformed(ActionEvent e) {
        dialog.setModal(true);
        dialog.setVisible(true);
      }
    };

    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public AbstractAction getUpdateStudentTreeAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        symphonie.getStudentTreeModel().update();
      }
    };

    a.putValue(Action.SMALL_ICON, icon);

    return a;
  }

  /* TEACHER VIEW ACTIONS ********************************** */

  /**
   * Creates an action that adds a mark column on the teacher model, this is a
   * singleton action
   * 
   * @param icon
   *          The action SMALL_ICON
   * @return an AbstractAction
   */
  public AbstractAction getAddMarkAction(Icon icon) {
    if (addMarkAction == null) {
      addMarkAction = new AbstractAction() {

        private final AddMarkDialog amd = new AddMarkDialog(symphonie, builder);

        public void actionPerformed(ActionEvent e) {
          amd.setModal(true);
          amd.setVisible(true);
        }
      };
      addMarkAction.putValue(Action.SMALL_ICON, icon);
    }
    return addMarkAction;
  }

  /** getAddMarkAction singleton instance */
  protected AbstractAction addMarkAction;

  /**
   * Creates an action that displays a dialog that allows users to add a formula
   * in the teacher view. This is a singleton action.
   * 
   * @param icon
   *          the Action SMALL_ICON
   * @return an AbstractAction
   */
  public AbstractAction getTeacherAddFormulaAction(Icon icon) {
    if (teacherAddFormulaAction == null) {
      teacherAddFormulaAction = new AbstractAction() {

        private final FormulaDialog dialog = new FormulaDialog(symphonie,
            builder);

        public void actionPerformed(ActionEvent e) {
          dialog.setModal(true);
          dialog.setVisible(true);
        }
      };
      teacherAddFormulaAction.putValue(Action.SMALL_ICON, icon);
    }
    return teacherAddFormulaAction;
  }

  /** getTeacherAddFormulaAction singleton instance */
  protected AbstractAction teacherAddFormulaAction;

  /**
   * Creates an action that removes a column
   * 
   * @param icon
   *          the action SMALL_ICON
   * @param table
   *          the table where the columns are to be removed
   * @return an AbstractAction
   */
  public AbstractAction getRemoveTeacherColumnAction(Icon icon,
      final JTable table) {
    AbstractAction a = new AbstractAction() {

      private Point p;
      private int columnIndex;

      public void actionPerformed(ActionEvent e) {
        p = PointSaver.getPoint();

        if (p != null) {
          columnIndex = table.columnAtPoint(p);
          symphonie.getCurrentTeacherModel().removeColumn(columnIndex);
          PointSaver.reset();
        }
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public AbstractAction getTeacherUpdateAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        symphonie.getCurrentTeacherModel().update();
      }
    };

    a.putValue(Action.SMALL_ICON, icon);

    return a;
  }

  /**
   * Creates an action that prints the teacher view. This is a singleton action
   * 
   * @param icon
   *          The action SMALL_ICON
   * @param table
   *          The teacher table
   * @return an AbstractAction
   */
  public AbstractAction getTeacherPrintAction(Icon icon, final JTable table) {
    if (teacherPrintAction == null) {
      teacherPrintAction = new AbstractAction() {

        public void actionPerformed(ActionEvent e) {
          try {
            table.print(JTable.PrintMode.FIT_WIDTH, ((TeacherModel) table
                .getModel()).getHeaderMessageFormat(), null);
          } catch (PrinterException e1) {
            symphonie.errDisplay.showException(e1);
          }
        }
      };

      teacherPrintAction.putValue(Action.SMALL_ICON, icon);
    }
    return teacherPrintAction;
  }

  /** getTeacherPrintAction singleton instance */
  protected AbstractAction teacherPrintAction;

  /**
   * Creates an action that displays the teacher view chart. This is a singleton
   * action.
   * 
   * @param icon
   *          The action SMALL_ICON
   * @return an AbstractAction
   */
  public AbstractAction getTeacherChartAction(Icon icon) {

    if (teacherChartAction == null) {
      teacherChartAction = new AbstractAction() {

        private final TeacherChartDialog dialog = new TeacherChartDialog(
            symphonie);

        public void actionPerformed(ActionEvent e) {
          dialog.setChart(symphonie.getCurrentTeacherModel(), symphonie
              .getChartStep());
          dialog.setModal(true);
          dialog.setVisible(true);
        }
      };

      teacherChartAction.putValue(Action.SMALL_ICON, icon);
    }

    return teacherChartAction;
  }

  /** getTeacherChartAction singleton instance */
  protected AbstractAction teacherChartAction;

  public AbstractAction getAddCourseAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      private final AddCourseDialog dialog = new AddCourseDialog(symphonie,
          builder);

      public void actionPerformed(ActionEvent e) {
        dialog.setModal(true);
        dialog.setVisible(true);
      }
    };

    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public AbstractAction getRemoveCourseAction(Icon icon, final JTree tree) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        Object o = tree.getLastSelectedPathComponent();
        if (o instanceof Course) {
          symphonie.getCourseTreeModel().removeCourse((Course) o);
          symphonie.getCurrentTeacherModel().clear();
        }
      }
    };

    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  public AbstractAction getUpdateCourseTreeAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        symphonie.getCourseTreeModel().update();
      }
    };

    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  /* JURY VIEW ACTIONS ************************************* */

  /**
   * Creates an action that displays a dialog that allows users to add a formula
   * in the jury view. This is a singleton action.
   * 
   * @param icon
   *          the Action SMALL_ICON
   * @return an AbstractAction
   */
  public AbstractAction getJuryAddFormulaAction(Icon icon) {
    if (juryAddFormulaAction == null) {
      juryAddFormulaAction = new AbstractAction() {

        private final JuryFormulaDialog dialog = new JuryFormulaDialog(
            symphonie, builder);

        public void actionPerformed(ActionEvent e) {
          dialog.setModal(true);
          dialog.setVisible(true);
        }
      };

      juryAddFormulaAction.putValue(Action.SMALL_ICON, icon);
    }
    return juryAddFormulaAction;
  }

  /** getJuryPrintAction singleton instance */
  protected AbstractAction juryAddFormulaAction;

  public AbstractAction getRemoveJuryColumnAction(Icon icon, final JTable table) {
    AbstractAction a = new AbstractAction() {

      private Point p;
      private int columnIndex;

      public void actionPerformed(ActionEvent e) {
        p = PointSaver.getPoint();

        if (p != null) {
          columnIndex = table.columnAtPoint(p);
          symphonie.getCurrentJuryModel().removeColumn(columnIndex);
          PointSaver.reset();
        }
      }
    };

    a.putValue(Action.SMALL_ICON, icon);

    return a;
  }

  public AbstractAction getJuryUpdateAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
        symphonie.getCurrentJuryModel().update();
      }
    };

    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

  /**
   * Creates an action that prints the jury view. This is a singleton action
   * 
   * @param icon
   *          The action SMALL_ICON
   * @param table
   *          The jury table
   * @param s
   *          The symphonie instance
   * @return an AbstractAction
   */
  public AbstractAction getJuryPrintAction(Icon icon, final JTable table) {
    if (juryPrintAction == null) {
      juryPrintAction = new AbstractAction() {

        public void actionPerformed(ActionEvent e) {
          try {
            table.print(JTable.PrintMode.FIT_WIDTH, ((JuryModel) table
                .getModel()).getHeaderMessageFormat(), null);
          } catch (PrinterException e1) {
            symphonie.errDisplay.showException(e1);
          }
        }
      };

      juryPrintAction.putValue(Action.SMALL_ICON, icon);
    }
    return juryPrintAction;
  }

  /** getJuryPrintAction singleton instance */
  protected AbstractAction juryPrintAction;

  /**
   * Creates an action that displays the jury view chart. This is a singleton
   * action.
   * 
   * @param icon
   *          The action SMALL_ICON
   * @return an AbstractAction
   */
  public AbstractAction getJuryChartAction(Icon icon) {
    if (juryChartAction == null) {
      juryChartAction = new AbstractAction() {

        private final JuryChartDialog dialog = new JuryChartDialog(symphonie);

        public void actionPerformed(ActionEvent e) {
          dialog.setChart(symphonie.getCurrentJuryModel(), symphonie
              .getChartStep());
          dialog.setModal(true);
          dialog.setVisible(true);
        }
      };
      juryChartAction.putValue(Action.SMALL_ICON, icon);
    }
    return juryChartAction;
  }

  /** getJuryChartAction singleton instance */
  protected AbstractAction juryChartAction;
}
