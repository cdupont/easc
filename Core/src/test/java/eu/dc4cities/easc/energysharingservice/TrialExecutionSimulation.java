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

package eu.dc4cities.easc.energysharingservice;

import eu.dc4cities.easc.EASC;
import eu.dc4cities.easc.Utils;
import eu.dc4cities.easc.activity.Activity;
import eu.dc4cities.easc.activity.DataCenterWorkingModes;
import eu.dc4cities.easc.configuration.TestObjectsGenerator;
import eu.dc4cities.easc.energyservice.MultiEASCEnergyService;
import eu.dc4cities.easc.resource.Units;
import eu.dc4cities.easc.workingmode.WorkingMode;
import org.jscience.physics.amount.Amount;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.measure.quantity.Dimensionless;
import java.util.Random;

public class TrialExecutionSimulation{

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void main() {
		Units.init();
		Random ran = new Random();
		boolean appDone = false;
		String args[] = null;
		int awm[] = {0,0};
		String configDirectory = Utils.parseCmdLineArgs(args).get(0);
		MultiEASCEnergyService es = new MultiEASCEnergyService(false, true); 
	    EASC easc = new EASC(configDirectory, new RealTimeMonitor(), es);
		easc.setAppConfig(TestObjectsGenerator.createEascTwoActivities());
		easc.init(true);
		
		//simulating a day profile with 96 time slots
		for(int ts = 0; ts < 96; ts++) {
			System.out.println("TimeSlot: " + ts);
			appDone = true;
			awm[0] = ran.nextInt(3);
			awm[1] = ran.nextInt(3);
			System.out.println("Selected working mode for act1:" + awm[0] + ", act2:" + awm[1]);
			easc.getActivityPlanExecutor().executeActivityPlan(TestObjectsGenerator.createEascActivityPlanFromApp(easc.getAppConfig(), awm), false);
			
			for(Activity a : easc.getAppConfig().getActivities()) {
				Amount<Dimensionless> aslo = (Amount<Dimensionless>) a.getServiceLevelObjectives().get(0).getCumulativeBusinessObjective();
				Amount<Dimensionless> acbi = easc.getMonitor().getActivityCumulativeBusinessItems(a.getName());
				System.out.println("Activity " + a.getName() + " SLA: "+ aslo + " --- WorkDone: " + acbi);
						
				if(aslo.isGreaterThan(acbi)) {
					appDone = false;
					
					for(DataCenterWorkingModes dc: a.getDataCenters()) {
						WorkingMode wm = easc.getWorkingModeManager().getCurrentWorkingMode(a.getName(), dc.getDataCenterName());
						//TODO to change this to appropriate hook instead of wm.getPerformanceLevels().get(0).getBusinessPerformance()
						//We need real trial for this :)
						Amount<Dimensionless> wmBizPerf = (Amount<Dimensionless>) wm.getPerformanceLevels().get(0).getBusinessPerformance();
						easc.getMonitor().addActivityCumulativeBusinessItems(a.getName(), dc.getDataCenterName(), wmBizPerf);
						System.out.println("Adding workdone: " + wmBizPerf);
						System.out.println("Activity " + a.getName() + ", runnig with WM: " + 
						dc.getCurrentWorkingMode().getName() + ", real time power calculated: " + 
						es.getActivityPowerMonitoring(easc.getAppConfig().getName(), a.getName(), 
						dc.getDataCenterName()));
					}
				}
			}
			
			if(appDone) {
				System.out.println("App's SLA has been met.");
				break;
			}
		}
	}

}
