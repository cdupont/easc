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
import javax.measure.unit.Unit;

/**
 * A server resource
 */
public class Server implements Resource {

	private Amount<DataAmount> disk;
	private Amount<DataAmount> ram;
	private Amount<Power> Pidle;
	private Integer nbCpus;
	private String name;
	private String ip;
	
	public Server() {
	}

	public Server(String name) {
		this.name = name;
	}

	public Server(Amount<DataAmount> disk, Amount<DataAmount> ram, Amount<Power> Pidle, Integer nbCpus, String name) {
		this.disk = disk;
		this.ram = ram;
		this.name = name;
		this.Pidle = Pidle;
        this.nbCpus = nbCpus;
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

	public void setDisk(int disk, Unit<DataAmount> unit) {
		this.disk = Amount.valueOf(disk, unit);
	}

	public void setDisk(Amount<DataAmount> disk) {
		this.disk = disk;
	}
	public Amount<DataAmount> getRam() {
		return ram;
	}

	public void setRam(int ram, Unit<DataAmount> unit) {
		this.ram = Amount.valueOf(ram, unit);
	}

	public void setRam(Amount<DataAmount> ram) {
		this.ram = ram;
	}

	public Amount<Power> getPidle() {
		return Pidle;
	}

	public void setPidle(Amount<Power> pidle) {
		Pidle = pidle;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getNbCpus() {
		return nbCpus;
	}

	public void setNbCpus(Integer nbCpus) {
		this.nbCpus = nbCpus;
	}

	@Override
	public String toString() {
		return name + ", " + " " + ip + ", disk: " + disk + ", mem:" + ram;
	}

	public boolean equals(Server o) {
		if(name.equals(o.getName()) && ip.equals(o.getIp()))
			return true;
		
		return false;
	}
}
