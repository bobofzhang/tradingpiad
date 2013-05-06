package market;

import java.math.BigDecimal;

import utilities.Assert;
import utilities.Decimal;
import utilities.Op;


/**
 * Un portefeuille contenant de l'argent dans les differenets monnais supportees
 *
 */
public class Wallet {

	/**
	 * Un tableau des quantites d'argent dans le portefeuille.
	 * A chaque indice correspond une monnaie , l'indice d'une currency est donne par la methode ordinal()
	 */
	public BigDecimal[] wallet;
	
	public Wallet(){
		wallet =new BigDecimal [Currency.values().length];
		for(int i=0; i< wallet.length;i++)
			wallet[i]=new Decimal("0");;
	}
	
	
	/**
	 * @param c La monnaie dont on veut connaitre la quantite
	 * @return La quantite de c
	 */
	public BigDecimal getAmount(Currency c){
		return wallet[c.ordinal()];
	}
	
	/**
	 * @param c La monnaie qu'on veut ajouter
	 * @param amount La quantite de c qu'on veut ajouter au portefeuille
	 */
	public void setAmount(Currency c, BigDecimal amount){
		BigDecimal new_amount=Op.add(wallet[c.ordinal()],amount);
		Assert.checkPrecond(new_amount.compareTo(Decimal.ZERO)>=0,"Action interdite: Pas suffisamment de "+c+" dans le portefeuille");
		wallet[c.ordinal()]=new_amount;
		
	}
	
	public String toString(){
		StringBuffer buf= new StringBuffer();
		buf.append("{");
		for(Currency c: Currency.values())
			buf.append(c+":"+wallet[c.ordinal()]+",");
		buf.append("}");
		return buf.toString();
	}
	
	/* (non-Javadoc)
	 * Copie profonde d'un wallet
	 */
	public Wallet clone(){
		Wallet w= new Wallet();
		for(Currency cur:Currency.values())
			w.setAmount(cur, this.getAmount(cur));
		return w;
		
	}
}
