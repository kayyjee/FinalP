package finaljk;

import java.lang.Math;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

class Network {
    
    private static int bitLoss = 0; // ~5% of packets will be dropped.
    private static DatagramPacket ReturnPacket;// Packet that returns ACKs to Host
    private static DatagramPacket ReceivePacket;//Packet that forwards to to Host
    private static DatagramSocket networkReceiveSocket;//Socket to return ACK packets to Host
    private static DatagramSocket networkSendSocket;//Socket to forward Data packets to Host
    private static InetAddress HostAIPAddress;//Host IP Addresses
    private static InetAddress HostBIPAddress;
    private static Packet packet;//Packet acting as a dynamic temp Packet to output data
    private static boolean sent = false;//Switches on when packet has been sent to avoid blocking at receive
    private static byte[] receiveData = new byte[1024];//byte array send to return ACKs
    private static byte[] sendData = new byte[1024];//byte array to forward to Host
    static PrintWriter writer;//log files
        
    
    
    public static void main(String args[]) throws Exception {
        //Take in user input for the IP addresses and bit loss
        System.out.println("What is the IP address of transmitting host?");
        Scanner input = new Scanner(System.in);
        String hostAIP = input.nextLine();
        HostAIPAddress = InetAddress.getByName(hostAIP);//host IP
        System.out.println("What is the IP address of receiving host?");
        Scanner input2 = new Scanner(System.in);
        String hostBIP = input2.nextLine();
        HostBIPAddress = InetAddress.getByName(hostBIP);//host IP
        System.out.println("What percentage of packets would you like to be dropped?");
        Scanner input3 = new Scanner(System.in);
        bitLoss = input3.nextInt();//bit loss
        
 
        //Open socket to send to hosts
        networkSendSocket = new DatagramSocket(7006);
        networkReceiveSocket = new DatagramSocket(7007);
        
        //while a connection is still maintained between hosts and network emulator, continue to forward packets.
        while (true) {
            sent = false;
            
            //receive a data packet, send to forward method
            ReceivePacket = new DatagramPacket(receiveData, receiveData.length);
            forward(ReceivePacket);
            
            //receive an ACK packet, send to acknowldge method
            ReturnPacket = new DatagramPacket(receiveData, receiveData.length);
            acknowledge(ReturnPacket);
        }
    }
    
    
    
    
    /**
     * This method takes a received packet and forwards it to the other host
     * @param sendPacket
     * @throws IOException 
     */
        public static void forward (DatagramPacket sendPacket) throws IOException{
            do {
            networkSendSocket.receive(ReceivePacket);
             byte[] sendData = ReceivePacket.getData();//byte array to hold packet data
             
             
                //write Packet object to byte array
                ByteArrayInputStream in = new ByteArrayInputStream(sendData);
	
                ObjectInputStream is = new ObjectInputStream(in);
	
                try {
	
                    packet = (Packet) is.readObject();//Check if object read resembles Packet object
                    
                    //Output Packet data
                    System.out.println("Data Packet received - "+packet);

                } catch (ClassNotFoundException e) {
	
                    e.printStackTrace();
	
                }
          
            //Prepare to send packet
            sendPacket = new DatagramPacket(sendData, sendData.length, HostBIPAddress, 7008);
            
            if(!drop()){//Run drop method to see if Packet is dropped or not
            networkSendSocket.send(sendPacket);//If packetDrop was false, send packet
            System.out.println("Data Packet Sent - " + packet );//output packet information

            sent=true;//Set boolean sent to true. This resolves blocking receive statement if packet was dropped
            }
            else{
                System.out.println("Dropped Data Packet - " + packet);//If packetDrop was true, don't send packet, output packet information

                
            }
            
            }
            while (!sent);//When packet is sent, set boolean to true. Break out and acknowledge
           
        }
            
        
        
        
        /**
         * This method waits for the host we forwarded a packet to, to return an ACK packet
         * @param ReturnPacket
         * @throws IOException 
         */
        public static void acknowledge (DatagramPacket ReturnPacket) throws IOException{
            networkReceiveSocket.receive(ReturnPacket);//take in an ACK packet
             byte[] sendData = ReturnPacket.getData();//byte array will contain packet data
	
                ByteArrayInputStream in = new ByteArrayInputStream(sendData);
	
                ObjectInputStream is = new ObjectInputStream(in);
	
                try {
	
                    packet = (Packet) is.readObject();//check if read object is a Packet object
	
                    System.out.println("ACK Packet received - "+packet);//output packet information

	
                } catch (ClassNotFoundException e) {
	
                    e.printStackTrace();
	
                }
            
            DatagramPacket FinalPacket = new DatagramPacket(sendData, sendData.length, HostAIPAddress, 7005);
            
            if (!drop()){//Run drop method to determine if ACK packet is to be dropped
            networkReceiveSocket.send(FinalPacket);//if drop is false, send packet
            System.out.println("ACK Pack Sent - " + packet );//output packet information

            }
            else{
                System.out.println("Dropped ACK Packet - " + packet);//if drop is true, don't send. Output packet information

            }
            
        }
        /**
         * This method determines if packet is to be dropped by comparing a random integer to user 
         * determined bitLoss
         * @return
         * @throws IOException 
         */
        public static boolean drop () throws IOException{
            Random rand = new Random();//initiate random number
            if (rand.nextInt(100) < bitLoss){//compare random number between 1 and a 100 to bit loss. If less, packet gets dropped.
            return true;//
        }
          
            else return false;//if random number is greater than bitLoss, send.
            
        }
    }



