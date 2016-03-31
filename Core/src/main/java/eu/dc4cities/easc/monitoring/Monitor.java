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

import eu.dc4cities.easc.energyservice.EnergyService;
import eu.dc4cities.easc.resource.Resource;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;
import java.util.HashMap;
import java.util.Map;

/**
 * Monitor interface that should be implemented by trials
 */
public abstract class Monitor {
	protected Map<String, MonitorResource> monitorResources;
	protected MonitorActivityWorkingMode monitorActivityAndWorkingmode;
	protected EnergyService es;
	
	public Monitor() {
		monitorResources = new HashMap<String, MonitorResource>();
	}
	
	// to leave it to the trial side to add its specific resources
	public abstract void init();
	// to leave it to the trial side to add its specific resources
	public abstract void addResourceToMonitor(Resource res);

	public void cleanUpMonitorResources() {
		this.monitorResources.clear();
	}
	
	public void addMonitorResource(String resKey, MonitorResource ms) {
		monitorResources.put(resKey, ms);
	}
	
	public void setMonitorActivityAndWorkingMode(MonitorActivityWorkingMode mwm) {
		monitorActivityAndWorkingmode = mwm;
	}

	public Amount<Dimensionless> getCPULoad(String resName, String dc) {
		return monitorResources.get(resName).getCPULoad(resName, dc);
	}

    // RAM load in percent
	public Amount<Dimensionless> getRAMLoad(String resName, String dc) {
		return monitorResources.get(resName).getRAMLoad(resName, dc);
	}
	
	public Amount<Frequency> getDiskRead(String resName, String dc) {
		return monitorResources.get(resName).getDiskRead(resName, dc);
	}
    // Disk write accesses bit/second
	public  Amount<Frequency> getDiskWrite(String resName, String dc) {
		return monitorResources.get(resName).getDiskWrite(resName, dc);
	}
    // network read accesses bit/second
	public  Amount<Frequency> getNetworkRead(String resName, String dc) {
		return monitorResources.get(resName).getNetworkRead(resName, dc);
	}
    // network write accesses bit/second
	public  Amount<Frequency> getNetworkWrite(String resName, String dc) {
		return monitorResources.get(resName).getNetworkWrite(resName, dc);
	}
	// power in Watt
	public  Amount<Power> getPower(String resName, String dc) {
		return monitorResources.get(resName).getPower(resName, dc);
	}
	
	public String getIpAddress(String resName, String dc) {
		return monitorResources.get(resName).getIpAddress(resName, dc);
	}

	public Amount<Dimensionless> getActivityCumulativeBusinessItems(String activity, String datacenter) {
		return monitorActivityAndWorkingmode.getActivityCumulativeBusinessItems(activity, datacenter);
	}
	
	public Amount<Dimensionless> getActivityCumulativeBusinessItems(String activity) {
		return monitorActivityAndWorkingmode.getActivityCumulativeBusinessItems(activity);
	}
	
	public void addActivityCumulativeBusinessItems(String activityName, String dataCenterName, Amount<Dimensionless> amount) {
		monitorActivityAndWorkingmode.addActivityCumulativeBusinessItems(activityName, dataCenterName, amount);
	}

	public Amount<Power> getWMPower(String activity, String datacenter, String wm) {
		return monitorActivityAndWorkingmode.getWMPower(activity, datacenter, wm);
    }

	public Map<String, MonitorResource> getMonitorResources() {
		return monitorResources;
	}

	public void setMonitorResources(Map<String, MonitorResource> monitorResources) {
		this.monitorResources = monitorResources;
	}

	public MonitorActivityWorkingMode getMonitorActivityAndWorkingmode() {
		return monitorActivityAndWorkingmode;
	}

	public void initWorkDone(String key) {
		monitorActivityAndWorkingmode.initWorkDone(key);
	}

	public abstract int getRealtimeCumulativeBusinessObjective();
	
	public abstract Amount<Power> getTotalPower(String datacenter);
	
	public Amount<?> getInstantBusinessPerformance(String activity,	String datacenter, String wm) {
		return monitorActivityAndWorkingmode.getInstantBusinessPerformance(activity, datacenter, wm);
	}

	public void setEnergyService(EnergyService energyService) {
		this.es = energyService;
	}
	
	public EnergyService getEnergyService() {
		return this.es;
	}

	//For trial that need to use power sharing capability for multiple EASCs, and multiple activities
	public abstract int getActivityShareToPowerConsumption(String activityName,	String datacenter);
}