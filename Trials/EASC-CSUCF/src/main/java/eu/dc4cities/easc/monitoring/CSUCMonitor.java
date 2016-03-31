package eu.dc4cities.easc.monitoring;

import eu.dc4cities.easc.resource.Resource;
import eu.dc4cities.easc.monitoring.zabbix.MonitorZabbix;
import eu.dc4cities.easc.monitoring.iaas.UtilsOpenNebula;
import eu.dc4cities.easc.energyservice.MultiEASCEnergyServiceCSUC;

import static javax.measure.unit.SI.WATT;
import javax.measure.unit.Unit;
import javax.measure.quantity.Power;

import org.jscience.physics.amount.Amount;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Monitor interface that should be implemented by trials
 */
public class CSUCMonitor extends Monitor {

//    private MonitorZabbix mz= new MonitorZabbix();

	public CSUCMonitor() {
		this.init();
	}

	// to leave it to the trial side to add its specific resources
	@Override
	public void init(){
		this.monitorActivityAndWorkingmode = new CSUCMonitorActivityWorkingMode();
	}
	// to leave it to the trial side to add its specific resources
	@Override
	public void addResourceToMonitor(Resource res){
	}

	@Override
	public int getRealtimeCumulativeBusinessObjective(){
		return 0;
	}
	
	@Override
        public Amount<Power> getTotalPower(String dcName){
		MonitorZabbix mz = new MonitorZabbix(dcName);

		Amount<Power> totalPower = Amount.valueOf(0, (Unit<Power>) WATT);
                LocalDateTime timePoint = LocalDateTime.now();
                long till = timePoint.toEpochSecond(ZoneOffset.of("+01:00"));
                long from = till-300;
		int totalpower;

		if (dcName.equals("csuc_barcelona")){
        	        UtilsOpenNebula utone = new UtilsOpenNebula();
                	List<String> hostsEnable = utone.HostsEnable();

	                Iterator<String> itHosts;
        	        Amount<Power> aux;
                	int quantEnable=hostsEnable.size();

	                if (quantEnable>0){
        	                totalpower=mz.getTotalPower("Montserrat", from, till);
				List<String> listAux= new ArrayList<String>();
				listAux.add("cluster00");
				listAux.add("cluster01");
        	                int powernodes=mz.getPowerNodes(from, till, listAux);
                	        int auxi=totalpower-powernodes;
                        	if (quantEnable==2) auxi=auxi/14;
	                        else auxi=(auxi-(auxi/14))/13;
        	                aux=Amount.valueOf(auxi, (Unit<Power>) WATT);
                	        itHosts=hostsEnable.iterator();
                        	while (itHosts.hasNext()) totalPower=totalPower.plus(Amount.valueOf(mz.getServerPower(itHosts.next(), from, till), (Unit<Power>) WATT).plus(aux));
	                }
		}else{
			totalpower=mz.getTotalPower("PDU", from, till);
                        //li restem els watts de consum de la PDU
                        totalpower=totalpower-137;
                        if (totalpower<0) totalpower=0;
			totalPower=Amount.valueOf(totalpower, (Unit<Power>) WATT);
		}

                return totalPower;
        }
	
	//For trial that need to use power sharing capability for multiple EASCs, and multiple activities
	public int getActivityShareToPowerConsumption(String activityName, String dcName){
		MultiEASCEnergyServiceCSUC multieascservice = (MultiEASCEnergyServiceCSUC) this.getEnergyService();
		double percentage = multieascservice.getPercentage("EASC-CSUC",activityName, dcName);
		return ((int) getTotalPower(dcName).times(percentage).longValue((Unit<Power>) WATT));
	}

}
