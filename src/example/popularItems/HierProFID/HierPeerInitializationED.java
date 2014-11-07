package example.popularItems.HierProFID;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import example.popularItems.PeerInitialization;
import example.popularItems.ProFIDNode;
import example.popularItems.ProFIDNode.NodeType;
import example.popularItems.ProFID.MultipleValueED;
import example.popularItems.ProFID.RandomContentFactoryED;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

public class HierPeerInitializationED extends PeerInitialization implements
		Control {

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------

	/**
	 * Creates a new instance and read parameters from the config file.
	 */
	public HierPeerInitializationED(String prefix) {
		super(prefix);
	}

	protected void initializePeerContent() {
		RandomContentFactoryED contentFactory = new RandomContentFactoryED();
		contentFactory.produceContent(Configuration.getInt("awarePeers",
				Configuration.getInt("SIZE")));
		HashMap<Integer, Map<Integer, Double>> content = contentFactory
				.getContent();
		if (writeContent)
			contentFactory.write(content, RandomContentFactoryED.getItemFreq());

		if (content == null) {
			System.err.println("PROBLEM IN CONTENT FACTORY!!!");
			System.exit(0);
		}

		CopyOnWriteArrayList<Integer> arr = DominatingSetFinder.getInstance(
				pid2).getDominatingSet();

		for (int i = 0; i < Network.size(); i++) {
			MultipleValueED prot = (MultipleValueED) Network.get(i)
					.getProtocol(pid);
			prot.setContent((HashMap<Integer, Double>) content.get(i));
		}

		DominatingSetFinder.getInstance(pid2).moveContentToDSPeers(pid);

		for (int i = 0; i < arr.size(); i++) {
			((ProFIDNode) Network.get(arr.get(i))).setType(NodeType.DSNODE);
		}
	}
}
