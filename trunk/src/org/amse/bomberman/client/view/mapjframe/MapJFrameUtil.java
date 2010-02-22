package org.amse.bomberman.client.view.mapjframe;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.amse.bomberman.client.Main;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Michail Korovkin
 */
public class MapJFrameUtil {
    private static final Color EMPTY_COLOR = Color.LIGHT_GRAY;
    private static final Color PL_EXPL_COLOR = new Color(63,255,255);
    private static final Color EXPLODE_COLOR = Color.RED;
    private static final String BOMB_ICON_PATH = "org/amse/bomberman/client/icons/bomb2.png";
    private static final String WALL_ICON_PATH = "org/amse/bomberman/client/icons/wall_blue-48.png";
    private static final String PL1_ICON_PATH = "org/amse/bomberman/client/icons/superman-48-1.png";
    private static final String PL2_ICON_PATH = "org/amse/bomberman/client/icons/superman-48-2.png";
    private static final String PL3_ICON_PATH = "org/amse/bomberman/client/icons/superman-48-3.png";
    private static final String PL4_ICON_PATH = "org/amse/bomberman/client/icons/superman-48-4.png";
    private static final String BURN_ICON_PATH = "org/amse/bomberman/client/icons/burn-48.png";
    
    private MapJFrameUtil() {
        
    }
    public static ImageIcon getPlayerIcon(int mapValue) {
        ClassLoader cl = Main.class.getClassLoader();
        switch(mapValue) {
            case 1: return new ImageIcon(cl.getResource(PL1_ICON_PATH));
            case 2: return new ImageIcon(cl.getResource(PL2_ICON_PATH));
            case 3: return new ImageIcon(cl.getResource(PL3_ICON_PATH));
            case 4: return new ImageIcon(cl.getResource(PL4_ICON_PATH));
            default: return new ImageIcon(cl.getResource(PL4_ICON_PATH));
        }
    }
    public static class MyJPanel extends JLabel {
        public MyJPanel(int size) {
            this.setPreferredSize(new Dimension(size, size));
        }
        public void setContent(int key) {
            Color color = EMPTY_COLOR;
            ImageIcon icon = null;
            ClassLoader cl = Main.class.getClassLoader();
            switch (key) {
                case Constants.MAP_EMPTY: {
                    icon = null;
                    break;
                }
                case Constants.MAP_BOMB: {
                    icon = new ImageIcon(cl.getResource(BOMB_ICON_PATH));
                    break;
                }
                case Constants.MAP_DETONATED_BOMB: {
                    icon = new ImageIcon(cl.getResource(BURN_ICON_PATH));
                    break;
                }
            }
            if (key < Constants.MAP_EMPTY && key >= Constants.MAP_PROOF_WALL) {
                icon = new ImageIcon(cl.getResource(WALL_ICON_PATH));
            } else {
                if (key > Constants.MAP_EMPTY && key <= Constants.MAX_PLAYERS) {
                    icon = MapJFrameUtil.getPlayerIcon(key);
                }
            }
            this.setBackground(color);
            this.setIcon(icon);
            this.setOpaque(true);
        }
        public void checkExplosion(int mapValue){
            ImageIcon icon = null;
            ClassLoader cl = Main.class.getClassLoader();
            // if it is wall
            if (mapValue < Constants.MAP_EMPTY && mapValue >= Constants.MAP_PROOF_WALL) {
                //value = WALL_EXPL_COLOR;
                icon = new ImageIcon(cl.getResource(BURN_ICON_PATH));
            } else {
                // if it is player
                if (mapValue > Constants.MAP_EMPTY && mapValue <= Constants.MAX_PLAYERS) {
                    icon = MapJFrameUtil.getPlayerIcon(mapValue);
                    this.setBackground(PL_EXPL_COLOR);
                } else {
                    // if it is center of Explosion
                    icon = new ImageIcon(cl.getResource(BURN_ICON_PATH));
                }
            }
            this.setIcon(icon);
        }
    }
}
