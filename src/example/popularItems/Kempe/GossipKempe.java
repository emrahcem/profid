package example.popularItems.Kempe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import example.popularItems.Churn;
import example.popularItems.UserInputs;

import peersim.Simulator;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Fallible;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import peersim.util.FileNameGenerator;

public class GossipKempe extends MultipleValueHolderKempe implements
		CDProtocol, EDProtocol {

	private static final String PAR_CRITERIA = "convLimit";

	private static final String PAR_EPSILON = "epsilon";

	private static final String PAR_FANOUT = "fanout";

	private static final String PAR_WRITEENABLED = "writeEnabled";

	private static final String PAR_CONVRULE = "incrementEachRound";

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
	public static boolean incrementEachRound;

	public static long numberOfMessageExchange;
	public static long totalNumberOfMessages;

	public GossipKempe(String prefix) {
		super(prefix);
		ev = new FileNameGenerator("events", ".txt");
		gen = new FileNameGenerator("trace", ".txt");
		centFreqErr = new FileNameGenerator("centFreqErr", ".txt");
		lahiriFreqErr = new FileNameGenerator("lahiriFreqErr", ".txt");
		regularFreqErr = new FileNameGenerator("regularFreqErr", ".txt");
		numberOfMessageExchange = 0;
		totalNumberOfMessages = 0;
		for (int i = 0; i < Simulator.experimentNo; i++) {
			ev.nextCounterName();
			gen.nextCounterName();
			centFreqErr.nextCounterName();
			lahiriFreqErr.nextCounterName();
			regularFreqErr.nextCounterName();
		}

		writeEnabled = Configuration.contains(prefix + "." + PAR_WRITEENABLED);
		incrementEachRound = Configuration
				.contains(prefix + "." + PAR_CONVRULE);
		convLimit = Configuration.getInt(prefix + "." + PAR_CRITERIA, 10);
		epsilon = Configuration.getDouble(prefix + "." + PAR_EPSILON, 10) / 100.0;
		fanout = Configuration.getInt(prefix + "." + PAR_FANOUT, 1);
		threshold = Configuration.getInt(prefix + "." + PAR_THRESHOLD);
		delta = Configuration.getDouble(prefix + "." + PAR_DELTA);
		// errorRate=Configuration.getDouble(prefix + "." + PAR_ERRORRATE,1);
		createErrorFiles();
		if (writeEnabled)
			createTraceFile();

	}

	@Override
	public void nextCycle(Node node, int protocolID) {
		if (node.isUp()) {
			try {
				// if(node.getID()==1)
				// gossipWriter.write("\r\n----------------------------------------------\r\n");
				int linkableID = FastConfig.getLinkable(protocolID);
				Linkable linkable = (Linkable) node.getProtocol(linkableID);

				Set<Integer> itemIds = prevIncomingItems.keySet();

				int min = fanout;
				if (linkable.degree() < fanout)
					min = linkable.degree();

				ItemState state = null;
				for (Iterator<Integer> iterator = itemIds.iterator(); iterator
						.hasNext();) {
					Integer itemId = (Integer) iterator.next();
					state = prevIncomingItems.get(itemId);
					prevIncomingItems.get(itemId)
							.setS(state.getS() / (min + 1));
					prevIncomingItems.get(itemId)
							.setW(state.getW() / (min + 1));
					if (currIncomingItems.containsKey(itemId)) {
						currIncomingItems.get(itemId).setS(
								currIncomingItems.get(itemId).getS()
										+ state.getS());
						currIncomingItems.get(itemId).setW(
								currIncomingItems.get(itemId).getW()
										+ state.getW());
					} else {
						currIncomingItems.put(itemId,
								new ItemState(state.getS(), state.getW()));
					}
				}

				if (linkable.degree() > 0) {
					int shuffledIndex[] = shuffle(linkable.degree());

					for (int i = 0; i < min; i++) {
						Node neigh = linkable.getNeighbor(shuffledIndex[i]);
						if (!neigh.isUp())
							neigh = node;
						else {
							numberOfMessageExchange++;
							totalNumberOfMessages += prevIncomingItems.size();
						}

						((Transport) node.getProtocol(FastConfig
								.getTransport(protocolID)))
								.send(node, neigh, new GossipMessageKempe(
										prevIncomingItems, node), protocolID);
						if (writeEnabled) {
							gossipWriter.write(CommonState.getTime() + "--"
									+ node.getID() + ":" + "sent Gossip to "
									+ neigh.getID() + ":"
									+ this.prevIncomingItems + "\r\n");
						}
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// received a message (passive thread)
	@Override
	public void processEvent(Node node, int pid, Object event) {
		// TODO Auto-generated method stub
		GossipMessageKempe messg = null;
		if (event instanceof GossipMessageKempe)
			messg = (GossipMessageKempe) event;
		else if (event instanceof Churn) {
			try {
				if (((Churn) event).isJoin() == false) {
					node.setFailState(Fallible.DOWN);
					if (writeEnabled)
						GossipKempe.gossipWriter.write(node.getIndex() + " "
								+ node.getID() + " is down\r\n");
				}

				if (((Churn) event).isJoin() == true) {
					node.setFailState(Fallible.OK);
					if (writeEnabled)
						GossipKempe.gossipWriter.write(node.getIndex() + " "
								+ node.getID() + " is up\r\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (node.isUp() && messg != null) {
			if (node.getID() != messg.getSender().getID())
				receivedMessageFromNeighbor = true;

			if (writeEnabled) {
				try {
					gossipWriter
							.write("\r\n----------------------------------------------\r\n");
					gossipWriter.write(CommonState.getTime() + "--"
							+ node.getID() + ":" + "received Gossip from:"
							+ messg.getSender().getID() + "("
							+ messg.getSender().getIndex() + "):"
							+ messg.getContent() + "\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			HashMap<Integer, ItemState> incomingContent = messg.getContent();

			Set<Integer> keys = incomingContent.keySet();
			for (Iterator<Integer> iterator = keys.iterator(); iterator
					.hasNext();) {
				Integer key = (Integer) iterator.next();
				ItemState state = incomingContent.get(key);
				if (currIncomingItems.containsKey(key)) {
					currIncomingItems.get(key).setS(
							currIncomingItems.get(key).getS() + state.getS());
					currIncomingItems.get(key).setW(
							currIncomingItems.get(key).getW() + state.getW());
				} else {
					if (knowsAbout.contains(key)) {
						currIncomingItems.put(key, new ItemState(state.getS(),
								state.getW()));
					} else {
						currIncomingItems.put(key, new ItemState(state.getS(),
								1 + state.getW()));
						knowsAbout.add(key);
					}
				}
			}

			if (writeEnabled) {
				try {
					gossipWriter.write(CommonState.getTime() + "--"
							+ node.getID() + ":" + "currentIncomings:"
							+ currIncomingItems + "\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!incrementEachRound)
				setConverged(checkConvergence(node));
		}
	}

	private void createTraceFile() {
		try {
			gossipWriter = new FileWriter(new File(UserInputs.DESTINATIONFOLDER
					+ File.separator + "experiment" + Simulator.experimentNo
					+ File.separator + gen.nextCounterName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createErrorFiles() {
		try {

			centFreqErrWriter = new FileWriter(new File(
					UserInputs.DESTINATIONFOLDER + File.separator
							+ "experiment" + Simulator.experimentNo
							+ File.separator + centFreqErr.nextCounterName()));

			lahiriFreqErrWriter = new FileWriter(new File(
					UserInputs.DESTINATIONFOLDER + File.separator
							+ "experiment" + Simulator.experimentNo
							+ File.separator + lahiriFreqErr.nextCounterName()));

			regularFreqErrWriter = new FileWriter(
					new File(UserInputs.DESTINATIONFOLDER + File.separator
							+ "experiment" + Simulator.experimentNo
							+ File.separator + regularFreqErr.nextCounterName()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private int[] shuffle(int length) {
		// TODO Auto-generated method stub
		int[] shuf = new int[length];

		for (int i = 0; i < shuf.length; i++) {
			shuf[i] = i;
		}

		int index = 0;
		int temp = 0;
		for (int i = shuf.length; i > 1; i--) {
			index = CommonState.r.nextInt(i);
			temp = shuf[i - 1];
			shuf[i - 1] = shuf[index];
			shuf[index] = temp;
		}

		return shuf;
	}

	// private void writeBufferContent(LinkedList<BufferEntry> buff, Node node){
	// if (write) {
	// Iterator<BufferEntry> itr= buff.iterator();
	//
	// try{
	//
	// gossipWriter.write("********Buffer content**********\r\n");
	//
	// while(itr.hasNext()){
	// BufferEntry entry= itr.next();
	// gossipWriter.write("Sender:"+
	// ((entry.sender==null)?"null":entry.sender.getID())+"\r\n");
	// gossipWriter.write("Receiver:"+((entry.receiver==null)?"null":entry.receiver.getID())+"\r\n");
	// gossipWriter.write("Sender content:"+entry.senderContent+"\r\n");
	// gossipWriter.write("MessageId:"+entry.messageId+"\r\n");
	//
	// }
	// gossipWriter.write("********************************\r\n\r\n");
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	//
	class TimeOut {

	}
}

// private boolean[] initilaizeNeighConverged(Node node,int pid) {
// int linkableID = FastConfig.getLinkable(pid);
// Linkable linkable = (Linkable) node.getProtocol(linkableID);
//
// boolean[] neighConverged= new boolean[linkable.degree()];
// for (int i = 0; i < neighConverged.length; i++) {
// Node peer=linkable.getNeighbor(i);
// MultipleValueHolderED holder=(MultipleValueHolderED)peer.getProtocol(pid);
// neighConverged[i]=holder.isConverged();
// }
//
// return neighConverged;
//
// }
// TODO Auto-generated method stub

// private boolean allNeighborsConverged(boolean[] neighConverged) {
// // TODO Auto-generated method stub
//
// for (int i = 0; i < neighConverged.length; i++) {
// if(!neighConverged[i])
// return false;
// }
// // try {
// // gossipWriter.write("all neighbors converged\r\n");
// // } catch (IOException e) {
// // // TODO Auto-generated catch block
// // e.printStackTrace();
// // }
// return true;
// }

//
// private void write(Node node, Node neigh, HashMap<Integer, Double>
// sendContent) {
// try {
// gossipWriter.write(CommonState.getTime()+" ("+node.getID()+"->"+neigh.getID()+") "+sendContent+"\r\n");
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// }
