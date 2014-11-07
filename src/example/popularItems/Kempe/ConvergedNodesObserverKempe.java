package example.popularItems.Kempe;

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
import java.util.LinkedList;

import example.popularItems.UserInputs;


import peersim.Simulator;
import peersim.config.*;
import peersim.core.*;
import peersim.util.FileNameGenerator;
import peersim.util.IncrementalStats;

/**
 *
 * 
 * @author Emrah Cem
 * 
 */
public class ConvergedNodesObserverKempe implements Control {

	// /////////////////////////////////////////////////////////////////////
	// Constants
	// /////////////////////////////////////////////////////////////////////

	/**
	 * Config parameter that determines the accuracy for standard deviation
	 * before stopping the simulation. If not defined, a negative value is used
	 * which makes sure the observer does not stop the simulation
	 * 
	 * @config
	 */
	//private static final String PAR_ACCURACY = "accuracy";

	/**
	 * The protocol to operate on.
	 * 
	 * @config
	 */
	private static final String PAR_PROT = "protocol";


	// /////////////////////////////////////////////////////////////////////
	// Fields
	// /////////////////////////////////////////////////////////////////////

	/**
	 * The name of this observer in the configuration. Initialized by the
	 * constructor parameter.
	 */
	private final String name;

	/**
	 * Accuracy for standard deviation used to stop the simulation; obtained
	 * from config property {@link #PAR_ACCURACY}.
	 */
	//private final double accuracy;

	/** Protocol identifier; obtained from config property {@link #PAR_PROT}. */
	private final int pid;

	private LinkedList<Integer> numOfConvergedNodesList;
	// /////////////////////////////////////////////////////////////////////
	// Constructor
	// /////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new observer reading configuration parameters.
	 */
	public ConvergedNodesObserverKempe(String n) {
		
		name = n;
		pid = Configuration.getPid(name + "." + PAR_PROT);
	
		numOfConvergedNodesList =new LinkedList<Integer>();
		
	
	}

	// /////////////////////////////////////////////////////////////////////
	// Methods
	// /////////////////////////////////////////////////////////////////////

	/**
	 * Print statistics for an average aggregation computation. Statistics
	 * printed are defined by {@link IncrementalStats#toString}. The current
	 * timestamp is also printed as a first field.
	 * 
	 * @return if the standard deviation is less than the given
	 *         {@value #PAR_ACCURACY}.
	 */
	public boolean execute() {
		//long time = peersim.core.CommonState.getTime();
		int numOfConvergedNodes=0;
		for (int i = 0; i < Network.size(); i++) {

			MultipleValueHolderKempe protocol = (MultipleValueHolderKempe) Network.get(i)
			.getProtocol(pid);
			
			if(protocol.isConverged())
			{
				numOfConvergedNodes++;
			}
	
		}
		numOfConvergedNodesList.add(numOfConvergedNodes);
		try {
			if(CommonState.getPhase()==CommonState.POST_SIMULATION){

				FileNameGenerator gen=new FileNameGenerator("convergedNodes",".txt");
				FileWriter writer=null;
				
				for (int i = 0; i < Simulator.experimentNo; i++) {
					gen.nextCounterName();
				}
				
				try {
					writer= new FileWriter(new File(UserInputs.DESTINATIONFOLDER+File.separator+"experiment"+Simulator.experimentNo+File.separator+gen.nextCounterName()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
				writer.write(numOfConvergedNodesList.toString().substring(1,numOfConvergedNodesList.toString().length()-1 ));
				writer.close();	
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

}
