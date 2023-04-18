// import java.io.*; 
// import java.net.*; 

// public class TCPClient implements Runnable {

//     public static void main(String[] args) {

//     }

//     @Override
//     public void run() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'run'");
//     }
// }
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

    // main method to create and start the client
    public static void main(String[] args) {
        TCPClient client = new TCPClient("localhost", 6789);
        client.start();
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
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeBytes(username + " has joined the chat.\n");
            output.flush();
            // OutputStream output = socket.getOutputStream();
            // PrintWriter writer = new PrintWriter(output, true);
            // writer.println(username + " has joined the chat.");
            // writer.flush();

            // receive acknowledgement from server
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String serverResponse = reader.readLine();
            System.out.println(serverResponse);

            // loop to send and receive messages
            String message;
            while (true) {
                System.out.print("Enter message (type '.' to exit: "); //to exit the chat, user enters "."
                message = scanner.nextLine();

                if (message.equals(".")) {
                    output.writeBytes(username + " has left the chat.\n");
                    output.flush();
                    break;
                }

                output.writeBytes(username + " - " + message + "\n");
                output.flush();

                // receive messages from server
                serverResponse = reader.readLine();
                System.out.println(serverResponse);
            }

            serverResponse = reader.readLine();
            System.out.println(serverResponse);

            // close response
            socket.close();
            scanner.close();
            // writer.close();
            reader.close();
        } catch (UnknownHostException ex) {
            System.out.println("server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}