#set name command

Go to IdeaOfProtocol to remember what is **caption**, what is **data** and so on.

## Request ##

Client sends protocol message to server with next caption:

|Caption pseudo name|identificator(int)|
|:------------------|:-----------------|
|SET\_NAME\_MESSAGE\_ID|10|

The data of message must contain only name to set.

## Response ##

_Currently this command is availiable in all client states, but in next releases it would be availiable only in "Not joined" state._

If message has more than one argument then response looks like this:
> SET\_NAME\_MESSAGE\_ID - caption

> "Wrong number of arguments." - only element in data.


Else:
> SET\_NAME\_MESSAGE\_ID - caption

> "Name was set." - only element in data.