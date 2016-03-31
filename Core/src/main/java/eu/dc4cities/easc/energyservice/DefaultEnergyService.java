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


import eu.dc4cities.easc.EASC;
import eu.dc4cities.easc.workingmode.WorkingMode;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;


/**
 * get energy from AppConfig.yaml
 */
public class DefaultEnergyService implements EnergyService {
	Logger logger = LoggerFactory.getLogger(DefaultEnergyService.class);
	
	public DefaultEnergyService() {
	}


	@Override
	public Amount<Power> getPredictedPower(String eascName, String activity, String datacentre, WorkingMode wm, Amount<Frequency> bizPerf) {
		//TODO: look for bizPerf equivalent
		return wm.getPerformanceLevels().get(0).getPower();
	}

	@Override
	public void addEasc(EASC easc) {
		//Does nothing
	}


	@Override
	public Amount<Power> getActivityPowerMonitoring(String eascName,
			String activityName, String dcName) {
		// TODO Auto-generated method stub
		return null;
	}

}
