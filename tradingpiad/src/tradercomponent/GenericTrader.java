package tradercomponent;

import java.math.BigDecimal;

import tradercomponent.price.PriceManager;
import tradercomponent.time.TimeManager;
import tradercomponent.volume.VolumeManager;

public class GenericTrader extends TraderSkeleton {
	
	private PriceManager pm;
	private VolumeManager vm;
	private TimeManager tm;
	
	public GenericTrader(PriceManager pm, VolumeManager vm, TimeManager tm){

		this.pm=pm;
		this.vm=vm;
		this.tm=tm;
	}

	@Override
	public boolean shouldBuy() {
		return tm.shouldBuy() && pm.shouldBuy();
	}

	@Override
	public boolean shouldSell() {
		return tm.shouldSell() && pm.shouldSell();
	}

	@Override
	public boolean shouldSellNow() {
		return tm.shouldSellNow();
	}

	@Override
	public BigDecimal getBuyPrice() {
		return pm.getBuyPrice();
	}

	@Override
	public BigDecimal getSellPrice(BigDecimal buyPrice) {
		return pm.getSellPrice(buyPrice);
	}

	@Override
	public BigDecimal getBuyAmount(BigDecimal price) {
		return vm.getBuyAmount(price);
	}

}
