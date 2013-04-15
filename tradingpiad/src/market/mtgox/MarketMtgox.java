package market.mtgox;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import market.Currency;
import market.Depth;
import market.ExchangeError;
import market.Market;
import market.Order;
import market.Trade;
import market.Type;
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

/** 
 * Classe implementant la transformation des donnees json en objets java pour la bourse d'echange Mtgox.
 * Elle defini aussi divers informations specifiques a cette bourse (precision des prix, frais de transaction,etc..)
 */
public abstract class MarketMtgox extends Market {

	private final static Currency[] currency_list = { Currency.USD, Currency.AUD, Currency.CAD, Currency.CHF, Currency.CNY, Currency.DKK, Currency.EUR, Currency.GBP, Currency.HKD, Currency.JPY, Currency.NZD, Currency.PLN, Currency.RUB, Currency.SEK, Currency.SGD, Currency.THB };

	private ObjectMapper mapper;
	private Decimal fee_percent;

	public MarketMtgox(Currency cur1, Currency cur2) throws ExchangeError {
		super(cur1, cur2, "mtgox");
		fee_percent = new Decimal(0.006);
		mapper = MarketMtgox.produceMapper();

		// Verification si la pair <cur1, cur2> est accepte par l'exchange
		Assert.checkPrecond(cur1.equals(Currency.BTC), "Mtgox n'autorise pas la pair: <" + cur1.name() + "," + cur2.name() + ">");
		Assert.checkPrecond(Arrays.asList(currency_list).contains(cur2), "Mtgox n'autorise pas la pair: <" + cur1.name() + "," + cur2.name() + ">");
	}

	@Override
	public void updateTicker() throws ExchangeError {
		try {
			super.ticker = mapper.readValue(getJsonTicker(), WrapTicker.class).ticker;
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new ExchangeError("JsonParseException: Erreur jackson");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new ExchangeError("JsonMappingException: Erreur jackson");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExchangeError("IOException: Erreur jackson ou recuperation donnees");
		}
	}

	@Override
	public void updateDepth() throws ExchangeError {
		try {
			depth = mapper.readValue(getJsonDepth(), Depth.class);
			depth.reverseBids();
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new ExchangeError("JsonParseException: Erreur jackson");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new ExchangeError("JsonMappingException: Erreur jackson");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExchangeError("IOException: Erreur jackson ou recuperation donnees");
		}
	}

	@Override
	public void updateTrades() throws ExchangeError {
		try {
			super.last_trades = mapper.readValue(getJsonTrades(), Trade[].class);
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new ExchangeError("JsonParseException: Erreur jackson");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new ExchangeError("JsonMappingException: Erreur jackson");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExchangeError("IOException: Erreur jackson ou recuperation donnees");
		}
	}

	public BigDecimal subFee(BigDecimal amount) {
		return Op.mult(Op.sub(Decimal.ONE, fee_percent), amount);
	}

	public BigDecimal getPricePrecision() {
		return new BigDecimal("0.00001");
	}

	public BigDecimal getAmountPrecision() {
		return new BigDecimal("0.00000001");
	}

	public BigDecimal roundPrice(BigDecimal price) {
		return price.setScale(5, RoundingMode.FLOOR);
	}

	public BigDecimal roundAmount(BigDecimal amount) {
		return amount.setScale(8, RoundingMode.FLOOR);
	}

	/**
	 * @param cur1 La premiere monnaie
	 * @param cur2 La seconde monnaie
	 * @return
	 */
	public static ObjectMapper produceMapper() {

		// JSON mapper inialisation
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, false);

		// Ajout d'un deserializer customise de Order pour passer de
		// "[ prix, amount ]" dans le JSON �
		// "new Order(prix,amount,Type.XXX) en Java"
		SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null));
		testModule.addDeserializer(Order.class, new OrderDeserializer());
		mapper.registerModule(testModule);

		// Ajout d'un deserializer de Type customise pour passer de "ask" (resp.
		// "bid") dans le JSON � "Type.ASK" (resp. "Type.BID") en Java
		testModule = new SimpleModule("MyModule2", new Version(1, 0, 0, null));
		testModule.addDeserializer(Type.class, new TypeDeserializer(true));// Inversed = true (pour l'instant si mtgox change pas encore..)
		mapper.registerModule(testModule);

		

		return mapper;
	}

}
