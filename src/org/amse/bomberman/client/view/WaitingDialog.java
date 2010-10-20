package org.amse.bomberman.client.view;

import java.awt.Dialog;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Class that represents Waiting Modal dialog.
 *
 * @author Kirilchuk V.E.
 */
@SuppressWarnings("serial")
public class WaitingDialog extends JDialog implements PropertyChangeListener {

    public static enum DialogState {

        CLOSED, CANCELED;
    }
    private static final String MESSAGE = "Waiting response from server.\n"
            + "Please, be patient. =)";
    private final JOptionPane optionPane;

    private final String closed = "Closed";
    private final String cancel = "Cancel";

    private volatile DialogState state = DialogState.CLOSED;

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
                JOptionPane.CANCEL_OPTION, null, options);

        //set the dialog display panel
        setContentPane(optionPane);

        //other configuration
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        optionPane.addPropertyChangeListener(this);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public DialogState showDialog() {
        setVisible(true);
        return state;
    }

    public void closeDialog() {
        optionPane.setValue(closed);
    }

    public void cancelDialog() {
        optionPane.setValue(cancel);
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (isVisible()
                && (e.getSource() == optionPane)
                && (prop.equals(JOptionPane.VALUE_PROPERTY))) {            
            // when we resetting value
            if (optionPane.getValue() == JOptionPane.UNINITIALIZED_VALUE) {
                //value reseted. Not need other, so return.
                return;
            }

            //If you were going to check something
            //before closing the window, you'd do
            //it here.
            Object selectedValue = optionPane.getValue();
            if (selectedValue.equals(cancel)) {
                state = DialogState.CANCELED;
            } else {
                state = DialogState.CLOSED;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);
            setVisible(false);
        }
    }
}
