#short description of protocol

# Protocol #

Currently only tcp\ip asynchronous connection is supported. Asynchronous means that server can send some messages to client without requests from client side(like, for example, in http).

Protocol message consists of:
  * signed 32 bit integer - **_Identificator of message_**
  * another one signed 32 bit integer - **_Number of elements in data_**
  * elements of data, currently UTF String - **_data of message_**

So, in java it looks like this:
```
List<String> data = message.getData();
int size = data.size();

out.writeInt(message.getMessageId());
out.writeInt(size);
for (String string : data) {
    out.writeUTF(string);
}
```

Goto ProtocolCaptions to see availiable protocol captions.

Goto ProtocolTechnicalDetails to see how such messages are actually sending through sockets.