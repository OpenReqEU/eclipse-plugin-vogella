package com.vogella.datacrawler.communication;

import com.vogella.datacrawler.data.handler.IDataHandler;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BugzillaCallback implements Callback<ResponseBody> {

	private IDataHandler dataHandler;

	public BugzillaCallback(IDataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}

	@Override
	public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
		if (response.isSuccessful()) {
			dataHandler.handleData(response.body());
		} else {
			System.out.println("Failure");
		}
	}

	@Override
	public void onFailure(Call<ResponseBody> call, Throwable t) {
		t.printStackTrace();
	}

}
