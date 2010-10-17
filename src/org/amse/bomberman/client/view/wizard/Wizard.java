package org.amse.bomberman.client.view.wizard;

import java.util.List;
import java.util.ArrayList;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.amse.bomberman.client.view.WaitingDialog;
import org.amse.bomberman.client.view.WaitingDialog.DialogResult;

/**
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
@SuppressWarnings("serial")
public class Wizard extends JFrame { 
    private final List<WizardListener> listeners = new ArrayList<WizardListener>();
    private WaitingDialog waitingDialog;
    ////
    private Dimension size;
    ////
    private JPanel mainPanel = new JPanel();
    //private CardLayout cardLayout;
    ////
    private static final String NEXT = "Next";
    private static final String BACK = "Back";
    private static final String FINISH = "Finish";
    private static final String CANCEL = "Cancel";
    ////
    private JPanel buttonPanel;
    private JButton backJButton = new JButton(BACK);
    private JButton nextJButton = new JButton(NEXT);
    private JButton cancelJButton = new JButton(CANCEL);

    public Wizard() {
        super("Let's BOMBERMANNING!!!");
        size = new Dimension(640, 480);
        initComponents();//will init cards panel with card layout
    }

    public void setNextText(String text) {
        this.nextJButton.setText(text);
    }

    public void setBackText(String text) {
        this.backJButton.setText(text);
    }

    private void initComponents() {
        this.setSize(size.width, size.height + 60);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        waitingDialog =  new WaitingDialog(this);
        buttonPanel   = new JPanel();

        JSeparator separator = new JSeparator();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(separator, BorderLayout.NORTH);

        Box buttonBox = Box.createHorizontalBox();
        buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
        buttonBox.add(backJButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(nextJButton);
        buttonBox.add(Box.createHorizontalStrut(30));
        buttonBox.add(cancelJButton);

        cancelJButton.setAction(new CancelAction());
        backJButton.setAction(new BackAction());
        nextJButton.setAction(new NextAction());

        //cards = new JPanel();
        //cardLayout = new CardLayout();
        //cards.setLayout(cardLayout);

        buttonPanel.add(buttonBox, BorderLayout.EAST);
        Container c = this.getContentPane();
        c.add(buttonPanel, BorderLayout.SOUTH);
        c.add(mainPanel, BorderLayout.CENTER);
    }

    public void setPanel(final JPanel mainPanel) {
        if(!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    setPanel(mainPanel);
                }
            });
            return;
        }

        getContentPane().remove(this.mainPanel);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().repaint();
        this.mainPanel = mainPanel;
    }

    public void showError(final String errorMessage) {
        if(!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    showError(errorMessage);
                }
            });
            return;
        }

        JOptionPane.showMessageDialog(this, errorMessage,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method must be called on EDT.
     * 
     * @return result of dialog.
     */
    public DialogResult showWaitingDialog() {
        return waitingDialog.showDialog();
    }

    public void closeWaitingDialog() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                waitingDialog.closeDialog();
            }
        });
    }
    
    public void cancelWaitingDialog() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                waitingDialog.cancelDialog();
            }
        });
    }

    public void addListener(WizardListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(WizardListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void fireWizardEvent(WizardEvent event) {
        synchronized (listeners) {
            for (WizardListener listener : listeners) {
                listener.wizardEvent(event);
            }
        }
    }

    @SuppressWarnings("serial")
    public class CancelAction extends AbstractAction {

        public CancelAction() {
            putValue(NAME, CANCEL);
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            fireWizardEvent(WizardEvent.CANCEL_PRESSED);
        }
    }

    @SuppressWarnings("serial")
    public class BackAction extends AbstractAction {

        public BackAction() {
            putValue(NAME, BACK);
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            fireWizardEvent(WizardEvent.BACK_PRESSED);
        }
    }

    @SuppressWarnings("serial")
    public class NextAction extends AbstractAction {

        public NextAction() {
            putValue(NAME, NEXT);
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            fireWizardEvent(WizardEvent.NEXT_PRESSED);
        }
    }
}
