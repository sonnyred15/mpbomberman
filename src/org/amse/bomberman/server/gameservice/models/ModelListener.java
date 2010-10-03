/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice.models;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface ModelListener {

    void fireStatsChanged();

    void fireFieldChanged();

    void end();
}
