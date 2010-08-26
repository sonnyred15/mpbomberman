package org.amse.bomberman.client.view.gamejframe;

import java.awt.Color;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.view.IView;
import org.amse.bomberman.client.control.impl.Controller;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.amse.bomberman.client.Main;
import org.amse.bomberman.client.view.ResultsTable;

/**
 *
 * @author Mikhail Korovkin
 */
public class GameJFrame extends JFrame implements IView{
    private GamePanel gamePanel;
    private BonusLabel livesLabel;
    private BonusLabel bombsLabel;
    private BonusLabel radiusLabel;
    private ResultsTable resultsTable;
    private JTextArea infoTA;

    private GameJFrameListener listener;
    private boolean dead = false;

    private boolean isFirstInit = true;

    private int width = 800;
    private int height = 600;
    private final int infoTextWidth = 250;

    private static final String LIFE_ICON_PATH = "org/amse/bomberman/client/icons/heart-48.png";
    private static final String B_RADIUS_ICON_PATH = "org/amse/bomberman/client/icons/b_radius-48.png";
    private static final String B_COUNT_ICON_PATH = "org/amse/bomberman/client/icons/b_count-48.png";
    private static ImageIcon ICON_BONUS_LIFE = new ImageIcon(Main.class
            .getClassLoader().getResource(LIFE_ICON_PATH));
    private static ImageIcon ICON_BONUS_B_RADIUS = new ImageIcon(Main.class
            .getClassLoader().getResource(B_RADIUS_ICON_PATH));
    private static ImageIcon ICON_BONUS_B_COUNT = new ImageIcon(Main.class
            .getClassLoader().getResource(B_COUNT_ICON_PATH));
    
    public GameJFrame() {
        super("BomberMan");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocation(400, 100);
        
        this.setResizable(true);
        this.setVisible(true);
    }

    public synchronized void update() {
        IModel model = Model.getInstance();
        if (!model.isStarted()) {
            Controller.getInstance().leaveGame();
        } else {
            if (isFirstInit) {
                int mapSize = Model.getInstance().getMap().getSize();
                if (mapSize < GamePanel.DEFAULT_RANGE) {
                    width = mapSize*GamePanel.CELL_SIZE + 50 + infoTextWidth;
                    height = mapSize*GamePanel.CELL_SIZE + 160;
                }
                this.initComponents();
                isFirstInit = false;
            }
            gamePanel.update();
            updateBonusPanels();
            updateHistory();
            updateResults();
            int lives = model.getPlayerLives();
            if (lives <= 0) {
                if (!dead) {
                    dead = true;
                    livesLabel.setEnabled(false);
                    bombsLabel.setEnabled(false);
                    radiusLabel.setEnabled(false);
                    this.removeKeyListener(listener);
                    JOptionPane.showMessageDialog(this, "You are dead!!!"
                            , "Death", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    private void initComponents() {
        this.setSize(width, height);

        gamePanel = new GamePanel();
        livesLabel = new BonusLabel(ICON_BONUS_LIFE, 0);
        bombsLabel = new BonusLabel(ICON_BONUS_B_COUNT, 0);
        radiusLabel = new BonusLabel(ICON_BONUS_B_RADIUS, 0);
        resultsTable = new ResultsTable();
        listener = new GameJFrameListener();
        // TODO sizes???
        //resultsPanel.setPreferredSize(new Dimension(infoTextWidth, height - 150));

        Box bonusBox = Box.createHorizontalBox();
        bonusBox.add(livesLabel);
        bonusBox.add(Box.createHorizontalStrut(15));
        bonusBox.add(bombsLabel);
        bonusBox.add(Box.createHorizontalStrut(15));
        bonusBox.add(radiusLabel);

        Box rightBox = Box.createVerticalBox();
        rightBox.add(Box.createVerticalStrut(42)); // !!!
        JScrollPane jsp = new JScrollPane(resultsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                , JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setPreferredSize(new Dimension(infoTextWidth, height / 3));
        rightBox.add(jsp);
        rightBox.add(Box.createVerticalGlue());
        JScrollPane jsp2 = new JScrollPane(createInfoTA(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                , JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp2.setPreferredSize(new Dimension(infoTextWidth, height / 3));
        rightBox.add(jsp2);

        Box leftBox = Box.createVerticalBox();
        leftBox.add(bonusBox);
        leftBox.add(Box.createVerticalStrut(10));
        leftBox.add(gamePanel);

        Box mainBox = Box.createHorizontalBox();
        mainBox.add(leftBox);
        mainBox.add(Box.createHorizontalStrut(10));
        mainBox.add(rightBox);

        Container c = getContentPane();
        c.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        c.add(mainBox);

        this.addKeyListener(listener);
        this.setJMenuBar(new GameJMenuBar());
    }
    private void updateResults() {
        this.resultsTable.update(Model.getInstance().getResults());
    }
    private void updateBonusPanels() {
        int lives = Model.getInstance().getPlayerLives();
        int bombs = Model.getInstance().getPlayerBombs();
        int radius = Model.getInstance().getPlayerRadius();
        if (livesLabel.getCount() != lives) {
            livesLabel.update(lives);
        }
        if (bombsLabel.getCount() != bombs) {
            bombsLabel.update(bombs);
        }
        if (radiusLabel.getCount() != radius) {
            radiusLabel.update(radius);
        }
    }
    private void updateHistory() {
        List<String> history = Model.getInstance().getHistory();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i ++) {
            if (history.size() - i < 4 ) {
                sb.append("-");
                sb.append(history.get(i));
                sb.append("\n");
            }
        }
        infoTA.setText(sb.toString());
    }
    private JTextArea createInfoTA() {
        infoTA = new JTextArea();
        infoTA.setFocusable(false);
        infoTA.setEditable(false);
        infoTA.setLineWrap(true);
        infoTA.setForeground(Color.RED);
        infoTA.setFont(new Font(Font.MONOSPACED, Font.BOLD + Font.ITALIC, 12));
        return infoTA;
    }
    private class BonusLabel extends JLabel {
        private ImageIcon image;
        private int count;

        private final Color bg = new Color(238,238,238);

        private final int size = 32;

        private BonusLabel(ImageIcon icon, int firstCount) {
            image = new ImageIcon(getScaledImage(icon.getImage(),size,size,bg));

            this.setIcon(image);
            this.setText("x" + firstCount);
            this.setVisible(true);
        }

        private void update(int newCount) {
            if (newCount != count) {
                count = newCount;
                this.setText("x" + newCount);
            }
        }

        private int getCount() {
            return count;
        }

        @Override
        public void setEnabled(boolean b) {
            super.setEnabled(b);
            //this.setBackground(bg);
        }
    }
    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @param background - color for background
     * @return - the new resized image
     */
    private static Image getScaledImage(Image srcImg, int w, int h, Color background){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION
                , RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, background, null);
        g2.dispose();
        return resizedImg;
    }
}
