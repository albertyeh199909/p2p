import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;



// for sending and forwarding join request
public class JoinPing extends Thread{

    static int PORT  = 12000;
    static Socket socket = null;
    static int timeBeforeRetry = 5000;//milliseconds
    static InetAddress HOST; 
    private int succ1;
    private int peerid;

    public JoinPing (int succ1, int peerid) {
        this.succ1 = succ1;
        this.peerid = peerid;
    }
    //sends join requests
    public void run () {

        int port1 = PORT + succ1;
        try {
            HOST = InetAddress.getByName("127.0.0.1");
            //https://stackoverflow.com/questions/47834435/better-way-to-handle-connection-refused-when-server-is-down
            //got idea to retry connection in case listener was not yet ran
            //retries connection in case of Connection Refused Error
            while (socket == null) {
                try {
                    socket = new Socket(HOST, port1);
                } catch(ConnectException e) {
                    System.out.println(e);
                    try {
                        Thread.sleep(timeBeforeRetry);
                    } catch(InterruptedException er) {
                        System.out.println(er);
                    }
                }
                catch(IOException err) {
                    System.out.println(err);
                }

            }    
                String sentence= "Peer " + Integer.toString(peerid) + " Join request forwarded to my successor";
                DataOutputStream outToPeer = new DataOutputStream(socket.getOutputStream());
                outToPeer.writeBytes(sentence + '\n');
                socket.close();
        }
        catch(IOException e) {
            System.out.println(e);
        }


    } 

   
    
}