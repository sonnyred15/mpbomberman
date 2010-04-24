package org.amse.bomberman.client.view.bomberwizard;

import org.amse.bomberman.client.view.wizard.WizardController;
import org.amse.bomberman.client.view.wizard.WizardAction;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import org.amse.bomberman.util.Creator;
/**
 *
 * @author Michael Korovkin
 */
public class Panel2 extends JPanel{
    private final int width = 640;
    private final int height = 480;
    private Color foreground = Color.ORANGE;

    private Image image;
    private static final String BACKGROUND_PATH = "/org/amse/bomberman/client" +
            "/view/resources/cover2.png";
    private static final URL BACKGROUND_URL = Panel2.class.getResource(BACKGROUND_PATH);

    private JTable table;
    private CreatingGameJPanel creatingPanel;
    private JPanel mainPanel = new JPanel();
    private CardLayout cardLayout = new CardLayout();

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(this.image!=null){//actually image is BufferedImage so drawImage will return true.
            g.drawImage(this.image, 0, 0, null);
        }
    }

    private void initComponents() {
        // create JRadioButtons and set Actions to it
        createButton = new JRadioButton("New game");
        createButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, CREATE_NAME);
                //----------------------------------------------------------
                WizardController.throwWizardAction(new WizardAction
                                (BombWizard.ACTION_NEXT_TEXT, "Create"));
            }
        });
        createButton.setOpaque(false);
        createButton.setForeground(foreground);
        joinButton = new JRadioButton("Join game");
        joinButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, JOIN_NAME);
                //--------------------------------------------------------
                WizardController.throwWizardAction(new WizardAction
                                (BombWizard.ACTION_NEXT_TEXT, "Join"));
            }
        });
        joinButton.setOpaque(false);
        joinButton.setForeground(foreground);

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
        Box topBox = Box.createHorizontalBox();
        topBox.add(Box.createHorizontalStrut(300));
        topBox.add(radioBox);
        topBox.add(Box.createHorizontalGlue());

        // createPanel
        creatingPanel = new CreatingGameJPanel();
        JPanel createPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 50));
        createPanel.add(creatingPanel);
        createPanel.setOpaque(false);

        // initialization of MyTable with list of games
        table = new JTable(new MyTableModel());
        this.setSizesTable();
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clicks = e.getClickCount();
                if (clicks > 1) {
                    if (table.getValueAt(table.getSelectedRow(), 0) != null) {
                        //-----------------------------------------------------
                        WizardController.throwWizardAction(new WizardAction
                                (BombWizard.ACTION_JOIN));
                    }
                }
            }
        });
        // JoinPanel
        JPanel joinPanel = new JPanel();
        JScrollPane jsp = new JScrollPane(table, JScrollPane
                .VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setPreferredSize(new Dimension(width - 150, height - 150));
        joinPanel.add(jsp);
        joinPanel.setOpaque(false);
        joinPanel.setPreferredSize(new Dimension(400, 400));

        // mainPanel - cardLayout with createPanel and joinPanel
        mainPanel.setLayout(cardLayout);
        mainPanel.add(createPanel, CREATE_NAME);
        mainPanel.add(joinPanel, JOIN_NAME);
        mainPanel.setOpaque(false);
        mainPanel.setPreferredSize(new Dimension(200, height-100));
        mainPanel.setMaximumSize(new Dimension(width - 120,height-100));
        mainPanel.setBorder(new LineBorder(Color.ORANGE, 1));

        // leftPanel
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(30,100));
        leftPanel.add(new JSeparator());

        // add radioPanel and mainPanel
        Box mainBox = Box.createVerticalBox();
        mainBox.add(topBox);
        mainBox.add(mainPanel);
        this.add(mainBox);
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
