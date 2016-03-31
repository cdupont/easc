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

import eu.dc4cities.easc.energyservice.MockEnergyService;
import eu.dc4cities.easc.resource.Resource;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Power;
import javax.measure.unit.SI;

/**
 * Monitor for testing
 */
public class MockMonitor extends Monitor {
	MonitorResource monitorServer;
	MonitorResource monitorVM;

	public MockMonitor() {
		this.monitorActivityAndWorkingmode = new MockMonitorActivityWorkingMode();
		this.monitorServer = new MockMonitorServer();
		this.es = new MockEnergyService();
	}
	
	// this is to add fixed resources for monitoring
	@Override
	public void init() {
		//adding DC1 monitoring resources
		this.addMonitorResource("Server1", monitorServer);
		this.addMonitorResource("Server2", monitorServer);
		this.addMonitorResource("Server3", monitorServer);
		
		this.addMonitorResource("VM1", monitorVM);
		this.addMonitorResource("VM2", monitorVM);
		
		this.initWorkDone("activity.datacenter");
	}

	//this is to add dynamic resources for monitoring
	@Override
	public void addResourceToMonitor(Resource res) {
		this.addMonitorResource(res.getName(), monitorServer);
	}

	@Override
	public Amount<Power> getTotalPower(String dcName) {
		return Amount.valueOf(100, SI.WATT);
	}

	@Override
	public int getRealtimeCumulativeBusinessObjective() {
		return 300;
	}

	@Override
	public int getActivityShareToPowerConsumption(String activityName, String dcName) {
		return 10;
	}
}
