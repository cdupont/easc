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

package eu.dc4cities.easc.monitoring;

import eu.dc4cities.easc.cloudcontrollers.PaaSController;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;

import static javax.measure.unit.NonSI.PERCENT;


public class MonitorContainerImpl implements MonitorResource {
	private Logger logger = LoggerFactory.getLogger(MonitorContainerImpl.class);
	private PaaSController paasController;

	public MonitorContainerImpl(PaaSController paasController){
		this.paasController = paasController;
	}

	@Override
	public Amount<Dimensionless> getCPULoad(String resName, String dc) {

		String appName = resName.split(":")[0];
		String containerId = resName.split(":")[1];
		double load = paasController.getContainerVCPULoad(appName, containerId);
		logger.debug("appName:containerId (" + appName + ":" + containerId + "), CPULoad: " + load);
		return Amount.valueOf(load, PERCENT);
	}


	@Override
	public Amount<Dimensionless> getRAMLoad(String resName, String dc) {
		String appName = resName.split(":")[0];
		String containerId = resName.split(":")[1];
		double load = paasController.getContainerVRAMLoad(appName, containerId);
		logger.debug("appName:containerId (" + appName + ":"+ containerId + "), RAMLoad: " +  load);
		return Amount.valueOf(load, PERCENT);
	}
	
	@Override
	public String getIpAddress(String resName, String dc) {
		String appName = resName.split(":")[0];
		String containerId = resName.split(":")[1];
		String ip = paasController.getContainerIP(appName, containerId).get();
		logger.debug("appName:containerId (" + appName + ":"+ containerId + "), IP: " +  ip);
		
		String serverIp = paasController.getContainerServer(appName, containerId).get().getIp();
		logger.debug("appName:containerId (" + appName + ":"+ containerId + "), serverIP: " +  serverIp);
		return ip;
	}


	@Override
	public Amount<Frequency> getDiskRead(String resName, String dc) {
//		this.cfCli = CFController.getInstance();
//		double load = cfCli.getContainerDiskLoad(appName, resName);
//		return Amount.valueOf(load, Frequency);
		return null;
	}


	@Override
	public Amount<Frequency> getDiskWrite(String resName, String dc) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Amount<Frequency> getNetworkRead(String resName, String dc) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Amount<Frequency> getNetworkWrite(String resName, String dc) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Amount<Power> getPower(String resName, String dc) {
		// TODO Auto-generated method stub
		return null;
	}
}
