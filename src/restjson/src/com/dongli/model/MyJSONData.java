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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.dongli.exception.MyRESTException;

public class MyJSONData {
	
	/* 
	 * create the new JSON object in data file
	 * @return uid
	 */
	public static String createObject(MyJSONObject mjo) throws MyRESTException {
		
		// generate uid for this object
		String uid = UIDGenerator.getUID();
		mjo.setUID(uid);

		// tmp path of data file to store this object. The file will to sent to S3 later.
		String path = "/tmp/" + uid;

		try {
			FileWriter fw = new FileWriter(path, false);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(mjo.toString());
			pw.close();
			fw.close();
		} catch (IOException e) {
			// e.printStackTrace();
			// failed to create the new object
			File nf = new File(path);
			nf.delete();
			throw new MyRESTException("Failed to create the object "+uid+".");
		}
		
		// create the new object on AWS S3 
		try {
			File uploadFile = new File(path);
			MyAWSStorage.getInstance().s3client.putObject(new PutObjectRequest(MyConfiguration.getInstance().bucket, uid, uploadFile));
		} catch (AmazonServiceException ase) {
			throw new MyRESTException("Failed to create the object "+uid+".");
		} catch (AmazonClientException ace) {
			throw new MyRESTException("Failed to create the object "+uid+".");
		}

		return uid;
	}
	
	/*
	 * Update an existing JSON object
	 */
	public static void updateObject(MyJSONObject mjo) throws MyRESTException {
		
		String uid = mjo.getUID();
		MyJSONObject myJSONObject = queryObject(uid);
		
		String path = "/tmp/" + uid;

		// write the JSON object to a tmp file
		try {
			FileWriter fw = new FileWriter(path, false);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(mjo.toString());
			pw.close();
			fw.close();
		} catch (IOException e) {
			// failed to create the new object
			File nf = new File(path);
			nf.delete();
			throw new MyRESTException("Failed to update the object.");
		}
		
		// update the new object on Amazon AWS S3 
		try {
			File uploadFile = new File(path);
			MyAWSStorage.getInstance().s3client.putObject(new PutObjectRequest(MyConfiguration.getInstance().bucket, uid, uploadFile));
		} catch (AmazonServiceException ase) {
			throw new MyRESTException("Failed to update the object "+uid+".");
		} catch (AmazonClientException ace) {
			throw new MyRESTException("Failed to qpdate the object "+uid+".");
		} 
	}
	
	/*
	 * Query a JSON object
	 */
	public static MyJSONObject queryObject(String uid) throws MyRESTException {
		
		String jsonStr = "";
		MyJSONObject myJSONObject = new MyJSONObject();
		
		try {
			// send query command to AWS S3
			S3Object s3object = MyAWSStorage.getInstance().s3client.getObject(new GetObjectRequest(MyConfiguration.getInstance().bucket, uid));
			jsonStr = displayTextInputStream(s3object.getObjectContent());
			// convert the object file to JSON object
			myJSONObject.setJSONObjectFromString(jsonStr);
		} catch (AmazonServiceException ase) {
			throw new MyRESTException("Failed to query the object "+uid+".");
		} catch (AmazonClientException ace) {
			throw new MyRESTException("Failed to query the object "+uid+".");
		} catch (IOException e) {
			throw new MyRESTException("Failed to query the object "+uid+".");
		} 

		return myJSONObject; 
		
	}
	
	/*
	 * Delete a JSON object
	 */
	public static void deleteObject(String uid) {
		try {
			MyAWSStorage.getInstance().s3client.deleteObject(new DeleteObjectRequest(MyConfiguration.getInstance().bucket, uid));
		} catch (AmazonServiceException ase) {
			// nothing
		} catch (AmazonClientException ace) {
			// nothing
		}
	}
	
	/*
	 * Query the list of <"url", url> 
	 */
	public static JSONArray getAllObjects() throws MyRESTException {

		// return value
		JSONArray jsonArray = new JSONArray();

		try {
			ObjectListing objects = MyAWSStorage.getInstance().s3client
					.listObjects(MyConfiguration.getInstance().bucket);

			do {
				for (S3ObjectSummary objectSummary : objects
						.getObjectSummaries()) {
					JSONObject jb = new JSONObject();
					jb.put("url", MyConfiguration.getInstance().urlPrefix
							+ MyConfiguration.getInstance().rootPrefix + "/"
							+ objectSummary.getKey());
					jsonArray.put(jb);
					// System.out.println(objectSummary.getKey());
				}

				objects = MyAWSStorage.getInstance().s3client
						.listNextBatchOfObjects(objects);
			} while (objects.isTruncated());
		} catch (JSONException e) {
			throw new MyRESTException("Failed to get all JSON object.");
		}
		catch (AmazonServiceException ase) {
			throw new MyRESTException("Failed to get all JSON object.");
		} catch (AmazonClientException ace) {
			throw new MyRESTException("Failed to get all JSON object.");
		}
		
		return jsonArray;
	}
	
	/*
	 * parse the object file sent from Amazon AWS S3
	 */
	private static String displayTextInputStream(InputStream input)
			throws IOException {
		// Read one text line at a time and display.
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder stringBuilder = new StringBuilder();
		
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			stringBuilder.append(line);
		}
		reader.close();
		
		return stringBuilder.toString();
	}
}
