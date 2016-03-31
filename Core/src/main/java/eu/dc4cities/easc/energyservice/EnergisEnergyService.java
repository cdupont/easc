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

package eu.dc4cities.easc.energyservice;

import eu.dc4cities.controlsystem.model.metrics.AssetCatalog;
import eu.dc4cities.controlsystem.model.metrics.MetricCatalog;
import eu.dc4cities.easc.EASC;
import eu.dc4cities.easc.Utils;
import eu.dc4cities.easc.configuration.Configuration;
import eu.dc4cities.easc.workingmode.WorkingMode;
import eu.dc4cities.energis.client.HttpClient;
import eu.dc4cities.energis.client.builder.ExecuteBuilder;
import eu.dc4cities.energis.client.builder.FormulaInput;
import eu.dc4cities.energis.client.response.ExecuteResponse;
import org.glassfish.jersey.client.ClientConfig;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static javax.measure.unit.SI.HERTZ;
import static javax.measure.unit.SI.WATT;

/**
 * retrieve energy infos through Energis
 */
public class EnergisEnergyService implements EnergyService {
	Configuration config;
	HttpClient energis;
	Logger logger = LoggerFactory.getLogger(EnergisEnergyService.class);
	
	public EnergisEnergyService(Configuration config) {
		this.config = config;

		energis = new HttpClient(config.getEnergisExecuteURL(), config.getEnergisExecutePort(), "dc4c15itt");
		
		assert Utils.assetCatalogContains(config.getSiteCode(), AssetCatalog.AssetType.SITE) : "Site " + config.getSiteCode() + " is not in the asset catalog";
	}
	
	public List<Amount<Power>> getPredictedPower(String activity, String datacentre, WorkingMode wm, List<Amount<Frequency>> bizPerf) {
	
		logger.debug("getPredictedPower: " + bizPerf.toString());
		//assert Utils.assetCatalogContains(wm.getName(), AssetCatalog.AssetType.WM) : "Working mode " + wm.getName() + " is not in the asset catalog";

		List<Amount<Power>> powers = new ArrayList<>();
		List<Double> intBizPerf = new ArrayList<>();
		
		for(Amount<Frequency> b : bizPerf) {
		    intBizPerf.add(b.doubleValue(HERTZ));
		}
		
		FormulaInput fi = new FormulaInput("X1", intBizPerf);
		
		ExecuteBuilder eb = ExecuteBuilder.getInstance();
		
		eb.setAssetCode(datacentre + "." + config.getEASCName() + "." + activity + "." + wm.getValue());
		logger.debug(eb.getAssetCode()); //cn_trento.EASC-trento.bend .1
		eb.setCompanyCode(AssetCatalog.DC4C.getCode());
		eb.setMetricName(MetricCatalog.POWER.getNameWithExpected()); //.getNameWithEstimated());
		eb.addInput(fi);
		
		try {
			ExecuteResponse execute = energis.execute(eb);
			
			for(Double val : execute.getValues()) {
				powers.add(Amount.valueOf(val, WATT));
				logger.debug("Working mode: " + wm.getName() + " bizPerf: " + bizPerf.toString() + " power received from Energis: " + powers.toString());
			}
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	
		return powers;
	}

	@Override
	public Amount<Power> getPredictedPower(String eascName, String activity, String datacentre, WorkingMode wm, Amount<Frequency> bizPerf) {
		
		ArrayList<Amount<Frequency>> b = new ArrayList<>();
		b.add(bizPerf);
		List<Amount<Power>> predicted = getPredictedPower(activity, datacentre, wm, b);
		return predicted.get(0);
	}

	@Override
	public void addEasc(EASC easc) {
		//Does nothing
	}


	//available only in the next release of Energis
	public boolean isEnergisAlive() {

		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		WebTarget target = client.target("http://" + config.getEnergisExecuteURL() + ":" + config.getEnergisExecutePort() + "/energiscloud-gateway/restful/api/v1/info");
		logger.debug("Energis URI: " + target.getUri());

		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		String jsonLine = response.readEntity(String.class);
		System.out.println(jsonLine);

		//HTTP return status = OK
		return (response.getStatus() == 200);
	}

	@Override
	public Amount<Power> getActivityPowerMonitoring(String eascName,
			String activityName, String dcName) {
		// TODO Auto-generated method stub
		return null;
	}

}
