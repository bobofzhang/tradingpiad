package arbitrage;

import java.math.BigDecimal;

import market.Market;
import strategies.PriceManager;

public class CompetitivePrice implements PriceManager {

	@Override
	public BigDecimal getBuyPrice(Market market) {
		return market.ticker.buy;
	}

	@Override
	public BigDecimal getSellPrice(Market market) {
		return market.ticker.sell;
	}

}
