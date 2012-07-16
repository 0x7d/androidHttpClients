package com.testhttp;

import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GoogleClient {
	public static final String TAG = "GoogleClient";

	public static long startTime, endTime;

	private static HttpsURLConnection mConn;

	public static String execute(String urlStr, Handler handler) {
		startTime = System.currentTimeMillis();

		Log.d(TAG, "[url] " + urlStr);

		URL url;
		try {
			url = new URL(urlStr);
			trustAllHosts();
			mConn = (HttpsURLConnection) url.openConnection();

			mConn.setRequestMethod("GET");
			mConn.setDoOutput(true);
			mConn.setHostnameVerifier(DO_NOT_VERIFY);

			String source = MainActivity.SOURCE;
			String auth = MainActivity.AUTH;
			Log.d(TAG, "[source] " + source + " [auth] " + auth);
			mConn.setRequestProperty("Source", source);
			mConn.setRequestProperty("Authorization", auth);
			mConn.setRequestProperty("Content-Type", "application/*");
			mConn.setRequestProperty("Range", "bytes=0-207618047");

			InputStream inputStream = mConn.getInputStream();

			int length = 0;
			long total = 0;
			int percent = -1;
			long fileLength = 207618047;
			long startTime = System.currentTimeMillis();
			byte[] buffer = new byte[512 * 1024];
			while ((length = inputStream.read(buffer)) != -1) {
				Log.d(TAG, "length " + length);
				total = total + length;
				int curPer = (int) ((100 * total) / fileLength);
				long elapse = System.currentTimeMillis() - startTime;
				if (percent != curPer) {
					percent = curPer;
					Log.d(TAG, "total bytes " + total);
					Log.d(TAG, "current percent " + percent);
					Log.d(TAG, "time elapse " + elapse);
					Message msg = handler
							.obtainMessage(MainActivity.GOOGLE_UPDATE_PROGRESS);
					Bundle data = new Bundle();
					data.putInt(MainActivity.PROGRESS, curPer);
					data.putLong(MainActivity.ELAPSE, elapse);
					msg.setData(data);
					handler.sendMessage(msg);
				}
			}

			long endTime = System.currentTimeMillis();
			Log.d(TAG, "Total time elapse " + (endTime - startTime));
			inputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "succeed";
	}

	public static void onStop() {
		if (mConn != null)
			mConn.disconnect();
	}

	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	private static void trustAllHosts() {
		X509TrustManager easyTrustManager = new X509TrustManager() {

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
				// Oh, I am easy!
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
				// Oh, I am easy!
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

		};

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { easyTrustManager };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");

			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
