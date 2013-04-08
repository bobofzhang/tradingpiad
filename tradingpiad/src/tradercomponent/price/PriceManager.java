package tradercomponent.price;

import java.math.BigDecimal;

public interface PriceManager {
	public BigDecimal getBuyPrice();
	public BigDecimal getSellPrice(BigDecimal buyPrice);
	public boolean shouldBuy();
	public boolean shouldSell();
}
