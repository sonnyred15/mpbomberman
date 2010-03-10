/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.client.view.control.ifaces;

import java.util.List;

/**
 *
 * @author Kirilchuk V.E
 */
public interface ChatUpdatesListener extends ErrorMessageListener {

    void newMessagesGetted(List<String> messages);
}
