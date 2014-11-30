package finaljk;

import java.io.*;
import java.net.*;

class Client {

    private static DatagramPacket sendPacket;
    private static DatagramSocket clientSocket;
    private static InetAddress ClientIPAddress;
    private static int WindowSize = 5;
    private static int totalPackets = 100;
    private static int packetNumber = 0;
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];
    private static int bitLoss = 5; // ~5% of packets will be dropped. 
//int numofpacketsto sned. (window size)

    public static void main(String args[]) throws Exception {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        clientSocket = new DatagramSocket(7005);
        ClientIPAddress = InetAddress.getByName("localhost");

        System.out.println(ClientIPAddress);

        String sentence = inFromUser.readLine();
        String[] Packets = CreatePackets(sentence);
        int i = 0;
        

        while (packetNumber < totalPackets - 1) {

            for (i = 0; i < WindowSize; i++) {
                sendData = Packets[packetNumber].getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, ClientIPAddress, 7006);
                Send(sendPacket);
                System.out.println("SENT: Packet " + packetNumber);
                packetNumber++;
            }
            for (i = 0; i < WindowSize; i++){
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String modifiedSentence = new String(receivePacket.getData());
                System.out.println("FROM SERVER:" + modifiedSentence);
                
            
            }
            //clientSocket.close();
        }
    }

    public static String[] CreatePackets(String message) {
        String[] PacketArray = new String[totalPackets];
        int i;

        for (i = 0; i < totalPackets; i++) {
            PacketArray[i] = message + String.valueOf(i);

        }
        return PacketArray;

    }

    public static void Send(DatagramPacket Packets) throws IOException {

        clientSocket.send(sendPacket);
        //send(Packets);
    }

}
