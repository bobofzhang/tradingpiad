package market;

import java.math.BigDecimal;

public class Order extends AbstractOrder{
	public String oid;

	
	public Order(BigDecimal price, BigDecimal amount,Type type){
		super(price,amount,type);
	}
	
	public String toString(){
		return"["+price+ "," +amount+","+trade_type.name()+"]";
	}
}
