package org.amse.bomberman.client.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import org.amse.bomberman.client.Main;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.BombMap.Cell;
import org.amse.bomberman.client.model.Model;

/**
 *
 * @author michail korovkin
 */
public class MapJFrame extends JFrame implements IView{
    private MyJPanel[][] cells;
    private JLabel livesJLabel;
    private final Color WALL_COLOR = Color.BLUE;
    private final Color WALL_EXPL_COLOR = new Color(0,0,127);
    private final Color EMPTY_COLOR = Color.LIGHT_GRAY;
    private final Color PLAYER1_COLOR = new Color(0,255,0);
    private final Color PLAYER2_COLOR = new Color(0,200,0);
    private final Color PLAYER3_COLOR = new Color(0,150,0);
    private final Color PL_EXPL_COLOR = new Color(63,255,255);
    private final Color BOMB_COLOR = Color.BLACK;
    private final Color EXPLODE_COLOR = Color.RED;
    private final Color BEAM_COLOR = Color.ORANGE;
    private final String BOMB_ICON_PATH = "org/amse/bomberman/client/icons/bomb2.png";
    private final String WALL_ICON_PATH = "org/amse/bomberman/client/icons/wall_blue-48.png";
    private final String MAN_ICON_PATH = "org/amse/bomberman/client/icons/superman-48.png";
    private final String BURN_ICON_PATH = "org/amse/bomberman/client/icons/burn-48.png";
    // is really nead???
    private boolean dead = false;
    private MapJFrameListener listener = new MapJFrameListener();
    private final int height = 600;
    private final int width = 500;
    
    public MapJFrame(BombMap map) {
        super("BomberMan");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(400, 100);
        setMinimumSize(new Dimension(width / 2, height / 2));
        
        Container c = getContentPane();
        c.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        livesJLabel = new JLabel("Lives: 0");
        // ???
        livesJLabel.setPreferredSize(new Dimension(width, 30));
        livesJLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.add(livesJLabel);
        int num = map.getSize();
        int size = (width - 50) / num;
        cells = new MyJPanel[num][num];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                cells[i][j] = new MyJPanel(48);
                c.add(cells[i][j]);
            }
        }
        this.refresh(map);
        this.addKeyListener(listener);
        this.setJMenuBar(new MapJMenuBar(this));
        setResizable(false);
        setVisible(true);
    }

    public void update() {
        IModel model = Model.getInstance();
        BombMap newMap = model.getMap();
        int lives = model.getPlayerLives();
        this.refresh(newMap);
        this.refreshLives(lives);
        this.repaint();
        if (lives <= 0) {
            if (!dead) {
                JOptionPane.showMessageDialog(this, "You are dead!!!", "Death"
                        , JOptionPane.INFORMATION_MESSAGE);
                this.removeKeyListener(listener);
                dead = true;
            }
        }
    }
    private void refreshLives(int lives) {
        String buf = livesJLabel.getText();
        String beginS = buf.substring(0, buf.length() - 1);
        String result = beginS.concat("" + lives);
        livesJLabel.setText(result);
    }
    private void refresh(BombMap map) {
        int num = map.getSize();
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                cells[i][j].setContent(map.getValue(i, j));
            }
        }
        ArrayList<Cell> expl = map.getExplosions();
        for (int i = 0; i < expl.size(); i++){
            int x = expl.get(i).getX();
            int y = expl.get(i).getY();
            cells[x][y].checkExplosion(map.getValue(x, y));
        }
    }
    private class MyJPanel extends JLabel {
        public MyJPanel(int size) {
            this.setPreferredSize(new Dimension(size, size));
        }
        public void setContent(int key) {
            Color color = EMPTY_COLOR;
            ImageIcon icon = null;
            ClassLoader cl = Main.class.getClassLoader();
            switch (key) {
                case BombMap.EMPTY: { 
                    icon = null;
                    break;
                }
                case BombMap.BOMB: {
                    icon = new ImageIcon(cl.getResource(BOMB_ICON_PATH));
                    break;
                }
                case BombMap.EXPLODED_BOMB: {
                    icon = new ImageIcon(cl.getResource(BURN_ICON_PATH));
                    break;
                }
                //case BombMap.BOMB_BEAM: { value = BEAM_COLOR; break;}
            }
            if (key < BombMap.EMPTY && key >= BombMap.BOMB_PROOF_WALL) {
                color = WALL_COLOR;
                icon = new ImageIcon(cl.getResource(WALL_ICON_PATH));
            } else {
                if (key > BombMap.EMPTY && key <= BombMap.MAX_PLAYERS) {
                    icon = new ImageIcon(cl.getResource(MAN_ICON_PATH));
                    /*if (key == 1) {
                        value = PLAYER1_COLOR;
                    } else {
                        if (key == 2) {
                            value = PLAYER2_COLOR;
                        } else {
                            value = PLAYER3_COLOR;
                        }
                    }*/
                }
            }
            this.setBackground(color);
            this.setIcon(icon);
            this.setOpaque(true);
        }
        public void checkExplosion(int mapValue){
            Color value = EMPTY_COLOR;
            ImageIcon icon = null;
            ClassLoader cl = Main.class.getClassLoader();
            // if it is wall
            if (mapValue < BombMap.EMPTY && mapValue >= BombMap.BOMB_PROOF_WALL) {
                //value = WALL_EXPL_COLOR;
                icon = new ImageIcon(cl.getResource(BURN_ICON_PATH));
            } else {
                // if it is player
                if (mapValue > BombMap.EMPTY && mapValue <= BombMap.MAX_PLAYERS) {
                    icon = new ImageIcon(cl.getResource(MAN_ICON_PATH));
                    value = PL_EXPL_COLOR;
                } else {
                    // if it is center of Explosion
                    icon = new ImageIcon(cl.getResource(BURN_ICON_PATH));
                    if (mapValue == BombMap.EXPLODED_BOMB) {
                        
                    } else {
                        //value = BEAM_COLOR;
                    }
                }
            }
            this.setBackground(value);
            this.setIcon(icon);
        }
    }
}
