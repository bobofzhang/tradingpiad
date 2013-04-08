package utilities;

import java.math.BigDecimal;

@SuppressWarnings("serial")
public class Decimal extends BigDecimal {

	public Decimal(double val) {
		super(val,Op.mc);
	}
	
	public Decimal(String val) {
		super(val,Op.mc);
	}
	

}
