package eu.dc4cities.easc.monitoring;

import static javax.measure.unit.SI.HERTZ;
import static javax.measure.unit.SI.MILLI;
import static javax.measure.unit.SI.WATT;

import javax.measure.unit.Unit;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;
import javax.measure.quantity.Dimensionless;


import org.jscience.physics.amount.Amount;

import eu.dc4cities.easc.monitoring.MonitorActivityWorkingMode;
import eu.dc4cities.easc.monitoring.WorkDone;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;

import java.net.URL;
import java.net.HttpURLConnection;

import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * Monitor for testing
 */
public class CSUCMonitorActivityWorkingMode  extends WorkDone implements MonitorActivityWorkingMode {
	
	public CSUCMonitorActivityWorkingMode() {
		super();
		Constants.init();
	}

	@Override
	public Amount<?> getInstantBusinessPerformance(String activity,	String datacenter, String wm) {
		return getStatFromWM(activity, datacenter, wm, "instant");
	}

        @SuppressWarnings("unchecked")
        @Override
        public Amount<Dimensionless> getActivityCumulativeBusinessItems(String activity, String dc) {
                Amount<Dimensionless> amount= (Amount<Dimensionless>)getStatFromWM(activity, dc, null, "cumulative");
		return amount;
        }

	@Override
	public Amount<Dimensionless> getActivityCumulativeBusinessItems(String activity){
		return null;
	}

	private Amount<?> getStatFromWM(String activity, String datacenter, String wm, String type){
		Unit unit = (type.equals("instant")) ? Unit.valueOf(Constants.unitsInstant.get(activity)) : Unit.valueOf(Constants.unitsCumulative.get(activity));
		Amount<?> amount=null;
		int value=0;
		String urlString="";
		urlString="http://"+Constants.dc4citiesip.get(datacenter)+":8500/getWMStatistics?activity="+activity+"&type="+type;
		amount = Amount.valueOf(0, unit);

		try{
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();
	        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        	String inputLine;
	        while ((inputLine = in.readLine()) != null) value = (new Integer(inputLine)).intValue();
		in.close();
		amount=Amount.valueOf(value, unit);

		}catch (Exception e){
			e.printStackTrace();
		}finally{
			return amount;
		}
	}

	@Override
	public Amount<Power> getWMPower(String activity, String datacenter, String wm) {
		return Amount.valueOf(0, WATT);
	}

	@Override
	public void addActivityCumulativeBusinessItems(String activityName, String dataCenterName, Amount<Dimensionless> amount){}

	@Override
	public void initWorkDone(String key){}


}
