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

package example.popularItems;

import peersim.graph.*;
import peersim.core.*;
import peersim.config.*;
import peersim.dynamics.WireGraph;

/**
 * Takes a {@link Linkable} protocol and adds random connections. 
 * @see GraphFactory#wireErdosRenyi
 */
public class WireErdosRenyi extends WireGraph {

	//--------------------------------------------------------------------------
	//Parameters
	//--------------------------------------------------------------------------

	/**
	 * @config
	 */
	private static final String PAR_DEGREE = "p";

	//--------------------------------------------------------------------------
	//Fields
	//--------------------------------------------------------------------------

	/**
	 * probability that any two nodes in the network is connected by a link 
	 */
	private final double p;

	//--------------------------------------------------------------------------
	//Initialization
	//--------------------------------------------------------------------------

	/**
	 * Standard constructor that reads the configuration parameters.
	 * Invoked by the simulation engine.
	 * @param prefix the configuration prefix for this class
	 */
	public WireErdosRenyi(String prefix)
	{
		super(prefix);
		p = Configuration.getDouble(prefix + "." + PAR_DEGREE,0.5);
	}

	//--------------------------------------------------------------------------
	//Methods
	//--------------------------------------------------------------------------

	/** Calls {@link GraphFactory#wireErdosRenyi}. */
	public void wire(Graph g) {

		CustomGraphFactory.wireErdosRenyi(g,p,CommonState.r);
	}

}
