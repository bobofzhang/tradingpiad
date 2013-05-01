package utilities;

import java.math.BigDecimal;

public class PosInf extends Decimal {

	public PosInf() {
		super("0");
	}
	
	public int compareTo(BigDecimal val){
		return val instanceof PosInf?0:1;
	}
	
	public String toString(){
		return "+Inf";
	}

}
