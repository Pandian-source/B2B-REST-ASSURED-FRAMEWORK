package Reschedule;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
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
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

class Order
{
	String orderid; String pnr; String firstname;String lastname;String doc;String fromdate;String todate; String RBD;
}

public class Reschedulesearch
{
	@SuppressWarnings({ "unused", "unchecked" })
	@Test
	public static void Searching () throws IOException,ParseException
	{
		Order A =new Order();
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

		FileReader reader  = new FileReader(Location.RescheduleRetriveRS);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonTree = objectMapper.readTree(reader);

		A.orderid = jsonTree.get("OrderViewRS").get("Order").get(0).get("OrderID").asText();

		A.pnr = jsonTree.get("OrderViewRS").get("Order").get(0).get("GdsBookingReference").asText();

		A.firstname = jsonTree.get("OrderViewRS").get("DataLists").get("PassengerList").get("Passengers").get(0).get("FirstName").asText();

		A.lastname = jsonTree.get("OrderViewRS").get("DataLists").get("PassengerList").get("Passengers").get(0).get("LastName").asText();

		A.doc = jsonTree.get("OrderViewRS").get("TicketDocInfos").get("TicketDocInfo").get(0).get("TicketDocument").get("TicketDocNbr").asText();

		A.fromdate = jsonTree.get("OrderViewRS").get("DataLists").get("FlightSegmentList").get("FlightSegment").get(0).get("Departure").get("Date").asText();

		A.RBD = jsonTree.get("OrderViewRS").get("Order").get(0).get("OfferItem").get(0).get("FareComponent").get(0).get("FareBasis").get("RBD").asText();

		JSONParser jsonparser = new JSONParser();

		FileReader Reader  = new FileReader(Location.RescheduleSearchRQ);

		Object obj = jsonparser.parse(Reader);

		JSONObject requestobject = (JSONObject)obj;

		JSONObject OrderReshopRQ=(JSONObject) requestobject.get("OrderReshopRQ");

		JSONObject Query=(JSONObject) OrderReshopRQ.get("Query");

		Query.get("OrderID");

		Query.put("OrderID", A.orderid);

		Query.get("GdsBookingReference");

		Query.put("GdsBookingReference", Arrays.asList(A.pnr));

		JSONObject Reshop=(JSONObject) Query.get("Reshop");

		JSONObject OrderServicing=(JSONObject) Reshop.get("OrderServicing");

		JSONObject Add=(JSONObject) OrderServicing.get("Add");

		JSONObject FlightQuery =(JSONObject) Add.get("FlightQuery");

		JSONObject OriginDestinations =(JSONObject) FlightQuery.get("OriginDestinations");

		JSONArray  OriginDestination =(JSONArray)OriginDestinations.get("OriginDestination");

		JSONObject PreviousCabinType = (JSONObject) OriginDestination.get(0);

		PreviousCabinType.get("PreviousCabinType"); PreviousCabinType.put("PreviousCabinType", A.RBD);

		JSONObject DataLists =(JSONObject) OrderReshopRQ.get("DataLists");

		JSONObject PassengerList= (JSONObject) DataLists.get("PassengerList");

		JSONArray  jsonarray =(JSONArray)PassengerList.get("Passenger");

		JSONObject passenger = (JSONObject) jsonarray.get(0);

		passenger.get("FirstName");

		passenger.put("FirstName", A.firstname);

		passenger.get("LastName");

		passenger.put("LastName", A.lastname);

		passenger.get("DocumentNumber");

		passenger.put("DocumentNumber", A.doc);

		String jsonrequest = Headervalue.gson(requestobject);

		Headervalue.ReplaceRequest(Location.RescheduleSearchRQ, jsonrequest);

		RequestSpecification requestSpecification = RestAssured.given();

		requestSpecification = requestSpecification.log().all();

		requestSpecification.baseUri(prop.getProperty("ENDPOINT"));

		requestSpecification.basePath(prop.getProperty("AirOrderReshop"));

		requestSpecification.body(jsonrequest);

		requestSpecification.headers(Headervalue.authentication());

		Response response = requestSpecification.post();

		ValidatableResponse validatableResponse = response.then().log().all();

		validatableResponse.statusCode(200);

		JsonPath jsonPathEvaluator = response.jsonPath();

		Headervalue.Createfolder();

		Headervalue.Response(Location.RescheduleSearchRS, response.asPrettyString());

	}}
