package eu.dc4cities.easc.resource;

import java.io.File;

import javax.measure.quantity.Power;

import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dc4cities.easc.Main;
import eu.dc4cities.easc.monitoring.Monitor;

public class HpMonitor extends Monitor {
	
    private static Logger log = LoggerFactory.getLogger(Main.class);

    private EascHpConfiguration configuration;
    private static final String EASC_HP_CONFIG_FILE = "EascHpConfig.yaml";
    
	public HpMonitor() {
		this.init();
	}
	
	public HpMonitor(String configDirectory) {
		this.configuration = EascHpConfiguration.from(configDirectory + File.separator + EASC_HP_CONFIG_FILE);
		this.init();
	}
	
	@Override
	public void init() {
		this.monitorActivityAndWorkingmode = new HpMonitorActivityWorkingMode(configuration);
	}

	public void initWorkDone(String key) {
		log.info("key: " + key);
		monitorActivityAndWorkingmode.initWorkDone(key);
	}

	@Override
	public void addResourceToMonitor(Resource res) {}

	@Override
	public Amount<Power> getTotalPower(String dcName) {
		return null;
	}

	@Override
	public int getRealtimeCumulativeBusinessObjective() {
		// This is to support producer/consumer model. Not useful for HP trial.
		return 0;
	}

	@Override
	public int getActivityShareToPowerConsumption(String activityName, String dcName) {
		// Not useful for HP trial.
		return 0;
	}

}
