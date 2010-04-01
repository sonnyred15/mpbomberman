package org.amse.bomberman.client.view.mywizard;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Panel11 {

    private static JTextField textField;
    private static JTextField passwordField;
    private static JTextField ftf;
    protected static final String textFieldString = "JTextField";
    protected static final String passwordFieldString = "JPasswordField";
    protected static final String ftfString = "JFormattedTextField";

    public static JPanel create() {

        JPanel textControlsPane = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        textControlsPane.setLayout(gridbag);
//INITIALIZATION
        textField = new JTextField(10);
        passwordField = new JTextField(10);
        ftf = new JTextField(10);
        JLabel textFieldLabel = new JLabel(textFieldString + ": ");
        textFieldLabel.setLabelFor(textField);
        JLabel passwordFieldLabel = new JLabel(passwordFieldString + ": ");
        passwordFieldLabel.setLabelFor(passwordField);
        JLabel ftfLabel = new JLabel(ftfString + ": ");
        ftfLabel.setLabelFor(ftf);
        JLabel[] labels = {textFieldLabel, passwordFieldLabel, ftfLabel};
        JTextField[] textFields = {textField, passwordField, ftf};

//ADDING ALL TO PANEL
        addLabelTextRows(labels, textFields, gridbag, textControlsPane);

        return textControlsPane;
    }

    private static void addLabelTextRows(JLabel[] labels,
            JTextField[] textFields,
            GridBagLayout gridbag,
            Container container) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;
        int numLabels = labels.length;

        for (int i = 0; i < numLabels; i++) {
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE; //reset to default
            c.weightx = 0.0; //reset to default
            container.add(labels[i], c);

            c.gridwidth = GridBagConstraints.REMAINDER; //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
//OTSTYPI DLYA TEXTFIELDOV!
            c.insets = new Insets(5, 5, 5, 5);
            container.add(textFields[i], c);
        }
    }
}
