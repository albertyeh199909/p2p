import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.*;
//import assignment1.*;

public class p2p extends Thread{

    static int PORT  = 12000; 

    
    
	public static void main(String[] args)throws Exception {
        InetAddress HOST = InetAddress.getByName("127.0.0.1");
        String type = args[0];
        Peer peer;  
        if (type.equals("init")) { 
            peer = new Peer(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
            JoinReceive joinlistener = new JoinReceive(peer);
            joinlistener.start();
            
        }
        else {
            peer = new Peer(Integer.parseInt(args[1]), Integer.parseInt(args[3]));
            JoinReceive joinlistener = new JoinReceive(peer);
            joinlistener.start();
            JoinPing joinsender = new JoinPing(Integer.parseInt(args[2]), Integer.parseInt(args[1]));
            joinsender.start();
            //joinlistener.send(Integer.parseInt(args[2]), Integer.parseInt(args[1]));
            
        }
        //get user input
        CommandListener input = new CommandListener(peer);
        input.start();
        //listen to incoming pings
        Receive listener = new Receive(peer.get_peerid(), peer);
        listener.start();
        //ping successors 
        Ping pingsucc = new Ping(peer);
        pingsucc.start();
        


    }

    
}