package org.amse.bomberman.client.view.wizard;

import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.util.List;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.amse.bomberman.client.view.DialogState;
import org.amse.bomberman.client.view.PseudoWaitingDialog;

/**
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
@SuppressWarnings("serial")
public class Wizard extends JFrame {

    private final List<WizardListener> listeners = new CopyOnWriteArrayList<WizardListener>();
    ////
    private final PseudoWaitingDialog waitingDialog = new PseudoWaitingDialog();
    private final JComponent dialogPanel = new JComponent() {
        private static final long serialVersionUID = 1L;
        private final Color bg = new Color(0, 0, 0, 64);

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(bg);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    private volatile DialogState dialogState = DialogState.CLOSED;
    ////
    private Dimension size;
    ////
    private JPanel mainPanel = new JPanel();
    ////
    private static final String NEXT = "Next";
    private static final String BACK = "Back";
    private static final String FINISH = "Finish";
    private static final String CANCEL = "Cancel";
    ////
    private JPanel  buttonPanel;
    private JButton backJButton = new JButton(BACK);
    private JButton nextJButton = new JButton(NEXT);
    private JButton cancelJButton = new JButton(CANCEL);

    public Wizard() {
        super("Let's BOMBERMANNING!!!");
        size = new Dimension(640, 480);
        initComponents();//will init cards panel with card layout
    }

    public void setNextText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                nextJButton.setText(text);
            }
        });
    }

    public void setBackText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                backJButton.setText(text);
            }
        });
    }

    private void initComponents() {
        this.setSize(size.width, size.height + 60);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        buttonPanel = new JPanel();

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


        buttonPanel.add(buttonBox, BorderLayout.EAST);
        Container c = this.getContentPane();
        c.add(buttonPanel, BorderLayout.SOUTH);
        c.add(mainPanel, BorderLayout.CENTER);

        initDialogPanel();
    }

    private void initDialogPanel() {
        dialogPanel.setLayout(new GridBagLayout());
        dialogPanel.setOpaque(false);
        dialogPanel.add(waitingDialog, new GridBagConstraints());        
        dialogPanel.addMouseListener(new MouseAdapter() {});
        dialogPanel.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                if(dialogPanel.isVisible()) {
                    dialogPanel.requestFocus();
                }
            }

        });
        dialogPanel.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                dialogPanel.requestFocus();
            }
        });
        dialogPanel.setVisible(false);
        getRootPane().setGlassPane(dialogPanel);
    }

    public DialogState getDialogState() {
        return dialogState;
    }

    public void showDialog() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                dialogState = DialogState.OPENED;
                dialogPanel.setVisible(true);
            }
        });
    }

    public void hideDialog() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                dialogState = DialogState.CLOSED;
                dialogPanel.setVisible(false);
            }
        });
    }

    public void setPanel(final JPanel mainPanel) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
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

    /**
     * Be careful! This method is blocking! Until user clicks
     * OK on message dialog, calling thread will be blocked on this method.
     *
     * @param errorMessage message to show.
     */
    public void showError(final String errorMessage) {
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        showError(errorMessage);
                    }
                });
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
            return;
        }

        JOptionPane.showMessageDialog(this, errorMessage,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void addListener(WizardListener listener) {
        listeners.add(listener);
    }

    public void removeListener(WizardListener listener) {
        listeners.remove(listener);
    }

    private void fireWizardEvent(WizardEvent event) {
        for (WizardListener listener : listeners) {
            listener.wizardEvent(event);
        }
    }

    @SuppressWarnings("serial")
    public class CancelAction extends AbstractAction {

        public CancelAction() {
            putValue(NAME, CANCEL);
            putValue(SMALL_ICON, null);
        }

        @Override
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

        @Override
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

        @Override
        public void actionPerformed(ActionEvent e) {
            fireWizardEvent(WizardEvent.NEXT_PRESSED);
        }
    }
}
