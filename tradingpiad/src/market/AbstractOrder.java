package market;

import java.math.BigDecimal;


public class AbstractOrder {
	
	public BigDecimal price;
	public BigDecimal amount;
	public Type trade_type;
	
	public AbstractOrder(BigDecimal price, BigDecimal amount, Type trade_type) {
		this.price=price;
		this.amount=amount;
		this.trade_type=trade_type;
	}
	
	public AbstractOrder(){};
	
	public int compareTo(AbstractOrder o){
		return this.price.compareTo(o.price);
	}

}
