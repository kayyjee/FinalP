package finaljk;

import java.io.*;
import java.net.*;

class Network {

    
    public static void main(String args[]) throws Exception {
        DatagramSocket networkSendSocket = new DatagramSocket(7006);
        DatagramSocket networkReceiveSocket = new DatagramSocket(7007);
        
        InetAddress NetworkIPAddress = InetAddress.getByName("localhost");
        
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            networkSendSocket.receive(receivePacket);
            
            
            String sentence = new String(receivePacket.getData());
            System.out.println("RECEIVED: " + sentence);
            
            InetAddress IPAddress = receivePacket.getAddress();
            
            sendData = sentence.getBytes();
            
            
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7008);
            networkSendSocket.send(sendPacket);
            
            
            
            
            
            DatagramPacket ReturnPacket = new DatagramPacket(receiveData, receiveData.length);
            networkReceiveSocket.receive(ReturnPacket);
            String sentence2 = new String(receivePacket.getData());
            System.out.println("RETURNED: " + sentence2 );
            
            DatagramPacket FinalPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7005);
            networkReceiveSocket.send(FinalPacket);
            
        }
    }
}


