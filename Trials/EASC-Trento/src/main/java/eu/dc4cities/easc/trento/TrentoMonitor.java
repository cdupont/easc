package eu.dc4cities.easc.trento;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.apache.commons.io.IOUtils;
import org.jscience.physics.amount.Amount;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.dc4cities.easc.monitoring.PaaSMonitor;
import eu.dc4cities.easc.resource.Resource;


/**
 * Monitor for testing
 */
public class TrentoMonitor extends PaaSMonitor {

	public TrentoMonitor() {
		this.monitorActivityAndWorkingmode = new TrentoMonitorActivityWorkingMode();		
	}
	
	// this is to add fixed resources for monitoring
	@Override
	public void init() {
		//adding DC1 monitoring resources
		//this.addMonitorResource("Server1", monitorServer);
		
		//this.addMonitorResource("VM1", monitorVM);
		
	}
	
	//this is to add dynamic resources for monitoring
	@Override
	public void addResourceToMonitor(Resource res) {
		//this.addMonitorResource(res.getName(), monitorServer);
	}

	public Amount<Power> getTotalPower(String dcName) {
		String urlString = "http://localhost:8080/MonitoringServlet/Power?op=tot";
		double totPower = 0;
		try {
			// Setup connection
			URL url = new URL(urlString);			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setReadTimeout(10000 /* milliseconds */);
		    conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
		    conn.setDoInput(true);
		    			
		    // Retrieve json row data
			conn.connect();
		    String charSet = getCharsetFromContentType(conn.getContentType());
		    if (charSet == null) charSet = "UTF-8";
		    String jsonBody = IOUtils.toString(conn.getInputStream(), charSet);
		    //logger.debug("WorkDone response is: " + jsonBody);
		    
		    // Parse json raw data			
			ObjectMapper mapper = new ObjectMapper();			
			@SuppressWarnings("unchecked")
			Map<String,Integer> myMap = mapper.readValue(jsonBody, Map.class);
			System.out.println("Total power: "+myMap.get("totalPower"));
			totPower = myMap.get("totalPower");
			//logger.debug("WorkDone extrapolated from json is: " + workDone);			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Amount.valueOf(totPower, SI.WATT);
	}
	
	/**
	 * Parse out a charset from a content type header.
	 * 
	 * @param contentType
	 *            e.g. "text/html; charset=EUC-JP"
	 * @return "EUC-JP", or null if not found. Charset is trimmed and
	 *         uppercased.
	 */
	private String getCharsetFromContentType(String contentType) {	 
		final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");		
		if (contentType == null)
			return null;
		Matcher m = charsetPattern.matcher(contentType);
		if (m.find()) {
			return m.group(1).trim().toUpperCase();
		}
		return null;
	}

	public int getRealtimeCumulativeBusinessObjective() {
		BendTrialMonitor bendMonitor = BendTrialMonitor.getInstance();
		int bendTodo = bendMonitor.getBendTodo();
		return bendTodo;
	}
}
