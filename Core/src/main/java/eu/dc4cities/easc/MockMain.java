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

import java.util.List;

/**
 * Example of Main.
 */
public class MockMain {

	public static void main(String args[]) {
		System.out.println("Generic EASC");
		List<String> configDirectory = Utils.parseCmdLineArgs(args);

		//for each EASC
		for(String c : configDirectory) {
	        EASC easc = new EASC(c, new MockMonitor());
	        easc.init(true);
	        easc.start();
	        
		}
        
	}
}
