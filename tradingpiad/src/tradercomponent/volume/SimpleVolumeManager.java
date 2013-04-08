package tradercomponent.volume;

import java.math.BigDecimal;

import utilities.Decimal;
import utilities.Op;

import market.Market;

public class SimpleVolumeManager implements VolumeManager{
	
	private Market m;
	private BigDecimal ieme;

	public SimpleVolumeManager(Market m, int ieme){
		this.m=m;
		this.ieme=new Decimal(String.valueOf(ieme));
	}

	@Override
	public BigDecimal getBuyAmount(BigDecimal price) {
		BigDecimal amountAllowed=Op.min(Op.div(Op.div(m.getTotalCur2Amount(), ieme),price),m.getWallet().getAmount(m.cur1));
		return Op.sub(amountAllowed, m.getTotalCur1Amount());
	}
}
