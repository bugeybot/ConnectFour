import java.io.*;
import java.net.*;
import java.util.*;

class Server {
    private static Map<String, DataOutputStream> clients = new HashMap<>();

    // PLAYERS - people who will be playing game
    private static Map<String, DataOutputStream> players = new HashMap();
    private static String[] playerArr = new String[2];

    // SPECTATORS - same as clients minus the players;
    private static Map<String, DataOutputStream> spectators = new HashMap(); // p

    private static int playerCount;
    private static ConnectFour game;
    private static int whosTurn;

    public static boolean full = false, gameRunning = false;

    static class ClientRequest implements Runnable {
        Socket connectionSocket;

        public ClientRequest(Socket socket) {
            this.connectionSocket = socket;
        }

        @Override
        public void run() {
            try {
                processRequest();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        private void processRequest() throws Exception {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            // send welcome msg
            String welcomeMsg = "Welcome! Please enter your name: ";
            outToClient.writeBytes(welcomeMsg + "\r\n");

            // receive name from client
            String clientName = inFromClient.readLine();
            System.out.println(clientName + " joined the chat!");

            // Send hello msg to the newly connected client
            String helloMsg = "Hello " + clientName + "! to the NP chatroom! If you would like to leave, type {quit}";
            outToClient.writeBytes(helloMsg + "\r\n");

            // broadcast
            for (String name : clients.keySet())
                clients.get(name).writeBytes(clientName + " joined the chat!\r\n");

            // store this name with its corresponding output stream for broadcasting
            // (stored afterwards to avoid sending trivial messages to client)
            clients.put(clientName, outToClient);

            spectators.put(clientName, outToClient); // p

            // Get msg from client
            while (true) {
                String clientMessage = null;
                while ((clientMessage = inFromClient.readLine()) != null) {
                    // p down
                    // join game command
                    if (clientMessage.contains("{joingame}"))
                        joinCommand(clientName);

                    else if (clientMessage.contains("{leavegame}"))
                        leaveCommand(clientName);
                        
                    else if (clientMessage.contains("{players}"))
                        clients.get(clientName).writeBytes("\'R\': " + playerArr[0] + "\r\n\'Y\': " + playerArr[1] + "\r\n");

                    else if (clientMessage.contains("{board}"))
                        if (gameRunning) clients.get(clientName).writeBytes(game.toString());
                        else clients.get(clientName).writeBytes("The game isn't running; You can't see a non-existant board.\r\n");

                    else if (clientMessage.contains("{start}"))
                        startCommand(clientName);

                    // quit command - will be changed to exit, quit will be quit game
                    else if (clientMessage.contains("{quit}"))
                        quitCommand(clientName);

                    // IN PROGRESS - working on error messages
                    else if (clientMessage.contains("{place}"))
                        placeCommand(clientName, clientMessage);

                    // spectator's messages are only sent to other spectators
                    else if (spectators.containsKey(clientName))
                        for (String name : spectators.keySet()) {
                            if (name != clientName)
                                spectators.get(name).writeBytes(clientName + ": " + clientMessage + "\r\n");
                        }

                    // if player speaks everyone will see (because they are not a spectator)
                    else
                        for (String names : clients.keySet()) {
                            if (names != clientName)
                                clients.get(names).writeBytes(clientName + ": " + clientMessage + "\r\n");
                        }
                }
            }
        }

        private void startCommand(String clientName) throws IOException {
            if (full && !gameRunning) {
                game = new ConnectFour(7, 6, 'R', 'Y');
                gameRunning = true;
                whosTurn = 0;

                for (String name : players.keySet())
                    clients.get(name).writeBytes("Your game has begun.\r\n");
                clients.get(playerArr[whosTurn]).writeBytes("It is your turn. Type \"{place} #\" (# 0-6).\r\n");
                for (String name : spectators.keySet())
                    clients.get(name).writeBytes("A game has begun.\r\n");
            } else if (!full)
                clients.get(clientName).writeBytes("The game isn't full yet.\r\n");
            else if (gameRunning)
                clients.get(clientName).writeBytes("The game is already running.\r\n");
        }

        // command to join game
        private void joinCommand(String clientName) throws IOException {
            // if no one joined red and player hasn't joined yellow
            if (full == false) {
                // adds a player
                for (int i = 0; i < playerArr.length; i++) if (playerArr[i].equals("")) playerArr[i] = clientName;
                playerCount += 1;
                players.put(clientName, spectators.get(clientName));

                // remove from spectator
                spectators.remove(clientName);

                System.out.println(clientName + " has joined the game!");

                // tell everyone a player joined
                for (String name : clients.keySet())
                    clients.get(name).writeBytes(clientName + " has joined the game!\r\n");
                if (playerCount >= 2)
                    full = true;
            } else {
                System.err.println(clientName + " tried to join, but the game is full.");
                // tell everyone that spot is taken
                for (String name : clients.keySet())
                    clients.get(name).writeBytes(clientName + " tried to join, but the game is full.\r\n");
            }
        }

        private void leaveCommand(String clientName) throws IOException {
            if (players.containsKey(clientName)) {
                full = false;
                playerCount -= 1;

                for (int i = 0; i < playerArr.length; i++) if (playerArr[i].equals(clientName)) playerArr[i] = "";
                spectators.put(clientName, players.get(clientName));
                players.remove(clientName);
            } else
                clients.get(clientName).writeBytes("You aren't a player.\r\n");
        }

        private void quitCommand(String clientName) throws IOException {
            clients.remove(clientName);
            System.out.println(clientName + " left the chat!");
            for (String name : clients.keySet())
                clients.get(name).writeBytes(clientName + " left the chat!\r\n");
        }

        private void placeCommand(String clientName, String clientMessage) throws IOException {
            if (players.containsKey(clientName) && gameRunning && playerArr[whosTurn].equals(clientName)) {
                int col = Integer.parseInt(clientMessage.split(" ")[1]); // this is the column to place the marker in
                System.out.println(clientName + " has placed a marker on column " + col);

                String response = game.playerTurn(game, whosTurn, col);
                clients.get(clientName).writeBytes(response);
                if (game.turnFailed) {
                    game.turnFailed = false;
                    return;
                }
                
                System.out.println(game.toString());
                for (String name : clients.keySet()) clients.get(name).writeBytes(game.toString());

                if (whosTurn == 1) whosTurn = 0;
                else whosTurn = 1;

                clients.get(playerArr[whosTurn]).writeBytes("It is your turn. Type \"{place} #\" (# 0-6).\r\n");
            }
            else if (players.containsKey(clientName)) clients.get(clientName).writeBytes("You're not a player. Only players can make moves.\r\n");
            else if (!gameRunning) clients.get(clientName).writeBytes("You can't make a move if the game isn't running.\r\n");
            else if (!playerArr[whosTurn].equals(clientName)) clients.get(clientName).writeBytes("It's not your turn.\r\n");
        }

        public static void main(String argv[]) throws Exception {
            // Create server socket
            ServerSocket serverSocket = new ServerSocket(25565);

            System.out.println("This server is ready to receive!");

            playerCount = 0;

            while (true) {
                Socket connectionSocket = serverSocket.accept();

                // Create client request instance
                ClientRequest request = new ClientRequest(connectionSocket);

                // Create a new thread to handle the client request
                Thread thread = new Thread(request);

                // Start the thread
                thread.start();
            }
        }
    }
}
