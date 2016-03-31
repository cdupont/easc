package eu.dc4cities.easc.monitoring;

import eu.dc4cities.easc.resource.Resource;
import eu.dc4cities.easc.monitoring.zabbix.MonitorZabbix;
import eu.dc4cities.easc.monitoring.iaas.UtilsOpenNebula;
import eu.dc4cities.easc.energyservice.MultiEASCEnergyServiceIMI;

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
public class IMIMonitor extends Monitor {

    private MonitorZabbix mz= new MonitorZabbix();

	public IMIMonitor() {
		this.init();
	}

	// to leave it to the trial side to add its specific resources
	@Override
	public void init(){
		this.monitorActivityAndWorkingmode = new IMIMonitorActivityWorkingMode();
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
        public Amount<Power> getTotalPower(){
                UtilsOpenNebula utone = new UtilsOpenNebula();
                List<String> hostsEnable = utone.HostsEnable();

                Iterator<String> itHosts;
                Amount<Power> totalPower = Amount.valueOf(0, (Unit<Power>) WATT);
                Amount<Power> aux;
                int quantEnable=hostsEnable.size();
                LocalDateTime timePoint = LocalDateTime.now();
                long till = timePoint.toEpochSecond(ZoneOffset.of("+01:00"));
                long from = till-300;

                if (quantEnable>0){
                        int totalpower=mz.getTotalPower("PDU", from, till);
			//li restem els watts de consum de la PDU
			totalpower=totalpower-137;
			if (totalpower<0) totalpower=0;
                        totalPower=Amount.valueOf(totalpower, (Unit<Power>) WATT);
                }

                return totalPower;
        }

	//For trial that need to use power sharing capability for multiple EASCs, and multiple activities
	public int getActivityShareToPowerConsumption(String activityName){
		MultiEASCEnergyServiceIMI multieascservice = (MultiEASCEnergyServiceIMI) this.getEnergyService();
		double percentage = multieascservice.getPercentage("EASC-IMI",activityName, "imi_barcelona");
		return ((int) getTotalPower().times(percentage).longValue((Unit<Power>) WATT));
	}

}
