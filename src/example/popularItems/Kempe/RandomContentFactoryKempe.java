package example.popularItems.Kempe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import example.popularItems.UserInputs;
import example.popularItems.ZipfGenerator;

import peersim.Simulator;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.util.FileNameGenerator;

/**
 * Initializes the content of peers.
 * 
 * @author Emrah Cem
 * 
 */
public class RandomContentFactoryKempe {

	private final FileNameGenerator gen;

	/**
	 * itemFreq: keeps (itemId, itemFreq) pairs This variable is used to keep
	 * the overall(system-wide) frequency of items,
	 * e.g.,{(1,32),(2,15),(3,47)},etc.
	 */
	private static HashMap<Integer, Double> itemFreq = new HashMap<Integer, Double>();

	private double initW = 1.0;

	/**
	 * content: keeps( peerId, {(itemId1,itemState1),(itemId2,itemState2),...} )
	 * itemState is composed of (s,w) tuple. Where s is the frequency and w is
	 * the weight. This variable keeps the contents of each peer, (will be
	 * filled in and returned at the end) e.g., ( 1,{(4,(50,1)),(5,(60,1))} ),
	 * means that peer1 keeps item4 with frequency 50 and item5 with frequency
	 * 60.
	 */
	private final HashMap<Integer, Map<Integer, ItemState>> content = new HashMap<Integer, Map<Integer, ItemState>>();

	private String itemFreqDist;
	private double skewVal;

	private String distToPeers;
	private double alpha;

	public RandomContentFactoryKempe() {
		gen = new FileNameGenerator("initialStates", ".txt");
	}

	/**
	 * 
	 * @param numOfPeersToDistribute
	 *            to how many peers the content will be distributed
	 * @return the content of each peer
	 */
	public HashMap<Integer, Map<Integer, ItemState>> produceContent(
			int numOfPeersToDistribute) {

		int numOfPeers = Configuration.getInt("SIZE");
		int numOfItems = Configuration.getInt("NUMBEROFITEMTYPES");
		int min = Configuration.getInt("MINIMUMFREQUENCY");
		int max = Configuration.getInt("MAXIMUMFREQUENCY");
		itemFreqDist = Configuration.getString("ITEMFREQDIST");
		if (itemFreqDist.equalsIgnoreCase("Zipf"))
			skewVal = Configuration.getDouble("SKEW");

		distToPeers = Configuration.getString("DISTTOPEERS");
		if (distToPeers.equalsIgnoreCase("PowerLaw"))
			alpha = Configuration.getDouble("ALPHA");
		int uniqueId = -1;

		double freq = 0;

		if (itemFreqDist.equalsIgnoreCase("Zipf")) {
			ZipfGenerator distrib = new ZipfGenerator(max - min, skewVal);
			for (int i = 0; i < numOfItems; i++) {
				freq = distrib.next();
				itemFreq.put(i, freq + min);
			}
		}

		// determine the overall frequencies of items randomly between
		// [minFrequency , maxFrequency]
		// e.g., if minFreq=5 maxFrequency=20, randomly give a frequency btw [5
		// , 20] to each item. (1,7),(2,16),(3,15), etc. ,first number
		// is the item id, second number is its frequency
		else if (itemFreqDist.equalsIgnoreCase("Uniform")) {

			for (int i = 0; i < numOfItems; i++) {
				freq = min + CommonState.r.nextInt(max - min + 1);
				itemFreq.put(i, freq);
			}
		}

		Map<Integer, ItemState> hash = null;

		for (int i = 0; i < numOfPeers; i++) {
			hash = new HashMap<Integer, ItemState>();
			hash.put(uniqueId, new ItemState(0.0, initW));
			content.put(i, hash);
		}
		hash = new HashMap<Integer, ItemState>();
		hash.put(uniqueId, new ItemState(1.0, initW));
		content.put(CommonState.r.nextInt(numOfPeers), hash);

		Set<Integer> set = itemFreq.keySet();
		Iterator<Integer> itr = set.iterator();

		while (itr.hasNext()) {
			int itemId = itr.next();
			freq = itemFreq.get(itemId);

			// distribute each item one by one to a random peer
			// if an item already exists in that peer, just increment its
			// frequency
			distributeItemsToPeers(numOfPeersToDistribute, itemId, freq);
		}

		return content;
	}

	@SuppressWarnings("unused")
	private void initializeFrequencies(int numOfPeers, int numOfItems) {
		for (int i = 0; i < numOfPeers; i++) {
			HashMap<Integer, ItemState> peerContent = (HashMap<Integer, ItemState>) content
					.get(i);
			for (int j = 0; j < numOfItems; j++) {
				peerContent.put(j, new ItemState(0, initW));
			}
		}
	}

	private void distributeItemsToPeers(int numOfPeers, int itemId, double freq) {
		Map<Integer, ItemState> hash;
		int peerId=-1;
		double x0 = 1, x1 = numOfPeers;
		double y = 0;
		for (int i = 0; i < freq; i++) {
			y = CommonState.r.nextDouble();

			// randomly chosen peer(using power-law distribution) to keep a copy
			// of item with id'itemId'
			if (distToPeers.equalsIgnoreCase("PowerLaw"))
				peerId = (int) Math
						.floor(Math.pow(
								((Math.pow(x1, (alpha + 1)) - Math.pow(x0,
										(alpha + 1))) * y + Math.pow(x0,
										(alpha + 1))), (1 / (alpha + 1))));
			// uniformly distribute to the peers in the network
			else if (distToPeers.equalsIgnoreCase("Uniform"))
				peerId = CommonState.r.nextInt(numOfPeers);
			// current content of the randomly chosen peer
			Map<Integer, ItemState> itemSet = (HashMap<Integer, ItemState>) content
					.get(peerId);

			// if peer has no item yet, add the item with id 'itemId' with a
			// frequency 1.
			if (itemSet == null) {
				hash = new HashMap<Integer, ItemState>();
				hash.put(itemId, new ItemState(1.0, initW));
				content.put(peerId, hash);
			}

			// if the peer has that item, increment its frequency
			// else add an entry with frequncy 1.
			else {
				if (itemSet.containsKey(itemId)) {
					itemSet.get(itemId).setS(itemSet.get(itemId).getS() + 1);
					// itemSet.put(itemId,itemSet.get(itemId));
				} else {
					itemSet.put(itemId, new ItemState(1.0, initW));
				}
			}
		}
	}

	// writes the result into a file called content.txt
	// not so crucial, just to see what the random initialization looks like.

	public void write(HashMap<Integer, Map<Integer, ItemState>> content,
			HashMap<Integer, Double> itemFreq) {

		// Write the summary of what is done to the file called 'content.txt'
		// id debug argument is set to 1, summary is also written to the
		// console.

		try {

			Set<Integer> set = itemFreq.keySet();
			Iterator<Integer> itr = set.iterator();

			Set<Integer> peers = content.keySet();
			Iterator<Integer> iter = peers.iterator();

			FileWriter writer = null;
			try {
				for (int i = 0; i < Simulator.experimentNo; i++) {
					gen.nextCounterName();
				}

				writer = new FileWriter(new File(UserInputs.DESTINATIONFOLDER
						+ File.separator + "experiment"
						+ Simulator.experimentNo + File.separator
						+ gen.nextCounterName()));

				set = itemFreq.keySet();
				itr = set.iterator();
				writer.write("itemId\tState\r\n");
				writer.write("-------------------\r\n");

				while (itr.hasNext()) {
					int next = itr.next();
					double freq = itemFreq.get(next);
					writer.write("item:" + next + "\tfreq:" + freq + "\r\n");
				}
				writer.write("\r\n");
				writer.write("peerId\tContent\r\n");
				writer.write("-------------------\r\n");
				while (iter.hasNext()) {
					int peer = iter.next();

					Map<Integer, ItemState> m = (HashMap<Integer, ItemState>) content
							.get(peer);
					Set<Integer> items = m.keySet();
					Iterator<Integer> itr2 = items.iterator();
					writer.write(Network.size() * Simulator.experimentNo + peer
							+ ":\t");

					while (itr2.hasNext()) {
						int id = itr2.next();
						ItemState state = m.get(id);
						writer.write("(" + id + ",(" + state.getS() + ","
								+ state.getW() + ")) ");
					}

					writer.write("\r\n");
				}

			} finally {
				writer.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static HashMap<Integer, Double> getItemFreq() {
		return itemFreq;
	}

	public HashMap<Integer, Map<Integer, ItemState>> getContent() {
		return content;
	}

	public HashMap<Integer, ItemState> getDeepCopyOfInitContent(int peerId) {
		Map<Integer, ItemState> peerState = content.get(peerId);
		Set<Integer> itemSet = peerState.keySet();
		HashMap<Integer, ItemState> newHash = new HashMap<Integer, ItemState>();
		for (Iterator<Integer> iterator2 = itemSet.iterator(); iterator2
				.hasNext();) {
			Integer itemId = (Integer) iterator2.next();
			ItemState itemState = peerState.get(itemId);
			newHash.put(itemId,
					new ItemState(itemState.getS(), itemState.getW()));
		}
		return newHash;
	}

}
