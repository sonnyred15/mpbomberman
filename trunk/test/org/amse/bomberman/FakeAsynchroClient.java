package org.amse.bomberman;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.protocol.impl.ProtocolConstants;
import org.amse.bomberman.protocol.GenericProtocolMessage;
import org.amse.bomberman.protocol.impl.requests.RequestCreator;
import org.amse.bomberman.util.IOUtilities;

/**
 *
 * @author Kirilchuk V.E.
 */
public class FakeAsynchroClient {

    Socket socket;
    DataOutputStream out = null;
    DataInputStream in = null;

    public FakeAsynchroClient(int port) throws UnknownHostException, IOException {
        socket = new Socket(Inet4Address.getLocalHost(), port);
        out = initOut();
        in = initIn();
    }

    public void sendRequest(GenericProtocolMessage<Integer, String> request) throws IOException {
        List<String> data = request.getData();
        if(data == null) {
            throw new IllegalArgumentException("Data can`t be null.");
        }
        int size = data.size();

        out.writeInt(request.getMessageId());
        out.writeInt(size);
        for (String string : data) {
            if(string == null) {
                throw new IllegalArgumentException("Strings in data can`t be null.");
            }
            out.writeUTF(string);
        }
        //
        out.flush();
    }

    public GenericProtocolMessage<Integer, String> receiveResult() throws IOException {
        GenericProtocolMessage<Integer, String> message = new GenericProtocolMessage<Integer, String>();
        int messageId = in.readInt();

        message.setMessageId(messageId);

        int size = in.readInt();
        List<String> data = new ArrayList<String>(size);
        for (int i = 0; i < size; i++) {
            data.add(in.readUTF());
        }
        message.setData(data);

        return message;
    }

    private DataOutputStream initOut() throws IOException {
        OutputStream os = this.socket.getOutputStream();
        return new DataOutputStream(new BufferedOutputStream(os));
    }

    private DataInputStream initIn() throws IOException {
        InputStream is = socket.getInputStream();
        return new DataInputStream(new BufferedInputStream(is));
    }

    public void closeConnection() {
        try {
            IOUtilities.close(out);
            IOUtilities.close(in);
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
