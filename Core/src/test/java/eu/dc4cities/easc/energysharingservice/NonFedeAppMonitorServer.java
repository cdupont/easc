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

package eu.dc4cities.easc.energysharingservice;

import eu.dc4cities.easc.monitoring.MonitorResource;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;

import static javax.measure.unit.NonSI.PERCENT;
import static javax.measure.unit.SI.HERTZ;
import static javax.measure.unit.SI.WATT;

//this class should have a member for monitoring the infrastructure like Zabbix monitoring system
//then resName will be used by that monitoring infrastructure to return appropriate monitoring metric
//if DC is a remote datacenter we need to connect to that DC monitoring system to get info. This will be up to 
//a Trial how the remote DCs would like to provide monitoring

public class NonFedeAppMonitorServer implements MonitorResource {
	@Override
	public Amount<Dimensionless> getCPULoad(String resName, String dc) {
		if(dc.equals("DC1"))
			return Amount.valueOf(10, PERCENT);
		else if(dc.equals("DC2"))
			return Amount.valueOf(20, PERCENT);
		
		return Amount.valueOf(50, PERCENT);
	}

	@Override
	public Amount<Dimensionless> getRAMLoad(String resName, String dc) {
		if(dc.equals("DC1"))
			return Amount.valueOf(10, PERCENT);
		else if(dc.equals("DC2"))
			return Amount.valueOf(20, PERCENT);
		
		return Amount.valueOf(50, PERCENT);
	}

	@Override
	public Amount<Frequency> getDiskRead(String resName, String dc) {
		if(dc.equals("DC1"))
			return Amount.valueOf(10, HERTZ);
		else if(dc.equals("DC2"))
			return Amount.valueOf(20, HERTZ);
		
		return Amount.valueOf(50, HERTZ);
	}

	@Override
	public Amount<Frequency> getDiskWrite(String resName, String dc) {
		if(dc.equals("DC1"))
			return Amount.valueOf(10, HERTZ);
		else if(dc.equals("DC2"))
			return Amount.valueOf(20, HERTZ);
		return Amount.valueOf(50, HERTZ);
	}

	@Override
	public Amount<Frequency> getNetworkRead(String resName, String dc) {
		if(dc.equals("DC1"))
			return Amount.valueOf(10, HERTZ);
		else if(dc.equals("DC2"))
			return Amount.valueOf(20, HERTZ);
		
		return Amount.valueOf(50, HERTZ);
	}

	@Override
	public Amount<Frequency> getNetworkWrite(String resName, String dc) {
		if(dc.equals("DC1"))
			return Amount.valueOf(10, HERTZ);
		else if(dc.equals("DC2"))
			return Amount.valueOf(20, HERTZ);
		
		return Amount.valueOf(50, HERTZ);
	}

	@Override
	public Amount<Power> getPower(String resName, String dc) {
		if(dc.equals("DC1"))
			return Amount.valueOf(10, WATT);
		else if(dc.equals("DC2"))
			return Amount.valueOf(20, WATT);
		return Amount.valueOf(50, WATT);
	}

	@Override
	public String getIpAddress(String resName, String dc) {
		if(dc.equals("DC1"))
			return "10.10.10.10";
		else if(dc.equals("DC2"))
			return  "20.20.20.20";
		return "13.13.13.13";
	}

}