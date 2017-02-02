package com.vogella.datacrawler;

import com.vogella.datacrawler.communication.BugzillaCommunicationController;
import com.vogella.datacrawler.data.handler.ORMDataHandler;

public class StartUp {

	public static void main(String[] args) {
		BugzillaCommunicationController commCtrl = new BugzillaCommunicationController();
		commCtrl.getBugs(new ORMDataHandler());
	}
}
