package org.amse.bomberman.util;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public final class UIUtil {
    
    private UIUtil() {}
    
    /**
     * Creates error JDialog.
     * @see JDialog
     * @param parent determines the Frame in which the dialog is displayed.
     * If null, or if the parentComponent has no Frame, a default Frame is used
     * @param description description of error.
     * @param message message of error.
     */
    public static void createErrorDialog(Component parent, 
                                         String description,
                                         String message) {
        //
        JOptionPane.showMessageDialog(parent, description + "\n" + message,
                                     "Error", JOptionPane.ERROR_MESSAGE);
    }
}
