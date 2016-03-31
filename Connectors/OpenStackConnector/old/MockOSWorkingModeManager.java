package eu.dc4cities.easc.old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import eu.dc4cities.easc.resource.Resource;
import eu.dc4cities.easc.resource.CFApplication;
import eu.dc4cities.easc.workingmode.DefaultWorkingModeManager;
import eu.dc4cities.easc.workingmode.WorkingMode;

public class MockOSWorkingModeManager extends DefaultWorkingModeManager {
    private Logger log = LoggerFactory.getLogger(MockOSWorkingModeManager.class);
	
    public MockOSWorkingModeManager(EnergyService es,
			List<WorkingMode> workingModes) {
		super(esm, workingModes);
	}

	@Override
    public boolean applyWorkingMode(String wmn) {
	    log.debug("Applying working mode, parsing systemCommand" + wmn);
	    MockOSController cfCli = MockOSController.getInstance();
		//Get workingMode appId, containers info, VMID, ServerID (from OS), disk, and memory capacity
		//We may need to get some query from OpenStack via CloudFoundry to get some of these info.
		Optional<WorkingMode> wm = getWorkingModeByName(wmn);
		WorkingMode wmInstance = wm.get();
		String appName = "a";
		int instances = 1 , disk = 100, mem = 200;
		boolean ret = false;
		
		Collection<Resource> resources = wmInstance.getResources();
		
		for(Resource res: resources) {
			if(res instanceof CFApplication) {
				CFApplication cfScale = (CFApplication) res;
				instances = cfScale.getInstances();
				disk = cfScale.getDisk();
				mem = cfScale.getRam();
				log.debug("tier name: " + cfScale.getName() + " , instances: " + instances + ", disk: " + disk + " , mem:" + mem);
				//need to return scaling operation status
				cfCli.scaleApp(appName, instances, disk, mem);
				ret = true;
				if (ret) {
					Collection<Resource> allocatedResources = new ArrayList<>();
				 
					for(String containerName: cfCli.getAppContainers(appName)) {
						CFApplication appInstance = new CFApplication(containerName);
						resources.add(appInstance);
					}
			
//				 	appInstance.setMemory
//					cfCli.getAppState(appName);
//				 	Query from OpenStack for such information
//				 	VM vmId = new VM();
//				 	vmId.setName();
//				 	Server serverId = new Server();
//				 	serverId.set Name();
					wmInstance.setResources(allocatedResources);
				}
			}
		}
		
		return ret;
    }
}