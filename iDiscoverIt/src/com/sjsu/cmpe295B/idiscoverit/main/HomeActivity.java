//Referred:http://stackoverflow.com/questions/2864840/treemap-sort-by-value
package com.sjsu.cmpe295B.idiscoverit.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;
import com.sjsu.cmpe295B.idiscoverit.webHelpers.HttpClientHelper;
import com.sjsu.cmpe295B.idiscoverit.webHelpers.StreamAudiotronsActivity;

/**
 * Home Activity is the first screen
 * activity a user is taken to on logging
 * into the application successfully.
 * This screen displays top rated audiotrons
 * which are clickable.
 */
public class HomeActivity extends Activity {
    private static String TAG = "HomeActivity";

    public static final String PREFS_NAME = "LoginPrefs";
    private ImageButton homeButton;
    private ImageButton recorderButton;
    private ImageButton categoriesButton;
    private ImageButton profileButton;

    private TreeMap<String, Long> ratedTronMap;
    private SortedSet<Entry<String, Long>> sortedSet;
    
    private String URL_STRING;
    SessionManager session;

    private GridViewAdapter gridViewAdapter;
    private GridView gridView;

    private BroadcastReceiver homeActivityReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        //#Setting a dynamic broadcast receiver to logout completely.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
        homeActivityReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG,"||||| Logout in progress ||||||");
                //Starting login activity
                Intent i = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        };
        registerReceiver(homeActivityReceiver,intentFilter);

        session = new SessionManager(getApplicationContext());

        /**Setting global url preferences to be used*/
        SharedPreferences urlPreferenceSettings = getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = urlPreferenceSettings.edit();

        prefsEditor.putString("loginActivity_URL", Constants.loginActivity_URL);
        prefsEditor.putString("uploadAudiotronActivity_URL", Constants.uploadAudiotronActivity_URL);
        prefsEditor.putString("categoryListDisplayActivity_URL", Constants.categoryListDisplayActivity_URL);
        prefsEditor.putString("audiotronsInCategoryDisplayActivity_URL", Constants.audiotronsInCategoryDisplayActivity_URL);
        prefsEditor.putString("streamAudiotronsActivity_URL", Constants.streamAudiotronsActivity_URL);
        prefsEditor.putString("feedback_URL", Constants.feedback_URL);
        prefsEditor.putString("homeActivity_URL", Constants.homeActivity_URL);
        prefsEditor.putString("myAudiotrons_URL",Constants.myAudiotrons_URL);
        prefsEditor.putString("favorites_URL", Constants.favorites_URL);
        prefsEditor.putString("myFavoriteAudiotrons_URL", Constants.myFavoriteAudiotrons_URL);
        prefsEditor.putString("addTag_URL_STRING", Constants.addTag_URL_STRING);
        prefsEditor.putString("newAudiotronsInCategoryDisplayActivity_URL", Constants.newAudiotronsInCategoryDisplayActivity_URL);
        prefsEditor.putString("search_URL", Constants.search_URL);
        boolean isPrefsCommitted = prefsEditor.commit();

        Log.v(TAG,"----Are preferences committed in LoginActivity >> "+isPrefsCommitted);

        Toast.makeText(getApplicationContext(), "User Login status: "+session.isLoggedIn(), Toast.LENGTH_SHORT).show();
        session.checkLogin(); //This call will return user back to login activity if user is logged out.
        
        String isSkipMessageChecked = urlPreferenceSettings.getString("skipMessage", "unchecked");
        Log.v(TAG,"#####is skip message checked??? :"+isSkipMessageChecked);
        if(!(isSkipMessageChecked.equals("checked"))){
        	Intent in = new Intent(HomeActivity.this,HelpActivity.class);
        	startActivity(in);
        }else if(isSkipMessageChecked.equals("unchecked")){
        	Intent in = new Intent(HomeActivity.this,HelpActivity.class);
        	startActivity(in);
        }
        
        setupViews();

    }

    private void setupViews() {

        homeButton = (ImageButton) findViewById(R.id.home);
        recorderButton = (ImageButton) findViewById(R.id.recorder);
        categoriesButton = (ImageButton) findViewById(R.id.categories);      //categoriesButton.setBackgroundColor(Color.GRAY);
        profileButton = (ImageButton) findViewById(R.id.my_profile);

        Log.v(TAG,"************* iDiscoverit APPLICATION STARTED(HOME ACTIVITY) **************");
        Log.v(TAG,"========> Calling getHallOfFameAudiotrons <========= ");

        buildHallOfFameAudiotronsList();

        /*
         * homeButton brings home screen to front every-time its clicked.
         */
        homeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.v(TAG, "-------HOME VIEW ENTRY---------");
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
            }
        });

        recorderButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.v(TAG, "-------RECORDER VIEW ENTRY---------");
                Log.v(TAG," ####Information: Launching new upload - Child Thread ########");

                new Thread(new Runnable() {
                    public void run() {
                        Log.v(TAG," -------Information: Inside run() of recorderButton-HomeActivity-------");
                        Intent i = new Intent(HomeActivity.this,VoiceRecordingActivity.class);
                        startActivity(i);
                    }
                }).start();
            }

        });

        categoriesButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.v(TAG, "-------CATEGORIES VIEW ENTRY---------");
                Log.v(TAG," ####Information: Launching new upload - Child Thread ########");

                new Thread(new Runnable() {
                    public void run() {
                        Log.v(TAG," -------Information: Inside run() of categoriesButton-HomeActivity-------");
                        Intent i = new Intent(HomeActivity.this,CategoryListDisplayActivity.class);
                        startActivity(i);
                    }
                }).start();
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.v(TAG, "-------PROFILE VIEW ENTRY---------");
                Log.v(TAG," ####Information: Launching new upload - Child Thread ########");

                new Thread(new Runnable() {
                    public void run() {
                        Log.v(TAG," -------Information: Inside run() of profileButton-HomeActivity-------");

                        Intent i = new Intent(HomeActivity.this,UserDetailsActivity.class);
                        startActivity(i);
                    }
                }).start();
            }
        });
    }
    /**
     * displayHallOfFameAudiotrons() uses grid adapter
     * inner class to display the audiotrons as images.
     * This method then gives control to streaming audio
     * class if a user clicks on an audio-image.
     */
    private void displayHallOfFameAudiotrons(){
        Log.v(TAG, "-------Inside displayHallOfFameAudiotrons---------- ");

        //USING ADAPTER HERE.....
        Log.v(TAG,"------Starting adapter view-------");
        //gridViewAdapter = new GridViewAdapter(this, audiotronArrList,ratedTronMap,sortedSet);
        gridViewAdapter = new GridViewAdapter(this,sortedSet);
        gridView = (GridView) findViewById(R.id.home_grid_view);
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,    long arg3) {
                Toast.makeText(HomeActivity.this, "Item clicked >> "+gridViewAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                //IF USER CLICKS ON AN IMAGE, CONTROL GOES TO STREAMING ACTIVITY
                Log.v(TAG,"=======>Calling StreamAudiotronActivity from Home(Hall-of-fame) Screen=========>");
                Intent i = new Intent(HomeActivity.this,StreamAudiotronsActivity.class)
                .putExtra("audiotronName",(String)gridViewAdapter.getItem(position));
                startActivity(i);
            }

        });
    }
    private void buildHallOfFameAudiotronsList(){

        Log.v(TAG, "-------Inside buildHallOfFameAudiotronsList---------- ");

        SharedPreferences urlPrefs = this.getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
        URL_STRING = urlPrefs.getString("homeActivity_URL", Constants.homeActivity_URL);
        Log.d(TAG,"---------Preferred URL is ----->"+URL_STRING);

        Log.v(TAG,"=====>Calling download audio names task <=======");
        DownloadTopRatedTronsTask task = new DownloadTopRatedTronsTask();
        task.execute();//audiotronArrList);
    }
    /**
     * getHallOfFameTronsFromDB() creates a http connection
     * and retrieves all top rated audios into a list
     * @return
     */
    private SortedSet<Entry<String,Long>> getHallOfFameTronsFromDB(){
        Log.v(TAG, "-------Inside getHallOfFameTrons---------- ");

        ArrayList postParameters = new ArrayList();
        ratedTronMap=new TreeMap<String, Long>();
        String jsonResponseMsg;
        JSONObject ratedJsonObj;//,userJsonObj;

        try {

            JSONObject jsonObjFromInputStream = (JSONObject) HttpClientHelper.executePost(URL_STRING, postParameters, "json");
            Log.v(TAG, "####### Information: Audiotrons:: key value pair>>>"+ jsonObjFromInputStream.names());

            jsonResponseMsg = jsonObjFromInputStream.getString("customActionMsg");
            if(jsonResponseMsg.equals("famedtronsRetrievalSuccess")){
            	Log.v(TAG, "####### Information: Ratingmap of audiotrons >>>"+ jsonObjFromInputStream.get("ratingMap"));
            	ratedJsonObj=(JSONObject) jsonObjFromInputStream.get("ratingMap");
            	//userJsonObj =(JSONObject) jsonObjFromInputStream.get("userMap");

            	Iterator<String> ratedTronsItr =ratedJsonObj.keys();
            	while(ratedTronsItr.hasNext()){
            		String ratedAtron =ratedTronsItr.next();
            		ratedTronMap.put(ratedAtron, ratedJsonObj.getLong(ratedAtron));//#both tron name and rating
            	}
            	sortedSet=entriesSortedByValues(ratedTronMap);
            	Log.v(TAG,"###**** Sorted rated map >>> "+entriesSortedByValues(ratedTronMap));
            	
//            	Iterator<String> userTronItr=userJsonObj.keys();
//            	while(userTronItr.hasNext()){
//            		String aTron = userTronItr.next();
//            		userTronMap.put(aTron, ratedJsonObj.getString(aTron)); //#both tron name and user obj
//            	}
            	
            }else if(jsonResponseMsg.equals("famedtronsRetrievalError")){
                Log.v(TAG,"#######Information: There are no audiotrons in the list; empty list!!");
                sortedSet=new TreeSet<Map.Entry<String,Long>>(); //#empty object
            }

        } catch (JSONException e) {
            Log.e(TAG,"$$$$$$ JSON-Exception in getHallOfFameTrons in HomeActivity...... "
                            + e.getMessage() + "$$$$$$$$");
        } catch (IOException e) {
            Log.e(TAG,"$$$$$$ IO-Exception in 'try' block getHallOfFameTrons in HomeActivity...... "
                            + e.getMessage() + "$$$$$$$$");
        } catch (Exception e) {
            Log.e(TAG,"$$$$$$ Exception in try block getHallOfFameTrons in HomeActivity...... "
                            + e.getMessage() + "$$$$$$$$");
        }
        return sortedSet;
    }
    /**
     * This inner class will download best rated audiotron-
     * names to be displayed in home screen
     * @author pallavi
     *
     */
    private class DownloadTopRatedTronsTask extends AsyncTask<Void, Toast, SortedSet<Entry<String,Long>>>{

        @Override
        protected SortedSet<Entry<String,Long>> doInBackground(Void... param) {

        	SortedSet<Entry<String, Long>> tempAudiotronsHolderSet = getHallOfFameTronsFromDB();
        	if(tempAudiotronsHolderSet!=null && tempAudiotronsHolderSet.size()>0)
        		Log.v(TAG,"####Information>>>>> resulting length in array of doInBgnd() >> '"+ tempAudiotronsHolderSet.size());
            return tempAudiotronsHolderSet;

        }
        @Override
        protected void onPostExecute(SortedSet<Entry<String,Long>> result) {//ArrayList<String> result

            Log.v(TAG, "------Inside onPostExecute(..)---------");

            if (result!=null && result.size() > 0) {
            	Log.v(TAG, "-----Completed Task--------");
                displayHallOfFameAudiotrons();
            }else{
                Toast.makeText(getApplicationContext(), "No audiotron to display in home screen: "+result.size(), Toast.LENGTH_LONG).show();
            }
        }
		
    }
    /**
     * Adapter class for grid view display
     * in Home Activity-Main Screen
     * @author pallavi
     *
     */
    private class GridViewAdapter extends BaseAdapter{

        ArrayList<String> audiotronNamesArrayList;
        Activity activity;
        SortedSet<Entry<String,Long>> fullSortedSet;
        public GridViewAdapter(Activity activity,SortedSet<Entry<String,Long>> sset){
            super();
            Log.v(TAG,"--------- Inside GridViewAdapter Constructor");
            audiotronNamesArrayList=new ArrayList<String>();
            
            this.activity=activity;
            this.fullSortedSet=sset;
            
            //#Creating arraylist of values from sorted set
            for(Entry<String,Long> vp:fullSortedSet){
            	audiotronNamesArrayList.add(vp.getKey());
            }
        }

        public int getCount() {
        	return audiotronNamesArrayList.size();
        }

        public Object getItem(int position) {
        	
            return audiotronNamesArrayList.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder view;
            LayoutInflater inflater = activity.getLayoutInflater();

            if(convertView == null){
                view = new ViewHolder();
                convertView = inflater.inflate(R.layout.home_gridview,null);
                view.txtViewTronName = (TextView) convertView.findViewById(R.id.home_textview_gridview);
                view.imgViewTron=(ImageView) convertView.findViewById(R.id.home_grid_imgview);

                view.rbar=(RatingBar) convertView.findViewById(R.id.home_rating_bar);
                view.rbar.setStepSize(0.5F);
                
                RatingBar.OnRatingBarChangeListener l =new OnRatingBarChangeListener() {
					
					public void onRatingChanged(RatingBar ratingBar, float rating,
							boolean fromUser) {
						Integer mypost = (Integer) ratingBar.getTag(); //#gets the position of this row
						String holderValue = (String) gridViewAdapter.getItem(mypost); //#gets the item at this row
						
						//////#new
						for(Entry<String,Long> vp:fullSortedSet){
							String key=vp.getKey();
							String val = Long.toString(vp.getValue());
							if(key.equals(holderValue)){
								System.out.println("Keys are equal: value = "+val);
								ratingBar.setRating(Float.valueOf(val)); //#Sets the float value to the row
							}
						}
						//////////

//						longValue = Long.toString(rateMap.get(holderValue)); //#gets the long value associated with the item @this row from map
//						Log.v(TAG,"^^^^^^^^^^Long holder value = "+longValue);
//						ratingBar.setRating(Float.valueOf(longValue)); //#Sets the float value to the row
					}
				};
				view.rbar.setOnRatingBarChangeListener(l);
                
                convertView.setTag(view);
            }else{
                view = (ViewHolder) convertView.getTag();
            }
            view.txtViewTronName.setText(audiotronNamesArrayList.get(position));
            view.imgViewTron.setImageResource(R.drawable.audio_icon);
            view.rbar.setTag(Integer.valueOf(position));
            view.rbar.setRating(1.0F);
            return convertView;
        }
    }
    /**
     * ViewHolder class for GridView
     * @author pallavi
     *
     */
    static class ViewHolder{
        ImageView imgViewTron;
        TextView txtViewTronName;
        RatingBar rbar;
    }
    //#This will sort the treemap based on rating equality from the map
    static <String,Long extends Comparable<? super Long>> SortedSet<Map.Entry<String, Long>> entriesSortedByValues(Map<String,Long> map){
    	SortedSet<Map.Entry<String,Long>> sortedEntries= new TreeSet<Map.Entry<String,Long>>(new Comparator<Map.Entry<String,Long>>() {
    				public int compare(Map.Entry<String, Long> e1,Map.Entry<String, Long>e2) {
    					int res= e2.getValue().compareTo(e1.getValue()); //#CHECK THIS !!!!!!!!!
    				//	Log.v(TAG,"Result of compare of "+e1.getValue()+" and "+e2.getValue()+" = "+res);
    					return res != 0 ? res : 1;
    				}
				});
    	sortedEntries.addAll(map.entrySet());
    	Log.v(TAG,"Sorted enrty >>> "+sortedEntries);
    	return sortedEntries;
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
        case R.id.menu_search:
            onSearchRequested();
            return true;
        case R.id.menu_help:
        	displayHelpAlert();
        	return true;
        case R.id.logout:
            android.content.SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            android.content.SharedPreferences.Editor editor = settings.edit();
            editor.remove("logged");
            editor.commit();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void displayHelpAlert(){
    	Intent in = new Intent(HomeActivity.this,HelpActivity.class);
    	startActivity(in);
    }

    /**-----Acitivty Lifecycle methods--------**/
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"---onResume->Registering receiver----");
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
        registerReceiver(homeActivityReceiver,iFilter);
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"---onPause->UN-Registering receiver-----");
        unregisterReceiver(homeActivityReceiver);
    }
    //# new: This destroys the entire activity and its subs
    @Override
    public void onDestroy(){
        super.onDestroy();

        android.os.Debug.stopMethodTracing();
    }
}