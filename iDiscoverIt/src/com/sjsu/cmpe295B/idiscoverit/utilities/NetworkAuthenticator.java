package com.sjsu.cmpe295B.idiscoverit.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkAuthenticator{
	
	private static String TAG="NetworkAuthenticator";
	private static ConnectivityManager connectivityManager;
	private static final String NO_CONNECTION="Internet unavailable. Please upload again later!";
	public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    public static String sPref = null;

    

	// private WifiManager wifiManager;
	// private WifiLock wifiLock;
	/*Wifi connection*/
	private static boolean checkWifi() {
		
		boolean wifi = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		if (wifi) {
			Log.v(TAG, "##### Information: Wifi exists: " + wifi + " ######");
			return true;
		} else
			return false;
	}
	/*Mobile Network connection*/
	private static boolean checkMobileNetwork() {
		boolean internetOverMobile = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		if (internetOverMobile) {
			Log.v(TAG, "##### Information: InternetOver Mobiles exists: "
					+ internetOverMobile + " ######");
			return true;
		} else
			return false;
	}

	/* checkConnection() checks if wifi/mobile networks are available */
	public static boolean checkConnection(Context currentContext) {
		connectivityManager = (ConnectivityManager)currentContext.getSystemService(Activity.CONNECTIVITY_SERVICE);

		if (checkWifi() || checkMobileNetwork()) {
			return true;
		} else
			return false;
	}

	/*
	 * noConnectionToast() displays a long-duration popup to user with the
	 * corresponding message
	 */
	public static String noConnectionToast() {

		return NO_CONNECTION;
	}
}
