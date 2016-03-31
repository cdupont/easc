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

package eu.dc4cities.easc.resource;

import org.jscience.physics.amount.Amount;

import javax.measure.quantity.DataAmount;
import javax.measure.quantity.Power;

/**
 * the resource for PaaS is the "CFApp" composed of several containers
 */
public class CFApplication implements VirtualResource {
	private Amount<DataAmount> disk;
	private Amount<DataAmount> ram;
	private int instances;
	private String name;
	private Amount<Power> defaultPower;
	
	public CFApplication() {
	}

	public CFApplication(String name) {
		this.name = name;
	}

	public CFApplication(Amount<DataAmount> disk, Amount<DataAmount> ram, int instances, String name, Amount<Power> defaultPower) {
		this.disk = disk;
		this.ram = ram;
		this.instances = instances;
		this.name = name;
		this.defaultPower = defaultPower;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Amount<DataAmount> getDisk() {
		return disk;
	}

	public void setDisk(Amount<DataAmount> disk) {
		this.disk = disk;
	}

	public Amount<DataAmount> getRam() {
		return ram;
	}

	@Override
	public Integer getVCpus() {
		//CF containers doesn't have VCPU allocation.
		return null;
	}

	public void setRam(Amount<DataAmount> ram) {
		this.ram = ram;
	}

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}

	public Amount<Power> getDefaultPower() {
		return defaultPower;
	}

	public void setDefaultPower(Amount<Power> defaultPower) {
		this.defaultPower = defaultPower;
	}

}
