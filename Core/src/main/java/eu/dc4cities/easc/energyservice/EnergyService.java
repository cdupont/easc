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

import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;

/**
 * interface to the energy service.
 * as the energy can be shared between several EASC, each EASC should register to be taken into account.
 */
public interface EnergyService {
	//get the predicted power
	Amount<Power> getPredictedPower(String eascName, String activity, String datacenter, WorkingMode wm, Amount<Frequency> bizPerf);
    public Amount<Power> getActivityPowerMonitoring(String eascName, String activityName, String dcName);
	//add a working mode manager to this energy service. This is useful in the case the energy as to be shared between several EASCs.
	void addEasc(EASC easc);
}
