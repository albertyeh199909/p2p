import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;



// for sending and forwarding join request
public class SendQuit extends Thread{

    static int PORT  = 12000;
    static Socket socket = null;
    static int timeBeforeRetry = 5000;//milliseconds
    static InetAddress HOST; 
    private int predecessor;
    private int firstsucc;
    private int secondsucc;
    private int peerid;

    public SendQuit (int predecessor, int firstsucc, int secondsucc, int peerid) {
        this.predecessor = predecessor;
        this.firstsucc = firstsucc;
        this.secondsucc = secondsucc;
        this.peerid = peerid;
    }
    public void run() {

        int port = PORT + predecessor;
        try {
            HOST = InetAddress.getByName("127.0.0.1");
            //retries connection in case of Connection Refused Error
            while (socket == null) {
                try {
                    socket = new Socket(HOST, port);
                } catch(ConnectException e) {
                    // Log if you want
                    try {
                        Thread.sleep(timeBeforeRetry);
                    } catch(InterruptedException er) {}
                }
                catch(IOException err) {}

            }    

                String sentence= "Peer " + Integer.toString(peerid) + " will depart from this network" + "@My new first successor is Peer " + Integer.toString(firstsucc) + "@My new second successor is Peer " + Integer.toString(secondsucc);
                DataOutputStream outToPeer = new DataOutputStream(socket.getOutputStream());
                outToPeer.writeBytes(sentence + '\n');
                socket.close();
        }
        catch(IOException e) {}


    } 
    
}