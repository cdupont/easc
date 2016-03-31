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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dc4cities.controlsystem.model.TimeParameters;
import eu.dc4cities.controlsystem.model.easc.EascActivityPlan;
import eu.dc4cities.controlsystem.model.json.JsonUtils;
import eu.dc4cities.controlsystem.model.unit.Units;
import eu.dc4cities.easc.activityplan.LazyActivityPlanExecutor;
import eu.dc4cities.easc.com.CtrlCom;
import eu.dc4cities.easc.configuration.Configuration;
import eu.dc4cities.easc.configuration.Constants;
import eu.dc4cities.easc.configuration.TestObjectsGenerator;
import eu.dc4cities.easc.energyservice.MockEnergyService;
import eu.dc4cities.easc.monitoring.MockMonitor;
import eu.dc4cities.easc.workingmode.DefaultWorkingModeManager;
import eu.dc4cities.easc.workingmode.WorkingModeManager;
import junit.framework.TestCase;
import org.glassfish.jersey.client.ClientConfig;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.measure.unit.UnitFormat;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;

import static javax.measure.unit.Unit.ONE;

public class ComTest extends TestCase {
	 
	//Run EASC server to listen CTRL requests
    CtrlCom ctrlCom;
    Configuration config;
    Application app;

    @Override
	protected void setUp() {
    	Units.init();
    	UnitFormat.getInstance().label(ONE.alternate("Page"), "Page");
    	config = new Configuration();
		this.app = TestObjectsGenerator.createApp();
		WorkingModeManager wmm = new DefaultWorkingModeManager(app);
    	this.ctrlCom = new CtrlCom(new LazyActivityPlanExecutor(wmm, app), config, app, wmm, new MockMonitor(), new MockEnergyService());
    	this.startCTRLCom();
    }
    
	@Override
	protected void tearDown() {
    	ctrlCom.stopServer();
    }
    
    @Test
    public void testReadActivitySpecifications() {
    	System.out.println("testReadActivitySpecifications");
   
        //read timeParams
        TimeParameters timeParams = new TimeParameters();
        DateTime dateFrom = new DateTime();
        timeParams.setDateNow(dateFrom);
		timeParams.setDateFrom(dateFrom);
		timeParams.setDateTo(dateFrom.plusDays(3));
	    ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget target = client.target("http://localhost:" + app.getAppPort() + Constants.EASC_PATH + "activityspecifications");
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(timeParams.toString()));
        String jsonLine = response.readEntity(String.class);
        System.out.println(jsonLine);
    
	    //HTTP return status = OK, 200
	    assertEquals(200, response.getStatus());
    }
    
    @Test
    public void testExecuteEascActivityPlan() {
    	System.out.println("testExecuteEascActivityPlan");
    	ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget target = client.target("http://localhost:" + app.getAppPort() + Constants.EASC_PATH + "activityplan");
        
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(getEascActivityPlanJSON()));
        String jsonLine = response.readEntity(String.class);
        System.out.println(jsonLine);
        
	    //HTTP return status = OK, No data
	    assertEquals(204, response.getStatus());
    }

    @Test
    public void testReadActivityPlanWithoutAnyActivityExecution() {
    	System.out.println("testReadActivityPlan");

	    ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget target = client.target("http://localhost:" + app.getAppPort() + Constants.EASC_PATH + "activityplan");
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
        String jsonLine = response.readEntity(String.class);
    	System.out.println("testReadActivityPlan: output without any activity execution");
        System.out.println(jsonLine);
    
	    //HTTP return status = OK, 200
	    assertEquals(200, response.getStatus());
    }

    @Test
    public void testReadActivityPlanWithActivityExecution() {
    	System.out.println("testReadActivityPlan: with activity execution");

    	System.out.println("execute an Activity Plan");
    	ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget target = client.target("http://localhost:" + app.getAppPort() + Constants.EASC_PATH + "activityplan");
        
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(getEascActivityPlanJSON()));
        String jsonLine = response.readEntity(String.class);
        System.out.println(jsonLine);
        
	    //HTTP return status = OK, No data
	    assertEquals(204, response.getStatus());

	    response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
        jsonLine = response.readEntity(String.class);
    	System.out.println("testReadActivityPlan: output with activity execution");
        System.out.println(jsonLine);
    
	    //HTTP return status = OK, 200
	    assertEquals(200, response.getStatus());
    }

	 @Test
	 public void testReadMonitoringAPI() {
		 System.out.println("testReadMonitoringMetrics");

		 //read timeParams
		 TimeParameters timeParams = new TimeParameters();
		 DateTime dateFrom = new DateTime();
		 timeParams.setDateNow(dateFrom);
		 timeParams.setDateFrom(dateFrom);
		 timeParams.setDateTo(dateFrom.plusDays(1));
		 ClientConfig clientConfig = new ClientConfig();
		 Client client = ClientBuilder.newClient(clientConfig);
		 WebTarget target = client.target("http://localhost:" + app.getAppPort() + Constants.EASC_PATH + "metrics");
		 Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(timeParams.toString()));
		 String jsonLine = response.readEntity(String.class);
		 System.out.println(jsonLine);

		 //HTTP return status = OK, 200
		 assertEquals(200, response.getStatus());
	 }
	 
	 @Test
	 public void testReadMonitoringAPIwithActivityExecution() {
		 System.out.println("testReadMonitoringMetrics: with activity execution");
		 System.out.println("execute an Activity Plan");
		 ClientConfig clientConfig = new ClientConfig();
		 Client client = ClientBuilder.newClient(clientConfig);
		 WebTarget target = client.target("http://localhost:" + app.getAppPort() + Constants.EASC_PATH + "activityplan");

		 Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(getEascActivityPlanJSON()));
		 String jsonLine = response.readEntity(String.class);
		 System.out.println(jsonLine);

		 //HTTP return status = OK, No data
		 assertEquals(204, response.getStatus());
		 //read timeParams
		 TimeParameters timeParams = new TimeParameters();
		 DateTime dateFrom = new DateTime();
		 timeParams.setDateNow(dateFrom);
		 timeParams.setDateFrom(dateFrom);
		 timeParams.setDateTo(dateFrom.plusDays(1));
		 target = client.target("http://localhost:" + app.getAppPort() + Constants.EASC_PATH + "metrics");
		 response = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(timeParams.toString()));
		 jsonLine = response.readEntity(String.class);
		 System.out.println(jsonLine);

		 //HTTP return status = OK, 200
		 assertEquals(200, response.getStatus());
	 }
	 
    // @Test
//    public void testGetOptionPlan() {
//    	
//    	ClientConfig clientConfig = new ClientConfig();
//        Client client = ClientBuilder.newClient(clientConfig);
//        WebTarget target = client.target("http://localhost:" + config.getEASCPort() + Constants.EASC_PATH + "optionplan");
//        
//        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(getEascQuotaJSON()));
//        
//	    //HTTP return status = OK
//	    assertEquals(200, response.getStatus());
//    	
//    }

	public void startCTRLCom() {
//	    ctrlCom.run();
	    ctrlCom.start();

	    try {
	     	//Apparently we need to give some time for the server to initialize
	    	Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    
	}


//	public String getEascQuotaJSON() {
//		
//		EascPowerPlan epqs = TestCommons.getFlatEascPowerPlan(Amount.valueOf(25, WATT));
//		
//		try {
//            ObjectMapper mapper = JsonUtils.getDc4CitiesObjectMapper();
//            StringWriter writer = new StringWriter();
//            mapper.writeValue(writer, epqs);
//            System.out.println(writer.toString());
//            return writer.toString();
//
//        } catch (JsonGenerationException e) {
//            e.printStackTrace();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    	
//    }
	
    public String getEascActivityPlanJSON() {
    	EascActivityPlan eap = TestObjectsGenerator.createEascActivityPlan();
        
        try {
            ObjectMapper mapper = JsonUtils.getDc4CitiesObjectMapper();
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, eap);
            System.out.println(writer.toString());
            return writer.toString();

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
