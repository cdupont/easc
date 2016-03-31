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

import static javax.measure.unit.SI.WATT;

/**
 * Energy service that returns constant values
 */
public class MockEnergyService implements EnergyService {

	@Override
	public Amount<Power> getPredictedPower(String eascName, String activity, String datacentre, WorkingMode wm, Amount<Frequency> bizPerf) {
		if(wm.getName().equals("WM0Hertz0Watt")) {
			return Amount.valueOf(0, WATT);
		} else if(wm.getName().equals("WM1")) {
			return Amount.valueOf(10, WATT);
		} else if (wm.getName().equals("WM2")) {
			return Amount.valueOf(20, WATT);
		} else if (wm.getName().equals("WM10Hertz22Watt")) {
			return Amount.valueOf(22, WATT);
		} else if (wm.getName().equals("WM20Hertz24Watt")) {
			return Amount.valueOf(24, WATT);
		} else  if (wm.getName().equals("WM30Hertz25Watt")) {
			return Amount.valueOf(25, WATT);
		} else  if (wm.getName().equals("WM40Hertz26Watt")) {
			return Amount.valueOf(26, WATT);
		} else {
			return Amount.valueOf(30, WATT);
		}
	}

	@Override
	public void addEasc(EASC easc) {

	}

	@Override
	public Amount<Power> getActivityPowerMonitoring(String eascName,
			String activityName, String dcName) {
		// TODO Auto-generated method stub
		return null;
	}

}
