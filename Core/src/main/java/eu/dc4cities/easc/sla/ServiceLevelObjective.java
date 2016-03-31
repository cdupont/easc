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

import eu.dc4cities.controlsystem.model.easc.PriceModifier;
import org.joda.time.DateTime;
import org.jscience.economics.money.Money;
import org.jscience.physics.amount.Amount;

import java.util.List;

/**
 * minimum business performance per TS used in SLA
 */
public class ServiceLevelObjective extends eu.dc4cities.controlsystem.model.easc.ServiceLevelObjective {
	private String timeFrom;
	private String timeTo;
	
	public ServiceLevelObjective() {
		super();
	}
	
	public ServiceLevelObjective(DateTime dateFrom, DateTime dateTo,
			Amount<?> instantBusinessObjective,
			Amount<?> cumulativeBusinessObjective, Amount<Money> basePrice,
			List<PriceModifier> priceModifiers, String timeFrom, String timeTo) {
		super(dateFrom, dateTo, instantBusinessObjective, cumulativeBusinessObjective,
				basePrice, priceModifiers);
		this.setTimeFrom(timeFrom);
		this.setTimeTo(timeTo); 
	}

	public String getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}

	public String getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}
}
