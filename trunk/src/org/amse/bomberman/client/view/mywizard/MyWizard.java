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
public class MyWizard  extends JFrame{
    private Dimension size = new Dimension(0,0);
    private List<String> myPanelNames;
    private List<JPanel> myPanels;
    private int currentID = -1;
    private JPanel cardPanel;
    private JPanel buttonPanel;
    private CardLayout cardLayout;
    private JButton backJButton = new JButton();
    private JButton nextJButton = new JButton();
    private JButton cancelJButton = new JButton();
    private static final String NEXT = "Next";
    private static final String BACK = "Back";
    private static final String FINISH = "Finish";
    private static final String CANCEL = "Cancel";

    public MyWizard() {
        super();
        this.initComponents();
    }
    public MyWizard(Dimension panelSize, String name) {
        super(name);
        this.size = panelSize;
        this.initComponents();
    }

    public void addNextJPanel(JPanel panel, String panelName) {
        myPanelNames.add(panelName);
        myPanels.add(panel);
        cardPanel.add(panel, panelName);
        //cardLayout.addLayoutComponent(panel, panelName);
    }
    public void setNextButtonEnable(boolean b) {
        nextJButton.setEnabled(b);
    }
    public void setBackButtonEnable(boolean b) {
        backJButton.setEnabled(b);
    }
    public JPanel getCurrentJPanel() {
        return myPanels.get(currentID);
    }
    /*
     * DANGER!!! Using this method you override standart Action to this button!
     */
    public void setNextAction(Action a) {
        nextJButton.setAction(a);
    }
    /*
     * DANGER!!! Using this method you override standart Action to this button!
     */
    public void setBackAction(Action a) {
        backJButton.setAction(a);
    }
    public void setCurrentJPanel(int id) {
        if (id >= 0 && id < myPanels.size()) {
            cardLayout.show(cardPanel, myPanelNames.get(id));
            currentID = id;
            if (id == 0) {
                backJButton.setEnabled(false);
            }
            if (id == myPanels.size() - 1) {
                nextJButton.setText(FINISH);
            } else {
                nextJButton.setEnabled(true);
                nextJButton.setText(NEXT);
            }
        }
    }
    public void goBack() {
        if (backJButton.isEnabled() && currentID > 0) {
            currentID--;
            cardLayout.previous(cardPanel);
            //cardLayout.show(cardPanel, myPanelNames.get(currentID));
            if (currentID == 0) {
                backJButton.setEnabled(false);
            }
            if (!nextJButton.isEnabled()) {
                nextJButton.setEnabled(true);
            }
            nextJButton.setText(NEXT);
        }
    }
    public void goNext() {
        if (nextJButton.isEnabled() && currentID < myPanels.size() - 1) {
            currentID++;
            cardLayout.next(cardPanel);
            //cardLayout.show(cardPanel, myPanelNames.get(currentID));
            if (currentID == myPanels.size() - 1) {
                nextJButton.setText(FINISH);
            } else {
                nextJButton.setText(NEXT);
            }
            if (!backJButton.isEnabled()) {
                backJButton.setEnabled(true);
            }
        }
    }

    private void initComponents() {
        this.setSize(size.width, size.height);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocation(400, 150);
        this.setResizable(false);

        myPanelNames = new ArrayList<String>();
        myPanels = new ArrayList<JPanel>();
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

        cancelJButton.setAction(new CancelAction(this));
        backJButton.setAction(new BackAction(this));
        nextJButton.setAction(new NextAction(this));

        cardPanel = new JPanel();
        //cardPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);
        Container c = this.getContentPane();
        c.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        c.add(cardPanel, java.awt.BorderLayout.CENTER);
    }

    public static class CancelAction extends AbstractAction {
        MyWizard parent;

        public CancelAction(MyWizard jframe) {
            parent = jframe;
            putValue(NAME, CANCEL);
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            parent.dispose();
        }
    }
    public static class BackAction extends AbstractAction {
        MyWizard parent;

        public BackAction(MyWizard jframe) {
            parent = jframe;
            putValue(NAME, BACK);
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            parent.goBack();
        }
    }
    public static class NextAction extends AbstractAction {
        MyWizard parent;

        public NextAction(MyWizard jframe) {
            parent = jframe;
            putValue(NAME, NEXT);
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            parent.goNext();
        }
    }
}
