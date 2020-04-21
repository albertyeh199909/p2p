import java.io.*;
import java.net.*;

public class JoinReceive extends Thread {

    static int PORT  = 12000;
    static InetAddress HOST;
    static int HASH = 256; 
    private Peer peer;

    public JoinReceive(Peer peer) {
        this.peer = peer;
    }
	public void run() {
        
		int port = PORT + peer.get_peerid();

        
        
        try {
		    ServerSocket welcomeSocket = new ServerSocket(port);
            System.out.println("Listening");
            while (true){

                // accept connection from connection queue
                Socket connectionSocket = welcomeSocket.accept();
                
                //System.out.println("Incoming");

                // create read stream to get input
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                String clientSentence;
                clientSentence = inFromClient.readLine();
                //data from client is stored in clientSentence
                
                //process incoming join requests
                if (clientSentence.contains("Join request forwarded")) {
                    String peernum = clientSentence.replaceAll("\\D+", "");
                    int num = Integer.parseInt(peernum);

                    //decide whether join request should be forwarded or handled by this peer
                    if ((num > peer.get_peerid() && num > peer.get_firstsucc()) && peer.get_peerid() > peer.get_firstsucc()) {
                        newsucc(peer.get_firstsucc(), peer.get_secondsucc(), num, 1);
                        newsucc(peer.get_peerid(), num, peer.get_firstpred(), 0);
                        changeSucc(peer, num, peer.get_firstsucc());
                        System.out.println("Peer " + Integer.toString(num) + " Join request received");
                        System.out.println("My new first successor is Peer " + Integer.toString(peer.get_firstsucc()));
                        System.out.println("My new second successor is Peer " + Integer.toString(peer.get_secondsucc()));
                    }
                    
                    else if (num > peer.get_peerid() && num < peer.get_firstsucc())  {
                        newsucc(peer.get_firstsucc(), peer.get_secondsucc(), num, 1);
                        newsucc(peer.get_peerid(), num, peer.get_firstpred(), 0);
                        changeSucc(peer, num, peer.get_firstsucc());
                        System.out.println("Peer " + Integer.toString(num) + " Join request received");
                        System.out.println("My new first successor is Peer " + Integer.toString(peer.get_firstsucc()));
                        System.out.println("My new second successor is Peer " + Integer.toString(peer.get_secondsucc()));  

                    }
                    else if (num > peer.get_peerid() && num > peer.get_firstsucc()) {
                        System.out.println(clientSentence);
                        JoinPing joinsender = new JoinPing();
                        String message = "Peer " + Integer.toString(num) + " Join request forwarded to my successor";
                        joinsender.sendTCP(peer.get_firstsucc(), message);
                    }

                }
                else if(clientSentence.contains("Store")) {
                    String filenum = clientSentence.replaceAll("\\D+", "");
                    int fileHash = Integer.parseInt(filenum) % HASH;
                    
                    if((fileHash > peer.get_peerid() && fileHash < peer.get_firstsucc()) || fileHash == peer.get_peerid()) {
                        System.out.println("Store " + filenum + " request accepted");
                        peer.add_files(filenum);
                        System.out.println(peer.get_files().get(0));
                    }
                    else if((fileHash > peer.get_peerid() && fileHash > peer.get_firstsucc()) && peer.get_peerid() > peer.get_firstsucc()) {
                        System.out.println("Store " + filenum + " request accepted");
                        peer.add_files(filenum);
                        System.out.println(peer.get_files().get(0));
                    }
                    else if(fileHash > peer.get_peerid() && fileHash >= peer.get_firstsucc()) {
                        System.out.println(clientSentence);
                        JoinPing joinsender = new JoinPing();
                        joinsender.sendTCP(peer.get_firstsucc(), clientSentence);
                    }
                }
                //process successor change requests
                else if (clientSentence.contains("requesting your successors")) {
                    String peernum = clientSentence.replaceAll("\\D+", "");
                    int replyPeer = Integer.parseInt(peernum);
                    newsucc(peer.get_firstsucc(), peer.get_secondsucc(), replyPeer, 2);

                }
                else if (clientSentence.contains("Peer no longer alive")) {
                    String[] arrOfStr = clientSentence.split("@", 5); 
                    String getnum = arrOfStr[1].replaceAll("\\D+", "");
                    int firstsucc = Integer.parseInt(getnum);
                    getnum = arrOfStr[2].replaceAll("\\D+", "");
                    int secondsucc = Integer.parseInt(getnum);
                    if(firstsucc != peer.get_secondsucc()) {
                        peer.set_secondsucc(firstsucc);
                    }
                    else {
                        peer.set_secondsucc(secondsucc);
                    }
                    System.out.println("My new first successor is " + Integer.toString(peer.get_firstsucc()));
                    System.out.println("My new second successor is " + Integer.toString(peer.get_secondsucc()));
                }
                else {
                    String[] arrOfStr = clientSentence.split("@", 5); 
                    String getnum = arrOfStr[1].replaceAll("\\D+", "");
                    int firstsucc = Integer.parseInt(getnum);
                    getnum = arrOfStr[2].replaceAll("\\D+", "");
                    int secondsucc = Integer.parseInt(getnum);
                    changeSucc(peer, firstsucc, secondsucc);
                    for (String a : arrOfStr) {
                        System.out.println(a); 
                    } 
                }

                
                

                
                connectionSocket.close();
                
            } 
        }
        catch(UnknownHostException e){
            System.out.println(e);
        }
        catch(IOException e){
            System.out.println(e);
            System.out.println("TCP receive");
        }
        catch(Exception e){
            System.out.println(e);
        }

	

        
    }
    //send new successors to appropriate peers
    //receiver predecessor = 0, joining peer = 1, abrupt leave = 2

    public void newsucc(int firstsucc, int secondsucc, int peerid, int receiver) throws Exception {
        int port1 = PORT + peerid;
        HOST = InetAddress.getByName("127.0.0.1");
        Socket socket = new Socket(HOST, port1);

        String message;
        if (receiver == 0) {
            message = "Successor Change request received@";
        }
        else if (receiver == 1) {
            message = "Join request has been accepted@";
        }
        else {
            message = "Peer no longer alive@";
        }
        String sentence= message + "My new first successor is Peer " + Integer.toString(firstsucc) + "@My new second successor is Peer " + Integer.toString(secondsucc);
        DataOutputStream outToPeer = new DataOutputStream(socket.getOutputStream());
        outToPeer.writeBytes(sentence + '\n');
        socket.close();
    }  

    public static void changeSucc(Peer peer, int firstsucc, int secondsucc) throws Exception{
        peer.set_firstsucc(firstsucc);
        peer.set_secondsucc(secondsucc);

    }

} 