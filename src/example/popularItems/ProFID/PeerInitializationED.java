package example.popularItems.ProFID;

import java.util.HashMap;
import java.util.Map;

import example.popularItems.PeerInitialization;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

public class PeerInitializationED extends PeerInitialization implements Control {

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------

	/**
	 * Creates a new instance and read parameters from the config file.
	 */
	public PeerInitializationED(String prefix) {
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

		for (int i = 0; i < Network.size(); i++) {
			MultipleValueED prot = (MultipleValueED) Network.get(i)
					.getProtocol(pid);
			prot.setContent((HashMap<Integer, Double>) content.get(i));
		}
	}
}
