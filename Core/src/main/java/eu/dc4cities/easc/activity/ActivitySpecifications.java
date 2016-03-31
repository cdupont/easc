/*
 * Copyright 2016 The DC4Cities author.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.dc4cities.easc.activity;

import eu.dc4cities.controlsystem.model.TimeParameters;
import eu.dc4cities.controlsystem.model.easc.*;
import eu.dc4cities.easc.Application;
import eu.dc4cities.easc.energyservice.EnergyService;
import eu.dc4cities.easc.monitoring.Monitor;
import org.joda.time.DateTime;
import org.jscience.economics.money.Money;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.quantity.Frequency;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create the ActivitySpecification message to CTRL.
 */
public class ActivitySpecifications {
	
	private Logger logger = LoggerFactory.getLogger(ActivitySpecifications.class);
	private Application app;
	private EnergyService es;
	private String eascName;
	private Monitor monitor;
	
	public ActivitySpecifications(Application app, EnergyService es, String eascName, Monitor monitor) {
		this.app = app;
		this.es = es;
		this.eascName = eascName;
		this.monitor = monitor;
	}
	
    public EascActivitySpecifications getActivitySpecifications(TimeParameters timeParams) {
        //Convert Application app into the EascActivitySpecifications eas format
        EascActivitySpecifications eas = new EascActivitySpecifications(app.getName());
        List<ActivitySpecification> ass = new ArrayList<>();

        for(Activity a: app.getActivities()) {
        	ActivitySpecification as = new ActivitySpecification(a.getName());
        	String relocability = a.getRelocability();
        	as.setRelocability(Relocability.from(relocability));
			if(a.getMigrationPerformanceCost() == null){
                as.setMigrationPerformanceCost(Amount.valueOf("0 " + a.getBusinessUnit()));
            } else {
                as.setMigrationPerformanceCost(a.getMigrationPerformanceCost());
            }
        	
			ArrayList<ServiceLevelObjective> ctrlSlos = new ArrayList<ServiceLevelObjective>();
        	
        	int y = timeParams.getDateFrom().getYear();
        	int m = timeParams.getDateFrom().getMonthOfYear();
        	int d = timeParams.getDateFrom().getDayOfMonth();
        	DateTime currDate = new DateTime(y, m, d, 0, 0, 0);
        	
        	int ht = timeParams.getDateTo().getHourOfDay();
        	int mt = timeParams.getDateTo().getMinuteOfHour();
        	DateTime endDate0;
        	if(ht == 0 && mt ==0)
        		endDate0 = timeParams.getDateTo();
        	else
        		endDate0 = timeParams.getDateTo().plusDays(1);

        	DateTime endDate = new DateTime(endDate0.getYear(), endDate0.getMonthOfYear(), endDate0.getDayOfMonth(), 0, 0, 0);
        	
			while(currDate.compareTo(endDate) < 0) {
				//logger.debug(Integer.toString(currDate.compareTo(endDate)) + currDate + " " + endDate);
    			DateTime sdate = new DateTime(currDate.getYear(), currDate.getMonthOfYear(), currDate.getDayOfMonth(), 0, 0);
    			DateTime edate = sdate.plusDays(1);
		      	y = sdate.getYear();
	        	m = sdate.getMonthOfYear();
	        	d = sdate.getDayOfMonth();
	        	
	        	//covers 24 hours, repeats for every day to iterate over all slos
        		for(eu.dc4cities.easc.sla.ServiceLevelObjective eascSlo: a.getServiceLevelObjectives()) {
            		ServiceLevelObjective ctrlSlo = new ServiceLevelObjective();

            		if(eascSlo.getInstantBusinessObjective() != null) { //instantBusinessObjective
        				ctrlSlo.setInstantBusinessObjective(eascSlo.getInstantBusinessObjective());
        				String[] tf = eascSlo.getTimeFrom().split(":");
        				String[] te = eascSlo.getTimeTo().split(":");

            			DateTime timeFrom = new DateTime(y, m, d, Integer.parseInt(tf[0]), Integer.parseInt(tf[1]), Integer.parseInt(tf[2]));
        				ctrlSlo.setDateFrom(timeFrom);
        				
        				if(Integer.parseInt(te[0]) < 24) {
        					DateTime timeTo = new DateTime(y, m, d, Integer.parseInt(te[0]), Integer.parseInt(te[1]), Integer.parseInt(te[2]));
        					ctrlSlo.setDateTo(timeTo);
        				} else { // to close a day with hour: 24
        					DateTime timeTo = new DateTime(edate.getYear(), edate.getMonthOfYear(), edate.getDayOfMonth(), 0, 0, 0);
        					ctrlSlo.setDateTo(timeTo);
        				}
        				
            			ctrlSlo.setBasePrice(eascSlo.getBasePrice());
            			ctrlSlo.setPriceModifiers(eascSlo.getPriceModifiers());
        			} else { //cumulativeBusinessObjective, it should be one slo, and advances one full day        				
        				if(a.getBusinessBucketStream().equals("yes")) { // in this case now becomes important to generate SLOs
    						Amount<?> basePerfZero = Amount.valueOf("0 " + a.getBusinessUnit());
    						Amount<?> priceZero = Amount.valueOf("0 EUR/"+ a.getBusinessUnit());
        					if(timeParams.getDateNow().compareTo(currDate) >= 0) {
        						if(y == timeParams.getDateNow().getYear() && m == timeParams.getDateNow().getMonthOfYear()
        						&& d == timeParams.getDateNow().getDayOfMonth()) { // the same day
        							Amount<?> bizp = this.getCumulativeBusinessObjective(a.getBusinessUnit());
        							//OK
        							ctrlSlo.setCumulativeBusinessObjective(bizp);
        							Amount<?> ratioFactor = bizp.divide(eascSlo.getCumulativeBusinessObjective());
        							//logger.debug(eascSlo.getBasePrice().times(portional).toString());
        		        			//ctrlSlo.setBasePrice((Amount<Money>) eascSlo.getBasePrice().times(bizp).divide(eascSlo.getCumulativeBusinessObjective()));
        							//OK
        							ctrlSlo.setBasePrice((Amount<Money>) eascSlo.getBasePrice().times(ratioFactor));
        							List<PriceModifier> pms = new ArrayList<>();
        							
        							if(ratioFactor.getEstimatedValue() != 0L) {
        								for(PriceModifier pmo: eascSlo.getPriceModifiers()) {
        									PriceModifier pm1 = new PriceModifier();
        									pm1.setThreshold(pmo.getThreshold().times(ratioFactor));
        									pm1.setModifier(pmo.getModifier().times(ratioFactor));
        									pms.add(pm1);
        								}
        							} else {
            							PriceModifier pm1 = new PriceModifier();
            							pm1.setThreshold(basePerfZero);
            							pm1.setModifier(priceZero);
            							pms.add(pm1);
        							}
        							ctrlSlo.setPriceModifiers(pms);
        						} else { // the now is ahead of the current 
        							ctrlSlo.setCumulativeBusinessObjective(eascSlo.getCumulativeBusinessObjective());
        		        			ctrlSlo.setBasePrice(eascSlo.getBasePrice());
        		        			ctrlSlo.setPriceModifiers(eascSlo.getPriceModifiers());
        						}
        					} else { // the now is before current date SLO window
        						ctrlSlo.setCumulativeBusinessObjective(basePerfZero);
    		        			ctrlSlo.setBasePrice(eascSlo.getBasePrice().times(0));
    							List<PriceModifier> pms = new ArrayList<>();
    							PriceModifier pm1 = new PriceModifier();
    							pm1.setThreshold(basePerfZero);
    							pm1.setModifier(priceZero);
    							pms.add(pm1);
    							ctrlSlo.setPriceModifiers(pms);
        					}
        				} else { // it is not stream oriented (producer/consumer)
        					ctrlSlo.setCumulativeBusinessObjective(eascSlo.getCumulativeBusinessObjective());
		        			ctrlSlo.setBasePrice(eascSlo.getBasePrice());
		        			ctrlSlo.setPriceModifiers(eascSlo.getPriceModifiers());
        				}
        				
        				ctrlSlo.setDateFrom(sdate);
        				ctrlSlo.setDateTo(edate);
        			}
    				//logger.debug("SLO: " + ctrlSlo + " for " + timeParams.getDateNow());
        			ctrlSlos.add(ctrlSlo);
        		}
        		
        		currDate = currDate.plusDays(1);
        	}

        	as.setServiceLevelObjectives(ctrlSlos);
			if(a.getPrecedences() != null) {
				as.setPrecedences(a.getPrecedences());
			} else {
				as.setPrecedences(new ArrayList<>());
			}

        	List<DataCenterSpecification> ctrlDataCenters = new ArrayList<>();
        	for(DataCenterWorkingModes dc: a.getDataCenters()) {
        		DataCenterSpecification dcSpec = new DataCenterSpecification(dc.getDataCenterName());
        		dcSpec.setDefaultWorkingMode(dc.getDefaultWorkingMode());
        		List<WorkingMode> ctrlWms = new ArrayList<>();
            	for(eu.dc4cities.easc.workingmode.WorkingMode eascwm: dc.getWorkingModes()) {
            		WorkingMode wm = new WorkingMode();
            		wm.setName(eascwm.getName());
            		wm.setValue(eascwm.getValue());
            		List<Amount<?>> bizPerfs = eascwm.getPerformanceLevels().stream().map(PerformanceLevel::getBusinessPerformance).collect(Collectors.toList());
            		wm.setPerformanceLevels(getPerformanceLevels(dc.getDataCenterName(), a.getName(), eascwm, bizPerfs));
           		
            		wm.setTransitions(eascwm.getTransitions());
            		ctrlWms.add(wm);
            	}
        		dcSpec.setWorkingModes(ctrlWms);
        		ctrlDataCenters.add(dcSpec);
			}

			as.setDataCenters(ctrlDataCenters);
			if (a.getForbiddenStates() != null) {
				as.setForbiddenStates(a.getForbiddenStates());
			}
        	ass.add(as);        	
        }
        
        eas.setActivitySpecifications(ass);
        
    	return eas;
	}

    private Amount<?> getCumulativeBusinessObjective(String businessUnit) {
    	int workToDo = monitor.getRealtimeCumulativeBusinessObjective();
    	
    	return Amount.valueOf(Integer.toString(workToDo) + " " + businessUnit);
	}

	//Retrieve business performance from Energy Service
    public List<PerformanceLevel> getPerformanceLevels(String datacentre, String activity, eu.dc4cities.easc.workingmode.WorkingMode wm, List<Amount<?>> bizPerf) {

    	List<PerformanceLevel> bls = new ArrayList<>();
    	bizPerf.stream().forEach(bp -> bls.add(new PerformanceLevel(bp, es.getPredictedPower(eascName, activity, datacentre, wm, (Amount<Frequency>)bp))));;
		return bls;
	}

}
