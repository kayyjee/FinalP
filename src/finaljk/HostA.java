package finaljk;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acts as a host where the user determines whether to Send Data Packets (Transmit) 
 * or Receive Data Packets from another Host. The Host sends and receives it's packets to and from
 * the Network Emulator.
 * 
 * 
 * 
 * @author Kyle
 */
class HostA {

    private static ArrayList<Packet> Window;//ArrayList that holds Packets for each Send of a WindowSize
    private static DatagramPacket sendPacket;//Data Packet sent to Network Emulator
    private static DatagramSocket clientSocket;//Socket to send to Network Emulator
    private static InetAddress NetEmuIPAddress;//IP Of Network Emulator
    public static ArrayList<Packet> PacketArray;//ArrayList that holds all Data Packets to be Sent.
    private static int[] checkedPackets;//Array to hold Packets Received
    private static final int WindowSize = 10;//Size of Window 
    private static final int totalPackets = 50;//Amount of Packets to send
    private static final byte[] receiveData = new byte[1024];//Byte Array of Packets Received
    private static byte[] sendData = new byte[1024];//Byte Array of Packets Sent
    static PrintWriter writer;//Write to log Files
    private static DatagramPacket ReceivePacket;//Data Packet received from Emulator
    private static DatagramSocket serverSocket;//Socket to receive from Network Emulator
    private static InetAddress IPAddress;//IP Address of Client Machine
    private static long delay = 666;//Delay before sending each Packet
    private static boolean EOTSent = false;//Boolean turns true only when EOT sent
    private static int timeOut;//Amount of time before timeout throws exception

    public static void main(String args[]) throws Exception {
        //Obtaining and formatting the date and time for the log file name.
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy-HH.mm.ss");
        Date date = new Date();
        String dateLog = dateFormat.format(date);
        //Create log writer.
        writer = new PrintWriter(dateLog+"_HostA_Log.txt", "UTF-8");
        //initialize arrays.
        PacketArray = new ArrayList<Packet>();
        checkedPackets = new int[totalPackets - 1];
        int b;
        for (b = 0; b < totalPackets - 1; b++) {
            checkedPackets[b] = 0;
        }

        //Save IP of Network Emulator
        System.out.println("What is the IP address of the network emulator?");
        Scanner input2 = new Scanner(System.in);
        String netIP = input2.nextLine();
        NetEmuIPAddress = InetAddress.getByName(netIP);
        
        //Prompt User for which mode to initialize
        System.out.println("What would you like to do? Send or Receive?");
        Scanner input3 = new Scanner(System.in);
        String choice = input3.nextLine();
        if (choice.equalsIgnoreCase("send")) {
            System.out.println("How much simulated network delay would you like (milliseconds)?");
            Scanner input = new Scanner(System.in);
            delay = input.nextLong();//take user input for delay
            
            timeOut = (int) delay * 6;//timeout is always 6 times larger than delay
            if (timeOut == 0){
                timeOut = 1000;//if user enters 0 delay, ensure we can still timeout
            }
            clientSocket = new DatagramSocket(7005);//Enter Send mode
            System.out.println("Preparing to send");
            CreatePackets();//prepare packetArray to hold all Packets
            sendStart();
        } else {
            serverSocket = new DatagramSocket(7008);//Enter Receive mode
            System.out.println("Waiting to receive...");
            receive();
        }

    }
    /**
     * This method is initialized if user selects to Transmit. Prepares window sized arrayList each time 
     * we send. Emptys contents of the arrayList by sending to the Network Emulator. 
     * Re-fills with packets each time it goes to send, as long as DataPackets exist.
     * @throws Exception 
     */
    public static void sendStart() throws Exception {
        int i = 0;
        int l = 0;
        //Stop when total Packets are empty
        while (!PacketArray.isEmpty()) {

            PrepareWindow();//Fills up Window<> with window amount of packets

            for (int c = 0; c < Window.size(); c++) {//For each packet in Window Array, send to Network Emulator
                
                //Check if next packet to send is an EOT
                int m = PacketArray.get(c).getPacketType();
                if (m == 3) {
                    if (PacketArray.size() == 1) {//Check if EOT is last Packet left in Array
                        SendEOT(PacketArray.get(c));
                    }
                } 
                //if not, send as normal Packet
                else {
                    
                    //Write Packet Object to Byte array
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutputStream os = new ObjectOutputStream(outputStream);
                    os.writeObject(PacketArray.get(l));//Get next Packet 
                    byte[] sendData = outputStream.toByteArray();
                    sendPacket = new DatagramPacket(sendData, sendData.length, NetEmuIPAddress, 7006);
                    Send(sendPacket);//Send to Network Emulator
                    Packet packet2 = (Packet) Window.get(l);
                    //Output Packet sent
                    System.out.println("SENT Data Packet - " + packet2.getSeqNum());
                    writer.println("SENT Data Packet - " + packet2);

                    l++;

                }
            }

            
            l = 0;
            
            clientSocket.setSoTimeout(timeOut);//initialize timeout for last Packet
            
            //Listen for a window size amount of Packet ACKs
            for (i = 0; i < Window.size(); i++) {
                
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                try {
                    clientSocket.receive(receivePacket);
                    byte[] receivedata = receivePacket.getData();//write incomming packet object to byte array

                    ByteArrayInputStream in = new ByteArrayInputStream(receivedata);
                    ObjectInputStream is = new ObjectInputStream(in);

                    try {
                        
                        Packet packet2 = (Packet) is.readObject();//if we receive an ACK, output Packet info
                        System.out.println("Packet ACK received - " + packet2);
                        writer.println("Packet ACK received - " + packet2);

                        if (packet2.getPacketType() == 4) {//if we receive an EOT ACK, End Program
                            
                                System.out.println("End of Transmission Received, Goodbye!");
                                writer.println("End of Transmission Received, Goodbye");
                                writer.close();
                                System.exit(0);
                            }
                        
                        CheckOffReceivedPackets(packet2);//Remove Packet from our Total Packet ArrayList
                        

                    } catch (ClassNotFoundException e) {

                        e.printStackTrace();

                    }

                } catch (Exception e) {//Throwed when Timeout
                    //Check if timeout occured on EOT ACK
                    if ((EOTSent == false)&&(PacketArray.get(0).getPacketType()==3)) break;
                    
                    //If not output which packet caused timeout
                    System.out.println("Timeout on packet - " + PacketArray.get(0).getSeqNum());
                    writer.println("Timeout on packet - " + PacketArray.get(0));

                    break;
                }

            }
            
        }
    }
    /**
     * This Method creates an array of Packet objects the size of the Total amount of Packets
     * @return
     * @throws IOException 
     */
    public static ArrayList CreatePackets() throws IOException {
        int i;

        for (i = 0; i < totalPackets; i++) {
            Packet packet = new Packet(1, i, WindowSize, i);//Create Packet object
            PacketArray.add(packet);//Add to PacketArray
        }
        Packet finalPacket = new Packet(3, totalPackets, WindowSize, totalPackets);//Last Packet is an EOT
        PacketArray.add(finalPacket);//Add to PacketArray
        return PacketArray;

    }
    /**
     * This method delays before sending the Packet to the networkEmulator
     * @param Packets
     * @throws IOException 
     */
    public static void Send(DatagramPacket Packets) throws IOException {
        //Simulated network delay.
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        clientSocket.send(sendPacket);//send to NetworkEmulator
        
    }
    /**
     * This method removes Data Packets from the array after we received it's ACK.
     * @param packet 
     */
    public static void CheckOffReceivedPackets(Packet packet) {
        
        for (Packet PacketArray1 : PacketArray) {

        }
        for (int i = 0; i < PacketArray.size(); i++) {
            if (PacketArray.get(i).getSeqNum() == packet.getSeqNum()) {
                PacketArray.remove(i);//Removes the corresponding Data Packet for the ACK
            }
        }
        PacketArray.remove(packet);
    }
    
    /**
     * This method Prepares a WindowSized (or less) amount of Packets in an ArrayList to send
     */
    public static void PrepareWindow() {
        int i;
        Window = new ArrayList(WindowSize);
        if (PacketArray.size() < WindowSize) {//If we have less Packets to send than Window Size, fill up with remaining Packets
            for (i = 0; i < PacketArray.size(); i++) {
                Window.add(PacketArray.get(i));

            }

        } else {
            for (i = 0; i < WindowSize; i++) {//Fill up normally per Window Size
                Window.add(PacketArray.get(i));
            }
        }

    }
    /**
     * This method is initialized when User determines to receive Data Packets.
     * We sit in receive mode and wait for packets to be sent from Network Emulator
     * @throws Exception 
     */
    public static void receive() throws Exception {
        while (true) {
            //Initialize Network Emulator config information
            ReceivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(ReceivePacket);
            IPAddress = ReceivePacket.getAddress();
            
            //Write Packet Object to Byte Array
            byte[] sendData = ReceivePacket.getData();

            ByteArrayInputStream in = new ByteArrayInputStream(sendData);

            ObjectInputStream is = new ObjectInputStream(in);
            
            //Read incoming Packet 
            Packet packet = (Packet) is.readObject();
            
            //output packet information
            System.out.println("Data Packet received = " + packet);
            writer.println("Data Packet received = " + packet);
            
            //If we Receive EOT, Send EOT ACK
            if (packet.getPacketType() == 3) {
                SendEnd(packet);
                writer.close();
            } else {
                //If not, treat as normal Data Packet, send ACK
                int seqNum = packet.getSeqNum();
                int windowSize = packet.getWindowSize();
                
                //write outgoing packet object to byte array
                packet = new Packet(2, seqNum, windowSize, seqNum);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(outputStream);
                os.writeObject(packet);
                sendData = outputStream.toByteArray();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7007);

                //send ACK to Network Emulator
                serverSocket.send(sendPacket);
                //output packet information
                System.out.println("ACK Packet Sent = " + packet);
                writer.println("ACK Packet Sent = " + packet);
            }
        }
    }

    /**
     * This method sends out an EOT ACK if we receive an EOT
     * @param packet
     * @throws IOException 
     */
    public static void SendEnd(Packet packet) throws IOException {
        int seqNum = packet.getSeqNum();
        int windowSize = packet.getWindowSize();
        
        //Create EOT ACK packet
        packet = new Packet(4, seqNum, windowSize, seqNum);
        
        //Write packet object to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(packet);
        sendData = outputStream.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7007);
        
        //Send Packet to network emulator
        serverSocket.send(sendPacket);
        //output packet information
        System.out.println("EoT Packet Sent = " + packet);
        writer.println("EoT Packet Sent = " + packet);
        //Set EOT Sent to true 
        EOTSent = true;
    }

    /**
     * This method sends an EOT Packet if it all data ACKs have been received
     * @param packet
     * @throws IOException 
     */
    public static void SendEOT(Packet packet) throws IOException {
        int seqNum = packet.getSeqNum();
        int windowSize = packet.getWindowSize();

        //Write packet object to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(packet);
        byte[] sendData = outputStream.toByteArray();
        sendPacket = new DatagramPacket(sendData, sendData.length, NetEmuIPAddress, 7006);
        Send(sendPacket);//send packet to network emulator
        //output packet information
        System.out.println("EoT Packet Sent = " + packet);
        EOTSent = true;
        writer.println("EoT Packet Sent = " + packet);
    }

}
