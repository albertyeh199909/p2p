//package assignment1;
import java.io.*;
import java.net.*;
import java.util.*;

import java.util.concurrent.locks.*;



public class Receive extends Thread {
    static byte[] sendData = new byte[1024];
    
    static int PORT  = 12000; 
    static ReentrantLock syncLock = new ReentrantLock();

    Peer peer;
    private int peerid;


    public Receive(int peerid, Peer peer) {
        this.peerid = peerid;
        this.peer = peer;
    }

    public void run() {
        int port = PORT + peerid;
        System.out.println(Integer.toString(port));
        DatagramSocket serverSocket;
        InetAddress HOST; 

        try {
            serverSocket = new DatagramSocket(port);
            HOST = InetAddress.getByName("127.0.0.1");
        
        
            String sentence = null;
            //prepare buffers
            byte[] receiveData = null;
            String serverMessage = null;
        
    
        
            while (true) {
                //receive UDP datagram
                receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    serverSocket.receive(receivePacket);
                }
                catch(IOException e){
                    System.out.println(e);
                }

                //get data
                sentence = new String(receivePacket.getData());
                //Need only the data received not the spaces till size of buffer
                sentence=sentence.trim();
                
                if (sentence.contains("response")) {
                    System.out.println(sentence);
                    String peernum = sentence.replaceAll("\\D+", "");
                    int num = Integer.parseInt(peernum);
                    if (num == peer.get_firstsucc()) {
                        peer.set_packetLoss1(0);
                        peer.set_firstalive(true);
                    }
                    else {
                        peer.set_packetLoss2(0);
                        peer.set_secondalive(true);
                    }
                    
                    continue;
                }
                
                if (sentence.contains("Ping request message")) {
                    String peernum = sentence.replaceAll("\\D+", "");
                    //System.out.println("Message is :" + sentence);
                    //System.out.println("Peernum is : " + peernum);
                    int num = Integer.parseInt(peernum);
                    int peerReceived = PORT + num;
                    if(sentence.contains("firstsucc")) {
                        peer.set_firstpred(num);
                        //System.out.println(peer.get_predecessor());
                    }
                    else if(sentence.contains("secondsucc")) {
                        peer.set_secondpred(num);
                    }
                    String[] arrOfStr = sentence.split("@", 3); 

                    System.out.println(arrOfStr[0]);
                


                
    
                    serverMessage = "Ping response received from Peer " + Integer.toString(peerid);
                    
                    //prepare to send reply back
                    sendData = serverMessage.getBytes();
                    
                    //send it back to client on SocktAddress sAddr
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, HOST, peerReceived);
                    try {
                        serverSocket.send(sendPacket);
                    }
                    catch(IOException e){
                        System.out.println(e);
                    }
                }
                //This should be the main
                //System.out.println(Thread.currentThread().getName());

            } // end of while (true)
        }
        catch(SocketException e) {
            System.out.println(e);
            System.out.println("Receive.java");
        }
        catch (UnknownHostException e) {
            System.out.println(e);
        }
    }
}