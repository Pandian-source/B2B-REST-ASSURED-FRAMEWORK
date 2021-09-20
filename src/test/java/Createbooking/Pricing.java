package Createbooking;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import Listenerclass.Headervalue;
import Listenerclass.Location;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;


class Airpricing
{
	Object ShoppingResponseIdvalue; Object offerid; String PricingshoppingResponseId; String OfferResponseId;

	String PrincingofferId; Float Totalamount; Float HST; Float amount; Float Total; String ResponseorderId;
}

public class Pricing
{
	@SuppressWarnings("unchecked")
	public static void pricing() throws IOException, ParseException
	{
		Airpricing P = new Airpricing();
		Properties prop = new Properties();

		try
		{
			FileInputStream Input = new FileInputStream(Location.ConfigFilePath);

			prop.load(Input);

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}

		FileReader reader  = new FileReader(Location.SearchResponse);

        ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonTree = objectMapper.readTree(reader);

		P.ShoppingResponseIdvalue = jsonTree.get("AirShoppingRS").get("ShoppingResponseId").asText();

		System.out.println(P.ShoppingResponseIdvalue);

		P.offerid = jsonTree.get("AirShoppingRS").get("OffersGroup").get("AirlineOffers").get(0).get("Offer")
				.get(0).get("OfferID").asText();

		System.out.println(P.offerid);
		


     	FileReader readerprice  = new FileReader(Location.PrincingRequest);

		JSONParser jsonparserprice = new JSONParser();

		Object jsonString = jsonparserprice.parse(readerprice);

		JSONObject objectprice = (JSONObject)jsonString;

		JSONObject offerpriceRq = (JSONObject) objectprice.get("OfferPriceRQ");

		offerpriceRq.get("ShoppingResponseId");	offerpriceRq.put("ShoppingResponseId", P.ShoppingResponseIdvalue);

		JSONObject query = (JSONObject) offerpriceRq.get("Query");

		JSONArray jsonArray = (JSONArray) query.get("Offer");

		JSONObject Offerid = (JSONObject) jsonArray.get(0);

		Offerid.get("OfferID"); Offerid.put("OfferID", P.offerid);

		String jsonprice = Headervalue.gson(objectprice);

		Headervalue.ReplaceRequest(Location.PrincingRequest, jsonprice);

		RequestSpecification requestSpecificationpricing = RestAssured.given();

		requestSpecificationpricing = requestSpecificationpricing.log().all();

		requestSpecificationpricing.baseUri(prop.getProperty("ENDPOINT"));

		requestSpecificationpricing.basePath(prop.getProperty("AirOfferPrice"));

		requestSpecificationpricing.body(jsonprice);

		requestSpecificationpricing.headers(Headervalue.authentication());

		Response responsepricing = requestSpecificationpricing.post();

		ValidatableResponse validatableResponsepricing = responsepricing.then().log().all();

		validatableResponsepricing.statusCode(200);

		Headervalue.Response(Location.PricingResponse, responsepricing.asPrettyString());

		JsonPath jsonPathEvaluatorpricing = responsepricing.jsonPath();

		P.PricingshoppingResponseId = jsonPathEvaluatorpricing.get("OfferPriceRS.ShoppingResponseId");

		System.out.println(P.PricingshoppingResponseId);

		P.OfferResponseId = jsonPathEvaluatorpricing.get("OfferPriceRS.OfferResponseId");

		System.out.println(P.OfferResponseId);

		P.PrincingofferId = jsonPathEvaluatorpricing.get("OfferPriceRS.PricedOffer[0].OfferID");

		System.out.println(P.PrincingofferId);

		P.Totalamount = jsonPathEvaluatorpricing.get("OfferPriceRS.PricedOffer[0].TotalPrice.BookingCurrencyPrice");

		System.out.println(P.Totalamount);

		P.HST = jsonPathEvaluatorpricing.get("OfferPriceRS.PricedOffer[0].OfferItem[0].FareDetail.Price.Taxes[4].BookingCurrencyPrice");

		System.out.println(P.HST);

	}


}
