package finaljk;

import java.lang.Math;
import java.io.*;
import java.net.*;
import java.util.*;

class Network {  
    static int bitLoss = 0;
    static int HostASendSocket = 7006;
    static int HostAReceiveSocket = 7009;
    static int HostBSendSocket = 7008;
    static int HostBReceiveSocket = 7007;
    
    public static void main(String args[]) throws Exception {
        System.out.println("What percentage of packets would you like to be dropped?");
        Scanner scan = new Scanner(System.in);
        bitLoss = scan.nextInt();
        
        
        NetworkThread HostA = new NetworkThread("HostA",HostASendSocket,HostBReceiveSocket,bitLoss);
        Thread HostAThread = new Thread(HostA);
        HostAThread.start();
        
        NetworkThread HostB = new NetworkThread("HostB",HostBSendSocket,HostAReceiveSocket,bitLoss);
        Thread HostBThread = new Thread(HostB);
        HostBThread.start();
        
        HostAThread.join();
        HostBThread.join();
    }
        
    public static boolean drop () throws IOException{
        int minimum = 0;
        int maximum = 99;

        int randomNum = minimum + (int)(Math.random() * maximum);
        if (randomNum > bitLoss){
            return false;

        }
        else return true;

    }
}



