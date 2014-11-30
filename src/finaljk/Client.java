package finaljk;

import java.io.*;
import java.net.*;

class Client {
    
    private static DatagramPacket sendPacket;
    private static DatagramSocket clientSocket;
    private static InetAddress ClientIPAddress;
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];
//int numofpacketsto sned. (window size)
    public static void main(String args[]) throws Exception {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        clientSocket = new DatagramSocket(7005);
        ClientIPAddress = InetAddress.getByName("localhost");
        
        
        System.out.println(ClientIPAddress);
        
        
        String sentence = inFromUser.readLine();
        String [] Packets =  CreatePackets(sentence);
        int i;
        for (i=0; i<10; i++){
            sendData = Packets[i].getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, ClientIPAddress, 7006);
            Send(sendPacket);
            
           }
        
        
        
        
        
        
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("FROM SERVER:" + modifiedSentence);
        clientSocket.close();
    }
    
    public static String[] CreatePackets(String message) {
        String [] PacketArray = new String [10];
        int i;
        
        for (i=0; i<10; i++){
            PacketArray[i] = message + String.valueOf(i);
            
        }
        return PacketArray;
        
    }
    
    public static void Send(DatagramPacket Packets) throws IOException {
        
      
            
            clientSocket.send(sendPacket);
            //send(Packets);
        }
    
}
