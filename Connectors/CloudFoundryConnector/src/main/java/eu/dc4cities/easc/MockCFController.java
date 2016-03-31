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
import eu.dc4cities.easc.cloudcontrollers.PaaSController;
import eu.dc4cities.easc.resource.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MockCFController implements PaaSController {

	private String apiEndpoint;
	private String user;
	private String password;
    private static Logger logger = LoggerFactory.getLogger(MockCFController.class);

	public MockCFController(String target, String user, String password) {
	    this.apiEndpoint = target;
	    this.user = user;
	    this.password = password;
	}
	
	public boolean auth() {
	    return true;
	}

	public boolean login() {
	    logger.debug("login method:");
	    logger.debug("Issuing client.login():");
	    return true;
	}

	public boolean startApp(String appName) {
	    logger.debug("Starting the application");
	    return true;
	}
	
	public boolean stopApp(String appName) {
	    logger.debug("Stopping the application");
	    return true;
	}

	@Override
	public AppState getAppState(String appName) {
		return AppState.STOPPED;
	}

	@Override
	public int getNumRunningInstancesApp(String appName) {
		return 0;
	}

	@Override
	public List<String> getAppContainersId(String appName) {
		ArrayList<String> l = new ArrayList<String>();
		l.add("containerId1");
		l.add("containerId2");
		return l;
	}

	public boolean scaleApp(String appName, int instances, Integer disk, Integer memory) {
		return true;
	}
	
	public List<String> getAppContainers(String appName) {
	    List<String> containersName = new ArrayList<>();

		return containersName;
	}
	
	
	public double getContainerVCPULoad(String appName, String containerName) {
		return 0;
	}

	public double getContainerVRAMLoad(String appName, String containerName) {
		return 0;
	}


	@Override
	public String getAppEnv(String appName) {
		return "";
	}

	@Override
	public String getAppResources(String appName) {
		return null;
	}

	@Override
	public Optional<String> getContainerIP(String appName, String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Server> getContainerServer(String appName,
			String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Server> getServers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean scaleApp(String appName, int instances, int disk, int memory) {
		return true;
	}
}