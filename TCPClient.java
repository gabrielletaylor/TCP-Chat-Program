// import necessary libraries
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;


// define the client class
public class TCPClient implements Runnable {

    // define instance variables
	private static JFrame loginFrm;
	private static JFrame chatFrm;
	private static JTextField userName;
	private static JTextField chatBar;
	private static JTextArea chat;
	private static JButton enter;
	private static JLabel prompt;
	private static JLabel welcome;
    private static String hostname;
    private static int port;
    public static String username;
    public static String message;

    private static Socket socket;

    // constructor to initialize hostname and port
    public TCPClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
    
    // main method to create and start the client
    public static void main(String[] args) {
    	
        TCPClient client = new TCPClient("localhost", 6789);
        welcome = new JLabel("Welcome to the Chat Room!");
        prompt = new JLabel("Please enter your username:");
        loginFrm = new JFrame("Client Chat");
    	
    	GridBagLayout gridLay = new GridBagLayout();
    	GridBagConstraints gbc = new GridBagConstraints();
    	gbc.insets = new Insets(2,2,2,2);
    	loginFrm.setLayout(gridLay);
    	gbc.weightx =1.0;
    	gbc.gridx = 0;
    	gbc.gridy = 2;
    	
    	loginFrm.setSize(300,300);
    	userName = new JTextField(10);
    	gridLay.setConstraints(userName, gbc);
    	
    	gbc.gridx =0;
    	gbc.gridy = 1;
    	gridLay.setConstraints(prompt, gbc);
    	
    	gbc.gridx = 0;
    	gbc.gridy = 0;
    	gridLay.setConstraints(welcome, gbc);
    	
    	enter = new JButton("Enter");
    	gbc.gridx = 0;
    	gbc.gridy = 3;
    	gridLay.setConstraints(enter, gbc);

    	
    	loginFrm.getContentPane().add(welcome);
    	loginFrm.getContentPane().add(prompt);
    	loginFrm.getContentPane().add(userName);
    	loginFrm.getContentPane().add(enter);
    	
    	chatFrm = new JFrame("Client Chat");
    	chatFrm.setLayout(new BorderLayout());
    	chatFrm.setSize(300,300);
    	chatBar = new JTextField(10);
    	
    	
    	chat = new JTextArea("Enter messages (type '.' to exit)\n");
    	

    	
    	
    	
        try {
        	
            // we are going to assume the hostname and port number are known to client 
            socket = new Socket(hostname, port);
            loginFrm.setVisible(true);

            Thread tempThread = new Thread(client);
            tempThread.start();
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            enter.addActionListener(new ActionListener()  {
            	public void actionPerformed(ActionEvent ae)
            	{
            		
                    username = userName.getText();
            		loginFrm.setVisible(false);
            		chatFrm.setTitle(username + "'s Chat");
            		
            		
            		   if(username != null)
                       {
                       	
                       	  try {
							output.writeBytes(username + " has joined the chat.\n");
							output.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                           
                       }
            
            		chat.setEditable(false);
                	chat.setBackground(Color.LIGHT_GRAY);
            		chatFrm.getContentPane().add(chat, BorderLayout.CENTER);
            		chatFrm.getContentPane().add(chatBar, BorderLayout.PAGE_END);
            		chatFrm.setVisible(true);
            	}
            });
            


            // loop to send messages
            
            chatBar.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent ae)
            	{
            		message = chatBar.getText();
            		chatBar.setText("");
            		if(socket.isConnected())
            		{
            			if(!message.equals("."))
                		{
                			
                			 try {
    							output.writeBytes(username + ": " + message + "\n");
    							output.flush();
    							
    						} catch (IOException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
                             
                			
                		}
            			
            			if(message.equals("."))
                		{
                			try {
    							output.writeBytes(username + " has left the chat.\n");
    	                        output.flush();
    	                        chatBar.setEnabled(false);
    	                        socket.close();

    	                        
    						} catch (IOException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
                		}
            		}
            		
            		
            		
            		
            	}
            });
            
            
         
            
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
            while (socket.isConnected()) {
                String message = input.readLine();
//                System.out.println(message);
                chat.append("\n" + message);
            }
        } catch (SocketException e) {
        	chat.append("\nYou left the chat.");
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}

