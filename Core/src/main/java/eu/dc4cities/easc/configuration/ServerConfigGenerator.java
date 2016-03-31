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

import eu.dc4cities.easc.resource.Server;
import org.jscience.physics.amount.Amount;

import javax.measure.unit.UnitFormat;
import java.util.ArrayList;
import java.util.List;

import static eu.dc4cities.easc.resource.Units.PAGE;
import static javax.measure.unit.NonSI.BYTE;
import static javax.measure.unit.SI.*;

/**
 * This class generates default config files
 */
public class ServerConfigGenerator extends ConfigGenerator {
	
	public ServerConfigGenerator() {
		UnitFormat.getInstance().label(PAGE, "Page");
        UnitFormat.getInstance().label(GIGA(BYTE), "GB");
	}

    public static void main(String[] args) {
        new ServerConfigGenerator().testCreateServerConfig();
        System.out.println(Amount.valueOf(100, MEGA(BYTE)).to(MEGA(BYTE)).toString());
    }

    public void testCreateServerConfig() {

        System.out.println("createServerConfig");

        DefaultServerConfig serverConfig = new DefaultServerConfig();
        
        Server Server = new Server(
                Amount.valueOf(100, GIGA(BYTE)),
        		Amount.valueOf(100, GIGA(BYTE)),
                Amount.valueOf(100, WATT),
                0,
        		"server1");
        List<eu.dc4cities.easc.resource.Server> serverList = new ArrayList<>();
        serverList.add(Server);
        serverConfig.setServers(serverList);
        writeConfig(serverConfig);
    }

    //for debugging purpose
    public static void writeConfig(ServerConfig conf) {
        writeYaml(Constants.SERVER_CONFIG_FILE, conf);
    }


}
