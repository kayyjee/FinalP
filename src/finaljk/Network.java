package finaljk;

import java.lang.Math;
import java.io.*;
import java.net.*;
import java.util.*;

class Network {
    
    private static int bitLoss = 0; // ~5% of packets will be dropped.
    private static DatagramPacket ReturnPacket;
    private static DatagramPacket ReceivePacket;
    private static DatagramSocket networkReceiveSocket;
    private static DatagramSocket networkSendSocket;
    private static InetAddress HostAIPAddress;
    private static InetAddress HostBIPAddress;
    private static Packet packet;
    private static boolean sent = false;
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];
    static PrintWriter writer;
        
    
    
    public static void main(String args[]) throws Exception {
        System.out.println("What is the IP address of transmitting host?");
        Scanner input = new Scanner(System.in);
        String hostAIP = input.nextLine();
        HostAIPAddress = InetAddress.getByName(hostAIP);
        System.out.println("What is the IP address of receiving host?");
        Scanner input2 = new Scanner(System.in);
        String hostBIP = input2.nextLine();
        HostBIPAddress = InetAddress.getByName(hostBIP);
        System.out.println("What percentage of packets would you like to be dropped?");
        Scanner input3 = new Scanner(System.in);
        bitLoss = input3.nextInt();
        
        //Create log writer.
        writer = new PrintWriter("Network_Log.txt", "UTF-8");
        
        networkSendSocket = new DatagramSocket(7006);
        networkReceiveSocket = new DatagramSocket(7007);
        
        
        while (true) {
            sent = false;
            ReceivePacket = new DatagramPacket(receiveData, receiveData.length);
            forward(ReceivePacket);
            
            ReturnPacket = new DatagramPacket(receiveData, receiveData.length);
            acknowledge(ReturnPacket);
        }
    }
    
    
    
    
    
        public static void forward (DatagramPacket sendPacket) throws IOException{
            do {
            networkSendSocket.receive(ReceivePacket);
             byte[] sendData = ReceivePacket.getData();
	
                ByteArrayInputStream in = new ByteArrayInputStream(sendData);
	
                ObjectInputStream is = new ObjectInputStream(in);
	
                try {
	
                    packet = (Packet) is.readObject();
	
                    System.out.println("Data Packet received - "+packet);
                    writer.println("Data Packet received - "+packet);
                } catch (ClassNotFoundException e) {
	
                    e.printStackTrace();
	
                }
          
            //IPAddress = ReceivePacket.getAddress();
            sendPacket = new DatagramPacket(sendData, sendData.length, HostBIPAddress, 7008);
            
            if(!drop()){
            networkSendSocket.send(sendPacket);
            System.out.println("Data Packet Sent - " + packet );
            writer.println("Data Packet Sent - "+packet);
            sent=true;
            }
            else{
                System.out.println("Dropped Data Packet - " + packet);
                writer.println("Dropped Data Packet - "+packet);
                
            }
            
            }
            while (!sent);
           
        }
            
        
        
        
        
        public static void acknowledge (DatagramPacket ReturnPacket) throws IOException{
            networkReceiveSocket.receive(ReturnPacket);
             byte[] sendData = ReturnPacket.getData();
	
                ByteArrayInputStream in = new ByteArrayInputStream(sendData);
	
                ObjectInputStream is = new ObjectInputStream(in);
	
                try {
	
                    packet = (Packet) is.readObject();
	
                    System.out.println("ACK Packet received - "+packet);
                    writer.println("ACK Packet received - "+packet);
	
                } catch (ClassNotFoundException e) {
	
                    e.printStackTrace();
	
                }
            
            DatagramPacket FinalPacket = new DatagramPacket(sendData, sendData.length, HostAIPAddress, 7005);
            
            if (!drop()){
            networkReceiveSocket.send(FinalPacket);
            System.out.println("ACK Pack Sent - " + packet );
            writer.println("ACK Pack Sent - "+packet);
            }
            else{
                System.out.println("Dropped ACK Packet - " + packet);
                writer.println("Dropped ACK Packet - "+packet);
            }
            
        }
        
        public static boolean drop () throws IOException{
            Random rand = new Random();
            if (rand.nextInt(100) < bitLoss){
            return true;
        }
          
            else return false;
            
        }
    }



