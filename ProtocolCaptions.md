#protocol captions and description

Not all captions availiable for both client and server, some of them can be sent only by one. And even if some message caption availiable for client, request with it can in some situations lead to InvalidRequestMessage from server.

To see when and what messages are availiable go to ClientStates.

## Message captions ##
|**Caption(pseudo name)**|**identificator(int)**|**Request from client**|**Response from server**|
|:-----------------------|:---------------------|:----------------------|:-----------------------|
|SetName|10|To set name|About result|
|GamesList|20|To get unstarted games|List of games with game params|
|GameMapsList|30|To get list of availiable maps|List of availiable maps|
|CreateGame|40|To create game|About result|
|JoinGame|50|To join game|About result|
|  | |
|GameInfo|60|To get game players info|Player names and so on|
|GameStatus|70|To ask is game started|Is game started or not|
|ChatAdd|80|To add message to chat|About result|
|ChatAddResult|90|N/A| Result of previous command|
|ChatGet|100|To get new msgs from chat(deprecated)|N/A|
|BotAdd|110|To add bot into game|About result|
|KickPlayer|120|To kick player from game|About result|
|  | |
|StartGame|130|To start game|About result|
|GameField|140|To get field, explosions and so on|Field, explosions and so on|
|DoMove|150|To make move|Move result|
|PlaceBomb|160|To place bomb|Place bomb result|
|PlayersStats|170|To get stats of players|Stats of players|
|EndResults|180|N/A|Notification about game end with stats|
|  | |
|[Leave](Leave.md)|190|To leave from lobby or game|About result|
|  | |
|DownloadMap|200|To download map(doesnt work)|Download|
|InvalidRequest|210|N/A|Info about error in request|
|  | |
|GameInfoNotify|510|N/A|Game info|
|GameStartedNotify|520|N/A|Notification about game start|
|GameTerminatedNotify|530|N/A|Notification about game termination|
|FameFieldChangedNotify|540|N/A|Notification about changes in field|
|GamesListChangedNotify|550|N/A|Notification about changes in game list|
|  | |
|[Disconnect](Disconnect.md)|1000|To disconnect and close connection|To notify client about closing connection|