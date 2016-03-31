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

import eu.dc4cities.easc.resource.Resource;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Power;
import javax.measure.unit.SI;

public class CFAppMonitor extends PaaSMonitor {
	private MonitorResource containerMonitor;
	private MonitorActivityWorkingMode wmMonitor;
	
	public CFAppMonitor() {
	}

	@Override
	public void init() {
		containerMonitor = new MonitorContainerImpl(cli);
		wmMonitor = new MonitorWMImpl(cli);
		this.setMonitorActivityAndWorkingMode(wmMonitor);
		//This method initializes the list of resources for the Trial monitoring purposes
		//The list of names of containers should be available via WMM its currentWM resources
//		this.addMonitorResource("container1", containerMonitor);
//		this.addMonitorResource("container2", containerMonitor);
//		this.addMonitorResource("container3", containerMonitor);
//		MonitorResource server = new MonitorServerImpl(this.config);
//		this.addMonitorResource("server1", server);
//		this.addMonitorResource("server2", server);
//		this.addMonitorResource("server3", server);
//		
//		MonitorResource vm1 = new MonitorContainerImpl(this.config);
//		this.addMonitorResource("vm1", vm1);
//		this.addMonitorResource("vm2", vm1);
//		this.addMonitorResource("vm3", vm1);
//		//You can add any other type of MonitorResource, like Containers, etc.
//		
//		//Since we have one type of Working Mode, it is not like resource in terms of type
//		MonitorWorkingMode wm1 = new MonitorWMImpl();
//		this.setMonitorWorkingMode(wm1);		
	}
	
	@Override
	public void addResourceToMonitor(Resource res) {
		String appName = res.getName();
		this.addMonitorResource(appName + ":" + res.getName(), containerMonitor);
	}

	@Override
	public Amount<Power> getTotalPower(String dcName) {
		return Amount.valueOf(1000, SI.WATT);
	}

	@Override
	public int getRealtimeCumulativeBusinessObjective() {
		return 450;
	}
}
