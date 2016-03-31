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

import eu.dc4cities.easc.monitoring.MockMonitor;
import org.jscience.economics.money.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.unit.UnitFormat;
import java.util.List;

import static eu.dc4cities.easc.resource.Units.PCPU_USAGE;

/**
 * Simple main to test the EASC. This file should make easyer to use jUnit on the
 * project. Defining test cases soon.
 *
 */
public class Main {
    //Create a log to monitor EASC execution.
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     *
     * @param args directory where to find configuration files for this EASC.
     */
    public static void main(String [] args) {
        //Declare amount needed to parse yaml files.
        UnitFormat.getInstance().label(Currency.EUR, "EUR");
        UnitFormat.getInstance().label(PCPU_USAGE, "pCPU_usage");

        //Parse and generate config files depending on command line arguments.
        List<String> configDirectory = Utils.parseCmdLineArgs(args);

        //Parse the directory given as parameter to create String object.
        configDirectory = Utils.parseCmdLineArgs(args);

        //Reference to the EASC generic class, easier to call init and start avoding casting all the time.
        EASCIaaS easc = new EASCIaaS(configDirectory.get(0),  new MockMonitor());

        logger.debug("--------------EASC-IaaS Demo version----------");

        //Initialize the EASC.
        easc.init(true);

        //Start the EASC loop.
        easc.start();
    }
}
