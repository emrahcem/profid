package example.popularItems.HierProFID;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import example.popularItems.Churn;
import example.popularItems.ProFIDNode;
import example.popularItems.ProFIDNode.NodeType;
import example.popularItems.UserInputs;
import example.popularItems.BufferEntry;
import example.popularItems.ProFID.GossipMessage;
import example.popularItems.ProFID.MultipleValueHolderED;

import peersim.Simulator;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Fallible;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import peersim.util.FileNameGenerator;
import peersim.util.RandPermutation;

public class HierGossipED extends MultipleValueHolderED implements CDProtocol,EDProtocol {

	private static final String PAR_CRITERIA = "convLimit";

	private static final String PAR_EPSILON = "epsilon";

	private static final String PAR_TIMEOUT = "timeout";

	private static final String PAR_FANOUT = "fanout";

	private static final String PAR_MMS = "mms";

	private static final String PAR_WRITEENABLED = "writeEnabled";

	private static final String PAR_THRESHOLD = "threshold";

	private static final String PAR_DELTA = "delta";

	public static FileWriter gossipWriter;
	public static FileWriter centFreqErrWriter;
	public static FileWriter lahiriFreqErrWriter;
	public static FileWriter regularFreqErrWriter;

	private final FileNameGenerator ev;
	private final FileNameGenerator gen;
	private final FileNameGenerator centFreqErr;
	private final FileNameGenerator lahiriFreqErr;
	private final FileNameGenerator regularFreqErr;
	public static boolean writeEnabled;
	public static long numberOfMessageExchange;
	public static long totalNumberOfMessages;

	public HierGossipED(String prefix) {
		super(prefix);
		ev=new FileNameGenerator("events",".txt");
		gen = new FileNameGenerator("trace",".txt");
		centFreqErr = new FileNameGenerator("centFreqErr",".txt");
		lahiriFreqErr = new FileNameGenerator("lahiriFreqErr",".txt");
		regularFreqErr = new FileNameGenerator("regularFreqErr",".txt");
		numberOfMessageExchange=0;
		totalNumberOfMessages=0;
		noNeighborCounter=0;

		for (int i = 0; i < Simulator.experimentNo; i++) {
			ev.nextCounterName();
			gen.nextCounterName();
			centFreqErr.nextCounterName();
			lahiriFreqErr.nextCounterName();
			regularFreqErr.nextCounterName();
		}

		mms=Configuration.getInt(prefix + "." + PAR_MMS,Integer.MAX_VALUE);
		convLimit= Configuration.getInt(prefix + "." + PAR_CRITERIA,10);
		epsilon=Configuration.getDouble(prefix + "." + PAR_EPSILON,10)/100.0;
		fanout=Configuration.getInt(prefix + "." + PAR_FANOUT,1);
		timeout=Configuration.getDouble(prefix + "." + PAR_TIMEOUT,Double.MAX_VALUE);
		threshold=Configuration.getInt(prefix + "." + PAR_THRESHOLD);
		delta=Configuration.getDouble(prefix + "." + PAR_DELTA);
		writeEnabled=Configuration.contains(prefix + "." + PAR_WRITEENABLED);

		createErrorFiles();

		if (writeEnabled) 
			createTraceFile();

	}

	@Override
	public void nextCycle(Node node, int protocolID) {
		
		if(node.isUp() && ((ProFIDNode) node).getType()==NodeType.DSNODE){
			deleteOwnPushes();
			if(getCurrentRound()%3==0){
				deleteOthersPushes(node);
			}

			try{
				ArrayList<Integer> domNeighs=((ProFIDNode)node).getDomNeighs();
				if (domNeighs!=null && domNeighs.size()>0){
					int min=fanout;
					if(domNeighs.size()<fanout)
						min=domNeighs.size();

					RandPermutation rp= new RandPermutation(CommonState.r);
					rp.setPermutation(domNeighs.size());
					int rnd;

					do{
						rnd=rp.next();
					}while(sendPush(node, Network.get(domNeighs.get(rnd)), protocolID)==null && rp.hasNext());

					int counter=1;
					while (rp.hasNext() && counter<min) {
						rnd=rp.next();
						if(Network.get(domNeighs.get(rnd)).isUp()){
							counter++;
							myPushBuffer.add(new BufferEntry(null,Network.get(domNeighs.get(rnd)),null));
						}
					}

					if(min>1)
						writeToGossipFile("add other push messages to buffer\r\n");
				}
				else{
					System.err.println("Peer" + node.getID()+" has no 2-hop dom neighbor");
					System.exit(0);
				}

				boolean noNeig=true;
				for(int i=0;i<domNeighs.size();i++){
					if(Network.get(domNeighs.get(i)).isUp()){
						noNeig=false;
					}
				}

				if(noNeig){
					noNeighborCounter++;
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			deleteOthersPushes(node);
			deleteOwnPushes();
			waitPullFrom=-1;

		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void processEvent(Node node, int pid, Object event) {
		if(((ProFIDNode)node).getType()==NodeType.DSNODE){
			GossipMessage messg=null;
			TimeOut timer=null;
			boolean churn=false;
	
			//get the type of the event
			if(event instanceof GossipMessage)
				messg = (GossipMessage)event;
			else if(event instanceof TimeOut)
				timer=(TimeOut)event;
			else if(event instanceof Churn)
			{
				churn=true;
				if(((Churn) event).isJoin() == false)
				{
					node.setFailState(Fallible.DOWN);
					writeToGossipFile(node.getIndex()+" "+node.getID()+" is down\r\n");
				}

				if(((Churn) event).isJoin() == true)
				{
					node.setFailState(Fallible.OK);
					writeToGossipFile(node.getIndex()+" "+node.getID()+" is up\r\n");
				}
			}

			try {
				if (node.isUp() && !churn){
					//if it is a timer event
					if(timer!=null){
						//if peer is still waiting for the message
						writeToGossipFile(CommonState.getTime()+"::"+node.getID()+" is in round :"+getCurrentRound()+",timer round is"+timer.getRound()+"\r\n");
						if(isTimeOutOccured(timer)){
							//writeToEventFile(CommonState.getTime()+"::"+node.getID()+"<-"+"TIMEOUT, read from buffer");
							writeToGossipFile(CommonState.getTime()+":: "+node.getID()+"<-"+"TIMEOUT, read from buffer\r\n");
							//System.err.println(CommonState.getTime()+":: "+node.getID()+" Timeout occured="+numOfTimeouts);
							numOfTimeouts++;
							waitPullFrom=-1;

							readPushReqBuffer(node, pid);
							readMyPushBuffer(node, pid);
						}
						//pull already came, timeout did not occur
						else{
							if(getCurrentRound()==timer.getRound()){
								writeToGossipFile(CommonState.getTime()+":: "+node.getID()+"Pull already came, no timeout:\r\n\r\n");
							}
							else{
								writeToGossipFile(CommonState.getTime()+":: "+node.getID()+"This timeout event has aged\r\n\r\n");
							}
						}
					}
					//if it is a gossip message
					else if(messg!=null){

						//if not waiting for pull and push came -> reply with a pull message
						if (messg.getType().equals("push") && !isWaitingForPull()) {

							//////reply all push requests, including this and those in the buffer//////
							readPushReqBuffer(node, pid);
							sendPull(node, pid, messg);
							readMyPushBuffer(node, pid);
						} 
						//if pull came and I wait a pull from the messg sender 
						else if (messg.getType().equals("pull") && waitPullFrom==messg.getSender().getID()) {
							writeToGossipFile(CommonState.getTime()+":: "+node.getID()+" <--PULL-- "+messg.getSender().getID()+"("+messg.getContent()+"), updated state"+content+"\r\n");

							//////update the content then send reply all push requests in the buffer, then send your next push(if exists)////// 

							updateContent(messg);

							//update your state
							waitPullFrom=-1;

							//writeToGossipFile(CommonState.getTime()+":: "+ node.getID()+":"+"Updated state:\r\n");
							readPushReqBuffer(node, pid);
							readMyPushBuffer(node, pid);
						} 
						//if waiting for pull and push came
						else if (messg.getType().equals("push") && isWaitingForPull() ) {
							if(waitPullFrom!=messg.getSender().getID()){
								writeToGossipFile(CommonState.getTime()+":: "+node.getID()+" <--PUSH-- "+ messg.getSender().getID()+"("+messg.getContent()+") (waiting for PULL from another peer)\r\n");
								writeToGossipFile(CommonState.getTime()+":: "+node.getID()+":"+"added request to buffer ... \r\n");

								//add request to buffer
								pushReqBuffer.add(new BufferEntry(messg.getSender(), null, (HashMap<Integer, Double>)messg.getContent().clone()));
							}
							//if it did came from whom you want a pull message
							else{
								writeToGossipFile(CommonState.getTime()+":: "+node.getID()+" <--PUSH-- "+messg.getSender().getID()+"("+messg.getContent()+") (waiting for PULL from this peer).Perform averaging:("+content+")\r\n");

								performAveraging(content, messg.getContent());

								waitPullFrom=-1;

								readPushReqBuffer(node, pid);
								readMyPushBuffer(node, pid);
							}
						}

						//pull came from a peer that I was not waiting for a pull, this may occur due to a timeout
						else{
							writeToGossipFile(CommonState.getTime()+":: "+node.getID()+":UNEXPECTED "+messg.getType()+" came from "+messg.getSender().getID()+",ignore it.\r\n");
						}
						if(messg.getContent().containsKey(-1)){
							setConverged(checkConvergence(node.getID()));
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void deleteOthersPushes(Node n) {

		while(!pushReqBuffer.isEmpty()){
			BufferEntry e=pushReqBuffer.pop();
			//System.err.println(CommonState.getTime()+":: "+n.getID()+"deleted "+e.sender.getID()+"'s push request");
			writeToGossipFile(CommonState.getTime()+":: "+n.getID()+"deleted"+e.getSender().getID()+"'s push request\r\n");
		}
		//if(pushReqBuffer.removeAll(pushReqBuffer)){

		//}
		waitPullFrom=-1;

	}

	private void deleteOwnPushes(){
		myPushBuffer.removeAll(myPushBuffer);
		waitPullFrom=-1;
	}

	private void updateContent(GossipMessage messg) {
		Set<Integer> contSet = messg.getContent().keySet();
		Iterator<Integer> itr = contSet.iterator();

		//update the content
		while (itr.hasNext()) {
			int key = itr.next();
			if (content.containsKey(key))
				content.put(key, messg.getContent().get(key));
		}
	}

	private boolean isWaitingForPull() {
		return waitPullFrom>=0;
	}

	private void writeToGossipFile(String str){
	
		if (writeEnabled){
			try {
				gossipWriter.write(str);
				gossipWriter.flush();	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isTimeOutOccured(TimeOut timer) {
		return waitPullFrom==timer.getDest().getID() && timer.getRound()==getCurrentRound();
	}

	private void sendPull(Node node, int pid, GossipMessage messg)
	throws IOException {
		writeToGossipFile(CommonState.getTime()+":: "+node.getID()+" <--PUSH-- "+messg.getSender().getID()+", perform averaging"+content+"\r\n");

		HashMap<Integer, Double> itemsToSend = null;
		//perform averaging using own content and incoming content
		itemsToSend = performAveraging(content, messg.getContent());
		((Transport) node.getProtocol(FastConfig.getTransport(pid)))
		.send(node, messg.getSender(), new GossipMessage(
				"pull", itemsToSend, node), pid);

		numberOfMessageExchange++;
		totalNumberOfMessages+=itemsToSend.size();
		writeToGossipFile(CommonState.getTime()+":: "+node.getID()+" --PULL--> "+messg.getSender().getID()+"("+itemsToSend+")\r\n");
	}

	private HashMap<Integer, Double> sendPush(Node node, Node neigh, int protocolID)
	throws IOException {

		//if it is not alive
		if (!neigh.isUp())
			return null;

		//select which items to send
		HashMap<Integer, Double> itemsToSend=getNRandomItemsFromHashMap(node,protocolID, content, mms);
		//send message
		((Transport)node.getProtocol(FastConfig.getTransport(protocolID))).
		send(
				node,
				neigh,
				new GossipMessage("push",itemsToSend,node),
				protocolID);

		//check buffer if you have a push message of the receiver
		BufferEntry entry=isInPushReqBuffer(neigh);
		if(entry!=null){
			writeToGossipFile(node.getID()+" has already received a push from"+neigh.getID()+", just calculate average \r\n");

			performAveraging(content, entry.getSenderContent());
			waitPullFrom=-1;
			readPushReqBuffer(node, protocolID);
		}
		else{
			TimeOut timer=new TimeOut(neigh,getCurrentRound());
			writeToGossipFile(CommonState.getTime()+":: "+node.getID()+"Timer started:("+neigh.getID()+","+getCurrentRound()+")\r\n");

			//add timer event to the event table
			EDSimulator.add((long)timeout,timer,node,protocolID);

			//set waiting id to neighbor id
			waitPullFrom=neigh.getID();

			writeToGossipFile(CommonState.getTime()+":: "+node.getID()+" --PUSH--> "+neigh.getID()+"("+itemsToSend+")"+"\r\n");
		}

		numberOfMessageExchange++;
		totalNumberOfMessages+=itemsToSend.size();

		return itemsToSend;
	}
	private BufferEntry isInPushReqBuffer(Node neigh) {

		for(int j=0;j<pushReqBuffer.size();j++){
			BufferEntry entry=pushReqBuffer.get(j);
			if(entry!=null && entry.getSender()!=null && entry.getSender().getID()==neigh.getID()){
				return pushReqBuffer.remove(j);
			}
		}
		return null;
	}

	private void readMyPushBuffer(Node node, int pid) throws IOException {
		//if it is my push message
		BufferEntry buff = myPushBuffer.poll();
		if(buff!=null){
			HashMap<Integer, Double> itemsToSend=sendPush(node, buff.getReceiver(), pid);
			writeToGossipFile(CommonState.getTime()+"[BUFFER]:: "+ node.getID()+" --PUSH--> "+buff.getReceiver().getID()+"("+itemsToSend+")\r\n");
		}
	}
	@SuppressWarnings("unchecked")
	private void readPushReqBuffer(Node node, int pid) throws IOException {

		while (!pushReqBuffer.isEmpty()) {
			//if it is my push message
			BufferEntry buff = pushReqBuffer.poll();
			writeToGossipFile(CommonState.getTime()+"[BUFFER]:: "+ node.getID()+" <--PUSH-- "+buff.getSender().getID()+"("+buff.getSenderContent()+")\r\n");

			HashMap<Integer, Double> avg= performAveraging(content,(HashMap<Integer, Double>)buff.getSenderContent().clone());
			((Transport) node.getProtocol(FastConfig.getTransport(pid))).send(
					node,
					buff.getSender(), 
					new GossipMessage("pull", avg, node),
					pid);

			numberOfMessageExchange++;
			totalNumberOfMessages+=avg.size();

			readMyPushBuffer(node, pid);
			writeToGossipFile(CommonState.getTime()+":: "+node.getID()+" --PULL--> "+buff.getSender().getID()+ "("+avg+")\r\n");
		}
	} 

	private  HashMap<Integer, Double>  performAveraging(HashMap<Integer, Double> thisContent,
			HashMap<Integer, Double> incomingContent) {

		HashMap<Integer, Double> itemsToSend= new HashMap<Integer, Double>();

		Iterator<Integer> incomingItr= incomingContent.keySet().iterator();

		double thisVal=0;
		double average=0;
		while(incomingItr.hasNext()){
			int itemId=incomingItr.next();

			thisVal=0;

			if(thisContent.containsKey(itemId))
				thisVal=thisContent.get(itemId);

			average=(thisVal+incomingContent.get(itemId))/2;
			thisContent.put(itemId, average);
			itemsToSend.put(itemId, average);

		}

		return itemsToSend;
	}

	private HashMap<Integer, Double> getNRandomItemsFromHashMap(Node node,int pid, HashMap<Integer,Double> map, int N){
		HashMap<Integer, Double> ret= new HashMap<Integer, Double>();

		if(N>=map.size()){
			return map;
		}

		Object arr[]= map.keySet().toArray();
		RandPermutation rp= new RandPermutation(CommonState.r);
		rp.setPermutation(arr.length);
		for(int i=1;i<=N;i++){
			int key=rp.next();
			ret.put((Integer)arr[key],map.get(arr[key]));
		}
		return ret;
	}

	private void createTraceFile() {
		try {
			gossipWriter = new FileWriter(new File(
					UserInputs.DESTINATIONFOLDER +File.separator+"experiment"
					+ Simulator.experimentNo + File.separator
					+ gen.nextCounterName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createErrorFiles() {
		try {

			centFreqErrWriter = new FileWriter(new File(
					UserInputs.DESTINATIONFOLDER +File.separator+"experiment"
					+ Simulator.experimentNo +File.separator
					+ centFreqErr.nextCounterName()));

			lahiriFreqErrWriter = new FileWriter(new File(
					UserInputs.DESTINATIONFOLDER +File.separator+"experiment"
					+ Simulator.experimentNo + File.separator
					+ lahiriFreqErr.nextCounterName()));

			regularFreqErrWriter = new FileWriter(new File(
					UserInputs.DESTINATIONFOLDER +File.separator+"experiment"
					+ Simulator.experimentNo + File.separator
					+ regularFreqErr.nextCounterName()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

//	@SuppressWarnings("unused")
//	private HashMap<Integer, Double> getNSelectiveItemsFromHashMap(HashMap<Integer,Double> map, int N){
//		HashMap<Integer, Double> ret= new HashMap<Integer, Double>();
//
//
//		if(N>=map.size()){
//			return map;
//		}
//
//		for(int i=0;i<N;i++){
//			int key=getMapping().get(getItemCounter()%map.size());
//			incrementItemCounter();
//			ret.put(key, map.get(key));
//		}
//		return ret;
//	}

	class TimeOut{

		private Node dest;
		private long round;

		public TimeOut(Node dest, long round){
			this.dest=dest;
			this.round=round;
		}

		public Node getDest() {
			return dest;
		}

		public long getRound() {
			return round;
		}

		public void setRound(long round) {
			this.round = round;
		}

		public void setDest(Node dest) {
			this.dest = dest;
		}


	}
}
