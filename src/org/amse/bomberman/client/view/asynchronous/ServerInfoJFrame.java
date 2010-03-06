package org.amse.bomberman.client.view.asynchronous;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Michail Korovkin
 */
public class ServerInfoJFrame extends JFrame {

    public static final long serialVersionUID = 1L;

    private final int width = 500;
    private final int height = 375;
    private JButton createJButton = new JButton();
    private JButton joinJButton = new JButton();
    private JButton refreshJButton = new JButton();
    private JTable table = new JTable(new MyTableModel());
    private final Dimension buttonSize = new Dimension(200, 40);//PENDING useless?
    private final Controller controller;

    public ServerInfoJFrame(Controller controller) {
        super("ServerInfo");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(400, 150);
        setMinimumSize(new Dimension(width / 2, height / 2));

        this.controller = controller;

        JPanel leftBox = new JPanel();
        // how calculate sizes???
        leftBox.setPreferredSize(new Dimension(100, 120));
        leftBox.setLayout(new GridLayout(4, 1, 10, 10));
        leftBox.add(createJButton);
        leftBox.add(joinJButton);
        leftBox.add(refreshJButton);

        this.setSizesTable();

        Container c = getContentPane();
        c.setLayout(new FlowLayout());
        c.add(leftBox);
        JScrollPane jsp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // how calculate sizes???
        jsp.setPreferredSize(new Dimension(width - 120, height - 50));
        c.add(jsp);

        refreshJButton.setAction(new RefreshAction(this));
        createJButton.setAction(new CreateAction(this));
        joinJButton.setAction(new JoinAction(this));
        setResizable(false);
        setVisible(false);
    }

    public void refreshTable(List<String> games) {
        MyTableModel tableModel = (MyTableModel) table.getModel();
        if (games != null) {
            int counter = 0;
            tableModel.clear();
            for (String game : games) {
                String[] buf = game.split(" ");
                table.setValueAt(buf[0], counter, 0);
                table.setValueAt(buf[1], counter, 1);
                table.setValueAt(buf[2], counter, 2);
                table.setValueAt(buf[3], counter, 3);
                table.setValueAt(buf[4], counter, 4);
                counter++;
            }
            table.repaint();
        }else{ //games == null true
            tableModel.clear();
        }
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
            this.controller.requestJoinGame(gameNumber);
  /*
            int maxPlayers = this.getSelectedMaxPl();
            if (maxPlayers != -1) {
                GameInfoJFrame jframe = new GameInfoJFrame(gameNumber, maxPlayers);
            } else {
                JOptionPane.showMessageDialog(this, "You did't select the game! " + " Do this and then click join.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Can not join to the game: \n"
        + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        } catch (NetException ex2) {
//            JOptionPane.showMessageDialog(this, "Connection was lost.\n" + ex2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            this.dispose();
//            StartJFrame jFrame = new StartJFrame();
//        }*/
    }

    private int getSelectedMaxPl() {
        int result = -1;
        if (table.getSelectedRow() != -1 && table.getValueAt(table.getSelectedRow(), 4) != null) {
            result = Integer.parseInt(
                    (String) table.getValueAt(table.getSelectedRow(), 4));
            return result;
        } else {
            return result;
        }
    }

    private int getSelectedGame() {
        int result = -1;
        if (table.getSelectedRow() != -1 && table.getValueAt(table.getSelectedRow(), 0) != null) {
            result = Integer.parseInt(
                    (String) table.getValueAt(table.getSelectedRow(), 0));
            return result;
        } else {
            return result;
        }
    }

    public static class RefreshAction extends AbstractAction {

        public static final long serialVersionUID = 1L;

        ServerInfoJFrame parent;

        public RefreshAction(ServerInfoJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Refresh");
            putValue(SHORT_DESCRIPTION, "Refresh information from server");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            parent.controller.requestGamesList();
        }
    }

    public class CreateAction extends AbstractAction {

        public static final long serialVersionUID = 1L;

        ServerInfoJFrame parent;

        public CreateAction(ServerInfoJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Create");
            putValue(SHORT_DESCRIPTION, "Create new Game");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            controller.showCreateGameFrame();
        }
    }

    public static class JoinAction extends AbstractAction {

        public static final long serialVersionUID = 1L;

        ServerInfoJFrame parent;

        public JoinAction(ServerInfoJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Join");
            putValue(SHORT_DESCRIPTION, "Join to the selected Game");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            int gameNumber = parent.getSelectedGame();
            if (gameNumber != -1) {
                parent.join(gameNumber);
            } else {
                JOptionPane.showMessageDialog(parent, "You didn't select the game! " + " Do this and then click join.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class MyTableModel extends AbstractTableModel {

        public static final long serialVersionUID = 1L;

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
