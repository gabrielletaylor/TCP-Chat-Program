import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer implements Runnable {

    private static Socket socket;
    private ArrayList<String> allUsers = new ArrayList<String>();
    private static ArrayList<Socket> clients = new ArrayList<>();
    private ArrayList<String> signedInUsers = new ArrayList<String>();

    TCPServer(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while (true) {
            socket = welcomeSocket.accept();
            clients.add(socket);
            TCPServer server = new TCPServer(socket);
            Thread tempThread = new Thread(server);
            tempThread.start();
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String clientMessage = inFromClient.readLine();

                if (clientMessage == null) {
                    break;
                }

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
                    // remove the socket from the clients
                    clients.remove(socket);
                }

                sendMessage(clientMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        // output stream to send messages to all clients
        System.out.println("sockets: " + clients.size());

        for (int i = 0; i < clients.size(); i++) {
            Socket socket = clients.get(i);
            try {
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                // synchronized to prevent more than one thread from writing at a time
                synchronized (this) {
                    outToClient.writeBytes(message + "\n");
                }
                outToClient.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}