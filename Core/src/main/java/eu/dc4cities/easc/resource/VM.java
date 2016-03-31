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

/**
 * A VM resource
 */
public class VM implements VirtualResource {
    String name;
    Amount<DataAmount> ram;
    String ip;
    Integer vCpus;

    public VM() {
    }

    public VM(String name) {
        this.name = name;
    }

    public VM(Amount<DataAmount> ram, String name, Integer vCpus, String ip) {
        this.name = name;
        this.ram = ram;
        this.vCpus = vCpus;
        this.ip = ip;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Amount<DataAmount> getRam() {
		return ram;
	}

    public void setRam(Amount<DataAmount> ramCapacity) {
		this.ram = ramCapacity;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

    @Override
    public int getInstances() {
        return 1;
    }

    @Override
    public Integer getVCpus() {
        return vCpus;
    }

    public void setvCpus(Integer vCpus) {
        this.vCpus = vCpus;
    }

}
