package market.jacksonstuff;


import java.io.IOException;

import market.Order;
import market.Type;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import utilities.Decimal;



/**
 * Classe pour deserialiser un tableau "[prix,qte]" en objet Order.
 * 
 */
public class OrderDeserializer extends JsonDeserializer<Order>{

	@Override
	public Order deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Decimal price;
		Decimal amount;
		Type type;
		String str_type;
		
		JsonToken t = jp.getCurrentToken();
		if (t.equals(JsonToken.START_ARRAY)){
			
			// On determine si l'ordre est un bid ou un ask
			str_type=jp.getParsingContext().getParent().getParent().getCurrentName();
			if (str_type.equals("bids"))
				type=Type.BID;
			else
				type=Type.ASK;
			
			// On recupere le prix
			jp.nextToken();
			price=new Decimal(jp.getText());
			
			// On recupere la quantit�
			jp.nextToken();
			amount=new Decimal(jp.getText());
			
			// On sort du tableau
			jp.nextToken();
			
			
			// On retourne l'objet java
			return new Order(price,amount,type);
		}
		return null;
		
	}
}
