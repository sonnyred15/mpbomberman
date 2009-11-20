package org.amse.bomberman.client.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
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
    private final int height = 650;
    private final int width = 650;
    // IT IS VERY BAD!!!
    private int lives = 3;

    public MapJFrame(BombMap map) {
        super("BomberMan");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(400, 100);
        setMinimumSize(new Dimension(width / 2, height / 2));
        
        Container c = getContentPane();
        c.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        int num = map.getSize();
        int size = (width - 50) / num;
        cells = new MyJPanel[num][num];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                cells[i][j] = new MyJPanel(size);
                c.add(cells[i][j]);
            }
        }
        this.refresh(map);
        this.addKeyListener(new MapJFrameListener());
        this.setJMenuBar(new MapJMenuBar(this));
        setResizable(false);
        setVisible(true);
    }

    public void update() {
        IModel model = Model.getInstance();
        BombMap newMap = model.getMap();
        this.refresh(newMap);
        this.repaint();
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
    private class MyJPanel extends JPanel {
        public MyJPanel(int size) {
            this.setPreferredSize(new Dimension(size, size));
        }
        public void setContent(int key) {
            Color value = null;
            switch (key) {
                case BombMap.EMPTY: { 
                    value = EMPTY_COLOR;
                    this.setBorder(new LineBorder(EMPTY_COLOR));
                    break;
                }
                case BombMap.BOMB: { value = BOMB_COLOR; break;}
                case BombMap.EXPLODED_BOMB: { value = EXPLODE_COLOR; break;}
                case BombMap.BOMB_BEAM: { value = BEAM_COLOR; break;}
            }
            if (key < BombMap.EMPTY && key >= BombMap.BOMB_PROOF_WALL) {
                value = WALL_COLOR;
                this.setBorder(new LineBorder(Color.BLACK));
            } else {
                if (key > BombMap.EMPTY && key <= BombMap.MAX_PLAYERS) {
                    // only for 2 players yet
                    if (key == 1) {
                        value = PLAYER1_COLOR;
                    } else {
                        if (key == 2) {
                            value = PLAYER2_COLOR;
                        } else {
                            value = PLAYER3_COLOR;
                        }
                    }
                }
            }
            this.setBackground(value);
        }
        public void checkExplosion(int mapValue){
            Color value = null;
            // if it is wall
            if (mapValue < BombMap.EMPTY && mapValue >= BombMap.BOMB_PROOF_WALL) {
                value = WALL_EXPL_COLOR;
            } else {
                // if it is player
                if (mapValue > BombMap.EMPTY && mapValue <= BombMap.MAX_PLAYERS) {
                    value = PL_EXPL_COLOR;
                    // IT IS BAAAADDD TO DO THAT!!!!!
                    Model model = (Model)Model.getInstance();
                    if (mapValue == model.getMyNumber()) {
                        lives--;
                        if (lives == 0) {
                            JOptionPane.showMessageDialog(this, "You are DEAD!!!"
                        , "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    // if it is center of Explosion
                    if (mapValue == BombMap.EXPLODED_BOMB) {
                        
                    } else {
                        value = BEAM_COLOR;
                    }
                }
            }
            this.setBackground(value);
        }
    }
}
