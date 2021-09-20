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


class Variable
{
	String OrderID; String GdsBookingReference;
}

public class Ticketing 
{

    @SuppressWarnings("unchecked")
	public static void TicketingRequest () throws IOException, ParseException
	{
		Variable V = new Variable();
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
		
		FileReader reader  = new FileReader(Location.BookingResponse);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonTree = objectMapper.readTree(reader);

		V.OrderID = jsonTree.get("OrderViewRS").get("Order").get(0).get("OrderID").asText();

		System.out.println(V.OrderID);

		V.GdsBookingReference = jsonTree.get("OrderViewRS").get("Order").get(0).get("GdsBookingReference").asText();

		System.out.println(V.GdsBookingReference);

		FileReader Issueticket = new FileReader(Location.IssueTicketRQ);

		JSONParser jsonparserticket = new JSONParser();

		Object jsonStringticket = jsonparserticket.parse(Issueticket);

		JSONObject objectticket = (JSONObject)jsonStringticket;

		JSONObject TicketRq = (JSONObject) objectticket.get("AirDocIssueRQ");

		JSONObject Query = (JSONObject) TicketRq.get("Query");

		Query.get("OrderID"); Query.put("OrderID", V.OrderID);
		
		Query.get("GdsBookingReference"); Query.put("GdsBookingReference", Arrays.asList(V.GdsBookingReference));

		String jsonticket = Headervalue.gson(objectticket);

		Headervalue.ReplaceRequest(Location.IssueTicketRQ, jsonticket);

		RequestSpecification requestSpecification = RestAssured.given();

		requestSpecification = requestSpecification.log().all();

		requestSpecification.baseUri(prop.getProperty("ENDPOINT"));

		requestSpecification.basePath(prop.getProperty("AirDocIssue"));

		requestSpecification.body(jsonticket);

		requestSpecification.headers(Headervalue.authentication());

		Response response = requestSpecification.post();

		ValidatableResponse validatableResponse = response.then().log().all();

		validatableResponse.statusCode(200);

		Headervalue.Response(Location.IssueTicketRS, response.asPrettyString());
	}
}
