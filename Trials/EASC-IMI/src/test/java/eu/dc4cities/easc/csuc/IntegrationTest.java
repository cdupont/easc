package eu.dc4cities.easc.imi;

import com.google.common.base.Optional;

import eu.dc4cities.easc.EASC;
import eu.dc4cities.easc.Utils;
import eu.dc4cities.easc.configuration.ConfigReader;
import eu.dc4cities.easc.configuration.DefaultServerConfig;
import eu.dc4cities.easc.configuration.ServerConfig;
import eu.dc4cities.easc.configuration.TestObjectsGenerator;
import eu.dc4cities.easc.energyservice.MultiEASCEnergyServiceIMI;
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
	WorkingMode VT0;
	WorkingMode VT1;
	WorkingMode VT2;
	WorkingMode VT3;
	WorkingMode VT4;

	//WC WMs
	WorkingMode WC0;
	WorkingMode WC2;
	WorkingMode WC5;
	WorkingMode WC7;

	@Override
	public void setUp() {
		Units.init();
		List<String> configDirectory = Utils.parseCmdLineArgs(new String[0]);
		MultiEASCEnergyServiceIMI es = new MultiEASCEnergyServiceIMI(false, true);
		Optional<ServerConfig> serverConfig = ConfigReader.readServerConfig(configDirectory.get(0));
		if(serverConfig.isPresent()) {
			es.setServerConfig(serverConfig.get());
			//System.out.println("server config:" + serverConfig.get().getServers().size() );
		}
		easc = new EASC(configDirectory.get(0), new MockMonitor(), es);
		easc.init(true);
        //es.addEasc(easc);

		//VT WMs
		VT0   = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(0);
		VT1 = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(1);
		VT2 = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(2);
		VT3 = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(3);
		VT4 = easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).getWorkingModes().get(4);

		//WC WMs
		WC0   = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(0);
		WC2 = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(1);
		WC5 = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(2);
		WC7 = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(3);
	}


	@Test
	public void testVT() {

		easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(WC0);

		assertEquals(0, getVTPred(VT0));
		assertEquals(246, getVTPred(VT1));
		assertEquals(260, getVTPred(VT2));
		assertEquals(277, getVTPred(VT3));
		assertEquals(291, getVTPred(VT4));

                easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(WC2);

                assertEquals(0, getVTPred(VT0));
                assertEquals(167, getVTPred(VT1));
                assertEquals(205, getVTPred(VT2));
                assertEquals(235, getVTPred(VT3));
                assertEquals(442, getVTPred(VT4));

                easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(WC5);

                assertEquals(0, getVTPred(VT0));
                assertEquals(139, getVTPred(VT1));
                assertEquals(180, getVTPred(VT2));
                assertEquals(212, getVTPred(VT3));
                assertEquals(400, getVTPred(VT4));

                easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(WC7);

                assertEquals(0, getVTPred(VT0));
                assertEquals(128, getVTPred(VT1));
                assertEquals(168, getVTPred(VT2));
                assertEquals(201, getVTPred(VT3));
                assertEquals(380, getVTPred(VT4));

	}

	@Test
	public void testWC() {

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VT0);

                assertEquals(0, getWCPred(WC0));
                assertEquals(230, getWCPred(WC2));
                assertEquals(230, getWCPred(WC5));
                assertEquals(230, getWCPred(WC7));

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VT1);
        
                assertEquals(0, getWCPred(WC0));
                assertEquals(91, getWCPred(WC2));
                assertEquals(119, getWCPred(WC5));
                assertEquals(130, getWCPred(WC7));

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VT2);
        
                assertEquals(0, getWCPred(WC0));
                assertEquals(66, getWCPred(WC2));
                assertEquals(91, getWCPred(WC5));
                assertEquals(103, getWCPred(WC7));

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VT3);
        
                assertEquals(0, getWCPred(WC0));
                assertEquals(53, getWCPred(WC2));
                assertEquals(76, getWCPred(WC5));
                assertEquals(87, getWCPred(WC7));

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VT4);
        
                assertEquals(0, getWCPred(WC0));
                assertEquals(79, getWCPred(WC2));
                assertEquals(121, getWCPred(WC5));
                assertEquals(141, getWCPred(WC7));

	}

    public int getVTPred(WorkingMode wm) {
        return (int)easc.getEnergyService().getPredictedPower("EASC-IMI", "VideoTranscoding", "imi_barcelona", wm, Amount.valueOf(100.0, SI.HERTZ)).doubleValue(WATT);
    }

	public int getWCPred(WorkingMode wm) {
		return (int)easc.getEnergyService().getPredictedPower("EASC-IMI", "WebCrawling", "imi_barcelona", wm, Amount.valueOf(100.0, SI.HERTZ)).doubleValue(WATT);
	}
}
