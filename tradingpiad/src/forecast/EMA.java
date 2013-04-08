package forecast;

import java.math.BigDecimal;

import utilities.Decimal;
import utilities.Op;
/**
 * 
 * @author Kevin
 *
 * Implementation de la moyenne mobile exponentielle (voir http://en.wikipedia.org/wiki/Moving_average)
 */
public class EMA {
	private final BigDecimal alpha; // Coefficient de lissage
	private final int n; // Nombre de jours
	
	private BigDecimal yesterday; // Valeur precedente de la moyenne
	private BigDecimal today; // valeur suivante de la moyenne
	
	//Variable temporaire pour els calculs;
	private int ite; //
	private BigDecimal tmp;
	
	public EMA(int n){
		alpha=Op.div(new Decimal("2"), Op.add(new Decimal(String.valueOf(n)),Decimal.ONE));
		ite=0;
		this.n=n;
		tmp=Decimal.ZERO;
	}
	
	public BigDecimal getValue(BigDecimal priceToday){
		yesterday=today;
		if(ite<n){
			tmp=Op.add(tmp, priceToday);
			today=Decimal.ZERO;
		}
		else if (ite==n){
			today=Op.div(tmp, new Decimal(String.valueOf(n)));
		}
		else{
			today=Op.add(yesterday, Op.mult(alpha, Op.sub(priceToday, yesterday)));// EMAtoday= EMAyest
		}
		
		ite++;
		return today;
	}
	
	public int nbPeriod(){
		return n;
	}
}
