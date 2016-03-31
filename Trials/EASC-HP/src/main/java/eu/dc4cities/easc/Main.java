package eu.dc4cities.easc;

import java.util.List;

import eu.dc4cities.easc.energyservice.MultiEASCEnergyService;
import eu.dc4cities.easc.resource.HpMonitor;

public class Main {
	
	public static void main(String args[]) {
		// Supports running multiple EASCs, each with its own configuration directory
		List<String> configDirs = Utils.parseCmdLineArgs(args);
		MultiEASCEnergyService es = new MultiEASCEnergyService(false, false);
		for (String configDir : configDirs) {
	        EASC easc = new EASC(configDir, new HpMonitor(configDir), es);
	        easc.init(true);
	        easc.start();
		}
	}
	
}
