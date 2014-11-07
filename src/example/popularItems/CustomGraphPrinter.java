package example.popularItems;

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

import peersim.config.Configuration;
import peersim.graph.GraphIO;
import peersim.reports.GraphObserver;
import peersim.util.FileNameGenerator;

import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.IOException;

import example.popularItems.UserInputs;

import peersim.Simulator;

/**
 * Prints the whole graph in a given format.
 */
public class CustomGraphPrinter extends GraphObserver {

	// ===================== fields =======================================
	// ====================================================================

	/**
	 * This is the prefix of the filename where the graph is saved. The
	 * extension is ".graph" and after the prefix the basename contains a
	 * numeric index that is incremented at each saving point. If not given, the
	 * graph is dumped on the standard output.
	 * 
	 * @config
	 */
	private static final String PAR_BASENAME = "outf";

	/**
	 * The name for the format of the output. Defaults to "neighborlist", which
	 * is a plain dump of neighbors. The class
	 * {@link peersim.dynamics.WireFromFile} can read this format. Other
	 * supported formats are "chaco" to be used with Yehuda Koren's Embedder,
	 * "netmeter" to be used with Sergi Valverde's netmeter and also with pajek,
	 * "edgelist" that dumps one (directed) node pair in each line for each
	 * edge, "gml" that is a generic format of many graph tools, and "dot" that
	 * can be used with the graphviz package.
	 * 
	 * @see GraphIO#writeEdgeList
	 * @see GraphIO#writeChaco
	 * @see GraphIO#writeNeighborList
	 * @see GraphIO#writeNetmeter
	 * @config
	 */
	private static final String PAR_FORMAT = "format";

	private static final String PAR_EXT = "extension";

	private final String baseName;

	private final FileNameGenerator fng;

	private final String format;

	private final String extension;

	// ===================== initialization ================================
	// =====================================================================

	/**
	 * Standard constructor that reads the configuration parameters. Invoked by
	 * the simulation engine.
	 * 
	 * @param name
	 *            the configuration prefix for this class
	 */
	public CustomGraphPrinter(String name) {

		super(name);
		baseName = Configuration.getString(name + "." + PAR_BASENAME,
				"topology");
		format = Configuration.getString(name + "." + PAR_FORMAT,
				"neighborlist");
		extension = Configuration.getString(name + "." + PAR_EXT, ".txt");
		if (baseName != null)
			fng = new FileNameGenerator(baseName, extension);
		else
			fng = null;
		updateFileName(fng);
	}

	// ====================== methods ======================================
	// =====================================================================

	private void updateFileName(FileNameGenerator f) {
		for (int i = 0; i < Simulator.experimentNo; i++) {
			f.nextCounterName();
		}
	}

	/**
	 * Saves the graph according to the specifications in the configuration.
	 * 
	 * @return always false
	 */
	public boolean execute() {
		try {
			updateGraph();

			// initialize output streams
			PrintStream pstr = null;
			if (baseName != null) {
				String fname = fng.nextCounterName();

				pstr = new PrintStream(new FileOutputStream(
						UserInputs.DESTINATIONFOLDER + File.separator
								+ "experiment" + Simulator.experimentNo
								+ File.separator + fname));
			} else
				System.out.println();

			if (format.equals("neighborlist"))
				CustomGraphIO.writeNeighborList(g, pstr);
			else if (format.equals("edgelist"))
				CustomGraphIO.writeEdgeList(g, pstr);
			else if (format.equals("chaco"))
				CustomGraphIO.writeChaco(g, pstr);
			else if (format.equals("netmeter"))
				CustomGraphIO.writeNetmeter(g, pstr);
			else if (format.equals("gml"))
				CustomGraphIO.writeGML(g, pstr);
			else if (format.equals("dot"))
				CustomGraphIO.writeDOT(g, pstr);
			else
				System.err.println(name + ": unsupported format " + format);

			if (pstr != null)
				pstr.close();

			return false;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
