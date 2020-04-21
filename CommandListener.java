import java.util.*; 
import java.lang.*; 
import java.net.*;
import java.io.*;
public class CommandListener extends Thread{

    static int PORT  = 12000;
    static int timeBeforeRetry = 1000;//milliseconds
    static InetAddress HOST; 
    static int HASH = 256;
    static String QUIT = "Quit";
    static String STORE = "Store";
    private String input = "";
    Peer peer;
    public CommandListener(Peer peer) {
        this.peer = peer;
    }


    public void run() {

        //https://stackoverflow.com/questions/4005574/java-key-listener-in-commandline
        //used code from here for getting additional user input, removed the while condition and the in.close() since it never reaches
        try {
        while (true) {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            input = in.readLine();
            if (input.equals(QUIT)) {
                //System.out.println(quit);
                sendQuit (peer.get_firstpred(), peer.get_peerid(), peer.get_firstsucc(), peer.get_secondsucc() );
                System.out.println(Integer.toString(peer.get_secondpred()));
                sendQuit (peer.get_secondpred(), peer.get_peerid(), peer.get_firstpred(), peer.get_firstsucc());
                System.out.println("EXITING");
                System.exit(0);
            }
            else if(input.contains(STORE)) {
                String num = input.replaceAll("\\D+", "");
                int filenum = Integer.parseInt(num);
                int hash = filenum % HASH;
                if (hash == peer.get_peerid() || (hash > peer.get_firstpred() && hash < peer.get_peerid()) || (hash > peer.get_peerid() && hash < peer.get_firstsucc())) {
                    System.out.println("Store " + num+ " request accepted");
                    peer.add_files(num);
                }
                else {
                    JoinPing fileRequest = new JoinPing();
                    String message = "Store " + num + " request forwarded to my successor";
                    System.out.println(message);
                    fileRequest.sendTCP(peer.get_firstsucc(), message);
                }

            }
            //in.close();
        }
        }
        catch(IOException e) {
            System.out.println(e);
        }
        
    }

    public void sendQuit(int predecessor, int peerid, int firstsucc, int secondsucc) {
        Socket socket = null;
        int port = PORT + predecessor;
        try {
            HOST = InetAddress.getByName("127.0.0.1");
            //retries connection in case of Connection Refused Error
            System.out.println("tranquillo");
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
        catch(IOException e) {
            System.out.println(e);
        }


    } 
}