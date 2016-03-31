package eu.dc4cities.easc.trento;

import com.google.common.base.Optional;

import eu.dc4cities.easc.EASC;
import eu.dc4cities.easc.Utils;
import eu.dc4cities.easc.configuration.ConfigReader;
import eu.dc4cities.easc.configuration.ServerConfig;
import eu.dc4cities.easc.energyservice.MultiEASCEnergyService;
import eu.dc4cities.easc.monitoring.MockMonitor;
import eu.dc4cities.easc.resource.Units;
import eu.dc4cities.easc.workingmode.WorkingMode;
import junit.framework.TestCase;

import org.jscience.physics.amount.Amount;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.unit.SI;

import java.util.List;

import static javax.measure.unit.SI.WATT;

public class IntegrationTest extends TestCase {
	
	private Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

	EASC easc;

	//fend WMs
	WorkingMode fendWM0;
	WorkingMode fendWMMin;
	WorkingMode fendWMMed;
	WorkingMode fendWMMax;

	//bend WMs
	WorkingMode bendWM0;
	WorkingMode bendWMMin;
	WorkingMode bendWMMed;
	WorkingMode bendWMMax;

	@Override
	public void setUp() {
		Units.init();
		List<String> configDirectory = Utils.parseCmdLineArgs(new String[0]);
		MultiEASCEnergyService es = new MultiEASCEnergyService(false, true);
		Optional<ServerConfig> serverConfig = ConfigReader.readServerConfig(configDirectory.get(0));
		if(serverConfig.isPresent()) {
			es.setServerConfig(serverConfig.get());
			//System.out.println("server config:" + serverConfig.get().getServers().size() );
		}
		easc = new EASC(configDirectory.get(0), new MockMonitor(), es);
		easc.init(true);
        //es.addEasc(easc);

		//fend WMs
		fendWM0   = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(0);
		fendWMMin = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(1);
		fendWMMed = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(2);
		fendWMMax = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(3);

		//fend WMs
		bendWM0   = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(0);
		bendWMMin = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(1);
		bendWMMed = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(2);
		bendWMMax = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(3);
	}


	@Test
	public void testFrontEnd() {

		assertEquals(167, getFendPred(fendWM0));
        assertEquals(806, getFendPred(fendWMMin));
        assertEquals(837, getFendPred(fendWMMed));
        assertEquals(881, getFendPred(fendWMMax));

        easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(bendWMMin);

		assertEquals(104, getFendPred(fendWM0));
		assertEquals(203, getFendPred(fendWMMin));
		assertEquals(289, getFendPred(fendWMMed));
		assertEquals(450, getFendPred(fendWMMax));

		easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(bendWMMed);

		assertEquals(65, getFendPred(fendWM0));
		assertEquals(185, getFendPred(fendWMMin));
		assertEquals(271, getFendPred(fendWMMed));
		assertEquals(452, getFendPred(fendWMMax));

		easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(bendWMMax);

		assertEquals(82, getFendPred(fendWM0));
		assertEquals(171, getFendPred(fendWMMin));
		assertEquals(254, getFendPred(fendWMMed));
		assertEquals(438, getFendPred(fendWMMax));
	}

	@Test
	public void testBackEnd() {

		assertEquals(0, getBendPred(bendWM0));
		assertEquals(829, getBendPred(bendWMMin));
		assertEquals(956, getBendPred(bendWMMed));
		assertEquals(2097, getBendPred(bendWMMax));

		easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(fendWMMin);

		assertEquals(0, getBendPred(bendWM0));
		assertEquals(762, getBendPred(bendWMMin));
		assertEquals(1304, getBendPred(bendWMMed));
		assertEquals(2041, getBendPred(bendWMMax));

		easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(fendWMMed);

		assertEquals(0, getBendPred(bendWM0));
		assertEquals(707, getBendPred(bendWMMin));
		assertEquals(1249, getBendPred(bendWMMed));
		assertEquals(1987, getBendPred(bendWMMax));

		easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(fendWMMax);

		assertEquals(0, getBendPred(bendWM0));
		assertEquals(590, getBendPred(bendWMMin));
		assertEquals(1112, getBendPred(bendWMMed));
		assertEquals(1848, getBendPred(bendWMMax));
	}

    public int getFendPred(WorkingMode wm) {
        return (int)easc.getEnergyService().getPredictedPower("EASC-Trento", "fend", "cn_trento", wm, Amount.valueOf(100.0, SI.HERTZ)).doubleValue(WATT);
    }

	public int getBendPred(WorkingMode wm) {
		return (int)easc.getEnergyService().getPredictedPower("EASC-Trento", "bend", "cn_trento", wm, Amount.valueOf(100.0, SI.HERTZ)).doubleValue(WATT);
	}
}
