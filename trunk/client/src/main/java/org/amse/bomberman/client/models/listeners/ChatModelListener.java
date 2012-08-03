package org.amse.bomberman.client.models.listeners;

import java.util.List;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface ChatModelListener {

    void updateChat(List<String> newMessages);
}
