import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;



// for sending and forwarding join request
public class JoinPing {

    static int PORT  = 12000;
    static int timeBeforeRetry = 5000;//milliseconds
    static InetAddress HOST; 
    
    //sends join requests
    public void sendTCP (int receiver, String message) {
        Socket socket = null;
        int port1 = PORT + receiver;
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
                String sentence= message;
                DataOutputStream outToPeer = new DataOutputStream(socket.getOutputStream());
                outToPeer.writeBytes(sentence + '\n');
                socket.close();
        }
        catch(IOException e) {
            System.out.println(e);
        }


    } 

   
    
}