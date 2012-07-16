package com.testhttp;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class GoogleHttpClient {
	public static void getHttpClient(int timeout) {

	}

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
