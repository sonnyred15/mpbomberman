package org.amse.bomberman.client.view.mywizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
/**
 *
 * @author Michael Korovkin
 */
public class Wizard extends JFrame{
    private List<WizardDescriptor> myDescriptors;
    private Dimension size = new Dimension(0,0);
    private int currentID = 0;

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel buttonPanel;

    private JButton backJButton = new JButton();
    private JButton nextJButton = new JButton();
    private JButton cancelJButton = new JButton();

    private static final String NEXT = "Next";
    private static final String BACK = "Back";
    private static final String FINISH = "Finish";
    private static final String CANCEL = "Cancel";

    public Wizard() {
        super();
        this.initComponents();
    }
    public Wizard(Dimension panelSize, String name) {
        super(name);
        this.size = panelSize;
        this.initComponents();
    }

    public void addWizardDescriptor(WizardDescriptor descriptor) {
        myDescriptors.add(descriptor);
        cardPanel.add(descriptor.getPanel(), descriptor.getIdentifier());
    }
    /*public void setNextButtonEnable(boolean b) {
        nextJButton.setEnabled(b);
    }
    public void setBackButtonEnable(boolean b) {
        backJButton.setEnabled(b);
    }
    public JPanel getCurrentJPanel() {
        return myDescriptors.get(currentID);
    }*/
    
    public void setCurrentJPanel(String id) {
        int newID = -1;
        for (int i = 0; i < myDescriptors.size(); i++) {
            if (myDescriptors.get(i).getIdentifier().equals(id)) {
                newID = i;
            }
        }
        if (newID >= 0 && newID < myDescriptors.size()) {
            int oldID = currentID;
            myDescriptors.get(oldID).doAfterDisplay();
            myDescriptors.get(newID).doBeforeDisplay();
            cardLayout.show(cardPanel, myDescriptors.get(newID).getIdentifier());
            currentID = newID;
            if (newID == 0) {
                backJButton.setEnabled(false);
            }
            if (newID == myDescriptors.size() - 1) {
                nextJButton.setText(FINISH);
            } else {
                nextJButton.setEnabled(true);
                nextJButton.setText(NEXT);
            }
        }
    }
    public void goBack() {
        if (backJButton.isEnabled() && currentID > 0) {
            myDescriptors.get(currentID).goBack();
            this.setCurrentJPanel(myDescriptors.get(currentID - 1).getIdentifier());
        }
    }
    public void goNext() {
        if (nextJButton.isEnabled() && currentID < myDescriptors.size() - 1) {
            myDescriptors.get(currentID).goNext();
            this.setCurrentJPanel(myDescriptors.get(currentID + 1).getIdentifier());
        }
    }

    private void initComponents() {
        this.setSize(size.width, size.height);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocation(400, 150);
        this.setResizable(false);

        myDescriptors = new ArrayList<WizardDescriptor>();
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

        cardPanel = new JPanel();
        //cardPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);
        Container c = this.getContentPane();
        c.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        c.add(cardPanel, java.awt.BorderLayout.CENTER);
    }

    public class CancelAction extends AbstractAction {

        public CancelAction() {
            putValue(NAME, CANCEL);
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }
    public class BackAction extends AbstractAction {

        public BackAction() {
            putValue(NAME, BACK);
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            goBack();
        }
    }
    public class NextAction extends AbstractAction {

        public NextAction() {
            putValue(NAME, NEXT);
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            goNext();
        }
    }
}
