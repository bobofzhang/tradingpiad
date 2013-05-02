package arbitrage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import market.EndOfRun;
import market.Market;
import market.Order;
import market.Type;
import strategies.Strategy;
import utilities.Decimal;
import utilities.Op;
import utilities.PosInf;

public class StrategyArbitrage implements Strategy {
	
	
	List<Order> listOrders ;
	
	@Override
	public void execute(Market[] m) throws EndOfRun {
		
	}
	
	public BigDecimal gain (List<CustomEdge> listEdges){
		
		Market market ;
		BigDecimal quantity = Decimal.ONE;
		BigDecimal w = BigDecimal.ZERO ;
		
		for (CustomEdge edge : listEdges){
			
			market = edge.getmarket();
			
			Order[] bids = market.getDepth().bids;
			Order[] asks = market.getDepth().asks;
			
				
			if (edge.getReversed()) {
				w = market.roundAmount(Op.div(Decimal.ONE, asks[0].price)); // Si on veut transfromer X cur2 en Y cur1 (et donc Y=X/(prix cur1) et X =1 arbitriarement )
			} 
			else {
				w = bids[0].price; // Si on transformer X cur1 en Y cur2 (et donc Y=X*(prix cur1) et X= 1 arbitrairement )
			}
			quantity = Op.mult(quantity, w);
				
		}	
		return quantity ; 
	}
	
	
	public BigDecimal getInitialQuantity (List<CustomEdge> listEdges){
		
		BigDecimal initialQuantity = new PosInf();
		Market market ; 
		
		for (CustomEdge edge : listEdges){
			market = edge.getmarket();
			
			Order[] bids = market.getDepth().bids;
			Order[] asks = market.getDepth().asks;
			
				
			if (!edge.getReversed()) {
				initialQuantity = initialQuantity.min(bids[0].amount);
			} 
			else {
				initialQuantity = Op.mult(initialQuantity.min(asks[0].amount), asks[0].price);
			}				
		}	
		
		return initialQuantity ; 
		
	}
	
	
	public ArrayList<Order> addOrders (BigDecimal initialQuantity , List<CustomEdge> listEdges){
	
		BigDecimal quantity = initialQuantity ; 
		
		Market market ;
		
		ArrayList<Order> array = new ArrayList<Order>();
		
		for (CustomEdge edge : listEdges){
			
			market = edge.getmarket();
			Order[] bids = market.getDepth().bids;
			Order[] asks = market.getDepth().asks;
			
			
			
			if (!edge.getReversed()){
				
				//edge.getmarket().addBidLimitOrder(bids[0].price ,quantity);				
				array.add(new Order(bids[0].price,quantity,Type.BID));
				quantity = market.roundPrice(Op.mult(quantity,bids[0].price ));
				
			}else{
				
				quantity = market.roundAmount(Op.div(quantity, asks[0].price));
				//edge.getmarket().addAskLimitOrder(asks[0].price , quantity);
				array.add(new Order(bids[0].price,quantity,Type.ASK));
				
			}
		
		}
		
		
		return array ;
		
	}
	
	
}
