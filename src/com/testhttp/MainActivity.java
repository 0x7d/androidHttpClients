package com.testhttp;

import com.test.httpclients.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity implements OnClickListener {
	public static final String TAG = "xxxxxxxxxx";

	public static final int APACHE_UPDATE_PROGRESS = 1425;
	public static final int GOOGLE_UPDATE_PROGRESS = 1426;
	public static final String PROGRESS = "progress";
	public static final String ELAPSE = "elapse";

	public static final String SOURCE = "source";
	public static final String AUTH = "auth";
	public static final String FILE_URL = "fileurl";

	private Button btGoogle, btApache;
	private TextView tvApacheStatus, tvGoogleStatus;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			int progress = msg.getData().getInt(PROGRESS);
			long elapse = msg.getData().getLong(ELAPSE);
			Log.d(TAG, "progress " + progress + " elapse " + elapse);
			if (what == APACHE_UPDATE_PROGRESS) {
				String text = tvApacheStatus.getText().toString() + "\n";
				text = text + getString(R.string.status, progress, elapse);
				tvApacheStatus.setText(text);

			} else if (what == GOOGLE_UPDATE_PROGRESS) {
				String text = tvGoogleStatus.getText().toString() + "\n";
				text = text + getString(R.string.status, progress, elapse);
				tvGoogleStatus.setText(text);

			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btApache = (Button) findViewById(R.id.bt_apache);
		btApache.setOnClickListener(this);

		btGoogle = (Button) findViewById(R.id.bt_google);
		btGoogle.setOnClickListener(this);

		tvApacheStatus = (TextView) findViewById(R.id.tv_apachestatus);
		tvApacheStatus.setMovementMethod(ScrollingMovementMethod.getInstance());

		tvGoogleStatus = (TextView) findViewById(R.id.tv_googlestatus);
		tvGoogleStatus.setMovementMethod(ScrollingMovementMethod.getInstance());
	}

	public void onStart() {
		super.onStart();
	}

	public void onStop() {
		super.onStop();
		ApacheClient.onStop();
		GoogleClient.onStop();
	}

	public void onClick(View view) {
		if (view.equals(btApache)) {
			Log.d(TAG, "btApache");
			Thread downloader = new Thread(apache);
			downloader.start();

		} else if (view.equals(btGoogle)) {
			Log.d(TAG, "btGoogle");
			Thread downloader = new Thread(google);
			downloader.start();
		}
	}

	private Runnable apache = new Runnable() {
		public void run() {
			String response = ApacheClient.execute(FILE_URL, mHandler);
			System.out.println("response " + response);
		}
	};

	private Runnable google = new Runnable() {
		public void run() {
			String response = GoogleClient.execute(FILE_URL, mHandler);
			System.out.println("response " + response);
		}
	};
}
