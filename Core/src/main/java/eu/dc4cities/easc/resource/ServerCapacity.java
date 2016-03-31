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
import javax.measure.unit.SI;

/**
 * A server capacity
 */
public class ServerCapacity {

	private Amount<DataAmount> disk;
	private Amount<DataAmount> ram;
	private Integer vCpus;

	public ServerCapacity() {
        disk = Amount.valueOf(0, SI.BIT);
        ram = Amount.valueOf(0, SI.BIT);
        vCpus = 0;
	}

	public ServerCapacity(Amount<DataAmount> disk, Amount<DataAmount> ram, Integer vCpus) {
		this.disk = disk;
		this.ram = ram;
		this.vCpus = vCpus;
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

	public void setRam(Amount<DataAmount> ram) {
		this.ram = ram;
	}

	public Integer getVCpus() {
		return vCpus;
	}

	public void setVCpus(Integer vCpus) {
		this.vCpus = vCpus;
	}

}
