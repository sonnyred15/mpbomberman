package org.amse.bomberman.client.view.gamejframe;

import java.awt.BorderLayout;
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
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import org.amse.bomberman.client.Main;


/**
 *
 * @author Michael Korovkin
 */
public class GameJFrame extends JFrame implements IView{
    private GamePanel gamePanel;
    private BonusLabel livesLabel;
    private BonusLabel bombsLabel;
    private BonusLabel radiusLabel;
    private JTextArea infoTA;

    private GameJFrameListener listener;
    private boolean dead = false;

    private boolean isFirstInit = true;

    private int width = 500;
    private int height = 640;

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
                    width = mapSize*GamePanel.CELL_SIZE + 20;
                    height = mapSize*GamePanel.CELL_SIZE + 160;
                }
                this.initComponents();
                isFirstInit = false;
            }
            gamePanel.update();
            updateBonusPanels();
            updateHistory();
            int lives = model.getPlayerLives();
            if (lives <= 0) {
                if (!dead) {
                    dead = true;
                    JOptionPane.showMessageDialog(this, "You are dead!!!"
                            , "Death", JOptionPane.INFORMATION_MESSAGE);
                    this.removeKeyListener(listener);
                    livesLabel.setEnabled(false);
                    bombsLabel.setEnabled(false);
                    radiusLabel.setEnabled(false);
                }
            }
        }
    }
    private void initComponents() {
        this.setSize(width, height);
        this.setMinimumSize(new Dimension(width / 2, height / 2));

        gamePanel = new GamePanel();
        livesLabel = new BonusLabel(ICON_BONUS_LIFE, 0);
        bombsLabel = new BonusLabel(ICON_BONUS_B_COUNT, 0);
        radiusLabel = new BonusLabel(ICON_BONUS_B_RADIUS, 0);
        listener = new GameJFrameListener();

        Box bonusBox = Box.createHorizontalBox();
        bonusBox.add(livesLabel);
        bonusBox.add(Box.createHorizontalStrut(30));
        bonusBox.add(bombsLabel);
        bonusBox.add(Box.createHorizontalStrut(30));
        bonusBox.add(radiusLabel);

        Box mainBox = Box.createVerticalBox();
        mainBox.add(bonusBox);
        mainBox.add(gamePanel);
        mainBox.add(new JSeparator(JSeparator.VERTICAL));
        mainBox.add(this.createInfoPanel());

        Container c = getContentPane();
        c.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        c.add(mainBox);

        this.addKeyListener(listener);
        this.setJMenuBar(new GameJMenuBar());
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
                sb.append(history.get(i));
                sb.append("\n");
            }
        }
        infoTA.setText(sb.toString());
    }
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();

        infoTA = new JTextArea();
        infoTA.setPreferredSize(new Dimension(width - 50, 40));
        infoTA.setFocusable(false);
        infoTA.setEditable(false);
        infoTA.setLineWrap(true);
        infoTA.setForeground(Color.RED);
        infoTA.setFont(new Font(Font.MONOSPACED, Font.BOLD + Font.ITALIC, 10));

        JScrollPane scrollTA = new JScrollPane(infoTA);

        infoPanel.add(scrollTA);

        return infoPanel;
    }
    private class BonusLabel extends JLabel {
        private ImageIcon image;
        private int count;

        private final Color bg = new Color(238,238,238);

        private JLabel label;
        private final int size = 32;

        private BonusLabel(ImageIcon icon, int firstCount) {
            image = new ImageIcon(getScaledImage(icon.getImage(),size,size,bg));

            label = new JLabel("x" + firstCount, image, JLabel.CENTER);
            Container c = getContentPane();
            c.add(label);
        }
        private void update(int newCount) {
            if (newCount != count) {
                count = newCount;
                label.setText("x" + newCount);
            }
        }
        private int getCount() {
            return count;
        }
        @Override
        public void setEnabled(boolean b) {
            label.setEnabled(b);
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
