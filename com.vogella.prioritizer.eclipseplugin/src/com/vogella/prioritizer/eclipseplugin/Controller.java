package com.vogella.prioritizer.eclipseplugin;

import java.util.List;

import com.vogella.prioritizer.eclipseplugin.communication.CommunicationController;
import com.vogella.spring.data.entities.Model;
import com.vogella.spring.data.entities.RankedBug;
import com.vogella.spring.data.entities.UserAccount;

import io.reactivex.observers.DisposableSingleObserver;

public class Controller {

	CommunicationController communicationController;

	public Controller() {
		communicationController = CommunicationController.getInstance();	
	}

	public interface IUpdateView {
		void updateView(List<RankedBug> bugs);

		void setError();
	}

	public void generateModel(String email) {
		UserAccount userAccount = new UserAccount();
		userAccount.setEmail(email);
		communicationController.postUserAccount(userAccount, new DisposableSingleObserver<Model>() {
			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}

			@Override
			public void onSuccess(Model model) {
				// TODO save model
			}
		});
	}

	public void getIssuesForCurrentUser(IUpdateView view) {
		communicationController.getIssues(new DisposableSingleObserver<List<RankedBug>>() {
			@Override
			public void onError(Throwable arg0) {
				view.setError();
			}

			@Override
			public void onSuccess(List<RankedBug> bugs) {
				view.updateView(bugs);
			}
		});
	}

}
