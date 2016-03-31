package eu.dc4cities.easc.trento;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ListIterator;

public class FendTrialMonitor {
	private static FendTrialMonitor instance = null;
//	private int lastLine = 0;
	String csvFile = "/tmp/log.jtl";
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";
	ArrayList<Biz> upBizs = new ArrayList<>();
	ArrayList<Biz> dwBizs = new ArrayList<>();
	ArrayList<Biz> bizs = new ArrayList<>();
	
	private FendTrialMonitor () {
		
	}
	
	public static synchronized FendTrialMonitor getInstance() {
		if (instance == null) {
			instance = new FendTrialMonitor();
		}
		return instance;
	}	
		
	private void updateValues() {
		//TODO Find a way to read line faster: 1) remove already read lines ++ 2) skip and read only new lines
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(cvsSplitBy);
				if (fields.length < 12) continue;
				long start = Long.parseLong(fields[0]);
				long elapsedTime = Long.parseLong(fields[1]);
				String rqstType = fields[2];
				String resultCode = fields[3];
				String result = fields[4];
				
				Biz metric = new Biz(start, elapsedTime, result, resultCode, rqstType);
				bizs.add(metric);
				//if (metric.isValid()) System.out.println(metric.toString());
				
				//updateValuesActivities(metric);							
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	  }
	
	private void updateValuesActivities(Biz metric) {
		if (metric.rqstType.equals("Upload")) {
			Biz upMetric = metric;
			upBizs.add(upMetric);
		
			if (upMetric.isValid()) System.out.println(upMetric.toString());
		}
		else if (metric.rqstType.equals("Download")) {
			Biz dwMetric = metric;
			dwBizs.add(dwMetric);
		
			if (dwMetric.isValid()) System.out.println(dwMetric.toString());
		}		
	}

	private int getFendUpDone(long now, long intervalMS){
		this.updateValues();
		int done = 0;		
		for (ListIterator<Biz> iterator = upBizs.listIterator(upBizs.size()); iterator.hasPrevious();) {
			   Biz biz = iterator.previous();
			   if ( ((now - intervalMS) <= biz.end.getTime()) && biz.isValid() ) {
				   done++;
			   } 
			   else {
				   break;
			   }
			}		
		return done;
	}
	
	private int getFendDwDone(long now, long intervalMS){
		this.updateValues();
		int done = 0;		
		for (ListIterator<Biz> iterator = dwBizs.listIterator(dwBizs.size()); iterator.hasPrevious();) {
			   Biz biz = iterator.previous();
			   if ( ((now - intervalMS) <= biz.end.getTime()) && biz.isValid() ) {
				   done++;
			   } 
			   else {
				   break;
			   }
			}
		return done;
	}
	
	public int getFendDone(long now, long intervalMS){
		this.updateValues();
		int done = 0;		
		for (ListIterator<Biz> iterator = bizs.listIterator(bizs.size()); iterator.hasPrevious();) {
			   Biz biz = iterator.previous();
			   if ( ((now - intervalMS) <= biz.end.getTime()) && biz.isValid() ) {
				   done++;
			   } 
			   else {
				   break;
			   }
			}
		return done;
	}
	
	private class Biz {
		Timestamp start = null;
		Timestamp end = null;
		long elapsedTime = 0;
		String result = "";
		String resultCode = "";
		String rqstType = "";
		
		public Biz(long start, long elapsedTime, String result, String resultCode, String rqstType) {
			this.start = new Timestamp(start);
			this.end = new Timestamp(start + elapsedTime);
			this.elapsedTime = elapsedTime;
			this.result = result.equals("OK") ? result : "KO";
			this.resultCode = resultCode;
			this.rqstType = rqstType;
		}

		public String toString() {
		return 
				start + " | " +
				end + " | " +
				elapsedTime + " | " +
				result + " | " +
				resultCode + " | " +
				rqstType + " | ";
		}
		
		public Boolean isValid() {
			return this.result.equals("OK") ? true : false;
		}
	}	
}
