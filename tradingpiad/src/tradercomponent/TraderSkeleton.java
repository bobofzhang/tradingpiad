package tradercomponent;

import java.math.BigDecimal;

import market.EndOfRun;
import market.ExchangeError;
import market.Market;
import market.Order;
import market.Type;
import strategies.Strategy;
import utilities.Item;
import utilities.Op;

public abstract class TraderSkeleton implements Strategy {

	public abstract boolean shouldBuy();

	public abstract boolean shouldSell();

	public abstract boolean shouldSellNow();

	public abstract BigDecimal getBuyPrice();

	public abstract BigDecimal getSellPrice(BigDecimal buyPrice);

	public abstract BigDecimal getBuyAmount(BigDecimal price);

	@Override
	public void execute(Market m) throws EndOfRun {
		try {
			m.updateAll();
		} catch (ExchangeError e1) {
			return;
		}

		boolean shBuy = shouldBuy();
		boolean shSell = shouldSell();
		boolean shSellNow = shouldSellNow();
		BigDecimal sellNowPrice;
		BigDecimal buyPrice= m.roundPrice(getBuyPrice());
		BigDecimal buyAmount= m.roundAmount(getBuyAmount(buyPrice));
		BigDecimal cur1Amount = m.roundAmount(m.getWallet().getAmount(m.cur1));

		if (shBuy && Op.sup0(buyAmount)) {
			m.addBid(new Order(buyPrice, buyAmount, Type.BID));
		} else {

			for (Item<Order> bidToRemove : m.getOpenBids()) {
				m.cancelOrder(bidToRemove);
			}
		}

		if (shSell) {
			for (Item<Order> item : m.getExecutedBids()) {
				m.addAsk(new Order(getSellPrice(item.e.price), item.e.amount, Type.ASK));
				m.getExecutedBids().delete(item);
			}
		}

		else if (shSellNow && Op.sup0(cur1Amount)) {
			sellNowPrice = Op.sub(m.getDepth().asks.length > 0 ? m.getDepth().asks[0].price : m.getTicker().sell, m.getPricePrecision());

			// On vide les asks
			for (Item<Order> item : m.getOpenAsks())
				m.cancelOrder(item);

			// on fait le solde -> liquidation totale
			Order solde = new Order(sellNowPrice, cur1Amount, Type.ASK);
			m.addAsk(solde);
		}
		
		m.waitTimeDelta();

	}
}
