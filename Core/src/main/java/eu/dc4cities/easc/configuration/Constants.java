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

import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Duration;

import static javax.measure.unit.SI.SECOND;

public class Constants {

    public static String DEFAULT_ENERGY_SERVICE = "default";
	public static String APP_CONFIG_FILE = "AppConfig.yaml";
    public static String CONFIG_FILE = "Config.yaml";
    public static String SLA_FILE = "SLA.yaml";
    public static String CONFIG_FOLDER = "resource";
    public static String SERVER_CONFIG_FILE = "ServerConfig.yaml";
    
    public static String DEFAULT_ENERGIS_URL = "hackmeplz";
    public static Integer DEFAULT_ENERGIS_PORT = 9999; //hackmeplz
    public static Amount<Duration> DEFAULT_MONIT_SAMPLING_TIME = Amount.valueOf(15, SECOND);
    public static String DEFAULT_ENERGIS_EXECUTE_URL = "127.0.0.1"; //Hackmeplz
    public static Integer DEFAULT_ENERGIS_EXECUTE_PORT = 80;
    public static String DEFAULT_APP_NAME = "defaultName";
    public static String VERSION = "v1";
    public static String EASC_PATH = "/" + VERSION + "/easc/" + DEFAULT_APP_NAME + "/";
    public static Integer DEFAULT_APP_PORT = 9999;
    public static String DEFAULT_SITE_CODE = "default_site";
    
}
