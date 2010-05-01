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
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

    private final int width = 500;
    private final int height = 600;

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
        this.setSize(width, height);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocation(400, 100);
        this.setMinimumSize(new Dimension(width / 2, height / 2));

        gamePanel = new GamePanel();
        livesLabel = new BonusLabel(ICON_BONUS_LIFE, 0);
        bombsLabel = new BonusLabel(ICON_BONUS_B_COUNT, 0);
        radiusLabel = new BonusLabel(ICON_BONUS_B_RADIUS, 0);
        listener = new GameJFrameListener();


        Box bonusBox = Box.createHorizontalBox();
        bonusBox.add(livesLabel);
        bonusBox.add(bombsLabel);
        bonusBox.add(radiusLabel);

        Box topBox = Box.createHorizontalBox();
        topBox.add(bonusBox);
        //topBox.add(this.createInfoPanel());

        Container c = getContentPane();
        c.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        c.add(topBox);
        c.add(gamePanel);
        this.addKeyListener(listener);
        this.setJMenuBar(new GameJMenuBar());

        setResizable(true);
        setVisible(true);
    }

    public synchronized void update() {
        IModel model = Model.getInstance();
        if (!model.isStarted()) {
            Controller.getInstance().leaveGame();
            /*this.dispose();
            Model.getInstance().removeListeners();
            BombWizard wizard = new BombWizard();
            Controller.getInstance().setReceiveInfoListener(wizard);
            wizard.setCurrentJPanel(BombWizard.IDENTIFIER2);*/
        } else {
            gamePanel.update();
            this.updateBonusPanels();
            int lives = model.getPlayerLives();
            if (lives <= 0) {
                if (!dead) {
                    dead = true;
                    JOptionPane.showMessageDialog(this, "You are dead!!!"
                            , "Death", JOptionPane.INFORMATION_MESSAGE);
                    this.removeKeyListener(listener);
                }
            }
        }
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
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout());

        infoTA = new JTextArea();
        infoTA.setPreferredSize(new Dimension(200, 50));
        this.infoTA.setEditable(false);
        this.infoTA.setLineWrap(true);
        this.infoTA.setForeground(Color.ORANGE);
        this.infoTA.setFont(new Font(Font.MONOSPACED, Font.BOLD + Font.ITALIC, 10));
        //this.infoTA.setOpaque(false);

        JScrollPane scrollTA = new JScrollPane(infoTA);
        //scrollTA.setOpaque(false);
        //scrollTA.getViewport().setOpaque(false);
        //scrollTA.setBorder(null);

        infoPanel.add(scrollTA);

        return infoPanel;
    }
    private class BonusLabel extends JLabel {
        private ImageIcon image;
        private int count;

        private JLabel label;

        private BonusLabel(ImageIcon icon, int firstCount) {
            image = icon;

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
    }
}
