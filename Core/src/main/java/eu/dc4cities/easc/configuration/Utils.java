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
import eu.dc4cities.easc.configuration.ConfigGenerator.CalRepresenter;
import eu.dc4cities.easc.configuration.ConfigReader.CalConstructor;
import eu.dc4cities.easc.resource.CFApplication;
import eu.dc4cities.easc.resource.Server;
import eu.dc4cities.easc.resource.VM;
import eu.dc4cities.easc.sla.SLA;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * This class provides a set of constructor to associate classes of objects to
 * tags on the yaml files. For instance, the !Application will create an Application
 * object, because the String "!Application" is associated to the Application.class
 * specification.
 *
 * @see eu.dc4cities.easc.configuration.ConfigReader
 */
public class Utils {

	public static CalConstructor getCalConstructor() {
		CalConstructor cc = new CalConstructor();
		cc.addTypeDescription(new TypeDescription(Configuration.class,       "!Configuration"));
		cc.addTypeDescription(new TypeDescription(Application.class,         "!Application"));
		cc.addTypeDescription(new TypeDescription(VM.class,                  "!VM"));
		cc.addTypeDescription(new TypeDescription(Server.class,              "!Server"));
		cc.addTypeDescription(new TypeDescription(CFApplication.class,       "!CFApplication"));
		cc.addTypeDescription(new TypeDescription(SLA.class,                 "!SLA"));
		cc.addTypeDescription(new TypeDescription(DefaultServerConfig.class, "!ServerConfig"));

		return cc;
	}	

	public static CalRepresenter getCalRepresenter() {
		CalRepresenter cr = new CalRepresenter();
		cr.addClassTag(Configuration.class,       new Tag("!Configuration"));
		cr.addClassTag(Application.class,         new Tag("!Application"));
		cr.addClassTag(VM.class,                  new Tag("!VM"));
		cr.addClassTag(Server.class,              new Tag("!Server"));
		cr.addClassTag(CFApplication.class,       new Tag("!CFApplication"));
		cr.addClassTag(SLA.class, 				  new Tag("!SLA"));
		cr.addClassTag(DefaultServerConfig.class, new Tag("!ServerConfig"));

		return cr;
	}
}