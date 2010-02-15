/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server;

/**
 *
 * @author Kirilchuk V.E
 */
public interface ServerChangeListener {

    void stateChanged();

    void addedToLog(String line);
}
