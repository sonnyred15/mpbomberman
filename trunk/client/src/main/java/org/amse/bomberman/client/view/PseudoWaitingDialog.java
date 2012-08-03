package org.amse.bomberman.client.view;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Kirilchuk V.E.
 */
@SuppressWarnings("serial")
public class PseudoWaitingDialog extends JPanel {

    private final JLabel text = new JLabel("Waiting response from server...");

    public PseudoWaitingDialog() {
        add(text);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xA0, 0xA0, 0xFF)),
                BorderFactory.createEmptyBorder(20, 20, 20 ,20)));

        setSize(getPreferredSize());
    }
}
