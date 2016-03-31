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

import eu.dc4cities.controlsystem.model.metrics.AssetCatalog;
import eu.dc4cities.controlsystem.model.metrics.AssetCatalog.AssetType;
import eu.dc4cities.easc.configuration.ConfigGenerator;
import eu.dc4cities.easc.configuration.Constants;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utilities.
 */
public class Utils {

	public static List<String> parseCmdLineArgs(String[] args) {
		Options options = new Options();
		options.addOption("g", "generate", false, "generate default config files");
		Option confs = new Option("c", "config", true, "specify EASC config directory");
		confs.setArgs(Option.UNLIMITED_VALUES);
		options.addOption(confs);
		options.addOption("h", "help", false, "usage");
		
		List<String> configDirectory = new ArrayList<String>();
		
		CommandLineParser parser = new BasicParser();
		try {
			CommandLine cmd = parser.parse(options, args);
				
			if(cmd.hasOption("h")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "EASC", options, true );
				return null;
			}
			
			if(cmd.hasOption("g")) {
				ConfigGenerator cg = new ConfigGenerator();
				cg.CreateWorkingMode();
				cg.CreateConfig();
				cg.CreateSLA();
				System.exit(0);
				return null;
			}
			
			
			if(cmd.getOptionValue("c") != null) {
				configDirectory.addAll(Arrays.asList(cmd.getOptionValues("c")));	
			} else {
				configDirectory.add(Constants.CONFIG_FOLDER);
			}	
			
			
		} catch (ParseException e) {
			e.printStackTrace();
			configDirectory.add(Constants.CONFIG_FOLDER);
		}
		return configDirectory;
	}
	

    public static boolean assetCatalogContains(String s, AssetType type)
    {
        for(AssetCatalog asset : AssetCatalog.values())
             if (asset.getCode().equals(s) && asset.getAssetType().equals(type)) 
                return true;
        return false;
    } 

}
