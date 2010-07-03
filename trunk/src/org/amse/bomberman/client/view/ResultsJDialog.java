package org.amse.bomberman.client.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.amse.bomberman.util.ProtocolConstants;

/**
 *
 * @author Michael Korovkin
 */
public class ResultsJDialog extends JDialog{
    private ResultsTable myTable;
    private final int width = 300;
    private JButton okButton;

    @SuppressWarnings("static-access")
    public ResultsJDialog(JFrame parent, List<String> results) {
        super(parent, "Game Results", true);

        myTable = new ResultsTable(results);

        //System.out.println("height = " + myTable.getRowCount() + " * "
        //        + myTable.getRowHeight() + " + 50");
        int tableHeight = (myTable.getRowCount()+1)*myTable.getRowHeight();

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // BEAUTIFULL setting sizes by incredible number 83 !!!!
        this.setSize(new Dimension(width, tableHeight+83));

        okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        Box mainBox = Box.createVerticalBox();
        mainBox.add(this.createTablePanel());
        mainBox.add(Box.createVerticalGlue());
        mainBox.add(Box.createVerticalStrut(20));
        mainBox.add(okButton);
        mainBox.add(Box.createVerticalStrut(10));

        Container c = getContentPane();
        //c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.add(mainBox);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private JComponent createTablePanel() {
        JPanel tablePanel = new JPanel(new GridLayout(1,0));
        JScrollPane scrollPane = new JScrollPane(myTable);
        tablePanel.add(scrollPane);

        return tablePanel;
    }

    private class ResultsTable extends JTable {

        public ResultsTable(List<String> results) {
            super(new ResultsTableModel(results));
            this.setSizes();
        }

        private void setSizes() {
            this.getTableHeader().setReorderingAllowed(false);
            columnModel.getColumn(0).setPreferredWidth(20);
            columnModel.getColumn(0).setResizable(false);
            columnModel.getColumn(2).setPreferredWidth(20);
            columnModel.getColumn(2).setResizable(false);
            columnModel.getColumn(3).setPreferredWidth(20);
            columnModel.getColumn(3).setResizable(false);
        }
    }

    private class ResultsTableModel extends AbstractTableModel {
        String[] columnNames = {"Place", "Name", "Kills", "Deaths"};
        Object[][] data;

        public ResultsTableModel(List<String> results) {
            data = new Object[results.size()][columnNames.length];
            String[] buf;
            for (int i = 0; i < data.length; i++) {
                buf = results.get(i).split(" ");
                if (buf.length != 3) {
                    throw new IllegalArgumentException("Wrong format of game results:"
                            + results.toString());
                }
                data[i][0] = i+1;
                data[i][1] = buf[0];
                data[i][2] = buf[1];
                data[i][3] = buf[2];
            }
        }
        public int getRowCount() {
            return data.length;
        }
        public int getColumnCount() {
            return columnNames.length;
        }
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
        @Override
        public String getColumnName(int col) {
            return columnNames[col];
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
    }
}
