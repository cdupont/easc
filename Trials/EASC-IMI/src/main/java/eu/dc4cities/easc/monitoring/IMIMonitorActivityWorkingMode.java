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

/**
 * Monitor for testing
 */
public class IMIMonitorActivityWorkingMode  extends WorkDone implements MonitorActivityWorkingMode {
	
	public IMIMonitorActivityWorkingMode() {
		super();
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


	public String getActualWM(String activity, String datacenter){
        String actualwm=null;
try{
    Connection c = null;
    Statement stmt = null;
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:resource/dc4cities.sqlt");
      c.setAutoCommit(false);
      stmt = c.createStatement();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todaytext = today.format(formatter);
        ResultSet rs=null;
        int iTimeout = 3;
	stmt.setQueryTimeout(iTimeout);
	rs = stmt.executeQuery( "SELECT wm FROM working_modes WHERE date='"+todaytext+"' AND activity='"+activity+"' AND status='canviat';");
	switch (activity){
		case "VideoTranscoding" :
			actualwm="VT0";
			break;
		case "WebCrawling" :
			actualwm="WC0";
			break;
		default:
			return null;
	}
      while ( rs.next() ) {
        actualwm = rs.getString("wm");
      }
}catch(Exception e){
}finally{
	return actualwm;
}
	}

	private Amount<?> getStatFromWM(String activity, String datacenter, String wm, String type){
		Unit unit;
		Amount<?> amount=null;
		try{
		switch (activity) {
			case "VideoTranscoding" :
					if (type.equals("instant"))
						unit=Unit.valueOf("MB/min");
					else
						unit=Unit.valueOf("MB");
					break;
                        case "WebCrawling" :
					if (type.equals("instant"))
						unit=Unit.valueOf("Webs/min");
					else
						unit=Unit.valueOf("Webs");
					break;
			default:
					return null;
		}

		amount = Amount.valueOf(0, unit);

    Connection c = null;
    Statement stmt = null;
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:resource/dc4cities.sqlt");
      c.setAutoCommit(false);
      stmt = c.createStatement();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todaytext = today.format(formatter);
	ResultSet rs=null;
        int iTimeout = 3;
        stmt.setQueryTimeout(iTimeout);
int value=0;
if (type.equals("instant")){
        int last_minute=0;
       rs = stmt.executeQuery( "SELECT last_minute, date_last_minute FROM progress WHERE date='"+todaytext+"' AND activity='"+activity+"';");
      while ( rs.next() ) {
        String date_last_minute = rs.getString("date_last_minute");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = LocalDateTime.parse(date_last_minute);
        if (Duration.between(before, now).getSeconds()<300)
         value = rs.getInt("last_minute");
      }

}else{
	rs = stmt.executeQuery( "SELECT last_instant_progress FROM progress WHERE date='"+todaytext+"' AND activity='"+activity+"';" );
	while ( rs.next() ) {
		value = rs.getInt("last_instant_progress");
	}
}

amount=Amount.valueOf(value, unit);

rs.close();
stmt.close();
c.close();

		}catch (Exception e){
		}finally{
			return amount;
		}
	}

	@Override
	public Amount<Power> getWMPower(String activity, String datacenter, String wm) {
		return Amount.valueOf(0, WATT);
	}


}
