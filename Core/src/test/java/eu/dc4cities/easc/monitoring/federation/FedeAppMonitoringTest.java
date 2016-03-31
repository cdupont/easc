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

package eu.dc4cities.easc.monitoring.federation;

import eu.dc4cities.easc.monitoring.Monitor;
import org.jscience.physics.amount.Amount;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static javax.measure.unit.NonSI.PERCENT;
import static javax.measure.unit.SI.*;
import static javax.measure.unit.Unit.ONE;

public class FedeAppMonitoringTest {
	Monitor monitor;
	
	@Before
	public void setUp() throws Exception {
		monitor = new FedeAppMonitor();
		monitor.init();
	}

	@After
	public void tearDown() throws Exception {
		monitor.cleanUpMonitorResources();
	}

	@Test
	public void testCleanUpMonitorResources() {
		monitor.cleanUpMonitorResources();
		assert(monitor.getMonitorResources().size() == 0);
	}

	@Test
	public void testAddMonitorResource() {
		monitor.addMonitorResource("newServerx", new FedeAppMonitorServer());
		assert(monitor.getMonitorResources().containsKey("newServerx"));
	}

	@Test
	public void testGetCPULoad() {
		assert(monitor.getCPULoad("Server1", "DC1").equals(Amount.valueOf(10, PERCENT)));
		assert(monitor.getCPULoad("Server1", "DC2").equals(Amount.valueOf(20, PERCENT)));
		assert(monitor.getCPULoad("VM1", "DC1").equals(Amount.valueOf(10, PERCENT)));
		assert(monitor.getCPULoad("VM1", "DC2").equals(Amount.valueOf(20, PERCENT)));
	}

	@Test
	public void testGetRAMLoad() {
		assert(monitor.getRAMLoad("Server1", "DC1").equals(Amount.valueOf(10, PERCENT)));
		assert(monitor.getRAMLoad("Server1", "DC2").equals(Amount.valueOf(20, PERCENT)));
		assert(monitor.getRAMLoad("VM1", "DC1").equals(Amount.valueOf(10, PERCENT)));
		assert(monitor.getRAMLoad("VM1", "DC2").equals(Amount.valueOf(20, PERCENT)));
	}

	@Test
	public void testGetDiskRead() {
		assert(monitor.getDiskRead("Server1", "DC1").equals(Amount.valueOf(10, HERTZ)));
		assert(monitor.getDiskRead("Server1", "DC2").equals(Amount.valueOf(20, HERTZ)));
		assert(monitor.getDiskRead("VM1", "DC1").equals(Amount.valueOf(10, HERTZ)));
		assert(monitor.getDiskRead("VM1", "DC2").equals(Amount.valueOf(20, HERTZ)));
	}

	@Test
	public void testGetDiskWrite() {
		assert(monitor.getDiskWrite("Server1", "DC1").equals(Amount.valueOf(10, HERTZ)));
		assert(monitor.getDiskWrite("Server1", "DC2").equals(Amount.valueOf(20, HERTZ)));
		assert(monitor.getDiskWrite("VM1", "DC1").equals(Amount.valueOf(10, HERTZ)));
		assert(monitor.getDiskWrite("VM1", "DC2").equals(Amount.valueOf(20, HERTZ)));
	}

	@Test
	public void testGetNetworkRead() {
		assert(monitor.getNetworkRead("Server1", "DC1").equals(Amount.valueOf(10, HERTZ)));
		assert(monitor.getNetworkRead("Server1", "DC2").equals(Amount.valueOf(20, HERTZ)));
		assert(monitor.getNetworkRead("VM1", "DC1").equals(Amount.valueOf(10, HERTZ)));
		assert(monitor.getNetworkRead("VM1", "DC2").equals(Amount.valueOf(20, HERTZ)));
	}

	@Test
	public void testGetNetworkWrite() {
		assert(monitor.getNetworkWrite("Server1", "DC1").equals(Amount.valueOf(10, HERTZ)));
		assert(monitor.getNetworkWrite("Server1", "DC2").equals(Amount.valueOf(20, HERTZ)));
		assert(monitor.getNetworkWrite("VM1", "DC1").equals(Amount.valueOf(10, HERTZ)));
		assert(monitor.getNetworkWrite("VM1", "DC2").equals(Amount.valueOf(20, HERTZ)));
	}

	@Test
	public void testGetPower() {
		assert(monitor.getPower("Server1", "DC1").equals(Amount.valueOf(10, WATT)));
		assert(monitor.getPower("Server1", "DC2").equals(Amount.valueOf(20, WATT)));
		assert(monitor.getPower("VM1", "DC1").equals(Amount.valueOf(10, WATT)));
		assert(monitor.getPower("VM1", "DC2").equals(Amount.valueOf(20, WATT)));
	}

	@Test
	public void testGetIpAddress() {
		assert(monitor.getIpAddress("Server1", "DC1").equals("10.10.10.10"));
		assert(monitor.getIpAddress("Server1", "DC2").equals("20.20.20.20"));
		assert(monitor.getIpAddress("VM1", "DC1").equals("10.10.10.10"));
		assert(monitor.getIpAddress("VM1", "DC2").equals("20.20.20.20"));
	}

	@Test
	public void testGetActivityCumulativeBusinessItems() {
		//assert(monitor.getActivityCumulativeBusinessItems("activity", "DC1").equals(Amount.valueOf(0, ONE.alternate("Page"))));
		assert(monitor.getActivityCumulativeBusinessItems("activity", "DC1").equals(Amount.valueOf(0, ONE)));
		assert(monitor.getActivityCumulativeBusinessItems("activity", "DC2").equals(Amount.valueOf(0, ONE)));
		//monitor.addActivityCumulativeBusinessItems("activity", "DC1", Amount.valueOf(10, ONE.alternate("Page")));
		monitor.addActivityCumulativeBusinessItems("activity", "DC1", Amount.valueOf(10, ONE));
		monitor.addActivityCumulativeBusinessItems("activity", "DC2", Amount.valueOf(20, ONE));
		assert(monitor.getActivityCumulativeBusinessItems("activity", "DC1").equals(Amount.valueOf(10, ONE)));
		assert(monitor.getActivityCumulativeBusinessItems("activity", "DC2").equals(Amount.valueOf(20, ONE)));
 	}

	@Test
	public void testGetInstantBusinessPerformance() {
		assert(monitor.getInstantBusinessPerformance("activity", "DC1", "wm").equals(Amount.valueOf(10, MILLI(HERTZ))));
		assert(monitor.getInstantBusinessPerformance("activity", "DC2", "wm").equals(Amount.valueOf(20, MILLI(HERTZ))));
	}

	@Test
	public void testGetWMPower() {
		assert(monitor.getWMPower("activity", "DC1", "wm").equals(Amount.valueOf(10, WATT)));
		assert(monitor.getWMPower("activity", "DC2", "wm").equals(Amount.valueOf(20, WATT)));
	}

}