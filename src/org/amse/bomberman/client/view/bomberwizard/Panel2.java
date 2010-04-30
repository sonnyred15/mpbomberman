package org.amse.bomberman.client.view.bomberwizard;

import org.amse.bomberman.client.view.wizard.WizardController;
import org.amse.bomberman.client.view.wizard.WizardEvent;
import org.amse.bomberman.util.Creator;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Michael Korovkin
 */
public class Panel2 extends JPanel{
    private final int width = 640;
    private final int height = 480;
    private Color foreground = new Color(255, 100, 100);

    private Image image;
    private static final String BACKGROUND_PATH = "/org/amse/bomberman/client" +
            "/view/resources/cover2.png";
    private static final URL BACKGROUND_URL = Panel2.class.getResource(BACKGROUND_PATH);

    private JPanel joinPanel;
    private CreatingGameJPanel createPanel;

    private JTable table;
    private JScrollPane jsp;

    private JRadioButton createButton;
    private JRadioButton joinButton;
    public static final String CREATE_NAME = "New game";
    public static final String JOIN_NAME = "Join game";

    public Panel2(){
        this.setSize(width, height);
        initComponents();
        this.setVisible(true);
        this.initBackgroundImage();
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
    public String getMap() {
        return this.createPanel.getMap();
    }
    public String getGameName() {
        return this.createPanel.getGameName();
    }
    public int getMaxPlayers() {
        return this.createPanel.getMaxPlayers();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(this.image!=null){//actually image is BufferedImage so drawImage will return true.
            g.drawImage(this.image, 0, 0, null);
        }
    }

    private void initComponents() {
        this.initRadioButtons();

        // createPanel
        createPanel = new CreatingGameJPanel();

        // initialization of MyTable with list of games
        table = new GamesTable();
        
        // JoinPanel
        joinPanel = new JPanel();
        jsp = new JScrollPane(table, JScrollPane
                .VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setPreferredSize(new Dimension(width - 150, height - 150));
        joinPanel.add(jsp);
        joinPanel.setOpaque(false);
        joinPanel.setPreferredSize(new Dimension(width - 150, height - 120));

        // add all panels and buttons to the main Container
        Box mainBox = Box.createVerticalBox();
        createButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        joinButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        joinPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        createPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainBox.add(createButton);
        mainBox.add(Box.createVerticalStrut(5));
        mainBox.add(createPanel);
        mainBox.add(Box.createVerticalStrut(15));
        mainBox.add(joinButton);
        mainBox.add(Box.createVerticalStrut(5));
        mainBox.add(joinPanel);
        this.add(mainBox);

        // set first state to the "create"
        createButton.setSelected(true);
        table.setEnabled(false);
    }
    private void initRadioButtons() {
        // create JRadioButtons and set Actions to it
        createButton = new JRadioButton("New game");
        createButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                table.setEnabled(false);
                createPanel.setEnabled(true);
                //----------------------------------------------------------
                //WizardController.throwWizardAction(new WizardEvent
                //                (BombWizard.EVENT_NEXT_TEXT, "Create"));
            }
        });
        createButton.setOpaque(false);
        createButton.setForeground(foreground);
        joinButton = new JRadioButton("Join game");
        joinButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                table.setEnabled(true);
                createPanel.setEnabled(false);
                //--------------------------------------------------------
                //WizardController.throwWizardAction(new WizardEvent
                //                (BombWizard.EVENT_NEXT_TEXT, "Join"));
            }
        });
        joinButton.setOpaque(false);
        joinButton.setForeground(foreground);

        // Button group to select type - create new game or join to the created game
        ButtonGroup selectGroup = new ButtonGroup();
        selectGroup.add(createButton);
        selectGroup.add(joinButton);
    }
    private void initBackgroundImage() {
        try{
            this.image = ImageIO.read(BACKGROUND_URL);//returns BufferedImage
            this.image =
                    this.image.getScaledInstance(this.getWidth(),
                                                 this.getHeight(),
                                                 Image.SCALE_SMOOTH);
        }catch (IOException ex){
            Creator.createErrorDialog(this, "Can`t load background!", ex.getMessage());
            this.image = null;
        }
    }

    private class GamesTable extends JTable {

        public GamesTable() {
            super(new MyTableModel());
            this.setSizes();
            this.setDoubleClick();
        }
        private void setSizes() {
            this.getTableHeader().setReorderingAllowed(false);
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
        private void setDoubleClick() {
            this.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isEnabled()) {
                        int clicks = e.getClickCount();
                        if (clicks > 1) {
                            if (getValueAt(getSelectedRow(), 0) != null) {
                                //-------------------------------------------------
                                WizardController.throwWizardAction
                                        (new WizardEvent(BombWizard.EVENT_JOIN));
                            }
                        }
                    }
                }
            });
        }
        @Override
        public void setEnabled(boolean b) {
            super.setEnabled(b);
            if (!b) {
                this.setBackground(Color.lightGray);
                this.tableHeader.setBackground(Color.gray);
                this.getTableHeader().setForeground(Color.lightGray);
                this.setForeground(Color.white);
            } else {
                this.setBackground(Color.white);
                this.tableHeader.setBackground(new Color(238,238,238));
                this.getTableHeader().setForeground(new Color(51,51,51));
                this.setForeground(new Color(51,51,51));
            }

        }
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
