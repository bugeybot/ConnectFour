import java.io.*;
import java.net.*;
import java.util.*;

class WebServer {
    private static Map<String, DataOutputStream> clients = new HashMap<>();

    // PLAYERS - people who will be playing game
    private static Map<String, DataOutputStream> players = new HashMap(); // p

    private static boolean redWent = false;
    
    // SPECTATORS - same as clients minus the players;
    private static Map<String, DataOutputStream> spectators = new HashMap(); // p

    private static int playerCount;
    
    private static Map<String, Character> playersArr = new HashMap();
    
    public static boolean full = false; //p

    static class ClientRequest implements Runnable {
        Socket connectionSocket;

        public ClientRequest(Socket socket) {
            this.connectionSocket = socket;
        }


        @Override
        public void run() {
            try { processRequest(); }
            catch (Exception e) { System.out.println(e); }
        }


        private void processRequest() throws Exception {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            
            playerCount = 0;
            ConnectFour board = new ConnectFour(7,6,'R','Y');
            // send welcome msg
            String welcomeMsg = "Welcome! Please enter your name: ";
            outToClient.writeBytes(welcomeMsg + "\r\n");

            // receive name from client
            String clientName = inFromClient.readLine();
            System.out.println(clientName + " joined the chat!");

            // Send hello msg to the newly connected client
            String helloMsg = "Hello " + clientName + "! to the ConnectFour lobby! If you would like to leave, type {quit}";
            outToClient.writeBytes(helloMsg + "\r\n");

            // broadcast
            for (String name : clients.keySet()) clients.get(name).writeBytes(clientName + " joined the chat!\r\n");

            // store this name with its corresponding output stream for broadcasting
            // (stored afterwards to avoid sending trivial messages to client)
            clients.put(clientName, outToClient);

            spectators.put(clientName, outToClient); // p

            // Get msg from client
            while (true) {
                String clientMessage = null;
                while ((clientMessage = inFromClient.readLine()) != null) {   
                    //p down                   
                    if (clientMessage.equals("{joingame}")) joinCommand(clientName);

                    else if (clientMessage.equals("{quit}")) quitCommand(clientName);

                    else if (clientMessage.contains("{place}") && full) {
                        if (players.containsKey(clientName)) {
                            if(playersArr.get(clientName) == 'R' && !redWent) {
                                int col = placeCommand(clientName, clientMessage); 
                                for (String names : clients.keySet()) { 
                                clients.get(names).writeBytes(board.playerTurn(playersArr.get( clientName ), col) + "\r\n");
                                clients.get(names).writeBytes(board.determineWin(playersArr.get( clientName )) + "\r\n"); 

                                }
                                redWent = true;                                
                            }
                            else if(playersArr.get(clientName) == 'Y' && redWent) {
                                int col = placeCommand(clientName, clientMessage);
                                for (String names : clients.keySet()) { 
                                    clients.get(names).writeBytes(board.playerTurn(playersArr.get( clientName ), col) + "\r\n");
                                    clients.get(names).writeBytes(board.determineWin(playersArr.get( clientName )) + "\r\n"); 

                            }
                                redWent = false;
                            }    
                            else players.get(clientName).writeBytes("You've already went, you must wait your turn.");
                            
                        }
                        else clients.get(clientName).writeBytes("You're not a player. Only players can make moves.");
                    }

                    else if (spectators.containsKey(clientName)) for (String name : spectators.keySet()) { 
                        if (name != clientName) spectators.get(name).writeBytes(clientName + ": " + clientMessage + "\r\n"); 
                    }

                    else for (String names : clients.keySet()) { 
                        if (names != clientName) clients.get(names).writeBytes(clientName + ": " + clientMessage + "\r\n"); 
                    }
                }
            }
        }

        private void joinCommand(String clientName) throws IOException {
            // if no one joined red and player hasn't joined yellow
            if (full == false) {
                //adds a player
                playerCount += 1;
                
                //add to player
                players.put(clientName, spectators.get(clientName));
                if(players.isEmpty()) playersArr.put( clientName, 'R' );
                else playersArr.put( clientName, 'Y' );
                //remove from spectator
                spectators.remove(clientName);
                
                System.out.println(clientName + " has joined the game");
                
                //tell everyone a player joined
                for (String name : clients.keySet()) clients.get(name).writeBytes(clientName + " has joined the game!\r\n");
                if (playerCount >= 2) full = true;
            } else {
                System.out.println(clientName + " tried to join, but the game is full");
                //tell everyone that spot is taken
                for (String name : clients.keySet()) clients.get(name).writeBytes(clientName + " tried to join, but the game is full\r\n");
            }
        }

        private void quitCommand(String clientName) throws IOException {
            clients.remove(clientName);
            System.out.println(clientName + " left the chat!");
            for (String name : clients.keySet()) clients.get(name).writeBytes(clientName + " left the chat!\r\n");
        }

        private int placeCommand(String clientName, String clientMessage) {
            int col = Integer.parseInt(clientMessage.split(" ")[1]); // this is the column to place the marker in
            System.out.println(clientName + " has placed a marker on column " + col);
            return col;
        }

        public static void main(String argv[]) throws Exception {
            // Create server socket
            ServerSocket serverSocket = new ServerSocket(25565);

            System.out.println("This server is ready to receive");

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
    
//    public static void main(String args[])
//    {
//    
//    //we assemble all the pieces of the puzzle for 
//    // building our Con nect Four Game
//    try (Scanner input = new Scanner(System.in)) {
//      // we create the ConnectFour instance
//      ConnectFour board = new ConnectFour(7, 6, 'R', 'Y');
//    
//      // we explain users how to enter their choices
//      System.out.println("Use 0-" + (board.getWidth() - 1) + " to choose a column");
//      // we display initial board
//      System.out.println(board.showBoard());
//    
//      // we iterate until max nb moves be reached
//      // simple trick to change player turn at each iteration
//      int moves = board.getMoves();
//      for (int player = 0; moves-- > 0; player = 1 - player) {
//        ConnectFour.playerTurn(board, player, 3);
//      }
//      System.out.println("Game over. No winner. Try again!");
//    }
  
    
    
}
