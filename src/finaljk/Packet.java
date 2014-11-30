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
    //1=ACK
    //2=Data
    //3=EOT
    private int packetType;	
    private int seqNum;
    private int windowSize;
    private int ackNum;
    
    public Packet (int type, int seq, int size, int ack){
        this.packetType = type;
        this.seqNum = seq;
        this.windowSize = size;
        this.ackNum = ack;   
    }
            
    public int getPacketType() {
        return packetType;	
    }
     
    public void setPacketType(int type) {
         this.packetType = type;
    }
    public int getSeqNum() {
        return seqNum;	
    }
     
    public void setSeqNum(int seq) {
         this.seqNum = seq;
    }
    
    public int getWindowSize() {
        return windowSize;	
    }
     
    public void setWindowSize(int size) {
         this.windowSize = size;
    }
    public int getAckNum() {
        return ackNum;	
    }
     
     public void setAckNum(int ack) {
         this.ackNum = ack;
     }
     
    @Override
    public String toString() {
        return "Packet Type = " + getPacketType() + 
                " Sequence Number = " + getSeqNum() + 
                " Window Size = " + getWindowSize() + 
                " Acknowledge Number = " + getAckNum();
     }
}
