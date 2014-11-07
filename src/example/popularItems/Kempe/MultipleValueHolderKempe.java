package example.popularItems.Kempe;

import java.io.IOException;
import java.util.*;

import example.popularItems.ProFIDState;

import peersim.core.*;

public class MultipleValueHolderKempe implements Protocol {

	/** Value held by this protocol */
	protected HashMap<Integer, ItemState> content;
	protected HashMap<Integer, ItemState> prevIncomingItems;
	protected HashMap<Integer, ItemState> currIncomingItems;
	protected HashSet<Integer> knowsAbout;
	protected long convergenceTime;
	protected int convLimit;

	protected int convCounter;

	protected boolean receivedMessageFromNeighbor = false;

	protected List<Double> uiEst;
	protected double lastNetSizeGuess;

	public double getLastNetSizeGuess() {
		return lastNetSizeGuess;
	}

	public void setLastNetSizeGuess(double lastNetSizeGuess) {
		this.lastNetSizeGuess = lastNetSizeGuess;
	}

	protected boolean converged;
	protected double epsilon;
	protected int fanout;
	// protected double errorRate;
	public static double delta;
	public static int threshold;
	protected HashMap<Integer, Double> FIS;

	// --------------------------------------------------------------------------
	// Initialization
	// --------------------------------------------------------------------------

	/**
	 * Does nothing.
	 */
	public MultipleValueHolderKempe(String prefix) {
		content = new HashMap<Integer, ItemState>();
		prevIncomingItems = new HashMap<Integer, ItemState>();
		currIncomingItems = new HashMap<Integer, ItemState>();
		knowsAbout = new HashSet<Integer>();
		converged = false;
		convCounter = 0;
		FIS = new HashMap<Integer, Double>();
		uiEst = new ArrayList<Double>();

	}

	// --------------------------------------------------------------------------

	/**
	 * Clones the value holder.
	 */
	public Object clone() {
		// System.err.println(CommonState.getTime()+"--MultipleValueHolder::clone()");
		MultipleValueHolderKempe mvh = null;
		try {
			mvh = (MultipleValueHolderKempe) super.clone();
		} catch (CloneNotSupportedException e) {
		} 
		mvh.FIS = new HashMap<Integer, Double>();
		mvh.prevIncomingItems = new HashMap<Integer, ItemState>();
		mvh.currIncomingItems = new HashMap<Integer, ItemState>();
		mvh.knowsAbout = new HashSet<Integer>();
		mvh.uiEst = new ArrayList<Double>();
		return mvh;
	}

	// --------------------------------------------------------------------------
	// methods
	// --------------------------------------------------------------------------

	public List<Double> getUiEst() {
		return uiEst;
	}

	public void setUiEst(List<Double> uiEst) {
		this.uiEst = uiEst;
	}

	public HashMap<Integer, Double> getFIS() {
		return FIS;
	}

	public void setFIS(HashMap<Integer, Double> fis) {
		FIS = fis;
	}

		// --------------------------------------------------------------------------

	/**
	 * @inheritDoc
	 */
	public void setContent(HashMap<Integer, ItemState> hashMap) {
		this.content = hashMap;
	
		Set<Integer> itemIds = content.keySet();
		for (Iterator<Integer> iterator = itemIds.iterator(); iterator.hasNext();) {
			knowsAbout.add((Integer) iterator.next());
		}
	}

	// --------------------------------------------------------------------------

	// public boolean checkConvergence2(){
	// boolean state=false;
	// //convergedNow=false;
	// HashMap<Integer, Double> result= new HashMap<Integer, Double>();
	// if(!isConverged()){
	// state=true;
	// Set<Integer> keys=getprevIncomingItems().keySet();
	// Iterator<Integer> itr=keys.iterator();
	//
	// while(itr.hasNext()){
	// int key=itr.next();
	// if(key!=-1){
	// ItemState estState=getprevIncomingItems().get(key);
	// double estFreq=(estState.getS()/estState.getW())*Network.size();
	// result.put(key, estFreq);
	// //
	// //System.err.println(Math.abs(RandomContentFactoryED.getItemFreq().get(key)/Network.size()-val)+">"+(RandomContentFactoryED.getItemFreq().get(key)/Network.size())*Math.pow(10,
	// -3));
	// double actFreq=RandomContentFactoryKempe.getItemFreq().get(key);
	// //System.err.println("actAvg"+actAvg+",guessedAvg:"+guessedAvg+",err:%"+100*Math.abs((actAvg-guessedAvg)/actAvg));
	// if(errorRate<100*Math.abs((actFreq-estFreq)/actFreq)){
	// state= false;
	// break;
	// }
	// }
	// }
	//
	// if(converged || state){
	// //convergedNow=true;
	// setFIS(result);
	// convergenceTime=CommonState.getTime();
	// Network.numberOfConvergedNodes++;
	//
	// }
	//
	//
	// }
	// //
	// // if(content.size()!=RandomContentFactoryKempe.getItemFreq().size()+1)
	// // state=false;
	// // }
	//
	// return converged || state;
	// }

	public boolean checkConvergence(Node node) {

		if (!isConverged()) {


				if (getprevIncomingItems().get(-1) == null
						|| getCurrIncomingItems().get(-1) == null) {
				}
				try{
				double currNetSize = getCurrIncomingItems().get(-1).getS()
						/ getCurrIncomingItems().get(-1).getW();
				double expectedVal = getprevIncomingItems().get(-1).getS()
						/ getprevIncomingItems().get(-1).getW();
				uiEst.add(expectedVal);
				
				boolean incr = false;
				if (GossipKempe.incrementEachRound)
					incr = getprevIncomingItems().get(-1).getW() != 0
							&& epsilon >= Math
									.abs((expectedVal - lastNetSizeGuess)
											/ lastNetSizeGuess);
				else
					incr = getCurrIncomingItems().get(-1).getW() != 0
							&& epsilon >= Math
									.abs((currNetSize - lastNetSizeGuess)
											/ lastNetSizeGuess);

				if (incr)
					incrementConvergedCounter();
				else
					resetConvergedCounter();
				if (GossipKempe.writeEnabled)
					try {
						GossipKempe.gossipWriter.write(node.getID()
								+ " convCounter:"
								+ convCounter
								+ " change:"
								+ Math.abs((expectedVal - lastNetSizeGuess)
										/ lastNetSizeGuess) + "\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				if (converged || convCounter == convLimit) {
					Set<Integer> itemIds = getprevIncomingItems().keySet();
					HashMap<Integer, Double> result = new HashMap<Integer, Double>();
					for (Iterator<Integer> iterator = itemIds.iterator(); iterator
							.hasNext();) {
						Integer itemId = (Integer) iterator.next();
						double val = getprevIncomingItems().get(itemId).getS()
								/ getprevIncomingItems().get(itemId).getW();
						if (itemId != -1) {
							result.put(itemId, val / lastNetSizeGuess);
						}
					}
					setFIS(result);
					convergenceTime = CommonState.getTime();
					ProFIDState.numberOfConvergedNodes++;
				}
				}catch(NullPointerException e){
					return false;
				}
			
		}
		if (GossipKempe.incrementEachRound)
			setLastNetSizeGuess(getprevIncomingItems().get(-1).getS()
					/ getprevIncomingItems().get(-1).getW());
		else
			setLastNetSizeGuess(getCurrIncomingItems().get(-1).getS()
					/ getCurrIncomingItems().get(-1).getW());

		return converged || convCounter == convLimit;
	}

	public void setReceivedMessageFromNeighbor(
			boolean receivedMessageFromNeighbor) {
		this.receivedMessageFromNeighbor = receivedMessageFromNeighbor;
	}

	public boolean isConverged() {
		return converged;
	}

	public void setConverged(boolean converged) {
		this.converged = converged;
	}

	public int getConvergeCriteria() {
		return convLimit;
	}

	public void setConvergeCriteria(int convergeCriteria) {
		this.convLimit = convergeCriteria;
	}

	public int getConvergedCounter() {
		return convCounter;
	}

	public void setConvergedCounter(int convergedCounter) {
		this.convCounter = convergedCounter;
	}

	public void resetConvergedCounter() {
		convCounter = 0;
	}

	public void incrementConvergedCounter() {
		convCounter += 1;
	}

	/**
	 * Returns the value as a string.
	 */
	public String toString() {
		String result = "{";
		if (content != null) {
			Set<Integer> ids = content.keySet();
			Iterator<Integer> itr = ids.iterator();

			ItemState state = null;

			itr = ids.iterator();
			while (itr.hasNext()) {
				int id = itr.next();
				if (id != -1) {
					state = content.get(id);

					result += "\t(" + id + "->(" + state.getS() + ","
							+ state.getW() + ")) ";
				}
			}
		}
		result += "}";
		return result;
	}

	public HashMap<Integer, ItemState> getCurrIncomingItems() {
		return currIncomingItems;
	}

	public void setCurrIncomingItems(
			HashMap<Integer, ItemState> currIncomingItems) {
		this.currIncomingItems = currIncomingItems;
	}

	public HashMap<Integer, ItemState> getprevIncomingItems() {
		return prevIncomingItems;
	}

	public void setprevIncomingItems(HashMap<Integer, ItemState> incomingItems) {
		this.prevIncomingItems = incomingItems;
	}

	public HashSet<Integer> getKnowsAbout() {
		return knowsAbout;
	}

	public void setKnowsAbout(HashSet<Integer> knowsAbout) {
		this.knowsAbout = knowsAbout;
	}
}
