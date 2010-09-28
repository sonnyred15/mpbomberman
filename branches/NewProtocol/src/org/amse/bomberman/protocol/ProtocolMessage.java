package org.amse.bomberman.protocol;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author chibis
 */
public class ProtocolMessage<Identificator, DataType> {

    private Identificator messageId = null;
    private List<? extends DataType> data = null;
    private boolean error = false;

    public List<DataType> getData() {
        return (data != null 
                ? Collections.unmodifiableList(data)
                : null);
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

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isBroken() {
        return (messageId == null || data == null);
    }
}
