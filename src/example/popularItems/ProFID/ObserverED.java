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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import example.popularItems.ProFIDState;
import example.popularItems.UserInputs;
import example.popularItems.gui.content.ContentPanel;
import peersim.Simulator;
import peersim.config.*;
import peersim.core.*;
import peersim.util.IncrementalStats;

/**
 * Print statistics for ProFID algorithms frequent item computation. Statistics printed
 * are defined by {@link IncrementalStats#toString}
 * 
 * @author Emrah Cem
 * 
 */
public class ObserverED implements Control {

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

	private static File outputFile = new File(UserInputs.DESTINATIONFOLDER
			+ File.separator + "output.txt");
	private static FileWriter writer;
	private static BufferedWriter out;


	/** Protocol identifier; obtained from config property {@link #PAR_PROT}. */
	private final int pid;

	private final int threshold = MultipleValueHolderED.threshold;

	private final double delta = MultipleValueHolderED.delta;

	private int round = 0;

	private static List<Long> convergenceTimes, totalMessages, totalItems,
			totalTimeOuts, totalNoNeighbors;
	private static List<Double>  avgErrs2, avgPrecs, avgRecs;


	// /////////////////////////////////////////////////////////////////////
	// Constructor
	// /////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new observer reading configuration parameters.
	 */
	public ObserverED(String n) {
		name = n;

		try {
			if (writer == null) {
				convergenceTimes = new ArrayList<Long>();
				totalMessages = new ArrayList<Long>();
				totalMessages = new ArrayList<Long>();
				totalItems = new ArrayList<Long>();
				totalNoNeighbors = new ArrayList<Long>();
				totalTimeOuts = new ArrayList<Long>();
//			avgErrs = new ArrayList<Double>();
				avgErrs2 = new ArrayList<Double>();
				avgPrecs = new ArrayList<Double>();
				avgRecs = new ArrayList<Double>();
				writer = new FileWriter(outputFile);
				out = new BufferedWriter(writer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		pid = Configuration.getPid(name + "." + PAR_PROT);
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
		round++;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				ContentPanel.console.append("Experiment "
						+ (Simulator.experimentNo) + ",Round " + round
						+ "\n");

			}
		});
		if (CommonState.getPhase() == CommonState.PHASE_UNKNOWN) {
			if (Network.size() == ProFIDState.numberOfConvergedNodes) {
				return true;
			}
		} else if (CommonState.getPhase() == CommonState.POST_SIMULATION) {
			calculateAccuracy();
			
//			String filename= UserInputs.DESTINATIONFOLDER+File.separator+"experiment0"+File.separator+"graph00000000.dot";
//			try {
//				Runtime.getRuntime().exec("resources\\Graphviz2.21\\bin\\dot -Tjpeg -Kdot " +filename+ " -o " +UserInputs.DESTINATIONFOLDER"+File.separator+"graph.jpeg");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}

		return false;

	}

	private void calculateAccuracy() {

		double[] regularRes;
		double[] regAvgRes = { 0, 0, 0, 0 };
		double[] centFreqRes;
		double[] centSumFreqRes = { 0, 0, 0, 0, 0, 0 };
		double[] lahiriFreqRes;
		double[] lahiriSumFreqRes = { 0, 0, 0, 0, 0, 0 };
		// double avgErr=0;
		double avgErr2 = 0;
		double sumAvgErr2 = 0;
		double prec = 0, recall = 0;
		double avgPrec = 0, avgRec = 0;
		int totalTimeout = 0;
		int totalNoNeigh = 0;
		for (int i = 0; i < Network.size(); i++) {
			MultipleValueHolderED protocol = (MultipleValueHolderED) Network
					.get(i).getProtocol(pid);
			totalTimeout += protocol.getNumOfTimeouts();
			totalNoNeigh += protocol.getNoNeighborCounter();
			HashMap<Integer, Double> fis = protocol.getFIS();

			centFreqRes = centThreshErrorCalculator(fis);
			for (int j = 0; j < centSumFreqRes.length; j++)
				centSumFreqRes[j] += centFreqRes[j];

			lahiriFreqRes = lahiriFreqErrorCalculator(fis);
			for (int j = 0; j < lahiriSumFreqRes.length; j++)
				lahiriSumFreqRes[j] += lahiriFreqRes[j];

			regularRes = regThreshErrorCalculator(fis, Network.get(i));
			for (int j = 0; j < regAvgRes.length; j++)
				regAvgRes[j] += regularRes[j];

			// avgErr=squaredErrorCalculator(fis);
			avgErr2 = avrgingErrorCalculator(fis);
			prec = computePrecision(fis);
			recall = computeRecall(fis);
			// sumAvgErr+=avgErr;
			sumAvgErr2 += avgErr2;
			avgPrec += prec;
			avgRec += recall;

		}

		// avgErr=sumAvgErr/=Network.size();
		avgErr2 = sumAvgErr2 /= Network.size();
		avgPrec /= Network.size();
		avgRec /= Network.size();
		try {
			convergenceTimes.add(CommonState.getTime());
			totalMessages.add(GossipED.numberOfMessageExchange);
			totalItems.add(GossipED.totalNumberOfMessages);
			totalTimeOuts.add((long) totalTimeout);
			totalNoNeighbors.add((long) totalNoNeigh);
			// avgErrs.add(avgErr);
			avgErrs2.add(avgErr2);
			avgPrecs.add(avgPrec);
			avgRecs.add(avgRec);
			if (Simulator.experimentNo + 1 == Configuration.getInt(
					Simulator.PAR_EXPS, 1)) {

				if (!UserInputs.writeNeighborList) {
					File f = new File(UserInputs.DESTINATIONFOLDER
							+ File.separator + "neighborList.txt");
					if (f.exists()) {
						f.delete();
					}
				}
				if (GossipED.gossipWriter != null)
					GossipED.gossipWriter.close();

				out.write(String.format("%25s", ""));
				for (int i = 0; i < convergenceTimes.size(); i++) {
					out.write(String.format("%12s",
							String.format("Exp" + "%d", i + 1)));
				}
				out.write(String.format("%14s", "Average"));
				out.write(String.format("%14s", "Std.Dev."));
				out.newLine();

				out.write(String.format("%-25s", "ConvergenceTime(ms)"));
				writeList(convergenceTimes);
				double avg = calcAvg(convergenceTimes);
				double std = calcStdDev(convergenceTimes, avg);
				out.write(String.format("%14.2f%14.2f", avg, std));
				out.newLine();

				out.write(String.format("%-25s", "TotalMessageExchanged"));
				writeList(totalMessages);
				avg = calcAvg(totalMessages);
				std = calcStdDev(totalMessages, avg);
				out.write(String.format("%14.2f%14.2f", avg, std));
				out.newLine();

				out.write(String.format("%-25s", "TotalItemExchanged"));
				writeList(totalItems);
				avg = calcAvg(totalItems);
				std = calcStdDev(totalItems, avg);
				out.write(String.format("%14.2f%14.2f", avg, std));
				out.newLine();

				out.write(String.format("%-25s", "TotalTimeoutOccurence"));
				writeList(totalTimeOuts);
				avg = calcAvg(totalTimeOuts);
				std = calcStdDev(totalTimeOuts, avg);
				out.write(String.format("%14.2f%14.2f", avg, std));
				// System.err.println(avg +" "+std);
				out.newLine();

				out.write(String.format("%-25s", "TotalNoNeighbor"));
				writeList(totalNoNeighbors);
				avg = calcAvg(totalNoNeighbors);
				std = calcStdDev(totalNoNeighbors, avg);
				out.write(String.format("%14.2f%14.2f", avg, std));
				out.newLine();

				out.write(String.format("%-25s", "Precision"));
				writeList(avgPrecs);
				avg = calcAvg(avgPrecs);
				std = calcStdDev(avgPrecs, avg);
				out.write(String.format("%14.2f%14.2f", avg, std));
				out.newLine();

				out.write(String.format("%-25s", "Recall"));
				writeList(avgRecs);
				avg = calcAvg(avgRecs);
				std = calcStdDev(avgRecs, avg);
				out.write(String.format("%14.2f%14.2f", avg, std));
				out.newLine();

				// out.write(String.format("%-25s","SquareError(%)"));
				// writeList(avgErrs);
				// avg=calcAvg(avgErrs);
				// std=calcStdDev(avgErrs, avg);
				// out.write(String.format("%14.2f%14.2f",avg,std));
				// out.newLine();

				out.write(String.format("%-25s", "RelativeError(%)"));
				writeList(avgErrs2);
				avg = calcAvg(avgErrs2);
				std = calcStdDev(avgErrs2, avg);
				out.write(String.format("%14.2f%14.2f", avg, std));
				out.newLine();

				out.close();
				writer.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("Convergence Time:" + (CommonState.getTime()));
		System.out
				.println("Total Messages:" + GossipED.numberOfMessageExchange);
		System.out.println("Total Messages(item):"
				+ GossipED.totalNumberOfMessages);
		System.out.println("Timeout Count:" + totalTimeout);
		System.out.println("NoNeighborCount:" + totalNoNeigh);
		// System.out.println("Avg Error:"+avgErr);
		//System.out.println("Avg # of spanned peers:" + DominatingSetFinder.span);

		try {
			GossipED.regularFreqErrWriter.write(regAvgRes[0] + " "
					+ regAvgRes[1] + "\r\n" + regAvgRes[2] + " " + regAvgRes[3]
					+ "\r\n");
			GossipED.regularFreqErrWriter.write("Sensitivity (recall):");
			if (regAvgRes[0] + regAvgRes[2] == 0)
				GossipED.regularFreqErrWriter.write("1\r\n");
			else
				GossipED.regularFreqErrWriter.write(regAvgRes[0]
						/ (regAvgRes[0] + regAvgRes[2]) + "\r\n");
			GossipED.regularFreqErrWriter.write("Specificity:" + regAvgRes[3]
					/ (regAvgRes[3] + regAvgRes[1]) + "\r\n");
			GossipED.regularFreqErrWriter
					.write("Accuracy:"
							+ (regAvgRes[0] + regAvgRes[3])
							/ (regAvgRes[0] + regAvgRes[1] + regAvgRes[2] + regAvgRes[3])
							+ "\r\n");
			GossipED.regularFreqErrWriter
					.write("Error:"
							+ (regAvgRes[1] + regAvgRes[2])
							/ (regAvgRes[0] + regAvgRes[1] + regAvgRes[2] + regAvgRes[3])
							+ "\r\n");
			GossipED.regularFreqErrWriter
					.write("Positive Predictive Value (precision):");
			if (regAvgRes[0] + regAvgRes[1] == 0)
				GossipED.regularFreqErrWriter.write("1\r\n");
			else
				GossipED.regularFreqErrWriter.write(regAvgRes[0]
						/ (regAvgRes[0] + regAvgRes[1]) + "\r\n");
			GossipED.regularFreqErrWriter.write("Negative Predictive Value:"
					+ regAvgRes[3] / (regAvgRes[3] + regAvgRes[2]) + "\r\n");
			GossipED.regularFreqErrWriter.flush();

			GossipED.centFreqErrWriter.write(centSumFreqRes[0] + " "
					+ centSumFreqRes[1] + "\r\n" + centSumFreqRes[2] + " "
					+ centSumFreqRes[3] + "\r\n" + centSumFreqRes[4] + " "
					+ centSumFreqRes[5] + "\r\n");
			GossipED.centFreqErrWriter.write("Sensitivity (recall):");
			if (centSumFreqRes[0] + centSumFreqRes[2] == 0)
				GossipED.centFreqErrWriter.write("1\r\n");
			else
				GossipED.centFreqErrWriter.write(centSumFreqRes[0]
						/ (centSumFreqRes[0] + centSumFreqRes[2]) + "\r\n");
			GossipED.centFreqErrWriter.write("Specificity:" + centSumFreqRes[3]
					/ (centSumFreqRes[3] + centSumFreqRes[1]) + "\r\n");
			GossipED.centFreqErrWriter.write("Accuracy:"
					+ (centSumFreqRes[0] + centSumFreqRes[3])
					/ (centSumFreqRes[0] + centSumFreqRes[1]
							+ centSumFreqRes[2] + centSumFreqRes[3]) + "\r\n");
			GossipED.centFreqErrWriter.write("Error:"
					+ (centSumFreqRes[1] + centSumFreqRes[2])
					/ (centSumFreqRes[0] + centSumFreqRes[1]
							+ centSumFreqRes[2] + centSumFreqRes[3]) + "\r\n");
			GossipED.centFreqErrWriter
					.write("Positive Predictive Value (precision):");
			if (centSumFreqRes[0] + centSumFreqRes[1] == 0)
				GossipED.centFreqErrWriter.write("1\r\n");
			else
				GossipED.centFreqErrWriter.write(centSumFreqRes[0]
						/ (centSumFreqRes[0] + centSumFreqRes[1]) + "\r\n");
			GossipED.centFreqErrWriter.write("Negative Predictive Value:"
					+ centSumFreqRes[3]
					/ (centSumFreqRes[2] + centSumFreqRes[3]) + "\r\n");
			GossipED.centFreqErrWriter.flush();

			GossipED.lahiriFreqErrWriter.write(lahiriSumFreqRes[0] + " "
					+ lahiriSumFreqRes[1] + "\r\n" + lahiriSumFreqRes[2] + " "
					+ lahiriSumFreqRes[3] + "\r\n" + lahiriSumFreqRes[4] + " "
					+ lahiriSumFreqRes[5] + "\r\n");
			GossipED.lahiriFreqErrWriter.write("Sensitivity (recall):");
			if (lahiriSumFreqRes[0] + lahiriSumFreqRes[2] == 0)
				GossipED.lahiriFreqErrWriter.write("1\r\n");
			else
				GossipED.lahiriFreqErrWriter.write(lahiriSumFreqRes[0]
						/ (lahiriSumFreqRes[0] + lahiriSumFreqRes[2]) + "\r\n");

			GossipED.lahiriFreqErrWriter.write("Specificity:"
					+ lahiriSumFreqRes[3]
					/ (lahiriSumFreqRes[3] + lahiriSumFreqRes[1]) + "\r\n");
			GossipED.lahiriFreqErrWriter.write("Accuracy:"
					+ (lahiriSumFreqRes[0] + lahiriSumFreqRes[3])
					/ (lahiriSumFreqRes[0] + lahiriSumFreqRes[1]
							+ lahiriSumFreqRes[2] + lahiriSumFreqRes[3])
					+ "\r\n");
			GossipED.lahiriFreqErrWriter.write("Error:"
					+ (lahiriSumFreqRes[1] + lahiriSumFreqRes[2])
					/ (lahiriSumFreqRes[0] + lahiriSumFreqRes[1]
							+ lahiriSumFreqRes[2] + lahiriSumFreqRes[3])
					+ "\r\n");
			GossipED.lahiriFreqErrWriter
					.write("Positive Predictive Value (precision):");
			if (lahiriSumFreqRes[0] + lahiriSumFreqRes[1] == 0)
				GossipED.lahiriFreqErrWriter.write("1\r\n");
			GossipED.lahiriFreqErrWriter.write(lahiriSumFreqRes[0]
					/ (lahiriSumFreqRes[0] + lahiriSumFreqRes[1]) + "\r\n");
			GossipED.lahiriFreqErrWriter.write("Negative Predictive Value:"
					+ lahiriSumFreqRes[3]
					/ (lahiriSumFreqRes[2] + lahiriSumFreqRes[3]) + "\r\n");
			GossipED.lahiriFreqErrWriter.flush();
			GossipED.lahiriFreqErrWriter.close();
			GossipED.centFreqErrWriter.close();
			GossipED.regularFreqErrWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeList(List<?> list) throws IOException {
		for (Object element : list) {
			if (element instanceof Long)
				out.write(String.format("%12d", element));
			else
				out.write(String.format("%12.4f", element));
		}
	}

	private double calcAvg(List<?> list) {
		double avg = 0;
		for (Object object : list) {
			if (object instanceof Long)
				avg += (Long) object;
			else
				avg += (Double) object;
		}
		avg = avg / list.size();
		return avg;
	}

	private double calcStdDev(List<?> list, double avg) {
		double std = 0;
		for (Object object : list) {
			if (object instanceof Long)
				std += Math.pow((Long) object - avg, 2.0);
			else
				std += Math.pow((Double) object - avg, 2.0);
		}
		std /= list.size();
		std = Math.pow(std, 0.5);
		return std;
	}

	private double computePrecision(HashMap<Integer, Double> fis) {
		double precision = 0;
		double total = 0;
		HashMap<Integer, Double> actFreqHash = RandomContentFactoryED
				.getItemFreq();
		Set<Integer> fisKeys = fis.keySet();
		Iterator<Integer> itr2 = fisKeys.iterator();
		double val = 0;
		int key = 0;
		while (itr2.hasNext()) {
			key = itr2.next();

			if (val >= threshold) {
				if (actFreqHash.get(key) >= threshold) {
					precision++;
					total++;
				} else {
					total++;
				}
			}

		}
		if (total == 0)
			return 1;

		return precision / total;
	}

	private double computeRecall(HashMap<Integer, Double> fis) {

		double recall = 0;
		double total = 0;
		HashMap<Integer, Double> actFreqHash = RandomContentFactoryED
				.getItemFreq();
		Set<Integer> keys = actFreqHash.keySet();
		Iterator<Integer> itr = keys.iterator();
		double val = 0;
		int key = 0;
		while (itr.hasNext()) {
			key = itr.next();
			if (!fis.containsKey(key))
				val = 0;
			else
				val = fis.get(key);

			if (actFreqHash.get(key) >= threshold) {
				if (val >= threshold) {
					recall++;
					total++;
				} else {
					total++;
				}
			}

		}

		if (total == 0)
			return 1;
		return recall / total;
	}

	// private double squaredErrorCalculator(HashMap<Integer, Double> fis) {
	// if(fis==null)
	// fis=new HashMap<Integer, Double>();
	//
	// HashMap<Integer, Double>
	// actFreqHash=RandomContentFactoryED.getItemFreq();
	// Set<Integer> actItems=actFreqHash.keySet();
	// Iterator<Integer> actItr=actItems.iterator();
	//
	// double avgRelError=0;
	// double missingCounter=0;
	// double actFreq=0;
	// double estFreq=-1;
	// while(actItr.hasNext()){
	// int actItem=actItr.next();
	// actFreq=actFreqHash.get(actItem);
	// if(!fis.containsKey(actItem)){
	// //avgRelError+=actItem*actItem;
	// missingCounter++;
	// }
	// else{
	// estFreq=fis.get(actItem);
	// //
	// System.err.println("("+actFreq+"-"+estFreq+")^2="+Math.abs(actFreq-estFreq)*Math.abs(actFreq-estFreq));
	// avgRelError+=Math.abs(actFreq-estFreq)*Math.abs(actFreq-estFreq);
	// }
	//
	// if(actFreq==0){
	// //System.err.println("act freq is 0.");
	// System.exit(-1);
	// }
	// }
	// if(missingCounter==actItems.size()){
	// //System.err.println("Counter is 0.");
	// //error is %100 because peer does not know any item.
	// return 100;
	// }
	// avgRelError/=actItems.size();
	// return avgRelError;
	//
	// }

	private double avrgingErrorCalculator(HashMap<Integer, Double> fis) {

		if (fis == null)
			fis = new HashMap<Integer, Double>();
		HashMap<Integer, Double> actFreqHash = RandomContentFactoryED
				.getItemFreq();
		Set<Integer> actItems = actFreqHash.keySet();
		Iterator<Integer> actItr = actItems.iterator();

		double avgRelError = 0;
		double missingCounter = 0;
		double actFreq = 0;
		double estFreq = -1;

		while (actItr.hasNext()) {
			int actItem = actItr.next();
			actFreq = actFreqHash.get(actItem);
			if (!fis.containsKey(actItem)) {
				avgRelError += 100;
				missingCounter++;
			} else {
				estFreq = fis.get(actItem);
				avgRelError += 100 * Math.abs(actFreq - estFreq) / actFreq;
			}

			if (actFreq == 0) {
				System.exit(-1);
			}

		}
		if (missingCounter == actItems.size()) {
			// error is %100 because peer does not know any item.
			return 100;
		}
		avgRelError /= actItems.size();
		return avgRelError;
	}

	private double[] regThreshErrorCalculator(HashMap<Integer, Double> fis,
			Node node) {
		double[] total = { 0, 0, 0, 0 };

		if (fis == null)
			fis = new HashMap<Integer, Double>();

		HashMap<Integer, Double> actFreqHash = RandomContentFactoryED
				.getItemFreq();
		Set<Integer> keys = actFreqHash.keySet();
		Iterator<Integer> itr = keys.iterator();
		double val = 0;
		int key = 0;
		while (itr.hasNext()) {
			key = itr.next();
			if (!fis.containsKey(key))
				val = 0;
			else
				val = fis.get(key);

			// true positive
			if (val >= threshold && actFreqHash.get(key) >= threshold) {
				total[0]++;
			}
			// false positive
			else if (val >= threshold && actFreqHash.get(key) < threshold) {
				total[1]++;
			}
			// true negative
			else if (val <= threshold && actFreqHash.get(key) > threshold) {
				total[2]++;
			}
			// false negative
			else {
				total[3]++;
			}
		}

		return total;
	}

	private double[] centThreshErrorCalculator(HashMap<Integer, Double> fis) {
		double[] total2 = { 0, 0, 0, 0, 0, 0 };

		if (fis == null)
			fis = new HashMap<Integer, Double>();

		HashMap<Integer, Double> actFreqHash = RandomContentFactoryED
				.getItemFreq();
		Set<Integer> keys = actFreqHash.keySet();
		Iterator<Integer> itr = keys.iterator();
		double val = 0;
		int key = 0;
		while (itr.hasNext()) {
			key = itr.next();
			if (!fis.containsKey(key))
				val = 0;
			else
				val = fis.get(key);

			if (val > threshold * (1.0 + delta / 200)
					&& actFreqHash.get(key) > threshold) {
				total2[0]++;
			}
			// false positive
			else if (val > threshold * (1.0 + delta / 200)
					&& actFreqHash.get(key) < threshold) {
				total2[1]++;
			}
			// true negative
			else if (val < threshold * (1.0 - delta / 200)
					&& actFreqHash.get(key) > threshold) {
				total2[2]++;
			}
			// false negative
			else if (val < threshold * (1.0 - delta / 200)
					&& actFreqHash.get(key) < threshold) {
				total2[3]++;
			}
			// undecidable
			else if (val < threshold * (1.0 + delta / 200)
					&& val > threshold * (1.0 - delta / 200)
					&& actFreqHash.get(key) > threshold) {
				total2[4]++;
			} else if (val < threshold * (1.0 + delta / 200)
					&& val > threshold * (1.0 - delta / 200)
					&& actFreqHash.get(key) < threshold) {
				total2[5]++;
			}
		}

		return total2;
	}

	private double[] lahiriFreqErrorCalculator(HashMap<Integer, Double> fis) {
		double[] total2 = { 0, 0, 0, 0, 0, 0 };

		if (fis == null)
			fis = new HashMap<Integer, Double>();

		HashMap<Integer, Double> actFreqHash = RandomContentFactoryED
				.getItemFreq();
		Set<Integer> keys = actFreqHash.keySet();
		Iterator<Integer> itr = keys.iterator();
		double val = 0;
		int key = 0;
		while (itr.hasNext()) {
			key = itr.next();
			if (!fis.containsKey(key))
				val = 0;
			else
				val = fis.get(key);
			// true positive
			if (val > threshold && actFreqHash.get(key) > threshold) {
				total2[0]++;
			}
			// false positive
			else if (val > threshold
					&& actFreqHash.get(key) < threshold * (1 - delta / 100)) {
				total2[1]++;
			}
			// false negative
			else if (val < threshold * (1.0 - delta / 100)
					&& actFreqHash.get(key) > threshold) {
				total2[2]++;
			}
			// true negative
			else if (val < threshold * (1.0 - delta / 100)
					&& actFreqHash.get(key) < threshold * (1 - delta / 100)) {
				total2[3]++;
			}
			// undecidable
			else if (val < threshold && val > threshold * (1.0 - delta / 100)
					&& actFreqHash.get(key) > threshold) {
				total2[4]++;
			} else if (val < threshold && val > threshold * (1.0 - delta / 100)
					&& actFreqHash.get(key) < threshold) {
				total2[5]++;
			}
		}

		return total2;
	}

}
