package org.amse.bomberman.client.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import org.amse.bomberman.client.Main;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.Cell;
import org.amse.bomberman.client.model.Model;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.util.*;

/**
 *
 * @author michail korovkin
 */
public class MapJFrame extends JFrame implements IView{
    private MyJPanel[][] cells;
    private JLabel livesJLabel;
    private Timer timer;
    private final Color EMPTY_COLOR = Color.LIGHT_GRAY;
    private final Color PL_EXPL_COLOR = new Color(63,255,255);
    private final Color EXPLODE_COLOR = Color.RED;
    private final Color BEAM_COLOR = Color.ORANGE;
    private final String BOMB_ICON_PATH = "org/amse/bomberman/client/icons/bomb2.png";
    private final String WALL_ICON_PATH = "org/amse/bomberman/client/icons/wall_blue-48.png";
    private final String PL1_ICON_PATH = "org/amse/bomberman/client/icons/superman-48-1.png";
    private final String PL2_ICON_PATH = "org/amse/bomberman/client/icons/superman-48-2.png";
    private final String PL3_ICON_PATH = "org/amse/bomberman/client/icons/superman-48-3.png";
    private final String PL4_ICON_PATH = "org/amse/bomberman/client/icons/superman-48-4.png";
    private final String BURN_ICON_PATH = "org/amse/bomberman/client/icons/burn-48.png";
    // is really nead???
    private boolean dead = false;
    private MapJFrameListener listener = new MapJFrameListener();
    private final int height = 600;
    private final int width = 500;
    
    public MapJFrame(BombMap map) {
        super("BomberMan");
        setSize(width, height);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
        JPanel field = new JPanel();
        field.setLayout(new GridLayout(num,num,0,0));
        cells = new MyJPanel[num][num];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                cells[i][j] = new MyJPanel(48);
                cells[i][j].setContent(map.getValue(new Cell(i,j)));
                field.add(cells[i][j]);
            }
        }
        c.add(field);
        List<Cell> changes = Model.getInstance().getChanges();
        //this.refresh(changes, map);
        this.addKeyListener(listener);
        this.setJMenuBar(new MapJMenuBar(this));

        checkStart();
        setResizable(true);
        setVisible(true);
    }

    public void stopWaitStart() {
        timer.cancel();
    }
    public void update() {
        IModel model = Model.getInstance();
        List<Cell> changes = model.getChanges();
        BombMap newMap = model.getMap();
        int lives = model.getPlayerLives();
        this.refresh(changes, newMap);
        this.refreshLives(lives);
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
    private void refresh(List<Cell> changes, BombMap map) {
        if (changes != null) {
            List<Cell> expl = map.getExplosions();
            for (Cell cell : changes) {
                if (expl.contains(cell)) {
                    cells[cell.getX()][cell.getY()].checkExplosion(map.getValue(cell));
                } else {
                    cells[cell.getX()][cell.getY()].setContent(map.getValue(cell));
                }
                // why it isn't need???
                //cells[cell.getX()][cell.getY()].repaint();
            }
        }
    }
    private void checkStart() {
        timer = new Timer();
        timer.schedule(new CheckTimerTask(this), (long)0,(long) Constants.GAME_STEP_TIME);
    }
    private class CheckTimerTask extends TimerTask {
        MapJFrame parent;
        public CheckTimerTask(MapJFrame jframe) {
            parent = jframe;
        }
        @Override
        public void run() {
            IConnector connect = Model.getInstance().getConnector();
            try {
                if (connect.isStarted()) {
                    Model.getInstance().addListener(parent);
                    connect.beginUpdating();
                    Model.getInstance().startBots();
                    // block JMenuItem "Start"
                    parent.getJMenuBar().getMenu(0).getItem(0).setEnabled(false);
                    this.cancel();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
                    icon = getPlayerIcon(key);
                    
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
            if (mapValue < Constants.MAP_EMPTY && mapValue >= Constants.MAP_PROOF_WALL) {
                //value = WALL_EXPL_COLOR;
                icon = new ImageIcon(cl.getResource(BURN_ICON_PATH));
            } else {
                // if it is player
                if (mapValue > Constants.MAP_EMPTY && mapValue <= Constants.MAX_PLAYERS) {
                    icon = getPlayerIcon(mapValue);
                    value = PL_EXPL_COLOR;
                } else {
                    // if it is center of Explosion
                    icon = new ImageIcon(cl.getResource(BURN_ICON_PATH));
                    if (mapValue == Constants.MAP_DETONATED_BOMB) {
                        
                    } else {
                        //value = BEAM_COLOR;
                    }
                }
            }
            this.setBackground(value);
            this.setIcon(icon);
        }
    }
    private ImageIcon getPlayerIcon(int mapValue) {
        ClassLoader cl = Main.class.getClassLoader();
        switch(mapValue) {
            case 1: return new ImageIcon(cl.getResource(PL1_ICON_PATH));
            case 2: return new ImageIcon(cl.getResource(PL2_ICON_PATH));
            case 3: return new ImageIcon(cl.getResource(PL3_ICON_PATH));
            case 4: return new ImageIcon(cl.getResource(PL4_ICON_PATH));
            default: return new ImageIcon(cl.getResource(PL4_ICON_PATH));
        }
    }
}
