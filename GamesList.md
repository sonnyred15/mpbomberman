#games list

Go to IdeaOfProtocol to remember what is **caption**, what is **data** and so on.

## Request ##

Client sends protocol message to server with next caption:

|Caption pseudo name|identificator(int)|
|:------------------|:-----------------|
|GAMES\_LIST\_MESSAGE\_ID|20|

The data of message must be empty. _Anyway data will be ignored_

## Response ##

_Currently this command is availiable in all client states, but in next releases it would be availiable only in "Not joined" state._

If there is no unstarted games on server then response looks like this:
> GAMES\_LIST\_MESSAGE\_ID - caption

> "No unstarted games finded." - only element in data.
Else:
> GAMES\_LIST\_MESSAGE\_ID - caption

> //TODO write description of how games are sending...