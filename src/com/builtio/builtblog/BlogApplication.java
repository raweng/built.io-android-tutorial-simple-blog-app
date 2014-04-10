package com.builtio.builtblog;

import android.app.Application;

import com.raweng.built.Built;

/**
 * This is built.io android tutorial.
 * 
 * Short introduction of some classes with some methods.
 * Contain classes: 
 * 1. BuiltQuery
 * 2. BuiltObject
 * 3. BuiltACL
 * 4. BuiltUser
 * 
 * For quick start with built.io refer "http://docs.built.io/quickstart/index.html#android"
 * 
 * @author raw engineering, Inc
 *
 */
public class BlogApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		/*
		 * Initialize the application for a project using built.io Application credentials "Application Api Key" 
		 * and "Application UID".
		 * 
		 */
		try {
			Built.initializeWithApiKey(BlogApplication.this, "bltdfcc61830fb5b32b", "notesapp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
