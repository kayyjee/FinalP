/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finaljk;

import java.io.Serializable;
/**
 *
 * @author JT
 */

public class Packet implements Serializable {
    //1=Data
    //2=ACK
    //3=EOT
    //4=EOTACK
    private int packetType;	
    private int seqNum;
    private int windowSize;
    private int ackNum;
    
    /**
     * This method is the object builder for the packet object.
     * @param type
     * @param seq
     * @param size
     * @param ack
     */
    public Packet (int type, int seq, int size, int ack){
        this.packetType = type;
        this.seqNum = seq;
        this.windowSize = size;
        this.ackNum = ack;   
    }   
    /**
     * This method is used to get the current Packet object's packet type
     * 1=Data
     * 2=ACK
     * 3=EOT
     * 4=EOTACK
     */
    public int getPacketType() {
        return packetType;	
    }   
    /**
     * This method is used to set the current Packet object's packet type.
     * @param type
     * 1=Data
     * 2=ACK
     * 3=EOT
     * 4=EOTACK 
     */
    public void setPacketType(int type) {
         this.packetType = type;
    }
    /**
     * This method is used to get the current Packet object's sequence number
     */
    public int getSeqNum() {
        return seqNum;	
    }
    /**
     * This method is used to set the current Packet Object's sequence number
     * @param seq
     */ 
    public void setSeqNum(int seq) {
         this.seqNum = seq;
    }
    /**
     * This method is used to get the current Packet object's window size.
     */
    public int getWindowSize() {
        return windowSize;	
    }
    /**
     * This method is used to set the current Packet Object's window size
     * @param size
     */  
    public void setWindowSize(int size) {
         this.windowSize = size;
    }
    /**
     * This method is used to get the current Packet object's acknowledge number
     */
    public int getAckNum() {
        return ackNum;	
    }
    /**
     * This method is used to set the current Packet Object's acknowledge number
     * @param ack
     */       
     public void setAckNum(int ack) {
         this.ackNum = ack;
     }  
    /**
     * This method is used to print out to see an overview of the Packet Object,
     * printing out each attribute of the Packet.
     */
    @Override
    public String toString() {
        return "Packet Type=" + getPacketType() + 
                " Seq Num=" + getSeqNum() +
                " Ack Num=" + getAckNum() + 
                " Window Size=" + getWindowSize();
     }
}
