package market.btce;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;

import market.Currency;
import market.Depth;
import market.ExchangeError;
import market.Market;
import market.Order;
import market.Trade;
import market.Type;
import market.jacksonstuff.Fee;
import market.jacksonstuff.OrderDeserializer;
import market.jacksonstuff.TypeDeserializer;
import market.jacksonstuff.WrapTicker;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import utilities.Assert;
import utilities.Decimal;
import utilities.Op;
import utilities.Util;


/** 
 * Classe implementant la transformation des donnees json en objets java pour la bourse d'echange Btce.
 * Elle defini divers informations specifiques a cette bourse (precision des prix, frais de transaction)
 */
public abstract class MarketBtce extends Market {

	private ObjectMapper mapper;
	
	private BigDecimal fee_percent;

	public MarketBtce(Currency cur1, Currency cur2) throws ExchangeError {
		super(cur1, cur2,"btce");

		// JSON mapper inialisation
		mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, false);
		
		// Ajout d'un deserializer d'ordre customis� pour passer de "[ prix, amount ]" dans le json � "new Order(prix,amount,Type.XXX) en java"
		SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null));
		testModule.addDeserializer(Order.class, new OrderDeserializer());
		mapper.registerModule(testModule);
		
		// Ajout d'un deserializer des types customis� pour passer de "ask"(resp. "bid") dans le json � "Type.ASK" (resp. "Type.BID") en java
		testModule = new SimpleModule("MyModule2", new Version(1, 0, 0, null));
		testModule.addDeserializer(Type.class, new TypeDeserializer(true));
		mapper.registerModule(testModule);
		
		// Verification si la pair <cur1, cur2> est accepte par l'exchange
				URL fee_url;
				Fee f;
				try {
					fee_url = new URL("https://btc-e.com/api/2/" + cur1.name().toLowerCase() + "_" + cur2.name().toLowerCase() + "/fee");
					String is = Util.getData(fee_url);
					f = mapper.readValue(is, Fee.class);
					Assert.checkPrecond(f.error == null, "BTC-E n'autorise pas la pair: <" + cur1.name() + "," + cur2.name() + ">");
					// On set les frais de transaction maintenant qu'on est sur que Btc-e autorise la paire <cur1,cur2> 
					fee_percent = Op.mult(f.trade,new Decimal("0.01"));
				} catch (JsonParseException e) {
					e.printStackTrace();
					throw new ExchangeError("JsonParseException: Erreur jackson");
				} catch (JsonMappingException e) {
					e.printStackTrace();
					throw new ExchangeError("JsonMappingException: Erreur jackson");
				} catch (IOException e) {
					e.printStackTrace();
					throw new ExchangeError("IOException: Erreur jackson");
				}
	}

	@Override
	public void updateTicker() throws ExchangeError {
		try {
			String is = getJsonTicker();
			ticker = mapper.readValue(is, WrapTicker.class).ticker;

			// On inverse buy et sell car btc-e les inverse par rapport � mtgox
			BigDecimal tmp = ticker.sell;
			ticker.sell = ticker.buy;
			ticker.buy = tmp;
			
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new ExchangeError("JsonParseException: Erreur jackson");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new ExchangeError("JsonMappingException: Erreur jackson");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExchangeError("IOException: Erreur jackson");
		}
	}

	@Override
	public void updateDepth() throws ExchangeError {
		try {
			String is = getJsonDepth();
			super.depth = mapper.readValue(is, Depth.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new ExchangeError("JsonParseException: Erreur jackson");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new ExchangeError("JsonMappingException: Erreur jackson");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExchangeError("IOException: Erreur jackson");
		}
	}

	@Override
	public void updateTrades() throws ExchangeError {
		try {

			String is = getJsonTrades();
			last_trades=Util.filterRecentTrade(mapper.readValue(is, Trade[].class), trades); // On conserve uniquement les echanges pas encore check�s

		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new ExchangeError("JsonParseException: Erreur jackson");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new ExchangeError("JsonMappingException: Erreur jackson");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExchangeError("IOException: Erreur jackson");
		}
	}
	
	public BigDecimal subFee(BigDecimal amount){
		return Op.mult(Op.sub(Decimal.ONE,fee_percent),amount);
	}
	
	public BigDecimal getPricePrecision(){
		return new BigDecimal ("0.00000001");
	}
	
	public BigDecimal getAmountPrecision(){
		return new BigDecimal ("0.00000001");
	}
	
	public BigDecimal roundPrice(BigDecimal price){
		return price.setScale(8,RoundingMode.HALF_UP);
	}
	
	public BigDecimal roundAmount(BigDecimal amount){
		return amount.setScale(8,RoundingMode.HALF_UP);
	}

}
