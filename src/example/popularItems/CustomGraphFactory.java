package example.popularItems;

import java.util.Random;

import peersim.graph.Graph;

public class CustomGraphFactory{

	private CustomGraphFactory(){}
	
	public static Graph wireErdosRenyi(Graph g, double p, Random r ){
	final int n = g.size();
	if( n < 2 ) return g;

	for (int i = 0; i < n-1; i++)
	{
		for (int j = i+1; j < n; j++){
			if(i!=j && r.nextDouble()<p)
				g.setEdge(i, j);
		}
	}
	return g;
}

}
