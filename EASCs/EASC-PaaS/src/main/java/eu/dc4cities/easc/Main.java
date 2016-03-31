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

import eu.dc4cities.easc.configuration.Constants;
import eu.dc4cities.easc.energyservice.MultiEASCEnergyService;
import eu.dc4cities.easc.monitoring.CFAppMonitor;
import eu.dc4cities.easc.resource.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.unit.UnitFormat;
import java.util.ArrayList;
import java.util.List;

import static javax.measure.unit.Unit.ONE;


public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static EASC easc;
    private static List<String> configDirectory;
    
    public static void main(String args[]) {
    	if(args.length == 0) {
    		logger.error("You need to pass mock, or bosh-lite, or multi-easc to specify the CF instance, and the EASC type.");
    		return;
    	}

    	logger.debug(args[0] + ":" );
    	configDirectory = Utils.parseCmdLineArgs(args);
    	switch(args[0]) {
    		case "mock":
    			mainMock();
    			break;
    		case "bosh-lite":
    			mainBoshlite();
    			break;
    		case "shared-infrastructure":
    			mainMultiEASC(args);
    			break;
    		case "osserver":
    			mainosserver();
    			break;
    		default:
        		System.out.println("nothing specified to run.");
    	}
    }

    public static void mainosserver() {
		MultiEASCEnergyService es = new MultiEASCEnergyService(false, true); 

    	EASCCloudFoundry eascf = new EASCCloudFoundry(configDirectory.get(0), new CFAppMonitor(), es);
    	//easc.start();
		System.out.println("servers list with DEAs.");

    	for(Server s: eascf.getServers()) {
    		System.out.println(s);
    	}
    }
    
    public static void mainBoshlite() {
    	logger.debug("--------------EASC-CF-Demo CloudFoundry/bosh-lite version----------");
		MultiEASCEnergyService es = new MultiEASCEnergyService(false, true);
    	easc = new EASCCloudFoundry(configDirectory.get(0), new CFAppMonitor(), es);
    	easc.start();
    	return;
    }

    public static void mainMock() {
    	logger.debug("--------------EASC-CF-Demo Mock version----------");
		
    	easc = new MockEASCCloudFoundry(configDirectory.get(0),  new CFAppMonitor());
    	easc.start();
    	return;
    }
    
    public static void mainMultiEASC(String args[]) {
		System.out.println("PaaS EASC");
		UnitFormat.getInstance().label(ONE.alternate("Page"), "Page");
		
		List<String> configDirectory = Utils.parseCmdLineArgs(args);

		List<EASC> eascs = new ArrayList<>();

		Constants.DEFAULT_ENERGY_SERVICE = "shared-infrastructure";
		//for each EASC
		MultiEASCEnergyService es = new MultiEASCEnergyService(false, true);
		for(String c : configDirectory) {
			eascs.add(new EASCCloudFoundry(c, new CFAppMonitor(), es));
	        easc.init(true);
	        //registering the easc with the Energy Service
	        //ES.addWmm(easc.getWorkingModeManager());
	        easc.start();	
		}
	}
}
