package example.popularItems;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import peersim.core.Network;
import peersim.edsim.EDSimulator;

/**
 * 
 * @author Emrah Cem
 * 
 *         Reads the content of the churn file into an ArrayList object Churn
 *         files have been generated by Affluenza tool allowing to generate
 *         complex churn scenarios. (http://ast-deim.urv.cat/trac/affluenza/)
 */
public class ChurnFileReader {
	public static void read(String fileName) throws IOException,
			NumberFormatException {

		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				fileName));

		String readLine;

		while (null != (readLine = bufferedReader.readLine())) {

			StringTokenizer stringTokenizer = new StringTokenizer(readLine);
			
			int nodeIndex = Integer.parseInt(stringTokenizer.nextToken());
			int numOfSessions = Integer.parseInt(stringTokenizer.nextToken());
			long joinTime = -1;
			long leaveTime = -1;
			Churn ch = null;

			for (int currSession = 0; currSession < numOfSessions-1; currSession++) {
				joinTime = (long)Double.parseDouble(stringTokenizer.nextToken());
				leaveTime = (long)Double.parseDouble(stringTokenizer.nextToken());
				ch = new Churn(true, joinTime);
				EDSimulator.add(ch.getTime() * 1000, (Object) ch,
						Network.get(nodeIndex), 0);
				ch = new Churn(false, leaveTime);
				EDSimulator.add(ch.getTime() * 1000, (Object) ch,
						Network.get(nodeIndex), 0);
			}
			//add for the last join event
			joinTime = (long)Double.parseDouble(stringTokenizer.nextToken());
			ch = new Churn(true, joinTime);
			EDSimulator.add(ch.getTime() * 1000, (Object) ch,
					Network.get(nodeIndex), 0);
		}
	}

}
