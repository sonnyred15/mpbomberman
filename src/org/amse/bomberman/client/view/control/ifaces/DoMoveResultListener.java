/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.client.view.control.ifaces;

/**
 *
 * @author Kirilchuk V.E
 */
public interface DoMoveResultListener extends ErrorMessageListener {

    void moveResult(boolean moved);
}
