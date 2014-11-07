package example.popularItems.Kempe;

public class ItemState{
	
	private double s;
	private double w;
	
	public ItemState(double s, double w){
		this.s=s;
		this.w=w;
		
	}

	public double getS() {
		return s;
	}

	public void setS(double s) {
		this.s = s;
	}

	public double getW() {
		return w;
	}

	public void setW(double w) {
		this.w = w;
	}
	
	public String toString(){
			return "{S:"+s+",W:"+w+"}";
	}
	
	
}