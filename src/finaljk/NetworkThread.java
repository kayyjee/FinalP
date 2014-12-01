/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finaljk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JT
 */
public class NetworkThread implements Runnable{
    private Thread t;
    private static int bitLoss; // ~5% of packets will be dropped.
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];
    public static int receiveSocket;
    public static int sendSocket;
    public String name;
    DatagramSocket networkReceiveSocket;
    DatagramSocket networkSendSocket;
        
    public NetworkThread (String name, int receivePort, int sendPort, int packetLoss) {
        this.name = name;
        this.receiveSocket = receivePort;
        this.sendSocket = sendPort;
        this.bitLoss = packetLoss;
        
        receiveData = new byte[1024];
        sendData = new byte[1024];
        
        System.out.println("Thread "+name+" has been created. Receving from port " + 
                receivePort + " and sending from port " + sendPort);
    }
    
    @Override
    public void run() {
        try {
            //Port coming from HostA (Client)
            networkReceiveSocket = new DatagramSocket(receiveSocket);
            //Port coming from HostB (Server)
            networkSendSocket = new DatagramSocket();
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
            Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
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
