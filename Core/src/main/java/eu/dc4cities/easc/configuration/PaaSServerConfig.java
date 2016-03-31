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

package eu.dc4cities.easc.configuration;

import eu.dc4cities.easc.cloudcontrollers.PaaSController;
import eu.dc4cities.easc.resource.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;


/**
 * server config getting its source from a PaaS API
 */
public class PaaSServerConfig implements ServerConfig {

	PaaSController paasController;
	Logger logger = LoggerFactory.getLogger(PaaSServerConfig.class);

	public PaaSServerConfig(PaaSController paasController) {

		this.paasController = paasController;
	}

	@Override
	public List<Server> getServers() {
		return paasController.getServers();
	}

	public void setServers(Set<Server> servers) {
		logger.error("method not supported");
	}
	
	public void addServers(Server server) {
		logger.error("method not supported");
	}
	
}
