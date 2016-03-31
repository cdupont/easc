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

package eu.dc4cities.easc.eascnonfede;

import eu.dc4cities.easc.monitoring.Monitor;
import eu.dc4cities.easc.monitoring.MonitorActivityWorkingMode;
import eu.dc4cities.easc.monitoring.MonitorResource;
import eu.dc4cities.easc.resource.Resource;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Power;
import javax.measure.unit.SI;

public class NonFedeAppMonitor extends Monitor {
	MonitorResource monitorServer;
	MonitorResource monitorVM;
	MonitorActivityWorkingMode monitorActivity;

	public NonFedeAppMonitor() {
		this.monitorActivityAndWorkingmode = new NonFedeAppMonitorActivityWorkingMode();
		this.monitorServer = new NonFedeAppMonitorServer();
		this.monitorVM = new NonFedeAppMonitorVM();
	}
	
	// this is to add fixed resources for monitoring
	@Override
	public void init() {
		//two way to define a resource
		//1. if the client does not know the DC name, we need to encode DC name as part of the resource key
		//this.addMonitorResource("DC1:Server1", monitorServer);
		//2. otherwise, the user knows, and will provide DC name
		//adding DC1 monitoring resources
		this.addMonitorResource("Server1", monitorServer);
		this.addMonitorResource("Server2", monitorServer);
		this.addMonitorResource("Server3", monitorServer);
		
		this.addMonitorResource("VM1", monitorVM);
		this.addMonitorResource("VM2", monitorVM);
	}

	//this is to add dynamic resources for monitoring
	@Override
	public void addResourceToMonitor(Resource res) {
		//to be adapted to special cases, if then elses for which resource to monitor, how, etc, 
		//keyRes association, etc.
		this.addMonitorResource(res.getName(), monitorServer);
	}

	@Override
	public Amount<Power> getTotalPower(String dc) {
		return Amount.valueOf(1000, SI.WATT);
	}

	@Override
	public int getRealtimeCumulativeBusinessObjective() {
		return 330;
	}

	@Override
	public int getActivityShareToPowerConsumption(String activityName, String dc) {
		return 10;
	}

}