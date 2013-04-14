package utilities;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Classe utilitaire faisant des opérations sur des BigDecimal de maniere a rendre le MathContext implicite
 *
 */
public class Op {

	static MathContext mc = new MathContext(64, RoundingMode.HALF_UP);

	public static BigDecimal add(BigDecimal a, BigDecimal b) {
		return a.add(b, mc);
	}

	public static BigDecimal sub(BigDecimal a, BigDecimal b) {
		return a.subtract(b, mc);
	}

	public static BigDecimal mult(BigDecimal a, BigDecimal b) {
		return a.multiply(b, mc);
	}

	public static BigDecimal div(BigDecimal a, BigDecimal b) {
		return a.divide(b, mc);
	}

	public static BigDecimal abs(BigDecimal a) {
		return a.abs(mc);
	}

	public static BigDecimal neg(BigDecimal a) {
		return a.negate(mc);
	}

	public static BigDecimal min(BigDecimal a, BigDecimal b) {
		return  a.min(b);
	}
	
	public static BigDecimal max(BigDecimal a, BigDecimal b) {
		return  a.max(b);
	}
	
	public static boolean sup0(BigDecimal a){
		return a.compareTo(Decimal.ZERO)>0;
	}
	
	public static boolean inf0(BigDecimal a){
		return a.compareTo(Decimal.ZERO)<0;
	}
	

}
