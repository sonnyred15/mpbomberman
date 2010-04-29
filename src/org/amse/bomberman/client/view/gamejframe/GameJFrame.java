package org.amse.bomberman.client.view.gamejframe;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.view.IView;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.view.bomberwizard.BombWizard;

/**
 *
 * @author Michael Korovkin
 */
public class GameJFrame extends JFrame implements IView{
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
        gamePanel = new GamePanel();
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
             /*JOptionPane.showMessageDialog(this,"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             this.dispose();
             BomberWizard wizard = new BomberWizard();
             wizard.setCurrentJPanel(0);
             Controller.getInstance().setReceiveInfoListener(wizard);
              *
              */
    }

    public synchronized void update() {
        IModel model = Model.getInstance();
        if (!model.isStarted()) {
            this.dispose();
            Model.getInstance().removeListener(this);
            BombWizard wizard = new BombWizard();
            Controller.getInstance().setReceiveInfoListener(wizard);
            wizard.setCurrentJPanel(BombWizard.IDENTIFIER2);
        } else {
            gamePanel.update();
            int lives = model.getPlayerLives();
            this.refreshLives(lives);
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
    private void refreshLives(int lives) {
        String buf = livesJLabel.getText();
        String beginS = buf.substring(0, buf.length() - 1);
        String result = beginS.concat("" + lives);
        livesJLabel.setText(result);
    }
}
