package org.amse.bomberman.client.view;

import java.awt.Dialog;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Kirilchuk V.E.
 */
@SuppressWarnings("serial")
public class WaitingDialog extends JDialog implements PropertyChangeListener {

    public static enum DialogResult {

        OPENED, CLOSED, CANCELED;
    }
    private static final String MESSAGE = "Waiting response from server.\n"
            + "Please, be patient. =)";
    private final JOptionPane optionPane;

    private final String opened = "Opened";
    private final String closed = "Closed";
    private final String cancel = "Cancel";

    public WaitingDialog(Window owner) {
        this(owner, "Waiting response from server.");
    }

    public WaitingDialog(Window owner, String caption) {
        //Don`t set MODELESS it will cause always return default result value
        //instead of waiting for user to cancel.
        super(owner, caption, Dialog.ModalityType.APPLICATION_MODAL);

        //content of dialog panel
        Object[] elements = {MESSAGE};

        //name of buttons left-to-right
        Object[] options = {cancel};

        //creating dialog option pane
        optionPane = new JOptionPane(elements, JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.CANCEL_OPTION, null, options, closed);

        //set the dialog display panel
        setContentPane(optionPane);

        //other configuration
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        optionPane.addPropertyChangeListener(this);

        pack();
        setLocationRelativeTo(owner);
    }

    public DialogResult showDialog() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Must be called from EDT");
        }

        optionPane.setValue(opened);
        setVisible(true);
        Object selectedValue = optionPane.getValue();
        if (selectedValue.equals(cancel)) {
            return DialogResult.CANCELED;
        } else {
            return DialogResult.CLOSED;
        }
    }

    public void closeDialog() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Must be called from EDT");
        }

        optionPane.setValue(closed);
    }

    public void cancelDialog() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Must be called from EDT");
        }
        
        optionPane.setValue(cancel);
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
                && (e.getSource() == optionPane)
                && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
            //If you were going to check something
            //before closing the window, you'd do
            //it here.
            setVisible(false);
        }
    }
}
