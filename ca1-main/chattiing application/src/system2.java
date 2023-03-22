import java.io.*;
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server.
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no
 * need to count bytes or to wait for a line feed at the end of the frame
 */

public class system2 implements Serializable {

    // The different types of message sent by the Client
    //usernumber to receive the list of the users connected
    // MESSAGE an ordinary text message
    //quiT to disconnect from the Server
    static final int usernumber= 0, MESSAGE = 2, quit = 1;
    private int type;
    private String message;

    // constructor
    system2(int type, String message) {
        this.type = type;
        this.message = message;
    }

    int getType() {
        return type;
    }

    String getMessage() {
        return message;
    }
}