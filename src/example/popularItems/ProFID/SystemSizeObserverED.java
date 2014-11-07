package example.popularItems.ProFID;

/*
 * Copyright (c) 2003-2005 The BISON Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import peersim.Simulator;


import peersim.config.*;
import peersim.core.*;
import peersim.util.IncrementalStats;

/**
 * Prints the system size estimations of a sample of nodes.
 * 
 * @author Emrah Cem
 *
 */
public class SystemSizeObserverED implements Control {

	// /////////////////////////////////////////////////////////////////////
	// Constants
	// /////////////////////////////////////////////////////////////////////


	/**
	 * The protocol to operate on.
	 * 
	 * @config
	 */
	private static final String PAR_PROT = "protocol";

	/**
	 * Size of the sample whose network size estimations are printed
	 */
	private static final String PAR_OBSERVED = "observedNodes";

	// /////////////////////////////////////////////////////////////////////
	// Fields
	// /////////////////////////////////////////////////////////////////////

	/**
	 * The name of this observer in the configuration. Initialized by the
	 * constructor parameter.
	 */
	private final String name;


	/** Protocol identifier; obtained from config property {@link #PAR_PROT}. */
	private final int pid;

	private final int numOfObservedNodes;

	private HashMap<Long, LinkedList<Double>> observedIds;

	// /////////////////////////////////////////////////////////////////////
	// Constructor
	// /////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new observer reading configuration parameters.
	 */
	public SystemSizeObserverED(String n) {
		System.err.println("Observer::constr(String) starts");
		name = n;
		pid = Configuration.getPid(name + "." + PAR_PROT);
		numOfObservedNodes=Configuration.getInt(name + "." + PAR_OBSERVED);
		observedIds =new HashMap<Long, LinkedList<Double>>();
		initialize(observedIds);
	}

	private void initialize(HashMap<Long, LinkedList<Double>> observedIds){

		for (int i = 0; i < numOfObservedNodes; i++) {
			observedIds.put((long)Network.size()*Simulator.experimentNo+i, new LinkedList<Double>());
		}
		
	}
	// /////////////////////////////////////////////////////////////////////
	// Methods
	// /////////////////////////////////////////////////////////////////////

	public boolean execute() {
		IncrementalStats is = new IncrementalStats();
		for (int i = 0; i < Network.size(); i++) {

			MultipleValueED protocol = (MultipleValueED) Network.get(i)
			.getProtocol(pid);
			HashMap<Integer, Double> content=protocol.getContent();
			
			if(observedIds.containsKey(Network.get(i).getID())){
				observedIds.get(Network.get(i).getID()).add(content.get(-1));
			}
			is.reset();
		}

		try {
			FileWriter writer= new FileWriter(new File("systemSize"));

			if(CommonState.getPhase()==CommonState.POST_SIMULATION){

				Set<Long> ids= observedIds.keySet(); 
				Iterator<Long> iter= ids.iterator();

				while(iter.hasNext()){
					long id=iter.next();
					LinkedList<Double> nextList=observedIds.get(id);
					writer.write(nextList.toString().substring(1,nextList.toString().length()-1 )+"\r\n");

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

}
