package eu.dc4cities.easc.old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dc4cities.easc.monitoring.Monitor;
import eu.dc4cities.easc.resource.Resource;
import eu.dc4cities.easc.resource.CFApplication;
import eu.dc4cities.easc.workingmode.DefaultWorkingModeManager;
import eu.dc4cities.easc.workingmode.WorkingMode;
import eu.dc4cities.easc.OSController;

public class OSWorkingModeManager extends DefaultWorkingModeManager {
    private Logger log = LoggerFactory.getLogger(OSWorkingModeManager.class);
	private Monitor monitor;
	private OSController osCli;
	
	public OSWorkingModeManager(EnergyService es, List<WorkingMode> workingModes, Monitor montr) {
		super(esm, workingModes);
		monitor = montr;
	}

	@Override
    public synchronized boolean applyWorkingMode(String wmn) {
		//No other threads in this execution context will be allowed to run
		//, until all instances of the application WMs get running
		osCli = OSController.getInstance();
		//Get workingMode appId, containers info, VMID, ServerID (from OS), disk, and memory capacity
		//We may need to get some query from OpenStack via CloudFoundry to get some of these info.
		WorkingMode wmToApply = getWorkingModeByName(wmn).get();
		WorkingMode currWM = getWorkingModeByName(this.getCurrentWorkingMode()).get();

		log.debug("Applying working mode " + wmn + " using CF Java API ");

		boolean ret = false;

		monitor.cleanUpMonitorResources();
		this.stopStartCFApps(currWM, wmToApply);
		this.scaleCFApps(wmToApply);
		this.updateResourcesToMonitor(wmToApply);
		//Change current WM
		this.currentWm = wmn;
		ret = true;
		return ret;
    }

	private void updateResourcesToMonitor(WorkingMode wmToApply) {
		for(Resource res: wmToApply.getResources()) {
			if(res instanceof CFApplication) {
				CFApplication cfApp = (CFApplication) res;
				int instances = cfApp.getInstances();
				String appName = cfApp.getName();

				log.debug("appName: " + appName);
			}
		}
	}

	private void scaleCFApps(WorkingMode wmToApply) {
		String appName;
		int instances = 1 , disk = 100, mem = 200;
		for(Resource res: wmToApply.getResources()) {
			if(res instanceof CFApplication) {
				CFApplication cfScale = (CFApplication) res;
				instances = cfScale.getInstances();
				disk = cfScale.getDisk();
				mem = cfScale.getRam();
				appName = cfScale.getName();
				log.debug("Scaling " + appName + " to i: " + instances + " d: " + disk + " m: " + mem);

				//need to return scaling operation status probably?
				osCli.scaleApp(appName, instances, disk, mem);
			}
		}
	}

	private void stopStartCFApps(WorkingMode currWM, WorkingMode wmToApply) {
		Collection<String> appsToStop = new ArrayList<>();
		Collection<String> appsToStart = new ArrayList<>();
	
		//Resource type is of CFApplication type
		//Adding all resources to stop from the current VM
		for(Resource res: currWM.getResources())
			appsToStop.add(res.getName());
		
		for(Resource appTobeScaled: wmToApply.getResources())
			appsToStart.add(appTobeScaled.getName());
		
		appsToStop.removeAll(appsToStart);

		//Stop all other apps we don't need at this WM
		for(String app: appsToStop)
			osCli.stopApp(app);
	}
}