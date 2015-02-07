Network Emulator

Objective

To design and implement a basic Send-And-Wait protocol simulator. The protocol will be half-duplex and 
use sliding windows to send multiple packets between to hosts on a LAN with an “unreliable network” between 
the two hosts.

Mission:
-	You may use any language of your choice to implement the three components shown in the diagram above.
-	You will be designing an application layer protocol in this case on top of either TCP or UDP. The protocol should be able to handle network errors such as packet loss and duplicate packets. You will implement timeouts and ACKs to handle retransmissions due to lost packets (ARQ).
-	The network emulator will act as an unreliable channel over with the packets will be sent. This means that transmitter will send the packets to the network emulator which in turn will forward them to the receiver. The receiver in turn will send ACKs back to the transmitter via the network emulator.
-	Your implementation of the network emulator will include a “noise” component which will randomly discard packets (and ACKs as well) to achieve a specified bit error rate. This can be specified as part of command line arguments.
-	Your overall application architecture will have a minimum of three source modules: transmitter, receiver, and network as well as any associated include files and libraries if necessary. For the purposes of simplicity it is recommended that the IP addresses and of the transmitter, sender and network be extracted from a common configuration file. The port numbers can also be part of the configuration file.
-	One side will be allowed to acquired the channel first and send all of its packets. An End of Transmission (EOT) will indicate that it has completed sending all of its packets, after which the other side can start sending packets.




Constraints:
-	The basic protocol is Send-and-Wait, however it is a modified version in that it will use a sliding window to send multiple frames rather than single frames. You will still have to implement a timer to wait for ACKs or to initiate a retransmission in the case of a no response for each frame in the window.
-	Your window will slide forward with each ACK received, until all of the frames in the current window have been ACK’d.
-	Both the transmitter and receiver will print out ongoing the session as simple text lines containing the type of packet sent, type of packet received, the status of the window, sequence numbers, etc. The format of this display will be left up to you.
-	Your application will maintain a log file at both the transmitter and the receiver. This can be used for both troubleshooting and for validating your protocol.
-	Your network module will take arguments such as the BER (Bit Error Rate), average delay per packet, and will also have a configuration file specifying IP addresses and port numbers for the transmitter and receiver.
-	You are required to submit an extensive test document complete with screen shots validating all of the protocol characteristics you have implemented. Examples are successful transactions, retransmissions, timeouts, etc. You may also want to include Wireshark captures for this.







Program Summary

This java program initiates a connection between Host A to the network emulator and then finally to Host B. 
After the proper connections has been made using UDP, the transmitter host is able to either SEND or RECEIVE.
The send command allows the particular host to send packets of data in a specified window size to the other host
through the network emulator on port 7005 – in which the network emulator will then receive it on port 7006. The
receive command will simply tell that client to wait for to receive packets from the other host on port 7008.
The network emulator in the middle of the two hosts will drop a specified percentage of packets that are being 
transferred between hosts as it goes through the network emulator. The network emulator receives the data packets 
on port 7006 and then forward those packets on port 7007 to the receiver. This simulated packet drop can happen
during the transit of the actual data packet transfer or during the acknowledgement packet transfer. If a packet
is dropped, the transmitter host will wait for a predetermined amount of time (specified delay times three (to ensure 
that it doesn’t time out too early before the packet is legitimately received)) before it times out and the transmitter
host will resend that packet. The size of the window doesn’t change (declared as size 5), but the packets inside that 
window that are being sent to the receiver differs depending on what packets the receiver has received and acknowledged
already and will “slide” to adjust the packet contents accordingly. The acknowledged packets will be removed from the queue,
while the packets which weren’t acknowledged (dropped/timed out) will be resent in that same window along with the new
packets that were next in line to be sent. During the whole process of sending, receiving and dropping packets, log files 
will be created at the transmitter, and receiver ends for later revision of the packet transfer events.




