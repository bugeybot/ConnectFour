
import java.io.*;
import java.net.*;
import java.util.*;

class WebServer {
    private static Map<String, DataOutputStream> clients = new HashMap<>();
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

            //send welcome msg
            String welcomeMsg = "Welcome! Please enter your name...";
            outToClient.writeBytes( welcomeMsg + "\r\n");
            
            //receive name from client
            String clientName = inFromClient.readLine();
            System.out.println(clientName + " joined the chat!");
            
            //Send hello msg to the newly connected client
            String helloMsg = "Hello " + clientName + "! to the NP chatroom! If you would like to leave, type {quit}";
            outToClient.writeBytes( helloMsg + "\r\n");
           
            //broadcast
            for(String name : clients.keySet()) {
                    clients.get( name ).writeBytes(clientName + " joined the chat!\r\n");
            }   
            
            //store this name with its corresponding output stream for broadcasting (stored afterwards to avoid sending trivial messages to client)
            clients.put( clientName, outToClient );
            
         // Get msg from client
            while (true) {
            String clientMessage = null;
                while ((clientMessage = inFromClient.readLine()) != null) {
                    if(clientMessage.equals("{quit}")) {
                        clients.remove( clientName );
                        System.out.println(clientName + " left the chat!");
                        for(String name : clients.keySet()) {
                            clients.get( name ).writeBytes(clientName + " left the chat!\r\n");
                        }
                        continue;
                    }
                    for(String name : clients.keySet()) {
                        if(name != clientName)
                            clients.get( name ).writeBytes(clientName + ": " + clientMessage + "\r\n");
                    }
                }
            }

            
            /* String clientMessage = inFromClient.readLine();
            System.out.println("RECEIVED: " + clientMessage);
            
            // Parse the request line to get the file name
            String fileName = clientMessage.split(" ")[1].substring(1);
            System.out.println("Requested file name: " + fileName);
            
            // Read the content of the file
            String statusLine = null;
            Scanner sc = null;
            try {
                // happy path: file is found
                statusLine = "HTTP/1.1 200 OK\r\n";
                sc = new Scanner(new File(fileName));
            }
            catch (Exception e) {
                // not so happy path: file is not found
                statusLine = "HTTP/1.1 404 Not Found\r\n";
                sc = new Scanner(new File("err.html"));
            }
            
            // Send the status line
            outToClient.writeBytes(statusLine);
            
            // Send the separator
            outToClient.writeBytes("\r\n");
            
            // Send file data
            while (sc.hasNextLine()) {
               outToClient.writeBytes(sc.nextLine() + "\r\n"); 
            }
            */
            // Close connection socket once it is done
            //connectionSocket.close(); not closed because we aren't done receiving
        }
    }
    
    public static void main(String argv[]) throws Exception {
        // Create server socket
        ServerSocket serverSocket = new ServerSocket(12345);

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
