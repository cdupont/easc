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

package eu.dc4cities.easc.monitoring;

import eu.dc4cities.controlsystem.model.TimeParameters;
import eu.dc4cities.controlsystem.model.easc.ActivityDataCenterMetrics;
import eu.dc4cities.controlsystem.model.easc.ActivityMetrics;
import eu.dc4cities.controlsystem.model.easc.EascMetrics;
import eu.dc4cities.easc.Application;
import eu.dc4cities.easc.activity.Activity;
import eu.dc4cities.easc.activity.DataCenterWorkingModes;
import eu.dc4cities.easc.workingmode.WorkingModeManager;
import org.jscience.physics.amount.Amount;

import java.util.ArrayList;
import java.util.List;

/**
 * Monitor for metrics
 */
public class MonitoringMetrics {

	public MonitoringMetrics(Monitor monitor, Application app, WorkingModeManager wmm) {
		this.monitor = monitor;
		this.app = app;
		this.wmm = wmm;
	}

	private Monitor monitor;
	private Application app;
	private WorkingModeManager wmm;
	
    /*
     * Returns the current working mode, business performance and power consumption of activities managed by 
     * the EASC on each data center in the federation (only one data center if working on single site). 
     * Note that if a data center is running two EASCs called A and B and the monitoring request is done for 
     * EASC A, the returned power must be the power consumed by EASC A only. 
     * Power consumed by EASC B will be returned in the monitoring call for that EASC.
     * 
     * The Control System will save metrics both at activity and site level 
     * If a single EASC is running multiple activities, metrics must be measured per-activity, so for example the 
     * power consumed by Activity 1 will be indicated under Activity 1 and the power for Activity 2 will be under 
     * Activity 2.
     * instantBusinessPerformance: current real business performance measured for the activity in the given data center
     * cumulativeBusinessPerformance: total number of business items processed by the activity in the given data center 
     * during the current evaluation period (i.e. from the start of the current day for day-based SLAs). This 
     * value is returned only for task-based activities and omitted for service-based activities 
     * (for which this number is not relevant)
     * power: current actual power consumption measured at the given data center for the resources used by 
     * the activity

     * @see eu.dc4cities.easc.com.EascApi#getMonitoringMetrics(eu.dc4cities.controlsystem.model.TimeParameters)
     */
    public EascMetrics getMonitoringMetrics(TimeParameters timeParams) {
        EascMetrics metrics = new EascMetrics(app.getName());
        
        List<ActivityMetrics> activitiesMetrics = new ArrayList<>();
        for(Activity a: app.getActivities()) {
        	ActivityMetrics am = new ActivityMetrics(a.getName());
        	List<ActivityDataCenterMetrics> ctrlDataCenterMetrics = new ArrayList<>();
        	for(DataCenterWorkingModes dc : a.getDataCenters()) {
        		eu.dc4cities.easc.workingmode.WorkingMode wm = wmm.getCurrentWorkingMode(a.getName(), dc.getDataCenterName());
        		if(wm != null) {
        			ActivityDataCenterMetrics dcMetrics = new ActivityDataCenterMetrics(dc.getDataCenterName());
        			dcMetrics.setWorkingModeName(wm.getName());
        			dcMetrics.setWorkingModeValue(wm.getValue());
        			//current actual power consumption measured at the given data center
        			//dcMetrics.setPower(monitor.getWMPower(a.getName(), dc.getDataCenterName(), wm.getName()));
        			//TODO: check consistency for HP trial
        			dcMetrics.setPower(monitor.getEnergyService().getActivityPowerMonitoring(app.getName(), a.getName(), dc.getDataCenterName()));
        			// current real business performance measured for the activity: wm.getPerformanceLevels().get(0) 
        			Amount<?> instantBusinessPerformance = monitor.getInstantBusinessPerformance(a.getName(), dc.getDataCenterName(), wm.getName());
        			dcMetrics.setInstantBusinessPerformance(instantBusinessPerformance);
        			// during the current evaluation period (i.e. from the start of the current day for day-based SLAs)
        			Amount<?> cumulativeBusinessPerformance = monitor.getActivityCumulativeBusinessItems(a.getName(), dc.getDataCenterName());
        			dcMetrics.setCumulativeBusinessPerformance(cumulativeBusinessPerformance);
        			ctrlDataCenterMetrics.add(dcMetrics);
        		}
        	}
            am.setDataCenters(ctrlDataCenterMetrics);
        	activitiesMetrics.add(am);
        }
        
		metrics.setActivities(activitiesMetrics);
        return metrics;	
    }
    
}
