import java.io.*;
import java.net.*;
import java.util.*;


class ChatClient {
    static class WriteThread implements Runnable {
        Socket connectionSocket;
        DataOutputStream outToServer;
        
        public WriteThread(DataOutputStream out) {
            this.outToServer = out;
        }
        
        @Override
        public void run() {
            try {
                process();
            } catch (Exception e) {
                System.out.println(e); 
            }
        }    
        private void process() throws Exception {
            Scanner sc = new Scanner(System.in);
            while(true) {
                String clientMsg = sc.nextLine();
                this.outToServer.writeBytes( clientMsg + "\r\n");
                if(clientMsg.equals( "{quit}" ))
                    break;
            }
            sc.close();
        }
        
    }

    public static void main(String argv[]) throws Exception {
        Socket connectionSocket = new Socket("localhost", 12345);

        DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        
        //get welcome msg from server
        String welcomeMsg = inFromServer.readLine();
        System.out.println(welcomeMsg);
        
        //enter client name from console
        Scanner s = new Scanner(System.in);
        String name = s.nextLine();
        
        //send the name to the server
        outToServer.writeBytes( name + "\r\n" );
        
        //receive hello msg from server
        String helloMsg = inFromServer.readLine();
        System.out.println(helloMsg);
        
        WriteThread write = new WriteThread(outToServer);
        Thread writeThread = new Thread(write);
        writeThread.start();
        
        // Get msg from server
        while (true) {
        String serverMessage = null;
            while ((serverMessage = inFromServer.readLine()) != null) 
                System.out.println(serverMessage);
        }
                
//        s.close();
//        outToServer.close();
//        connectionSocket.close();
    }
}
