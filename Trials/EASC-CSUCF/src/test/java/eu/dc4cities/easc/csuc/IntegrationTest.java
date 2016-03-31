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

        WorkingMode VT0;
        WorkingMode VT1;
        WorkingMode VT2;
        WorkingMode VT3;
        WorkingMode VT4;

	//WC WMs
	WorkingMode WCWM0;
	WorkingMode WCWM2;
	WorkingMode WCWM5;
	WorkingMode WCWM7;

        WorkingMode WC0;
        WorkingMode WC2;
        WorkingMode WC5;
        WorkingMode WC7;

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

                VT0   = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(0);
                VT1 = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(1);
                VT2 = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(2);
                VT3 = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(3);
                VT4 = easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).getWorkingModes().get(4);

		//WC WMs
		WCWM0   = easc.getAppConfig().getActivities().get(2).getDataCenters().get(0).getWorkingModes().get(0);
		WCWM2 = easc.getAppConfig().getActivities().get(2).getDataCenters().get(0).getWorkingModes().get(1);
		WCWM5 = easc.getAppConfig().getActivities().get(2).getDataCenters().get(0).getWorkingModes().get(2);
		WCWM7 = easc.getAppConfig().getActivities().get(2).getDataCenters().get(0).getWorkingModes().get(3);

                WC0   = easc.getAppConfig().getActivities().get(2).getDataCenters().get(1).getWorkingModes().get(0);
                WC2 = easc.getAppConfig().getActivities().get(2).getDataCenters().get(1).getWorkingModes().get(1);
                WC5 = easc.getAppConfig().getActivities().get(2).getDataCenters().get(1).getWorkingModes().get(2);
                WC7 = easc.getAppConfig().getActivities().get(2).getDataCenters().get(1).getWorkingModes().get(3);
	}


	@Test
	public void testVT() {

		easc.getAppConfig().getActivities().get(2).getDataCenters().get(0).setCurrentWorkingMode(WCWM0);

		assertEquals(0, getPred(VTWM0, "csuc_barcelona", "VideoTranscoding"));
		assertEquals(183, getPred(VTWM1, "csuc_barcelona", "VideoTranscoding"));
		assertEquals(211, getPred(VTWM2, "csuc_barcelona", "VideoTranscoding"));
		assertEquals(240, getPred(VTWM3, "csuc_barcelona", "VideoTranscoding"));
		assertEquals(262, getPred(VTWM4, "csuc_barcelona", "VideoTranscoding"));

                easc.getAppConfig().getActivities().get(2).getDataCenters().get(0).setCurrentWorkingMode(WCWM2);

                assertEquals(0, getPred(VTWM0, "csuc_barcelona", "VideoTranscoding"));
                assertEquals(132, getPred(VTWM1, "csuc_barcelona", "VideoTranscoding"));
                assertEquals(177, getPred(VTWM2, "csuc_barcelona", "VideoTranscoding"));
                assertEquals(214, getPred(VTWM3, "csuc_barcelona", "VideoTranscoding"));
                assertEquals(358, getPred(VTWM4, "csuc_barcelona", "VideoTranscoding"));

                easc.getAppConfig().getActivities().get(2).getDataCenters().get(0).setCurrentWorkingMode(WCWM5);

                assertEquals(0, getPred(VTWM0, "csuc_barcelona", "VideoTranscoding"));
                assertEquals(115, getPred(VTWM1, "csuc_barcelona", "VideoTranscoding"));
                assertEquals(160, getPred(VTWM2, "csuc_barcelona", "VideoTranscoding"));
                assertEquals(199, getPred(VTWM3, "csuc_barcelona", "VideoTranscoding"));
                assertEquals(332, getPred(VTWM4, "csuc_barcelona", "VideoTranscoding"));

                easc.getAppConfig().getActivities().get(2).getDataCenters().get(0).setCurrentWorkingMode(WCWM7);

                assertEquals(0, getPred(VTWM0, "csuc_barcelona", "VideoTranscoding"));
                assertEquals(108, getPred(VTWM1, "csuc_barcelona", "VideoTranscoding"));
                assertEquals(153, getPred(VTWM2, "csuc_barcelona", "VideoTranscoding"));
                assertEquals(192, getPred(VTWM3, "csuc_barcelona", "VideoTranscoding"));
                assertEquals(319, getPred(VTWM4, "csuc_barcelona", "VideoTranscoding"));

                easc.getAppConfig().getActivities().get(2).getDataCenters().get(1).setCurrentWorkingMode(WC0);

                assertEquals(0, getPred(VT0, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(246, getPred(VT1, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(260, getPred(VT2, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(277, getPred(VT3, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(291, getPred(VT4, "imi_barcelona", "VideoTranscodingImi"));

                easc.getAppConfig().getActivities().get(2).getDataCenters().get(1).setCurrentWorkingMode(WC2);

                assertEquals(0, getPred(VT0, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(167, getPred(VT1, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(205, getPred(VT2, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(235, getPred(VT3, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(442, getPred(VT4, "imi_barcelona", "VideoTranscodingImi"));

                easc.getAppConfig().getActivities().get(2).getDataCenters().get(1).setCurrentWorkingMode(WC5);

                assertEquals(0, getPred(VT0, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(139, getPred(VT1, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(180, getPred(VT2, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(212, getPred(VT3, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(400, getPred(VT4, "imi_barcelona", "VideoTranscodingImi"));

                easc.getAppConfig().getActivities().get(2).getDataCenters().get(1).setCurrentWorkingMode(WC7);

                assertEquals(0, getPred(VT0, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(128, getPred(VT1, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(168, getPred(VT2, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(201, getPred(VT3, "imi_barcelona", "VideoTranscodingImi"));
                assertEquals(380, getPred(VT4, "imi_barcelona", "VideoTranscodingImi"));

	}

	@Test
	public void testWC() {

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VTWM0);

                assertEquals(0, getPred(WCWM0, "csuc_barcelona", "WebCrawling"));
                assertEquals(166, getPred(WCWM2, "csuc_barcelona", "WebCrawling"));
                assertEquals(166, getPred(WCWM5, "csuc_barcelona", "WebCrawling"));
                assertEquals(167, getPred(WCWM7, "csuc_barcelona", "WebCrawling"));

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VTWM1);
        
                assertEquals(0, getPred(WCWM0, "csuc_barcelona", "WebCrawling"));
                assertEquals(78, getPred(WCWM2, "csuc_barcelona", "WebCrawling"));
                assertEquals(95, getPred(WCWM5, "csuc_barcelona", "WebCrawling"));
                assertEquals(104, getPred(WCWM7, "csuc_barcelona", "WebCrawling"));

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VTWM2);
        
                assertEquals(0, getPred(WCWM0, "csuc_barcelona", "WebCrawling"));
                assertEquals(61, getPred(WCWM2, "csuc_barcelona", "WebCrawling"));
                assertEquals(78, getPred(WCWM5, "csuc_barcelona", "WebCrawling"));
                assertEquals(86, getPred(WCWM7, "csuc_barcelona", "WebCrawling"));

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VTWM3);
        
                assertEquals(0, getPred(WCWM0, "csuc_barcelona", "WebCrawling"));
                assertEquals(53, getPred(WCWM2, "csuc_barcelona", "WebCrawling"));
                assertEquals(68, getPred(WCWM5, "csuc_barcelona", "WebCrawling"));
                assertEquals(76, getPred(WCWM7, "csuc_barcelona", "WebCrawling"));

                easc.getAppConfig().getActivities().get(0).getDataCenters().get(0).setCurrentWorkingMode(VTWM4);
        
                assertEquals(0, getPred(WCWM0, "csuc_barcelona", "WebCrawling"));
                assertEquals(70, getPred(WCWM2, "csuc_barcelona", "WebCrawling"));
                assertEquals(96, getPred(WCWM5, "csuc_barcelona", "WebCrawling"));
                assertEquals(110, getPred(WCWM7, "csuc_barcelona", "WebCrawling"));

                easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(VT0);

                assertEquals(0, getPred(WC0, "imi_barcelona", "WebCrawling"));
                assertEquals(230, getPred(WC2, "imi_barcelona", "WebCrawling"));
                assertEquals(230, getPred(WC5, "imi_barcelona", "WebCrawling"));
                assertEquals(230, getPred(WC7, "imi_barcelona", "WebCrawling"));

                easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(VT1);

                assertEquals(0, getPred(WC0, "imi_barcelona", "WebCrawling"));
                assertEquals(91, getPred(WC2, "imi_barcelona", "WebCrawling"));
                assertEquals(119, getPred(WC5, "imi_barcelona", "WebCrawling"));
                assertEquals(130, getPred(WC7, "imi_barcelona", "WebCrawling"));

                easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(VT2);

                assertEquals(0, getPred(WC0, "imi_barcelona", "WebCrawling"));
                assertEquals(66, getPred(WC2, "imi_barcelona", "WebCrawling"));
                assertEquals(91, getPred(WC5, "imi_barcelona", "WebCrawling"));
                assertEquals(103, getPred(WC7, "imi_barcelona", "WebCrawling"));

                easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(VT3);

                assertEquals(0, getPred(WC0, "imi_barcelona", "WebCrawling"));
                assertEquals(53, getPred(WC2, "imi_barcelona", "WebCrawling"));
                assertEquals(76, getPred(WC5, "imi_barcelona", "WebCrawling"));
                assertEquals(87, getPred(WC7, "imi_barcelona", "WebCrawling"));

                easc.getAppConfig().getActivities().get(1).getDataCenters().get(0).setCurrentWorkingMode(VT4);

                assertEquals(0, getPred(WC0, "imi_barcelona", "WebCrawling"));
                assertEquals(79, getPred(WC2, "imi_barcelona", "WebCrawling"));
                assertEquals(121, getPred(WC5, "imi_barcelona", "WebCrawling"));
                assertEquals(141, getPred(WC7, "imi_barcelona", "WebCrawling"));

	}

    public int getPred(WorkingMode wm, String dataCenter, String activity) {
        return (int)easc.getEnergyService().getPredictedPower("EASC-CSUC", activity, dataCenter, wm, Amount.valueOf(100.0, SI.HERTZ)).doubleValue(WATT);
    }

}
