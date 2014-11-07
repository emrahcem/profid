package example.popularItems;
/**
 * 
 * @author Emrah Cem
 * 
 * Represents the Churn event
 *
 */
public class Churn {
	/**
	 * Node id having a churn event (meaning that the peer will either join or leave the network)
	 */
	private int nodeID;
	
	/**
	 * Type of the churn : join or leave
	 */
	private boolean isJoin;
	
	/**
	 * Time of the churn event
	 */
	private long time;
	
	/**
	 * Constructor
	 * @param join is the type of churn "join"
	 * @param t time of the churn event
	 */
	public Churn(boolean join, long t){
		isJoin = join;
		time= t;
	}
	
	//------------------------------------------------------------------
	//SETTERS and GETTERS
	//------------------------------------------------------------------
	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isJoin() {
		return isJoin;
	}
	
	public void setJoin(boolean isJoin) {
		this.isJoin = isJoin;
	}
	
	
}
