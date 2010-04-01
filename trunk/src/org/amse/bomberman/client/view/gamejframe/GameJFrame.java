package org.amse.bomberman.client.view.gamejframe;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.view.IView;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.view.mywizard.MainWizard;
import org.amse.bomberman.client.view.mywizard.RequestResultListener;

/**
 *
 * @author Michael Korovkin
 */
public class GameJFrame extends JFrame implements IView, RequestResultListener{
    private GamePanel gamePanel;
    private JLabel livesJLabel;
    // is really nead???
    private boolean dead = false;
    private GameJFrameListener listener = new GameJFrameListener(this);
    private final int height = 600;
    private final int width = 500;
    
    public GameJFrame() {
        super("BomberMan");
        this.setSize(width, height);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocation(400, 100);
        this.setMinimumSize(new Dimension(width / 2, height / 2));
        BombMap map;
        try {
            Controller.getInstance().requestGameMap();
            // HACK!!! Delay for answer from server before get BombMap
            Object delay = new Object();
            synchronized (delay) {
                try {
                    delay.wait(100);
                } catch (InterruptedException ex) {
                }
            }
            //--------------------------------------------------------------
            map = Model.getInstance().getMap();
            gamePanel = new GamePanel(map);
            Container c = getContentPane();
            c.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            livesJLabel = new JLabel("Lives: 0");
            livesJLabel.setPreferredSize(new Dimension(width, 30));
            livesJLabel.setHorizontalAlignment(SwingConstants.CENTER);
            c.add(livesJLabel);
            c.add(gamePanel);
            this.addKeyListener(listener);
            this.setJMenuBar(new GameJMenuBar(this));

            setResizable(true);
            setVisible(true);
        } catch (NetException ex) {
             JOptionPane.showMessageDialog(this,"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             this.dispose();
             MainWizard wizard = new MainWizard();
             wizard.setCurrentJPanel(0);
             Controller.getInstance().setReceiveInfoListener(wizard);
        }
    }

    public void update() {
        IModel model = Model.getInstance();
        if (!model.isStarted()) {
            //JOptionPane.showMessageDialog(this, "You leaved the game.\n"
            //            , "STOP", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            Model.getInstance().removeListener(this);
            MainWizard wizard = new MainWizard();
            Controller.getInstance().setReceiveInfoListener(wizard);
            wizard.setCurrentJPanel(1);
            wizard.updateCurrentPanel();
        } else {
            gamePanel.update();
            int lives = model.getPlayerLives();
            this.refreshLives(lives);
            if (lives <= 0) {
                if (!dead) {
                    JOptionPane.showMessageDialog(this, "You are dead!!!"
                            , "Death", JOptionPane.INFORMATION_MESSAGE);
                    this.removeKeyListener(listener);
                    dead = true;
                }
            }
        }
    }
    public void received(List<String> list) {
        // Need to do something?
    }
    private void refreshLives(int lives) {
        String buf = livesJLabel.getText();
        String beginS = buf.substring(0, buf.length() - 1);
        String result = beginS.concat("" + lives);
        livesJLabel.setText(result);
    }
}
