/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.protocol;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author chibis
 */
public class ProtocolMessage<Identificator, DataType> {

    private Identificator messageId;
    private List<? extends DataType> data;

    public List<DataType> getData() {
        return Collections.unmodifiableList(data);
    }

    public void setData(List<? extends DataType> data) {
        this.data = data;
    }

    public Identificator getMessageId() {
        return messageId;
    }

    public void setMessageId(Identificator messageId) {
        this.messageId = messageId;
    }

    public boolean isBroken() {
        return (messageId == null || data == null);
    }
}
