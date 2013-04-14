package market;

import java.math.BigDecimal;
import java.util.Comparator;

import utilities.Decimal;
import utilities.Op;
import utilities.Util;

/**
 * Classe representant un cahier des ordres (appelle depth dans les api des bourses d'echnages)
 *
 */
public class Depth {

	/**
	 * La liste des ordres de ventes (demandes de Bitcoin)
	 */
	public Order[] asks;
	/**
	 * La liste des ordres d'achats (offres de Bitcoin)
	 */
	public Order[] bids;
	
	public Depth(){
		bids=new Order[0];
		asks=new Order[0];
	}
	
	/**
	 * Inverser le classement des bids. Certaines bourses d'echanges inversent l'ordre.
	 * Pour normaliser les donnees, il faut parfois utiliser cette methode
	 */
	public void reverseBids(){
		Util.reverse(bids);
	}
	
	/**
	 * Inverser le classement des asks. Certaines bourses d'echanges inversent l'ordre.
	 * Pour normaliser les donnees, il faut parfois utiliser cette methode
	 */
	public void reverseAsks(){
		Util.reverse(asks);
	}
	
	/**
	 * Methode utilitaire pour calculer getBidSum et getAskSum
	 */
	private static BigDecimal getSum(Order[] tab, Order stopOrder, Comparator<AbstractOrder> comp){
		BigDecimal sum= Decimal.ZERO;
		int i=0;
		while(i<tab.length && comp.compare(tab[i],stopOrder)<0)
			sum=Op.add(sum, tab[i].amount);
		return sum;
	}
	
	/**
	 * @param stopPrice Jusqu'a quel prix on descend dans le cahier
	 * @return Calcul la somme du volume des bids du bid le plus competitif jusqu'à un bid de prix plus petit que stopPrice
	 */
	public BigDecimal getBidSum(BigDecimal stopPrice){
		return getSum(bids,new Order(stopPrice, Decimal.ONE,Type.BID), new CompareBid());
	}
	
	/**
	 * Permet de calculer le volume qui doit etre executer avant qu'un ordre de prix stopPrice soit execute
	 * @param stopPrice Jusqu'a quel prix on descend dans le cahier
	 * @return Calcul la somme du volume des asks allant du ask le plus competitif jusqu'à un ask de prix plus grand que stopPrice
	 */
	public BigDecimal getAskSum(BigDecimal stopPrice){
		return getSum(asks,new Order(stopPrice, Decimal.ONE,Type.ASK), new CompareBid());
	}
	
	public String toString(){
		StringBuffer buf= new StringBuffer(30+30*(bids.length+asks.length));
		buf.append("{asks:[");
		for(Order o: asks){
			buf.append(o);
			buf.append(",");
		}
		buf.append("],\n");
		buf.append("bids:[");
		for(Order o: bids){
			buf.append(o);
			buf.append(",");
		}
		buf.append("]}");
		
		return buf.toString();
	}
}
