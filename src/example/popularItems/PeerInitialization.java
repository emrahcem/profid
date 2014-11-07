package example.popularItems;

import java.io.IOException;

import javax.swing.JOptionPane;

import peersim.config.Configuration;
import peersim.core.Control;

public abstract class PeerInitialization implements Control {

	/**
	 * The protocol to operate on.
	 * 
	 * @config
	 */
	private static final String PAR_PROT = "protocol";
	private static final String PAR_PROT2 = "protocol2";
	private static final String PAR_WRITECONTENT = "writeContent";
	private static final String PAR_ITEMDIST = "itemDistribution";
	private static final String PAR_SKEW = "skew";
	private static final String PAR_DISTTOPEERS = "distributionToPeers";
	private static final String PAR_ALPHA = "alpha";
	private static final String PAR_CHURN = "churnFile";

	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------

	/** Protocol identifier; obtained from config property {@link #PAR_PROT}. */
	protected final int pid;

	protected final int pid2;

	protected final String itemDist;

	protected double skew;

	protected final String distToPeers;

	protected double alpha;

	public static boolean writeContent;

	public static String churnFile;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------

	/**
	 * Creates a new instance and read parameters from the config file.
	 */
	public PeerInitialization(String prefix) {

		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		pid2 = Configuration.getPid(prefix + "." + PAR_PROT2);
		itemDist = Configuration.getString(prefix + "." + PAR_ITEMDIST);
		if (itemDist.equalsIgnoreCase("Zipf"))
			skew = Configuration.getDouble(prefix + "." + PAR_SKEW);
		writeContent = Configuration.contains(prefix + "." + PAR_WRITECONTENT);
		distToPeers = Configuration.getString(prefix + "." + PAR_DISTTOPEERS);
		if (distToPeers.equalsIgnoreCase("PowerLaw"))
			alpha = Configuration.getDouble(prefix + "." + PAR_ALPHA);
		if (Configuration.contains(prefix + "." + PAR_CHURN))
			churnFile = Configuration.getString(prefix + "." + PAR_CHURN);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	@Override
	public boolean execute() {
		
		if (churnFile != null) {
			readChurnFile();
		}
		initializePeerContent();
		return false;
	}

	protected void readChurnFile() {
		try {
			ChurnFileReader.read(churnFile);
		} catch (NumberFormatException e) {
			JOptionPane.showConfirmDialog(null, "Churn file is currupted.",
					"Number Format Exception", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(null,
					"Churn file could not be read.", "IO Exception",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	protected abstract void initializePeerContent();
}
