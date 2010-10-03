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
    private boolean success = false;

    public List<DataType> getData() {
        return (data != null
                ? Collections.unmodifiableList(data)
                : null);
    }

    public void setData(List<? extends DataType> data) {
        if (data == null) {
            throw new IllegalArgumentException("Data can`t be null.");
        }
        this.data = data;
    }

    public Identificator getMessageId() {
        return messageId;
    }

    public void setMessageId(Identificator messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("Identificator can`t be null.");
        }
        this.messageId = messageId;
    }

    public boolean isOperationSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isBroken() {
        return (messageId == null || data == null);
    }
}
