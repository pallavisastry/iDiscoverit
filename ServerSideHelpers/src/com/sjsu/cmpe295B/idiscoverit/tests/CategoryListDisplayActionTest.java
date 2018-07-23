package com.sjsu.cmpe295B.idiscoverit.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

public class CategoryListDisplayActionTest {
	private static final String URL_STRING = "http://localhost:8080/ServerSideHelpers/categoryListDisplayAction";
	HttpClient httpClient;
	HttpPost postRequest;

	ResponseHandler<String> responseHandler;
	//private String testFileName = "audiotron2";

	HttpURLConnection connection = null;
	InputStream inStream = null;
	String strResponse = null;

	@Test
	public void testExecute() {
		System.setProperty("http.keepAlive", "false");

		MultipartEntity reqEntity;
		try {
			httpClient = new DefaultHttpClient();
			responseHandler = new BasicResponseHandler();

			postRequest = new HttpPost(URL_STRING);

			reqEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
		
			postRequest.setEntity(reqEntity);

			HttpResponse httpResponse = httpClient.execute(postRequest);
			StatusLine sl = httpResponse.getStatusLine();
			int sc = sl.getStatusCode();
			System.out.println("SC in test >>>> " + sc);
			
			inStream = httpResponse.getEntity().getContent();
			inputStreamAsJSONObj(inStream);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void inputStreamAsJSONObj(InputStream stream)
			throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}

		br.close();
		System.out.println("<<<<TEST>>>>>" + sb.toString());
		
		// JSONObject jsonObj = null;
		// try {
		// jsonObj = new JSONObject(sb.toString());
		// System.out.println("json obj in test>>> "+jsonObj);
		//
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

}
