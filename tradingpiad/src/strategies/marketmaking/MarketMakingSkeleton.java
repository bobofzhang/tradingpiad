package strategies.marketmaking;

import java.math.BigDecimal;

import market.EndOfRun;
import market.ExchangeError;
import market.Market;
import market.Order;
import market.Type;
import strategies.Agent;
import strategies.Strategy;

/**
 * Classe correspondant a la strategie de MarketMaking.
 * Celle ci consiste a constamment placer des ordres d'achat et de vente dans le cahier des ordres et attendre leur execution.
 * Seul le squelette de la strategie est defini, la maniere dont sont determines les prix d'achat et de vente  ainsi que les quantites "investis" sur le marche est a implementee dans une classe fille.
 */
public abstract class MarketMakingSkeleton implements Strategy{

	@Override
	public void execute(Market[] marketList) throws EndOfRun {
		Market m=marketList[0];
		// Actualisation des informations du marche
		try {
			m.updateAll();
		} catch (ExchangeError e1) {
			System.out.println(e1);
			/* Si une erreur s'est produite (une erreur http qui est de la faute du serveur par exemple)
			alors ne rien faire au temps t courant (on retenhtera de faire quelque chose au prochain temps t+1 ) )*/
			return;
		}
		Agent.removeAllOrder(marketList);
		
		// On vide l'historique des bids execcute car cette strategie ne s'en sert pas
		m.getExecutedBids().clear();
		
		// On recalcule les differentes valeurs utilisees par la strategie
		updateInfos();
		
		// On recupere ecs valeurs
		BigDecimal buyPrice=getBuyPrice(); // Le prix de vente calcule
		BigDecimal sellPrice=getSellPrice(); // Le prix d'achat calcule
		BigDecimal buyAmount=getBuyAmount(); // La  quantite que l'on va peut etre acheter
		BigDecimal sellAmount=getSellAmount(); // La quantite que l'on va peut etre vendre
		/*
		
		System.out.println("ticker.buy="+m.getTicker().buy);
		System.out.println("ticker.sell"+m.getTicker().sell);
		System.out.println("profit prix du marche"+m.getProfit(m.getTicker().sell, m.getTicker().buy));
		System.out.println("buyPrice="+buyPrice);
		System.out.println("sellPrice="+sellPrice);
		System.out.println("buyAmount="+buyAmount);
		System.out.println("sellAmount="+sellAmount);*/
		
		if(sellAmount.compareTo(m.getPricePrecision())>0) // Si on a determine qu'on pouvait encore acheter
			m.addAsk(new Order(sellPrice,sellAmount,Type.ASK));
		if(buyAmount.compareTo(m.getPricePrecision())>0) // Si on a determine qu'on pouvait encore vendre
			m.addBid(new Order(buyPrice, buyAmount, Type.BID));
		
		//System.out.println("Open bids :"+m.getOpenBids());
		//System.out.println("Open asks :"+m.getOpenAsks());
		
		m.waitTimeDelta();// On passe au temps t+1
		
	}
	
	
	/**
	 * Mise a jour des informations.
	 * Ici,on re-calcul certaines informations en fonction de l'etat du marche actuel
	 * Cette methode doit etre invoque avant les methodes getBuyPrice getBuyAmount, getSellPrice et getSellAmount
	 */
	protected abstract void updateInfos();
	
	
	/**
	 * @return Le prix auquel on souhaite vendre lors de la derniere update des informations
	 */
	protected abstract BigDecimal getSellPrice();
	/**
	 * @return Le prix auquel on souhaite acheter lors de la derniere update des informations
	 */
	protected abstract BigDecimal getBuyPrice();
	/**
	 * @return La quantite que l'on souhaite vendre
	 */
	protected abstract BigDecimal getSellAmount();
	/**
	 * @return La quantite que l'on souhaite acheter
	 */
	protected abstract BigDecimal getBuyAmount();

}
