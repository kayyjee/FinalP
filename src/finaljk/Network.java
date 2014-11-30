package finaljk;

import java.io.*;
import java.net.*;

class Network {
    
    private static DatagramPacket ReturnPacket;
    private static DatagramPacket ReceivePacket;
    private static DatagramSocket networkReceiveSocket;
    private static DatagramSocket networkSendSocket;
    private static InetAddress NetworkIPAddress;
    private static InetAddress IPAddress;
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];
        
    
    
    public static void main(String args[]) throws Exception {
        networkSendSocket = new DatagramSocket(7006);
        networkReceiveSocket = new DatagramSocket(7007);
        NetworkIPAddress = InetAddress.getByName("localhost");
        
        
        
            while (true) {
            ReceivePacket = new DatagramPacket(receiveData, receiveData.length);
            forward(ReceivePacket);
            
            
            
            
            
            
            ReturnPacket = new DatagramPacket(receiveData, receiveData.length);
            acknowledge(ReturnPacket);
            
            
            
            
        }
    }
    
    
    
    
    
        public static void forward (DatagramPacket sendPacket) throws IOException{
            networkSendSocket.receive(ReceivePacket);
            String sentence = new String(ReceivePacket.getData());
            System.out.println("RECEIVED: " + sentence);
            IPAddress = ReceivePacket.getAddress();
            sendData = sentence.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7008);
            networkSendSocket.send(sendPacket);
            
        }
        
        
        
        
        public static void acknowledge (DatagramPacket ReturnPacket) throws IOException{
            networkReceiveSocket.receive(ReturnPacket);
            String sentence2 = new String(ReceivePacket.getData());
            System.out.println("RETURNED: " + sentence2 );
            DatagramPacket FinalPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7005);
            networkReceiveSocket.send(FinalPacket);
            
        }
    }



