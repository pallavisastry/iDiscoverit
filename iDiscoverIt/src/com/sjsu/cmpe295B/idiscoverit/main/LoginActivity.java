package com.sjsu.cmpe295B.idiscoverit.main;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.NetworkAuthenticator;
import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;
import com.sjsu.cmpe295B.idiscoverit.webHelpers.HttpClientHelper;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	
	private static String TAG="LoginActivity";
	public static final String EXTRA_UN = "Temp";
	
	private UserLoginTask mAuthTask = null;
	
	private String URL_STRING = Constants.loginActivity_URL;
	
	// Values for username and password at the time of the login attempt.
	private String mUsername;
	private String mPassword;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
//	private SharedPreferences preferenceSettings;
	
	SessionManager session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		Log.v(TAG, "**********************************************************");
		Log.v(TAG,"***********iDiscoverIt LoginActivity Started***************");
		
		session = new SessionManager(getApplicationContext()) ;
		
		// Set up the login form.
		mUsername = getIntent().getStringExtra(EXTRA_UN);
		mUsernameView = (EditText) findViewById(R.id.username);
		mUsernameView.setText(mUsername);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}
	private void setNewPassword(){
		//enable setting new password.......!!!!!!!
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
        case R.id.menu_forgot_password:
            setNewPassword();
            return true;
        
        }
        return super.onOptionsItemSelected(item);
    }

	/*Acitivity Lifecycle*/
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		android.os.Debug.stopMethodTracing();
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length()<3) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid username .
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		} else if (mUsername.length()<3) {
			mUsernameView.setError(getString(R.string.error_invalid_username));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	private String authenticateUserOverNetwork(){
		
		Log.v(TAG, "--------Inside authenticateUserOverNetwork() LoginActivity--------");
		String response;
		ArrayList postParameters = new ArrayList();
		String jsonObjFromArrtoStr = null;
		JSONObject jsonObjFromInputStream;
		
		try{
			postParameters.add(new BasicNameValuePair("userName", mUsername));
			postParameters.add(new BasicNameValuePair("password", mPassword));
		
			jsonObjFromInputStream =  (JSONObject) HttpClientHelper.executePost(URL_STRING, postParameters, "json");
			Log.v(TAG, "~~~~~json OBJ keys>>> " + jsonObjFromInputStream.keys() +" name >>"+ jsonObjFromInputStream.names());

			boolean isLoginSuccesful = jsonObjFromInputStream.getBoolean("loginSuccess");
			boolean isRegistrationSuccessful = jsonObjFromInputStream.getBoolean("regSuccess");
			
			String sessionObj = jsonObjFromInputStream.getString("session");
			Log.v(TAG,"~~~~~~~ Session values ~~~~~ "+sessionObj);
			
			JSONObject jo= new JSONObject(sessionObj);
			
			boolean isLogged = jo.getBoolean("loggedin");
			String un = jo.getString("username");
			String uExists = jo.getString("userExists");
			
			//Server-Session in json values: Creating android-session context if its valid!
			if(isLogged == true && un!=null && (!un.equals(""))){ //loggedin=true;username=valid;username!=""
				session.createLoginSession(un);//#android session
				if((isLoginSuccesful==true && isRegistrationSuccessful==true)){  
					return Constants.REGISTRATION_SUCCESS;
				}
				else if((isLoginSuccesful==true && isRegistrationSuccessful ==false)){
					return Constants.LOGIN_SUCCESS;
				}
			}else if(isLogged == false && un.equals("") && uExists.equals("userExists")){
				if(isLoginSuccesful==false || isRegistrationSuccessful==false){
					return Constants.USER_EXISTS;
				}
			}else 
				return Constants.FAILURE;
			
		} catch (JSONException e) {
			Log.e(TAG,
					"$$$$$$ JSON-Exception in authenticateUserOverNetwork in LoginActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		} catch (IOException e) {
			Log.e(TAG,
					"$$$$$$ IO-Exception in 'else-try' block authenticateUserOverNetwork in LoginActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Constants.FAILURE;
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			Log.v(TAG,
					"--------Inside doInBackground() of UserLoginTask---------");

			String authenticatedValue = authenticateUserOverNetwork();
			Log.v(TAG,"##########Info: authenticatedValue in UserLoginTask >> "+ authenticatedValue);

			return authenticatedValue;
		}

		@Override
		protected void onPostExecute(String result) {
			mAuthTask = null;
			showProgress(false);

			if (result.equals(Constants.LOGIN_SUCCESS)) {
				Toast.makeText(getApplicationContext(), "Login Status: "+session.isLoggedIn(), Toast.LENGTH_SHORT).show();
				Intent i = new Intent(LoginActivity.this,HomeActivity.class);//BUILD A XML_HELP page and on done cick-send to HOME!!!!
				startActivity(i);
				finish();
			} else if(result.equals(Constants.REGISTRATION_SUCCESS)){
				Toast.makeText(getApplicationContext(), "Registration/Login Status: "+session.isLoggedIn(), Toast.LENGTH_SHORT ).show();
				Intent i = new Intent(LoginActivity.this,HomeActivity.class);
				startActivity(i);
				finish();
			}else if(result.equals(Constants.USER_EXISTS)){
				Toast.makeText(getApplicationContext(), "Sorry!User name is taken.Choose another or check your credentials!", Toast.LENGTH_LONG ).show();
				startActivity(new Intent(LoginActivity.this, LoginActivity.class));
			}
			else{
				Toast.makeText(getApplicationContext(), "Sorry! Error:Login Status: "+session.isLoggedIn(), Toast.LENGTH_LONG).show();
				startActivity(new Intent(LoginActivity.this, LoginActivity.class));
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
}
