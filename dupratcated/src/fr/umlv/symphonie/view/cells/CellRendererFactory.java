/*
 * This file is part of Symphonie
 * Created : 8 mars 2005 14:52:30
 */

package fr.umlv.symphonie.view.cells;

import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import fr.umlv.symphonie.model.StudentModel;

public final class CellRendererFactory {

  private static final DefaultTableCellRenderer DEFAULT = new DefaultTableCellRenderer();

  public static TableCellRenderer getTeacherModelCellRenderer(
      final HashMap<Object, CellFormat> formattedCells) {
    return new DefaultTableCellRenderer() {

      public java.awt.Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus, int row,
          int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, column);

        label.setHorizontalAlignment(SwingConstants.CENTER);

        if (column == 0 || row == 0)
          label.setFont(getFont().deriveFont(Font.BOLD));

        CellFormat format = formattedCells.get(value);
        if (format != null) {
          if (format.getCondition().getValue().equals(Boolean.TRUE)) {
            label.setForeground(format.getForeground());
            label.setBackground(format.getBackground());
          }
        } else {
          label.setForeground(DEFAULT.getForeground());
          label.setBackground(DEFAULT.getBackground());
        }

        return label;
      }
    };
  }

  public static FormattableCellRenderer getStudentModelCellRenderer(
      final StudentModel model) {
    return new FormattableCellRenderer() {

      private final Hashtable<Object, CellFormat> fObjects = new Hashtable<Object, CellFormat>();

      public void addFormatedObject(Object o, CellFormat format) {
        if (o == null || format == null) return;

        fObjects.put(o, format);
      }

      public CellFormat removeFormatedObject(Object o) {
        return fObjects.remove(o);
      }

      private final DefaultTableCellRenderer render = new DefaultTableCellRenderer();

      public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus, int row,
          int column) {
        JLabel label = (JLabel) render.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, column);

        if (value == null) return label;

        label.setHorizontalAlignment(SwingConstants.CENTER);
        if (column == 0 || row % 4 == 0) {
          label.setFont(render.getFont().deriveFont(Font.BOLD));
          return label;
        }

        CellFormat format = fObjects.get(value);
        if (format != null) {
          if (format.getCondition().getValue()) {
            label.setForeground(format.getForeground());
            label.setBackground(format.getBackground());
          }
        } else {
          label.setForeground(DEFAULT.getForeground());
          label.setBackground(DEFAULT.getBackground());
        }

        return label;
      }

      public TableModel getModel() {
        return model;
      }
    };
  }
}
