import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer implements Runnable {

    private Socket socket;
    private ArrayList<String> allUsers = new ArrayList<String>();
    private ArrayList<String> signedInUsers = new ArrayList<String>();
    String clientMessage, outMessage;

    TCPServer(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            TCPServer server = new TCPServer(connectionSocket);
            // Thread tempThread = new Thread(server);
            // tempThread.start();

            server.run();
        }
    }

    @Override
    public void run() {
        try {
            // get input stream from client
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            clientMessage = inFromClient.readLine();
            System.out.println(clientMessage);

            // check if user has sent sign-in message
            if (clientMessage.contains("has joined the chat")) {
                // get the username of the user
                String[] newUser = clientMessage.split(" ");
                // add the user to all users and currently signed in users
                allUsers.add(newUser[0]);
                signedInUsers.add(newUser[0]);
            }
            // check if user has sent sign-off message
            else if (clientMessage.contains("has left the chat")) {
                // get the username of the user
                String[] username = clientMessage.split(" ");
                // remove the user from currently signed in users
                signedInUsers.remove(username[0]);
            }

            // output stream to send messages to clients
            DataOutputStream outToClient = new DataOutputStream(this.socket.getOutputStream());
            // synchronized to prevent more than one thread from writing at a time
            synchronized (this) {
                outToClient.writeBytes(clientMessage + "\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}