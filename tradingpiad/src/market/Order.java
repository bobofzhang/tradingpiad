package market;

import java.math.BigDecimal;

/**
 * Un ordre dans le cahier des ordres
 *
 */
public class Order extends AbstractOrder{
	public String oid;

	
	/**
	 * @param price Le prix auquel on fixe l'ordre
	 * @param amount La quantite de l'ordre
	 * @param type Le type de l'ordre (BID ou ASK = demande ou offre)
	 */
	public Order(BigDecimal price, BigDecimal amount,Type type){
		super(price,amount,type);
	}
	
	public String toString(){
		return"["+price+ "," +amount+","+trade_type.name()+"]";
	}
}
