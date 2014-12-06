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
import com.dongli.model.MyConfiguration;
import com.dongli.model.MyJSONData;
import com.dongli.model.MyJSONObject;

public class JSONRESTResource extends ServerResource {
	
	/* This method will process GET request
	 * to a single object or a list of UIDs
	 * @return one object || all objects || error message
	 */
	@Get
    public Representation representGet() {
		
		String rootPath = getRequest().getResourceRef().getPath();
        String refPath = rootPath.substring(MyConfiguration.getInstance().getRootPrefix().length());
        
        // Get all objects
        if(refPath==null || refPath.trim().equals("") || refPath.equals("/")) {
        	return new StringRepresentation(MyJSONData.getAllObjects().toString()+"\n", MediaType.TEXT_PLAIN);
        }
        
        String uid = refPath.substring(1);
        if(uid==null || !refPath.substring(0, 1).equals("/")) {
        	return new StringRepresentation(getErrorMessage("GET", rootPath, "Not a valid REST url."), MediaType.TEXT_PLAIN);
        }
        
        // Get a specific object
     	MyJSONObject myJsonObject = MyJSONData.queryObject(uid);
     	
     	// Cannot find object, show error message
     	if(myJsonObject == null) {
        	getErrorMessage("GET", rootPath, "Cannot find object "+uid+".");
        	return new StringRepresentation(getErrorMessage("GET", rootPath, "Cannot find object "+uid+"."), MediaType.TEXT_PLAIN);
     	}
     	
       	return new StringRepresentation(myJsonObject.toString()+"\n", MediaType.TEXT_PLAIN);
    }
	
	/* This method will process POST request
	 * to create a new object
	 * @return created object || error message
	 */
	@Post
	public Representation representPost(Representation rep) throws IOException {
		
		// if there is no parameter with POST request
		if(rep == null) {
			String rootPath = getRequest().getResourceRef().getPath();
        	return new StringRepresentation(getErrorMessage("POST", rootPath, "Please provide JSON parameters."), MediaType.TEXT_PLAIN);
		}
		
		String msg = rep.getText();
		
		MyJSONObject myJsonObject = new MyJSONObject();
		
		if(myJsonObject.setJSONObjectFromString(msg) == 0) {
			String rootPath = getRequest().getResourceRef().getPath();
        	return new StringRepresentation(getErrorMessage("POST", rootPath, "Ilegal JSON pamameters."), MediaType.TEXT_PLAIN);
		}
		
		String uid = MyJSONData.createObject(myJsonObject);
		
		// if failed to create the new object
		if(uid == null) {
			String rootPath = getRequest().getResourceRef().getPath();
        	return new StringRepresentation(getErrorMessage("POST", rootPath, "Failed to create new object."), MediaType.TEXT_PLAIN);
		}
		
		// successfully create the new object
		MyJSONObject createdObject = MyJSONData.queryObject(uid);
		if(createdObject == null) {
			String rootPath = getRequest().getResourceRef().getPath();
        	return new StringRepresentation(getErrorMessage("POST", rootPath, "Failed to create new object."), MediaType.TEXT_PLAIN);
		}
		
		return new StringRepresentation(MyJSONData.queryObject(uid).toString()+"\n", MediaType.TEXT_PLAIN);
	}
	
	/* This method will process PUT request 
	 * to update an existing object
	 * @return new object || error message
	 */
	@Put
    public StringRepresentation representPut(Representation rep) throws IOException {
		
		// if there is no parameter with POST request
		if(rep == null) {
			String rootPath = getRequest().getResourceRef().getPath();
			return new StringRepresentation(getErrorMessage("POST", rootPath, "Please provide JSON parameters."), MediaType.TEXT_PLAIN);
		}
		
		String msg = rep.getText();
		MyJSONObject myJSONObject = new MyJSONObject();
		
		if(myJSONObject.setJSONObjectFromString(msg) == 0) {
			String rootPath = getRequest().getResourceRef().getPath();
        	return new StringRepresentation(getErrorMessage("POST", rootPath, "Ilegal JSON pamameters."), MediaType.TEXT_PLAIN);
		}
		
		if(myJSONObject.getUID() == null) {
			String rootPath = getRequest().getResourceRef().getPath();
			return new StringRepresentation(getErrorMessage("POST", rootPath, "Please privode uid in parameters.."), MediaType.TEXT_PLAIN);
		}
			
		// Failed to update object
		if(MyJSONData.updateObject(myJSONObject) == 0) {
			String rootPath = getRequest().getResourceRef().getPath();
        	return new StringRepresentation(getErrorMessage("PUT", rootPath, "failed to update object"), MediaType.TEXT_PLAIN);
		}
		
		return new StringRepresentation(MyJSONData.queryObject((String)(myJSONObject.getUID())).toString()+"\n", MediaType.TEXT_PLAIN);
    }
	
	/* This method will process DELETE request
	 * to delete an object 
	 * @ return null | error message
	 */
	@Delete
    public String representDelete() {
		
		String rootPath = getRequest().getResourceRef().getPath();
		String refPath = rootPath.substring(MyConfiguration.getInstance().getRootPrefix().length());
		
		if(refPath==null || refPath.equals("") || refPath.equals("/")) {
			return getErrorMessage("DELETE", rootPath, "Cannot find the object to delete.");
		}
		
		String uid = refPath.substring(1);
		
		// cannot find the object to be deleted
		if(MyJSONData.deleteObject(uid) == 0) {
			return getErrorMessage("DELETE", rootPath, "Cannot find the object to delete.");
		}
		
        return "";
    }
	
	private String getErrorMessage(String verb, String root, String msg) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("verb", verb);
			jsonObject.put("url", MyConfiguration.getInstance().getUrlPrefix()+root);
			jsonObject.put("message", msg);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject.toString()+"\n";
	}
}
