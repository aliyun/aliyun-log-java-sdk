package com.aliyun.openservices.log.http.client;

import com.aliyun.openservices.log.common.auth.CredentialsProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientConnectionHelper {
	// endpoint -> containers
	private Map<String, ClientConnectionContainer> mAllConnections;

	private ClientConnectionHelper() {
		mAllConnections = new ConcurrentHashMap<String, ClientConnectionContainer>();
		(new Thread(new ClientConnectionUpdateThread(this))).start();
	}

	private static class SingletonHolder {
		private static final ClientConnectionHelper INSTANCE = new ClientConnectionHelper();
	}

	public static ClientConnectionHelper getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public ClientConnectionContainer GetConnectionContainer(String endpoint, CredentialsProvider credentialsProvider) {
		if (!mAllConnections.containsKey(endpoint)) {
			ClientConnectionContainer container = new ClientConnectionContainer(endpoint, credentialsProvider);
			mAllConnections.put(endpoint, container);
			return container;
		} else {
			return mAllConnections.get(endpoint);
		}
	}

	class ClientConnectionUpdateThread implements Runnable {
		ClientConnectionHelper mHelper;

		ClientConnectionUpdateThread(ClientConnectionHelper helper) {
			mHelper = helper;
		}

		@Override
		public void run() {
			while (true) {
				for (Map.Entry<String, ClientConnectionContainer> entry : mHelper.mAllConnections.entrySet()) {
					entry.getValue().UpdateConnections();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

				}
			}
		}
	}
}
