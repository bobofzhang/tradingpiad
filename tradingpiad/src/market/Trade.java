package market;

import java.math.BigDecimal;



/**
 * Un echange qui a ete execute. (utilise dans l'historique des echanges)
 *
 */
public class Trade extends AbstractOrder{
	public long date;
	public String tid;
	
	/**
	 * @param date La date a laquelle l'echange a ete execute
	 * @param price A quel prix l'echange a ete effectuer
	 * @param amount Quelle quantite a ete echangee
	 * @param tid L'identifiant de l'echange (propre a la bourse d'echange)
	 * @param type De quel type d'ordre resulte l'echange (BID ou ASK = demande ou offre)
	 */
	public Trade(long date, BigDecimal price, BigDecimal amount, String tid,Type type){
		super(price,amount,type);
		this.date=date;
		this.tid=tid;
	}
	
	public Trade(){
		super();
	}
	
	public String toString(){
		return "["+date+","+price+","+amount+","+tid+","+trade_type+"]";
	}

}
