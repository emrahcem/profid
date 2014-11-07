package example.popularItems.ProFID;

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
public class RandomContentFactoryED {

	private final FileNameGenerator gen;

	/**
	 * itemFreq: keeps (itemId, itemFreq) pairs This variable is used to keep
	 * the overall(system-wide) frequency of items,
	 * e.g.,{(1,32),(2,15),(3,47)},etc.
	 */
	private static HashMap<Integer, Double> itemFreq = new HashMap<Integer, Double>();

	/**
	 * content: keeps( peerId, {(itemId1,freq1),(itemId2,freq2),...} ) This
	 * variable keeps the contents of each peer, (will be filled in and returned
	 * at the end) e.g., ( 1,{(4,50),(5,60)} ), means that peer1 keeps item4
	 * with frequency 50 and item5 with frequency 60.
	 */
	private HashMap<Integer, Map<Integer, Double>> content = new HashMap<Integer, Map<Integer, Double>>();

	/**
	 * 
	 */
	private static HashMap<Integer, Map<Integer, Integer>> mapping = new HashMap<Integer, Map<Integer, Integer>>();
	/**
	 * Name of the item frequency distribution : Zipf or uniform
	 */
	private String itemFreqDist;

	/**
	 * Skewness parameter for Zipf distribution
	 */
	private double skewVal;

	/**
	 * Name of the distribution determining how items are distributed to peers
	 */
	private String distToPeers;

	/**
	 * Parameter for PowerLaw distribution
	 */
	private double alpha;

	/**
	 * Constructor
	 */
	public RandomContentFactoryED() {
		gen = new FileNameGenerator("initialStates", ".txt");
	}

	/**
	 * 
	 * @param numOfPeersToDistribute
	 *            to how many peers the content will be distributed
	 * @return the content of each peer
	 */
	public HashMap<Integer, Map<Integer, Double>> produceContent(
			int numberOfPeersToDistribute) {

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
		int counter[] = new int[numOfPeers];

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

		// /////This is used to estimate the network size/////////////
		Map<Integer, Double> hash = null;
		Map<Integer, Integer> hashing = new HashMap<Integer, Integer>();

		for (int i = 0; i < numOfPeers; i++) {
			hash = new HashMap<Integer, Double>();
			hashing = new HashMap<Integer, Integer>();
			hash.put(uniqueId, 0.0);
			hashing.put(counter[i]++, uniqueId);
			content.put(i, hash);
			mapping.put(i, hashing);
		}
		hash = new HashMap<Integer, Double>();
		hash.put(uniqueId, 1.0);
		content.put(CommonState.r.nextInt(numOfPeers), hash);

		// /////////////////////////////////////////////////////////////

		Set<Integer> set = itemFreq.keySet();
		Iterator<Integer> itr = set.iterator();

		// initializeContent(numberOfPeersToDistribute, numOfItems);
		while (itr.hasNext()) {
			int itemId = itr.next();
			freq = itemFreq.get(itemId);

			distributeItemsToPeers(numberOfPeersToDistribute, counter, itemId,
					freq);
		}

		return content;
	}

	private void distributeItemsToPeers(int numberOfPeers, int[] counter,
			int itemId, double freq) {
		Map<Integer, Double> hash;
		Map<Integer, Integer> hashing;
		int peerId = -1;
		// distribute each item one by one to a random peer
		// if an item already exists in that peer, just increment its frequency
		Map<Integer, Double> map;
		Map<Integer, Integer> mapp;
		double x0 = 1, x1 = numberOfPeers;
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
				peerId = CommonState.r.nextInt(numberOfPeers);
			// current content of the randomly chosen peer
			map = (HashMap<Integer, Double>) content.get(peerId);
			mapp = (HashMap<Integer, Integer>) mapping.get(peerId);

			// if peer has no item yet, add the item with id 'itemId' with a
			// frequency 1.
			if (map == null) {
				hash = new HashMap<Integer, Double>();
				hashing = new HashMap<Integer, Integer>();

				hash.put(itemId, 1.0);
				hashing.put(counter[peerId]++, itemId);
				content.put(peerId, hash);
				mapping.put(peerId, hashing);
			}
			// if the peer has that item, increment its frequency
			// else add an entry with frequncy 1.
			else {
				if (map.containsKey(itemId)) {
					map.put(itemId, map.get(itemId) + 1);
				} else {
					map.put(itemId, 1.0);
					mapp.put(counter[peerId]++, itemId);
				}
			}
		}

	}

	// @SuppressWarnings("unused")
	// private void makeAllPeersAware(int numOfPeers, int numOfItems) {
	// // TODO Auto-generated method stub
	// for (int i = 0; i <numOfPeers; i++) {
	// HashMap<Integer, Double> peerContent=(HashMap<Integer,
	// Double>)content.get(i);
	// for (int j=0; j<numOfItems;j++){
	// peerContent.put(j,0.0);
	// }
	// }
	//
	// }

	/**
	 * writes the result into a file called content.txt just to see what the
	 * random initialization looks like.
	 * 
	 * @param content
	 *            contents of peers
	 * @param itemFreq
	 *            global frequencies of all items
	 */
	public void write(HashMap<Integer, Map<Integer, Double>> content,
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
				writer.write("itemId\tFrequency\r\n");
				writer.write("-------------------\r\n");

				while (itr.hasNext()) {
					int next = itr.next();
					double freq = itemFreq.get(next);
					writer.write(next + "\t" + freq + "\r\n");
				}
				writer.write("\r\n");
				writer.write("peerId\tContent\r\n");
				writer.write("-------------------\r\n");
				while (iter.hasNext()) {
					int peer = iter.next();

					Map<Integer, Double> m = (HashMap<Integer, Double>) content
							.get(peer);
					Set<Integer> items = m.keySet();
					Iterator<Integer> itr2 = items.iterator();
					writer.write(Network.size() * Simulator.experimentNo + peer
							+ ":\t");

					while (itr2.hasNext()) {
						int id = itr2.next();
						double freq = m.get(id);
						writer.write("(" + id + "," + freq + ") ");
					}

					writer.write("\r\n");
				}

			} finally {
				writer.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// -----------------------------------------------------------------
	// GETTERS
	// -----------------------------------------------------------------
	public static HashMap<Integer, Double> getItemFreq() {
		return itemFreq;
	}

	public HashMap<Integer, Map<Integer, Double>> getContent() {
		return content;
	}

	public Map<Integer, Double> getContentOfPeer(long peerId) {
		return content.get(peerId);
	}

	public static HashMap<Integer, Map<Integer, Integer>> getMapping() {
		return mapping;
	}

}
