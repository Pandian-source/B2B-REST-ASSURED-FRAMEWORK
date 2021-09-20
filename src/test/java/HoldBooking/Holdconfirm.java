package HoldBooking;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
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
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

class Holdconfirmvariable
{
	String GdsBookingReference; String OrderStatus; String ResponseorderId; String bookingshoppingResponseId; String Orderid;

	String OfferResponseId; String PrincingofferId;  Float Totalamount; Float HST; Float Total;

	String Firstname; String Lastname; String Birthdate;

}
public class Holdconfirm
{
	@SuppressWarnings("unchecked")
	public static void HoldBookingConfirm() throws IOException, ParseException
	{
		Holdconfirmvariable B = new Holdconfirmvariable();
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

		FileReader reader  = new FileReader(Location.HoldbookingRS);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonTree = objectMapper.readTree(reader);

		B.bookingshoppingResponseId = jsonTree.get("OrderViewRS").get("ShoppingResponseId").asText();

		System.out.println(B.bookingshoppingResponseId);

		B.Orderid = jsonTree.get("OrderViewRS").get("Order").get(0).get("OrderID").asText();

		System.out.println(B.Orderid);

		B.GdsBookingReference = jsonTree.get("OrderViewRS").get("Order").get(0).get("GdsBookingReference").asText();

		System.out.println(B.GdsBookingReference);

		B.Totalamount = (float) jsonTree.get("OrderViewRS").get("Order").get(0).get("TotalPrice").get("EquivCurrencyPrice").floatValue();

		System.out.println(B.Totalamount);

		B.HST = (float) jsonTree.get("OrderViewRS").get("Order").get(0).get("OfferItem").get(0).get("FareDetail")
				.get("Price").get("Taxes").get(4).get("EquivCurrencyPrice").floatValue();


		B.Firstname = jsonTree.get("OrderViewRS").get("DataLists").get("PassengerList").get("Passengers").get(0).get("FirstName").asText();

		System.out.println(B.Firstname);

		B.Lastname = jsonTree.get("OrderViewRS").get("DataLists").get("PassengerList").get("Passengers").get(0).get("LastName").asText();

		System.out.println(B.Lastname);

		B.Birthdate = jsonTree.get("OrderViewRS").get("DataLists").get("PassengerList").get("Passengers").get(0).get("BirthDate").asText();

		System.out.println(B.Birthdate);

        System.out.println(B.HST);

		double Amount = B.Totalamount + B.HST;

		BigDecimal bd = new BigDecimal(Amount).setScale(2, RoundingMode.HALF_UP);
		B.Total = (float) bd.doubleValue();

		System.out.println(B.Total);


		FileReader readerbooking = new FileReader(Location.HoldconfirmRq);

		JSONParser jsonparserbooking = new JSONParser();

		Object jsonStringbooking = jsonparserbooking.parse(readerbooking);

		JSONObject objectbooking = (JSONObject)jsonStringbooking;

		JSONObject OrderChangeRQ = (JSONObject) objectbooking.get("OrderChangeRQ");

		OrderChangeRQ.get("ShoppingResponseId"); OrderChangeRQ.put("ShoppingResponseId", B.bookingshoppingResponseId);

		JSONObject Query = (JSONObject) OrderChangeRQ.get("Query");

		Query.get("OrderID"); Query.put("OrderID", B.Orderid);

		Query.get("GdsBookingReference"); Query.put("GdsBookingReference", Arrays.asList(B.GdsBookingReference));

		JSONObject Payments = (JSONObject) OrderChangeRQ.get("Payments");

		JSONArray Payment = (JSONArray) Payments.get("Payment");

		JSONObject amount = (JSONObject) Payment.get(0);

		amount.get("Amount"); amount.put("Amount", B.Total);

		JSONObject DataLists = (JSONObject) OrderChangeRQ.get("DataLists");

		JSONObject PassengerList = (JSONObject) DataLists.get("PassengerList");

		JSONArray Passenger = (JSONArray) PassengerList.get("Passenger");

		JSONObject Firstname = (JSONObject) Passenger.get(0);

		Firstname.get("FirstName"); Firstname.put("FirstName", B.Firstname);

		Firstname.get("LastName"); Firstname.put("LastName", B.Lastname);

		Firstname.get("BirthDate"); Firstname.put("BirthDate", B.Birthdate);
		

		String confirm = Headervalue.gson(objectbooking);

		Headervalue.ReplaceRequest(Location.HoldconfirmRq, confirm);

		RequestSpecification requestSpecificationbooking = RestAssured.given();

		requestSpecificationbooking = requestSpecificationbooking.log().all();

		requestSpecificationbooking.baseUri(prop.getProperty("ENDPOINT"));

		requestSpecificationbooking.basePath(prop.getProperty("AirOrderChange"));

		requestSpecificationbooking.body(confirm);

		requestSpecificationbooking.headers(Headervalue.authentication());

		Response Responsebooking = requestSpecificationbooking.post();

		ValidatableResponse validatableResponsebooking = Responsebooking.then().log().all();

		validatableResponsebooking.statusCode(200);

		Headervalue.Response(Location.HoldconfirmRS, Responsebooking.asPrettyString());


	}}
