package utilities;

import java.math.BigDecimal;

/**
 * Classe eritant de BigDecimal pour rnedre implicite l'utilisation de MathContext
 */
@SuppressWarnings("serial")
public class Decimal extends BigDecimal {

	public Decimal(double val) {
		super(val,Op.mc);
	}
	
	public Decimal(String val) {
		super(val,Op.mc);
	}
	

}
