package eu.dc4cities.easc.old;

import java.util.List;

import eu.dc4cities.easc.EASC;
import eu.dc4cities.easc.configuration.Configuration;
import eu.dc4cities.easc.configuration.OSConfigReader;
import eu.dc4cities.easc.configuration.OpenStackConfig;
import eu.dc4cities.easc.energyservice.EnergyService;
import eu.dc4cities.easc.monitoring.Monitor;
import eu.dc4cities.easc.old.MockOSWorkingModeManager;
import eu.dc4cities.easc.workingmode.WorkingMode;
import eu.dc4cities.easc.workingmode.WorkingModeManager;

public class MockEASCOpenStack extends EASC {
	public MockEASCOpenStack(String configDirectory, Configuration config, EnergyService energyService, Monitor montr) {
		super(configDirectory, config, energyService, wD, montr);
		//It is better to encapsulate the next three lines inside EASCCloudFoundry init method, to abstract the 
		// complexities out of the user view.
		
		//Defining a CloudFoundry WMM, instead of DefaultWMM 
		List<WorkingMode> wmList = this.getAppConfig().getActivities().iterator().next().getWorkingModes();
		WorkingModeManager wmm = new MockOSWorkingModeManager(this.getEnergyServiceManager(), wmList);
		this.setWorkingModeManager(wmm);
		
		//read CF parameters for application
    	OpenStackConfig osConfig = OSConfigReader.readOpenStackConfig(configDirectory).get();
    	//Provision CF instance
		MockOSController osCli = MockOSController.getInstance(osConfig.getApiEndpoint(), osConfig.getUsername(), osConfig.getPassword());
		osCli.login();
	}
	
	public void init() {
		super.init(true);
		
    	//Then, stage the application, not pushing the app. 
    	//this is to provision the application on CF, to be ready for running
    	//	cfCli.provisionApp(appName, disk, ram, serviceNames);
    	//Perhaps if I pass EASC as parameter would be better??
    	//cfCli.startApp(appName);
	}

}
