package Runnerclass;
import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import Createbooking.Airshopping;
import Createbooking.Pricing;
import Createbooking.Booking;
import Createbooking.Ticketing;
import Createbooking.Voidcancel;
import HoldBooking.HoldSearch;
import HoldBooking.HoldPrice;
import HoldBooking.HoldBooking;
import HoldBooking.Holdconfirm;
import Createbooking.AirFareRules;
import Createbooking.AirOrderRetrive;

@Listeners(Listenerclass.TestListeners.class)
public class Running
{
 /*	@Test(priority=1)
	public static void Searching() throws IOException, ParseException
	{
		Airshopping.Searching();
	}
	
	@Test(priority=2)
	public static void FareRules() throws IOException, ParseException
	{
		AirFareRules.FareRules();
	}

	@Test(priority=3)
	public static void pricing() throws IOException, ParseException
	{
		Pricing.pricing();
	}
	
	@Test(priority=4)
	public static void Booking() throws IOException, ParseException
	{
		Booking.NormalBooking();
	}
	
	@Test(priority=5)
	public static void Retrive() throws IOException, ParseException
	{
		AirOrderRetrive.Retrive();
	}
	@Test(priority=6)
	public static void HoldTicketing() throws IOException, ParseException
	{
		HoldTicketing.TicketingRequest();
	}
	@Test(priority=7)
	public static void Voidcancel() throws IOException, ParseException
	{
		Voidcancel.VoidRequest();
	}
	*/
	
	
	@Test(priority=1)
	public static void Holdsearching() throws IOException, ParseException
	{
		HoldSearch.HoldSearching();	
	}
	
	@Test(priority=2)
	public static void Holdpricing() throws IOException, ParseException
	{
		HoldPrice.Holdpricing();
	}
	
	@Test(priority=3)
	public static void Holdcreate() throws IOException, ParseException
	{
		HoldBooking.HoldBookingCreate();
	}
	
	@Test(priority=4)
	public static void Holdconfirm() throws IOException, ParseException
	{
		Holdconfirm.HoldBookingConfirm();
	}
	
	
	
	
}



