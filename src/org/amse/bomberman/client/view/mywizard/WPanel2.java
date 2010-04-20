package org.amse.bomberman.client.view.mywizard;

import java.awt.CardLayout;
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
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
/**
 *
 * @author Michael Korovkin
 */
public class WPanel2 extends JPanel{
    private final int width = 640;
    private final int height = 480;
    //private JButton refreshJButton = new JButton();
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
    /*private void join(int gameNumber) throws NetException {
        IController control = Controller.getInstance();
        control.requestJoinGame(gameNumber);
        int maxPlayers = this.getSelectedMaxPl();
        if (maxPlayers != -1) {
        } else {
            JOptionPane.showMessageDialog(this, "You did't select the game! "
                + " Do this and then click join.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }*/

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
        radioBox.setPreferredSize(new Dimension(120, 50));
        radioBox.add(createButton);
        radioBox.add(joinButton);

        // createPanel
        creatingPanel = new WCreatingGameJPanel();
        JPanel createPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 150));
        createPanel.add(creatingPanel);

        // JoinPanel
        JPanel joinPanel = new JPanel();
        JScrollPane jsp = new JScrollPane(table, JScrollPane
                .VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        joinPanel.add(jsp);
        //joinPanel.add(refreshJButton);
        jsp.setPreferredSize(new Dimension(width - 150, height - 100));
        joinPanel.setPreferredSize(new Dimension(width - 150, height - 50));
        //refreshJButton.setAction(new RefreshAction(this));

        // mainPanel - cardLayout with createPanel and joinPanel
        mainPanel.setLayout(cardLayout);
        mainPanel.add(createPanel, CREATE_NAME);
        mainPanel.add(joinPanel, JOIN_NAME);

        // add radioPanel and mainPanel
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
        this.add(radioBox);
        this.add(mainPanel);
    }

    /*private class RefreshAction extends AbstractAction {
        WPanel2 parent;
        public RefreshAction(WPanel2 panel) {
            parent = panel;
            putValue(NAME, "Refresh");
            putValue(SHORT_DESCRIPTION, "Refresh information from server");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Controller.getInstance().requestGamesList();
            } catch (NetException ex) {
                /*JOptionPane.showMessageDialog(parent,"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                getWizard().setCurrentJPanel(0);
            }
        }
    }*/

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
