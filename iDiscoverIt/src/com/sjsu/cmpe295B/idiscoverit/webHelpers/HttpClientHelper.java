package com.sjsu.cmpe295B.idiscoverit.webHelpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import android.util.Log;
import com.sjsu.cmpe295B.idiscoverit.utilities.MiscellaneousUtilities;

public class HttpClientHelper {

	private static final String TAG = "HttpClientHelper";
	public static final int HTTP_TIMEOUT = 30 * 1000;
	private static final String USER_AGENT_MOBILE = "Mobile";
	private static final String RESPONSE_TYPE_JSON = "json";
	private static HttpClient mHttpClient;
	private static HttpPost postRequest;
	private static HttpResponse response;
	private static InputStream inputStream;

	private static HttpClient getHttpClient() {
		Log.v(TAG, "---------Inside getHttpClient()--------");
		if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();
			// final HttpParams params = mHttpClient.getParams();
			// HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
			// HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
			// ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
		}
		return mHttpClient;
	}

	public static Object executePost(String url, ArrayList postParameters, String responseType) throws Exception {
		Log.v(TAG, "---------Inside executePost()--------");
		try {
			System.setProperty("http.keepAlive", "false");

			HttpClient httpClient = getHttpClient();
			postRequest = new HttpPost(url);
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
			postRequest.setEntity(formEntity);
			httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT_MOBILE);
			System.out.println("POST REQUEST : " + postRequest);
			response = httpClient.execute(postRequest);
			inputStream = response.getEntity().getContent();

			if (responseType.equals(RESPONSE_TYPE_JSON)) {
				return MiscellaneousUtilities.inputStreamAsJSONObj(inputStream);
			}
			return MiscellaneousUtilities.inputStreamAsString(inputStream);
			
		} finally {
			if (inputStream != null) {
				Log.v(TAG,	"~~~~~~~~~~~~ Inside finally of executePost~~~~~~~~~");

				try {
					inputStream.close();
				} catch (IOException e) {
					Log.e(TAG, "$$$$IO EXception closing inputstream in executePost$$$$$" + e);
				}
			}
		}
	}
}
