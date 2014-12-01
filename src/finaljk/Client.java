package finaljk;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class Client {

    private static DatagramPacket sendPacket;
    private static DatagramSocket clientSocket;
    private static InetAddress ClientIPAddress;
    private static int WindowSize = 5;
    private static int totalPackets = 10;
    private static int packetNumber = 0;
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];
    static int hostASendSocket = 7006;
    static int hostAReceiveSocket = 7009;
    public static ArrayList<Packet> packetsContainer;
    public static String hostASendIP = "localhost";

    public static void main(String args[]) throws Exception {
//        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
//        clientSocket = new DatagramSocket(7005);
        ClientIPAddress = InetAddress.getByName("localhost");
        
        packetsContainer = new ArrayList<>();

        System.out.println(ClientIPAddress);
        System.out.println("What would you like to do? Send or Receive?");
        Scanner input = new Scanner(System.in);
        String choice = input.nextLine();

        
        while(true) {
        if (choice.equalsIgnoreCase("send")){
            send();
        }
        else{
            receive();
        }   
            //clientSocket.close();
        }
    }

    private static void send() throws Exception {     
         
        //Fill the window with packets.
        for (int i = 0; i < packetsContainer.size(); i++) {
                //Packet type, seq num, window size, ack num
                Packet packet = new Packet(1, packetNumber, 5, packetNumber);
                //Add this pcaket into the container
                packetsContainer.add(packet);
                System.out.println("Packet " + packetsContainer.get(i).getSeqNum() + "added to window.");
                packetNumber++;
        }
         

        for(int i = 0; i < packetsContainer.size(); i++) {
           InetAddress IPAddress = InetAddress.getByName(hostASendIP);
           sendData = serializePacket(packetsContainer.get(i));
           DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, hostASendSocket);
           clientSocket.send(sendPacket);
           System.out.println("Packet " + packetsContainer.get(i).getSeqNum() + " has been sent");

           //Create the timer for last packet
           // If its the last packet, create a timer
//           if (i == packetsContainer.size()-1) {
//               System.out.println("LAST PACKET SENT!!");
//               HostA.maxPacketSent = seqNum;
//               System.out.println("Max Packet is now " + seqNum);
//               timer = new Timer(String.valueOf(seqNum));
//               timer.schedule(new timeOut() {
//               }, 500);
//               System.out.println("TIMER CREATED for last packet, seq number " + seqNum);
//               //Create a threadID object that will be held in a container for easy access later
//               threadID elton = new threadID(seqNum, timer);
//               threadList.add(elton);
//               clientSocket.close();
//           }
        
        }
        System.out.println("Going into receive mode");
        receive();
        
//        int i = 0;
//        while (packetNumber < totalPackets - 1) {
//            
//            for (i = 0; i < WindowSize; i++) {
//                try {
//                    DatagramSocket Socket = new DatagramSocket();
//                    InetAddress IPAddress = InetAddress.getByName("localhost");
//                    byte[] incomingData = new byte[1024];
//                    Packet packet = new Packet(2,packetNumber,5,1);
//                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                    ObjectOutputStream os = new ObjectOutputStream(outputStream);
//                    os.writeObject(packet);
//                    byte[] data = outputStream.toByteArray();
//                    DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, hostASendSocket);
//                    Socket.send(sendPacket);
//                    System.out.println("Packet: " + packetNumber + " sent");
////                        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
////                        Socket.receive(incomingPacket);
////                        String response = new String(incomingPacket.getData());
////                        System.out.println("Response from server:" + response);
////                        Thread.sleep(2000);
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                packetNumber++;
//            }
//        }
//                for (i = 0; i < WindowSize; i++){
//                    DatagramSocket Socket = new DatagramSocket();
//                    InetAddress IPAddress = InetAddress.getByName("localhost");
//                    byte[] incomingData = new byte[1024];
//                    DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
//                    Socket.receive(incomingPacket);
//                    String response = new String(incomingPacket.getData());
//                    System.out.println("Response from server:" + response);
//                }
        receive();
    }
    
    private static void receive() {
        try {        
            //Create a socket for the clients to connec to you on.
            DatagramSocket listenSocket = new DatagramSocket(hostAReceiveSocket);
            
            //Define the size of byte arrays that will hold data
            byte[] receiveData =  new byte[1024];
            
            System.out.println("Receiver socket created - waiting to receive");
            
            // While this loop is true, keep listening
            while (true){
                    DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
                    listenSocket.receive(receivePacket);
                    
                    receiveData = receivePacket.getData();
                    
                    ByteArrayInputStream in = new ByteArrayInputStream(receiveData);
                    ObjectInputStream is = new ObjectInputStream(in);
                    
                    Packet packet = (Packet) is.readObject();
                    System.out.println("Packet : "+packet.getSeqNum()+" received");
                    
                    if(packet.getPacketType()==3){
                        System.out.println("Last packet has been received.");
                    }
            }   
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Serializes the packets in order for them to be put in the array and sent to another host.
    public static byte[] serializePacket(Packet packet) {
        //Serialize packet object into a bytearray
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(outputStream);
            os.writeObject(packet);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outputStream.toByteArray();

    }
    
}