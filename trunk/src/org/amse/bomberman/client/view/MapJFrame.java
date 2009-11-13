package org.amse.bomberman.client.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.Map;
import org.amse.bomberman.client.model.Model;

/**
 *
 * @author michail korovkin
 */
public class MapJFrame extends JFrame implements IView{
    private MyJPanel[][] cells;
    private final Color PLAYER1_COLOR = Color.GREEN;
    private final Color PLAYER2_COLOR = Color.ORANGE;
    private final Color PLAYER3_COLOR = Color.PINK;
    private final int height = 650;
    private final int width = 650;

    public MapJFrame(Map map) {
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
        setResizable(false);
        setVisible(true);
    }

    public void update() {
        IModel model = Model.getInstance();
        Map newMap = model.getMap();
        this.refresh(newMap);
        this.repaint();
    }
    private void refresh(Map map) {
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
            if (key == Map.EMPTY) {
                this.setBackground(Color.LIGHT_GRAY);
            } else {
                if (key < Map.EMPTY && key >= Map.BOMB_PROOF_WALL) {
                    this.setBackground(Color.blue);
                    this.setBorder(new LineBorder(Color.BLACK));
                } else {
                    if (key > Map.EMPTY && key <= Map.MAX_PLAYERS) {
                        // only for 2 players yet
                        if (key == 1) {
                            this.setBackground(PLAYER1_COLOR);
                        } else {
                            if (key == 2) {
                                this.setBackground(PLAYER2_COLOR);
                            } else {
                                this.setBackground(PLAYER3_COLOR);
                            }
                        }
                    }
                }
            }
        }
    }
}
