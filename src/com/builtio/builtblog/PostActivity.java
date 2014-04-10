package com.builtio.builtblog;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltQuery;
import com.raweng.built.QueryResult;
import com.raweng.built.QueryResultsCallBack;

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
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		postList = new ArrayList<String>();
		postUid  = new ArrayList<String>();

		adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, postList);
		setListAdapter(adapter);

	  /**
	   * If user want set acl on account to make blog private then at first user need to login
	   */
	/*final BuiltUser user = new BuiltUser();
		user.login("YOUR_BUILT_REGISTERD/ACTIVATED_EMAIL_ID","YOUR_PASSWORD", new BuiltResultCallBack() {

			@Override
			public void onSuccess() {
				/// After login successful BuiltUser object provided in success callback.User_Email_ID User_Password

		 		/// Saving user data or session on disc.		
				try {
					user.saveSession();
				} catch (Exception e) {
					e.printStackTrace();
				}

				fetchAllPost_UsingBuiltQuery();
			}

			@Override
			public void onError(BuiltError builtErrorObject) {

				/// there is an error while login.
				/// builtErrorObject contains more details of error.

				Toast.makeText(PostActivity.this, "error :"+builtErrorObject.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAlways() {
				/// write code here that user want to execute.
				/// regardless of success or failure of the operation.
			}
		});*/
		
		
		/*
		 * Fetching all posts
		 */
		fetchAllPost_UsingBuiltQuery();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.post, menu);

		MenuItem item = (MenuItem) menu.findItem(R.id.saveMenu);
		item.setVisible(false);
		MenuItem item1 = (MenuItem) menu.findItem(R.id.cancelMenu);
		item1.setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh: {
			fetchAllPost_UsingBuiltQuery();
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
		startActivity(intent);

	}

	/**
	 * Fetch all BuiltObjects of class.
	 * Using BuiltQuery class.
	 */
	public void fetchAllPost_UsingBuiltQuery(){

		/* Creating the BuiltQuery object with class uid.
		 * Executing query to get BuiltObject. 
		 */
		BuiltQuery builtQuery = new BuiltQuery("blognote"); 
		builtQuery.exec(new QueryResultsCallBack() {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(QueryResult queryResultObject) {

				/// After executing successful QueryResult object provided in success callback.
				
				List<BuiltObject> builtObjectList = queryResultObject.getResultObjects();

				for (int i = 0; i < builtObjectList.size(); i++) {
					
					/*
					 * Extracting data from BuiltUser object.
					 */
					if(!postList.contains(builtObjectList.get(i).getString("blogtext"))){
						postList.add(builtObjectList.get(i).getString("blogtext"));
						postUid.add(builtObjectList.get(i).getUid());
					}
				}

				((ArrayAdapter<String>) getListAdapter()).notifyDataSetChanged();
			}

			@Override
			public void onError(BuiltError error) {
				
				/// builtErrorObject contains more details of error.
				Toast.makeText(PostActivity.this, "error :"+error.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAlways() {
				
				/// write code here that user want to execute.
				/// regardless of success or failure of the operation.
			}
		});

	}

	@Override
	protected void onListItemClick(ListView list, View v, int position, long id) {
		super.onListItemClick(list, v, position, id);
		Intent intent = new Intent(PostActivity.this, BlogSavingActivity.class);
		intent.putExtra("content", list.getItemAtPosition(position).toString());
		intent.putExtra("uid", postUid.get(position).toString());
		startActivity(intent);
	}	

}
