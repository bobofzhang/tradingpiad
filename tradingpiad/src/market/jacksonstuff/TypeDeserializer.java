package market.jacksonstuff;

import java.io.IOException;


import market.Type;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;


public class TypeDeserializer extends JsonDeserializer<Type> {

	private String ask="ask";
	
	public TypeDeserializer(boolean inversed){
		if (inversed)
			ask="bid";
	}
	
	public TypeDeserializer(){
		this(false);
	}
	@Override
	public Type deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		if (jp.getText().equals(ask))
			return Type.ASK;
		else
			return Type.BID;
	}
}
