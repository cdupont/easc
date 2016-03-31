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

package eu.dc4cities.easc;

import eu.dc4cities.easc.resource.Units;
import junit.framework.TestCase;
import org.jscience.physics.amount.Amount;
import org.junit.Test;

import javax.measure.quantity.*;

import static javax.measure.unit.NonSI.*;
import static javax.measure.unit.SI.*;

public class UnitsTest extends TestCase {

	@Test
    public static void testUnit() {
    	//Units.init();
    	System.out.println("Hello");
    	Amount<DataAmount> d1mb = Amount.valueOf(1024, Units.MB); 
    	Amount<DataAmount> d1gb = Amount.valueOf(1, Units.GB);

    	System.out.println("d1mb in MB=" + d1mb.longValue(Units.MB));
    	System.out.println("d1mb in GB=" + d1mb.longValue(Units.GB));
    	
    	System.out.println("d1gb in MB=" + d1gb.longValue(Units.MB));
    	System.out.println("d1gb in GB=" + d1gb.longValue(Units.GB));
        //creating a new power measure in MW
        Amount<DataAmount> data = Amount.valueOf(1, BYTE.times(1024).times(1024));
        System.out.println("data=" + data.toString());
        
        //creating a new power measure in MW
        Amount<DataAmount> datatera = Amount.valueOf(1, BYTE.times(1024).times(1024).times(1024));
        System.out.println("datatera=" + datatera.toString());
        
        //creating a new energy measure in Joules
        Amount<Energy> energy = Amount.valueOf(1, JOULE);
        System.out.println("energy=" + energy.toString());

        //creating a new power measure in MW
        Amount<Power> power = Amount.valueOf(1, MEGA(WATT));
        System.out.println("power=" + power.toString());

        //creating a new length measure in meters
        Amount<Length> x = Amount.valueOf(1, METER);
        System.out.println("x=" + x);

        //creating a new duration measure in s
        Amount<javax.measure.quantity.Duration> t = Amount.valueOf(2, MICRO(SECOND));
        System.out.println("t=" + t);

        //creating a new velocity measure in m/s
        Amount<Velocity> v1 = Amount.valueOf(1000, KILOMETERS_PER_HOUR);
        System.out.println("v1=" + v1);

        //creating a new velocity out of x an t in Km/h
        Amount<Velocity> v2 = (Amount<Velocity>) x.divide(t);
        System.out.println("v2=" + v2.to(KILO(METER).divide(HOUR)));

        //adding velocities with different units
        Amount<Velocity> v3 = (Amount<Velocity>) v1.plus(v2);
        //System.out.println("v3=" + v3.to( METER_PER_SECOND ));

        //compute an energy from power and time
        Amount<?> energy2 = power.times(t);
        System.out.println("energy2=" + energy2.to(JOULE));

        //ratio between 2 energies in percent
        Amount<Dimensionless> eRate = (Amount<Dimensionless>) energy.divide(energy2);
        System.out.println("eRate=" + eRate.to(PERCENT));

        //a data transfer rate in Bytes/Seconds
        Amount<?> dataTransfer = Amount.valueOf(1000, BYTE.divide(SECOND));
        System.out.println("dataTransfer=" + dataTransfer);

        //a frequency in mHz
        Amount<?> freq = Amount.valueOf(1000, MILLI(HERTZ));
        System.out.println("freq=" + freq);

        //creating a new length measure in foot
        Amount<Length> m = Amount.valueOf(33, FOOT).divide(11).times(2);
        System.out.println(m);
        System.out.println(m.isExact() ? "exact" : "inexact");
        System.out.println(m.getExactValue());

        //consumption of a car
        Amount<? extends Quantity> carMileage = Amount.valueOf(20, MILE.divide(GALLON_LIQUID_US));
        System.out.println("carMileage=" + carMileage);
        
        Amount<Dimensionless> test = Amount.valueOf(20, PERCENT);
        System.out.println("test=" + test);
        

    }
    
}
