package arbitrage;

import java.math.BigDecimal;

import market.Market;
import market.Order;

import org.jgrapht.graph.DefaultWeightedEdge;

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
	
	
	/**
	 * Cree un arc customise pour le graphe des taux de changes dans la strategie Arbitrage simple.
	 * @param market Le marche sur lequel se fait l'echange hypothetique
	 * @param reversed Le sens de l'arc :
	 *        false si l'echange se fait dans le sens (market.cur1->market.cur2)
	 *        true si l'echange se fait dans le sens (market.cur2 -> market.cur1)
	 */
	public CustomEdge(Market market, boolean reversed){
		this.market=market;
		this.reversed=reversed;
	}

	protected double getWeight() {
		
		if (market ==null)
			// si paas de marche, l'arrete coute 0
			return 0.;
		
		Order[] bids = market.getDepth().bids;
		Order[] asks = market.getDepth().asks;
		
		if (bids.length > 0 && asks.length > 0) {
			
			BigDecimal w;
			
			if (reversed) {
				w = market.roundAmount(Op.div(Decimal.ONE, asks[0].price)); // Si on veut transfromer X cur2 en Y cur1 (et donc Y=X/(prix cur1) et X =1 arbitriarement )
			} 
			
			else {
				w = bids[0].price; // Si on transformer X cur1 en Y cur2 (et donc Y=X*(prix cur1) et X= 1 arbitrairement )
			}
			
			return -Math.log(market.subFee(w).doubleValue());
			// On log le resulte pour rendre les poids des arc additifs et permettre l'utilisation de l'algorithme du plus court chemin
			// On change el signe pour que l'algo trouve le chemin le plus long(= le plus profitable) au lieu du plus court

		} else
			return Double.POSITIVE_INFINITY;
	}
}
