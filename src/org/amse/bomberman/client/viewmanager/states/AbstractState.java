package org.amse.bomberman.client.viewmanager.states;

import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.view.WaitingDialog.DialogState;
import org.amse.bomberman.client.view.wizard.Wizard;
import org.amse.bomberman.client.viewmanager.State;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Kirilchuk V.E.
 */
public abstract class AbstractState implements State {

    protected final ViewManager machine;
    protected State previous;
    protected State next;

    public AbstractState(ViewManager machine) { 
        this.machine  = machine;
    }

    public State setPrevious(State previous) {
        this.previous = previous;
        return this;
    }

    public State setNext(State next) {
        this.next = next;
        return this;
    }

    Wizard getWizard() {
        return machine.getWizard();
    }

    Controller getController() {
        return machine.getController();
    }

    /**
     * Override this if you wan`t to release some resoursec before changing
     * state. This method is used by ViewManager and must not be called
     * directly in state to release resourses before calling viewManager.setState()
     */
    public void release() {
        //do_nothing by default
    }
}
