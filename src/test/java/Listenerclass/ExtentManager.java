package Listenerclass;

import java.io.File;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
	
	private static ExtentReports extent;

	
	public static ExtentReports createInstance()
	{
		String fileName = getReportName();
		String directory = System.getProperty("user.dir") + "/REPORTS/";
		new File(directory).mkdirs();
		String path = directory + fileName;
		ExtentSparkReporter spark = new ExtentSparkReporter(path);
		spark = new ExtentSparkReporter("./REPORTS/Extent.html");
		spark.config().setEncoding("utf-8");
		spark.config().setDocumentTitle("Automation Reports");
		spark.config().setReportName("Automation Test Results");
		spark.config().setTheme(Theme.DARK);
		
		extent = new ExtentReports();
		extent.setSystemInfo("NAME", "PANDIAN ANGAIAH");
		extent.setSystemInfo("API", "REST");
		extent.setSystemInfo("SOURCE", "ENGINE");
		extent.attachReporter(spark);
		
		return extent;
		
	}
	
	public static String getReportName()
	{
		Date d = new Date();
		String filename = "Automation Report_" + d.toString().replace(":", "-").replace(" ", "-")+".png";
		return filename;
		
	}
	

}
