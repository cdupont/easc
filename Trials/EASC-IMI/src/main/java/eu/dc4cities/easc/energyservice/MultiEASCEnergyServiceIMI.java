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
public class MultiEASCEnergyServiceIMI extends MultiEASCEnergyService {

    public MultiEASCEnergyServiceIMI(boolean powerFromEnergies, boolean powerSharingCapability) {
	super(powerFromEnergies, powerSharingCapability);
    }

    public MultiEASCEnergyServiceIMI(ServerConfig serverConfig, List<EASC> eascs, boolean powerFromEnergies, boolean powerSharingCapability) {
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

        Collection<Server> servers = getUsedServers(serverConfig.getServers(), allApps);
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


    public Amount<Power> getDynPower(String eascName, String activity, String datacenter, WorkingMode wm, Amount<Frequency> bizPerf) {

        Amount<Power> powerWM = Amount.valueOf(0, SI.WATT);

        if (getPowerFromEnergies() == true && energis.isEnergisAlive()) {
            logger.info("retrieve WM powers from Energis");
            powerWM = energis.getPredictedPower(eascName, activity, datacenter, wm, bizPerf);
            logger.info("power received from Energis: " + powerWM);
        } else {
        	//TODO: to match the bizPerf
            powerWM = wm.getPerformanceLevels().get(0).getPower();
            logger.info("retrieve WM powers from config file: " + powerWM);
        }

        return powerWM;
    }


    //get all CFApps for the other running activities
    private Collection<VirtualResource> getApps(String eascName, String activity, String datacenter) {

        //Get current working mode for all EASCs except ours
        Collection<WorkingMode> wms = new ArrayList<>();
        for(EASC easc : eascs) {
            for(Activity act : easc.getAppConfig().getActivities()) {
                for(DataCenterWorkingModes dcwms : act.getDataCenters()) {
                    //Exclude our own working mode
                    if(! (easc.getConfig().getEASCName().equals(eascName) &&
                          act.getName().equals(activity) &&
                          dcwms.getDataCenterName().equals(datacenter))) {
                        wms.add(dcwms.getCurrentWorkingMode());
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

    //computes the number of servers needed overall
    //simple bin packing
    public Collection<Server> getUsedServers(Collection<Server> servers, Collection<VirtualResource> vrs) {

        Collection<Server> sortedServers = servers.stream()
                .sorted(identity)
                .collect(Collectors.toList());

        //Mapping of server RAM occupancy
        Map<Server, ServerCapacity> mapping = new HashMap<>();
        sortedServers.stream().forEach(s -> mapping.put(s, new ServerCapacity()));

        for(VirtualResource vr : vrs) {
            for(int i=0; i<vr.getInstances(); i++) {
                boolean withCPU = vr.getVCpus() != null;
                for(Server s : sortedServers) {

                    ServerCapacity serverCapacity = mapping.get(s);
                    //compare the RAM remained free in the server to the size of the app
                    Amount<DataAmount> freeRam = s.getRam().minus(serverCapacity.getRam());

                    if (freeRam.compareTo((Measurable) vr.getRam())>0 || freeRam.approximates(vr.getRam())){

                        //increment this server occupancy
                        if(withCPU) {
                            if(s.getNbCpus() - serverCapacity.getVCpus() >= vr.getVCpus()) {
                                serverCapacity.setRam(serverCapacity.getRam().plus(vr.getRam()));
                                serverCapacity.setVCpus(serverCapacity.getVCpus() + vr.getVCpus());
                                break;
                            }
                        } else {
                            serverCapacity.setRam(serverCapacity.getRam().plus(vr.getRam()));
                            break;
                        }
                    }
                }
            }
        }

        Collection<Server> usedServers = mapping.entrySet().stream()
                .filter(e -> e.getValue().getRam().isGreaterThan(Amount.valueOf(0, SI.BIT)))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        usedServers.forEach(s -> logger.debug("Used server: " + s.getName()));

        return usedServers;
    }

    @Override
    //For Read Monitoring API
    public Amount<Power> getActivityPowerMonitoring(String eascName, String activityName, String dcName) {
        //it should also support other trials
        //that are not based on this division, like HP trial. In this case it should call
        //monitor.getWMPower method that is implemented by trial.

        //Get EASC with name
        EASC easc = eascs.stream()
                .filter(e -> e.getAppConfig().getName().equals(eascName))
                .findFirst().get();
        
        //if they don't specify powerSharingCapability, then getWMPower from monitoring impl will be used
        //like HP trial with two activities will not use energy sharing
        if(getPowerSharingCapability() == false)
            return easc.getMonitor().getWMPower(activityName, dcName, easc.getWorkingModeManager().getCurrentWorkingMode(activityName, dcName).getName());

        //Get total power: this total power is the same for all eascs
        Amount<Power> power = easc.getMonitor().getTotalPower();

        int activityShareToPowerConsumption = easc.getMonitor().getActivityShareToPowerConsumption(activityName);

        int allActivitiesShareToPowerConsumption = 0;
        for(EASC e: eascs){
        	for(Activity a: e.getAppConfig().getActivities()) {
        		allActivitiesShareToPowerConsumption +=  e.getMonitor().getActivityShareToPowerConsumption(a.getName()); //allApps.mapToInt(o -> o.getInstances()).sum();
        	}
        }
        if(allActivitiesShareToPowerConsumption == 0)
            allActivitiesShareToPowerConsumption = 2;
        Amount<Power> powerFinal = power.times(activityShareToPowerConsumption).divide(allActivitiesShareToPowerConsumption);

        return powerFinal;
    }

    public List<EASC> getEascs() {
        return eascs;
    }

    public void setEascs(List<EASC> eascs) {
        this.eascs = eascs;
    }

    @Override
    public void addEasc(EASC easc) {
        this.eascs.add(easc);
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig sc) {
        serverConfig = sc;
    }

}
