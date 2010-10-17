package org.amse.bomberman.client.viewmanager;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface State {
    void init();

    void previous();

    void next();

    State setPrevious(State previous);

    State setNext(State next);

    void release();
}
