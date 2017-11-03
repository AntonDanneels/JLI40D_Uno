# Iteration 2: Distributed scenario

## Client <-> Dispatcher

- Client starts up and connects with dispatcher
- Client receives IP of a game server

## Client <-> Game server

- Client joins game
- Client sends game moves
- Client login/register via his game server
- Client requests list of games via the game server, this list contains {Game info, Server ip}
- Client creates a game via the game server

## Game server <-> Database

- Game server updates his database and maintains local cache for his games
- Game server can request other games from the db and start maintaining them
- Game server can receive a simplified list of game meta-data: {Game info, server ip}

## Game server <-> Dispatcher

- Game server communicates with the dispatcher about the amount of games
- Dispatcher can force game server to shut down and transfer games and users

## Database <-> Database

- Database send info about his games to the other databases who are dumb writers
- Database receives info about other games and simply writes them