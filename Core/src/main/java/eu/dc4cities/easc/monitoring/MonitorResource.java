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

import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;

/**
 * Monitor for ressources
 */
public interface MonitorResource {
     // VCPU load in percent
	Amount<Dimensionless> getCPULoad(String resName, String dc);

    // VRAM load in percent
	Amount<Dimensionless> getRAMLoad(String resName, String dc);

    // Disk read accesses bit/second
	Amount<Frequency> getDiskRead(String resName, String dc);
    
    // Disk write accesses bit/second
	Amount<Frequency> getDiskWrite(String resName, String dc);
    
    // network read accesses bit/second
	Amount<Frequency> getNetworkRead(String resName, String dc);
    
    // network write accesses bit/second
	Amount<Frequency> getNetworkWrite(String resName, String dc);

	// power in Watt
	Amount<Power> getPower(String resName, String dc);
	
	//Being in a cloud platform VMs, Containers can get new IP addresses as they migrate
	String getIpAddress(String resName, String dc);
}
