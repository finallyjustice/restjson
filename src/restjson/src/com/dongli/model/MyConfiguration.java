/** File:		MyConfiguration.java
 ** Author:		Dongli Zhang
 ** Contact:	dongli.zhang0129@gmail.com
 **
 ** Copyright (C) Dongli Zhang 2014
 **
 ** This program is free software;  you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation; either version 2 of the License, or
 ** (at your option) any later version.
 **
 ** This program is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY;  without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 ** the GNU General Public License for more details.
 **
 ** You should have received a copy of the GNU General Public License
 ** along with this program;  if not, write to the Free Software 
 ** Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * This file is used for server configuration
 */

package com.dongli.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MyConfiguration {
	public String urlPrefix;    // domain name and port of the server, e.g., http://localhost:8080
	public String accessKey;    // aws access key
	public String secretKey;    // aws secret key
	public String bucket;       // aws bucket
	public String rootPrefix;   // web application root path
	
	private static MyConfiguration myConfiguration;
	
	private MyConfiguration() {
		
		Properties props=System.getProperties(); 
		
		File configFile = new File(props.getProperty("user.home")+"/restjsonhome/restjson.config");
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile));
			String line = "";
			  
			while((line = bufferedReader.readLine()) != null) {
				if(!line.startsWith("#") && !line.trim().equals("")) {
					String[] tmpParam = line.split(" ");
					if(tmpParam[0].equals("url"))
						urlPrefix = tmpParam[1];
					if(tmpParam[0].equals("access_key"))
						accessKey = tmpParam[1];
					if(tmpParam[0].equals("secret_key"))
						secretKey = tmpParam[1];
					if(tmpParam[0].equals("bucket"))
						bucket = tmpParam[1];
				}
			}
			bufferedReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		rootPrefix = "/restjson/object";
	}
	
	public static MyConfiguration getInstance() {
		if(myConfiguration == null) {
			myConfiguration = new MyConfiguration();
		}
		return myConfiguration;
	}
}
