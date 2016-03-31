package eu.dc4cities.easc;

import java.util.List;

import eu.dc4cities.easc.monitoring.IMIMonitor;
import eu.dc4cities.easc.resource.Units;
import eu.dc4cities.easc.energyservice.MultiEASCEnergyServiceIMI;
import eu.dc4cities.easc.configuration.ConfigReader;
import eu.dc4cities.easc.configuration.ServerConfig;

import com.google.common.base.Optional;

/**
 * Example of Main.
 */
public class Main {

	public static void main(String args[]) {
		System.out.println("IMI EASC");
		Units.init();
		List<String> configDirectory = Utils.parseCmdLineArgs(args);

		MultiEASCEnergyServiceIMI es = new MultiEASCEnergyServiceIMI(false, true);
		Optional<ServerConfig> serverConfig = ConfigReader.readServerConfig(configDirectory.get(0));
	        if(serverConfig.isPresent()) {
        	        es.setServerConfig(serverConfig.get());
		}
		EASC easc = new EASC(configDirectory.get(0), new IMIMonitor(), es);
		easc.init(true);
		easc.start();
	}
}
