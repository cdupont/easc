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
import java.util.HashMap;
import java.util.Map;

/**
 * work done interface
 */
public class WorkDone {
	private Map<String, Amount<Dimensionless>> workDone;
	
	public WorkDone() {
		this.workDone = new HashMap<String, Amount<Dimensionless>>();
	}
	
	public void initWorkDone(String key) {
		this.workDone.put(key, Amount.ZERO);
	}
	
	public Amount<Dimensionless> getActivityCumulativeBusinessItems(String activity, String dc) {
		return this.workDone.get(activity + "." + dc);
	}
	
	public Amount<Dimensionless> getActivityCumulativeBusinessItems(String activity) {
		Amount<Dimensionless> wd = Amount.ZERO;
		
		for(String key: this.workDone.keySet()) {
			int place = key.indexOf(".");
			if(place != -1) {
				String a = key.substring(0, place);
			
				if(a.equals(activity))
					wd = wd.plus(this.workDone.get(key));
			}
		}
		
		return wd;
	}

	public void addActivityCumulativeBusinessItems(String activityName, String dataCenterName, Amount<Dimensionless> amount) {
		String key = activityName + "." + dataCenterName;
		Amount<Dimensionless> current = workDone.get(key);
		//System.out.println("current.getEstimatedValue: " + current.getEstimatedValue() + ", amount.getEstimatedValue: " + amount.getEstimatedValue());
		if (current.equals(Amount.ZERO)) {
			//System.out.println("current equals Amount.ZERO");
			workDone.put(key, amount);
		} else {
			workDone.put(key, current.plus(amount));
		}
	}

}
