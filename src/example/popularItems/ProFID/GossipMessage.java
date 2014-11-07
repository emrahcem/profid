package example.popularItems.ProFID;

import java.util.HashMap;

import peersim.core.Node;
/**
 * 
 * @author Emrah Cem
 *
 * Represents the gossip message.
 */
public class GossipMessage{

	/**
	 * Content of the gossip message. Content is the set of <itemId,frequency> tuples.
	 */
	private final HashMap<Integer, Double>  content;
	/**
	 * Sende of the gossip message
	 */
	private final Node sender;
	/**
	 * Type of the gossip message: push or pull
	 */
	private final String type;
	
	/**
	 * Constructor
	 * @param type type of  the gossip message
	 * @param content content of the gossip message
	 * @param sender sender of the gossip message
	 */
	public GossipMessage(String type, HashMap<Integer, Double>  content, Node sender){
		this.sender=sender;
		this.type=type;
		this.content=content;
	}
	
	//--------------------------------------------------------------
	//GETTERS
	//--------------------------------------------------------------
	public HashMap<Integer, Double> getContent() {
		return content;
	}
	public Node getSender() {
		return sender;
	}
	public String getType() {
		return type;
	}

}