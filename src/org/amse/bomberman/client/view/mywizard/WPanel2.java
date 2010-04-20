package org.amse.bomberman.client.view.mywizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
/**
 *
 * @author Michael Korovkin
 */
public class WPanel2 extends JPanel{
    private final int width = 640;
    private final int height = 480;
    private JTable table;
    private WCreatingGameJPanel creatingPanel;
    private JPanel mainPanel = new JPanel();
    private CardLayout cardLayout = new CardLayout();
    JRadioButton createButton;
    JRadioButton joinButton;
    public static final String CREATE_NAME = "New game";
    public static final String JOIN_NAME = "Join game";

    public WPanel2(){
        this.setSize(width, height);
        initComponents();
        this.setVisible(true);
    }

    public String getState() {
        if (createButton.isSelected()) {
            return CREATE_NAME;
        } else return JOIN_NAME;
    }
    public int getSelectedMaxPl() {
        int result = -1;
        if (table.getSelectedRow() != -1
                && table.getValueAt(table.getSelectedRow(), 4) != null) {
            result = Integer.parseInt(
                    (String) table.getValueAt(table.getSelectedRow(), 4));
            return result;
        } else {
            return result;
        }
    }
    public int getSelectedGame() {
        int result = -1;
        if (table.getSelectedRow() != -1
                && table.getValueAt(table.getSelectedRow(), 0) != null) {
            result = Integer.parseInt(
                    (String) table.getValueAt(table.getSelectedRow(), 0));
            return result;
        } else {
            return result;
        }
    }

    public void setMaps(List<String> maps) {
        if (!maps.get(0).equals("No maps on server was founded.")) {
            creatingPanel.setMaps(maps);
        }
    }
    public void setGames(List<String> games) {
        MyTableModel tableModel = (MyTableModel) table.getModel();
        tableModel.clear();
        if (!games.get(0).equals("No unstarted games finded.")) {
            int counter = 0;
            for (String game : games) {
                String[] buf = game.split(" ");
                table.setValueAt(buf[0], counter, 0);
                table.setValueAt(buf[1], counter, 1);
                table.setValueAt(buf[2], counter, 2);
                table.setValueAt(buf[3], counter, 3);
                table.setValueAt(buf[4], counter, 4);
                counter++;
            }
        }
        table.repaint();
    }
    public String getMap() {
        return this.creatingPanel.getMap();
    }
    public String getGameName() {
        return this.creatingPanel.getGameName();
    }
    public int getMaxPlayers() {
        return this.creatingPanel.getMaxPlayers();
    }

    private void setSizesTable() {
        table.getTableHeader().setReorderingAllowed(false);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(40);
        columnModel.getColumn(0).setMinWidth(40);
        columnModel.getColumn(1).setWidth(150);
        columnModel.getColumn(1).setMinWidth(100);
        columnModel.getColumn(2).setWidth(150);
        columnModel.getColumn(2).setMinWidth(50);
        columnModel.getColumn(3).setMaxWidth(50);
        columnModel.getColumn(3).setMinWidth(30);
        columnModel.getColumn(4).setMaxWidth(100);
        columnModel.getColumn(4).setMinWidth(50);
        columnModel.getColumn(0).setResizable(false);
        columnModel.getColumn(3).setResizable(false);
        columnModel.getColumn(4).setResizable(false);
    }

    private void initComponents() {
        // initialization of MyTable with list of games
        table = new JTable(new MyTableModel());
        this.setSizesTable();

        // create JRadioButtons and set Actions to it
        createButton = new JRadioButton("New game");
        createButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, CREATE_NAME);
            }
        });
        joinButton = new JRadioButton("Join game");
        joinButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, JOIN_NAME);
            }
        });

        // Button group to select type - create new game or join to the created game
        ButtonGroup selectGroup = new ButtonGroup();
        selectGroup.add(createButton);
        selectGroup.add(joinButton);
        createButton.setSelected(true);
        Box radioBox = Box.createVerticalBox();
        radioBox.add(Box.createVerticalStrut(10));
        radioBox.add(createButton);
        radioBox.add(Box.createVerticalStrut(10));
        radioBox.add(joinButton);
        radioBox.setPreferredSize(new Dimension(width-50, 80));
        radioBox.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        // createPanel
        creatingPanel = new WCreatingGameJPanel();
        JPanel createPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
        createPanel.add(creatingPanel);

        // JoinPanel
        JPanel joinPanel = new JPanel();
        JScrollPane jsp = new JScrollPane(table, JScrollPane
                .VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        joinPanel.add(jsp);
        //joinPanel.add(refreshJButton);
        jsp.setPreferredSize(new Dimension(width - 150, height - 200));
        joinPanel.setPreferredSize(new Dimension(width - 150, height - 150));

        // mainPanel - cardLayout with createPanel and joinPanel
        mainPanel.setLayout(cardLayout);
        mainPanel.add(createPanel, CREATE_NAME);
        mainPanel.add(joinPanel, JOIN_NAME);

        // leftPanel
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(30,100));
        leftPanel.add(new JSeparator());

        // add radioPanel and mainPanel
        Box mainBox = Box.createVerticalBox();
        mainBox.add(radioBox);
        mainBox.add(mainPanel);
        //this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.add(leftPanel, BorderLayout.WEST);
        this.add(radioBox, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
        //this.add(mainBox);
    }

    private class MyTableModel extends AbstractTableModel {
        String[] columnNames = {"ID", "Name", "Map", "Players", "maxPlayers"};
        Object[][] data = new Object[50][5];

        public int getRowCount() {
            return data.length;
        }
        public int getColumnCount() {
            return columnNames.length;
        }
        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
        @Override
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
        public void clear() {
            data = new Object[50][5];
        }
    }
}
