package example.popularItems;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import affluenza.simulation.Simulation;

import example.popularItems.gui.content.AlgoParamsPanel;
import example.popularItems.gui.content.NetworkParamsPanel;

public class UserInputs {
	/**
	 * Number of experiments to perform in a single run
	 */
	public static String numOfExperiments;

	/**
	 * Network size
	 */
	public static String size = "";

	/**
	 * Type of the topology
	 */
	public static String topologyType = "";

	/**
	 * Parameter for Barabasi Albert topology
	 */
	public static String k = "";

	/**
	 * Parameter for Erdors-Renyi topology
	 */
	public static String p = "";

	/**
	 * 
	 */
	public static String beta = "";

	/**
	 * Number of items in the network
	 */
	public static String numOfItems = "";

	/**
	 * Distribution of global frequencies of items
	 */
	public static String itemFreqDist = "";

	/**
	 * Minimum global frequency
	 */
	public static String minFreq = "";

	/**
	 * Maximum global frequency
	 */
	public static String maxFreq = "";

	/**
	 * Minimum network delay between two nodes in the network
	 */
	public static String minDelay = "";

	/**
	 * Maximum network delay between two nodes in the network
	 */
	public static String maxDelay = "";

	/**
	 * Probability of message drop
	 */
	public static String drop = "";

	/**
	 * Skewness parameter of Zipf distribution
	 */
	public static String skew = "";

	/**
	 * Algorithm to be used in the experiment
	 */
	public static String algo = "";

	/**
	 * Convergence parameter
	 */
	public static String convLimit = "";

	/**
	 * Convergence parameter
	 */
	public static String epsilon = "";

	/**
	 * Number of neighbors gossip message is sent in a single round
	 */
	public static String fanout = "";

	/**
	 * Maximum message size in terms of <itemId,frequency> tuple
	 */
	public static String mms = "";

	/**
	 * Threshold used to determine frequent items
	 */
	public static String threshold = "";

	/**
	 * Margin around the threshold used in some of threshold mechanisms
	 */
	public static String delta = "";

	/**
	 * Distribution used to distribute items to peers
	 */
	public static String distToPeers = "";

	/**
	 * Parameter for PowerLaw distribution used in distributing items to peers
	 */
	public static String variate = "";

	/**
	 * Directory in which the results will be saved
	 */
	public static String DESTINATIONFOLDER = "";

	/**
	 * Trace file is written if enabled, can be used to analyze the algorithm.
	 * However, if network size is very large make sure to disable it
	 */
	public static boolean writeEnabled;

	/**
	 * Initial states of peers including the local item frequencies is written
	 * if enabled, as well as the global frequencies of items
	 */
	public static boolean writeInitState;

	/**
	 * Neighborlist of peers are written if enabled, it is basically the set of
	 * node ids. First id in a line is the node and the rest is its neighbors
	 * seperated by a space.
	 */
	public static boolean writeNeighborList;

	/**
	 * Churn file is used as input if enabled; otherwise, network will be static
	 */
	public static boolean includeChurn;

	public static String churnModel;

	public static File configFile;
	/**
	 * Runs directly from the config file if enabled, user dont have to enter
	 * parameters one by one using GUI.
	 */
	public static boolean runFromManualConfig = false;

	public static String saveTo = "";

	public enum InputStatus {
		OK, MISSINGFIELD, INVALIDMINMAX
	}

	/**
	 * Checks if all fields have been entered
	 * 
	 * @return positive if all fields are entered, negative otherwise.
	 */
	public static InputStatus allFieldsOK() {

		boolean filled = saveTo.equals("") || convLimit.equals("")
				|| epsilon.equals("") || fanout.equals("")
				|| itemFreqDist.equalsIgnoreCase("- Select -")
				|| distToPeers.equalsIgnoreCase("- Select -")
				|| topologyType.equalsIgnoreCase("- Select -")
				|| churnModel.equalsIgnoreCase("- Select -")
				|| threshold.equals("") || delta.equals("");
		if ((!algo.equals("PushSum") && (filled || mms.equals("")))
				|| (algo.equals("PushSum") && filled)) {
			return InputStatus.MISSINGFIELD;
		} else if (Integer.parseInt(minDelay) > Integer.parseInt(maxDelay)) {
			return InputStatus.INVALIDMINMAX;
		}
		return InputStatus.OK;

	}

	public static void createSimulationFiles() throws IOException {

		createSimFolder();
		createConfigFile();
		if (UserInputs.includeChurn) {
			createChurnFile();
		}
	}

	private static void createChurnFile() throws IOException {
		File f = new File(UserInputs.DESTINATIONFOLDER + File.separator
				+ "churn.avt");
		f.delete();
		f.createNewFile();
		Simulation.saveAVT(f);
	}

	private static void createConfigFile() throws IOException {
		String str = "";
		str += "SIZE " + size + "\n";
		str += "NUMBEROFEXPERIMENTS " + numOfExperiments + "\n";
		str += "NUMBEROFITEMTYPES " + numOfItems + "\n\n";
		str += "ALGORITHM " + algo + "\n\n";
		str += "ITEMFREQDIST " + itemFreqDist + "\n";
		if (itemFreqDist.equalsIgnoreCase("Zipf"))
			str += "SKEW " + skew + "\n";
		str += "\n";

		str += "DISTTOPEERS " + distToPeers + "\n";
		if (distToPeers.equalsIgnoreCase("PowerLaw"))
			str += "ALPHA " + variate + "\n";
		str += "\n";

		str += "MINIMUMFREQUENCY " + minFreq + "\n";
		str += "MAXIMUMFREQUENCY " + maxFreq + "\n\n";

		str += "CYCLES 600\n";
		str += "CYCLE 1000\n\n";

		str += "MINDELAY " + minDelay + "\n";
		str += "MAXDELAY " + maxDelay + "\n\n";

		str += "DROP " + String.valueOf((Double.parseDouble(drop) / 100))
				+ "\n\n";

		str += "TIMEOUT " + Integer.parseInt(maxDelay) * 3 + "\n\n";

		str += "CONVLIMIT " + convLimit + "\n";
		str += "EPSILON " + epsilon + "\n";
		str += "FANOUT " + fanout + "\n";
		str += "MMS " + mms + "\n";
		str += "THRESHOLD " + threshold + "\n";
		str += "DELTA " + delta + "\n\n";

		str += "simulation.experiments NUMBEROFEXPERIMENTS\n"
				+ "items.maximumFrequency MAXIMUMFREQUENCY\n"
				+ "items.minimumFrequency MINIMUMFREQUENCY\n"
				+ "random.seed 12345\n" + "network.size SIZE\n"
				+ "awarePeers SIZE\n"
				+ "network.numberOfItemTypes NUMBEROFITEMTYPES\n"
				+ "network.node example.popularItems.ProFIDNode\n"
				+ "simulation.endtime CYCLE*CYCLES\n"
				+ "simulation.logtime CYCLE\n\n";

		str += "################### protocols ###############################\n\n";

		str += "protocol.link peersim.core.IdleProtocol\n\n";
		if (UserInputs.algo.equalsIgnoreCase("Hierarchical ProFID"))
			str += "protocol.gossip example.popularItems.HierProFID.HierGossipED\n{\n";
		else if (UserInputs.algo.equalsIgnoreCase("PushSum")) {
			str += "protocol.gossip example.popularItems.Kempe.GossipKempe\n{\n";
		} else
			str += "protocol.gossip example.popularItems.ProFID.GossipED\n{\n";

		str += "\tlinkable link\n\tstep CYCLE\n\tconvLimit CONVLIMIT\n\tepsilon EPSILON\n\tfanout FANOUT\n";
		if (!UserInputs.algo.equalsIgnoreCase("PushSum"))
			str += "\tmms MMS\n";
		str += "\tthreshold THRESHOLD\n\tdelta DELTA\n\ttimeout TIMEOUT\n";
		str += "\ttransport tr\n\t";
		if (!writeEnabled)
			str += "#";
		str += "writeEnabled\n\t";
		if (UserInputs.algo.equalsIgnoreCase("PushSum"))
			str += "incrementEachRound\n\t";
		if (UserInputs.algo.equalsIgnoreCase("Adaptive ProFID"))
			str += "adaptive\n";
		str += "}\n\n";

		str += "protocol.urt peersim.transport.UniformRandomTransport\n{\n";
		str += "\tmindelay MINDELAY\n\tmaxdelay MAXDELAY\n}\n\n";

		str += "protocol.tr peersim.transport.UnreliableTransport\n{\n";
		str += "\ttransport urt\n\tdrop DROP\n}\n\n";

		str += "################### initialization ########################\n\n";
		str += "init.topo example.popularItems.TopologyInitialization\n{}\n\n";
		str += "init.fromFile peersim.dynamics.WireFromFile\n{\n";
		str += "\tfile " + UserInputs.DESTINATIONFOLDER + File.separator
				+ "neighborList.txt" + "\n\tundir\n\tprotocol link\n}\n\n";
		// str+="\tfile NetworkFiles\\powerLaw\\k=5\\"+size+"\\neighborList.txt\n\tundir\n\tprotocol link\n}\n\n";

		if (UserInputs.algo.equalsIgnoreCase("Hierarchical ProFID"))
			str += "init.peers example.popularItems.HierProFID.HierPeerInitializationED\n{\n";
		else if (UserInputs.algo.equalsIgnoreCase("PushSum"))
			str += "init.peers example.popularItems.Kempe.PeerInitializationKempe\n{\n";
		else
			str += "init.peers example.popularItems.ProFID.PeerInitializationED\n{\n";

		str += "\tprotocol gossip\n\tprotocol2 link\n\titemDistribution ITEMFREQDIST\n";
		if (itemFreqDist.equalsIgnoreCase("Zipf"))
			str += "\tskew SKEW\n";
		str += "\tdistributionToPeers DISTTOPEERS\n";
		if (distToPeers.equalsIgnoreCase("PowerLaw"))
			str += "\talpha ALPHA\n";
		str += "\t";
		if (!writeInitState)
			str += "#";
		str += "writeContent\n";
		if (includeChurn)
			// str += "\tchurnFile heterYao" + size + ".avt\n";
			str += "\tchurnFile " + UserInputs.DESTINATIONFOLDER
					+ File.separator + "churn.avt" + "\n";
		str += "}\n\n";

		str += "init.neighbors example.popularItems.CustomGraphPrinter\n{\n";
		str += "\toutf neighborList\n\tformat neighborlist\n\tundir\n\tprotocol link\n\textension .txt\n}\n\n";

		 str += "init.visualize example.popularItems.CustomGraphPrinter\n{\n";
		 str +=
		 "\toutf graph\n\tformat dot\n\tundir\n\tprotocol link\n\textension .dot\n}\n\n";

		str += "init.sch peersim.edsim.CDScheduler\n{\n";
		str += "\tprotocol gossip\n\trandstart\n}\n\n";

		str += "include.init topo fromFile visualize sch ";
		if (writeNeighborList)
			str += "neighbors ";
		str += "peers\n\n";

		str += "################ control #########################\n\n";

		if (UserInputs.algo.equalsIgnoreCase("Hierarchical ProFID"))
			str += "control.observer example.popularItems.HierProFID.HierObserverED\n{\n";
		else if (UserInputs.algo.equalsIgnoreCase("PushSum"))
			str += "control.observer example.popularItems.Kempe.IncomingObserverKempe\n{\n";
		else
			str += "control.observer example.popularItems.ProFID.ObserverED\n{\n";

		str += "\tprotocol gossip\n\tfrom CYCLE-1\n\tstep CYCLE\n\tFINAL\n}\n\n";

		if (UserInputs.algo.equalsIgnoreCase("PushSum"))
			str += "control.converged example.popularItems.Kempe.ConvergedNodesObserverKempe\n{\n";
		else
			str += "control.converged example.popularItems.ProFID.ConvergedNodesObserverED\n{\n";
		str += "\tprotocol gossip\n\tfrom CYCLE-1\n\tstep CYCLE\n\tFINAL\n}";

		FileWriter fw;
		File f = new File(DESTINATIONFOLDER + File.separator + "configFile.txt");
		f.delete();
		f.createNewFile();
		fw = new FileWriter(f);
		fw.write(str);
		fw.close();

	}

	public static void createSimFolder() throws IOException {
		int counter = 1;
		boolean exists = false;

		DESTINATIONFOLDER = saveTo + File.separator;
		if (!UserInputs.runFromManualConfig) {

			DESTINATIONFOLDER += algo;
			if (includeChurn)
				DESTINATIONFOLDER += "-withChurn";
			DESTINATIONFOLDER += "_p" + size + "i" + numOfItems + "min"
					+ minFreq + "max" + maxFreq + "c" + convLimit + "e"
					+ epsilon + "d" + drop + "min" + minDelay + "max"
					+ maxDelay + "f" + fanout + "t" + threshold + "delta"
					+ delta;
		} else {
			if (peersim.config.Configuration.contains("ALGORITHM"))
				DESTINATIONFOLDER += peersim.config.Configuration
						.getString("ALGORITHM");
			if (peersim.config.Configuration.contains("churnFile"))
				DESTINATIONFOLDER += "-withChurn";
			DESTINATIONFOLDER += "_p";
			DESTINATIONFOLDER += peersim.config.Configuration.getString("SIZE");
			DESTINATIONFOLDER += "i";
			DESTINATIONFOLDER += peersim.config.Configuration
					.getString("NUMBEROFITEMTYPES");
			DESTINATIONFOLDER += "min";
			DESTINATIONFOLDER += peersim.config.Configuration
					.getString("MINIMUMFREQUENCY");
			DESTINATIONFOLDER += "max";
			DESTINATIONFOLDER += peersim.config.Configuration
					.getString("MAXIMUMFREQUENCY");
			DESTINATIONFOLDER += "c";
			DESTINATIONFOLDER += peersim.config.Configuration
					.getString("CONVLIMIT");
			DESTINATIONFOLDER += "e";
			DESTINATIONFOLDER += peersim.config.Configuration
					.getString("EPSILON");
			DESTINATIONFOLDER += "d";
			DESTINATIONFOLDER += peersim.config.Configuration.getString("DROP");
			DESTINATIONFOLDER += "min";
			DESTINATIONFOLDER += peersim.config.Configuration
					.getString("MINDELAY");
			DESTINATIONFOLDER += "max";
			DESTINATIONFOLDER += peersim.config.Configuration
					.getString("MINDELAY");
			DESTINATIONFOLDER += "f";
			DESTINATIONFOLDER += peersim.config.Configuration
					.getString("FANOUT");
			DESTINATIONFOLDER += "t";
			DESTINATIONFOLDER += peersim.config.Configuration
					.getString("THRESHOLD");
			DESTINATIONFOLDER += "delta";
			DESTINATIONFOLDER += peersim.config.Configuration
					.getString("DELTA");
		}

		while ((new File(DESTINATIONFOLDER + "v" + counter).exists())) {
			counter++;
			exists = true;
		}

		if (exists)
			System.err.print("It exists!! Creating version" + counter + "?");

		DESTINATIONFOLDER += "v" + counter;

		new File(DESTINATIONFOLDER).mkdir();
		File f = new File(DESTINATIONFOLDER);
		System.err.println("Created " + f.getAbsoluteFile());
	}

	public static void save() {
		AlgoParamsPanel algoPanel = AlgoParamsPanel.getInstance();
		NetworkParamsPanel netPanel = NetworkParamsPanel.getInstance();

		convLimit = algoPanel.getTxtConvLimit().getText();
		epsilon = algoPanel.getTxtEpsilon().getText();
		fanout = algoPanel.getTxtFanout().getText();
		mms = algoPanel.getTxtMms().getText();
		threshold = algoPanel.getTxtThreshold().getText();
		delta = algoPanel.getTxtDelta().getText();
		numOfExperiments = netPanel.getTxtNumOfExp().getText();
		size = netPanel.getTxtSize().getText();
		numOfItems = netPanel.getTxtNumOfItems().getText();
		itemFreqDist = (String) netPanel.getCmbItemFreqDist().getSelectedItem();
		if (itemFreqDist.equalsIgnoreCase("Uniform")) {
			minFreq = netPanel.getTxtMinFreq().getText();
			maxFreq = netPanel.getTxtMaxFreq().getText();
		} else if (itemFreqDist.equalsIgnoreCase("Zipf")) {
			minFreq = netPanel.getTxtMinFreq1().getText();
			maxFreq = netPanel.getTxtMaxFreq1().getText();
			skew = netPanel.getTxtSkew().getText();
		}

		distToPeers = (String) netPanel.getCmbDistToPeers().getSelectedItem();
		if (distToPeers.equalsIgnoreCase("PowerLaw")) {
			variate = netPanel.getTxtVariate().getText();
		}
		drop = netPanel.getTxtDrop().getText();
		minDelay = netPanel.getTxtMinDelay().getText();
		maxDelay = netPanel.getTxtMaxDelay().getText();
		algo = (String) algoPanel.getCmbAlgorithm().getSelectedItem();
		topologyType = (String) netPanel.getCmbTopology().getSelectedItem();
		if (topologyType.equalsIgnoreCase("WireKOut")) {
			k = netPanel.getTxtWireKOut().getText();

		} else if (topologyType.equalsIgnoreCase("Erdos-Renyi")) {
			p = netPanel.getTxtErdos().getText();
		} else if (topologyType.equalsIgnoreCase("Barabasi-Albert")) {
			k = netPanel.getTxtBarabasi().getText();
		} else if (topologyType.equalsIgnoreCase("Watts&Strogatz")) {
			beta = netPanel.getTxtWattsBeta().getText();
			k = netPanel.getTxtWattsK().getText();
		}

		writeEnabled = algoPanel.getChkCreateTraceFile().isSelected();
		writeInitState = algoPanel.getChkInitState().isSelected();
		writeNeighborList = algoPanel.getChkNeighborList().isSelected();
		includeChurn = netPanel.getCmbChurn().getSelectedIndex() > 1;
		churnModel = (String) netPanel.getCmbChurn().getSelectedItem()
				.toString();
	}

}
