/** File:		JSONRESTResurce.java
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

package com.dongli.restlet;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.dongli.exception.MyRESTException;
import com.dongli.model.MyConfiguration;
import com.dongli.model.MyJSONData;
import com.dongli.model.MyJSONObject;

public class JSONRESTResource extends ServerResource {
	
	/* This method will process GET request
	 * to a single object or a list of UIDs
	 */
	@Get
	public Representation representGet() {

		String rootPath = getRequest().getResourceRef().getPath();
		String refPath = rootPath
				.substring(MyConfiguration.getInstance().rootPrefix.length());

		// Get a specific object
		MyJSONObject myJsonObject;

		try {
			// Get all objects
			if (refPath == null || refPath.trim().equals("")
					|| refPath.equals("/")) {
				return new StringRepresentation(MyJSONData.getAllObjects()
						.toString() + "\n", MediaType.TEXT_PLAIN);
			}

			String uid = refPath.substring(1);
			if (uid == null || !refPath.substring(0, 1).equals("/")) {
				throw new MyRESTException("Not a valid REST url.");
			}

			myJsonObject = MyJSONData.queryObject(uid);
		} catch (MyRESTException e) {
			// TODO Auto-generated catch block
			return new StringRepresentation(getErrorMessage("GET", rootPath,
					e.getMessage()), MediaType.TEXT_PLAIN);
		}

		return new StringRepresentation(myJsonObject.toString() + "\n",
				MediaType.TEXT_PLAIN);
	}
	
	/* This method will process POST request
	 * to create a new object
	 */
	@Post
	public Representation representPost(Representation rep) throws IOException {
		
		String uid;
		
		try {
			// if there is no parameter with POST request
			if(rep == null) 
				throw new MyRESTException("Please provide JSON parameters."); 
		
			String msg = rep.getText();
			MyJSONObject myJsonObject = new MyJSONObject();
		
			myJsonObject.setJSONObjectFromString(msg);
		
			uid = MyJSONData.createObject(myJsonObject);
			// successfully create the new object
			MyJSONObject createdObject = MyJSONData.queryObject(uid);
			if(createdObject == null)
				throw new MyRESTException("Failed to create the object "+uid+".");
			
			return new StringRepresentation(MyJSONData.queryObject(uid).toString()+"\n", MediaType.TEXT_PLAIN);
			
		} catch (MyRESTException e) {
			//e.printStackTrace();
			String rootPath = getRequest().getResourceRef().getPath();
			return new StringRepresentation(getErrorMessage("POST", rootPath, e.getMessage()), MediaType.TEXT_PLAIN);
		}
	}
	
	/* This method will process PUT request 
	 * to update an existing object
	 */
	@Put
    public StringRepresentation representPut(Representation rep) throws IOException {
		
		MyJSONObject myJSONObject;
		
		try {
			// if there is no parameter with POST request
			if(rep == null)
				throw new MyRESTException("Please provide JSON parameters.");
		
			String msg = rep.getText();
			myJSONObject = new MyJSONObject();
		
			myJSONObject.setJSONObjectFromString(msg);
			if(myJSONObject.getUID() == null) 
				throw new MyRESTException("Please privode uid in parameters..");
			
			// Failed to update object
			MyJSONData.updateObject(myJSONObject);
			
			return new StringRepresentation(MyJSONData.queryObject((String)(myJSONObject.getUID())).toString()+"\n", MediaType.TEXT_PLAIN);
		} catch (MyRESTException e) {
			String rootPath = getRequest().getResourceRef().getPath();
			return new StringRepresentation(getErrorMessage("PUT", rootPath, e.getMessage()), MediaType.TEXT_PLAIN);
		}
    }
	
	/* This method will process DELETE request
	 * to delete an object 
	 */
	@Delete
    public String representDelete() {
		
		String rootPath = getRequest().getResourceRef().getPath();
		String refPath = rootPath.substring(MyConfiguration.getInstance().rootPrefix.length());
		
		if(refPath==null || refPath.equals("") || refPath.equals("/")) {
			return "";
		}
		
		String uid = refPath.substring(1);
		MyJSONData.deleteObject(uid);
		
        return "";
    }
	
	/*
	 * Generate the error message for REST API
	 */
	private String getErrorMessage(String verb, String root, String msg) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("verb", verb);
			jsonObject.put("url", MyConfiguration.getInstance().urlPrefix+root);
			jsonObject.put("message", msg);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject.toString()+"\n";
	}
}
