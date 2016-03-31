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

package eu.dc4cities.easc;

import eu.dc4cities.easc.activity.DataCenterWorkingModes;
import eu.dc4cities.easc.cloudcontrollers.PaaSController;
import eu.dc4cities.easc.monitoring.Monitor;
import eu.dc4cities.easc.resource.CFApplication;
import eu.dc4cities.easc.resource.Resource;
import eu.dc4cities.easc.resource.Units;
import eu.dc4cities.easc.workingmode.DefaultWorkingModeManager;
import eu.dc4cities.easc.workingmode.WorkingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class PaaSWorkingModeManager extends DefaultWorkingModeManager {
    private Logger log = LoggerFactory.getLogger(PaaSWorkingModeManager.class);
	private Monitor monitor;
	private PaaSController paasController;
	
	public PaaSWorkingModeManager(Application app, PaaSController paasController, Monitor m) {
		super(app);
		this.paasController = paasController;
		this.monitor = m;
	}

	@Override
    public synchronized boolean applyWorkingMode(String activityName, String dcName, String wmn) {
		//TODO: Not needed at the end. Get workingMode appId, containers info, VMID, ServerID (from OS), disk, and memory capacity
		//We may need to get some query from OpenStack via CloudFoundry to get some of these info.
 		DataCenterWorkingModes dc = this.getDataCenter(activityName, dcName);
		WorkingMode wmToApply = this.getWorkingMode(dc, wmn);
		//WorkingMode currWM = this.getCurrentWorkingMode(activityName, dcName);
		boolean status = false;
		
		log.debug("Applying working mode for activity " + activityName + ":" + wmn + " using CF Java API.");
		
		//if(wmToApply.equals(currWM) == false) {
		//TODO: to clear out only Container based resources
		//monitor.cleanUpMonitorResources();
		ArrayList<Resource> appsTobeScaled = (ArrayList<Resource>) wmToApply.getResources();
		CFApplication app = (CFApplication) appsTobeScaled.get(0);

		if(app.getInstances() == 0) {
			if(paasController.stopApp(app.getName()))
				status = true;
		} else {
			status = this.scaleCFApp(app);
			if(status && 
					paasController.getAppState(app.getName()) != PaaSController.AppState.STARTED)
				status = paasController.startApp(app.getName());
		}
		//} else
		//	status = true;

		//Change current WM
		if(status == true) 
			dc.setCurrentWorkingMode(wmToApply);
		
		return status;
    }

	private void updateResourcesToMonitor(WorkingMode wmToApply) {
		for(Resource res: wmToApply.getResources()) {
			if(res instanceof CFApplication) {
				CFApplication cfApp = (CFApplication) res;
				String appName = cfApp.getName();

				for(String containerId: paasController.getAppContainersId(appName)) {
					CFApplication container = new CFApplication(containerId);
					container.setName(appName);
					log.debug("adding to monitorResource appName:containerId: " + appName + ":" + containerId);
					monitor.addResourceToMonitor(container);
				}
			}
		}
	}

	private boolean scaleCFApp(CFApplication app) {
		int instances = app.getInstances();
		int disk = (int) app.getDisk().longValue(Units.MB);
		int mem = (int) app.getRam().longValue(Units.MB);
		String appName = app.getName();
		log.debug("Scaling " + appName + " to i: " + instances + " d: " + disk + " m: " + mem);

		boolean state = paasController.scaleApp(appName, instances, disk, mem);

		return state;
	}

	private int stopStartCFApps(WorkingMode currWM, WorkingMode wmToApply) {
		Collection<String> appsToStop = new ArrayList<>();
		Collection<String> appsToStart = new ArrayList<>();
	
		//TODO: Resource type is of CFApplication type
		//Adding all resources to stop from the current VM
		if(currWM != null)
			for(Resource res: currWM.getResources())
				appsToStop.add(res.getName());
		
		//TODO: Resource type is of CFApplication type
		for(Resource appTobeScaled: wmToApply.getResources()) {
			CFApplication app = (CFApplication) appTobeScaled;
			
			if(app.getInstances() == 0)
				appsToStop.add(app.getName());
			else
				appsToStart.add(appTobeScaled.getName());
		}
		
		appsToStop.removeAll(appsToStart);

		//Stop all other apps we don't need at this WM
		for(String app: appsToStop)
			paasController.stopApp(app);
		
		return appsToStart.size();
	}
}