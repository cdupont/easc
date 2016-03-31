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

import eu.dc4cities.controlsystem.model.TimeParameters;
import eu.dc4cities.controlsystem.model.easc.EascActivityPlan;
import eu.dc4cities.controlsystem.model.easc.EascActivitySpecifications;
import eu.dc4cities.controlsystem.model.easc.EascMetrics;
import eu.dc4cities.easc.Application;
import eu.dc4cities.easc.activity.ActivitySpecifications;
import eu.dc4cities.easc.activityplan.ActivityPlanExecutor;
import eu.dc4cities.easc.configuration.Configuration;
import eu.dc4cities.easc.energyservice.EnergyService;
import eu.dc4cities.easc.monitoring.Monitor;
import eu.dc4cities.easc.monitoring.MonitoringMetrics;
import eu.dc4cities.easc.workingmode.WorkingModeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST api with CTRL
 */
@Path("/{version}/easc/{name}")
public class EascService implements EascApi {
	
	ActivityPlanExecutor ape;
	ActivitySpecifications as;
	MonitoringMetrics mm;

	private Logger logger = LoggerFactory.getLogger(EascService.class);

	
	public EascService(ActivityPlanExecutor ape, Configuration config, Application app, WorkingModeManager wmm, Monitor monitor, EnergyService es) {
		this.ape = ape;
		this.as = new ActivitySpecifications(app, es, config.getEASCName(), monitor);
		this.mm = new MonitoringMetrics(monitor, app, wmm);
	}

	//Read Activity Specifications (new)
	//POST <EascBaseUrl>/v1/easc/{eascName}/activityspecifications
	@POST
    @Path("/activityspecifications")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public EascActivitySpecifications getActivitySpecifications(@PathParam("version") String version, @PathParam("name") String name, TimeParameters timeParams) {
        logger.debug("Received request for Activity Specifications:");
        logger.debug("Time: " + timeParams.toString());
        EascActivitySpecifications appSpec = getActivitySpecifications(timeParams);
        logger.debug("Replying with activity specifications:" + appSpec);
        return appSpec;
    }	

	//Read Monitoring Metrics (new)
	//POST <EascBaseUrl>/v1/easc/{eascName}/metrics
    @POST
    @Path("/metrics")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)   
	public EascMetrics getMonitoringMetrics(@PathParam("version") String version, @PathParam("name") String name, TimeParameters timeParams) {
        logger.debug("Received request for Read Monitoring Metrics:");
        logger.debug("Time: " + timeParams.toString());
        EascMetrics metrics = getMonitoringMetrics(timeParams);
        logger.debug("Replying with metrics at " + timeParams.getDateNow() + " :");
        logger.debug(metrics.toString());
        return metrics;
	}

    //Read Activity Plan (updated)
    @GET
    @Path("/activityplan")
    @Produces(MediaType.APPLICATION_JSON)
    public EascActivityPlan getActivityPlan(@PathParam("version") String version, @PathParam("name") String name) {
        logger.debug("Get activity plan");
        return getActivityPlan();
    }

    //Execute Activity Plan (updated)
    @PUT
    @Path("/activityplan")
    @Consumes(MediaType.APPLICATION_JSON)
    public void executeActivityPlan(@PathParam("version") String version, @PathParam("name") String name, EascActivityPlan activityPlan) {
        logger.debug("Execute received activity plan:");
        logger.debug(activityPlan.toString());
        executeActivityPlan(activityPlan);
    }

    @Override
    public boolean executeActivityPlan(EascActivityPlan activityPlan) {
        return ape.executeActivityPlan(activityPlan, false);
    }

    @Override
    public EascActivityPlan getActivityPlan() {
        return ape.getActivityPlan();
    }

    @Override
    public EascActivitySpecifications getActivitySpecifications(TimeParameters timeParams) {
        return as.getActivitySpecifications(timeParams);
    }

    @Override
    public EascMetrics getMonitoringMetrics(TimeParameters timeParams) {
        return mm.getMonitoringMetrics(timeParams);
    }
    
}
