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
import utilities.Item;
import utilities.Op;

public class MarketMaking implements Strategy {
	private BigDecimal maxbtc; // Nombre max de bitcoin qu'on veut avoir en possession
	private BigDecimal nieme= new Decimal("4");

	public MarketMaking() {
	}

	@Override
	public void execute(Market m) throws EndOfRun {
		long buyLimitTime, sellLimitTime;
		try {
			m.updateAll();
		} catch (ExchangeError e1) {
			System.out.println(e1);
			return;
		}
		buyLimitTime = m.getStartTime() + ((m.getEndTime() - m.getStartTime())* 98)/ 100;
		sellLimitTime = m.getStartTime() + ((m.getEndTime() - m.getStartTime()) * 99) / 100;

		long currentTime = m.getCurrentTime();
		
		BigDecimal bid = m.getDepth().bids.length>0?m.getDepth().bids[0].price:m.getTicker().buy;
		BigDecimal ask =  m.getDepth().asks.length>0?m.getDepth().asks[0].price:m.getTicker().sell;
		
		BigDecimal buyPrice = Op.add(bid, m.getPricePrecision());
		BigDecimal sellPrice = Op.sub(ask, m.getPricePrecision());
		System.out.println(buyPrice);
		System.out.println(sellPrice);
		
		maxbtc=new Decimal("10");
		BigDecimal buyAmount = Op.sub(maxbtc, m.getTotalCur1Amount()); // Montant qu'on veut acheter (<= � 0 si on veut rien acheter)
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
		System.out.println("profit=" + m.getProfit(sellPrice, buyPrice));
		System.out.println(currentTime + "and" + buyLimitTime + "," + m.getOpenBids().isEmpty() + "," + buyAmountPossible.compareTo(new Decimal(1.0)));
		if (currentTime <= buyLimitTime && m.getOpenBids().isEmpty() && buyAmountPossible.compareTo(new Decimal(1.0)) > 0) {
			System.out.println("buyAmount=" + buyAmountPossible);
			System.out.println("price=" + buyPrice);
			System.out.println("totalprice=" + Op.mult(buyPrice, buyAmountPossible));
			// Si le profit est sup�rier � la precision des prix
			if (m.getProfit(sellPrice, buyPrice).compareTo(Decimal.ZERO) > 0) {
				m.addBid(new Order(buyPrice, buyAmountPossible, Type.BID));
			}
		}

		System.out.println(currentTime + "and" + sellLimitTime);
		// si il reste du temps pour vendre
		if (currentTime <= sellLimitTime && m.getWallet().getAmount(m.cur1).compareTo(BigDecimal.ZERO) > 0) {

			// On parcours chaque bid execut�
			for (Item<Order> item : m.getExecutedBids()) {
				// Si on fait du profit, alors on place un ask order correpondant
				if (m.getProfit(sellPrice, item.e.price).compareTo(BigDecimal.ZERO) > 0) {
					m.addAsk(new Order(sellPrice, item.e.amount, Type.ASK));
					m.getExecutedBids().delete(item);
				}
			}
			// Si il n'y pas plus de temps pour vendre, alors on vends � perte
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
