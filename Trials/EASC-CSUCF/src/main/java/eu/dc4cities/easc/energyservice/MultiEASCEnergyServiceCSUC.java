package eu.dc4cities.easc.energyservice;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.measure.Measurable;
import javax.measure.quantity.DataAmount;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;
import javax.measure.unit.SI;

import eu.dc4cities.easc.EASC;
import eu.dc4cities.easc.activity.Activity;
import eu.dc4cities.easc.activity.DataCenterWorkingModes;
import eu.dc4cities.easc.configuration.*;
import eu.dc4cities.easc.resource.Server;
import eu.dc4cities.easc.resource.VM;
import eu.dc4cities.easc.monitoring.CSUCMonitor;

import eu.dc4cities.easc.resource.ServerCapacity;
import eu.dc4cities.easc.resource.VirtualResource;
import org.jscience.physics.amount.Amount;

import eu.dc4cities.easc.workingmode.WorkingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.measure.unit.SI.*;


/**
 * Energy service that share the idle powers between several EASCs
 */
public class MultiEASCEnergyServiceCSUC extends MultiEASCEnergyService {

    public MultiEASCEnergyServiceCSUC(boolean powerFromEnergies, boolean powerSharingCapability) {
	super(powerFromEnergies, powerSharingCapability);
    }

    public MultiEASCEnergyServiceCSUC(ServerConfig serverConfig, List<EASC> eascs, boolean powerFromEnergies, boolean powerSharingCapability) {
	super(serverConfig,eascs,powerFromEnergies,powerSharingCapability);
    }

    @Override
    //For Activity Specification
    //Calculates the power of the WM based on infrastructure situation
    //linear version: all servers and all containers are the same size
    //contribution to static power is the same for all apps
    //overhead due to DEA is ignored
    public Amount<Power> getPredictedPower(String eascName, String activity, String datacenter, WorkingMode wm, Amount<Frequency> bizPerf) {

        logger.debug("getPredictedPower: eascName=" + eascName + ", activity=" + activity + ", datacenter=" + datacenter
                + ", wm=" + wm.getName() + ", bizPerf=" + bizPerf);

        // retrieve dynamic power (Power of containers)
        Amount<Power> predictedPower = getDynPower(eascName, activity, datacenter, wm, bizPerf);
        
        if(getPowerSharingCapability()) {
            predictedPower = predictedPower.plus(getStaticPower(eascName, activity, datacenter, wm));
        }

        logger.debug("getPredictedPower: " + predictedPower);
        return predictedPower;
    }

    private Amount<Power> getStaticPower(String eascName, String activity, String datacenter, WorkingMode wm) {

        // get apps from other activities running
        Collection<VirtualResource> allApps = getApps(eascName, activity, datacenter);

        //add the apps from our EASC
        Collection<VirtualResource> myApps = wm.getResources().stream()
                .filter(a -> a instanceof VirtualResource)
                .map(a -> (VirtualResource) a)
                .collect(Collectors.toList());
        allApps.addAll(myApps);

//hem de pillar els servidors del data center corresponent 
Collection<Server> myServers = wm.getResources().stream()
                .filter(a -> a instanceof Server)
                .map(a -> (Server) a)
                .collect(Collectors.toList());

Collection<Server> configServers = serverConfig.getServers();
HashMap<String,Server> hashServer = new HashMap<String,Server>();
Collection<Server> datacenterServers = new ArrayList<Server>();

Iterator<Server> it = configServers.iterator();
while (it.hasNext()){
	Server tempserver = it.next();
	hashServer.put(tempserver.getName(),tempserver);
}

it = myServers.iterator();
while (it.hasNext()){
	datacenterServers.add(hashServer.get(it.next().getName()));
}

//        Collection<Server> servers = getUsedServers(serverConfig.getServers(), allApps);
        Collection<Server> servers = getUsedServers(datacenterServers, allApps);
        Collection<Amount<Power>> pIdles = servers.stream()
                .map(Server::getPidle)
                .collect(Collectors.toList());

        //Compute static power
        Amount<DataAmount> myRAM = myApps.stream().map(a -> a.getRam().times(a.getInstances())).reduce(Amount.valueOf(0, BIT), Amount::plus);
        Amount<DataAmount> totalRAM = allApps.stream().map(a -> a.getRam().times(a.getInstances())).reduce(Amount.valueOf(0, BIT), Amount::plus);

        Amount<Power> sumPowers = pIdles.stream().reduce(Amount.valueOf(0, WATT), Amount::plus);

	double percentage = 0.0;
	int myCPU = myApps.stream().map(a -> a.getVCpus()).mapToInt(Integer::intValue).sum();
	int totalCPU = allApps.stream().map(a -> a.getVCpus()).mapToInt(Integer::intValue).sum();
	if ((totalRAM.getEstimatedValue() > 0)&&(totalCPU>0))
	percentage=(((double)myCPU/totalCPU)+(myRAM.getEstimatedValue()/totalRAM.getEstimatedValue()))/2;

        return totalRAM.getEstimatedValue() == 0 ? Amount.valueOf(0, WATT) : sumPowers.times(percentage);
    }

public double getPercentage(String eascName, String activity, String datacenter){

        EASC easc = eascs.stream()
                .filter(e -> e.getAppConfig().getName().equals(eascName))
                .findFirst().get();
	WorkingMode wm = easc.getWorkingModeManager().getCurrentWorkingMode(activity, datacenter);
	
       // get apps from other activities running
        Collection<VirtualResource> allApps = getApps(eascName, activity, datacenter);

        //add the apps from our EASC
        Collection<VirtualResource> myApps = wm.getResources().stream()
                .filter(a -> a instanceof VirtualResource)
                .map(a -> (VirtualResource) a)
                .collect(Collectors.toList());
        allApps.addAll(myApps);

	Amount<DataAmount> myRAM = myApps.stream().map(a -> a.getRam().times(a.getInstances())).reduce(Amount.valueOf(0, BIT), Amount::plus);
        Amount<DataAmount> totalRAM = allApps.stream().map(a -> a.getRam().times(a.getInstances())).reduce(Amount.valueOf(0, BIT), Amount::plus);
        double percentage = 0.0;
        int myCPU = myApps.stream().map(a -> a.getVCpus()).mapToInt(Integer::intValue).sum();
        int totalCPU = allApps.stream().map(a -> a.getVCpus()).mapToInt(Integer::intValue).sum();
        if ((totalRAM.getEstimatedValue() > 0)&&(totalCPU>0))
        percentage=(((double)myCPU/totalCPU)+(myRAM.getEstimatedValue()/totalRAM.getEstimatedValue()))/2;
return percentage;
}


    //get all CFApps for the other running activities
    private Collection<VirtualResource> getApps(String eascName, String activity, String datacenter) {

//Hauria de pillar totes les virtuals de altres activitats pero del mateix datacenter

        //Get current working mode for all EASCs except ours
        Collection<WorkingMode> wms = new ArrayList<>();
        for(EASC easc : eascs) {
            for(Activity act : easc.getAppConfig().getActivities()) {
                for(DataCenterWorkingModes dcwms : act.getDataCenters()) {
			if (dcwms.getDataCenterName().equals(datacenter)) {
                    //Exclude our own working mode
                    if(! (easc.getConfig().getEASCName().equals(eascName) &&
                          act.getName().equals(activity) &&
                          dcwms.getDataCenterName().equals(datacenter))) {
                        wms.add(dcwms.getCurrentWorkingMode());
                    }
			}
                }
            }
        }

        //Get apps from those WMs
        Stream<VirtualResource> apps = wms.stream().flatMap(mywm -> mywm.getResources()
                .stream().filter(a -> a instanceof VirtualResource)
                .map(a -> (VirtualResource) a));
        return apps.collect(Collectors.toList());
    }

    //For Read Monitoring API
    public Amount<Power> getActivityPowerMonitoring(String eascName, String activityName, String dcName) {
        //it should also support other trials
        //that are not based on this division, like HP trial. In this case it should call
        //monitor.getWMPower method that is implemented by trial.

        //Get EASC with name
        EASC easc = eascs.stream()
                .filter(e -> e.getAppConfig().getName().equals(eascName))
                .findFirst().get();

        //Get total power: this total power is the same for all eascs
        Amount<Power> power = easc.getMonitor().getTotalPower(dcName);
        int allActivitiesShareToPowerConsumption = 0;
        int activityShareToPowerConsumption = 0;
        for(EASC e: eascs)
                for(Activity a: e.getAppConfig().getActivities()) {
		  for(DataCenterWorkingModes dcwms : a.getDataCenters()) {
			if (dcwms.getDataCenterName().equals(dcName)){
                          if(a.getName().equals(activityName)) {
                                activityShareToPowerConsumption = easc.getMonitor().getActivityShareToPowerConsumption(activityName, dcName);
                                allActivitiesShareToPowerConsumption += activityShareToPowerConsumption;
                          } else
                                allActivitiesShareToPowerConsumption +=  e.getMonitor().getActivityShareToPowerConsumption(a.getName(), dcName); //allApps.mapToInt(o -> o.getInstances()).sum();
			}
		  }
                }

        if(allActivitiesShareToPowerConsumption == 0)
            allActivitiesShareToPowerConsumption = 2;
        Amount<Power> powerFinal = power.times(activityShareToPowerConsumption).divide(allActivitiesShareToPowerConsumption);

        return powerFinal;
    }


}
