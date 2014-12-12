/** File:		MyJSONObject.java
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
 * This file defines my own JSON Object.
 */

package com.dongli.model;

import org.json.JSONException;
import org.json.JSONObject;
import com.dongli.exception.MyRESTException;

public class MyJSONObject {
	
	// arbitrary number of fields
	private JSONObject jsonObject;
	
	// constructor
	public MyJSONObject() {
		
	}
	
	/*
	 * Given a json string to generate the jsonObject
	 */
	public void setJSONObjectFromString(String input) throws MyRESTException {
		try {
			jsonObject = new JSONObject(input);
		} catch (JSONException e) {
			//e.printStackTrace();
			throw new MyRESTException("Ilegal JSON pamameters.");
		}
	}
	
	public String getUID() {
		try {
			return (String) jsonObject.get("uid");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setUID(String uid) {
		try {
			jsonObject.put("uid", uid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Convert this object to JSON string
	 */
	@Override
	public String toString() {
		return jsonObject.toString();
	}
}
