package org.amse.bomberman.client.view.mywizard;

import java.awt.Dimension;
import java.util.List;

/**
 *
 * @author Michael Korovkin
 */
public class BombWizard extends Wizard implements RequestResultListener {
    public static final String IDENTIFIER1 = "Server_Panel";
    public static final String IDENTIFIER2 = "Create/Join_Panel";
    public static final String IDENTIFIER3 = "GameInfo_Panel";
    public BombWizard() {
        super(new Dimension(660, 530),"Let's BOMBERMANNING!!!");
        this.addWizardDescriptor(new PanelDescriptor1());
        this.addWizardDescriptor(new PanelDescriptor2());
        this.addWizardDescriptor(new PanelDescriptor3());
        this.setCurrentJPanel(IDENTIFIER1);
        this.setVisible(true);
    }

    public void received(List<String> result) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
