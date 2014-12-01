package finaljk;


import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


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
        
        
        NetworkThreadObj HostA = new NetworkThreadObj("HostA",HostASendSocket,HostBReceiveSocket,bitLoss);
        Thread HostAThread = new Thread(HostA);
        HostAThread.start();
        
        NetworkThreadObj HostB = new NetworkThreadObj("HostB",HostBSendSocket,HostAReceiveSocket,bitLoss);
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JT
 */
 class NetworkThreadObj implements Runnable{
    private Thread t;
    private static int bitLoss; // ~5% of packets will be dropped.
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];
    public  int receiveSocket;
    public  int sendSocket;
    public String name;
   
        
    public NetworkThreadObj (String name, int receivePort, int sendPort, int packetLoss) {
        this.name = name;
        this.receiveSocket = receivePort;
        this.sendSocket = sendPort;
        this.bitLoss = packetLoss;
        
        receiveData = new byte[1024];
        sendData = new byte[1024];
        
        System.out.println("Thread "+name+" has been created. Receving from port " + 
                receivePort + " and sending from port " + this.sendSocket);
    }
    
   
    public void run() {
        try {
            //Port coming from HostA (Client)
            System.out.println("About to create a receive socket for "+this.name+" on "+this.sendSocket);
            DatagramSocket networkReceiveSocket = new DatagramSocket(receiveSocket);
            //Port coming from HostB (Server)
            DatagramSocket networkSendSocket = new DatagramSocket();
            //networkReceiveSocket = new DatagramSocket(7007);
            
            
            
            while (true) {
                //Read the packet and print out information regarding the received packet.
                DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
                String received = new String(receivePacket.getData());
                networkReceiveSocket.receive(receivePacket);
                receiveData = receivePacket.getData();

                ByteArrayInputStream in = new ByteArrayInputStream(receiveData);
                ObjectInputStream is = new ObjectInputStream(in);

                System.out.println("Packet received from " +receivePacket.getSocketAddress());
                Packet packet = (Packet) is.readObject();
                packet.toString();
                System.out.println(packet.toString());

                //Forward the packet
                //SERIALIZE packet back down into byte stream
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(outputStream);
                os.writeObject(packet);
                sendData = outputStream.toByteArray();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("localhost") , sendSocket);
                networkSendSocket.send(sendPacket);
                System.out.println("PACKET SENT to "+networkSendSocket.getInetAddress());
                
                //            networkSendSocket.receive(ReceivePacket);
                //            String sentence = new String(ReceivePacket.getData());
                //            System.out.println("RECEIVED: " + sentence);
                //            IPAddress = ReceivePacket.getAddress();
                //            sendData = sentence.getBytes();
                //            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7008);

//                if(!drop()){
//                    networkReceiveSocket.send(sendPacket);
//                }   
            }
        } catch (SocketException ex) {
            Logger.getLogger(NetworkThreadObj.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetworkThreadObj.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NetworkThreadObj.class.getName()).log(Level.SEVERE, null, ex);
        }
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


    




