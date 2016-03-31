package eu.dc4cities.easc.old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.dc4cities.easc.OSController;
import eu.dc4cities.easc.old.OSWorkingModeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import eu.dc4cities.easc.resource.Resource;
import eu.dc4cities.easc.workingmode.DefaultWorkingModeManager;
import eu.dc4cities.easc.workingmode.WorkingMode;

public class OSWorkingModeManagerSysCommand extends DefaultWorkingModeManager {
    private Logger log = LoggerFactory.getLogger(OSWorkingModeManager.class);
	public OSWorkingModeManagerSysCommand(EnergyService es,
			List<WorkingMode> workingModes) {
		super(esm, workingModes);
	}

	@Override
    public boolean applyWorkingMode(String wmn) {
	    log.debug("Applying working mode, parsing systemCommand" + wmn);
	    OSController cfCli = OSController.getInstance();
		//Get workingMode appId, containers info, VMID, ServerID (from OS), disk, and memory capacity
		//We may need to get some query from OpenStack via CloudFoundry to get some of these info.
		Optional<WorkingMode> wm = getWorkingModeByName(wmn);
		WorkingMode wmInstance = wm.get();
		String appName = "a";
		int instances = 1 , disk = 100, mem = 200;
		boolean ret = false;
// in client		cf scale helloworld-php -i 4 -d 100M/G -m 128M/G
// in AppConfig	disk and mem must be specified in MB, cf scale helloworld-php -i 4 -d 100 -m 128
		String tiersScale = wmInstance.getActuator().getSystemCommand().trim();
		log.debug(tiersScale);
		
		String[] tiersScales = tiersScale.split(";");
		for(String cfScale: tiersScales) {
			if(cfScale.trim().startsWith("cf scale ")) {
				String[] pieces = cfScale.split(" ");
				appName = pieces[2];

				log.debug( "app:" + appName);
			
				if(pieces[3].equals("-i"))
					instances = Integer.parseInt(pieces[4]);
				else
					log.debug( "3:" + pieces[3]);
			
				if(pieces[5].equals("-d"))
					disk = Integer.parseInt(pieces[6]);
			
				if(pieces[7].equals("-m"))
					mem = Integer.parseInt(pieces[8]);

				log.debug(instances + " " + disk + " " + mem);
				//need to return scaling operation status
//				cfCli.scaleApp(appName, instances, disk, mem);
				ret = true;
				if (ret) {
					Collection<Resource> resources = new ArrayList<>();
				 
//		        for(String containerName: cfCli.getAppContainers(appName)) {
//					Container appInstance = new Container(containerName);
//				    resources.add(appInstance);
//				}
			
//				 appInstance.setMemory
//				 cfCli.getAppState(appName);

//				 Query from OpenStack for such information
//				 VM vmId = new VM();
//				 vmId.setName();
//				 Server serverId = new Server();
//				 serverId.set Name();
					wmInstance.setResources(resources);
				}
			}
		} 
		
		return ret;
    }
}