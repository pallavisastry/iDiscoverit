package com.sjsu.cmpe295B.idiscoverit.main;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;
import com.sjsu.cmpe295B.idiscoverit.webHelpers.HttpClientHelper;

public class CategoryListDisplayActivity extends Activity {

	private static final String TAG = "CategoryListDisplayActivity";
	
	private static String URL_STRING;
	private ArrayList<String> catArrList;

	private ListView categoryListView;
	private CategoryListArrayAdapter categoryListadapter;
	SessionManager session;
	
	private BroadcastReceiver categoryListActivityReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_list_display);
		
		Log.v(TAG, "**********************************************************");
		Log.v(TAG,"***********CategoryListDisplayActivity Activity Started***************");

		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		
		// #Setting a dynamic broadcast receiver to logout completely.
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		categoryListActivityReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "||||| Logout in progress ||||||");
				// Starting login activity
				Intent i = new Intent(CategoryListDisplayActivity.this,LoginActivity.class);
				startActivity(i);
				finish();
			}
		};
		
		//#This preference is available only after session is appropriately set by checking session.checkLogin()
		SharedPreferences urlPrefs = this.getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
		URL_STRING = urlPrefs.getString("categoryListDisplayActivity_URL",Constants.categoryListDisplayActivity_URL);
		Log.d(TAG,"---------Preferred URL is ----->"+URL_STRING);
		
		categoryListView = (ListView) this.findViewById(R.id.list);
		catArrList = new ArrayList<String>();

		DownloadCategoryListTask task = new DownloadCategoryListTask();
		task.execute(catArrList);
	}

	private void display() {
		Log.v(TAG,"---------Inside display() of CategoryListActivity-------");
		
		categoryListadapter = new CategoryListArrayAdapter(CategoryListDisplayActivity.this);
		categoryListView.setAdapter(categoryListadapter);

		categoryListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView parent, View view,int position, long id) {

				TextView tv = (TextView) view.findViewById(R.id.catagory_text);
				final String text = tv.getText().toString();

				//Toast.makeText(getApplicationContext(),	"Clicked list item " + position + " text:" + text,Toast.LENGTH_SHORT).show();

				//Intent i = new Intent(CategoryListDisplayActivity.this,AudiotronsInCategoryDisplayActivity.class).putExtra("categoryName", text);
				Intent i = new Intent(CategoryListDisplayActivity.this,AudiotronsInCategoryTab.class).putExtra("categoryName", text);
				startActivity(i);
			}

		});

	}

	private ArrayList<String> getCategoryListToDisplay() {
		
		JSONObject jsonObjFromInputStream;
		JSONArray jsonArraryFromIS;
		ArrayList postParameters = new ArrayList();

		try {
			Log.v(TAG, "-------Inside getCategoryListToDisplay---------- ");

			jsonObjFromInputStream = (JSONObject) HttpClientHelper.executePost(URL_STRING, postParameters, "json");
			Log.v(TAG, "~~~~~json OBJ>>>  vals>> "+jsonObjFromInputStream.names());
			
			String jsonResponseMsg = jsonObjFromInputStream.getString("customActionMsg");
			if(jsonResponseMsg.equals("categoryRetrievalSuccess")){
				jsonArraryFromIS = (JSONArray) jsonObjFromInputStream.get("categoryListFromDB");

				for(int i=0;i<jsonArraryFromIS.length();i++)
					catArrList.add(jsonArraryFromIS.getString(i));
				Log.v(TAG,"#####Information: ARRRRaaayyyylist>>> "+catArrList);
			
			}else if(jsonResponseMsg.equals("categoryRetrievalError")){
				Log.v(TAG,"#######Information: There are no cateogories in the list#########");
			}

		} catch (JSONException e) {
			Log.e(TAG,
					"$$$$$$ JSON-Exception in getCategoryListToDisplay in CatActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		} catch (IOException e) {
			Log.e(TAG,
					"$$$$$$ IO-Exception in 'else-try' block getCategoryListToDisplay in CatActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		} catch (Exception e) {
			Log.e(TAG,"$$$$$$ EXCEPTION in getCategoryListToDisplay $$$$$$$"+e.getMessage());
		}catch(OutOfMemoryError e){
			Log.e(TAG,"$$$$@@@@%%%%%%% outta mem error!!!!! $$$$@@@@%%%%%%% "+e);
		}
		finally{
			Log.v(TAG,"----Nullyfying all objects in getCatLisToDispl-------");
			jsonObjFromInputStream = null;
			jsonArraryFromIS = null;
			postParameters = null;
		}
		return catArrList;
	}
	private class DownloadCategoryListTask extends
			AsyncTask<ArrayList<String>, Toast, ArrayList<String>> {

		/**
		 * doInBackground(..) is the follow-up of execute method run on a new
		 * instance of this class. This method calls the private method
		 * getCategoryListToDisplay() of the parent class which deals with
		 * network operations and gets the appropriate value which is then sent
		 * to onPostExecute(..).
		 */
		@Override
		protected ArrayList<String> doInBackground(ArrayList<String>... tempCategoryHolderArr) {
			Log.v(TAG,"------Inside doInBgnd(..) of DownloadCategoryListTask---------");

			tempCategoryHolderArr[0] = getCategoryListToDisplay();
			Log.v(TAG,"####Information>>>>>> resulting length in array of doInBgnd() >> '"+ tempCategoryHolderArr[0].size());
			return (ArrayList<String>) tempCategoryHolderArr[0];
		}

		/**
		 * onPostExecute(..) takes param from doInBackground(..). In this
		 * method, an array adapter is being created in the main UI thread once
		 * doInBackground(..) is completed. A toast message is currently being
		 * displayed that identifies the item being clicked.
		 */
		@Override
		protected void onPostExecute(ArrayList<String> result) {

			Log.v(TAG, "------Inside onPostExecute(..)---------");
			
			if(result.size()>0){
				CategoryListDisplayActivity.this.display();
			}
			else{
				//Popup this-maybe
				Toast.makeText(getApplicationContext(),	"Oopps.Sorryyy..Nothing to display now.",Toast.LENGTH_LONG).show();
			}
		}
	}
	/**
	 * An adapter inner class to 
	 * display listview using 
	 * view holder pattern
	 * @author pallavi
	 *
	 */
	private class CategoryListArrayAdapter extends BaseAdapter {
		LayoutInflater vi;

		public CategoryListArrayAdapter(Activity context) {

			Log.v(TAG, "------Inside adapter constructor-------");
			vi = (LayoutInflater) getSystemService(context.LAYOUT_INFLATER_SERVICE);
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.v(TAG, "------Inside getView()-------");

			ViewHolder holder;
			View v = convertView;

			if (v == null) {

				v = vi.inflate(R.layout.activity_category_list_display, null);

				holder = new ViewHolder();
				holder.catListView = (ListView) v.findViewById(R.id.list);
				holder.txv = (TextView) v.findViewById(R.id.catagory_text);

				holder.txv.setText(catArrList.get(position));

				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			return v;
		}
		public int getCount() {
			return catArrList.size();
		}
		public Object getItem(int arg0) {
			return catArrList.get(arg0);
		}
		public long getItemId(int arg0) {
			return catArrList.indexOf(arg0);
		}

	}
	class ViewHolder {
		ListView catListView;
		TextView txv;
	}

	/**-----Acitivty Lifecycle methods--------**/
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG,"---onResume->Registering receiver----");
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		registerReceiver(categoryListActivityReceiver,iFilter);
	}
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG,"---onPause()->UN-Registering receiver-----");
		unregisterReceiver(categoryListActivityReceiver);
	}
	// #new
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	// #new
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_categories_display, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_home:
				startActivity(new Intent(CategoryListDisplayActivity.this, HomeActivity.class));
				return true;
			case R.id.menu_help:
				//add help xml process
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

