package com.vogella.datacrawler.data.handler;

import okhttp3.ResponseBody;

public interface IDataHandler {
	public void handleData(ResponseBody responseBody);
}
