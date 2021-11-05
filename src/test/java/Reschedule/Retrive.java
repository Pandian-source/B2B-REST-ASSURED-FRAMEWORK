package Reschedule;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.io.FileReader;

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

class HoldVariable
{
	String OrderID; String GdsBookingReference;
}

public class Retrive 
{
	@SuppressWarnings({ "unused", "unchecked" })
	public static void RERetrive () throws IOException,ParseException
	{
	   HoldVariable V = new HoldVariable();
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
		
		FileReader reader  = new FileReader(Location.HoldIssueTicketRS);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonTree = objectMapper.readTree(reader);

		V.OrderID = jsonTree.get("OrderViewRS").get("Order").get(0).get("OrderID").asText();

		System.out.println(V.OrderID);

		V.GdsBookingReference = jsonTree.get("OrderViewRS").get("Order").get(0).get("GdsBookingReference").asText();

		System.out.println(V.GdsBookingReference);
		
		FileReader Issueticket = new FileReader(Location.RescheduleRetriveRQ);
		
		JSONParser jsonparserticket = new JSONParser();

		Object jsonStringticket = jsonparserticket.parse(Issueticket);

		JSONObject objectticket = (JSONObject)jsonStringticket;

		JSONObject TicketRq = (JSONObject) objectticket.get("OrderRetreiveRQ");

		JSONObject Query = (JSONObject) TicketRq.get("Query");

		Query.get("OrderID"); Query.put("OrderID", V.OrderID);
		
		Query.get("GdsBookingReference"); Query.put("GdsBookingReference", Arrays.asList(V.GdsBookingReference));

		String jsonticket = Headervalue.gson(objectticket);
		
		Headervalue.ReplaceRequest(Location.RescheduleRetriveRQ, jsonticket);

		RequestSpecification requestSpecification = RestAssured.given();

		requestSpecification = requestSpecification.log().all();

		requestSpecification.baseUri(prop.getProperty("ENDPOINT"));

		requestSpecification.basePath(prop.getProperty("AirOrderRetreive"));

		requestSpecification.body(jsonticket);

		requestSpecification.headers(Headervalue.authentication());

		Response response = requestSpecification.post();

		ValidatableResponse validatableResponse = response.then().log().all();

		validatableResponse.statusCode(200);

		JsonPath jsonPathEvaluator = response.jsonPath();

		Headervalue.Createfolder();

		Headervalue.Response(Location.RescheduleRetriveRS, response.asPrettyString());
}}
