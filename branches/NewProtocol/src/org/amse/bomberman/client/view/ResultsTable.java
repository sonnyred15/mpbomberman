package org.amse.bomberman.client.view;

import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.amse.bomberman.protocol.ProtocolConstants;

/**
 *
 * @author Mikhail Korovkin
 */
public class ResultsTable extends JTable {

    public ResultsTable() {
        this.setModel(new ResultsTableModel());
        this.setSizes();
        this.setFocusable(false);
        this.setVisible(true);
    }

    public void update(List<String> results) {
        ((ResultsTableModel) this.getModel()).setResults(results);
        this.revalidate();
    }

    private void setSizes() {
        this.getTableHeader().setReorderingAllowed(false);
        final int w = 25;
        columnModel.getColumn(0).setPreferredWidth(w);
        columnModel.getColumn(0).setMinWidth(w);
        columnModel.getColumn(0).setResizable(false);
        columnModel.getColumn(2).setPreferredWidth(w);
        columnModel.getColumn(2).setMinWidth(w);
        columnModel.getColumn(2).setResizable(false);
        columnModel.getColumn(3).setPreferredWidth(w);
        columnModel.getColumn(3).setMinWidth(w);
        columnModel.getColumn(3).setResizable(false);
        columnModel.getColumn(4).setPreferredWidth(w);
        columnModel.getColumn(4).setMinWidth(w);
        columnModel.getColumn(4).setResizable(false);
    }

    private void setResults(List<String> results) {
        ((ResultsTableModel) this.getModel()).setResults(results);
    }

    private class ResultsTableModel extends AbstractTableModel {

        String[] columnNames = {"№", "Name", "Kills", "Deaths", "Frags"};
        Object[][] data;

        public ResultsTableModel() {
            data = new Object[0][columnNames.length];
        }

        public void setResults(List<String> results) {
            data = new Object[results.size()][columnNames.length];
            String[] buf;
            for (int i = 0; i < data.length; i++) {
                buf = results.get(i).split(ProtocolConstants.SPLIT_SYMBOL);
                if (buf.length != 4) {
                    throw new IllegalArgumentException("Wrong format of game results:"
                            + results.toString());
                }
                data[i][0] = i + 1;
                data[i][1] = buf[0];
                data[i][2] = buf[1];
                data[i][3] = buf[2];
                data[i][4] = buf[3];
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
