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


import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationTest extends TestCase {
	
	private Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

	@Override
	public void setUp() {
		
	}

	@Test
	//test complete loop when WTD is about to stop.
	public void testTrialStop() {

		//1. ask option plan from DefaultEascOptionPlanBuilder.getEascOptionPlan
		//2. select 1 plan from the response
		//3. push that plan to DefaultActivityPlanExecutor.executeActivityPlan
		//4. analyse result
		
		//5. advance time 15min
		//6. redo the test
    }
}
