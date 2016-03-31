package eu.dc4cities.easc.trento;

import static javax.measure.unit.SI.MILLI;
import static javax.measure.unit.SI.WATT;
import static javax.measure.unit.Unit.ONE;
import static javax.measure.unit.SI.WATT;
import static javax.measure.unit.SI.SECOND;
import static javax.measure.unit.NonSI.PERCENT;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import eu.dc4cities.easc.monitoring.MonitorActivityWorkingMode;
import eu.dc4cities.easc.monitoring.WorkDone;

/**
 * Implementation of Monitoring Activity and WorkingMode for Trentino Trial in the Middle of Dolomities
 */
public class TrentoMonitorActivityWorkingMode extends WorkDone implements MonitorActivityWorkingMode {
	FendTrialMonitorDB fendMonitor = null;
	long fendLast;
	BendTrialMonitor bendMonitor = null;
	long bendLast;
	
	public TrentoMonitorActivityWorkingMode() {
		super();
		fendMonitor = FendTrialMonitorDB.getInstance();
		fendLast = GregorianCalendar.getInstance().getTimeInMillis();
		bendMonitor = BendTrialMonitor.getInstance();
		bendLast = GregorianCalendar.getInstance().getTimeInMillis();
	}

	@Override
	// Return exam requests per minute for FEND and BEND (from last monitoring call: every 5 minutes)
	public Amount<Frequency> getInstantBusinessPerformance(String activity, String datacenter, String wm) {
		double bizPerfRate = 0;
		if (activity.equals("fend")) {
			long now = new GregorianCalendar().getTimeInMillis();
			long msAfterMidNight = now - todayMidNight();
			long interval = now - fendLast;
			
			// If right after midnight check only on today
			if (msAfterMidNight < interval) interval = msAfterMidNight;
			
			interval = 5 * 60 * 1000; // Hardcode interval to 5 minutes
			now = (int)(now / 1000);
			interval = (int)(interval / 1000);
			int fendDone = fendMonitor.getFendRqsts(now, interval);
			//System.out.println("FEND: From " + new Timestamp(now) + " to " + new Timestamp(now-interval) + ", " + fendDone + " have been done");
			fendLast = now;
			bizPerfRate = fendDone == 0 ? 0 : (double)fendDone/5; // Hardcoded interval 5 minutes
			return (Amount<Frequency>) Amount.valueOf(bizPerfRate, Unit.valueOf("Req/min"));
		} else if (activity.equals("bend")) {
			long now = new GregorianCalendar().getTimeInMillis();
			long msAfterMidNight = now - todayMidNight();
			long interval = now - fendLast;

			// If right after midnight check only on today
			if (msAfterMidNight < interval) interval = msAfterMidNight;

			interval = 5 * 60 * 1000; // Hardcode interval to 5 minutes
			//System.out.println(now);
			now = (int)(now / 1000);
			interval = (int)(interval / 1000);
			//System.out.println(now);
			int bendDone = bendMonitor.getBendDone(now, interval);
			//System.out.println("BEND: From " + new Timestamp(now*1000) + " to " + new Timestamp((now-interval)*1000) + ", " + bendDone + " have been done");
			bendLast = now;
			bizPerfRate = bendDone == 0 ? 0 : (double)bendDone/5; // Hardcoded interval 5 minutes
			return (Amount<Frequency>) Amount.valueOf(bizPerfRate, Unit.valueOf("Exam/min"));
		}
		return (Amount<Frequency>) Amount.valueOf(bizPerfRate, Unit.valueOf("None/min"));
	}
	
	//TODO Return cumulative exams don in the day, required only from task-oriented (BEND)
	public Amount<Dimensionless> getActivityCumulativeBusinessItems(String activity, String dc) {
		int bizPerfCum = 0;

		if (activity.equals("bend")) {
			bizPerfCum = bendMonitor.getBendDone();
		}
		return (Amount<Dimensionless>) Amount.valueOf(bizPerfCum, Unit.valueOf("Exam"));	
	}
	
	@Override
	public Amount<Power> getWMPower(String activity, String datacenter, String wm) {
		return Amount.valueOf(0, WATT);
	}

	private long todayMidNight() {
		// today    
		Calendar date = new GregorianCalendar();
		// reset hour, minutes, seconds and millis
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		return date.getTimeInMillis();
	}
}
