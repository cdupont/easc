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

import eu.dc4cities.easc.cloudcontrollers.IaaSController;
import eu.dc4cities.easc.configuration.CFConfigReader;
import eu.dc4cities.easc.configuration.CloudFoundryConfig;
import eu.dc4cities.easc.configuration.OSConfigReader;
import eu.dc4cities.easc.configuration.OpenStackConfig;
import eu.dc4cities.easc.energyservice.EnergyService;
import eu.dc4cities.easc.monitoring.PaaSMonitor;
import eu.dc4cities.easc.resource.Server;
import eu.dc4cities.easc.workingmode.WorkingModeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EASCCloudFoundry extends EASC {
	//public OSServerConfig serverConfig = new OSServerConfig();
	CFController cfCli;
    private static Logger logger = LoggerFactory.getLogger(EASCCloudFoundry.class);

	public EASCCloudFoundry(String configDirectory, PaaSMonitor monitorFactory, EnergyService energyService) {
		super(configDirectory, monitorFactory, energyService);

		//read OS parameters for application
		OpenStackConfig osConfig = OSConfigReader.readOpenStackConfig(configDirectory).get();
		//Provision OS instance, for later use in monitoring
		IaaSController osCli = new OSController(osConfig.getApiEndpoint(), osConfig.getUsername(), osConfig.getPassword(), osConfig.getTenant());
		while(osCli.auth() == false) {
	    	logger.debug("Make sure OpenStack services are up.");
	    	logger.debug("Trying to login within 3 seconds.");
	    	try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		//read CF parameters for application
		CloudFoundryConfig cfConfig = CFConfigReader.readCloudFoundryConfig(configDirectory).get();

		//Provision CF instance
		cfCli = new CFController(cfConfig.getApiEndpoint(), cfConfig.getUsername(), cfConfig.getPassword(), osCli);
		while(cfCli.login() == false) {
	    	logger.debug("Make sure CloudFoundry services are up.");
	    	logger.debug("Trying to login within 3 seconds.");
	    	try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		monitorFactory.setPaaSController(cfCli);
		monitorFactory.init();
		
        //Defining a CloudFoundry WMM, instead of DefaultWMM 
		WorkingModeManager wmm = new PaaSWorkingModeManager(this.getAppConfig(), cfCli, monitorFactory);
		this.setWorkingModeManager(wmm);

		//EnergyService multiEnergyService = new MultiEASCEnergyService(new PaaSServerConfig(cfCli), Arrays.asList(this), false, false);
		//this.setEnergyService(multiEnergyService);

        this.init(false);
	}
	
	 public List<Server> getServers() {
		 return cfCli.getServers(); 
	 }
}