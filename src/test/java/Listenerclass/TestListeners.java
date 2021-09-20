package Listenerclass;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;


public class TestListeners implements ITestListener
{

	private static ExtentReports extent = ExtentManager.createInstance();
	private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();
	Logger log = Logger.getLogger(TestListeners.class);



	public void onTestStart(ITestResult result)
	{
		ExtentTest test = extent.createTest(result.getTestClass().getName() + " :: " + result.getMethod().getMethodName());
		extentTest.set(test);
		String logText = "<b>Test Method " + result.getMethod().getMethodName() + "Started<b>";
		Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
		extentTest.get().log(Status.INFO, m);
		log.info(logText);
		
		
	}
	public void onTestSuccess(ITestResult result)
	{
		String logText = "<b>Test Method " + result.getMethod().getMethodName() + "Success<b>";
		Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
		extentTest.get().log(Status.PASS, m);
		log.info(logText);

	}

	public void onTestFailure(ITestResult result)
	{
		String logText = "<b>Test Method " + result.getMethod().getMethodName() + "Failed<b>";
		Markup m = MarkupHelper.createLabel(logText, ExtentColor.BLUE);
		extentTest.get().log(Status.FAIL, m);
		log.info(logText);
	}


	public void onTestSkipped(ITestResult result)
	{
		String logText = "<b>Test Method " + result.getMethod().getMethodName() + "Skipped<b>";
		Markup m = MarkupHelper.createLabel(logText, ExtentColor.YELLOW);
		extentTest.get().log(Status.SKIP, m);
		log.info(logText);
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

	}

	public void onTestFailedWithTimeout(ITestResult result) {

	}

	public void onStart(ITestContext context)
	{

	}

	public void onFinish(ITestContext context)
	{
		if (extent!=null)
		{
			extent.flush();
		}
	}}

