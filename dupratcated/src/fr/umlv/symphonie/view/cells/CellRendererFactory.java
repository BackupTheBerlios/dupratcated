/*
 * This file is part of Symphonie
 * Created : 8 mars 2005 14:52:30
 */

package fr.umlv.symphonie.view.cells;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public final class CellRendererFactory {

  /** Unmodifiable cell renderer used to retrieve default rendering values */
  private static final DefaultTableCellRenderer DEFAULT = new DefaultTableCellRenderer();

  /**
   * Creates a cell renderer that allows single object formatting for a JTable
   * displaying StudentModels
   * 
   * @return a <code>FormattableCellRenderer</code>
   */
  public static FormattableCellRenderer getStudentModelCellRenderer() {
    return new AbstractFormattableCellRenderer() {

      public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus, int row,
          int column) {
        JLabel label = (JLabel) render.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, column);

        // Reset color values
        label.setForeground(DEFAULT.getForeground());
        label.setBackground(DEFAULT.getBackground());

        if (value == null) return label;

        label.setHorizontalAlignment(SwingConstants.CENTER);
        if (column == 0 || row % 4 == 0)
          label.setFont(render.getFont().deriveFont(Font.BOLD));

        CellFormat format = fObjects.get(value);
        if (format != null && format.getCondition().getValue()) {
          label.setForeground(format.getForeground());
          label.setBackground(format.getBackground());
        }

        return label;
      }
    };
  }

  /**
   * Creates a cell renderer that allows single object formatting for a JTable
   * displaying TeacherModels
   * 
   * @return a <code>FormattableCellRenderer</code>
   */
  public static FormattableCellRenderer getTeacherModelCellRenderer() {
    return new AbstractFormattableCellRenderer() {

      public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus, int row,
          int column) {
        JLabel label = (JLabel) render.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, column);

        // Reset colors
        label.setForeground(DEFAULT.getForeground());
        label.setBackground(DEFAULT.getBackground());

        if (value == null) return label;

        label.setHorizontalAlignment(SwingConstants.CENTER);

        if (column == 0 || row == 0)
          label.setFont(render.getFont().deriveFont(Font.BOLD));

        CellFormat format = fObjects.get(value);
        if (format != null
            && format.getCondition().getValue().equals(Boolean.TRUE)) {
          label.setForeground(format.getForeground());
          label.setBackground(format.getBackground());
        }

        return label;
      }
    };
  }

  /**
   * Creates a cell renderer that allows single object formatting for a JTable
   * displaying JuryModels
   * 
   * @return a <code>FormattableCellRenderer</code>
   */
  public static FormattableCellRenderer getJuryModelCellRenderer() {
    return new AbstractFormattableCellRenderer() {

      public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus, int row,
          int column) {
        JLabel label = (JLabel) render.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, column);

        // Reset colors
        label.setForeground(DEFAULT.getForeground());
        label.setBackground(DEFAULT.getBackground());

        if (value == null) return label;

        label.setHorizontalAlignment(SwingConstants.CENTER);

        if (column == 0 || row == 0
            || column == table.getModel().getColumnCount() - 2)
          label.setFont(render.getFont().deriveFont(Font.BOLD));

        CellFormat format = fObjects.get(value);
        if (format != null
            && format.getCondition().getValue().equals(Boolean.TRUE)) {
          label.setForeground(format.getForeground());
          label.setBackground(format.getBackground());
        }

        return label;
      }
    };
  }
}
