/*
 * This file is part of Symphonie
 * Created : 21 mars 2005 17:02:48
 */
package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.util.ComponentBuilder;


/**
 * The admin model which represents jury's view.
 * @author fvallee
 *
 */
public class AdminJuryModel extends JuryModel {
  
  
  /**
   * Constructs an empty <code>AdminJuryModel</code>
   * @param manager The <code>DataManager</code> which will be used to interact with database.
   * @param builder The <code>ComponentBuilder</code> which will provide internationalization.
   */
  public AdminJuryModel(DataManager manager, ComponentBuilder builder){
    super(manager, builder);
  }
  
  
  /**
   * Tells if a cell is editable or not.
   * In admin mode, you can edit students' names, courses' titles, courses' coefficients and comments.
   * @see fr.umlv.symphonie.model.JuryModel#isCellEditable(int, int)
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    
    if ((columnIndex == 0 || columnIndex == columnCount - 2)
        && rowIndex >= 3)
      return true;
    
    if (columnIndex > 0 && columnIndex < columnCount -2){
      if (rowIndex == 0)
        return true;
      
      if (rowIndex == 1 && columnList.get(columnIndex -1) instanceof Course)
        return true;
      
      return false;
    }

    return super.isCellEditable(rowIndex, columnIndex);
  }
  
  
  /**
   * Sets value at a given cell.
   * @see fr.umlv.symphonie.model.JuryModel#setValueAt(java.lang.Object, int, int)
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    
    final int row = rowIndex;
    
    if ((columnIndex == 0 || columnIndex == columnCount - 2)
        && rowIndex >= 3){
      final Student s = studentList.get(rowIndex - 3);
      
      StringTokenizer tokenizer = new StringTokenizer((String)aValue, ":");
      
      final String lastName = tokenizer.nextToken();
      if (tokenizer.hasMoreElements() == false)
        return;
      final String name = tokenizer.nextToken();
      
      es.execute(new Runnable(){
        public void run(){
          try {
            manager.changeStudentLastName(s, lastName);
            manager.changeStudentName(s, name);
          }catch (DataManagerException e){
            System.out.println(e.getMessage());
          }
          
          try {
            EventQueue.invokeAndWait(new Runnable() {
              public void run() {
                AdminJuryModel.this.fireTableRowsUpdated(row, row);
              }
            });
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          } catch (InvocationTargetException e1) {
            e1.printStackTrace();
          }
        }
      });
      
      return;
    
    }
    
    if (columnIndex > 0 && columnIndex < columnCount -2){
      final Object o = columnList.get(columnIndex - 1);
      
      if (o instanceof Formula){
        
      }
      
      if (row == 0){
        final String value = (String)aValue;
        if (value.equals("") == false){
          es.execute(new Runnable(){
            public void run(){
              try {
                manager.changeCourseTitle((Course)o, value);
              }catch (DataManagerException e){
                System.out.println(e.getMessage());
              }
              
              try {
                EventQueue.invokeAndWait(new Runnable() {
                  public void run() {
                    AdminJuryModel.this.fireTableRowsUpdated(row, row);
                  }
                });
              } catch (InterruptedException e1) {
                e1.printStackTrace();
              } catch (InvocationTargetException e1) {
                e1.printStackTrace();
              }
            }
          });
        }
      }
      
      if (rowIndex == 1){
        final float value;
        
        try{
          value = Float.parseFloat((String)aValue);
        }catch(NumberFormatException e){
          return;
        }
        
        es.execute(new Runnable(){
          public void run(){
            try {
              manager.changeCourseCoeff((Course)o, value);
            }catch (DataManagerException e){
              System.out.println(e.getMessage());
            }
            
            try {
              EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                  AdminJuryModel.this.fireTableRowsUpdated(row, rowCount - 1);
                }
              });
            } catch (InterruptedException e1) {
              e1.printStackTrace();
            } catch (InvocationTargetException e1) {
              e1.printStackTrace();
            }
          }
        });
      }
    }
    
    
    
    
    
    super.setValueAt(aValue, rowIndex, columnIndex);
  }
  
  
  
  
//  public static void main(String[] args) throws IOException {
//    JFrame frame = new JFrame ("test JuryModel");
//    frame.setSize(800,600);
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    
//    DataManager dataManager = SQLDataManager.getInstance();
//
//    final JuryModel model = AdminJuryModel.getInstance(dataManager);
//    
//    final JTable table = new JTable(model);
//    table.setTableHeader(null);
//    
//    
//    HashMap<String, String> map = TextualResourcesLoader.getResourceMap("language/symphonie", new Locale(
//    "french"), "ISO-8859-1");
//    
//    ComponentBuilder builder = new ComponentBuilder(map);
//    
//    // popup and buttons
//    final JPopupMenu pop = builder.buildPopupMenu(SymphonieConstants.JURYVIEWPOPUP_TITLE);
//    
//    pop.add(builder.buildButton(SymphonieActionFactory.getJuryAddFormulaAction(null, frame, builder),SymphonieConstants.ADD_FORMULA, ComponentBuilder.ButtonType.MENU_ITEM));
//    pop.add(builder.buildButton(SymphonieActionFactory.getJuryUpdateAction(null), SymphonieConstants.UPDATE, ComponentBuilder.ButtonType.MENU_ITEM));
//    pop.add(builder.buildButton(SymphonieActionFactory.getJuryPrintAction(null, table), SymphonieConstants.PRINT_MENU_ITEM, ComponentBuilder.ButtonType.MENU_ITEM));
//    pop.add(builder.buildButton(SymphonieActionFactory.getJuryChartAction(null, frame), SymphonieConstants.DISPLAY_CHART, ComponentBuilder.ButtonType.MENU_ITEM));
//    
//    final AbstractButton removeColumn = builder.buildButton(SymphonieActionFactory.getRemoveJuryColumnAction(null, table), SymphonieConstants.REMOVE_COLUMN, ComponentBuilder.ButtonType.MENU_ITEM);
//    pop.add(removeColumn);
//    
//    // listener which displays the popup
//    table.addMouseListener(new MouseAdapter() {
//
//      public void mousePressed(MouseEvent e) {
//        if (SwingUtilities.isRightMouseButton(e)) {
//          pop.show(e.getComponent(), e.getX(), e.getY());
//        }
//      }
//    });
//    
//    // listener which saves the cursor location
//    table.addMouseListener(new MouseAdapter() {
//
//      public void mousePressed(MouseEvent e) {
//        if (SwingUtilities.isRightMouseButton(e)) {
//          PointSaver.setPoint(e.getPoint());
//        }
//      }
//    });
//    
//    // listener which disables buttons
//    table.addMouseListener(new MouseAdapter() {
//
//      public void mousePressed(MouseEvent e) {
//        if (SwingUtilities.isRightMouseButton(e)) {
//          if (model.isColumnFormula(table.columnAtPoint(e.getPoint())))
//            removeColumn.setEnabled(true);
//          else removeColumn.setEnabled(false);
//        }
//      }
//    });
//    
//    
//    
//    
//    
//    
//    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
//      
//      public java.awt.Component getTableCellRendererComponent(JTable table,Object value,
//          boolean isSelected,boolean hasFocus,int row,int column){
//        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        
//          label.setHorizontalAlignment(SwingConstants.CENTER);
//          
//          if (column == 0 || row == 0 || column == table.getModel().getColumnCount() - 2)
//            label.setFont(getFont().deriveFont(Font.BOLD));
//          
//          return label;
//      }
//    });
//    
//    
//    JScrollPane scroll = new JScrollPane(table);
//    
//    frame.setContentPane(scroll);
//    
//    frame.setVisible(true);
//  }
}
