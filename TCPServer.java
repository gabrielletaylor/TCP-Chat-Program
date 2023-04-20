import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class TCPServer implements Runnable {

    private Socket socket;
    private ArrayList<String> allUsers = new ArrayList<String>();
    private ArrayList<String> signedInUsers = new ArrayList<String>();
    private static ArrayList<Socket> clients = new ArrayList<>();

    TCPServer(Socket socket) {

        this.socket = socket;
    }

    private Socket getSocket() {
        return this.socket;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while (true) {
            Socket socket = welcomeSocket.accept();
            TCPServer server = new TCPServer(socket);
            clients.add(server.getSocket());
            Thread tempThread = new Thread(server);
            tempThread.start();
        }
    }

    @Override
    public void run() {
    	
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            while (true) {
                String clientMessage = inFromClient.readLine();
//                System.out.println(clientMessage);
                
                // check if user has sent sign-in message
                if (clientMessage.contains("has joined the chat")) {
                    // get the username of the user
                    String[] newUser = clientMessage.split(" ");
                    // add the user to all users and currently signed in users
                    allUsers.add(newUser[0]);
                    signedInUsers.add(newUser[0]);
                    System.out.println(clientMessage);
                }
                // check if user has sent sign-off message
                else if (clientMessage.contains("has left the chat")) {
                    // get the username of the user
                    String[] username = clientMessage.split(" ");
                    // remove the user from currently signed in users
                    signedInUsers.remove(username[0]);
                    // remove the socket from the clients
                    clients.remove(socket);
                    System.out.println(clientMessage);
                }
                // send regular message to all clients
                sendMessage(socket, clientMessage);
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    // synchronized to prevent more than one thread from writing at a time
    public synchronized void sendMessage(Socket sender, String message) {
        // output stream to send messages to all clients
        for (int i = 0; i < clients.size(); i++) {
            Socket socket = clients.get(i);
            try {
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                outToClient.writeBytes(message + "\n");
                outToClient.flush();
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
    }
    
    
}