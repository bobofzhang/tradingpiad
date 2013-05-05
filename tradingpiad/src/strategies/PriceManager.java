package strategies;

import java.math.BigDecimal;

import market.Market;

public interface PriceManager {

	BigDecimal getBuyPrice(Market market);

	BigDecimal getSellPrice(Market market);

}
