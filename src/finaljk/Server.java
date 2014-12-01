package finaljk;

import java.io.*;
import java.net.*;

class Server {

    private static DatagramPacket ReturnPacket;
    private static DatagramPacket ReceivePacket;
    private static DatagramSocket receiveSocket;
    private static DatagramSocket sendSocket;
    private static InetAddress ServerIPAddress;
    private static InetAddress IPAddress;
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];
    static int hostBReceiveSocket = 7007;
    static int hostBSendSocket = 7008;

    public static void main(String args[]) throws Exception {

        receiveSocket = new DatagramSocket(hostBReceiveSocket);
        sendSocket = new DatagramSocket();
        ServerIPAddress = InetAddress.getByName("localhost");

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
            receiveSocket.receive(receivePacket);

            receiveData = receivePacket.getData();

            ByteArrayInputStream in = new ByteArrayInputStream(receiveData);
            ObjectInputStream is = new ObjectInputStream(in);

            Packet packet = (Packet) is.readObject();
            System.out.println("Received Packet From: " + receivePacket.getSocketAddress());
            System.out.println(packet.toString());
            
            //Change the packet type to an ACK (type 2)
            packet.setPacketType(2);
            
            //Serializes the packet back in order to send it back to Host A 
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(packet);
            sendData = outputStream.toByteArray();   
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("localhost") , hostBSendSocket);
            sendSocket.send(sendPacket);
            System.out.println("PACKET SENT to "+sendSocket.getInetAddress());
            
            
//            ReceivePacket = new DatagramPacket(receiveData, receiveData.length);
//            serverSocket.receive(ReceivePacket);
//            String sentence = new String(ReceivePacket.getData());
//            System.out.println("Server Has Received: " + sentence);
//            IPAddress = ReceivePacket.getAddress();
//
//            String capitalizedSentence = sentence.toUpperCase();
//            sendData = capitalizedSentence.getBytes();
//            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7007);
//            serverSocket.send(sendPacket);
        }
    }
}
