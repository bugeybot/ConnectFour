# (Connect) Four

## Introduction

We created a connect four implementation where two different clients connect to a server and are able to play connect 4. Essentially, you can say our project is connect 4 online but through a server and with spectators.

## System Architectures
### Server
* Uses multithreading to connect multiple clients to the server
* Handles commands from clients to play connect four
### Connect Four
* Contains all game logic, which is utilized by server to distribute to clients
### Clients
* Uses threads to communicate with server, letting it receive data and send it

## Features
1. Clients have different specifications
	- All clients are defined as ‘spectators’ until server is told otherwise
	- Only ‘players’ may advance the game of connect four
	- ‘Players’ should not receive outside help and therefore do not see messages by any client who is not a player (of which there are only two)
2. Clients have various commands they can run through the server
	- {joingame} turns a ‘spectator’  to  a ‘player’,  {quit} allows players to quit the game, {place} # allows the player to place a piece on the board
3. Players can play a game of connect four between each other
	- Each player must wait for the other’s turn to be complete
	- Ends game once winner is determined.
4. Chat features
	- All clients can speak with each other before they join a game
	- Clients who join a game can only see each other type
	- ‘Spectators’ (anyone who isn’t a ‘player’) can see both the players type and each other type
	- All spectators can see the game 

## Getting Started
### Installation and Setup
1. First item
	> git clone insert here
	> cd node-login
	> npm install
2. Second item
3. Third item

## Run
### Connect 4 Commands
- {joingame} makes the previous ‘spectator’  a ‘player’ who can play connect four
- {start} begins an instance of ConnectFour between the two players
- {place} advances game of connect four
- {leavegame} allows a ‘player’ to leave the game, and become a ‘spectator’
- {quit} allows client to leave the server
- {players} shows the players currently playing and their symbols
- {board} shows the current board...and more!

## Demo Video

https://

## Contributors

* Marco Roostaie
* Nathaniel Gratton
* Pedro Ferreira


