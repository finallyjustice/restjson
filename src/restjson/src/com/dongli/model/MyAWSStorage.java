/** File:		MyAWSStorage.java
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
 * This is a singleton class to maintain a persistent AmazonS3Client instance.
 */

package com.dongli.model;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

public class MyAWSStorage {
	private static MyAWSStorage myAWSStorage;
	private AWSCredentials myCredentials;
	// This s3client will be used by other classes
	public AmazonS3Client s3client;
	
	private MyAWSStorage() {
		myCredentials = new BasicAWSCredentials(MyConfiguration.getInstance().accessKey, MyConfiguration.getInstance().secretKey);
		s3client = new AmazonS3Client(myCredentials);
	}
	
	public static MyAWSStorage getInstance() {
		if(myAWSStorage == null) 
			myAWSStorage = new MyAWSStorage();
		return myAWSStorage;
	}
}
