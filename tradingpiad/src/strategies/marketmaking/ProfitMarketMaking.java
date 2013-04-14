package strategies.marketmaking;

import java.math.BigDecimal;

import market.Market;
import market.Type;

import utilities.Assert;
import utilities.Decimal;
import utilities.Op;

/**
 * 
 * Cette classe correpond à une des strategies de trading de type Market Making.
 *
 * Elle agit des deux cotes du cahier des ordres, c'est a dire que, lors de son execution, elle pose constamment a la fois des ordres ventes et des ordres d'achat.
 * 
 * La particularité de cette strategie est qu'elle calcule un prix d'achat et de revente dont la difference (modulo les frais de transactions ) permet de faire un profit.
 * Les ordres ainsi places dans le cahier des ordres ne sont pas les plus competitifs mais sont sur de garantir de faire un profit minimum (ou de ne pas depasser une perte fixe)
 * 
 */
public class ProfitMarketMaking extends MarketMakingSkeleton{
	
	private Market m;
	
	/**
	 * Le profit que l'on souhaite accomplir, permet de fixer le prix d'achat et de evnte de maniere a ceque is on achetait puis on revendait instantanement , on ferait ce profit.
	 */
	private BigDecimal profit;
	
	/**
	 * Le nombre de secondes ou l'on regarde dans le passe pour comparer la quantite de bid para rapport a la quantite de ask 
	 */
	private long nb_sec;
	
	/**
	 * Le pourcentage de notre argent total que l'on souhaite investir a un moment donne 
	 */
	private BigDecimal percentageToInvest;
	
	/**
	 * Poids des ASK
	 */
	private BigDecimal wa; 
	/**
	 *  Poids des BID
	 */
	private BigDecimal wb;
	/* 
	 * REMARQUE :  Si par xemple wa>wb ca signifie que si on regarde nb_sec secondes en arriere, il y a plus de ASK que de BID (en terme de volume)
	 */
	
	
	/**
	 * Prix auquel la strategie veut acheter 
	 */
	private BigDecimal buyPrice;
	
	/**
	 * Prix auquel la straegie veut vendre
	 */
	private BigDecimal sellPrice;
	/**
	 * Quantite que la strategie veut acheter
	 */
	private BigDecimal buyAmount;
	/**
	 * Quantite que la strategie veut vendre
	 */
	private BigDecimal sellAmount;
	
	
	
	/**
	 * @param m Le marche sur lequel la strategie va etre appliquee.
	 * @param profit Le profit que l'on souhaite lors du processus d'achat puis revente. Peut etre negatif.
	 * @param nb_sec Le nombre de seconde ou on regarde en arriere dans l'historique pour estimer la proportionde BID et de ASK.
	 * @param percentageToInvest Le pourcentage de monnaie qu'on possède que l'on souhaite risquer (c'est la meme valeur pour les deux monnaies )
	 */
	public ProfitMarketMaking(Market m, BigDecimal profit,long nb_sec , BigDecimal percentageToInvest){
		Assert.checkPrecond(percentageToInvest.compareTo(Decimal.ONE)<=0 && percentageToInvest.compareTo(Decimal.ZERO)>0, "Le diviseur doit etre superier a 1");
		Assert.checkPrecond(nb_sec>0, "La fenetre de temps doit etre positive");
		this.m=m;
		this.profit=profit;
		this.nb_sec=nb_sec;
		this.percentageToInvest=percentageToInvest;
	}
	
	
	/**
	 * Actualise les différentes valeurs (prix d'achat, de vente, quantites) par rapport aux changements recents du marche. 
	 */
	public void updateInfos(){
		
		// Calcul des poids wa et wb
		setWeight();
		
		/* Remarque sur le calcul du prix de vente et d'achat :
		 * Le prix de d'achat (X1) et le prix de vente (X2) sont calcules en resolvant le systeme suivant :
		 * 	
		 * 		(1) X2*(1-frais)^2-X1=profit
		 * 		(2) (X2-ask)/wa=(bid-X1)/wb
		 * 		
		 * Ce qui donne comme solution :
		 * 
		 * 		(1) X2=(profit+X1)/(1-frais)^2
		 * 		(2) X1=[alpha*(w*bid+ask)]/[alpha*w+1] avec alpha= (1-frais)^2-X1 et w=wa/wb		
		 */
		
		// Calcul du prix d'achat et du prix de vente
		BigDecimal w=Op.div(wa, wb);
		BigDecimal alpha=m.subFee(m.subFee(Decimal.ONE));
		
		BigDecimal bid=m.getDepth().bids.length > 0 ? m.getDepth().bids[0].price : m.getTicker().buy;
		BigDecimal ask=m.getDepth().asks.length > 0 ? m.getDepth().asks[0].price : m.getTicker().sell;
		BigDecimal num=Op.sub(Op.add( Op.mult(Op.mult(alpha, w), bid),Op.mult(alpha, ask)),profit);
		BigDecimal denom=Op.add(Decimal.ONE, Op.mult(alpha, w));
		this.buyPrice= m.roundPrice(Op.div(num, denom));
		
		//Calcul du prix de vente
		this.sellPrice= m.roundPrice(Op.div(Op.add(profit, getBuyPrice()),alpha));
		
		// Calculate de la quantite a vendre
		BigDecimal maxCur1=Op.mult(m.getTotalCur1Amount(), this.percentageToInvest);
		BigDecimal am1=Op.sub(maxCur1, m.getInMarketCur1());
		this.sellAmount= m.roundAmount(am1.min(m.getWallet().getAmount(m.cur1)));
		
		
		//Calcul de la quantite a acheter 
		BigDecimal maxCur2=Op.mult(m.getTotalCur2Amount(), this.percentageToInvest);
		BigDecimal am2=Op.sub(maxCur2, m.getInMarketCur2());
		this.buyAmount =m.roundAmount(Op.div(am2, buyPrice).min(Op.div(m.getWallet().getAmount(m.cur2),buyPrice)));
			
	}
	
	/**
	 * setWeight() permets d'actualiser les poids wa et wb.
	 * Ces 2 poids representent la proportion de BID et de ASK dans l'historique des derniers echanges nb_sec secondes enarriere
	 */
	private void setWeight(){
		BigDecimal tmp_wa=Decimal.ONE;
		BigDecimal tmp_wb=Decimal.ONE;
		
		int i=m.getTrades().size()-1;
		long dateLim= m.getCurrentTime()/1000-nb_sec;
		while(i>=0 && m.getTrades().get(i).date>dateLim){
			if(m.getTrades().get(i).trade_type.equals(Type.ASK))
				tmp_wa=Op.add(tmp_wa, m.getTrades().get(i).amount);
			else
				tmp_wb=Op.add(tmp_wb, m.getTrades().get(i).amount);
			i--;
		}
		
		wa=Op.add(Op.mult(tmp_wa,new Decimal("10")),tmp_wb);// On limite a un rapport de 1/5
		wb=Op.add(Op.mult(tmp_wb,new Decimal("10")),tmp_wa);// On limite a un rapport de 1/5
		
	}
	
	@Override
	protected BigDecimal getBuyPrice() {
		return this.buyPrice;
	}

	@Override
	protected BigDecimal getSellPrice() {
		return this.sellPrice;
	}
	
	@Override
	protected BigDecimal getSellAmount(){
		return this.sellAmount;
	}
	
	@Override
	protected BigDecimal getBuyAmount(){
		return this.buyAmount;
	}

}
