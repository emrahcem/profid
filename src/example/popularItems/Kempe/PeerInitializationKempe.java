package example.popularItems.Kempe;

import java.util.HashMap;
import java.util.Map;

import example.popularItems.PeerInitialization;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

public class PeerInitializationKempe extends PeerInitialization implements
		Control {

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------

	/**
	 * Creates a new instance and read parameters from the config file.
	 */
	public PeerInitializationKempe(String prefix) {
		super(prefix);
	}

	protected void initializePeerContent() {
		RandomContentFactoryKempe contentFactory = new RandomContentFactoryKempe();
		contentFactory.produceContent(Configuration.getInt("awarePeers",
				Configuration.getInt("SIZE")));
		HashMap<Integer, Map<Integer, ItemState>> content = contentFactory
				.getContent();
		if (writeContent)
			contentFactory.write(content,RandomContentFactoryKempe.getItemFreq());

		if (content == null) {
			System.err.println("PROBLEM IN CONTENT FACTORY!!!");
			System.exit(0);
		}

		for (int i = 0; i < Network.size(); i++) {
			MultipleValueHolderKempe prot = (MultipleValueHolderKempe) Network
					.get(i).getProtocol(pid);

			if ((HashMap<Integer, ItemState>) content.get(i) != null) {
				prot.setContent((HashMap<Integer, ItemState>) content.get(i));
				prot.setprevIncomingItems(((HashMap<Integer, ItemState>) contentFactory
						.getDeepCopyOfInitContent(i)));
			}
		}
	}
}
