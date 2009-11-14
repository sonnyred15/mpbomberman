package org.amse.bomberman.client.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.Model;

/**
 *
 * @author michail korovkin
 */
public class MapJFrame extends JFrame implements IView{
    private MyJPanel[][] cells;
    private final Color WALL_COLOR = Color.BLUE;
    private final Color EMPTY_COLOR = Color.LIGHT_GRAY;
    private final Color PLAYER1_COLOR = Color.GREEN;
    private final Color PLAYER2_COLOR = Color.CYAN;
    private final Color PLAYER3_COLOR = Color.PINK;
    private final Color BOMB_COLOR = Color.BLACK;
    private final Color EXPLODE_COLOR = Color.RED;
    private final Color BEAM_COLOR = Color.ORANGE;
    private final int height = 650;
    private final int width = 650;

    public MapJFrame(BombMap map) {
        super("BomberMan");
        setSize(width, height);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
    }
}
