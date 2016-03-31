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
import eu.dc4cities.easc.resource.CFApplication;
import eu.dc4cities.easc.resource.Resource;
import eu.dc4cities.easc.resource.Units;
import eu.dc4cities.easc.workingmode.DefaultWorkingModeManager;
import eu.dc4cities.easc.workingmode.WorkingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class MockCFWorkingModeManager extends DefaultWorkingModeManager {
    private Logger log = LoggerFactory.getLogger(MockCFWorkingModeManager.class);

	private PaaSController paasController;

    public MockCFWorkingModeManager(Application app, PaaSController paasController) {

		super(app);
		this.paasController = paasController;
	}



	@Override
    public boolean applyWorkingMode(String activityName, String dcName, String wmn) {
	    log.debug("Applying working mode: " + wmn);
		//Get workingMode appId, containers info, VMID, ServerID (from OS), disk, and memory capacity
		//We may need to get some query from OpenStack via CloudFoundry to get some of these info.
 		DataCenterWorkingModes dc = this.getDataCenter(activityName, dcName);
		WorkingMode wmToApply = this.getWorkingMode(dc, wmn);
		WorkingMode currWM = this.getCurrentWorkingMode(activityName, dcName);
		String appName = "a";
		int instances = 1, disk = 100, mem = 200;
		boolean ret = false;
		
		Collection<Resource> resources = wmToApply.getResources();
		
		for(Resource res: resources) {
			if(res instanceof CFApplication) {
				CFApplication cfScale = (CFApplication) res;
				instances = cfScale.getInstances();
				disk = (int)cfScale.getDisk().longValue(Units.MB);
				mem = (int)cfScale.getRam().longValue(Units.MB);
				log.debug("tier name: " + cfScale.getName() + " , instances: " + instances + ", disk: " + disk + " , mem:" + mem);
				//need to return scaling operation status
				paasController.scaleApp(appName, instances, disk, mem);
				ret = true;
				if (ret) {
					Collection<Resource> allocatedResources = new ArrayList<>();
				 
					for(String containerName: paasController.getAppContainersId(appName)) {
						CFApplication appInstance = new CFApplication(containerName);
						allocatedResources.add(appInstance);
					}
			
//				 	appInstance.setMemory
//					cfCli.getAppState(appName);
//				 	Query from OpenStack for such information
//				 	VM vmId = new VM();
//				 	vmId.setName();
//				 	Server serverId = new Server();
//				 	serverId.set Name();
					wmToApply.setResources(allocatedResources);
				}
			}
		}
		
		return ret;
    }
}