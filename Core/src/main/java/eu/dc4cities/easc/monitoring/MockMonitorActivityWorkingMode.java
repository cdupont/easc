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

package eu.dc4cities.easc.monitoring;

import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;

import static javax.measure.unit.SI.*;

/**
 * Monitor for testing
 */
public class MockMonitorActivityWorkingMode  extends WorkDone implements MonitorActivityWorkingMode {
	
	public MockMonitorActivityWorkingMode() {
		super();
	}

	@Override
	public Amount<Frequency> getInstantBusinessPerformance(String activity,
			String datacenter, String wm) {
		return Amount.valueOf(1000, MILLI(HERTZ));
	}

	@Override
	public Amount<Power> getWMPower(String activity, String datacenter,
			String wm) {
		return Amount.valueOf(128, WATT);
	}

}
