package example.popularItems.ProFID;

import java.util.*;

import example.popularItems.BufferEntry;
import example.popularItems.ProFIDState;

import peersim.config.Configuration;
import peersim.core.*;

public class MultipleValueHolderED implements MultipleValueED, Protocol{

	/**
	 * Data structure keeping the local item set of the peer   
	 */
	protected HashMap<Integer, Double>  content;
	
	/**
	 * Parameter used in convergence decision. If the change of the content is less than a predetermined @epsilon value
	 * for @convLimit consecutive cycles (rounds), then algorithm is said to be converged.  
	 */
	protected int convLimit;
	
	/**
	 * Keeps for how many consecutive rounds the change of the content is less than a predetermined @epsilon value
	 * If change is larger than @epsilon this variable is set to 0; otherwise, it is incremented by 1.
	 */
	protected int convCounter;
	
	/**
	 * Whenever a convergence check is performed, the peer also guesses the network size. This is the last 
	 * network size guess of the peer.
	 */
	protected double lastNetSizeGuess;
	
	/**
	 * A boolean variable keeping whether the node decided that algorithm has converged. Note that convergence
	 * decision of each node is independent. 
	 */
	protected boolean converged;
	
	/**
	 * Parameter used in convergence decision. If the change of the content is less than a predetermined @epsilon value
	 * for @convLimit consecutive cycles (rounds), then algorithm is said to be converged.  
	 */
	protected double epsilon;
	
	/**
	 * Number of nodes to which a peer will send gossip in a single round
	 */
	protected int fanout;
	
	/**
	 * Timeout duration for a gossip message. Whenever a peer sends a gossip message, it expects the reply within a given time, namely timeout, 
	 * otherwise timeout occurs
	 */
	protected double timeout;
	
	/**
	 * Used in convergence decision. Computed frequencies of the node is compared to the actual frequencies
	 * if the difference is less than errorRate then convergence is decided.
	 */
	protected double errorRate;
	
	/**
	 * Convergence time of the peer, time unit depends on the user configuration specified in configuration file 
	 */
	protected long convergenceTime;
	
	/**
	 * Keeps the maximum message size in terms of number of item types.
	 */
	protected int mms;
	
	/**
	 * Keeps the current round for the peer, since peers may not be synchronized, they may not be in the same round
	 * at a given time 
	 */
	protected long currentRound;
	
	/**
	 * Total number of timeouts triggered by the peer
	 */
	protected int numOfTimeouts;
	
	/**
	 * Number of times where the peer had no neighbor to gossip due to churn 
	 */
	protected int noNeighborCounter;
	
	/**
	 * Threshold used to determine frequent items
	 */
	public static int threshold;
	
	/**
	 * Decision margin around the threshold.
	 */
	public static double delta;
	
	/**
	 * Keeps the push messages that will be send 
	 */
	protected LinkedList<BufferEntry> myPushBuffer;
	
	/**
	 * Keeps the push messages received from other peers
	 */
	protected LinkedList<BufferEntry> pushReqBuffer;
	
	/**
	 * id of the last message that has been sent 
	 */
	protected static int messageId;
	
	/**
	 * id of the neighbor a pull message is being waited from
	 */
	protected long waitPullFrom;
	
	/**
	 * Structure keeping <item,frequency> tuples when peer decides that it converged
	 */
	protected HashMap<Integer,Double> FIS; 
	
	/**
	 * The list keeping the network size guesses occuring whenever a convergenceCheck is performed. 
	 */
	protected LinkedList<Double> netSizeGuessList;
	
	/**
	 * The time in which the peer send the first push message
	 */
	private long firstPushTime;
	
	private boolean firstPush;
	//--------------------------------------------------------------------------
	//Initialization
	//--------------------------------------------------------------------------


	/**
	 * Constructor.
	 */
	public MultipleValueHolderED(String prefix)
	{
		pushReqBuffer= new LinkedList<BufferEntry>();
		myPushBuffer= new LinkedList<BufferEntry>();
		converged=false;
		convCounter=0;
		lastNetSizeGuess=0;
		FIS=new HashMap<Integer, Double>();
		timeout=Double.MAX_VALUE;
		waitPullFrom=-1;
		numOfTimeouts=0;
		noNeighborCounter=0;
		netSizeGuessList=new LinkedList<Double>();
		firstPush=true;
	}

	
		/**
	 * Clones the value holder.
	 */
	public Object clone()
	{
		MultipleValueHolderED mvh=null;
		try { mvh=(MultipleValueHolderED)super.clone(); }
		catch( CloneNotSupportedException e ) {} // never happens
		mvh.converged=false;
		mvh.convCounter=0;
		mvh.lastNetSizeGuess=0;
		mvh.FIS=new HashMap<Integer, Double>();
		mvh.waitPullFrom=-1;
		mvh.myPushBuffer= new LinkedList<BufferEntry>();
		mvh.pushReqBuffer= new LinkedList<BufferEntry>();
		mvh.numOfTimeouts=0;
		mvh.noNeighborCounter=0;
		mvh.netSizeGuessList=new LinkedList<Double>();
		mvh.firstPush=true;
		return mvh;
	}

	/**
	 * using the convergence rule of ProFID algorithm, returns if the peer has converged or not
	 * @param peerId id of the peer whose convergence state will be checked
	 * @return convergence state of the peer, true if it has converged, false otherwise.
	 */
	public boolean checkConvergence(long peerId){

		if(!isConverged()){
			netSizeGuessList.add(lastNetSizeGuess);
			// Check if change in uid (unique id) is small enough to increase the convergence counter
			if(lastNetSizeGuess!=0 && epsilon>= Math.abs(getContent().get(-1)-lastNetSizeGuess)/lastNetSizeGuess)
				incrementConvergenceCounter();
			else
				resetConvergenceCounter();
			
			if(converged || convCounter==convLimit){
				//compute the qlobal frequency estimates
				Set<Integer> itemIds=content.keySet();
				HashMap<Integer, Double> result= new HashMap<Integer, Double>();
				for (Iterator<Integer> iterator = itemIds.iterator(); iterator.hasNext();) {
					Integer itemId = (Integer) iterator.next();
					if(itemId>=0)	{
					result.put(itemId,content.get(itemId)/lastNetSizeGuess);
					}
				}
				
				//set the global frequency estimation list of the peer
				setFIS(result);
				
				//convergence time of the peer is set
				convergenceTime=CommonState.getTime();
				
				//Increase the number of converged nodes, when this counter reaches the network size, gossiping ends
				// and computations are done
				ProFIDState.numberOfConvergedNodes++;
			}

		}
		setLastNetSizeGuess(getContent().get(-1));

		return converged || convCounter==convLimit;
	}

	/**
	 * Checks the convergence of the ProFID by comparing the computed and actual frequencies.
	 * If error rate is below @errorRate, than peer decides that algorithm converged. Note that
	 * this is just for performance analysis. Normally, actual frequencies of items are unknown.
	 * @return
	 */
	public boolean checkConvergenceWithoutConvRule(){
		boolean state=false;
		HashMap<Integer, Double> result= new HashMap<Integer, Double>();
		if(!isConverged()){
			state=true;
			Set<Integer> keys=content.keySet();
			Iterator<Integer> itr=keys.iterator();
			while(itr.hasNext()){
				int key=itr.next();
				if(key!=-1){
					double estFreq=content.get(key);
					estFreq*=Network.size();
					result.put(key, estFreq);
					double actFreq=RandomContentFactoryED.getItemFreq().get(key);
					if(100*Math.abs((actFreq-estFreq)/actFreq)>errorRate){
						state= false;
						break;
					}
				}
			}

			if(content.size()!=RandomContentFactoryED.getItemFreq().size()+1)
				state=false;

			if(converged || state){
				setFIS(result);
				convergenceTime=CommonState.getTime();
				ProFIDState.numberOfConvergedNodes++;
			}
		}
		return converged || state;
	}

	//-----------------------------------------------------------------------------------
	//GETTERS and SETTERS
	//-----------------------------------------------------------------------------------
	public long getCurrentRound() {
		return  ((CommonState.getTime()-firstPushTime)/Configuration.getInt("CYCLE"))+1;
	}

	public int getNoNeighborCounter() {
		return noNeighborCounter;
	}

	public long getFirstPushTime() {
		return firstPushTime;
	}

	public void setFirstPushTime(long firstPushTime) {
		this.firstPushTime = firstPushTime;
	}
	
	public void setNoNeighborCounter(int noNeighborCounter) {
		this.noNeighborCounter = noNeighborCounter;
	}

	public void setCurrentRound(long currentRound) {
		this.currentRound = currentRound;
	}
	
	public int getNumOfTimeouts() {
		return numOfTimeouts;
	}

	public void setNumOfTimeouts(int numOfTimeouts) {
		this.numOfTimeouts = numOfTimeouts;
	}

	public HashMap<Integer, Double> getFIS() {
		return FIS;
	}

	public void setFIS(HashMap<Integer, Double> fis) {
		FIS = fis;
	}

	public long getMms() {
		return mms;
	}

	public void setMms(int mms) {
		this.mms = mms;
	}

	public HashMap<Integer, Double> getContent()
	{
		return content;
	}


	@SuppressWarnings("unchecked")
	public void setContent(HashMap<Integer, Double>  content)
	{
		this.content =(HashMap<Integer, Double>)content.clone();
	}
	
	public long getConvergenceTime() {
		return convergenceTime;
	}

	public LinkedList<BufferEntry> getMyPushBuffer() {
		return myPushBuffer;
	}

	public void setMyPushBuffer(LinkedList<BufferEntry> myPushBuffer) {
		this.myPushBuffer = myPushBuffer;
	}

	public LinkedList<BufferEntry> getPushReqBuffer() {
		return pushReqBuffer;
	}

	public void setPushReqBuffer(LinkedList<BufferEntry> pushReqBuffer) {
		this.pushReqBuffer = pushReqBuffer;
	}


	public LinkedList<Double> getNetSizeGuessList() {
		return netSizeGuessList;
	}

	public boolean isConverged() {
		return converged;
	}

	public void setConverged(boolean converged) {
		this.converged = converged;
	}

	public int getConvergenceCounter() {
		return convCounter;
	}

	public void setConvergenceCounter(int convergedCounter) {
		this.convCounter = convergedCounter;
	}

	public void resetConvergenceCounter(){
		convCounter=0;
	}

	public void incrementConvergenceCounter(){
		convCounter+=1;
	}

	public double getLastNetSizeGuess() {
		return lastNetSizeGuess;
	}

	public void setLastNetSizeGuess(double lastNetSizeGuess) {
		this.lastNetSizeGuess = lastNetSizeGuess;
	}
	
	public boolean isFirstPush() {
		return firstPush;
	}

	public void setFirstPush(boolean firstPush) {
		this.firstPush = firstPush;
	}
	
	//-----------------------------------------------------------------------
	//end GETTERS and SETTERS
	//-----------------------------------------------------------------------


	/**
	 * Returns the value as a string.
	 */
	public String toString() { 
		String result="{";
		if(content!=null){
			Set<Integer> ids= content.keySet();
			Iterator<Integer> itr= ids.iterator();

			double frequency=0;

			result+="(-1->"+content.get(-1)+") ";
			itr= ids.iterator();
			double sizeEstimation=0;
			while(itr.hasNext()){
				int id= itr.next();
				if(id!=-1){
					frequency=content.get(id);
					sizeEstimation=1/content.get(-1);
					result+="\t||("+id+"->"+frequency*sizeEstimation+") ";
				}
			}
		}
		result+="}";
		return result; 
	}
	
}



