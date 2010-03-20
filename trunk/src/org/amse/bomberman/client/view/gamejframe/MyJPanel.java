package org.amse.bomberman.client.view.gamejframe;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.amse.bomberman.client.Main;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Michael Korovkin
 */
public class MyJPanel extends JLabel {

    private static final Color EMPTY_COLOR = Color.LIGHT_GRAY;
    private static final Color PL_EXPL_COLOR = new Color(63, 255, 255);
    private static final Color EXPLODE_COLOR = Color.RED;
    private static final String BOMB_ICON_PATH = "org/amse/bomberman/client/icons/bomb-48.png";
    private static final String WALL_ICON_PATH = "org/amse/bomberman/client/icons/wall-48.png";
    private static final String PL1_ICON_PATH = "org/amse/bomberman/client/icons/superman-48-1.png";
    private static final String PL2_ICON_PATH = "org/amse/bomberman/client/icons/superman-48-2.png";
    private static final String PL3_ICON_PATH = "org/amse/bomberman/client/icons/superman-48-3.png";
    private static final String PL4_ICON_PATH = "org/amse/bomberman/client/icons/superman-48-4.png";
    private static final String BURN_ICON_PATH = "org/amse/bomberman/client/icons/burn-48.png";
    private static final String LIFE_ICON_PATH = "org/amse/bomberman/client/icons/heart-48.png";
    private static final String B_RADIUS_ICON_PATH = "org/amse/bomberman/client/icons/b_radius-48.png";
    private static final String B_COUNT_ICON_PATH = "org/amse/bomberman/client/icons/b_count-48.png";
    private static ImageIcon ICON_PL1 = new ImageIcon(Main.class
            .getClassLoader().getResource(PL1_ICON_PATH));
    private static ImageIcon ICON_PL2 = new ImageIcon(Main.class
            .getClassLoader().getResource(PL2_ICON_PATH));
    private static ImageIcon ICON_PL3 = new ImageIcon(Main.class
            .getClassLoader().getResource(PL3_ICON_PATH));
    private static ImageIcon ICON_PL4 = new ImageIcon(Main.class
            .getClassLoader().getResource(PL4_ICON_PATH));
    private static ImageIcon ICON_BONUS_LIFE = new ImageIcon(Main.class
            .getClassLoader().getResource(LIFE_ICON_PATH));
    private static ImageIcon ICON_BONUS_B_RADIUS = new ImageIcon(Main.class
            .getClassLoader().getResource(B_RADIUS_ICON_PATH));
    private static ImageIcon ICON_BONUS_B_COUNT = new ImageIcon(Main.class
            .getClassLoader().getResource(B_COUNT_ICON_PATH));
    private static ImageIcon ICON_WALL = new ImageIcon(Main.class
            .getClassLoader().getResource(WALL_ICON_PATH));
    private static ImageIcon ICON_BOMB = new ImageIcon(Main.class
            .getClassLoader().getResource(BOMB_ICON_PATH));
    private static ImageIcon ICON_BURN = new ImageIcon(Main.class
            .getClassLoader().getResource(BURN_ICON_PATH));

    public MyJPanel(int size) {
        this.setPreferredSize(new Dimension(size, size));
        this.setOpaque(true);
    }
    public void setContent(int key) {
        Color color = EMPTY_COLOR;
        ImageIcon icon = null;
        switch (key) {
            case Constants.MAP_EMPTY: {
                icon = null;
                break;
            }
            case Constants.MAP_BOMB: {
                icon = ICON_BOMB;
                break;
            }
            case Constants.MAP_DETONATED_BOMB: {
                icon = ICON_BURN;
                break;
            }
            case Constants.MAP_BONUS_LIFE: {
                icon = ICON_BONUS_LIFE;
                break;
            }
            case Constants.MAP_BONUS_BOMB_COUNT: {
                icon = ICON_BONUS_B_COUNT;
                break;
            }
            case Constants.MAP_BONUS_BOMB_RADIUS: {
                icon = ICON_BONUS_B_RADIUS;
                break;
            }
        }
        if (key < Constants.MAP_EMPTY && key >= Constants.MAP_PROOF_WALL) {
            icon = ICON_WALL;
        } else {
            if (key > Constants.MAP_EMPTY && key <= Constants.MAX_PLAYERS) {
                icon = MyJPanel.getPlayerIcon(key);
            }
        }
        this.setBackground(color);
        this.setIcon(icon);
    }
    public void checkExplosion(int mapValue) {
        ImageIcon icon = null;
        // if it is wall
        if (mapValue < Constants.MAP_EMPTY && mapValue >= Constants.MAP_PROOF_WALL) {
            //value = WALL_EXPL_COLOR;
            icon = ICON_BURN;
        } else {
            // if it is player
            if (mapValue > Constants.MAP_EMPTY && mapValue <= Constants.MAX_PLAYERS) {
                icon = MyJPanel.getPlayerIcon(mapValue);
                this.setBackground(PL_EXPL_COLOR);
            } else {
                // if it is center of Explosion
                icon = ICON_BURN;
            }
        }
        this.setIcon(icon);
    }
    private static ImageIcon getPlayerIcon(int mapValue) {
        switch (mapValue) {
            case 1:
                return ICON_PL1;
            case 2:
                return ICON_PL2;
            case 3:
                return ICON_PL3;
            case 4:
                return ICON_PL4;
            default:
                return ICON_PL1;
        }
    }
}
