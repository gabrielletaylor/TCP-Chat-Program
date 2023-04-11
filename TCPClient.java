// import necessary libraries
import java.io.*;
import java.net.*;
import java.util.*;

// define the client class
public class TCPClient {

    // define instance variables
    private String hostname;
    private int port;
    private String username;

    // constructor to initialize hostname and port
    public TCPClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    // method to start the client
    // client contacts server using TCP sockets 
    public void start() {
        try {
            // we are going to assume the hostname and port number are known to client 
            Socket socket = new Socket(hostname, port);
            System.out.println("Welcome to the Chat Room!"); //client prompts a welcome message 
            System.out.print("Please enter your username: "); // asks enter username using the keyboard

            // read user input for the username
            Scanner scanner = new Scanner(System.in);
            username = scanner.nextLine();

            // username will be sent to the server
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(username);

            // receive acknowledgement from server
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String serverResponse = reader.readLine();
            System.out.println(serverResponse);

            // loop to send and receive messages
            String message;
            do {
                System.out.print("Enter message (type '.' to exit: "); //to exit the chat, user enters "."
                message = scanner.nextLine();

                // send message to server
                writer.println(message);

                // receive messages from server
                serverResponse = reader.readLine();
                System.out.println(serverResponse);
    
            }while (!message.equals("."));

            // send sign-on message to server
            writer.println(username + " has left the chat.");

            // close response
            socket.close();
            scanner.close();
            writer.close();
            reader.close();
        } catch (UnknownHostException ex) {
            System.out.println("server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }

        // main method to create and start the client
        public static void main(String[] args) {
            TCPClient client = new TCPClient("localhost", 8000);
            client.start();
        }
    }
}