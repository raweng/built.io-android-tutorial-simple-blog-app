package com.builtio.builtblog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.raweng.built.Built;
import com.raweng.built.BuiltACL;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltClass;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.utilities.BuiltConstant;

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

    private BuiltACL builtACL;

    private String 	 builtObjectUid;

    private Context  context;

    private BuiltApplication builtApplication;

    private int position = 0;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        context = BlogSavingActivity.this;

        initProgressDialog();

        /*
         * Initialised builtApplication with api key.
         */
        try {
            builtApplication = Built.application(context, "bltdfcc61830fb5b32b");
        } catch (Exception e) {
            e.printStackTrace();
        }

        postEditText = (EditText) findViewById(R.id.blogEditText);

        Intent intent = getIntent();

        if (intent.hasExtra("uid")) {
            builtObjectUid = intent.getStringExtra("uid");
            postEditText.setText(intent.getStringExtra("content"));
            position = intent.getIntExtra("position", 0);
        }

    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(BlogSavingActivity.this);
        progressDialog.setMessage("Loading Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);

        MenuItem item = menu.findItem(R.id.action_new);
        item.setVisible(false);
        MenuItem item1 = menu.findItem(R.id.action_refresh);
        item1.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveMenu: {

                if (!TextUtils.isEmpty(postEditText.getText())){
                    showACLDialog();
                }else {
                    showToast("Empty post");
                }

                break;
            }
            case R.id.cancelMenu: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showACLDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
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
                        builtACL = builtApplication.acl();
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
                        builtACL = builtApplication.acl();

                        builtACL.setPublicReadAccess(false);
                        builtACL.setPublicWriteAccess(false);
                        builtACL.setPublicDeleteAccess(false);

                        /**
                         * If user logged in with save session
                         * then user can set acl for personal use with Read, Write, and Delete Access.
                         */

                    /*
					builtACL.setUserDeleteAccess(builtApplication.getCurrentUser().getUserUid(), true);
					builtACL.setUserReadAccess(builtApplication.getCurrentUser().getUserUid(), true);
					builtACL.setUserWriteAccess(builtApplication.getCurrentUser().getUserUid(), true);
                    */


					/*
					 * Creating & Updating Object.
					 */
                        saveObject();

                    }
                }).show();

    }

    /**
     * Creating & Updating Object.
     */
    public void saveObject(){

        try {

            progressDialog.show();

            final BuiltObject object;

            BuiltClass builtClass = builtApplication.classWithUid("blognote");

            if(!TextUtils.isEmpty(builtObjectUid)){
                object = builtClass.object(builtObjectUid);
            }else {
                object = builtClass.object();
            }

               /*
		        * Setting up the fields
		        */

            object.set("blogtext", postEditText.getText().toString().trim());
            object.setACL(builtACL);
            object.saveInBackground( new BuiltResultCallBack() {
                @Override
                public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                    if(builtError == null){

                        Intent intent = new Intent();
                        intent.putExtra("content", postEditText.getText().toString());
                        intent.putExtra("uid", object.getUid());

                        if (builtObjectUid != null){
                            showToast("Object updated successfully...");
                            intent.putExtra("position" , position);
                        }else {
                            showToast("Object created successfully...");
                        }

                        progressDialog.dismiss();

                        setResult(RESULT_OK , intent);
                        finish();

                    }else {
                        showToast(builtError.getErrorMessage());
                    }

                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void showToast(String msg){
        Toast.makeText(context , msg , Toast.LENGTH_SHORT).show();
    }



}
