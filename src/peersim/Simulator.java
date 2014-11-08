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

package peersim;

import java.io.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import example.popularItems.UserInputs;
import example.popularItems.UserInputs.InputStatus;
import example.popularItems.exceptions.InvalidInputException;
import example.popularItems.exceptions.MinMaxException;
import example.popularItems.exceptions.ProFIDException;
import example.popularItems.gui.SaveResultsPanel;
import example.popularItems.gui.content.ContentPanel;
//import example.popularItems.gui.content.ContentPanel;

import peersim.cdsim.*;
import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.util.FileNameGenerator;

/**
 * This is the main entry point to peersim. This class loads configuration and
 * detects the simulation type. According to this, it invokes the appropriate
 * simulator. The known simulators at this moment, along with the way to detect
 * them are the following:
 * <ul>
 * <li>{@link CDSimulator}: if {@link CDSimulator#isConfigurationCycleDriven}
 * returns true</li>
 * <li>{@link EDSimulator2}: if {@link EDSimulator2#isConfigurationEventDriven}
 * returns true</li>
 * </ul>
 * This list represents the order in which these alternatives are checked. That
 * is, if more than one return true, then the first will be taken. Note that
 * this class checks only for these clues and does not check if the
 * configuration is consistent or valid.
 * 
 * @see #main
 */
public class Simulator implements Runnable {

	// ========================== static constants ==========================
	// ======================================================================

	public static int experimentNo = -1;

	/** {@link CDSimulator} */
	public static final int CDSIM = 0;

	/** {@link EDSimulator2} */
	public static final int EDSIM = 1;

	/** Unknown simulator */
	public static final int UNKNOWN = -1;

	/** the class names of simulators used */
	protected static final String[] simName = { "peersim.cdsim.CDSimulator",
			"peersim.edsim.EDSimulator", };

	/**
	 * Parameter representing the number of times the experiment is run.
	 * Defaults to 1.
	 * 
	 * @config
	 */
	public static final String PAR_EXPS = "simulation.experiments";

	/**
	 * If present, this parameter activates the redirection of the standard
	 * output to a given PrintStream. This comes useful for processing the
	 * output of the simulation from within the simulator.
	 * 
	 * @config
	 */
	public static final String PAR_REDIRECT = "simulation.stdout";

	/**
	 * The maximum frequency an item can have.
	 * 
	 * @config
	 */
	private static final String PAR_MAXFREQ = "items.maximumFrequency";

	/**
	 * The minimum frequency an item can have.
	 * 
	 * @config
	 */
	private static final String PAR_MINFREQ = "items.minimumFrequency";

	/** Maximum frequency of an item */
	protected static int maximumFrequency;

	/** Minimum frequency of an item */
	protected static int minimumFrequency;

	/** Writes transactions to the file */
	public static FileWriter gossipWriter;

	// ==================== static fields ===================================
	// ======================================================================

	/** */
	private static int simID = UNKNOWN;

	public static long startTime;



	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * Returns the numeric id of the simulator to invoke. At the moment this can
	 * be {@link #CDSIM}, {@link #EDSIM} or {@link #UNKNOWN}.
	 */
	public static int getSimID() {

		if (simID == UNKNOWN) {
			if (CDSimulator.isConfigurationCycleDriven()) {
				simID = CDSIM;
			} else if (EDSimulator.isConfigurationEventDriven()) {
				simID = EDSIM;
			}
		}
		return simID;
	}

	// ----------------------------------------------------------------------


	public static void runSimulation() throws RuntimeException, IOException,
			ProFIDException {
		
		UserInputs.saveTo = SaveResultsPanel.getInstance().getTxtSaveTo().getText();
		startTime = System.currentTimeMillis();

		if (!UserInputs.runFromManualConfig){
			UserInputs.save();	
			if (UserInputs.allFieldsOK() == InputStatus.OK) {
				UserInputs.createSimulationFiles();
			} else if (UserInputs.allFieldsOK() == InputStatus.MISSINGFIELD) {
				throw new InvalidInputException(
						"Please fill in all fields correctly");

			} else if (UserInputs.allFieldsOK() == InputStatus.INVALIDMINMAX) {
				throw new MinMaxException(
						"Minimum delay must be less than maximum delay");
			}

			UserInputs.configFile = new File(UserInputs.DESTINATIONFOLDER + File.separator
					+ "configFile.txt");
			Configuration.setConfig(new ParsedProperties(UserInputs.configFile.getAbsolutePath()));
		}
		else if (UserInputs.runFromManualConfig) {
			UserInputs.createSimFolder();
			if (Configuration.contains("init.fromFile")) {
				copyfile(Configuration.getString("init.fromFile.file"),
						UserInputs.DESTINATIONFOLDER + File.separator
								+ "neighborList.txt");
				//System.err.println("Copy from:" + Configuration.getString("init.fromFile.file")
				//		+ "  to:" + UserInputs.DESTINATIONFOLDER
				//		+ File.separator + "neighborList.txt");
			}
		}

		Thread th = new Thread(new Simulator());
		th.start();
	}

	@Override
	public void run() {

		/**
		 * Get the maximum frequency from the configuration file, if it does not
		 * exist, it is set to 100
		 */
		maximumFrequency = Configuration.getInt(PAR_MAXFREQ, 100);

		/**
		 * Get the minimum frequency from the configuration file, if it does not
		 * exist, it is set to 0
		 */
		minimumFrequency = Configuration.getInt(PAR_MINFREQ, 0);

		FileNameGenerator fileNames = new FileNameGenerator("output", ".txt");

		PrintStream outp = null;
		PrintStream newout = null;

		int exps = Configuration.getInt(PAR_EXPS, 1);

		final int SIMID = getSimID();
		if (SIMID == UNKNOWN) {
			ContentPanel.console
					.append("Simulator::unable to identify configuration, exiting.");
			return;
		}

		try {
			for (int k = 0; k < exps; ++k) {
				experimentNo = k;
				if (k > 0) {
					long seed = CommonState.r.nextLong();
					CommonState.initializeRandom(seed);
				}
				new File(UserInputs.DESTINATIONFOLDER + File.separator
						+ "experiment" + k).mkdir();

				try {
					String fileName = fileNames.nextCounterName();
					outp = new PrintStream(new File(
							UserInputs.DESTINATIONFOLDER + File.separator
									+ "experiment" + k + File.separator
									+ fileName));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}

				newout = (PrintStream) Configuration.getInstance(PAR_REDIRECT,
						outp);
				if (newout != System.out) {
					System.setOut(newout);
				}

				// XXX could be done through reflection, but
				// this is easier to read.
				switch (SIMID) {
				case CDSIM:
					CDSimulator.nextExperiment();
					break;
				case EDSIM:
					EDSimulator.nextExperiment();
					break;
				}
				newout.close();

			}

			Runnable doAppend2 = new Runnable() {
				public void run() {

					int rem = 0;
					long runningTime = (System.currentTimeMillis() - Simulator.startTime) / 1000;

					int day = (int) (runningTime / 86400);
					rem = (int) (runningTime % 86400);

					int hour = rem / 3600;
					rem = rem % 3600;

					int min = rem / 60;
					rem = rem % 60;

					int sec = rem;
					ContentPanel.console.append("Completed in "
							+ String.format("%02d", day) + "d::"
							+ String.format("%02d", hour) + "h::"
							+ String.format("%02d", min) + "m::"
							+ String.format("%02d", sec) + "s\n");

					ContentPanel.console.append("Results were saved in:\n"
							+ UserInputs.DESTINATIONFOLDER + "\n");

				}
			};

			SwingUtilities.invokeLater(doAppend2);

		} catch (MissingParameterException e) {
			System.exit(1);
		} catch (IllegalParameterException e) {
			System.exit(1);
		}

		if (Configuration.contains("__x"))
			Network.test();

	}

	private static void copyfile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
