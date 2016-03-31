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
public class OpenStackConfig {
	private String username;
	private String password;
	//API endpoint: This is the URL of the Cloud Controller in your OpenStack instance.
	private String apiEndpoint;
	//Tenant name
	private String tenant;
	//The space in the organization where you want to deploy your application.
	
	public OpenStackConfig() {
	}
	
	public OpenStackConfig(String username, String password,
			String apiEndpoint, String tenant) {
		this.username = username;
		this.password = password;
		this.apiEndpoint = apiEndpoint;
		this.tenant = tenant;
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

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
}
