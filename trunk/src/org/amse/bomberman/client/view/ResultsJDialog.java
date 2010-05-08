package org.amse.bomberman.client.view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Michael Korovkin
 */
public class ResultsJDialog extends JDialog{
    private ResultsTable myTable;
    private final int width = 300;
    private final int height = 100;

    public ResultsJDialog(List<String> results) {
        super((JDialog)null, "Game Results", true);
        myTable = new ResultsTable(results);
        myTable.setPreferredScrollableViewportSize(new Dimension(500, 100));
        myTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(myTable);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(width, height));
        this.setLocationByPlatform(true);

        this.setLayout(new GridLayout(1,0));
        this.add(scrollPane);
        this.setVisible(true);
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
    }
}
