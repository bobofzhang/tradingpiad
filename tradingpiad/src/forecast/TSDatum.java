package forecast;

import java.math.BigDecimal;

public class TSDatum {
	BigDecimal volume;
	BigDecimal high;// Plus haut prix
	BigDecimal low; // Plus bas prix
	BigDecimal open; // Prix d'ouverture
	BigDecimal close; // Prix de fermeture --> Celui à prendre pour les estimations 

	
	public TSDatum(BigDecimal volume, BigDecimal high,BigDecimal low,BigDecimal open,BigDecimal close){
		this.volume=volume;
		this.high=high;
		this.low=low;
		this.open=open;
		this.close=close;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public BigDecimal getLow() {
		return low;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public BigDecimal getClose() {
		return close;
	}
	
	
}
