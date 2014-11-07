package example.popularItems;

import java.util.Random;

import peersim.core.CommonState;

/**
 * 
 * Code source is
 * http://diveintodata.org/2009/09/13/zipf-distribution-generator-in-java/
 * 
 */
public class ZipfGenerator {
	Random r = new Random(0);
	private int size;
	private double skew;
	private double bottom = 0;

	public ZipfGenerator(int size, double skew) {
		this.r = new Random(System.currentTimeMillis());
		this.size = size;
		this.skew = skew;

		for (int i = 1; i < size; i++) {
			this.bottom += (1 / Math.pow(i, this.skew));
		}
	}

	// the next() method returns an rank id. The frequency of returned rank ids
	// are follows Zipf distribution.
	public int next() {
		int rank;
		double friquency = 0;
		double dice;

		rank = CommonState.r.nextInt(size);
		friquency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
		dice = CommonState.r.nextDouble();

		while (!(dice < friquency)) {
			rank = CommonState.r.nextInt(size);
			friquency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
			dice = CommonState.r.nextDouble();
		}

		return rank;
	}

	// This method returns a probability that the given rank occurs.
	public double getProbability(int rank) {
		return (1.0d / Math.pow(rank, this.skew)) / this.bottom;
	}
}