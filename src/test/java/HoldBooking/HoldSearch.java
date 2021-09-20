package HoldBooking;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Listenerclass.Headervalue;
import Listenerclass.Location;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;


class Variable {

	String ShoppingResponseId; String Offer; 
}
public class HoldSearch
{
	public static void HoldSearching () throws IOException, ParseException
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

		JSONParser jsonparser = new JSONParser();

		FileReader reader  = new FileReader(Location.HoldsearchRq);

		Object obj = jsonparser.parse(reader);

		JSONObject requestobject = (JSONObject)obj;

		RequestSpecification requestSpecification = RestAssured.given();

		requestSpecification = requestSpecification.log().all();

		requestSpecification.baseUri(prop.getProperty("ENDPOINT"));

		requestSpecification.basePath(prop.getProperty("AirShopping"));

		requestSpecification.body(requestobject);

		requestSpecification.headers(Headervalue.authentication());

		Response response = requestSpecification.post();

		ValidatableResponse validatableResponse = response.then().log().all();

		validatableResponse.statusCode(200);

		JsonPath jsonPathEvaluator = response.jsonPath();

		Headervalue.Createfolder();

		Headervalue.Response(Location.HoldsearchRS, response.asPrettyString());

		V.ShoppingResponseId = jsonPathEvaluator.get("AirShoppingRS.ShoppingResponseId");

		System.out.println(V.ShoppingResponseId);

		ArrayList<String>OfferIDS = jsonPathEvaluator.get("AirShoppingRS.OffersGroup.AirlineOffers[0].Offer");

		System.out.println(OfferIDS.size());

		V.Offer = jsonPathEvaluator.get("AirShoppingRS.OffersGroup.AirlineOffers[0].Offer[0].OfferID");

		System.out.println(V.Offer);
	}

}
