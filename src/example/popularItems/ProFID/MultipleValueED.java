package example.popularItems.ProFID;

import java.util.HashMap;


public interface MultipleValueED {

	/**
	 * Returns the value of the parameter hold by the implementor
	 * of this interface. 
	 */
	public HashMap<Integer, Double> getContent();

	/**
	 * Modifies the value of the parameter hold by the implementor
	 * of this interface. 
	 */
	public void setContent(HashMap<Integer, Double> map);
	
	public boolean isConverged() ;

	public void setConverged(boolean converged) ;


}
