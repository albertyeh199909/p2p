//package assignment1;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;




public class Ping extends Thread{

    static byte[] sendData = new byte[1024];
    static byte[] sendData1 = new byte[1024];
    static int PORT  = 12000;
    static int permittedLoss = 3;
    static int timeBeforeRetry = 5000;
    static InetAddress HOST; 
    Peer peer;



    public Ping (Peer peer) {
        this.peer = peer;
    }
    public void run(){
        try {
            DatagramSocket socket = new DatagramSocket();
            HOST = InetAddress.getByName("127.0.0.1");
            
            //get por to of successors and send pings
            while(true){
                int port1 = PORT + peer.get_firstsucc();
                int port2 = PORT + peer.get_secondsucc(); 
                int peerid = peer.get_peerid();
                int UPDATE_INTERVAL = peer.get_ping() * 1000;

                String message= "Ping request message received from Peer " + Integer.toString(peerid) + "@firstsucc";
                String message1 = "Ping request message received from Peer " + Integer.toString(peerid) + "@secondsucc";
                sendData = message.getBytes();
                sendData1 = message1.getBytes();
                DatagramPacket sendPacket1 = new DatagramPacket(sendData, sendData.length, HOST,  port1);
                DatagramPacket sendPacket2 = new DatagramPacket(sendData1, sendData1.length, HOST,  port2);

                peer.set_firstalive(false);
                peer.set_secondalive(false);

                try{
                    socket.send(sendPacket1);
                    socket.send(sendPacket2);
                } catch (IOException e){
                    System.out.println(e);
                    System.out.println("Send Packet");
                }
                System.out.println("Ping requests sent to Peers " + peer.get_firstsucc() +  " and " + peer.get_secondsucc());
            //sleep for UPDATE_INTERVAL
                
                if(peer.get_firstalive() == false) {
                    peer.set_packetLoss1(peer.get_packetLoss1() + 1);
                    if (peer.get_packetLoss1() >= permittedLoss) {
                        int receiver = peer.get_secondsucc();
                        System.out.println("Peer " + Integer.toString(peer.get_firstsucc()) + " is no longer alive");
                        peer.set_firstsucc(peer.get_secondsucc());
                        firstsucc_request(peer.get_peerid(), peer.get_firstsucc());
                        peer.set_packetLoss1(0);
                    }
                }
                if(peer.get_secondalive() == false) {
                    peer.set_packetLoss2(peer.get_packetLoss2() + 1);
                    if (peer.get_packetLoss2() >= permittedLoss) {
                        System.out.println("Peer " + Integer.toString(peer.get_secondsucc()) + " is no longer alive");
                        firstsucc_request(peer.get_peerid(), peer.get_firstsucc());
                        peer.set_packetLoss2(0);
                    }
                }
                try{
                    Thread.sleep(UPDATE_INTERVAL);//in milliseconds
                } catch (InterruptedException e){
                    System.out.println(e);
                }

            }// while ends
        }
        catch(SocketException e){
            System.out.println(e);
            System.out.println("Ping.java");
        }
        catch(UnknownHostException  e){
            System.out.println(e);
        }
    } //run ends
    public void firstsucc_request(int peerid, int receiver) {
        Socket socket = null;
        int port = PORT + receiver;
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

                String sentence= "Peer " + Integer.toString(peerid) + " requesting your successors"; 
                DataOutputStream outToPeer = new DataOutputStream(socket.getOutputStream());
                outToPeer.writeBytes(sentence + '\n');
                socket.close();
        }
        catch(IOException e) {
            System.out.println(e);
            System.out.println("firstsucc_request");
        }


    } 
    

}

