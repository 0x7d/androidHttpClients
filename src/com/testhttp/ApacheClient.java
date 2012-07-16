package com.testhttp;

import java.io.InputStream;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpRequestBase;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

public class ApacheClient {
	public static final String TAG = "ApacheClient";

	public static long startTime, endTime;

	public static HttpRequestBase mRequest;

	public static String execute(String url, Handler handler) {
		startTime = System.currentTimeMillis();

		Log.d(TAG, "[url] " + url);

		mRequest = new HttpGet(url);

		String source = MainActivity.SOURCE;
		String auth = MainActivity.AUTH;
		Log.d(TAG, "[source] " + source + " [auth] " + auth);
		mRequest.addHeader("Source", source);
		mRequest.addHeader("Authorization", auth);
		mRequest.addHeader("Content-Type", "application/*");
		mRequest.addHeader("Range", "bytes=0-207618047");

		Log.d(TAG, "xxxxxxxxxxxxx REQUEST HEADER xxxxxxxxxxxxxxxx");
		Header[] allHeaders = mRequest.getAllHeaders();
		int headerLength = allHeaders.length;
		for (int index = 0; index < headerLength; index++) {
			Header header = allHeaders[index];
			String name = header.getName();
			String value = header.getValue();
			Log.d(TAG, "[name] " + name + " [value] " + value);
		}
		Log.d(TAG, "xxxxxxxxxxxxx REQUEST HEADER xxxxxxxxxxxxxxxx");

		DefaultHttpClient client = ApacheHttpClient.getHttpClient(30000);
		client.log.enableDebug(true);

		HttpResponse response = null;
		try {
			response = client.execute(mRequest);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		Log.d(TAG, "response == null ? " + (response == null));

		int statusCode = response.getStatusLine().getStatusCode();
		Log.d(TAG, "statusCode " + statusCode);

		HttpEntity entity = response.getEntity();
		if (entity == null) {
			return "";
		}

		InputStream inputStream = null;
		try {
			inputStream = entity.getContent();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (inputStream == null) {
			return "";
		}

		try {
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
							.obtainMessage(MainActivity.APACHE_UPDATE_PROGRESS);
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
			return "succeed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	public static void onStop() {
		if (mRequest != null)
			mRequest.abort();
	}

}
