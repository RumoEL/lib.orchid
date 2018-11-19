package com.subgraph.orchid.connections;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.subgraph.orchid.TorException;

public class ConnectionSocketFactory {
	static final String[] V1_CIPHERS_ONLY = {
		"TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
	    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
	    "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
	};
	
	private static final String[] MANDATORY_CIPHERS = {
	    //		"TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
	    //"TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
	    //"SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
	    //"SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA"
		    "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
	    "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
	    "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
	    "TLS_RSA_WITH_AES_256_GCM_SHA384",
	    "TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384",
	    "TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384",
	    "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
	    "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384",
	    "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
	    "TLS_RSA_WITH_AES_128_GCM_SHA256",
	    "TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256",
	    "TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256",
	    "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
	    "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",
	    "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
	    "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
	    "TLS_RSA_WITH_AES_256_CBC_SHA256",
	    "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384",
	    "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384",
	    "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
	    "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
	    "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
	    "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
	    "TLS_RSA_WITH_AES_256_CBC_SHA",
	    "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA",
	    "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA",
	    "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
	    "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
	    "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
	    "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
	    "TLS_RSA_WITH_AES_128_CBC_SHA256",
	    "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256",
	    "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256",
	    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
	    "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",
	    "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
	    "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
	    "TLS_RSA_WITH_AES_128_CBC_SHA",
	    "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA",
	    "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA",
	    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
	    "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
	    "TLS_EMPTY_RENEGOTIATION_INFO_SCSV",
	    "TLS_DH_anon_WITH_AES_256_GCM_SHA384",
	    "TLS_DH_anon_WITH_AES_128_GCM_SHA256",
	    "TLS_DH_anon_WITH_AES_256_CBC_SHA256",
	    "TLS_ECDH_anon_WITH_AES_256_CBC_SHA",
	    "TLS_DH_anon_WITH_AES_256_CBC_SHA",
	    "TLS_DH_anon_WITH_AES_128_CBC_SHA256",
	    "TLS_ECDH_anon_WITH_AES_128_CBC_SHA",
	    "TLS_DH_anon_WITH_AES_128_CBC_SHA",
	    "SSL_RSA_WITH_DES_CBC_SHA",
	    "SSL_DHE_RSA_WITH_DES_CBC_SHA",
	    "SSL_DHE_DSS_WITH_DES_CBC_SHA",
	    "SSL_DH_anon_WITH_DES_CBC_SHA",
	    "TLS_RSA_WITH_NULL_SHA256",
	    "TLS_ECDHE_ECDSA_WITH_NULL_SHA",
	    "TLS_ECDHE_RSA_WITH_NULL_SHA",
	    "SSL_RSA_WITH_NULL_SHA",
	    "TLS_ECDH_ECDSA_WITH_NULL_SHA",
	    "TLS_ECDH_RSA_WITH_NULL_SHA",
	    "TLS_ECDH_anon_WITH_NULL_SHA",
	    "SSL_RSA_WITH_NULL_MD5",
	    "TLS_KRB5_WITH_DES_CBC_SHA",
	    "TLS_KRB5_WITH_DES_CBC_MD5",
	};

	private static final TrustManager[] NULL_TRUST = {
		new X509TrustManager() {
			private final X509Certificate[] empty = {};
			public void checkClientTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return empty;
			}
		}
	};
	
	private static SSLContext createSSLContext() {
		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
		try {
			final SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, NULL_TRUST, null);
			return sslContext;
		} catch (NoSuchAlgorithmException e) {
			throw new TorException(e);
		} catch (KeyManagementException e) {
			throw new TorException(e);
		}
	}
	
	private final SSLSocketFactory socketFactory;
	
	ConnectionSocketFactory() {
		socketFactory = createSSLContext().getSocketFactory();
	}
	
	SSLSocket createSocket() {
		try {
			final SSLSocket socket = (SSLSocket) socketFactory.createSocket();
			socket.setEnabledCipherSuites(MANDATORY_CIPHERS);
			socket.setUseClientMode(true);
			return socket;
		} catch (IOException e) {
			throw new TorException(e);
		}
	}
}
