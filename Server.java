
/*
 * Threaded UDPServer
 * Compile: javac UDPServer.java
 * Run: java UDPServer PortNo
 */

package assignment1;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.*;

public class UDPServer extends Thread{

    //static List<SocketAddress> clients=new ArrayList<SocketAddress>();
    static byte[] sendData = new byte[1024];
    static DatagramSocket serverSocket;
    static int UPDATE_INTERVAL = 1000;//milliseconds
    static ReentrantLock syncLock = new ReentrantLock();
    
	public void receive (int serverPort, int peerid)throws Exception {
        //Assign Port no
		serverSocket = new DatagramSocket(serverPort);
        
        String sentence = null;
        //prepare buffers
        byte[] receiveData = null;
        String serverMessage = null;
        SocketAddress sAddr;
        //Start the other sending thread
        //UDPServer us=new UDPServer();
        //us.start();
        
        while (true){
            //receive UDP datagram
            receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            
            //get data
            sentence = new String(receivePacket.getData());
            //Need only the data received not the spaces till size of buffer
            sentence=sentence.trim();

            System.out.println(sentence);
            if sentence.contains("received") {
                continue;
            }
            
            //get lock
            syncLock.lock();
            sAddr = receivePacket.getSocketAddress();
            serverMessage = "Ping response received from Peer " + Integer.toString(peerid);
            
            //prepare to send reply back
            sendData = serverMessage.getBytes();
            
            //send it back to client on SocktAddress sAddr
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, sAddr);
            serverSocket.send(sendPacket);
            //This should be the main
            //System.out.println(Thread.currentThread().getName());
            syncLock.unlock();
        } // end of while (true)
        
	} // end of main()
// We will send from this thread
    public void run(){
        while(true){
            //get lock
            syncLock.lock();

            String message= "Ping request message received from Peer " + Integer.toString(peerid);
            sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clients.get(j));
            try{
                serverSocket.send(sendPacket);
            } catch (IOException e){ }

            //release lock
            syncLock.unlock();
        //sleep for UPDATE_INTERVAL
            try{
                Thread.sleep(UPDATE_INTERVAL);//in milliseconds
            } catch (InterruptedException e){
                System.out.println(e);
            }
       // System.out.println(Thread.currentThread().getName());
        }// while ends
    } //run ends
} // end of class UDPServer
