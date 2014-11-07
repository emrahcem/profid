package example.popularItems.HierProFID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import example.popularItems.ProFIDNode;
import example.popularItems.ProFID.MultipleValueED;

import peersim.core.Linkable;
import peersim.core.Network;

public class DominatingSetFinder {

	private static DominatingSetFinder finder;
	public static double span = 0;
	public static long dsSize = 0;
	public static double avgDSDegree = 0;
	private static CopyOnWriteArrayList<Integer> blackNodes;
	private CopyOnWriteArrayList<Integer> greyNodes;
	private CopyOnWriteArrayList<Integer> whiteNodes;
	private HashMap<Integer, ArrayList<Integer>> topLevel;
	private int pid2;

	public static DominatingSetFinder getInstance(int pid2) {
		if (finder == null)
			return finder = new DominatingSetFinder(pid2);
		else
			return finder;
	}

	private DominatingSetFinder(int pid2) {
		blackNodes = new CopyOnWriteArrayList<Integer>();
		greyNodes = new CopyOnWriteArrayList<Integer>();
		whiteNodes = new CopyOnWriteArrayList<Integer>();
		topLevel = new HashMap<Integer, ArrayList<Integer>>();
		this.pid2 = pid2;
	}

	public CopyOnWriteArrayList<Integer> getDominatingSet() {

		if (blackNodes.size() == 0) {
			updateNodes();
			createBlackNodes();
			dsSize = blackNodes.size();
			span = computeSpan();
			avgDSDegree=avgDegree();
			return blackNodes;
		} else
			return blackNodes;
	}

	private void updateGreyNodes() {
		greyNodes.clear();

		// Add new Grey Nodes to list.
		for (Integer i : blackNodes) {
			Linkable v = (Linkable) Network.get(i).getProtocol(pid2);
			for (int j = 0; j < v.degree(); j++) {

				if (!greyNodes.contains(v.getNeighbor(j).getIndex())) {
					greyNodes.add(v.getNeighbor(j).getIndex());
				}
			}
		}

		// Remove BlackNodes from greyList.
		for (Integer i : blackNodes) {
			if (greyNodes.contains(i)) {
				greyNodes.remove(i);
			}

		}
	}

	private void updateWhiteNodes() {
		whiteNodes.clear();
		for (int i = 0; i < Network.size(); i++) {
			if (blackNodes.contains(Network.get(i).getIndex()) == false
					&& greyNodes.contains(Network.get(i).getIndex()) == false) {
				whiteNodes.add(Network.get(i).getIndex());
			}
		}
	}

	private void updateNodes() {
		updateGreyNodes();
		updateWhiteNodes();
	}

	private void createBlackNodes() {
		do {
			for (int i = 0; i < Network.size(); i++) {
				Linkable v = (Linkable) Network.get(i).getProtocol(pid2);
				if (calculateSpan(v) > 0) {
					if (hasGreaterSpanNeighbourInTwoStep(v) == false) {
						blackNodes.add(Network.get(i).getIndex());
						updateNodes();
					} else {
						// Message
					}
				} else {
					// Message
				}
			}
		} while (whiteNodes.size() > 0);
	}

	private Boolean hasGreaterSpanNeighbourInTwoStep(Linkable v) {
		Boolean returnValue = false;
		Integer selfSpanValue = calculateSpan(v);

		// One hop
		for (int j = 0; j < v.degree(); j++) {
			Linkable u = (Linkable) v.getNeighbor(j).getProtocol(pid2);
			if (blackNodes.contains(v.getNeighbor(j).getIndex()) == false
					&& calculateSpan(u) > selfSpanValue) {
				returnValue = true;
				break;
			}
		}

		// Two hop
		for (int j = 0; j < v.degree(); j++) {
			Linkable u = (Linkable) v.getNeighbor(j).getProtocol(pid2);
			for (int k = 0; k < u.degree(); k++) {
				Linkable u2 = (Linkable) u.getNeighbor(k).getProtocol(pid2);
				if (blackNodes.contains(u.getNeighbor(k).getIndex()) == false
						&& calculateSpan(u2) > selfSpanValue) {
					returnValue = true;
					break;
				}
			}
		}
		return returnValue;
	}

	private Integer calculateSpan(Linkable v) {
		Integer returnValue = 0;
		for (int j = 0; j < v.degree(); j++) {
			if (whiteNodes.contains(v.getNeighbor(j).getIndex()) == true) {
				returnValue++;
			}
		}
		if (returnValue > 0) {
			returnValue += 1;
		}
		return returnValue;
	}

	
	private double avgDegree(){
		Integer index = 0;
		ArrayList<Integer> nonDomNeighbors = null;
		HashSet<Integer> twoHopNonDomNeighbors = null;
		Linkable v = null;
		HashSet<Integer> set = null;
		ArrayList<Integer> list = null;
		int currentPeerIndex = 0;
		double avg=0;
		System.out.println("Dom size:" + blackNodes.size());
		System.out.println(blackNodes);

		for (int counter = 0; counter < blackNodes.size(); counter++) {
			currentPeerIndex = (Integer) blackNodes.get(counter);
			System.out.print(currentPeerIndex + " ");
			v = (Linkable) Network.get(currentPeerIndex).getProtocol(pid2);
			nonDomNeighbors = new ArrayList<Integer>();
			twoHopNonDomNeighbors = new HashSet<Integer>();
			for (int i = 0; i < v.degree(); i++) {
				if (!blackNodes.contains(v.getNeighbor(i).getIndex())) {
					nonDomNeighbors.add(v.getNeighbor(i).getIndex());
					twoHopNonDomNeighbors.add(v.getNeighbor(i).getIndex());
				} else {
					if (topLevel.get(currentPeerIndex) == null) {
						set = new HashSet<Integer>();
						list = new ArrayList<Integer>();
						if (set.add(v.getNeighbor(i).getIndex())) {
							list.add(v.getNeighbor(i).getIndex());
							topLevel.put(currentPeerIndex, list);
						}
					} else {
						list = topLevel.get(currentPeerIndex);
						if (set.add(v.getNeighbor(i).getIndex()))
							list.add(v.getNeighbor(i).getIndex());
					}
				}
			}
			Linkable u = null;

			Object[] twoHopArr = twoHopNonDomNeighbors.toArray();
			for (int i = 0; i < twoHopArr.length; i++) {
				index = (Integer) twoHopArr[i];
				u = (Linkable) Network.get(index).getProtocol(pid2);

				for (int j = 0; j < u.degree(); j++) {
					if (currentPeerIndex != u.getNeighbor(j).getIndex()
							&& blackNodes.contains(u.getNeighbor(j).getIndex())) {
						if (topLevel.get(currentPeerIndex) == null) {
							set = new HashSet<Integer>();
							list = new ArrayList<Integer>();
							if (set.add(u.getNeighbor(j).getIndex())) {
								list.add(u.getNeighbor(j).getIndex());
								topLevel.put(currentPeerIndex, list);
							}
						} else {
							list = topLevel.get(currentPeerIndex);
							if (set.add(u.getNeighbor(j).getIndex())) {
								list.add(u.getNeighbor(j).getIndex());
							}
						}
					}
				}
			}
		}

		Set<Integer> s = topLevel.keySet();
		Iterator<Integer> itr = s.iterator();
		System.out.println(s.size()
				+ " peers have at least one DSpeer neighbor within 2 hops");

		while (itr.hasNext()) {
			int key = itr.next();
			ArrayList<Integer> topNeighs = topLevel.get(key);
			((ProFIDNode) Network.get(key)).setDomNeighs(topNeighs);

			avg += topLevel.get(key).size();
		}
		avg = avg / blackNodes.size();
		
		return avg;
	}
	
	public void moveContentToDSPeers(int pid) {
		System.out.println("Dom size:" + blackNodes.size());
		System.out.println(blackNodes);
		Linkable v;
		int currentPeerIndex;
		for (int counter = 0; counter < blackNodes.size(); counter++) {
			currentPeerIndex = (Integer) blackNodes.get(counter);
			v = (Linkable) Network.get(currentPeerIndex).getProtocol(pid2);
			ArrayList<Integer> topNeighs = topLevel.get(currentPeerIndex);
			((ProFIDNode) Network.get(currentPeerIndex))
					.setDomNeighs(topNeighs);
			HashMap<Integer, Double> myContent = ((MultipleValueED) Network
					.get(currentPeerIndex).getProtocol(pid)).getContent();
			for (int i = 0; i < v.degree(); i++) {
				if (!((ProFIDNode) v.getNeighbor(i)).isAlreadySharedLSI()
						&& !blackNodes.contains(v.getNeighbor(i).getIndex())) {
					HashMap<Integer, Double> neigCont = ((MultipleValueED) v
							.getNeighbor(i).getProtocol(pid)).getContent();
					((ProFIDNode) v.getNeighbor(i)).setAlreadySharedLSI(true);
					Set<Integer> neigItemIds = neigCont.keySet();
					Iterator<Integer> neigItr = neigItemIds.iterator();
					int id = -1;
					while (neigItr.hasNext()) {
						id = neigItr.next();
						if (myContent.containsKey(id)) {
							myContent.put(id,
									myContent.get(id) + neigCont.get(id));
						} else {
							myContent.put(id, neigCont.get(id));
						}
					}
				}
			}
		}
	}

	private double computeSpan() {
		Iterator<Integer> itr2 = blackNodes.iterator();
		Linkable linkable3;
		HashSet<Long> spanned= new HashSet<Long>();
		while (itr2.hasNext()) {
			int ind = (int) itr2.next();
			linkable3 = (Linkable) Network.get(ind).getProtocol(pid2);
			spanned.add(Network.get(ind).getID());
			for (int j = 0; j < linkable3.degree(); j++) {
				spanned.add(linkable3.getNeighbor(j).getID());
			}
		}
		return (double) spanned.size() / Network.size();
	}
}