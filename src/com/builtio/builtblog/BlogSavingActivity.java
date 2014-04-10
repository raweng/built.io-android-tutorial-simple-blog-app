package com.builtio.builtblog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.raweng.built.BuiltACL;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;

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
public class BlogSavingActivity extends Activity{

	private EditText postEditText;

	BuiltACL builtACL;
	String 	 builtObjectUid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);

		postEditText = (EditText) findViewById(R.id.blogEditText);

		Intent intent = getIntent();
		
		if (intent.hasExtra("uid")) {
			builtObjectUid = intent.getStringExtra("uid");
			postEditText.setText(intent.getStringExtra("content"));
		}	

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.post, menu);

		MenuItem item = (MenuItem) menu.findItem(R.id.action_new);
		item.setVisible(false);
		MenuItem item1 = (MenuItem) menu.findItem(R.id.action_refresh);
		item1.setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.saveMenu: {

			AlertDialog.Builder alert = new AlertDialog.Builder(BlogSavingActivity.this);
			alert.setIcon(R.drawable.ic_launcher)
			.setMessage("Make it Public ?")
			.setTitle("Accessibility")
			.setPositiveButton("Public", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					/*
					 * Enabling the Read, Write, and Delete Access to public.
					 * This task can be done easily using BuiltACL class.
					 */
					builtACL = new BuiltACL();

					builtACL.setPublicReadAccess(true);
					builtACL.setPublicWriteAccess(true);
					builtACL.setPublicDeleteAccess(true);

					saveObject();
				}
			})
			.setNegativeButton("Private", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					/*
					 * Disabling the Read, Write, and Delete Access to public.
					 * This task can be done easily using BuiltACL class.
					 */
					builtACL = new BuiltACL();

					builtACL.setPublicReadAccess(false);
					builtACL.setPublicWriteAccess(false);
					builtACL.setPublicDeleteAccess(false);

					/**
					 * If user user logged in with save session
					 * then user can set acl for personal use with Read, Write, and Delete Access.
					 */
					/*builtACL.setUserDeleteAccess(BuiltUser.getSession().getUserUid(), true);
					builtACL.setUserReadAccess(BuiltUser.getSession().getUserUid(), true);
					builtACL.setUserWriteAccess(BuiltUser.getSession().getUserUid(), true);*/

					/*
					 * Creating & Updating Object.
					 */
					saveObject();

				}
			}).show();

			break;
		}
		case R.id.cancelMenu: {
			finish();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Creating & Updating Object.
	 */
	public void saveObject(){

		BuiltObject object = new BuiltObject("blognote");
		
		/*
		 * Setting up the fields
		 */
		object.set("blogtext", postEditText.getText().toString());

		if(!TextUtils.isEmpty(builtObjectUid)){
			/*
			 * Setting uid of object for update.
			 */
			object.setUid(builtObjectUid);
		}
		object.setACL(builtACL);
		
		object.save(new BuiltResultCallBack() {

			@Override
			public void onSuccess() {
				/// object is created successfully
				Toast.makeText(BlogSavingActivity.this, "Object created Successfully...", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(BuiltError builtErrorObject) {
				
				/// there was an error in creating the object
			    /// builtErrorObject will contain more details
				
				Toast.makeText(BlogSavingActivity.this, "Error : "+builtErrorObject.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAlways() {
				
				// write code here that you want to execute
			    // regardless of success or failure of the operation
				finish();
			}
		});

	}


}
