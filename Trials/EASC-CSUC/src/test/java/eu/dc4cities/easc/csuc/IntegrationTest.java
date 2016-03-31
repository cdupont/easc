package eu.dc4cities.easc.csuc;

import com.google.common.base.Optional;

import eu.dc4cities.easc.EASC;
import eu.dc4cities.easc.Utils;
import eu.dc4cities.easc.configuration.ConfigReader;
import eu.dc4cities.easc.configuration.DefaultServerConfig;
import eu.dc4cities.easc.configuration.ServerConfig;
import eu.dc4cities.easc.configuration.TestObjectsGenerator;
import eu.dc4cities.easc.energyservice.MultiEASCEnergyServiceCSUC;
import eu.dc4cities.easc.monitoring.MockMonitor;
import eu.dc4cities.easc.resource.Server;
import eu.dc4cities.easc.resource.Units;
import eu.dc4cities.easc.workingmode.WorkingMode;
import junit.framework.TestCase;

import org.jscience.physics.amount.Amount;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.quantity.Power;
import javax.measure.unit.SI;

import java.util.List;

import static javax.measure.unit.SI.WATT;

public class IntegrationTest extends TestCase {
	
	private Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

	EASC easc;

	//VT WMs
	WorkingMode VTWM0;
	WorkingMode VTWM1;
	WorkingMode VTWM2;
	WorkingMode VTWM3;
	WorkingMode VTWM4;

	//WC WMs
	WorkingMode WCWM0;
	WorkingMode WCWM2;
	WorkingMode WCWM5;
	WorkingMode WCWM7;

	@Override
	public void setUp() {
		Units.init();
		List<String> configDirectory = Utils.parseCmdLineArgs(new String[0]);
		MultiEASCEnergyServiceCSUC es = new MultiEASCEnergyServiceCSUC(false, true);
		Optional<ServerConfig> serverConfig = ConfigReader.readServerConfig(configDirectory.get(0));
		if(serverConfig.isPresent()) {
			es.setServerConfig(serverConfig.get());
			//System.out.println("server config:" + serverConfig.get().getServers().size() );
		}
		easc = new EASC(configDirectory.get(0), new MockMonitor(), es);
		easc.init(true);
        //es.addEasc(easc);

		//VT WMs
		VTWM0   = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(0);
		VTWM1 = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(1);
		VTWM2 = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(2);
		VTWM3 = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(3);
		VTWM4 = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(4);

		//WC WMs
		WCWM0   = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(0);
		WCWM2 = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(1);
		WCWM5 = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(2);
		WCWM7 = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(3);
	}


	@Test
	public void testVT() {

		easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(WCWM0);

		assertEquals(0, getVTPred(VTWM0));
		assertEquals(183, getVTPred(VTWM1));
		assertEquals(211, getVTPred(VTWM2));
		assertEquals(240, getVTPred(VTWM3));
		assertEquals(262, getVTPred(VTWM4));

                easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(WCWM2);

                assertEquals(0, getVTPred(VTWM0));
                assertEquals(132, getVTPred(VTWM1));
                assertEquals(177, getVTPred(VTWM2));
                assertEquals(214, getVTPred(VTWM3));
                assertEquals(358, getVTPred(VTWM4));

                easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(WCWM5);

                assertEquals(0, getVTPred(VTWM0));
                assertEquals(115, getVTPred(VTWM1));
                assertEquals(160, getVTPred(VTWM2));
                assertEquals(199, getVTPred(VTWM3));
                assertEquals(332, getVTPred(VTWM4));

                easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(WCWM7);

                assertEquals(0, getVTPred(VTWM0));
                assertEquals(108, getVTPred(VTWM1));
                assertEquals(153, getVTPred(VTWM2));
                assertEquals(192, getVTPred(VTWM3));
                assertEquals(319, getVTPred(VTWM4));

	}

	@Test
	public void testWC() {

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VTWM0);

                assertEquals(0, getWCPred(WCWM0));
                assertEquals(166, getWCPred(WCWM2));
                assertEquals(166, getWCPred(WCWM5));
                assertEquals(167, getWCPred(WCWM7));

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VTWM1);
        
                assertEquals(0, getWCPred(WCWM0));
                assertEquals(78, getWCPred(WCWM2));
                assertEquals(95, getWCPred(WCWM5));
                assertEquals(104, getWCPred(WCWM7));

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VTWM2);
        
                assertEquals(0, getWCPred(WCWM0));
                assertEquals(61, getWCPred(WCWM2));
                assertEquals(78, getWCPred(WCWM5));
                assertEquals(86, getWCPred(WCWM7));

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VTWM3);
        
                assertEquals(0, getWCPred(WCWM0));
                assertEquals(53, getWCPred(WCWM2));
                assertEquals(68, getWCPred(WCWM5));
                assertEquals(76, getWCPred(WCWM7));

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VTWM4);
        
                assertEquals(0, getWCPred(WCWM0));
                assertEquals(70, getWCPred(WCWM2));
                assertEquals(96, getWCPred(WCWM5));
                assertEquals(110, getWCPred(WCWM7));

	}

    public int getVTPred(WorkingMode wm) {
        return (int)easc.getEnergyService().getPredictedPower("EASC-CSUC", "VideoTranscoding", "csuc_barcelona", wm, Amount.valueOf(100.0, SI.HERTZ)).doubleValue(WATT);
    }

	public int getWCPred(WorkingMode wm) {
		return (int)easc.getEnergyService().getPredictedPower("EASC-CSUC", "WebCrawling", "csuc_barcelona", wm, Amount.valueOf(100.0, SI.HERTZ)).doubleValue(WATT);
	}
}
