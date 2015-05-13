package com.builtio.builtblog;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.raweng.built.Built;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltClass;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltQuery;
import com.raweng.built.QueryResult;
import com.raweng.built.QueryResultsCallBack;
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
public class PostActivity extends ListActivity {

    private ArrayList<String> postList;

    private ArrayList<String> postUid;

    private ArrayAdapter<String> adapter;

    private BuiltApplication builtApplication;

    private Context context;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = PostActivity.this;

        initProgressDialog();

        try {
            builtApplication = Built.application(PostActivity.this , "bltdfcc61830fb5b32b");
        } catch (Exception e) {
            e.printStackTrace();
        }

        postList = new ArrayList<>();
        postUid  = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, postList);

        setListAdapter(adapter);

        /**
         * If user want set ACL on account to make blog private then at first user need to login
         */

        /*
        BuiltUser builtUser = builtApplication.builtUser();
        builtUser.loginInBackground("YOUR_AUTHORISED_EMAIL_ID_HERE","YOUR_PASSWORD", new BuiltResultCallBack() {
            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                if (builtError == null){
                    fetchAllPostUsingBuiltQuery();
                }else {
                    Toast.makeText(context, "error :"+builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        */

        /**
         * Fetching all posts
         */
        fetchAllPostUsingBuiltQuery();
    }

    /**
     * Initialised progress Dialog.
     */
    private void initProgressDialog() {
        progressDialog = new ProgressDialog(PostActivity.this);
        progressDialog.setMessage("Loading Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);

        MenuItem item = menu.findItem(R.id.saveMenu);
        item.setVisible(false);
        MenuItem item1 = menu.findItem(R.id.cancelMenu);
        item1.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                fetchAllPostUsingBuiltQuery();
                break;
            }
            case R.id.action_new: {
                createBlogPost();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Open activity to create new post.
     */
    private void createBlogPost() {
        Intent intent = new Intent(PostActivity.this, BlogSavingActivity.class);
        startActivityForResult(intent, 200);
    }

    /**
     * Fetch all BuiltObjects of class.
     * Using BuiltQuery class.
     */
    public void fetchAllPostUsingBuiltQuery(){

		/* Creating the BuiltQuery object with class uid.
		 * Executing query to get BuiltObject. 
		 */

        try {
            progressDialog.show();

            BuiltClass  builtClass = builtApplication.classWithUid("blognote");
            BuiltQuery  builtQuery = builtClass.query();
            builtQuery.execInBackground( new QueryResultsCallBack() {
                @Override
                public void onCompletion(BuiltConstant.ResponseType responseType, QueryResult queryResult, BuiltError builtError) {

                    if (builtError == null){
                        List<BuiltObject> builtObjectList = queryResult.getResultObjects();

                        for (int i = 0; i < builtObjectList.size(); i++) {
                            /**
                             * Extracting data from List<BuiltObject>.
                             */
                            if(!postUid.contains(builtObjectList.get(i).getUid())){
                                postList.add(builtObjectList.get(i).getString("blogtext"));
                                postUid.add(builtObjectList.get(i).getUid());
                            }
                        }

                        ((ArrayAdapter<String>) getListAdapter()).notifyDataSetChanged();

                    }else {
                        Toast.makeText(PostActivity.this , "Error:"+builtError.getErrorMessage() , Toast.LENGTH_SHORT).show();
                    }

                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onListItemClick(ListView list, View v, int position, long id) {
        super.onListItemClick(list, v, position, id);
        Intent intent = new Intent(PostActivity.this, BlogSavingActivity.class);
        intent.putExtra("content", list.getItemAtPosition(position).toString());
        intent.putExtra("uid", postUid.get(position).toString());
        intent.putExtra("position" , position);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100){

            if (resultCode == RESULT_OK){

                if (data != null){
                    postUid.set(data.getIntExtra("position", 0), data.getStringExtra("uid"));
                    postList.set(data.getIntExtra("position", 0) , data.getStringExtra("content"));

                    ((ArrayAdapter<String>) getListAdapter()).notifyDataSetChanged();
                }

            }
        }else if (requestCode == 200){
            if (resultCode == RESULT_OK){

                if (data != null){
                    postUid.add( 0 , data.getStringExtra("uid"));
                    postList.add( 0 ,data.getStringExtra("content"));

                    ((ArrayAdapter<String>) getListAdapter()).notifyDataSetChanged();
                }
            }
        }
    }
}
