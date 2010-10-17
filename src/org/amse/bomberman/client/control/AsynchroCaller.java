package org.amse.bomberman.client.control;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface AsynchroCaller {

    void returnOk();

    void returnException(Exception ex);
}
