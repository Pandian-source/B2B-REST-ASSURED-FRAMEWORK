package Createbooking;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import Listenerclass.Headervalue;
import Listenerclass.Location;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

class VariableCancel
{
	String OrderID; String GdsBookingReference; String Ticketnumberone; String Ticketnumbertwo;
}

@Listeners(Listenerclass.TestListeners.class)
public class Voidcancel {

	@SuppressWarnings({ "unchecked" })
	public static void VoidRequest () throws IOException, ParseException
	{
		VariableCancel V = new VariableCancel();
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
		
		FileReader reader  = new FileReader(Location.IssueTicketRS);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonTree = objectMapper.readTree(reader);

		V.OrderID = jsonTree.get("OrderViewRS").get("Order").get(0).get("OrderID").asText();

		System.out.println(V.OrderID);
		
		V.GdsBookingReference = jsonTree.get("OrderViewRS").get("Order").get(0).get("GdsBookingReference").asText();

		System.out.println(V.GdsBookingReference);
		
		V.Ticketnumberone = jsonTree.get("OrderViewRS").get("TicketDocInfos").get("TicketDocInfo").get(0).get("TicketDocument")
				.get("TicketDocNbr").asText();
		
		V.Ticketnumbertwo = jsonTree.get("OrderViewRS").get("TicketDocInfos").get("TicketDocInfo").get(1).get("TicketDocument")
				.get("TicketDocNbr").asText();
		
		FileReader Void = new FileReader(Location.VoidRQ);

		JSONParser jsonparservoid = new JSONParser();

		Object jsonStringvoid = jsonparservoid.parse(Void);

		JSONObject objectvoid = (JSONObject)jsonStringvoid;

		JSONObject AirTicketVoidRQ = (JSONObject) objectvoid.get("AirTicketVoidRQ");

		JSONObject Query = (JSONObject) AirTicketVoidRQ.get("Query");

		Query.get("OrderID");

		Query.put("OrderID", V.OrderID);

		Query.get("GdsBookingReference");

		Query.put("GdsBookingReference", Arrays.asList(V.GdsBookingReference));

		Query.get("TicketNumber");

		Query.put("TicketNumber", Arrays.asList(V.Ticketnumberone,V.Ticketnumbertwo));

        String jsonvoid = Headervalue.gson(objectvoid);

		System.out.println(jsonvoid);

        Headervalue.ReplaceRequest(Location.VoidRQ, jsonvoid);

		RequestSpecification requestSpecification = RestAssured.given();

		requestSpecification = requestSpecification.log().all();

		requestSpecification.baseUri(prop.getProperty("ENDPOINT"));

		requestSpecification.basePath(prop.getProperty("AirTicketVoid"));

		requestSpecification.body(jsonvoid);

		requestSpecification.headers(Headervalue.authentication());

		Response response = requestSpecification.post();

		ValidatableResponse validatableResponse = response.then().log().all();

		validatableResponse.statusCode(200);

		Headervalue.Response(Location.VoidRS, response.asPrettyString());

	}}
