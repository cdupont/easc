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

package eu.dc4cities.easc.configuration;

import eu.dc4cities.easc.Application;
import eu.dc4cities.easc.activity.Activity;
import eu.dc4cities.easc.resource.Resource;
import eu.dc4cities.easc.resource.Server;
import eu.dc4cities.easc.resource.VM;
import eu.dc4cities.easc.sla.MinBizPerf;
import eu.dc4cities.easc.sla.SLA;
import eu.dc4cities.easc.workingmode.WorkingMode;
import org.jscience.physics.amount.Amount;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import javax.measure.quantity.Frequency;
import javax.measure.unit.UnitFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static eu.dc4cities.easc.resource.Units.PAGE;
import static javax.measure.unit.SI.SECOND;

/**
 * This class generates default config files
 */
public class ConfigGenerator {
	
	public ConfigGenerator() {
		UnitFormat.getInstance().label(PAGE, "Page");	
	}	
	
	@Deprecated
    public void CreateWorkingMode() {

        System.out.println("createWorkingMode");

        Date start = new Date();

        Resource wmr1 = new VM("WebServer");
        Resource server1 = new Server("Server1");
        Collection<Resource> resources1 = new ArrayList<>();
        resources1.add(wmr1);
        resources1.add(server1);
        
        WorkingMode wm1 = new WorkingMode("WM1", 1);
        
        Resource wmr21 = new VM("WebServer");
        Resource wmr22 = new VM("WebServer");
        Resource server2 = new Server("Server2");
        Collection<Resource> resources2 = new ArrayList<>();
        resources2.add(wmr21);
        resources2.add(wmr22);
        resources2.add(server2);
        WorkingMode wm2 = new WorkingMode("WM2", 2);

        List<WorkingMode> workingModes = new ArrayList<>();
        workingModes.add(wm1);
        workingModes.add(wm2);

        Activity activity = new Activity();//"Front-End", workingModes, optionPlans);
        activity.setBusinessUnit("Page");
        List<Activity> activities = new ArrayList<>();
        activities.add(activity);

        Application app = new Application("petClinic", activities);

        writeConfig(app);

    }

    public Configuration CreateConfig() {

        Configuration config = new Configuration();

        writeConfig(config);

        return config;
    }
    
    public void CreateSLA() {

        List<MinBizPerf> minBizPerfs = new ArrayList<>();
        MinBizPerf minBizPerf = new MinBizPerf(1, 10, (Amount<Frequency>) Amount.valueOf(10, PAGE.divide(SECOND)));
        minBizPerfs.add(minBizPerf);
    	SLA sla = new SLA(Amount.valueOf(100, PAGE), minBizPerfs);

        writeConfig(sla);
    }


    public static void writeYaml(String fileName, Object o) {

        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            
            CalRepresenter cr = Utils.getCalRepresenter();            
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            Yaml yaml = new Yaml(cr, options);
            String output = yaml.dump(o);
            System.out.println(output);

            writer.print(output);
            writer.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    //Extention to Snake YAML to serialize/deserialize iCals & JScience
    public static class CalRepresenter extends Representer {
        public CalRepresenter() {
            this.representers.put(Amount.class, new RepresentAmount());
        }

        private class RepresentAmount implements Represent {
            public Node representData(Object data) {
                Amount amount = (Amount) data;
                String value = amount.toString();
                return representScalar(new Tag("!amount"), value);
            }
        }

    }    

    //for debugging purpose
    public static void writeConfig(Application app) {

        writeYaml(Constants.CONFIG_FOLDER + File.separator + Constants.APP_CONFIG_FILE, app);
    }

    //for debugging purpose
    public static void writeConfig(Configuration conf) {

        writeYaml(Constants.CONFIG_FOLDER + File.separator + Constants.CONFIG_FILE, conf);
    }

    //for debugging purpose
    public static void writeConfig(SLA conf) {

        writeYaml(Constants.CONFIG_FOLDER + File.separator + Constants.SLA_FILE, conf);
    }
}
