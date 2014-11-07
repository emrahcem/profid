package example.popularItems.Kempe;

import java.util.HashMap;

import peersim.core.Node;

public class GossipMessageKempe{

	private final HashMap<Integer, ItemState>  content;
	private Node sender;


	public GossipMessageKempe(HashMap<Integer, ItemState>  content,Node sender){
		this.content=content;
		this.sender=sender;
	}
	
	public HashMap<Integer, ItemState> getContent() {
		return content;
	}

	public Node getSender() {
		return sender;
	}
}