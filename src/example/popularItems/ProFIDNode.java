package example.popularItems;

import java.util.ArrayList;

import peersim.core.GeneralNode;

public class ProFIDNode extends GeneralNode{

	public enum NodeType{DSNODE, NONDSNODE};
	protected NodeType type=NodeType.NONDSNODE;
	protected boolean alreadySharedLSI=false;
	protected ArrayList<Integer> domNeighs; 
	private static long counterID = 0;
	private long ID;
	public ProFIDNode(String prefix) {
		super(prefix);
		domNeighs= new ArrayList<Integer>();
	}

	public boolean isAlreadySharedLSI() {
		return alreadySharedLSI;
	}


	public void setAlreadySharedLSI(boolean alreadySharedLSI) {
		this.alreadySharedLSI = alreadySharedLSI;
	}

	public ArrayList<Integer> getDomNeighs() {
		return domNeighs;
	}


	public void setDomNeighs(ArrayList<Integer> domNeighs) {
		this.domNeighs = domNeighs;
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}
	
	public long getID() { return ID; }

	/** returns the next unique ID */
	private long nextID() {

		return counterID++;
	}
	
	public Object clone() {
		
		ProFIDNode result = null;
		result=(ProFIDNode)super.clone();
		result.ID=nextID();
		
		return result;
	}
}

