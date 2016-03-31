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

public class TestStandAlone extends TestCase {

    private String[] workingDir;

    /**
     * Method to set variable to all tests. For instane, set directory of configuration files.
     */
    protected void setUp() {
        //Set working directory to resources, if you change the configuration files you
        //need also to update the expected behavior of the test.
        workingDir = new String[1];
        workingDir[0] = "./resource/";
    }

    /**
     * Test the application as a standalone.
     */
    public void testApplication() {

    //        Main.main(workingDir);

    }

}