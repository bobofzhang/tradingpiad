package market.bitstamp;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import market.Currency;
import market.Depth;
import market.ExchangeError;
import market.Market;
import market.Order;
import market.Ticker;
import market.Trade;
import market.Type;
import market.jacksonstuff.BitstampTickerDeserializer;
import market.jacksonstuff.OrderDeserializer;
import market.jacksonstuff.TypeDeserializer;

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
 * Classe implementant la transformation des donnees json en objets java pour la bourse d'echange Bitstamp.
 * Elle defini divers information specifiques a cette bourse (precision des prix, frais de transaction)
 */
public abstract class MarketBitstamp extends Market {

	private ObjectMapper mapper;
	private BigDecimal fee_percent=new Decimal("0.005");

	public MarketBitstamp(Currency cur1, Currency cur2) throws ExchangeError {
		super(cur1, cur2,"bitstamp");
		// Verification si la pair <cur1, cur2> est accepte par l'exchange
		Assert.checkPrecond(cur1 == Currency.BTC && cur2 == Currency.USD, "Bitstamp autorise seulement <BTC,USD>");

		// JSON mapper inialisation
		mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, false);
		
		// Ajout d'un deserializer de Order customise pour passer de "[ prix, amount ]" dans le JSON � "new Order(prix,amount,Type.XXX) en Java"
		SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null));
		testModule.addDeserializer(Order.class, new OrderDeserializer());
		mapper.registerModule(testModule);
		
		// Ajout d'un deserializer de Type customise pour passer de "ask" (resp. "bid") dans le JSON � "Type.ASK" (resp. "Type.BID") en Java
		testModule = new SimpleModule("MyModule2", new Version(1, 0, 0, null));
		testModule.addDeserializer(Type.class, new TypeDeserializer(true));
		mapper.registerModule(testModule);
		
		// Ajout d'un deserializer du Ticker customise car le ticker de bitstamp differe des autres exchanges
		testModule = new SimpleModule("MyModule3", new Version(1, 0, 0, null));
		testModule.addDeserializer(Ticker.class, new BitstampTickerDeserializer());
		mapper.registerModule(testModule);

	}

	@Override
	public void updateTicker() throws ExchangeError {
		try {
			String is = getJsonTicker();
			ticker = mapper.readValue(is, Ticker.class);
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
			depth = mapper.readValue(is, Depth.class);
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
			String is =getJsonTrades();
			last_trades=Util.filterRecentTrade(mapper.readValue(is, Trade[].class), trades); // On conserve uniquement les echanges pas encore "check�s"
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
		return new BigDecimal ("0.01");
	}
	
	public BigDecimal getAmountPrecision(){
		return new BigDecimal ("0.00000001");
	}

	public BigDecimal roundPrice(BigDecimal price){
		return price.setScale(2,RoundingMode.FLOOR);
	}
	
	public BigDecimal roundAmount(BigDecimal amount){
		return amount.setScale(8,RoundingMode.FLOOR);
	}

}
