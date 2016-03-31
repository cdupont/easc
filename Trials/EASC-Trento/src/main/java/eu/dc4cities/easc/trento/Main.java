package eu.dc4cities.easc.trento;

import java.util.List;

import com.google.common.base.Optional;

import eu.dc4cities.easc.EASC;
import eu.dc4cities.easc.EASCCloudFoundry;
import eu.dc4cities.easc.Utils;
import eu.dc4cities.easc.configuration.ConfigReader;
import eu.dc4cities.easc.configuration.ServerConfig;
import eu.dc4cities.easc.energyservice.MultiEASCEnergyService;
import eu.dc4cities.easc.resource.Units;

public class Main {
	public static void main(String args[]) {
		Units.init();
		List<String> configDirectory = Utils.parseCmdLineArgs(args);
		MultiEASCEnergyService es = new MultiEASCEnergyService(false, true); 
        Optional<ServerConfig> serverConfig = ConfigReader.readServerConfig(configDirectory.get(0));
        if(serverConfig.isPresent()) {
        	es.setServerConfig(serverConfig.get());
        	//System.out.println("server config:" + serverConfig.get().getServers().size() );
        }

        EASC easc = new EASCCloudFoundry(configDirectory.get(0), new TrentoMonitor(), es);
		easc.start();
	}
}
