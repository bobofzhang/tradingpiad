package arbitrage;

import market.Currency;

public class CustomVertex{
	public Currency c;
	int t;
	private int hashcode;
	public CustomVertex() {
	}
	public CustomVertex(Currency c,int t){
		this.c=c;
		this.t=t;
		computeHashCode();
	}
	
	public void computeHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		result = prime * result + t;
		this.hashcode= result;
	}
	@Override
	public int hashCode(){
		return hashcode;
		}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CustomVertex))
			return false;
		CustomVertex other = (CustomVertex) obj;
		if (c != other.c)
			return false;
		if (t != other.t)
			return false;
		return true;
	}
	
	public String toString(){
		return "("+c.toString()+","+t+")";
	}
}
