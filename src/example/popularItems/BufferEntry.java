package example.popularItems;

import java.util.HashMap;

import peersim.core.Node;

/**Class representing the entry of the buffer
 * If a peer is busy with replying the push message of another peer 
 * and gets another push or wants to send a push message, then they are put in the buffer    
 */
public class BufferEntry{

	private final Node sender; 
	private final Node receiver;
	private final HashMap<Integer, Double>  senderContent;

	public BufferEntry(Node sender, Node receiver, HashMap<Integer, Double>  senderContent){
		this.sender=sender;
		this.receiver=receiver;
		this.senderContent=senderContent;
	}
	
	public Node getSender() {
		return sender;
	}

	public Node getReceiver() {
		return receiver;
	}

	public HashMap<Integer, Double> getSenderContent() {
		return senderContent;
	}
}