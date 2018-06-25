package com.vogella.common.ui.dialog;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ReportDialog extends TitleAreaDialog {

	private String title;
	private String infoMessage;
	private ReportModel model;

	public ReportDialog(Shell shell, ReportModel model,  String title, String message) {
		super(shell);
		this.model = model;
		this.title = title;
		this.infoMessage = message;
	}

	@Override
	public void create() {
		super.create();
		setTitle(title);
		setMessage(infoMessage, IMessageProvider.INFORMATION);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Send", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite content = new Composite((Composite) super.createDialogArea(parent), SWT.NONE);

		DataBindingContext dbc = new DataBindingContext();
		
		new Label(content, SWT.NONE).setText("Title: ");

		Text titleText = new Text(content, SWT.BORDER);
		titleText.setMessage("title");
		
		ISWTObservableValue titleTextObservable = WidgetProperties.text(SWT.Modify).observe(titleText);
		
		dbc.bindValue(titleTextObservable, model.getTitleObservable());

		Label descriptionLabel = new Label(content, SWT.NONE);
		descriptionLabel.setText("Description: ");
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.TOP).applyTo(descriptionLabel);

		Text descriptionText = new Text(content, SWT.BORDER | SWT.MULTI);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(descriptionText);
		descriptionText.setMessage("description");
		
		ISWTObservableValue descriptionTextObservable = WidgetProperties.text(SWT.Modify).observe(descriptionText);
		
		dbc.bindValue(descriptionTextObservable, model.getDescriptionObservable());

		GridLayoutFactory.fillDefaults().numColumns(2).generateLayout(content);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(content);

		return content;
	}
}
