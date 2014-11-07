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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import example.popularItems.ProFIDState;
import example.popularItems.UserInputs;
import example.popularItems.gui.content.ContentPanel;

import peersim.Simulator;
import peersim.config.*;
import peersim.core.*;
import peersim.util.IncrementalStats;

/**
 * 
 * @author Emrah Cem
 * 
 */
public class IncomingObserverKempe implements Control {

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
	// private static final String PAR_ACCURACY = "accuracy";

	/**
	 * The protocol to operate on.
	 * 
	 * @config
	 */
	private static final String PAR_PROT = "protocol";

	// private static final String PAR_THRESHOLD = "threshold";

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

	/**
	 * Accuracy for standard deviation used to stop the simulation; obtained
	 * from config property {@link #PAR_ACCURACY}.
	 */
	// private final double accuracy;

	/** Protocol identifier; obtained from config property {@link #PAR_PROT}. */
	private final int pid;

	private static List<Long> convergenceTimes, totalMessages, totalItems;
	private static List<Double> avgErrs, avgPrecs, avgRecs;
	private final double delta = MultipleValueHolderKempe.delta;
	private final int threshold = MultipleValueHolderKempe.threshold;
	private int round = 0;

	// public static int numberOfConvergedNodes;

	// private boolean setConverged;
	// /////////////////////////////////////////////////////////////////////
	// Constructor
	// /////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new observer reading configuration parameters.
	 */
	public IncomingObserverKempe(String n) {

		try {
			if (writer == null) {
				convergenceTimes = new ArrayList<Long>();
				totalMessages = new ArrayList<Long>();
				totalMessages = new ArrayList<Long>();
				totalItems = new ArrayList<Long>();
				avgErrs = new ArrayList<Double>();
				avgPrecs = new ArrayList<Double>();
				avgRecs = new ArrayList<Double>();
				writer = new FileWriter(outputFile);
				out = new BufferedWriter(writer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.err.println(CommonState.getTime()+"--IncomingObserver is created");
		// System.err.println("Observer::constr(String) starts");

		name = n;
		// accuracy = Configuration.getDouble(name + "." + PAR_ACCURACY, -1);
		pid = Configuration.getPid(name + "." + PAR_PROT);
		// observe random node in each experiment
		// observedId=(Network.size()+1)*Simulator.experimentNo+CommonState.r.nextInt(Network.size());
		// always observe the 0 th node
		// numberOfConvergedNodes=0;
		// setConverged=false;
		// =(Network.size()+1)*Simulator.experimentNo;

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
		ContentPanel.console.append("Experiment "
				+ (Simulator.experimentNo) + ",Round " + round
				+ "\n");
		if (CommonState.getPhase() == CommonState.PHASE_UNKNOWN) {
			for (int i = 0; i < Network.size(); i++) {
				if (Network.get(i).isUp()) {
					MultipleValueHolderKempe protocol = (MultipleValueHolderKempe) Network
							.get(i).getProtocol(pid);
					if (GossipKempe.incrementEachRound) {
						protocol.setConverged(protocol.checkConvergence(Network
								.get(i)));
						protocol.setReceivedMessageFromNeighbor(false);
					}
					if (protocol.getCurrIncomingItems().size() != 0)
						protocol.setprevIncomingItems(protocol
								.getCurrIncomingItems());
					protocol.setCurrIncomingItems(new HashMap<Integer, ItemState>());
				}

			}
			if (Network.size() == ProFIDState.numberOfConvergedNodes) {
				System.out.println("Convergence Time:"
						+ (CommonState.getTime()));
				System.out.println("Total Messages:"
						+ GossipKempe.numberOfMessageExchange);
				return true;
			}

			return false;
		} else if (CommonState.getPhase() == CommonState.POST_SIMULATION) {

			calculateAccuracy();
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
		double totalAvgError = 0;
		// double totalDiffError=0;
		double avgErr = 0;
		double avg = 0;
		double prec = 0, recall = 0;
		double avgPrec = 0, avgRec = 0;
		for (int i = 0; i < Network.size(); i++) {
			MultipleValueHolderKempe protocol = (MultipleValueHolderKempe) Network
					.get(i).getProtocol(pid);
			HashMap<Integer, Double> fis = protocol.getFIS();
			avgErr = avrgingErrorCalculator(fis, Network.get(i).getID());
			// double
			// diffErr=squaredErrorCalculator(fis,(int)(Network.get(i).getID()));

			centFreqRes = centThreshErrorCalculator(fis);
			for (int j = 0; j < centSumFreqRes.length; j++)
				centSumFreqRes[j] += centFreqRes[j];

			lahiriFreqRes = lahiriFreqErrorCalculator(fis);
			for (int j = 0; j < lahiriSumFreqRes.length; j++)
				lahiriSumFreqRes[j] += lahiriFreqRes[j];

			regularRes = regThreshErrorCalculator(fis);
			for (int j = 0; j < regAvgRes.length; j++)
				regAvgRes[j] += regularRes[j];

			if (GossipKempe.writeEnabled) {
				try {
					GossipKempe.gossipWriter.write("peer "
							+ Network.get(i).getID() + ":" + fis);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			totalAvgError += avgErr;
			prec = computePrecision(fis, Network.get(i).getID());
			recall = computeRecall(fis);
			avgPrec += prec;
			avgRec += recall;
			// totalDiffError+=diffErr;
		}

		totalAvgError /= Network.size();
		avgPrec /= Network.size();
		avgRec /= Network.size();
		// totalDiffError/=Network.size();
		try {
			convergenceTimes.add(CommonState.getTime());
			totalMessages.add(GossipKempe.numberOfMessageExchange);
			totalItems.add(GossipKempe.totalNumberOfMessages);
			avgErrs.add(totalAvgError);
			avgPrecs.add(avgPrec);
			avgRecs.add(avgRec);
			// diffErrs.add(totalDiffError);

			if (Simulator.experimentNo + 1 == Configuration.getInt(
					Simulator.PAR_EXPS, 1)) {

				if (!UserInputs.writeNeighborList) {
					File f = new File(UserInputs.DESTINATIONFOLDER
							+ File.separator + "neighborList.txt");
					if (f.exists()) {
						f.delete();
					}
				}

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
				avg = calcAvg(convergenceTimes);
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

				// out.write(String.format("%-25s","TotalTimeoutOccurence"));
				// writeList(totalTimeOuts);
				// avg=calcAvg(totalTimeOuts);
				// std=calcStdDev(totalTimeOuts, avg);
				// out.write(String.format("%14.2f%14.2f",avg,std));
				// //System.err.println(avg +" "+std);
				// out.newLine();
				//
				// out.write(String.format("%-25s","TotalNoNeighbor"));
				// writeList(totalNoNeighbors);
				// avg=calcAvg(totalNoNeighbors);
				// std=calcStdDev(totalNoNeighbors, avg);
				// out.write(String.format("%14.2f%14.2f",avg,std));
				// out.newLine();
				//
				// out.write(String.format("%-25s","SquareError(%)"));
				// writeList(avgErrs);
				// avg=calcAvg(avgErrs);
				// std=calcStdDev(avgErrs, avg);
				// out.write(String.format("%14.2f%14.2f",avg,std));
				// out.newLine();

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

				out.write(String.format("%-25s", "RelativeError(%)"));
				writeList(avgErrs);
				avg = calcAvg(avgErrs);
				std = calcStdDev(avgErrs, avg);
				out.write(String.format("%14.2f%14.2f", avg, std));
				out.newLine();

				// out.write(String.format("%-25s","DiffError(%)"));
				// writeList(diffErrs);
				// avg=calcAvg(diffErrs);
				// std=calcStdDev(diffErrs, avg);
				// out.write(String.format("%14.2f%14.2f",avg,std));
				// out.newLine();

				// out.write("Total Messages:"+GossipED.numberOfMessageExchange+"\n");
				// out.write("Total Messages(item):"+GossipED.totalNumberOfMessages+"\n");
				// out.write("Timeout Count:"+totalTimeout+"\n");
				// out.write("Avg Error:"+avgErr+"\n");

				out.close();
				writer.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			GossipKempe.regularFreqErrWriter.write(regAvgRes[0] + " "
					+ regAvgRes[1] + "\r\n" + regAvgRes[2] + " " + regAvgRes[3]
					+ "\r\n");
			GossipKempe.regularFreqErrWriter.write("Sensitivity (recall):");
			if (regAvgRes[0] + regAvgRes[2] == 0)
				GossipKempe.regularFreqErrWriter.write("1\r\n");
			else
				GossipKempe.regularFreqErrWriter.write(regAvgRes[0]
						/ (regAvgRes[0] + regAvgRes[2]) + "\r\n");
			GossipKempe.regularFreqErrWriter.write("Specificity:"
					+ regAvgRes[3] / (regAvgRes[3] + regAvgRes[1]) + "\r\n");
			GossipKempe.regularFreqErrWriter
					.write("Accuracy:"
							+ (regAvgRes[0] + regAvgRes[3])
							/ (regAvgRes[0] + regAvgRes[1] + regAvgRes[2] + regAvgRes[3])
							+ "\r\n");
			GossipKempe.regularFreqErrWriter
					.write("Error:"
							+ (regAvgRes[1] + regAvgRes[2])
							/ (regAvgRes[0] + regAvgRes[1] + regAvgRes[2] + regAvgRes[3])
							+ "\r\n");
			GossipKempe.regularFreqErrWriter
					.write("Positive Predictive Value (precision):");
			if (regAvgRes[0] + regAvgRes[1] == 0)
				GossipKempe.regularFreqErrWriter.write("1\r\n");
			else
				GossipKempe.regularFreqErrWriter.write(regAvgRes[0]
						/ (regAvgRes[0] + regAvgRes[1]) + "\r\n");
			GossipKempe.regularFreqErrWriter.write("Negative Predictive Value:"
					+ regAvgRes[3] / (regAvgRes[3] + regAvgRes[2]) + "\r\n");
			GossipKempe.regularFreqErrWriter.flush();

			GossipKempe.centFreqErrWriter.write(centSumFreqRes[0] + " "
					+ centSumFreqRes[1] + "\r\n" + centSumFreqRes[2] + " "
					+ centSumFreqRes[3] + "\r\n" + centSumFreqRes[4] + " "
					+ centSumFreqRes[5] + "\r\n");
			GossipKempe.centFreqErrWriter.write("Sensitivity (recall):");
			if (centSumFreqRes[0] + centSumFreqRes[2] == 0)
				GossipKempe.centFreqErrWriter.write("1\r\n");
			else
				GossipKempe.centFreqErrWriter.write(centSumFreqRes[0]
						/ (centSumFreqRes[0] + centSumFreqRes[2]) + "\r\n");
			GossipKempe.centFreqErrWriter.write("Specificity:"
					+ centSumFreqRes[3]
					/ (centSumFreqRes[3] + centSumFreqRes[1]) + "\r\n");
			GossipKempe.centFreqErrWriter.write("Accuracy:"
					+ (centSumFreqRes[0] + centSumFreqRes[3])
					/ (centSumFreqRes[0] + centSumFreqRes[1]
							+ centSumFreqRes[2] + centSumFreqRes[3]) + "\r\n");
			GossipKempe.centFreqErrWriter.write("Error:"
					+ (centSumFreqRes[1] + centSumFreqRes[2])
					/ (centSumFreqRes[0] + centSumFreqRes[1]
							+ centSumFreqRes[2] + centSumFreqRes[3]) + "\r\n");
			GossipKempe.centFreqErrWriter
					.write("Positive Predictive Value (precision):");
			if (centSumFreqRes[0] + centSumFreqRes[1] == 0)
				GossipKempe.centFreqErrWriter.write("1\r\n");
			else
				GossipKempe.centFreqErrWriter.write(centSumFreqRes[0]
						/ (centSumFreqRes[0] + centSumFreqRes[1]) + "\r\n");
			GossipKempe.centFreqErrWriter.write("Negative Predictive Value:"
					+ centSumFreqRes[3]
					/ (centSumFreqRes[2] + centSumFreqRes[3]) + "\r\n");
			GossipKempe.centFreqErrWriter.flush();

			GossipKempe.lahiriFreqErrWriter.write(lahiriSumFreqRes[0] + " "
					+ lahiriSumFreqRes[1] + "\r\n" + lahiriSumFreqRes[2] + " "
					+ lahiriSumFreqRes[3] + "\r\n" + lahiriSumFreqRes[4] + " "
					+ lahiriSumFreqRes[5] + "\r\n");
			GossipKempe.lahiriFreqErrWriter.write("Sensitivity (recall):");
			if (lahiriSumFreqRes[0] + lahiriSumFreqRes[2] == 0)
				GossipKempe.lahiriFreqErrWriter.write("1\r\n");
			else
				GossipKempe.lahiriFreqErrWriter.write(lahiriSumFreqRes[0]
						/ (lahiriSumFreqRes[0] + lahiriSumFreqRes[2]) + "\r\n");

			GossipKempe.lahiriFreqErrWriter.write("Specificity:"
					+ lahiriSumFreqRes[3]
					/ (lahiriSumFreqRes[3] + lahiriSumFreqRes[1]) + "\r\n");
			GossipKempe.lahiriFreqErrWriter.write("Accuracy:"
					+ (lahiriSumFreqRes[0] + lahiriSumFreqRes[3])
					/ (lahiriSumFreqRes[0] + lahiriSumFreqRes[1]
							+ lahiriSumFreqRes[2] + lahiriSumFreqRes[3])
					+ "\r\n");
			GossipKempe.lahiriFreqErrWriter.write("Error:"
					+ (lahiriSumFreqRes[1] + lahiriSumFreqRes[2])
					/ (lahiriSumFreqRes[0] + lahiriSumFreqRes[1]
							+ lahiriSumFreqRes[2] + lahiriSumFreqRes[3])
					+ "\r\n");
			GossipKempe.lahiriFreqErrWriter
					.write("Positive Predictive Value (precision):");
			if (lahiriSumFreqRes[0] + lahiriSumFreqRes[1] == 0)
				GossipKempe.lahiriFreqErrWriter.write("1\r\n");
			GossipKempe.lahiriFreqErrWriter.write(lahiriSumFreqRes[0]
					/ (lahiriSumFreqRes[0] + lahiriSumFreqRes[1]) + "\r\n");
			GossipKempe.lahiriFreqErrWriter.write("Negative Predictive Value:"
					+ lahiriSumFreqRes[3]
					/ (lahiriSumFreqRes[2] + lahiriSumFreqRes[3]) + "\r\n");
			GossipKempe.lahiriFreqErrWriter.flush();
			GossipKempe.lahiriFreqErrWriter.close();
			GossipKempe.centFreqErrWriter.close();
			GossipKempe.regularFreqErrWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("RelativeError:%" + avg);
		// System.out.println("Accuracy:%"+100*(total[0]+total[2])/((double)Network.size()*Network.numberoOfItemTypes()));
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

	private double avrgingErrorCalculator(HashMap<Integer, Double> fis, long id) {

		if (fis == null)
			fis = new HashMap<Integer, Double>();
		HashMap<Integer, Double> actFreqHash = RandomContentFactoryKempe
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
				System.err.println("act freq is 0.");
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

	private double computePrecision(HashMap<Integer, Double> fis, long id) {
		double precision = 0;
		double total = 0;
		HashMap<Integer, Double> actFreqHash = RandomContentFactoryKempe
				.getItemFreq();
		Iterator<Integer> itr2 = fis.keySet().iterator();
		double val = 0;
		int key = 0;

		while (itr2.hasNext()) {
			key = itr2.next();
			val = fis.get(key);

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
		// System.err.println(precision+"/"+total+"="+precision/total);
		return precision / total;
	}

	private double computeRecall(HashMap<Integer, Double> fis) {

		double recall = 0;
		double total = 0;
		HashMap<Integer, Double> actFreqHash = RandomContentFactoryKempe
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
		if (total == 0) {
			return 1;
		}
		return recall / total;
	}

	// private double squaredErrorCalculator(HashMap<Integer, Double> fis, int
	// i) {
	// if(fis==null)
	// fis=new HashMap<Integer, Double>();
	//
	// HashMap<Integer, Double>
	// actFreqHash=RandomContentFactoryKempe.getItemFreq();
	// Set<Integer> actItems=actFreqHash.keySet();
	// Iterator<Integer> actItr=actItems.iterator();
	// double avgRelError=0;
	// double missingCounter=0;
	// double actFreq=0;
	// double estFreq=-1;
	// while(actItr.hasNext()){
	// int actItem=actItr.next();
	// actFreq=actFreqHash.get(actItem);
	// if(!fis.containsKey(actItem)){
	// avgRelError+=actItem;
	// missingCounter++;
	// }
	// else{
	// estFreq=fis.get(actItem);
	// //
	// System.err.println("("+actFreq+"-"+estFreq+")^2="+Math.abs(actFreq-estFreq)*Math.abs(actFreq-estFreq));
	// avgRelError+=Math.abs(actFreq-estFreq);
	// }
	//
	// if(actFreq==0){
	// System.err.println("act freq is 0.");
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

//	private double errorCalculator1(HashMap<Integer, Double> fis, long id) {
//
//		if (fis == null)
//			fis = new HashMap<Integer, Double>();
//		Set<Integer> keys = fis.keySet();
//		Iterator<Integer> itr = keys.iterator();
//		// System.out.println(Network.get(i).getID()+" frequentList");
//		// System.out.println(holder.converged);
//		double avgRelError = 0;
//		double counter = 0;
//		while (itr.hasNext()) {
//			int key = itr.next();
//			double estFreq = fis.get(key);
//
//			if (key != -1) {
//				counter++;
//				// double actFreq=RandomContentFactoryED.getItemFreq().get(key);
//				double actFreq = RandomContentFactoryKempe.getItemFreq().get(
//						key);
//				// System.out.println("Item "+key+", actFreq:"+actFreq+", estFreq:"+estFreq+", err:"+100*Math.abs(actFreq-estFreq)/actFreq);
//				// System.err.println(CommonState.getTime()+"-Peer "+Network.get(i).getID()+"\r\n")
//				avgRelError += 100 * Math.abs(actFreq - estFreq) / actFreq;
//			}
//		}
//		avgRelError /= counter;
//		// System.out.println("--------------------------\r\n");
//		// System.out.println("Avg error:"+avgRelError);
//		return avgRelError;
//	}

	private double[] regThreshErrorCalculator(HashMap<Integer, Double> fis) {
		double[] total = { 0, 0, 0, 0 };

		if (fis == null)
			fis = new HashMap<Integer, Double>();
		HashMap<Integer, Double> actFreqHash = RandomContentFactoryKempe
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

			// System.out.println(val+" "+actFreqHash.get(key));
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
		// return 100*(total[1]+total[2])/(total[0]+total[2]+total[1]+total[3]);
		return total;
	}

	private double[] centThreshErrorCalculator(HashMap<Integer, Double> fis) {
		double[] total2 = { 0, 0, 0, 0, 0, 0 };

		if (fis == null)
			fis = new HashMap<Integer, Double>();

		HashMap<Integer, Double> actFreqHash = RandomContentFactoryKempe
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

		HashMap<Integer, Double> actFreqHash = RandomContentFactoryKempe
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
