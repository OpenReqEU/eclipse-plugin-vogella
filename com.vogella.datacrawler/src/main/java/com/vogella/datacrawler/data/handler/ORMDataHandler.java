package com.vogella.datacrawler.data.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.hibernate.Session;

import com.vogella.datacrawler.data.HibernateUtil;
import com.vogella.datacrawler.data.entities.Bug;

import okhttp3.ResponseBody;

public class ORMDataHandler implements IDataHandler {

	String splitArg = ",";
	String line = "";

	@Override
	public void handleData(ResponseBody responseBody) {
		try {
			BufferedReader bufferedReader = null;
			Session session = HibernateUtil.getSessionFactory().openSession();
			try {

				bufferedReader = new BufferedReader(new InputStreamReader(responseBody.byteStream()));
				while ((line = bufferedReader.readLine()) != null) {

					// use comma as separator
					String[] docLine = line.split(splitArg);

					Bug bug = new Bug();
					bug.setId(docLine[0]);
					bug.setDescription(docLine[2]);
					bug.setAssignedTo(docLine[1]);
					bug.setProduct(docLine[3]);
					bug.setComponent(docLine[4]);
					
					session.beginTransaction();
					session.save(bug);
					session.getTransaction().commit();
				}

				return;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
