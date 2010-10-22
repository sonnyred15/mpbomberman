package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.impl.ChatModel;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NewChatMessagesHandler implements ProtocolHandler {

    public void process(Controller controller, List<String> data) {
        ChatModel chat = controller.getContext().getChatModel();
        if (!data.get(0).equals("No new messages.")) {
                chat.addMessages(data);
        }
    }
}
