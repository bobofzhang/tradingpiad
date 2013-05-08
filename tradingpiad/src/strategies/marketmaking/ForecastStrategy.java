package strategies.marketmaking;

import java.math.BigDecimal;
import java.util.Iterator;

import market.EndOfRun;
import market.ExchangeError;
import market.Market;
import market.Order;
import market.Type;
import strategies.Strategy;
import utilities.Decimal;
import utilities.Forecast;
import utilities.Item;
import utilities.Op;

/**
 * Strategie d'achat-vente avec prediction sur l'evolution des prix.
 * La methode de prediction consiste a compter les montes et les descentes sur la serie temporelle des prix (voir classe TimeSerie).
 * S'il y a beaucoup de montes , on estime que le prix risque de monter, s'il y a beaucoup de descentes , on estime qu'il va descendre.
 * 
 * Si le prix monte on achete, s'il descend vont descendre on vend
 * 
 * A la fin on vend quand meme pour finir avec que de l'argent (et plus aucun de bitcoins)
 *
 */
public class ForecastStrategy implements Strategy{

	private BigDecimal maxbtc;
	
	private Forecast forecast ; 
	private int start_index = 0  ; 
	private int window_size = 10 ; 
	private long buyLimitTime, sellLimitTime;
	
	
	public ForecastStrategy(BigDecimal maxbtc, int window_size) {
		this.maxbtc = maxbtc;
		this.window_size=window_size;
	}
	
	
	
	
	@Override
	public void execute(Market[] marketList) throws EndOfRun {	
		Market m=marketList[0];
		forecast = new Forecast(m.getTs());
		forecast.calcul_rt(window_size);
		
		try {
			m.updateAll();
		} catch (ExchangeError e1) {
			System.out.println(e1);
			return;
		}
		
		buyLimitTime = m.getStartTime() + ((m.getEndTime() - m.getStartTime())*7) / 10;
		sellLimitTime = m.getStartTime() + ((m.getEndTime() - m.getStartTime()) * 9) / 10;
		
		long currentTime = m.getCurrentTime();
		
		
		BigDecimal bid = m.getDepth().bids.length>0?m.getDepth().bids[0].price:m.getTicker().buy;
		BigDecimal ask =  m.getDepth().asks.length>0?m.getDepth().asks[0].price:m.getTicker().sell;
		
		
		BigDecimal buyPrice = Op.add(bid, m.getPricePrecision());
		BigDecimal sellPrice = Op.sub(ask, m.getPricePrecision());
		
		
		BigDecimal buyAmount = Op.sub(maxbtc, m.getWallet().getAmount(m.cur1)); // Montant qu'on veut acheter (<= 0 si on veut rien acheter)
		System.out.println("buyamount="+buyAmount);
		BigDecimal buyAmountPossible;
		
	
		Iterator<Item<Order>> it = m.getOpenBids().iterator();
		// Parcourir la liste pour retirer ( supprimer ) les bid order qui ne sont plus profitables
		while (it.hasNext()) {
			Item<Order> bidToRemove = it.next();
			m.cancelOrder(bidToRemove);
		}
		
		
		// Minimum entre le montant qu'on veut acheter et celui qu'on peut acheter
		buyAmountPossible = m.roundAmount(Op.min(buyAmount, Op.div(m.getWallet().getAmount(m.cur2), buyPrice)));
		
		// Si il reste du temps pour acheter et si le montant est positif
		System.out.println(currentTime + "and" + buyLimitTime + "," + m.getOpenBids().isEmpty() + "," +"val="+buyAmountPossible+" | "+ buyAmountPossible.compareTo(m.getAmountPrecision()));
		

		
		if (currentTime <= buyLimitTime && m.getOpenBids().isEmpty() && buyAmountPossible.compareTo(m.getAmountPrecision()) > 0) {
			
			System.out.println("buyAmount=" + buyAmountPossible);
			System.out.println("price=" + buyPrice);
			System.out.println("totalprice=" + Op.mult(buyPrice, buyAmountPossible));
			
			
			// Si le profit est superieur a la precision des prix
			//if (m.getProfit(sellPrice, buyPrice).compareTo(Decimal.ZERO) > 0) {
				//m.addBid(new Order(buyPrice, buyAmountPossible, Type.BID));
			//}
			
			//ici j'appele la methode calcul_rt de Forecast
			//forecast.calcul_rt(start_index, window_size);
			
			//forecast.calcul_rt( window_size);
			if (forecast.is_forecast_positif() == true) {
				
				System.out.println("************************************************************"+forecast.is_forecast_positif());
				m.addBid(new Order(buyPrice, buyAmountPossible, Type.BID));
				//start_index += window_size ; 
			}
			
			
		}

		System.out.println(currentTime + "and" + sellLimitTime);
		// si il reste du temps pour vendre
		if (currentTime <= sellLimitTime && m.getWallet().getAmount(m.cur1).compareTo(m.getAmountPrecision()) > 0) {

			// On parcours chaque bid execute
			for (Item<Order> item : m.getExecutedBids()) {
			
				// Si on fait du profit, alors on place un ask order correspondant
				if (forecast.is_forecast_negatif()== true) {
					m.addAsk(new Order(sellPrice, item.e.amount, Type.ASK));
					m.getExecutedBids().delete(item);
					//
					start_index += window_size ; 
				}
				
				

				
			}
			
			
			// Si il n'y pas plus de temps pour vendre, alors on vends a perte
		} else if (m.getWallet().getAmount(m.cur1).compareTo(BigDecimal.ZERO) > 0) {
			// On vide les asks
			for (Item<Order> item : m.getOpenAsks())
				m.cancelOrder(item);

			// on fait le solde -> liquidation totale
			Order solde = new Order(sellPrice, m.roundAmount(m.getWallet().getAmount(m.cur1)), Type.ASK);
			m.addAsk(solde);
		}
		
		
		
		
		System.out.println("your bids" + m.getOpenBids());
		System.out.println("your asks" + m.getOpenAsks());
		System.out.println("your executedbids" + m.getExecutedBids());
		System.out.println("end");
		m.waitTimeDelta();

		
	}

}
