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

package eu.dc4cities.easc;

import com.google.common.base.Optional;
import eu.dc4cities.easc.cloudcontrollers.IaaSController;
import org.jscience.physics.amount.Amount;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ext.HypervisorService;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.openstack.OSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

import static javax.measure.unit.NonSI.BYTE;
import static javax.measure.unit.SI.WATT;

public class OSController implements IaaSController {
	private String apiEndpoint;
	private String user;
	private String password;
	private String tenant;
	private OSClient osClient;
    private static Logger logger = LoggerFactory.getLogger(OSController.class);

	public OSController(String target, String user, String password, String tenant) {
	    this.apiEndpoint = target;
	    this.user = user;
	    this.password = password;
	    this.tenant = tenant;
	}
	
	public boolean auth() {
		logger.debug("Logging into OpenStack:");
		try {
			osClient = OSFactory.builder().endpoint(apiEndpoint).credentials(user, password).tenantName(tenant).authenticate();
		} catch(Exception e) {
			logger.debug("Couldn't authenticate with OpenStack.");
			return false;
		}

	    return true;
	}

	public Optional<String> getServerNameByVMIP(String vmIp) {
		for(Server vm : osClient.compute().servers().list())
			for (List<? extends Address> address : vm.getAddresses().getAddresses().values()) {
			  Iterator<? extends Address> iter = address.iterator();
			  while(iter.hasNext()) {
			      Address add = iter.next();

			      //finds the instance of a VM with vmIp address in OpenStack
			      if(add.getAddr().equals(vmIp)) {
			    	  //logger.debug(vm.toString());
			    	  return Optional.of(vm.getHypervisorHostname());
			      }
			  }
			}
		
		return Optional.absent();
	}
	
	@Override
	public Optional<eu.dc4cities.easc.resource.Server> getServerByVMIP(String vmIp) {
		//it seems this is the only way to get server info. First from the VM, we get the server name
		//then, we go over the list of servers, and find the right server, and extract its info.
		
		Optional<String> hyperName = this.getServerNameByVMIP(vmIp);
		if(hyperName.isPresent() == false)
			return Optional.absent();

		HypervisorService hypervisorService = osClient.compute().hypervisors();
		//iterate over all servers
		for(Hypervisor hypervisor : hypervisorService.list()) {
			if(hypervisor.getHypervisorHostname().equals(hyperName.get())) {
				eu.dc4cities.easc.resource.Server host = new eu.dc4cities.easc.resource.Server();
				host.setName(hypervisor.getHypervisorHostname());
				host.setDisk(hypervisor.getLocalDisk(), BYTE.times(1024).times(1024).times(1024));
				host.setRam(hypervisor.getLocalMemory(), BYTE.times(1024).times(1024));
				host.setIp(hypervisor.getHostIP());
				host.setPidle(Amount.valueOf(600, WATT));
				return Optional.of(host);
			}
		}
	
		return Optional.absent();
	}
	
}