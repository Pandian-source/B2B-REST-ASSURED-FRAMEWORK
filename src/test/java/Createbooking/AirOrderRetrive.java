package Createbooking;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

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

class Order
{
	String GdsBookingReference; String Orderid;
}

public class AirOrderRetrive
{

	@SuppressWarnings("unchecked")
	public static void Retrive() throws IOException, ParseException
	{
		Order O = new Order();
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

		FileReader reader  = new FileReader(Location.BookingResponse);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonTree = objectMapper.readTree(reader);

		O.Orderid = jsonTree.get("OrderViewRS").get("Order").get(0).get("OrderID").asText();

		System.out.println(O.Orderid);

		O.GdsBookingReference = jsonTree.get("OrderViewRS").get("Order").get(0).get("GdsBookingReference").asText();

		System.out.println(O.GdsBookingReference);



		FileReader readerbooking = new FileReader(Location.OrderRetriveRq);

		JSONParser jsonparserbooking = new JSONParser();

		Object jsonStringbooking = jsonparserbooking.parse(readerbooking);

		JSONObject objectbooking = (JSONObject)jsonStringbooking;

		JSONObject OrderRetreiveRQ = (JSONObject) objectbooking.get("OrderRetreiveRQ");

		JSONObject Query = (JSONObject) OrderRetreiveRQ.get("Query");

		Query.get("OrderID"); Query.put("OrderID", O.Orderid);

		Query.get("GdsBookingReference"); Query.put("GdsBookingReference", Arrays.asList(O.GdsBookingReference));

		String jsonrestrive = Headervalue.gson(objectbooking);

		Headervalue.ReplaceRequest(Location.IssueTicketRQ, jsonrestrive);

		RequestSpecification requestSpecification = RestAssured.given();

		requestSpecification = requestSpecification.log().all();

		requestSpecification.baseUri(prop.getProperty("ENDPOINT"));

		requestSpecification.basePath(prop.getProperty("AirOrderRetreive"));

		requestSpecification.body(jsonrestrive);

		requestSpecification.headers(Headervalue.authentication());

		Response response = requestSpecification.post();

		ValidatableResponse validatableResponse = response.then().log().all();

		validatableResponse.statusCode(200);

		Headervalue.Response(Location.OrderRetriveRS, response.asPrettyString());
	}
}
