// import necessary libraries
import java.io.*;
import java.net.*;
import java.util.*;

// define the client class
public class TCPClient implements Runnable {

    // define instance variables
    private static String hostname;
    private static int port;
    private String username;
    private static Socket socket;

    // constructor to initialize hostname and port
    public TCPClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    // main method to create and start the client
    public static void main(String[] args) {
        TCPClient client = new TCPClient("localhost", 6789);

        try {
            // we are going to assume the hostname and port number are known to client 
            socket = new Socket(hostname, port);
            System.out.println("Welcome to the Chat Room!"); //client prompts a welcome message 
            System.out.print("Please enter your username: "); // asks enter username using the keyboard

            // read user input for the username
            Scanner scanner = new Scanner(System.in);
            String username = scanner.nextLine();

            System.out.println("Enter messages (type '.' to exit)"); //to exit the chat, user enters "."

            Thread tempThread = new Thread(client);
            tempThread.start();

            // username will be sent to the server
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            output.writeBytes(username + " has joined the chat.\n");
            output.flush();

            // loop to send messages
            String message;
            while (true) {
                // System.out.println("Enter message (type '.' to exit): "); //to exit the chat, user enters "."
                message = scanner.nextLine();

                if (message.equals(".")) {
                    output.writeBytes(username + " has left the chat.\n");
                    output.flush();
                    break;
                }

                output.writeBytes(username + ": " + message + "\n");
                output.flush();
            }

            // close response
            socket.close();
            scanner.close();
        } catch (UnknownHostException ex) {
            System.out.println("server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String message = input.readLine();
                System.out.println(message);
            }
        } catch (SocketException e) {
            System.out.println("You left the chat.");
        } catch (Exception e) {

        }
    }
}