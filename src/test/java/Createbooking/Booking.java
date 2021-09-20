package Createbooking;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

public class Booking 
{

	@SuppressWarnings("unchecked")
	public static void NormalBooking() throws IOException, ParseException
	{
		Bookvariable B = new Bookvariable();
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

        FileReader reader  = new FileReader(Location.PricingResponse);

        ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonTree = objectMapper.readTree(reader);

		B.PricingshoppingResponseId = jsonTree.get("OfferPriceRS").get("ShoppingResponseId").asText();

		System.out.println(B.PricingshoppingResponseId);

		B.OfferResponseId = jsonTree.get("OfferPriceRS").get("OfferResponseId").asText();

		System.out.println(B.OfferResponseId);

		B.PrincingofferId = jsonTree.get("OfferPriceRS").get("PricedOffer").get(0).get("OfferID").asText();

		System.out.println(B.PrincingofferId);

		B.Totalamount = (float) jsonTree.get("OfferPriceRS").get("PricedOffer").get(0).get("TotalPrice").get("EquivCurrencyPrice").floatValue();

		System.out.println(B.Totalamount);

		B.HST = (float) jsonTree.get("OfferPriceRS").get("PricedOffer").get(0).get("OfferItem").get(0).get("FareDetail")
				.get("Price").get("Taxes").get(4).get("EquivCurrencyPrice").floatValue();

		System.out.println(B.HST);

		double Amount = B.Totalamount + B.HST;

		BigDecimal bd = new BigDecimal(Amount).setScale(2, RoundingMode.HALF_UP);
        B.Total = (float) bd.doubleValue();

        System.out.println(B.Total);



        FileReader readerbooking = new FileReader(Location.BookingRequest);

		JSONParser jsonparserbooking = new JSONParser();

		Object jsonStringbooking = jsonparserbooking.parse(readerbooking);

		JSONObject objectbooking = (JSONObject)jsonStringbooking;

		JSONObject BookingRq = (JSONObject) objectbooking.get("OrderCreateRQ");

		BookingRq.get("ShoppingResponseId"); BookingRq.put("ShoppingResponseId", B.PricingshoppingResponseId);

		BookingRq.get("OfferResponseId"); BookingRq.put("OfferResponseId", B.OfferResponseId);

		JSONObject Query = (JSONObject) BookingRq.get("Query");

		JSONObject Order = (JSONObject) Query.get("Order");

		JSONArray jsonArrayBooking = (JSONArray) Order.get("Offer");

		JSONObject OfferidBooking = (JSONObject) jsonArrayBooking.get(0);

		OfferidBooking.get("OfferID"); OfferidBooking.put("OfferID", B.PrincingofferId);

		JSONObject Payments = (JSONObject) BookingRq.get("Payments");

		JSONArray Payment = (JSONArray) Payments.get("Payment");

		JSONObject Paymentarray = (JSONObject) Payment.get(0);

		Paymentarray.get("Amount"); Paymentarray.put("Amount", B.Total);

		String jsonbooking = Headervalue.gson(objectbooking);

		Headervalue.ReplaceRequest(Location.BookingRequest, jsonbooking);

		RequestSpecification requestSpecificationbooking = RestAssured.given();

		requestSpecificationbooking = requestSpecificationbooking.log().all();

		requestSpecificationbooking.baseUri(prop.getProperty("ENDPOINT"));

		requestSpecificationbooking.basePath(prop.getProperty("AirOrderCreate"));

		requestSpecificationbooking.body(jsonbooking);

		requestSpecificationbooking.headers(Headervalue.authentication());

		Response Responsebooking = requestSpecificationbooking.post();

		ValidatableResponse validatableResponsebooking = Responsebooking.then().log().all();

		validatableResponsebooking.statusCode(200);

		Headervalue.Response(Location.BookingResponse, Responsebooking.asPrettyString());

		JsonPath jsonPathEvaluatorpricing = Responsebooking.jsonPath();

		B.ResponseorderId = jsonPathEvaluatorpricing.get("OrderViewRS.Order[0].OrderID");

		System.out.println(B.ResponseorderId);

		B.GdsBookingReference = jsonPathEvaluatorpricing.get("OrderViewRS.Order[0].GdsBookingReference");

		System.out.println(B.GdsBookingReference);

		B.OrderStatus = jsonPathEvaluatorpricing.get("OrderViewRS.Order[0].OrderStatus");

		System.out.println(B.OrderStatus);

	}
}






	/*	JsonNode Readerbooking =B.mapper.readTree(new File(Location.BookingRequest));

        ObjectNode Shopping = (ObjectNode) Readerbooking.get("OrderCreateRQ");

        Shopping.get("ShoppingResponseId");

        Shopping.get("OfferResponseId");

        Shopping.put("ShoppingResponseId", B.PricingshoppingResponseId);

        Shopping.put("OfferResponseId", B.OfferResponseId);

        ObjectNode Offer = (ObjectNode) Readerbooking.get("OrderCreateRQ").get("Query").get("Order").get("Offer").get(0);

        Offer.get("OfferID");

        Offer.put("OfferID",B.PrincingofferId);

        ObjectNode amount = (ObjectNode) Readerbooking.get("OrderCreateRQ").get("Payments").get("Payment").get(0);

        amount.get("Amount");

        amount.put("Amount",B.Total);

        Gson gsonbooking1 = new GsonBuilder().setPrettyPrinting().create();

		String jsonbooking1 = gsonbooking1.toJson(Shopping);

		System.out.println(jsonbooking1); */

















































