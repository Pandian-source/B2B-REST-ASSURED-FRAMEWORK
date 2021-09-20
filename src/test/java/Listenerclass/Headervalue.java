package Listenerclass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Headervalue {

	public static Float Total;

	public static Map<String, String> authentication ()
	{
		Map<String, String> Headers = new HashMap<String, String>();
		Headers.put("content-type", "application/json");
		Headers.put("Connection", "keep-alive");
		Headers.put("Authorization", "c20ad4d76fe97759aa27a0c99bff67101585545954");
		return Headers;
	}

	public static void Createfolder()
	{
		File Directory = new File ("JSON FILE OUTPUT");

		if (Directory.exists())
		{
			System.out.println(Directory.exists());
		}
		else
		{
			Directory.mkdir();
		}

	}

	public static void Response(String path, String response) throws IOException
	{

		FileWriter file = new FileWriter(path);

		file.write(response);

		file.flush();

		file.close();
	}
	public static void ReplaceRequest(String path, String Request) throws IOException
	{

		FileWriter file = new FileWriter(path);

		file.write(Request);

		file.flush();

		file.close();
	}
	public static String gson(Object JSONObject)
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonresponse = gson.toJson(JSONObject);
		return jsonresponse;

	}

	public static Float Roundingvalue (Float Amount, Float HST)
	{
		    double Totalamount = Amount + HST;

			BigDecimal bd = new BigDecimal(Totalamount).setScale(2, RoundingMode.HALF_UP);
	        return Total = (float) bd.doubleValue();

	}



}






