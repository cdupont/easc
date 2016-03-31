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

package eu.dc4cities.easc.energyservice;

import eu.dc4cities.controlsystem.model.easc.PerformanceLevel;
import eu.dc4cities.easc.EASC;
import eu.dc4cities.easc.activity.Activity;
import eu.dc4cities.easc.activity.DataCenterWorkingModes;
import eu.dc4cities.easc.configuration.ConfigReader;
import eu.dc4cities.easc.configuration.Constants;
import eu.dc4cities.easc.configuration.DefaultServerConfig;
import eu.dc4cities.easc.configuration.ServerConfig;
import eu.dc4cities.easc.resource.Server;
import eu.dc4cities.easc.resource.ServerCapacity;
import eu.dc4cities.easc.resource.VirtualResource;
import eu.dc4cities.easc.workingmode.WorkingMode;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Measurable;
import javax.measure.quantity.DataAmount;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;
import javax.measure.unit.SI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.measure.unit.SI.BIT;
import static javax.measure.unit.SI.WATT;


/**
 * Energy service that share the idle powers between several EASCs
 */
public class MultiEASCEnergyService implements EnergyService {

    //the server config (coming from either file or PaaS communicator)
    ServerConfig serverConfig;
    //working mode managers of each easc
    List<EASC> eascs;
    //Energis service
    EnergisEnergyService energis;

    Logger logger = LoggerFactory.getLogger(EnergisEnergyService.class);
    private boolean powerFromEnergies;
    private boolean powerSharingCapability;

    //comparators to choose which server will be used first
    Comparator<Server> ramCompare = (e1, e2) -> e2.getRam().compareTo(e1.getRam());
    Comparator<Server> identity = (e1, e2) -> 1;

    public MultiEASCEnergyService(boolean powerFromEnergies, boolean powerSharingCapability) {
        this.eascs = new ArrayList<>();
        this.powerFromEnergies = powerFromEnergies;
        this.serverConfig = new DefaultServerConfig();
        this.powerSharingCapability = powerSharingCapability;
        if (powerFromEnergies)
            this.energis = new EnergisEnergyService(ConfigReader.readConfiguration(Constants.CONFIG_FOLDER).get());
    }

    public MultiEASCEnergyService(ServerConfig serverConfig, List<EASC> eascs, boolean powerFromEnergies, boolean powerSharingCapability) {
        this.eascs = eascs;
        this.serverConfig = serverConfig;
        this.powerFromEnergies = powerFromEnergies;
        this.powerSharingCapability = powerSharingCapability;
        if (powerFromEnergies)
            this.energis = new EnergisEnergyService(ConfigReader.readConfiguration(Constants.CONFIG_FOLDER).get());
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
        
        if(powerSharingCapability) {

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

        return totalRAM.getEstimatedValue() == 0 ? Amount.valueOf(0, WATT) : sumPowers.times(myRAM.getEstimatedValue()).divide(totalRAM.getEstimatedValue());
    }

    public Amount<Power> getDynPower(String eascName, String activity, String datacenter, WorkingMode wm, Amount<Frequency> bizPerf) {
        Amount<Power> powerWM = null;
        if (powerFromEnergies == true && energis.isEnergisAlive()) {
            logger.info("retrieve WM powers from Energis");
            powerWM = energis.getPredictedPower(eascName, activity, datacenter, wm, bizPerf);
            logger.info("power received from Energis: " + powerWM);
        } else {
        	// Return the power of the lowest performance level matching the required biz perf
    		for (PerformanceLevel perfLevel : toDecreasingLevels(wm.getPerformanceLevels())) {
    			if (powerWM == null || compareBizPerf(bizPerf, perfLevel.getBusinessPerformance()) <= 0) {
    				powerWM = perfLevel.getPower();
    			} else {
    				break;
    			}
    		}
            logger.info("retrieve WM powers from config file: " + powerWM);
        }
        return powerWM != null ? powerWM : Amount.valueOf(0, SI.WATT);
    }

    private List<PerformanceLevel> toDecreasingLevels(List<PerformanceLevel> perfLevels) {
		List<PerformanceLevel> decreasingLevels = new ArrayList<>(perfLevels);
		decreasingLevels.sort(
			new Comparator<PerformanceLevel>() {
				@Override
				public int compare(PerformanceLevel level1, PerformanceLevel level2) {
					return compareBizPerf(level2.getBusinessPerformance(), level1.getBusinessPerformance());
				}
			});
		return decreasingLevels;
	}
	
	private int compareBizPerf(Amount<?> perf1, Amount<?> perf2) {
		// Need a custom method because JScience doesn't support comparisons between Amount<?>
		if (!perf1.getUnit().equals(perf2.getUnit())) {
			throw new IllegalArgumentException("BizPerf units don't match (perf1='" + perf1.getUnit() + "', "
					+ "perf2='" + perf2.getUnit() + "')");
		}
		return Double.compare(perf1.getEstimatedValue(), perf2.getEstimatedValue());
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
        if(this.powerSharingCapability == false)
            return easc.getMonitor().getWMPower(activityName, dcName, easc.getWorkingModeManager().getCurrentWorkingMode(activityName, dcName).getName());

        //Get total power: this total power is the same for all eascs
        Amount<Power> power = easc.getMonitor().getTotalPower(dcName);
        int allActivitiesShareToPowerConsumption = 0;
        int activityShareToPowerConsumption = 0;
        for(EASC e: eascs)
        	for(Activity a: e.getAppConfig().getActivities()) {
        		if(a.getName().equals(activityName)) {
        			activityShareToPowerConsumption = easc.getMonitor().getActivityShareToPowerConsumption(activityName, dcName);
        			allActivitiesShareToPowerConsumption += activityShareToPowerConsumption;
        		} else
        			allActivitiesShareToPowerConsumption +=  e.getMonitor().getActivityShareToPowerConsumption(a.getName(), dcName); //allApps.mapToInt(o -> o.getInstances()).sum();
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

    protected boolean getPowerSharingCapability(){
    	return this.powerSharingCapability;
    }

    protected boolean getPowerFromEnergies(){
    	return this.powerFromEnergies;
    }
}
