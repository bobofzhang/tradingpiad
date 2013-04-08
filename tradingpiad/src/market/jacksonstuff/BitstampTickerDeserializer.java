package market.jacksonstuff;

import java.io.IOException;
import java.math.BigDecimal;

import market.Ticker;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import utilities.Decimal;

public class BitstampTickerDeserializer extends JsonDeserializer<Ticker> {

	@Override
	public Ticker deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		BigDecimal high, last, buy, vol, low, sell;
		JsonToken t = jp.getCurrentToken();
		if (t.equals(JsonToken.START_OBJECT)) {
			jp.nextValue();
			high = new Decimal(jp.getText());

			jp.nextValue();
			last = new Decimal(jp.getText());
			
			jp.nextValue();
			buy = new Decimal(jp.getText());

			jp.nextValue();
			vol = new Decimal(jp.getText());

			jp.nextValue();
			low = new Decimal(jp.getText());

			jp.nextValue();
			sell = new Decimal(jp.getText());

			jp.nextToken();
			return new Ticker(buy, sell, low, high, vol, last);
		}
		return null;

	}

}
