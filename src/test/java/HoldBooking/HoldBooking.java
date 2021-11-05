package HoldBooking;

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

class Bookvariable
{
	String GdsBookingReference; String OrderStatus; String ResponseorderId; String PricingshoppingResponseId;

	String OfferResponseId; String PrincingofferId;  Float Totalamount; Float HST; Float Total;

}

public class HoldBooking
{
    @SuppressWarnings("unchecked")
	public static void HoldBookingCreate() throws IOException, ParseException
	{

		Bookvariable V = new Bookvariable();
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

		FileReader reader  = new FileReader(Location.HoldpriceRS);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonTree = objectMapper.readTree(reader);

		V.PricingshoppingResponseId = jsonTree.get("OfferPriceRS").get("ShoppingResponseId").asText();

		System.out.println(V.PricingshoppingResponseId);

		V.OfferResponseId = jsonTree.get("OfferPriceRS").get("OfferResponseId").asText();

		System.out.println(V.OfferResponseId);

		V.PrincingofferId = jsonTree.get("OfferPriceRS").get("PricedOffer").get(0).get("OfferID").asText();

		System.out.println(V.PrincingofferId);

		V.Totalamount = (float) jsonTree.get("OfferPriceRS").get("PricedOffer").get(0).get("TotalPrice").get("EquivCurrencyPrice").floatValue();

		System.out.println(V.Totalamount);

		V.HST = (float) jsonTree.get("OfferPriceRS").get("PricedOffer").get(0).get("OfferItem").get(0).get("FareDetail")
				.get("Price").get("Taxes").get(4).get("EquivCurrencyPrice").floatValue();

		System.out.println(V.HST);


		FileReader readerbooking = new FileReader(Location.HoldbookingRq);

		JSONParser jsonparserbooking = new JSONParser();

		Object jsonStringbooking = jsonparserbooking.parse(readerbooking);

		JSONObject objectbooking = (JSONObject)jsonStringbooking;

		JSONObject BookingRq = (JSONObject) objectbooking.get("OrderCreateRQ");

		BookingRq.get("ShoppingResponseId"); BookingRq.put("ShoppingResponseId", V.PricingshoppingResponseId);

		BookingRq.get("OfferResponseId"); BookingRq.put("OfferResponseId", V.OfferResponseId);

		JSONObject Query = (JSONObject) BookingRq.get("Query");

		JSONObject Order = (JSONObject) Query.get("Order");

		JSONArray jsonArrayBooking = (JSONArray) Order.get("Offer");

		JSONObject OfferidBooking = (JSONObject) jsonArrayBooking.get(0);

		OfferidBooking.get("OfferID"); OfferidBooking.put("OfferID", V.PrincingofferId);

		String jsonbooking = Headervalue.gson(objectbooking);

		Headervalue.ReplaceRequest(Location.HoldbookingRq, jsonbooking);

		RequestSpecification requestSpecificationbooking = RestAssured.given();

		requestSpecificationbooking = requestSpecificationbooking.log().all();

		requestSpecificationbooking.baseUri(prop.getProperty("ENDPOINT"));

		requestSpecificationbooking.basePath(prop.getProperty("AirOrderCreate"));

		requestSpecificationbooking.body(jsonbooking);

		requestSpecificationbooking.headers(Headervalue.authentication());

		Response Responsebooking = requestSpecificationbooking.post();

		ValidatableResponse validatableResponsebooking = Responsebooking.then().log().all();

		validatableResponsebooking.statusCode(200);

		Headervalue.Response(Location.HoldbookingRS, Responsebooking.asPrettyString());

		JsonPath jsonPathEvaluatorpricing = Responsebooking.jsonPath();

		V.ResponseorderId = jsonPathEvaluatorpricing.get("OrderViewRS.Order[0].OrderID");

		System.out.println(V.ResponseorderId);

		V.GdsBookingReference = jsonPathEvaluatorpricing.get("OrderViewRS.Order[0].GdsBookingReference");

		System.out.println(V.GdsBookingReference);

		V.OrderStatus = jsonPathEvaluatorpricing.get("OrderViewRS.Order[0].OrderStatus");

		System.out.println(V.OrderStatus);


	}
}



