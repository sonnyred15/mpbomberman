#client states

Ok, here we will talk about client states.
Client can be in on of three states:
  * **Not joined** to any game.
  * Joined to game, but game is unstarted.(So called **"Lobby"** state)
  * **In started game.**

If client sent message with caption that not corresponds to current state he will get message from server about invalid request with message like "Such action is not allowed in curent state".

If you don`t want this to happen look next what commands are legal in concrete state.

### Not joined ###

|Caption pseudo name|identificator(int)|
|:------------------|:-----------------|
|SET\_NAME\_MESSAGE\_ID|10|
|GAMES\_LIST\_MESSAGE\_ID|20|
|GAME\_MAPS\_LIST\_MESSAGE\_ID|30|
|CREATE\_GAME\_MESSAGE\_ID|40|
|JOIN\_GAME\_MESSAGE\_ID|50|

After last two commands if they were success you state will be changed to "Lobby" state.

### Joined - Lobby ###

|Caption pseudo name|identificator(int)|
|:------------------|:-----------------|
|GAME\_INFO\_MESSAGE\_ID|60|
|GAME\_STATUS\_MESSAGE\_ID|70|
|CHAT\_ADD\_MESSAGE\_ID|80|
|CHAT\_GET\_MESSAGE\_ID|100|
|BOT\_ADD\_MESSAGE\_ID |110|
|KICK\_PLAYER\_MESSAGE\_ID|120|
|START\_GAME\_MESSAGE\_ID|130|
|LEAVE\_MESSAGE\_ID|190|

After game start you state will be changed to "In started game".
After leave you state will be changed to "Not joined".

### In started game ###

|Caption pseudo name|identificator(int)|
|:------------------|:-----------------|
|GAME\_INFO\_MESSAGE\_ID|60|
|GAME\_STATUS\_MESSAGE\_ID|70|
|GAME\_MAP\_INFO\_MESSAGE\_ID|140|
|DO\_MOVE\_MESSAGE\_ID|150|
|PLACE\_BOMB\_MESSAGE\_ID|160|
|PLAYERS\_STATS\_MESSAGE\_ID|170|
|LEAVE\_MESSAGE\_ID|190|

After leave you state will be changed to "Not joined".