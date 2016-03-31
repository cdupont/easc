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

package eu.dc4cities.easc.sla;

import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.quantity.Dimensionless;
import java.util.List;

/**
 * SLA of an activity
 */
public class SLA {

    protected Logger logger = LoggerFactory.getLogger(SLA.class);

    //minimum number of business items guaranteed per day, can be null    
	Amount<Dimensionless> businessItemsPerDay;
	
	//minimum business performance guaranteed per time frame, can be null
	List<MinBizPerf> minBusinessPerformance;
	
	public SLA() {		
	}
	
	public SLA(Amount<Dimensionless> businessItemsPerDay, List<MinBizPerf> minBusinessPerformance) {
		this.businessItemsPerDay = businessItemsPerDay;
		this.minBusinessPerformance = minBusinessPerformance;

		if(businessItemsPerDay != null)	logger.debug("SLA businessItemsPerDay:" + businessItemsPerDay.toString());
	}

	public Amount<Dimensionless> getBusinessItemsPerDay() {
		return businessItemsPerDay;
	}

	public void setBusinessItemsPerDay(Amount<Dimensionless> businessItemsPerDay) {
		this.businessItemsPerDay = businessItemsPerDay;
	}
	
    public List<MinBizPerf> getMinBusinessPerformance() {
        return minBusinessPerformance;
    }

    public void setMinBusinessPerformance(List<MinBizPerf> minBusinessPerformance) {
        this.minBusinessPerformance = minBusinessPerformance;
    }
}
