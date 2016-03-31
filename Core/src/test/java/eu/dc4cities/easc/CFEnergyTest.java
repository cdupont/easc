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

package eu.dc4cities.easc;

import eu.dc4cities.easc.configuration.DefaultServerConfig;
import eu.dc4cities.easc.configuration.TestObjectsGenerator;
import eu.dc4cities.easc.energyservice.MultiEASCEnergyService;
import eu.dc4cities.easc.resource.Resource;
import eu.dc4cities.easc.resource.Server;
import eu.dc4cities.easc.resource.Units;
import eu.dc4cities.easc.resource.VM;
import eu.dc4cities.easc.workingmode.DefaultWorkingModeManager;
import eu.dc4cities.easc.workingmode.WorkingMode;
import org.jscience.physics.amount.Amount;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.measure.quantity.Power;
import java.util.ArrayList;
import java.util.List;

import static javax.measure.unit.NonSI.BYTE;
import static javax.measure.unit.SI.*;
import static org.junit.Assert.assertEquals;


public class CFEnergyTest {

	MultiEASCEnergyService multiEASCEnergyService;
	
	@Before
	public void setUp() throws Exception {

		DefaultServerConfig serverConfig = new DefaultServerConfig();

		Server server1 = new Server(Amount.valueOf(100, GIGA(BYTE)), Amount.valueOf(1, GIGA(BYTE)), Amount.valueOf(100, WATT), 4, "server1");
        Server server2 = new Server(Amount.valueOf(100, GIGA(BYTE)), Amount.valueOf(1, GIGA(BYTE)), Amount.valueOf(100, WATT), 4, "server2");
        List<Server> serverList = new ArrayList<>();
        serverList.add(server1);
        serverList.add(server2);
        serverConfig.setServers(serverList);
		multiEASCEnergyService = new MultiEASCEnergyService(false, true);
		multiEASCEnergyService.setServerConfig(serverConfig);
		
		EASC easc = new EASC(TestObjectsGenerator.createApp(), multiEASCEnergyService, TestObjectsGenerator.createConfiguration("testEASC1"));
		multiEASCEnergyService.addEasc(easc);
	}


	@After
	public void tearDown() throws Exception {
		
	}

	//Servers = 100 Watt
	//WM = 10 Watt
    //1 EASCs, 3 apps, 2 DCs, 2 containers = 12 containers total
    @Test
	public void test1Server1EASC() {
	
    	WorkingMode wm = TestObjectsGenerator.createWorkingMode("WM1", 1, "", 1, 10);
		 
		Amount<Power> predicted = multiEASCEnergyService.getPredictedPower("testEASC1", "test1", "dctest1", wm, Amount.valueOf(100.0, HERTZ));

		assertEquals(43, (int)predicted.doubleValue(WATT));
	}
	

	//Server = 100 Watt
	//Apps = 10 Watt
	//Server Idle power is shared fairly among 3 apps
    @Test
	public void test1Server2EASCs() {
	
    	WorkingMode wm = TestObjectsGenerator.createWorkingMode("WM1", 1, "", 1, 10);

		//add a second EASC
		EASC easc = new EASC(TestObjectsGenerator.createApp(), multiEASCEnergyService, TestObjectsGenerator.createConfiguration("testEASC2"));
		multiEASCEnergyService.addEasc(easc);

		Amount<Power> predicted1 = multiEASCEnergyService.getPredictedPower("testEASC1", "test1", "dctest1", wm, Amount.valueOf(100.0, HERTZ));
		assertEquals(26, (int) predicted1.doubleValue(WATT));

		Amount<Power> predicted2 = multiEASCEnergyService.getPredictedPower("testEASC2", "test1","dctest1", wm, Amount.valueOf(100.0, HERTZ));
		assertEquals(26, (int)predicted2.doubleValue(WATT));

	}

	

	//Servers = 100 Watt
	//Apps = 10 Watt
	//server #2 is not counted because not useful
    @Test
	public void test2Servers1App() {

		DefaultServerConfig serverConfig = (DefaultServerConfig) multiEASCEnergyService.getServerConfig();
        
        serverConfig.addServers(new Server(Amount.valueOf(100, GIGA(BYTE)), Amount.valueOf(1000, MEGA(BYTE)), Amount.valueOf(100, WATT), 0, "server2"));

    	WorkingMode wm = TestObjectsGenerator.createWorkingMode("WM1", 1, "", 1, 10);
    	
		Amount<Power> predicted = multiEASCEnergyService.getPredictedPower("testEASC1", "test1", "dctest1", wm, Amount.valueOf(100.0, HERTZ));
				
		assertEquals(43, (int)predicted.doubleValue(WATT));
	}
	


	//Servers: idle = 100 Watt, RAM = 1 Go
	//WM dyn power = 10W
	//2 EASCs, 3 apps, 2 DCs, 2 containers = 24 containers total
	//our app = 2 containers
	//container size = 100 Mo, total = 2400 Mo, 3 servers needed
    //PWM = 10 + 100 * 3 * 2 / 24
    @Test
	public void test2Easc3Apps() {
		DefaultServerConfig serverConfig = (DefaultServerConfig) multiEASCEnergyService.getServerConfig();
		serverConfig.addServers(new Server(Amount.valueOf(100, GIGA(BYTE)),
				Amount.valueOf(1, GIGA(BYTE)), Amount.valueOf(100, WATT), 0, "server3"));
        serverConfig.addServers(new Server(Amount.valueOf(100, GIGA(BYTE)),
                Amount.valueOf(1, GIGA(BYTE)), Amount.valueOf(100, WATT), 0, "server4"));

		EASC easc = new EASC(TestObjectsGenerator.createApp(), multiEASCEnergyService, TestObjectsGenerator.createConfiguration("testEASC2"));
        easc.setWorkingModeManager(new DefaultWorkingModeManager(TestObjectsGenerator.createApp()));
        easc.setEnergyService(multiEASCEnergyService);
        multiEASCEnergyService.addEasc(easc);
		
    	WorkingMode wm = TestObjectsGenerator.createWorkingMode("WM1", 1, "", 1, 10);
    	
		Amount<Power> predicted = multiEASCEnergyService.getPredictedPower("testEASC1", "test1", "dctest1", wm, Amount.valueOf(100.0, HERTZ));

		//18 W = 100*2/24 + 10
		assertEquals(34, (int)predicted.doubleValue(WATT));
	}

	@Test
	public void testVCpu() {

		WorkingMode wm = TestObjectsGenerator.createWorkingMode("wmtest0", 1, "", 1, 10);

		List<Resource> vms = new ArrayList<>();
		VM vm1 = new VM(Amount.valueOf(100, Units.MB), "test VM", 3, "");
		VM vm2 = new VM(Amount.valueOf(100, Units.MB), "test VM", 3, "");
		vms.add(vm1);
		vms.add(vm2);
		wm.setResources(vms);

		Amount<Power> predicted = multiEASCEnergyService.getPredictedPower("testEASC1", "test1", "dctest1", wm, Amount.valueOf(100.0, HERTZ));

		//18 W = 100*2/24 + 10
		assertEquals(43, (int)predicted.doubleValue(WATT));
	}
	
}
