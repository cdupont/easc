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

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;

/**
 * Monitor interface that should be implemented by trials
 */
public interface MonitorActivityWorkingMode {

    Amount<Power> getWMPower(String activity, String datacenter, String wm);
	void addActivityCumulativeBusinessItems(String activityName, String dataCenterName, Amount<Dimensionless> amount);
	void initWorkDone(String key);
    Amount<Dimensionless> getActivityCumulativeBusinessItems(String activity, String datacenter);
	Amount<Dimensionless> getActivityCumulativeBusinessItems(String activity);
    //Number of items processed per unit of time
	Amount<?> getInstantBusinessPerformance(String activity, String datacenter,
			String wm);
}
