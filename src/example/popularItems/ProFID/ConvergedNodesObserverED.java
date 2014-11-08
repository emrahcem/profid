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
import java.util.LinkedList;

import example.popularItems.UserInputs;
import peersim.Simulator;
import peersim.config.*;
import peersim.core.*;
import peersim.util.FileNameGenerator;

/**
 * 
 * @author Emrah Cem
 *
 */
public class ConvergedNodesObserverED implements Control {

	// /////////////////////////////////////////////////////////////////////
	// Constants
	// /////////////////////////////////////////////////////////////////////

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


	/** Protocol identifier; obtained from config property {@link #PAR_PROT}. */
	private final int pid;

	private LinkedList<Integer> numOfConvergedNodesList;
	// /////////////////////////////////////////////////////////////////////
	// Constructor
	// /////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new observer reading configuration parameters.
	 */
	public ConvergedNodesObserverED(String n) {

		name = n;
		pid = Configuration.getPid(name + "." + PAR_PROT);
		numOfConvergedNodesList =new LinkedList<Integer>();

	}

	// /////////////////////////////////////////////////////////////////////
	// Methods
	// /////////////////////////////////////////////////////////////////////

	
	public boolean execute() {
		int numOfConvergedNodes=0;
		for (int i = 0; i < Network.size(); i++) {

			MultipleValueED protocol = (MultipleValueED) Network.get(i)
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
					writer= new FileWriter(new File(UserInputs.DESTINATIONFOLDER+"/experiment"+Simulator.experimentNo+"/"+gen.nextCounterName()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//omit the brackets at the beginning and the endand write the result to the file
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
