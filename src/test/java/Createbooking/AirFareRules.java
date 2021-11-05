package Createbooking;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import Listenerclass.Headervalue;
import Listenerclass.Location;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

class AirFarevariable {

    String ShoppingResponseId; String Offer;
}


public class AirFareRules 
{
	
	@SuppressWarnings("unchecked")
	@Test
	public static void FareRules () throws IOException, ParseException
	{
		AirFarevariable V = new AirFarevariable();
       Properties prop = new Properties();
		try
		{
			FileInputStream ip = new FileInputStream(Location.ConfigFilePath);
			prop.load(ip);

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		
		FileReader reader  = new FileReader(Location.SearchResponse);

        ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonTree = objectMapper.readTree(reader);

		V.ShoppingResponseId = jsonTree.get("AirShoppingRS").get("ShoppingResponseId").asText();

		System.out.println(V.ShoppingResponseId);

		V.Offer = jsonTree.get("AirShoppingRS").get("OffersGroup").get("AirlineOffers").get(0).get("Offer")
				.get(0).get("OfferID").asText();

		System.out.println(V.Offer);
		
		
		
		FileReader readerprice  = new FileReader(Location.FareRuleRequest);

		JSONParser jsonparserprice = new JSONParser();

		Object jsonString = jsonparserprice.parse(readerprice);

		JSONObject objectFare = (JSONObject)jsonString;

		JSONObject FareRulesRQ = (JSONObject) objectFare.get("FareRulesRQ");
		
		JSONObject Query = (JSONObject) FareRulesRQ.get("Query");
	    
		JSONArray jsonArray = (JSONArray) Query.get("Offer");
		
		JSONObject Offerid = (JSONObject) jsonArray.get(0);
		
		JSONObject attributes = (JSONObject) Offerid.get("attributes");
	
		attributes.get("ShoppingResponseId"); attributes.put("ShoppingResponseId", V.ShoppingResponseId);
		
		attributes.get("OfferID"); attributes.put("OfferID", V.Offer);
		
		String jsonFare = Headervalue.gson(objectFare);
		
		Headervalue.ReplaceRequest(Location.FareRuleRequest, jsonFare);
		
		RequestSpecification requestSpecificationpricing = RestAssured.given();

		requestSpecificationpricing = requestSpecificationpricing.log().all();

		requestSpecificationpricing.baseUri(prop.getProperty("ENDPOINT"));

		requestSpecificationpricing.basePath(prop.getProperty("AirFareRules"));

		requestSpecificationpricing.body(jsonFare);

		requestSpecificationpricing.headers(Headervalue.authentication());

		Response responsepricing = requestSpecificationpricing.post();

		ValidatableResponse validatableResponsepricing = responsepricing.then().log().all();

		validatableResponsepricing.statusCode(200);

		Headervalue.Response(Location.FareRuleResponse, responsepricing.asPrettyString());
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
}
}