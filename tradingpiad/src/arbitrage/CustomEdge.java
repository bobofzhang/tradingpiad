package arbitrage;

import java.math.BigDecimal;

import market.Market;
import market.Order;

import org.jgrapht.graph.DefaultWeightedEdge;

import strategies.PriceManager;
import utilities.Decimal;
import utilities.Op;

/**
 * Un arc customise pour le graphe utilise lors de la recherche de la sequence
 * d'echange la plus profitable pour la strategie ArbitrageSimple.
 * Le poids d'une arrete est le taux de change d'une monnaie (sommet source de l'arc ) vers une autre(sommet cible de l'arc)
 */
public class CustomEdge extends DefaultWeightedEdge{
	
	private Market market;
	private boolean reversed;
	private PriceManager priceManager;
	
	
	/**
	 * Cree un arc customise pour le graphe des taux de changes dans la strategie Arbitrage simple.
	 * @param market Le marche sur lequel se fait l'echange hypothetique
	 * @param reversed Le sens de l'arc :
	 *        false si l'echange se fait dans le sens (market.cur1->market.cur2)
	 *        true si l'echange se fait dans le sens (market.cur2 -> market.cur1)
	 */
	public CustomEdge(Market market, boolean reversed, PriceManager priceManager){
		this.market=market;
		this.reversed=reversed;
		this.priceManager=priceManager;
	}

	protected double getWeight() {
		
		if (market ==null)
			// si paas de marche, l'arrete coute 0
			return 0.;

		return -Math.log(quantiteApresEchange(Decimal.ONE).doubleValue());
			// On log le resulte pour rendre les poids des arc additifs et permettre l'utilisation de l'algorithme du plus court chemin
			// On change el signe pour que l'algo trouve le chemin le plus long(= le plus profitable) au lieu du plus court

	}
	private double trueWeight(){
		if (market ==null)
			// si paas de marche, l'arrete coute 0
			return 1;

		return quantiteApresEchange(Decimal.ONE).doubleValue();

	}
	
	public String toString (){
        return "(" + super.getSource() + "-> (poids = " + trueWeight()+  ") -> " + super.getTarget()+ ")";
	}

	public Market getmarket(){ return market ; }
	
	public boolean isReversed(){ return reversed;}
	


	public BigDecimal getSellPrice() {
		return priceManager.getSellPrice(market);
	}

	public BigDecimal getBuyPrice() {
		 return priceManager.getBuyPrice(market);
	}
	
	public BigDecimal quantiteApresEchange(BigDecimal quantiteAvant){
		if (!reversed)
			return market.roundPrice(market.subFee(Op.mult(quantiteAvant, priceManager.getSellPrice(market))));
		else
			return market.roundAmount(market.subFee(Op.div(quantiteAvant, priceManager.getBuyPrice(market))));
	}
	
	public BigDecimal quantiteAvantEchange(BigDecimal qteApres){
		
		BigDecimal qteWithoutFee=Op.div(qteApres, market.subFee(Decimal.ONE));
		
		if (!reversed){
			return market.roundAmount(Op.div(qteWithoutFee, priceManager.getSellPrice(market)));
		}
		else{
			return market.roundPrice(Op.mult(qteWithoutFee, priceManager.getBuyPrice(market)));
		}
	}
}
