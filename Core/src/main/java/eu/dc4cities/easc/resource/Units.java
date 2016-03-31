
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

package eu.dc4cities.easc.resource;

import javax.measure.quantity.DataAmount;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;

import static javax.measure.unit.NonSI.BYTE;
import static javax.measure.unit.SI.GIGA;
import static javax.measure.unit.SI.MEGA;
import static javax.measure.unit.Unit.ONE;

public class Units {
    public final static Unit<Dimensionless> PAGE = ONE.alternate("Page");
    public final static Unit<Dimensionless> PCPU_USAGE = ONE.alternate("pCPU_usage");
    public final static Unit<Dimensionless> REPORT = ONE.alternate("Report");
    public final static Unit<DataAmount> MB = MEGA(BYTE);
    public final static Unit<DataAmount> GB = GIGA(BYTE);
    {
    	UnitFormat.getInstance().label(MB, "MB");
    	UnitFormat.getInstance().label(GB, "GB");
    }
	public static void init() {
		UnitFormat.getInstance().label(MB, "MB");
    	UnitFormat.getInstance().label(GB, "GB");		
	}
}