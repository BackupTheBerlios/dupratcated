/*
 * This file is part of Symphonie
 * Created : 27-févr.-2005 15:26:50
 */
package fr.umlv.symphonie.model;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import fr.umlv.symphonie.data.*;
import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.data.formula.SymphonieFormulaFactory;
import fr.umlv.symphonie.util.*;
import fr.umlv.symphonie.view.PointSaver;
import fr.umlv.symphonie.view.SymphonieActionFactory;
import fr.umlv.symphonie.view.SymphonieConstants;


/**
 * @author susmab
 *
 */
public class JuryModel extends AbstractTableModel {

  protected DataManager manager;
  
  protected int rowCount    = 0;
  protected int columnCount = 0;
  protected int globalTimeStamp = -1; // le globalTimeStamp pourrait etre la somme de
                                    // tous les timeStamp (a voir)
  
  protected final List<Object> columnList = new ArrayList<Object>();
  protected final List<Student> studentList = new ArrayList<Student>();
  protected final Map<Student, Map<Course, Map<Integer, StudentMark>>> dataMap = new HashMap<Student, Map<Course, Map<Integer, StudentMark>>>();
  
  protected final Map<Integer, Course> courseMap = new HashMap<Integer, Course>();

  /**
   * Pool de threads qui n'en contient qu'un seul et qui sert pour le
   * rafraîchissement du canal courant.
   */
  private final ExecutorService es = Executors.newSingleThreadExecutor();
  
  protected final Object lock = new Object();
  
  private static JuryModel instance = null; 
  
  private JuryModel(DataManager manager) {
    this.manager = manager;
    update();
  }
  
  private void setManager(DataManager manager){
    this.manager = manager;
  }
  
  public static JuryModel getInstance(DataManager manager){
    if (instance == null)
      instance = new JuryModel(manager);
    
    else instance.setManager(manager);
    
    return instance;
  }
  
  public void update() {
    
    es.execute(new Runnable(){
      
      /* (non-Javadoc)
       * @see java.lang.Runnable#run()
       */
      public void run() {
        
        synchronized (lock) {
          
          clear();
          
          Pair<Map<Integer, Course>, SortedMap<Student, Map<Course, Map<Integer, StudentMark>>>> allData = null;
          try {
            allData = manager.getAllStudentsMarks();
          } catch (DataManagerException e) {
            System.out.println("Error getting data for Jury View");
            e.printStackTrace();
          }

          columnList.addAll(allData.getFirst().values());
          studentList.addAll(allData.getSecond().keySet());
          dataMap.putAll(allData.getSecond());
          courseMap.putAll(allData.getFirst());

          /*
           * ici ajouter les formules de la vue jury
           */
          List<Formula> formulaList;

          try {
            formulaList = manager.getJuryFormulas();
          } catch (DataManagerException e2) {
            formulaList = null;
          }

          if (formulaList != null) {

            int column;

            for (Formula f : formulaList) {
              column = f.getColumn();
              if (column < 0)
                columnList.add(0, f);

              else if (column > columnList.size())
                columnList.add(f);

              else
                columnList.add(column - 1, f);
            }

          }

          dataMap.putAll(allData.getSecond());

          columnCount = columnList.size() + 4;
          rowCount = 3 + studentList.size();

          /*
           * System.out.println("lignes : " + JuryModel.this.rowCount);
           * System.out.println("colonnes : " + JuryModel.this.columnCount);
           */

          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                JuryModel.this.fireTableStructureChanged();
              }
            });
          } catch (InterruptedException e1) {
            System.out.println("exception interrupted");
            e1.printStackTrace();
          } catch (InvocationTargetException e1) {
            System.out.println("exception invocation");
            e1.printStackTrace();
          }
        }
        /* System.out.println(("thread fini !")); */
      }
    });

  }


  protected void clear(){
    rowCount = 0;
    columnCount = 0;
    columnList.clear();
    studentList.clear();
    dataMap.clear();
  }
  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getRowCount()
   */
  public int getRowCount() {
    return rowCount;
  }

  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  public int getColumnCount() {
    return columnCount;
  }

  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    
    /*
     * cas de la ligne separatrice
     */
    if (rowIndex == 2)
      return null;
    
    /*
     * cas de la colonne tout a gauche
     */
    if (columnIndex == 0){
      if (rowIndex == 0)
        return "Matiere";
      if (rowIndex == 1)
        return "Coeff";
      return studentList.get(rowIndex - 3);
    }
    
    
    /*
     * cas de la colonne tout a droite
     * (commentaires)
     */
    if (columnIndex == columnCount - 1){
      if (rowIndex == 0)
        return "Commentaires";
      if (rowIndex == 1)
        return null;
      
      return studentList.get(rowIndex - 3).getComment();
    }
    
    
    /*
     * cas de l'avant derniere colonne
     * (etudiants)
     */
    if (columnIndex == columnCount - 2){
      if (rowIndex <= 1)
        return null;
      
      return studentList.get(rowIndex - 3);
    }
    
    
    /*
     * cas de la colonne des moyennes
     * (avant avant derniere colonne)
     */
    
    if (columnIndex == columnCount -3){
      if (rowIndex == 0)
        return "Moyenne";
      if (rowIndex <= 2)
        return null;
      
      float result = 0;
      
      for (int i = 0; i < columnList.size() ; i++){
        if (columnList.get(i) instanceof Course)
          result += ((Course)columnList.get(i)).getCoeff() * (Float)getValueAt(rowIndex, i + 1);
      }
      return result;
    }
    
    
    /*
     * autres cas
     */
    Object o = columnList.get(columnIndex - 1);
    
    if (o instanceof Formula){
      Formula f = (Formula)o;
      
      if (rowIndex == 0)
        return f.getDescription();
      
      if (rowIndex == 1)
        return null;
      
      SymphonieFormulaFactory.clearMappedValues();
      
      Student s = studentList.get(rowIndex - 3);
      
      for (Course c : dataMap.get(s).keySet()){
        SymphonieFormulaFactory.putMappedValue(c.getTitle(), getAverage(dataMap.get(s).get(c).values()));
      }
      
      return f.getValue();
    }
    
    Course c = (Course)o;
    
    if(rowIndex == 0)
      return c;
    if(rowIndex == 1)
      return c.getCoeff();
    
    return getAverage(dataMap.get(studentList.get(rowIndex - 3)).get(c).values());
  }

  private float getAverage(Collection<StudentMark> name) {
    
    float result = 0;
    
    for (StudentMark sm : name){
      result += sm.getValue() * sm.getCoeff();
    }
    
    return result;
  }

  
  public boolean isCellEditable(int rowIndex,int columnIndex){
    if (columnIndex == columnCount - 1 && rowIndex > 2)
      return true;

    return false;
  }

  
  public void setValueAt(Object aValue,int rowIndex,int columnIndex){
    
    if (columnIndex != columnCount -1)
      return;
    
    Student s = studentList.get(rowIndex - 3);
    
    try {
      manager.changeStudentComment(s, (String)aValue);
    } catch (DataManagerException e) {
      e.getMessage();
      e.printStackTrace();
    }
  }
  
  public void removeColumn(int columnIndex){
    
    if (columnIndex >= columnCount - 3) return;

    Object o = columnList.get(columnIndex - 1);

    if (o instanceof Formula) {
      es.execute(new Runnable() {

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {

          synchronized (lock) {
            
            
            
            
            try {
              EventQueue.invokeAndWait(new Runnable() {

                public void run() {
                  JuryModel.this.fireTableStructureChanged();
                }
              });
            } catch (InterruptedException e1) {
              System.out.println("exception interrupted");
              e1.printStackTrace();
            } catch (InvocationTargetException e1) {
              System.out.println("exception invocation");
              e1.printStackTrace();
            }
          }
        }
      });
    }

  }

  public boolean isColumnFormula(int columnIndex){
    if (columnIndex == 0 
        || columnIndex >= columnCount -3)
      return false;
    
    Object o = columnList.get(columnIndex -1);
    
    if (o instanceof Formula)
      return true;
    
    return false;
  }
  
  public MessageFormat getHeaderMessageFormat(){
    return new MessageFormat("Ensemble des moyennes des etudiants et deliberes");
  }
  
  
  /* (non-Javadoc)
   * @see fr.umlv.symphonie.view.charts.SymphonieChartFactory#createJuryPanel()
   */
  public ChartPanel getChartPanel(int step){
    
    Map<Integer, Integer>[] dataTab;
    
    
    int size = (20%step == 0 ? 20/step : 20/step + 1);
    
    dataTab = new Map[size];
    
    initDataTabForJury(dataTab, courseMap);
    
    for (Map<Course, Map<Integer, StudentMark>> map : dataMap.values()){
      for (Course c : map.keySet()){
        int index = (int)getAverage(map.get(c).values()) / step;
        
        if (index >= size)
          index = size -1;
        
        int previousValue = dataTab[index].get(c.getId());
        dataTab[index].put(c.getId(), previousValue + 1);
      }
    }
    
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    
    int low = 0;
    int hi = step;
    
    for (Map<Integer, Integer> map : dataTab){
      for (int courseId : map.keySet()){
        dataset.addValue(map.get(courseId), courseMap.get(courseId).getTitle(), "de " + low + " a " + hi);
      }
      
      low += step;
      hi += step;
    }
    
    JFreeChart barChart = ChartFactory.createBarChart3D("Moyennes des etudiants", "intervalles de notes", 
        "nombre d'eleves", dataset, PlotOrientation.VERTICAL, true, true, true);
    barChart.getPlot().setForegroundAlpha(0.65f);
    
    return new ChartPanel(barChart);
  }

  /**
   * @param dataTab
   * @param courseMap
   */
  private void initDataTabForJury(Map<Integer, Integer>[] dataTab, Map<Integer, Course> courseMap) {
    
    for (int i = 0 ; i < dataTab.length ; i++){
      dataTab[i] = new HashMap<Integer, Integer>();
    }
    
    for (Map<Integer, Integer> map : dataTab){
      for (int i : courseMap.keySet()){
        map.put(i, 0);
      }
    }
  }
  
  
  
  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    JFrame frame = new JFrame ("test JuryModel");
    frame.setSize(800,600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    DataManager dataManager = SQLDataManager.getInstance();

    final JuryModel model = JuryModel.getInstance(dataManager);
    
    final JTable table = new JTable(model);
    table.setTableHeader(null);
    
    
    HashMap<String, String> map = TextualResourcesLoader.getResourceMap("language/symphonie", new Locale(
    "french"), "ISO-8859-1");
    
    ComponentBuilder builder = new ComponentBuilder(map);
    
    // popup and buttons
    final JPopupMenu pop = builder.buildPopupMenu(SymphonieConstants.JURYVIEWPOPUP_TITLE);
    
    pop.add(builder.buildButton(SymphonieActionFactory.getJuryAddFormulaAction(null, frame, builder),SymphonieConstants.ADD_FORMULA, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(SymphonieActionFactory.getJuryUpdateAction(null), SymphonieConstants.UPDATE, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(SymphonieActionFactory.getJuryPrintAction(null, table), SymphonieConstants.PRINT_MENU_ITEM, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(SymphonieActionFactory.getJuryChartAction(null, frame), SymphonieConstants.DISPLAY_CHART, ComponentBuilder.ButtonType.MENU_ITEM));
    
    final AbstractButton removeColumn = builder.buildButton(SymphonieActionFactory.getRemoveJuryColumnAction(null, table), SymphonieConstants.REMOVE_COLUMN, ComponentBuilder.ButtonType.MENU_ITEM);
    pop.add(removeColumn);
    
    // listener which displays the popup
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          pop.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });
    
    // listener which saves the cursor location
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          PointSaver.setPoint(e.getPoint());
        }
      }
    });
    
    // listener which disables buttons
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          if (model.isColumnFormula(table.columnAtPoint(e.getPoint())))
            removeColumn.setEnabled(true);
          else removeColumn.setEnabled(false);
        }
      }
    });
    
    
    
    
    
    
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
      
      public java.awt.Component getTableCellRendererComponent(JTable table,Object value,
          boolean isSelected,boolean hasFocus,int row,int column){
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
          label.setHorizontalAlignment(SwingConstants.CENTER);
          
          if (column == 0 || row == 0 || column == table.getModel().getColumnCount() - 2)
            label.setFont(getFont().deriveFont(Font.BOLD));
          
          return label;
      }
    });
    
    
    JScrollPane scroll = new JScrollPane(table);
    
    frame.setContentPane(scroll);
    
    frame.setVisible(true);
  }

}
