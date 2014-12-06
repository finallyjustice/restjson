/** File:		MyJSONData.java
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
 * This object is the data model for JSON object
 */

package com.dongli.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyJSONData {
	
	/* 
	 * create the new JSON object in data file
	 * @return successfully - uid, failed - null
	 */
	public static String createObject(MyJSONObject mjo) {
		// generate uid for this object
		String uid = UIDGenerator.getUID();
		mjo.setUID(uid);
		
		// path of data file to store this object
		String path = MyConfiguration.getInstance().getJsondbFolder()+uid;  
		
		try {  
            FileWriter fw = new FileWriter(path, false);  
            PrintWriter pw = new PrintWriter(fw);  
            pw.println(mjo.toString());  
            pw.close();  
            fw.close();  
        } catch (IOException e) {  
            //e.printStackTrace();
        	// failed to create the new object
            File nf = new File(path);
            nf.delete();
            return null;
        }  
        
		return uid;
	}
	
	/*
	 * Update an existing JSON object
	 * @return successfully-1, failed-0
	 */
	public static int updateObject(MyJSONObject mjo) {
		String uid = mjo.getUID();
		String path = MyConfiguration.getInstance().getJsondbFolder()+uid; 
		
		// check if uid exists
		File file = new File(path);
		if(!file.exists()) {
			//return 0 if not exist
			return 0;
		}
		
		try {  
            FileWriter fw = new FileWriter(path, false);  
            PrintWriter pw = new PrintWriter(fw);  
            pw.println(mjo.toString());  
            pw.close();  
            fw.close();  
        } catch (IOException e) {  
        	// failed to update object
            //e.printStackTrace();
        	return 0;
        }  
		
		return 1;
	}
	
	/*
	 * Query a JSON object
	 * Return null of not found
	 */
	public static MyJSONObject queryObject(String uid) {
		String path = MyConfiguration.getInstance().getJsondbFolder()+uid;
		File file = new File(path);
		
		String jsonStr = "";
		StringBuilder stringBuilder = new StringBuilder();
		MyJSONObject myJSONObject = new MyJSONObject();
		
		// file not found
		if(!file.exists())
			return null;
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String line = "";
			  
			while((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
			
			bufferedReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		jsonStr = stringBuilder.toString();
		
		if(myJSONObject.setJSONObjectFromString(jsonStr) == 0)
			return null;
		
		return myJSONObject; 
	}
	
	/*
	 * Delete a JSON object
	 * @return object not exist - 0, successfully - 1
	 */
	public static int deleteObject(String uid) {
		File file = new File(MyConfiguration.getInstance().getJsondbFolder()+uid);
		
		// JSON object does not exist
		if(!file.exists())
			return 0;
		
		file.delete();
		return 1;
	}
	
	/*
	 * Query the list of <"url", url> 
	 */
	public static JSONArray getAllObjects() {
		
		File dir = new File(MyConfiguration.getInstance().getJsondbFolder());
		File[] files = dir.listFiles(); 
		JSONArray jsonArray = new JSONArray();
		
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i].getName();
			JSONObject jb = new JSONObject();
			try {
				jb.put("url", MyConfiguration.getInstance().getUrlPrefix()+MyConfiguration.getInstance().getRootPrefix()+"/"+fileName);
				jsonArray.put(jb);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return jsonArray;
	}
}
