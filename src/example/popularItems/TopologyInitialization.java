package example.popularItems;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import peersim.Simulator;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.OverlayGraph;
import peersim.graph.Graph;
import peersim.graph.GraphFactory;

public class TopologyInitialization implements Control{

	/**
	 * The number of items (how many different items are there?).
	 * 
	 * @config
	 */
	private static final String PAR_NUMOFITEMTYPE = "network.numberOfItemTypes";
	
	public TopologyInitialization(String prefix) {
		
	}
	@Override
	public boolean execute() {
		ProFIDState.numberOfConvergedNodes=0;
		ProFIDState.numOfItemTypes=Configuration.getInt(PAR_NUMOFITEMTYPE);
		
		if(Simulator.experimentNo ==0 && !UserInputs.runFromManualConfig){

			Graph g=new OverlayGraph(1,false);
			//TOPOLOGY TYPE IS NOT GIVEN IN CONFIG FILE, INCLUDE IT IN CONFIG FILE
			//Topology is created here depending on the user selection 
			if(UserInputs.topologyType.equalsIgnoreCase("WireKOut")){
				GraphFactory.wireKOut(g, Integer.parseInt(UserInputs.k), CommonState.r);
			}
			else if(UserInputs.topologyType.equalsIgnoreCase("Erdos-Renyi")){
				CustomGraphFactory.wireErdosRenyi(g, Double.parseDouble(UserInputs.p), CommonState.r);
			}
			else if(UserInputs.topologyType.equalsIgnoreCase("Barabasi-Albert")){
				GraphFactory.wireScaleFreeBA(g, Integer.parseInt(UserInputs.k), CommonState.r);
			}
			else if(UserInputs.topologyType.equalsIgnoreCase("Watts&Strogatz")){
				GraphFactory.wireWS(g, Integer.parseInt(UserInputs.k), Double.parseDouble(UserInputs.beta), CommonState.r);
			}
				try {
					//Then resulting graph is written into neighborList.txt file so that same file will be used in each experiment as the input topology
						CustomGraphIO.writeNeighborList(g, new PrintStream(new FileOutputStream(UserInputs.DESTINATIONFOLDER+File.separator+"neighborList.txt")));
					//	CustomGraphIO.writeNeighborList(g, new PrintStream(new FileOutputStream("neighborList.txt")));
				} catch (FileNotFoundException e1) {
				}

		}
		return false;
	}

}
