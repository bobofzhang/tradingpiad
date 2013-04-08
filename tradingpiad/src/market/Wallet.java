package market;

import java.math.BigDecimal;

import utilities.Assert;
import utilities.Op;


public class Wallet {

	public BigDecimal[] wallet;
	
	public Wallet(){
		wallet =new BigDecimal [Currency.values().length];
		for(int i=0; i< wallet.length;i++)
			wallet[i]=BigDecimal.ZERO;
	}
	
	public BigDecimal getAmount(Currency c){
		return wallet[c.ordinal()];
	}
	
	public void setAmount(Currency c, BigDecimal amount){
		BigDecimal new_amount=Op.add(wallet[c.ordinal()],amount);
		Assert.checkPrecond(new_amount.compareTo(BigDecimal.ZERO)>=0,"Forbidden action: Not enough "+c+" in the wallet");
		wallet[c.ordinal()]=new_amount;
		
	}
	
	public String toString(){
		StringBuffer buf= new StringBuffer();
		buf.append("{");
		for(Currency c: Currency.values())
			buf.append(c+":"+wallet[c.ordinal()]+", ");
		buf.append("}");
		return buf.toString();
	}
}
