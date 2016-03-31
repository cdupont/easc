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

/**
 * initial config to talk to CF API
 */
public class CloudFoundryConfig {

	private String username;
	private String password;
	//API endpoint: This is the URL of the Cloud Controller in your Cloud Foundry instance.
	private String apiEndpoint;
	//Org: The organization where you want to deploy your application.
	private String org;
	//The space in the organization where you want to deploy your application.
	private String space;
//	private String EASCName = Constants.DEFAULT_EASC_NAME;

	public CloudFoundryConfig(String username, String password,
			String apiEndpoint, String org, String space) {
		this.username = username;
		this.password = password;
		this.apiEndpoint = apiEndpoint;
		this.org = org;
		this.space = space;
	}
	
	public CloudFoundryConfig() {
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getApiEndpoint() {
		return apiEndpoint;
	}
	public void setApiEndpoint(String apiEndpoint) {
		this.apiEndpoint = apiEndpoint;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	public String getSpace() {
		return space;
	}
	public void setSpace(String space) {
		this.space = space;
	}
}
