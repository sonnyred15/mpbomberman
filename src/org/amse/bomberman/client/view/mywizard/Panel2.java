package org.amse.bomberman.client.view.mywizard;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.IController;
import org.amse.bomberman.client.control.impl.Controller;

/**
 *
 * @author Michael Korovkin
 */
public class Panel2 extends JPanel implements Updating{
    private final int width = 640;
    private final int height = 480;
    private MyWizard parent;
    private JButton refreshJButton = new JButton();
    private JTable table = new JTable(new MyTableModel());
    private final Dimension buttonSize = new Dimension(200, 40);
    private CreatingGameJPanel createPanel;

    public Panel2(MyWizard jframe){
        this.setSize(width, height);
        parent = jframe;
        createPanel = new CreatingGameJPanel(parent);

        JPanel leftBox = new JPanel();
        // how calculate sizes???
        leftBox.setPreferredSize(new Dimension(200, 240));
        leftBox.setLayout(new FlowLayout());
        leftBox.add(createPanel);
        leftBox.add(refreshJButton);
        this.setSizesTable();

        this.setLayout(new FlowLayout());
        this.add(leftBox);
        JScrollPane jsp = new JScrollPane(table, JScrollPane
                .VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // how calculate sizes???
        jsp.setPreferredSize(new Dimension(width - 200, height - 50));
        this.add(jsp);

        refreshJButton.setAction(new RefreshAction(this));
        this.setVisible(true);
    }
    // !!!!!!!!!!!!!!!!!!!!  is it really need???
    public MyWizard getWizard() {
        return parent;
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

    public void doBeforeShow() {
        try {
            Controller.getInstance().requestGamesList();
            Controller.getInstance().requestMapsList();
        } catch (NetException ex) {
            JOptionPane.showMessageDialog(this,"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            parent.setCurrentJPanel(0);
        }
    }
    public void setMaps(List<String> maps) {
        if (!maps.get(0).equals("No maps on server was founded.")) {
            createPanel.setMaps(maps);
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
    private void join(int gameNumber) {
        IController control = Controller.getInstance();
        try {
            control.requestJoinGame(gameNumber);
            int maxPlayers = this.getSelectedMaxPl();
            if (maxPlayers != -1) {
            } else {
                JOptionPane.showMessageDialog(this, "You did't select the game! "
                        + " Do this and then click join.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NetException ex2) {
            JOptionPane.showMessageDialog(this,"Connection was lost.\n"
                    + ex2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            parent.setCurrentJPanel(0);
        }
    }

    private class RefreshAction extends AbstractAction {
        Panel2 parent;
        public RefreshAction(Panel2 panel) {
            parent = panel;
            putValue(NAME, "Refresh");
            putValue(SHORT_DESCRIPTION, "Refresh information from server");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Controller.getInstance().requestGamesList();
            } catch (NetException ex) {
                JOptionPane.showMessageDialog(parent,"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                getWizard().setCurrentJPanel(0);
            }
        }
    }

    /*
    public static class JoinAction extends AbstractAction {
        ServerInfoJFrame myParent;

        public JoinAction(ServerInfoJFrame jFrame) {
            myParent = jFrame;
            putValue(NAME, "Join");
            putValue(SHORT_DESCRIPTION, "Join to the selected Game");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            int gameNumber = myParent.getSelectedGame();
            if (gameNumber != -1) {
                myParent.join(gameNumber);
            } else {
                JOptionPane.showMessageDialog(myParent, "You did't select the game! "
                        + " Do this and then click join.", "Error", JOptionPane.ERROR_MESSAGE);
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
