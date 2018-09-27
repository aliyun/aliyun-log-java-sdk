package com.aliyun.openservices.log.http.client;

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

	public static final ClientConnectionHelper getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public ClientConnectionContainer GetConnectionContainer(String endpoint, String accessId, String accessKey) {
		String key = endpoint + "#" + accessId;
		if (mAllConnections.containsKey(key) == false) {
			ClientConnectionContainer container = new ClientConnectionContainer();
			container.Init(endpoint, accessId, accessKey);
			mAllConnections.put(key, container);
			return container;
		} else {
			return mAllConnections.get(key);
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
