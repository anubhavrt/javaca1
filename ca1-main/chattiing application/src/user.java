import java.net.*;
import java.io.*;
import java.util.*;


//The Client that can be run as a console
public class user  {

    // notification
    private String notif = " *** ";

    // for I/O
    private ObjectInputStream sInput;		// to read from the socket
    private ObjectOutputStream sOutput;		// to write on the socket
    private Socket socket;					// socket object

    private String server, username;	// server and username
    private int port;					//port

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    user(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    /*
      To start the chat
     */
    public boolean start() {
        // try to connect to the server
        try {
            socket = new Socket(server, port);
        }
        // exception handler if it failed
        catch(Exception ec) {
            display("Error connecting to server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        /* Creating both Data Stream */
        try
        {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the server
        new ListenFromServer().start();
        // Send our username to the server this is the only message that we
        // will send as a String. All other messages will be system2 objects
        try
        {
            sOutput.writeObject(username);
        }
        catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }
        // success we inform the caller that it worked
        return true;
    }

    /*
     * To send a message to the console
     */
    private void display(String msg) {

        System.out.println(msg);

    }

    /*
     * To send a message to the server
     */
    void sendMessage(system2 msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    /*
      When something goes wrong
     *Close the Input/Output streams and disconnect
     */
    private void disconnect() {
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {}
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {}
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {}

    }

    public static void main(String[] args) {
        // default values if not entered
        int portNumber =1000;
        String serverAddress ="localhost";
        String userName = "Anonymous";
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter the username: ");
        userName = scan.nextLine();

        // different case according to the length of the arguments.
        switch(args.length) {
            case 3:
                // for > javac Client username portNumber serverAddr
                serverAddress = args[2];
            case 2:
                // for > javac Client username portNumber
                try {
                    portNumber = Integer.parseInt(args[1]);
                }
                catch(Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
                    return;
                }
            case 1:
                // for > javac Client username
                userName = args[0];
            case 0:
                // for > java Client
                break;
            // if number of arguments are invalid
            default:
                System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
                return;
        }
        // create the user object
        user client = new user(serverAddress, portNumber, userName);
        // try to connect to the server and return if not connected
        if(!client.start()) {
            return;
        }
        System.out.println("\nNamaste! thanks forn using our app.");
        System.out.println("please read following guidlines:");
        System.out.println("1. please start typing messages ");
        System.out.println("2. to send message privately, type: '#<username><space><type message>'");
        System.out.println("3. Type 'usernumber' without quotes to see list of active users");
        System.out.println("4. Type 'quit' to signoff");

        // infinite loop to get the input from the user
        while(true) {
            System.out.print("> ");
            // read message from user
            String msg = scan.nextLine();
            // logout if message is quit
            if(msg.equalsIgnoreCase("quit")) {
                client.sendMessage(new system2(system2.quit, ""));
                break;
            }
            // message to check who are present in chatroom
            else if(msg.equalsIgnoreCase("usernumber")) {
                client.sendMessage(new system2(system2.usernumber, ""));
            }
            // regular text message
            else {
                client.sendMessage(new system2(system2.MESSAGE, msg));
            }
        }
        // close resource
        scan.close();
        //. disconnect client.
        client.disconnect();
    }

    /*
     * a class that waits for the message from the server
     */
    class ListenFromServer extends Thread {

        public void run() {
            while(true) {
                try {
                    // read the message form the input datastream
                    String msg = (String) sInput.readObject();
                    // print the message
                    System.out.println(msg);
                    System.out.print("> ");
                }
                catch(IOException e) {
                    display(notif + "Server has closed the connection: " + e + notif);
                    break;
                }
                catch(ClassNotFoundException e2) {
                }
            }
        }
    }
}