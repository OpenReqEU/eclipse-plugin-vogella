package com.vogella.datacrawler.data.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class CSVDataHandler implements IDataHandler {

	@Override
	public void handleData(ResponseBody responseBody) {
		try {
			File csvFile = new File("bugzilla_bugs.csv");

			InputStream inputStream = null;
			OutputStream outputStream = null;

			try {
				byte[] fileReader = new byte[4096];

				inputStream = responseBody.byteStream();
				outputStream = new FileOutputStream(csvFile);

				while (true) {
					int read = inputStream.read(fileReader);

					if (read == -1) {
						break;
					}

					outputStream.write(fileReader, 0, read);
				}

				outputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}

				if (outputStream != null) {
					outputStream.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
