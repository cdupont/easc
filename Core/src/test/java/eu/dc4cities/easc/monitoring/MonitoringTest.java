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

import eu.dc4cities.easc.resource.Server;
import org.jscience.physics.amount.Amount;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static javax.measure.unit.NonSI.PERCENT;
import static javax.measure.unit.SI.*;
import static javax.measure.unit.Unit.ONE;

public class MonitoringTest {
	Monitor monitor;
	
	@Before
	public void setUp() throws Exception {
		monitor = new MockMonitor();
		monitor.addResourceToMonitor(new Server("server1"));
		monitor.addResourceToMonitor(new Server("server2"));
		monitor.addResourceToMonitor(new Server("server3"));
		monitor.init();
	}

	@After
	public void tearDown() throws Exception {
		monitor.cleanUpMonitorResources();
	}

	@Test
	public void testAddResourceToMonitor() {
		monitor.addResourceToMonitor(new Server("newServer"));
		assert(monitor.getMonitorResources().containsKey("newServer"));	
	}

	@Test
	public void testCleanUpMonitorResources() {
		monitor.cleanUpMonitorResources();
		assert(monitor.monitorResources.size() == 0);
	}

	@Test
	public void testAddMonitorResource() {
		monitor.addMonitorResource("newServerx", new MockMonitorServer());
		assert(monitor.getMonitorResources().containsKey("newServerx"));
	}

	@Test
	public void testGetCPULoad() {
		assert(monitor.getCPULoad("server1", "DC1").equals(Amount.valueOf(50, PERCENT)));
	}

	@Test
	public void testGetRAMLoad() {
		assert(monitor.getRAMLoad("server1", "DC1").equals(Amount.valueOf(50, PERCENT)));
	}

	@Test
	public void testGetDiskRead() {
		assert(monitor.getDiskRead("server1", "DC1").equals(Amount.valueOf(50, HERTZ)));
	}

	@Test
	public void testGetDiskWrite() {
		assert(monitor.getDiskWrite("server1", "DC1").equals(Amount.valueOf(50, HERTZ)));
	}

	@Test
	public void testGetNetworkRead() {
		assert(monitor.getNetworkRead("server1", "DC1").equals(Amount.valueOf(50, HERTZ)));
	}

	@Test
	public void testGetNetworkWrite() {
		assert(monitor.getNetworkWrite("server1", "DC1").equals(Amount.valueOf(50, HERTZ)));
	}

	@Test
	public void testGetPower() {
		assert(monitor.getPower("server1", "DC1").equals(Amount.valueOf(50, WATT)));
	}

	@Test
	public void testGetIpAddress() {
		assert(monitor.getIpAddress("server1", "DC1").equalsIgnoreCase("13.13.13.13"));
	}

	@Test
	public void testGetActivityCumulativeBusinessItems() {
		assert(monitor.getActivityCumulativeBusinessItems("activity", "datacenter").equals(Amount.valueOf(0, ONE)));
		monitor.addActivityCumulativeBusinessItems("activity", "datacenter", Amount.valueOf(10, ONE));
		assert(monitor.getActivityCumulativeBusinessItems("activity", "datacenter").equals(Amount.valueOf(10, ONE)));
 	}

	@Test
	public void testGetWMBusinessPerformance() {
		assert(monitor.getInstantBusinessPerformance("activity", "datacenter", "wm").equals(Amount.valueOf(1000, MILLI(HERTZ))));
	}

	@Test
	public void testGetWMPower() {
		assert(monitor.getWMPower("activity", "datacenter", "wm").equals(Amount.valueOf(128, WATT)));
	}
}
