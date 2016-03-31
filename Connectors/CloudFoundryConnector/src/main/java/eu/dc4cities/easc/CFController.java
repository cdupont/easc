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
import eu.dc4cities.easc.cloudcontrollers.PaaSController;
import eu.dc4cities.easc.resource.Server;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.StartingInfo;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.InstanceStats;
import org.cloudfoundry.client.lib.domain.Staging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CFController implements PaaSController {
	private String apiEndpoint;
	private String user;
	private String password;
	private CloudCredentials credentials;
	private CloudFoundryClient client;
	private IaaSController iaasController;
    private static Logger logger = LoggerFactory.getLogger(CFController.class);

	public CFController(String target, String user, String password, IaaSController iaasController) {
	    this.apiEndpoint = target;
	    this.user = user;
	    this.password = password;
		this.iaasController = iaasController;
	}

	public boolean login() {
	    logger.debug("Logging into CloudFoundry:");
	    try {
	    	this.credentials = new CloudCredentials(user, password);
	    	client = new CloudFoundryClient(credentials, getTargetURL(apiEndpoint), true);
	    	client.login();
	    } catch(Exception e) {
	    	logger.debug("Couldn't authenticate with CloudFoundry.");
	    	return false;
	    }
	    
	    return true;
	}

	public boolean startApp(String appName) {
		boolean status = true;
	    logger.debug("Starting the application " + appName);
		try {
			StartingInfo startInfo = client.startApplication(appName);
		} catch(Exception e) {
			//App is transitioning, or in progress
			if(this.getAppState(appName) == PaaSController.AppState.STARTED 
					|| this.getAppState(appName) == PaaSController.AppState.UPDATING)
				status = true;
			else
				status = false;
			logger.debug("Exception happened during starting. msg:" + e.getMessage() + " cause:" + e.getCause());
			e.printStackTrace();
		}
		
	    return status;
	}
	
	public boolean stopApp(String appName) {
		boolean status = true;

	    logger.debug("Stopping the application " + appName);
		try {
			client.stopApplication(appName);
		} catch(Exception e) {
			//App is transitioning, or in progress
			if(this.getAppState(appName) == PaaSController.AppState.STOPPED 
					|| this.getAppState(appName) == PaaSController.AppState.UPDATING)
				status = true;
			else
				status = false;

			logger.debug("Exception happened during stopping. msg:" + e.getMessage() + " cause:" + e.getCause());
			e.printStackTrace();
		}
			
	    return status;
	}
	
	public boolean scaleApp(String appName, int instances, int disk, int memory) {
		if(instances != this.getNumRunningInstancesApp(appName))
			try {
				client.updateApplicationInstances(appName, instances);
			} catch(Exception e) {
				logger.debug("Exception happened during horizental scaling:: msg:" + e.getMessage() + " cause:" + e.getCause());
				e.printStackTrace();
				if(instances == this.getNumInstancesApp(appName))
					return true;
				return false;
			}
		
		if(disk != this.getDiskApp(appName))
			try {
				client.updateApplicationDiskQuota(appName, disk);
			} catch(Exception e) {
				logger.debug("Exception happened during vertical scaling:: msg:" + e.getMessage() + " cause:" + e.getCause());
				e.printStackTrace();
				if(disk == this.getDiskApp(appName))
					return true;
				return false;
			}

		if(memory != this.getMemApp(appName))
			try {
				client.updateApplicationMemory(appName, memory);
			} catch(Exception e) {
				logger.debug("Exception happened during vertical scaling:: msg:" + e.getMessage() + " cause:" + e.getCause());
				e.printStackTrace();
				if(memory == this.getMemApp(appName))
					return true;
				return false;
			}
		
		return true;
	}
	
	public AppState getAppState(String appName) {
		CloudApplication app = null;
		
		try {
			app = client.getApplication(appName);
		} catch(Exception e) {
			logger.debug("Exception happened during getting app state. msg:" + e.getMessage() + " cause:" + e.getCause());
		}
		
		switch (app.getState()) {
			case UPDATING: return PaaSController.AppState.UPDATING;
			case STARTED: return PaaSController.AppState.STARTED;
			case STOPPED: return PaaSController.AppState.STOPPED;
			default: return PaaSController.AppState.STOPPED;
		}
	}
	
    private static URL getTargetURL(String target) {
        try {
            return URI.create(target).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("The target URL is not valid: " + e.getMessage());
        }
    }

	public List<String> getAppContainersId(String appName) {
	    List<String> containersId = new ArrayList<>();

	    try {
	    	ApplicationStats appState = client.getApplicationStats(appName);
	    	for(InstanceStats ins: appState.getRecords()) {
	    		containersId.add(ins.getId());
	    	} 	
	    } catch(Exception e) {
	    	logger.debug("Exception happened during getAppContainersId. msg:" + e.getMessage() + " cause:" + e.getCause());
	    }
	    
		return containersId;
	}
	
	
	public double getContainerVCPULoad(String appName, String containerId) {
		try {
			Optional<InstanceStats> ins = getContainer(appName, containerId);
			if(ins.isPresent())
				return ins.get().getUsage().getCpu();
		} catch(Exception e) {
			logger.debug("Exception happened during getContainerVCPULoad. msg:" + e.getMessage() + " cause:" + e.getCause());
		}
		
		return -10;
	}

	public double getContainerVRAMLoad(String appName, String containerId) {
		try {
			Optional<InstanceStats> ins = getContainer(appName, containerId);
			if(ins.isPresent())
				return ins.get().getUsage().getMem();
		} catch(Exception e) {
			logger.debug("Exception happened during getContainerVRAMLoad. msg:" + e.getMessage() + " cause:" + e.getCause());
		}

		return -10;
	}

	public List<Server> getServers() {
		List<Server> servers = new ArrayList<>();
		List<String> containerIps = new ArrayList<>();
		
		try {
			for(CloudApplication application : client.getApplications()) {
				if(application.getState() == CloudApplication.AppState.STARTED) {
					String appName = application.getName();
					List<String> containersIds = this.getAppContainersId(appName);
					for(String containerId: containersIds) {
						Optional<String> vmIp = this.getContainerIP(appName, containerId);
						if(vmIp.isPresent())
							containerIps.add(vmIp.get());
					}
				}
			}
		} catch(Exception e) {
			logger.debug("Exception happened during getServers. msg:" + e.getMessage() + " cause:" + e.getCause());
		}
		
		for(String vmIp: containerIps) {
			Optional<Server> s = iaasController.getServerByVMIP(vmIp);
			if(s.isPresent())
				servers.add(s.get());
		}
	    
	    return servers;
	}
	
	public Optional<String> getContainerIP(String appName, String containerId) {
		try {
			Optional<InstanceStats> ins = getContainer(appName, containerId);
			if(ins.isPresent() == true) {
				String host = ins.get().getHost();
				if(host != null)
					return Optional.of(host);
			}
		} catch(Exception e) {
			logger.debug("Exception happened during getContainerIP. msg:" + e.getMessage() + " cause:" + e.getCause());
		}
		
		return Optional.absent();
	}

	public Optional<Server> getContainerServer(String appName, String containerId) {
		try {
			iaasController.auth();
			Optional<String> vmIp = this.getContainerIP(appName, containerId);
			if(vmIp.isPresent())
				return iaasController.getServerByVMIP(vmIp.get());
		} catch(Exception e) {
			logger.debug("Exception happened during getContainerServer. msg:" + e.getMessage() + " cause:" + e.getCause());
		}
		
		return Optional.absent();
	}
	
	public String getAppEnv(String appName) {
		CloudApplication app = client.getApplication(appName);
		String envs = "ENV: ";
		for(String env : app.getEnv())
			envs += env + ", ";
		
		return envs;
	}

	public String getAppResources(String appName) {
		CloudApplication app = client.getApplication(appName);
		String resources = "RES: ";
		resources += app.getResources();
		return resources;
	}
	
	public double getContainerDiskLoad(String appName, String containerName) {
		ApplicationStats appState = client.getApplicationStats(appName);
		for(InstanceStats ins: appState.getRecords()) {
			if(ins.getName() == containerName)
				return ins.getUsage().getDisk();
		}
		
		return -1;
	}

	public Optional<InstanceStats> getContainer(String appName, String containerId) {
		try {
			ApplicationStats appState = client.getApplicationStats(appName);
			for(InstanceStats ins: appState.getRecords()) {
				if(ins != null)
					if(ins.getId().equals(containerId))
						return Optional.of(ins);
			}
		} catch(Exception e) {
			logger.debug("Exception happened during getContainer. msg:" + e.getMessage() + " cause:" + e.getCause());
		}
		
		return Optional.absent();
	}

	public int getNumRunningInstancesApp(String appName) {
		CloudApplication app = null;
		try {
			app = client.getApplication(appName);
			if(app != null)
				return app.getRunningInstances();
		} catch(Exception e) {
			logger.debug("Exception happened during getting number of running instances. msg:" + e.getMessage() + " cause:" + e.getCause());
		}

		return 0;
	}
	
	public int getNumInstancesApp(String appName) {
		CloudApplication app = null;
		try {
			app = client.getApplication(appName);
			if(app != null)
				return app.getInstances();
		} catch(Exception e) {
			logger.debug("Exception happened during getting number of instances. msg:" + e.getMessage() + " cause:" + e.getCause());
		}

		return 0;
	}

	public int getMemApp(String appName) {
		CloudApplication app = null;
		try {
			app = client.getApplication(appName);

			if(app != null)
				return app.getMemory();
		} catch(Exception e) {
			logger.debug("Exception happened during getting memory size. msg:" + e.getMessage() + " cause:" + e.getCause());
		}

		return 0;
	}

	public int getDiskApp(String appName) {
		CloudApplication app = null;
		try {
			app = client.getApplication(appName);
			if(app != null)
				return app.getDiskQuota();
		} catch(Exception e) {
			logger.debug("Exception happened during getting disk size. msg:" + e.getMessage() + " cause:" + e.getCause());
		}
		
		return 0;
	}
	
	public boolean createApp(String appName, Integer disk, Integer memory, List<String> serviceNames) {
		Staging staging = null;
		staging = new Staging();
		
		client.createApplication(appName, staging, disk, memory, null, serviceNames);
		return true;
	}

}