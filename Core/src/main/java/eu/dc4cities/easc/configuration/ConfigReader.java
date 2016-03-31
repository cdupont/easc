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

import com.google.common.base.Optional;
import eu.dc4cities.easc.Application;
import eu.dc4cities.easc.sla.SLA;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jscience.physics.amount.Amount;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

/**
 * This class reads the configuration files necessary to run an EASC. For now
 * there are at least 3 configuration files needed, their default names are
 * statically defined in the Constants class. All files use the YAML format.
 *
 * <ul>
 * <li>
 * Config.yaml - define the software parameters configuration.
 * </li>
 * <li>
 * AppConfig.yaml - applicaiton configuration define working modes and activities.
 * </li>
 * <li>
 * Sla.yaml - Service layer agreement showing thresolds and penalties of the contract.
 * </li>
 * </ul>
  *
 *
 * @see Constants
 *
 */
public class ConfigReader {

    static CalConstructor cc = Utils.getCalConstructor();
    /**
     * Parse application configuration file, normally AppConfig.yaml.
     *
     * <h2>Example of AppConfig.yaml</h2>
     *
     * <pre>
     *   !Application
     *   name: EASC-IaaS
     *   activities:
     *   - name: "VM1"
     *   businessUnit: "Utilisation per pCPU"
     *   optionPlans:
     *   - !Aggressive
     *   - !Eager
     *   workingModes:
     *   - WMName: WM0
     *   actuator:
     *   systemCommand: bin/WMSwitch.sh WM0
     *   defaultPower: !amount '10 W'
     *   defaultWM: true
     *   - WMName: WM1
     *   actuator:
     *   systemCommand: bin/WMSwitch.sh WM1
     *   defaultPower: !amount '5 W'
     *   defaultWM: false
     * </pre>
     *
     * @param configPath contains the relative or absolute path to the folder containing configuration files.
     * @return object for the application after parsing.
     */
    public static Optional<Application> readAppConfig(String configPath) {
        return readConfigFile(configPath + File.separator + Constants.APP_CONFIG_FILE);
    }

    /**
     * Parse configuration file, normally Config.yaml. This file holds characteristics
     * of the application, as such the ip address and port TCP of controller.
     *
     * <h2>Example of Config.yaml</h2>
     *
     * <pre>
     *     !Configuration
     *     EASCName: EASC-HP
     *     EASCPort: 9999
     *     energisExecutePort: 8080
     *     energisExecuteURL: hackmeplz
     *     energisPort: 8484
     *     energisURL: hackmeplz
     *     monitoringSamplingTime: !amount '300 s'
     *     siteCode: hp_milan
     * </pre>
     *
     * @param configPath contains the relative or absolute path to the folder containing configuration files.
     * @return object for the application after parsing.
     */
    public static Optional<Configuration> readConfiguration(String configPath) {
        return readConfigFile(configPath + File.separator + Constants.CONFIG_FILE);
    }

    /**
     *
     *
     * @param configPath contains the relative or absolute path to the folder containing configuration files.
     * @return object for the application after parsing.
     */
    public static Optional<SLA> readSLA(String configPath) {
        return readConfigFile(configPath + File.separator + Constants.SLA_FILE);
    }

    /**
     *
     *
     * @param configPath contains the relative or absolute path to the folder containing configuration files.
     * @return object for the application after parsing.
     */
    public static Optional<ServerConfig> readServerConfig(String configPath) {
        return readConfigFile(configPath + File.separator + Constants.SERVER_CONFIG_FILE);
    }

    /**
     *
     * Generic config file reader. it automatically discover which type of object should
     * return depending on the caller.
     *
     * @param path the relative or absolute path of the file to parse.
     * @param <T> class of the object, changes depending of the YAML first tag.
     * @return an object of the parsed file.
     *
     * @todo safe cast?
     */
    public static <T> Optional<T> readConfigFile(String path) {
        try {
            InputStream input = new FileInputStream(new File(path));
            Yaml yaml = new Yaml(cc);
            T config = (T) yaml.load(input);
            return Optional.of(config);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Optional.absent();
        }

    }


    /**
     * Constructor class for YAML tags. Defines how to parse, transform to objects,
     * parsed YAML tags. For instance, when a !cal is found aside with a value will
     * rip off the !cal tag and transform the value into a ICalendar object. When find
     * an !amount tag will create a jScience type less Amount object.
     *
     * To add a new type here, you should link with the tag on CalConstructor and
     * then create a private class that extends AbstractConstruct with the method below.
     *
     * <pre>
     * {@code
     * public Object construct(Node node)
     * }
     * </pre>
     *
     * Here Node is the parsed YAML value after the tag in question. For instance
     * the constructor class for tag !amount is ConstructAmount. So when a tag !amount
     * is parsed the construct method of ConstructAmount will translate the node parameter
     * into a String and then find its amount using the jScience Amount class.
     *
     * To add new types, you should start it here defining a new tag and after that
     * a proper Construct class that inherent from AbstractConstruct. Remember to define
     * the construct method in the class, like this:
     *
     * <pre>
     * {@code
     * ...
     * this.yamlConstructors.put(new Tag("!power"), new ConstructPower());
     * ...
     *
     * private class ConstructPower extends AbstractConstruct {
     *    public Object construct(Node node) {
     *       String val = (String) constructScalar((ScalarNode) node);
     *       return Double.valueOf(val);
     *    }
     * }
     * }
     * </pre>
     *
     *
     * In the example above we create a new YAML tag !power that transform
     * the amount into double type.
     *
     */
    static class CalConstructor extends Constructor {
        private final Construct javaDateConstruct;

        public CalConstructor() {
        	javaDateConstruct = new ConstructYamlTimestamp();
            this.yamlConstructors.put(new Tag("!amount"), new ConstructAmount());
            this.yamlConstructors.put(new Tag("!date"), new ConstructDate());
        }

        private class ConstructAmount extends AbstractConstruct {
            public Object construct(Node node) {
                String val = (String) constructScalar((ScalarNode) node);
                return Amount.valueOf(val);
            }
        }
        
        private class ConstructDate extends AbstractConstruct {
            public Object construct(Node node) {
        	    Date date = (Date) javaDateConstruct.construct(node);
                return new DateTime(date, DateTimeZone.UTC);
            }

        }
        
        class JodaPropertyConstructor extends Constructor {
            public JodaPropertyConstructor() {
                yamlClassConstructors.put(NodeId.scalar, new TimeStampConstruct());
            }

            class TimeStampConstruct extends Constructor.ConstructScalar {
                @Override
                public Object construct(Node nnode) {
                    if (nnode.getTag().equals("tag:yaml.org,2002:timestamp")) {
                        Construct dateConstructor = yamlConstructors.get(Tag.TIMESTAMP);
                        Date date = (Date) dateConstructor.construct(nnode);
                        return new DateTime(date, DateTimeZone.UTC);
                    } else {
                        return super.construct(nnode);
                    }
                }
            }
        }
        
        public class JodaTimeConstructor extends Constructor {
            private final Construct javaDateConstruct;
            private final Construct jodaDateConstruct;

            public JodaTimeConstructor() {
                javaDateConstruct = new ConstructYamlTimestamp();
                jodaDateConstruct = new ConstructJodaTimestamp();
                // Whenever we see an explicit timestamp tag, make a Joda Date
                // instead
                yamlConstructors.put(Tag.TIMESTAMP, jodaDateConstruct);
                // See
                // We need this to work around implicit construction.
                yamlClassConstructors.put(NodeId.scalar, new TimeStampConstruct());
            }

            public class ConstructJodaTimestamp extends AbstractConstruct {
                public Object construct(Node node) {
                    Date date = (Date) javaDateConstruct.construct(node);
                    return new DateTime(date, DateTimeZone.UTC);
                }
            }

            class TimeStampConstruct extends Constructor.ConstructScalar {
                @Override
                public Object construct(Node nnode) {
                    if (nnode.getTag().equals(Tag.TIMESTAMP)) {
                        return jodaDateConstruct.construct(nnode);
                    } else {
                        return super.construct(nnode);
                    }
                }
            }
        }     
      
    }


    public static void addTypeDescription(Class myClass, String descr) {

        cc.addTypeDescription(new TypeDescription(myClass, descr));
    }


}