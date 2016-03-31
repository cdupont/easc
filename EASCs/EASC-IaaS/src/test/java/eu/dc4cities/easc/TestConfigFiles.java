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

import eu.dc4cities.easc.activity.Activity;
import eu.dc4cities.easc.configuration.ConfigReader;
import eu.dc4cities.easc.configuration.Configuration;
import eu.dc4cities.easc.configuration.Constants;
import junit.framework.TestCase;
import org.jscience.economics.money.Currency;
import org.junit.Test;

import javax.measure.unit.UnitFormat;
import java.util.List;

import static eu.dc4cities.easc.resource.Units.PCPU_USAGE;

public class TestConfigFiles extends TestCase {

    private String workingDir="";

    /**
     * Method to set variable to all tests. For instane, set directory of configuration files.
     */
    protected void setUp() {
        //Set working directory to resources, if you change the configuration files you
        //need also to update the expected behavior of the test.
        workingDir = "./resource/";
    }


    /**
     * Tests if the simulator reads the AppConfig.yaml file generating the expected infrastrcuture.
     */
    @Test
    public void testAppConfig() {

        //Declare amount needed to parse yaml files.
        UnitFormat.getInstance().label(Currency.EUR, "EUR");
        UnitFormat.getInstance().label(PCPU_USAGE, "pCPU_usage");

        //Start application configReader, read the workingDir searching for Constants.APP_CONFIG_FILE.
        Application app = ConfigReader.readAppConfig(workingDir).get();

        //No exceptions here means everything went fine.
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] Parsing "+Constants.APP_CONFIG_FILE+" file...");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("<==== DUMP Application ====>");

        //Assert if name is as expected.
        System.out.println("Name : " + app.getName());
        assertEquals("Application name does not match on "+Constants.APP_CONFIG_FILE,
                app.getName(),
                "EASC-IaaS");

        //Assert if number of activities is as expected.
        List<Activity> activities = app.getActivities();
        assertEquals("Number of activities does not match on " + Constants.APP_CONFIG_FILE, activities.size(), 2);

        System.out.println("Acitivities list: ");
        for(Activity act : activities) {
            System.out.println("Name : "+act.getName());
            System.out.println("  BusinessUnit : "+act.getBusinessUnit());
        }

    }

    /**
     * Tests if the simulator reads the Config.yaml file generating the expected object structure.
     */
    @Test
    public void testConfig() {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] Parsing "+Constants.CONFIG_FILE+" file...");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        Configuration config = ConfigReader.readConfiguration(workingDir).get();
    }


}