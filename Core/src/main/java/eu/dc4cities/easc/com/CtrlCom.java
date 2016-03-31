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

package eu.dc4cities.easc.com;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import eu.dc4cities.controlsystem.model.json.JsonUtils;
import eu.dc4cities.easc.Application;
import eu.dc4cities.easc.activityplan.ActivityPlanExecutor;
import eu.dc4cities.easc.configuration.Configuration;
import eu.dc4cities.easc.energyservice.EnergyService;
import eu.dc4cities.easc.monitoring.Monitor;
import eu.dc4cities.easc.workingmode.WorkingModeManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * This class is dedicated to communications with CTRL (requests, responses).
 */
public class CtrlCom extends Thread {
	ActivityPlanExecutor ape;
	Configuration config;

	Server server;
    EascService svc;
    EnergyService es;
    
    public CtrlCom(ActivityPlanExecutor ape, Configuration config, Application app, WorkingModeManager wmm, Monitor monitor, EnergyService es) {
    	this.ape = ape;
    	this.config = config;
        this.svc = new EascService(ape, config, app, wmm, monitor, es);
        
        //map EASC service and configure server
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(JsonUtils.getDc4CitiesObjectMapper());

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(svc);
        resourceConfig.register(JacksonFeature.class);
        resourceConfig.register(provider);
        ServletContainer servletContainer = new ServletContainer(resourceConfig);
        ServletHolder sh = new ServletHolder(servletContainer);
        server = new Server(app.getAppPort());
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addServlet(sh, "/*");
    } 
     
       
    @Override
    public void run() {
        startServer();
    }

    
    public void stopServer() {
        try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void startServer() {
        try {
            System.out.println("EASC API: Listening to CTRL requests");
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EascService getEascService() {
        return svc;
    }

    public void setEascService(EascService svc) {
        this.svc = svc;
    }
}
