package com.vogella.prioritizer.eclipseplugin.views;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.vogella.prioritizer.eclipseplugin.Activator;
import com.vogella.prioritizer.eclipseplugin.Controller;

public class PrioritizerPreference extends PreferencePage implements IWorkbenchPreferencePage {

	public static class PreferenceKeys {
		private static final String KEY_ENABLE_RECOMMENDER = "enableRecommender";
		private static final String KEY_ENABLE_COMPONENT_SELECTION = "enableComponentSelection";
		private static final String KEY_COMPONENT_SELECTION = "componentSelection";
		private static final String KEY_COMPONENT_SELECTION_INDEX = "componentSelectionIndex";
		private static final String KEY_ENABLE_MILESTONE_SELECTION = "enableMilestoneSelection";
		private static final String KEY_MILESTONE_SELECTION = "milestoneSelection";
		private static final String KEY_MILESTONE_SELECTION_INDEX = "milestoneSelectionIndex";
		private static final String KEY_USER_EMAIL = "userEmail";
	}

	private String[] platformComponentEntries = new String[] { "UI", "SWT", "Debug", "Releng", "Compare", "Team", "IDE",
			"User Assistance", "Runtime", "Text", "Resources", "Search", "PMC", "Ant", "Doc", "Incubator", "CVS",
			"Website", "WebDAV", "Scripting" };

	private String[] targetMilestoneEntries = new String[] { "4.7", "4.7 RC1", "4.7 RC2", "4.7 RC3", "4.7 RC4",
			"4.7 RC4a", "4.7.1", "4.8", "4.8 M1", "4.8 M2", "4.8 M3", "4.8 M4", "4.8 M5", "4.8 M6", "4.8 M7" };

	Controller controller;

	private Group additionalInformationGroup;
	private Group emailGroup;
	private Button enableRecommenderButton;
	private Button enableComponentSelectionButton;
	private Combo platformComponentsCombo;
	private Button enableTargetMilestoneSelection;
	private Combo targetMilestonesCombo;

	private Text emailText;

	@Override
	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
		setDescription("Prioritizer Settings");
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		controller = new Controller();
	}

	private void initPreferences() {
		enableRecommenderButton.setSelection(getPreferenceStore().getBoolean(PreferenceKeys.KEY_ENABLE_RECOMMENDER));
		enableComponentSelectionButton
				.setSelection(getPreferenceStore().getBoolean(PreferenceKeys.KEY_ENABLE_COMPONENT_SELECTION));
		platformComponentsCombo.select(getPreferenceStore().getInt(PreferenceKeys.KEY_COMPONENT_SELECTION_INDEX));
		targetMilestonesCombo.select(getPreferenceStore().getInt(PreferenceKeys.KEY_MILESTONE_SELECTION_INDEX));
		emailText.setText(getPreferenceStore().getString(PreferenceKeys.KEY_USER_EMAIL));
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true));

		createEnableRecommenderPreference(composite);
		createEmailPanel(composite);
		createInputPanel(composite);
		initPreferences();
		handleEnabledState();
		return composite;
	}

	private void createEmailPanel(Composite parent) {
		GridLayout gridLayout = null;
		GridData gridData = null;

		emailGroup = new Group(parent, SWT.BORDER | SWT.WRAP);
		gridLayout = new GridLayout();
		emailGroup.setLayout(gridLayout);
		emailGroup.setText("Provide E-Mail Address");
		emailGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Link explanation = new Link(emailGroup, SWT.WRAP);
		explanation.setText(
				"Please provide the E-Mail address you use at <a href=\"https://bugs.eclipse.org\">https://bugs.eclipse.org</a> to get recommendations based on your previously resolved issues.");
		explanation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(e.text));
				} catch (PartInitException | MalformedURLException e1) {
					e1.printStackTrace();
				}
			}
		});
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 400;
		explanation.setLayoutData(gridData);

		Composite emailPanel = new Composite(emailGroup, SWT.NONE);
		gridLayout = new GridLayout(2, false);
		emailPanel.setLayout(gridLayout);
		emailPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		new Label(emailPanel, SWT.NONE).setText("E-Mail:");

		emailText = new Text(emailPanel, SWT.NONE);
		emailText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
	}

	private void createInputPanel(Composite composite) {
		GridLayout gridLayout = null;
		GridData gridData = null;

		additionalInformationGroup = new Group(composite, SWT.BORDER | SWT.WRAP);
		gridLayout = new GridLayout();
		additionalInformationGroup.setLayout(gridLayout);
		additionalInformationGroup.setText("Provide additional information");
		additionalInformationGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label explanation = new Label(additionalInformationGroup, SWT.WRAP);
		explanation.setText("To get better recommendations, you can provide additional information here.");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 400;
		explanation.setLayoutData(gridData);

		enableComponentSelectionButton = new Button(additionalInformationGroup, SWT.CHECK);
		enableComponentSelectionButton.setText("Specify Eclipse Platform Component");
		enableComponentSelectionButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEnabledState();
				super.widgetSelected(e);
			}
		});

		platformComponentsCombo = new Combo(additionalInformationGroup, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalIndent = 8;
		platformComponentsCombo.setLayoutData(gridData);
		for (String platform : platformComponentEntries) {
			platformComponentsCombo.add(platform);
		}

		enableTargetMilestoneSelection = new Button(additionalInformationGroup, SWT.CHECK);
		enableTargetMilestoneSelection.setText("Specify Target Milestone of Issue");
		enableTargetMilestoneSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEnabledState();
				super.widgetSelected(e);
			}
		});

		targetMilestonesCombo = new Combo(additionalInformationGroup, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalIndent = 8;
		targetMilestonesCombo.setLayoutData(gridData);
		for (String targetMilestone : targetMilestoneEntries) {
			targetMilestonesCombo.add(targetMilestone);
		}
	}

	private void createEnableRecommenderPreference(Composite composite) {
		enableRecommenderButton = new Button(composite, SWT.CHECK);
		enableRecommenderButton.setText("Enable Recommender");
		enableRecommenderButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEnabledState();
				super.widgetSelected(e);
			}
		});
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(PreferenceKeys.KEY_ENABLE_RECOMMENDER, enableRecommenderButton.getSelection());
		getPreferenceStore().setValue(PreferenceKeys.KEY_ENABLE_COMPONENT_SELECTION,
				enableComponentSelectionButton.getSelection());
		getPreferenceStore().setValue(PreferenceKeys.KEY_COMPONENT_SELECTION,
				platformComponentsCombo.getItem(platformComponentsCombo.getSelectionIndex()));
		getPreferenceStore().setValue(PreferenceKeys.KEY_COMPONENT_SELECTION_INDEX,
				platformComponentsCombo.getSelectionIndex());
		getPreferenceStore().setValue(PreferenceKeys.KEY_ENABLE_MILESTONE_SELECTION,
				enableTargetMilestoneSelection.getSelection());
		getPreferenceStore().setValue(PreferenceKeys.KEY_MILESTONE_SELECTION,
				targetMilestonesCombo.getItem(targetMilestonesCombo.getSelectionIndex()));
		getPreferenceStore().setValue(PreferenceKeys.KEY_MILESTONE_SELECTION_INDEX,
				targetMilestonesCombo.getSelectionIndex());
		getPreferenceStore().setValue(PreferenceKeys.KEY_USER_EMAIL, emailText.getText());
		if (!emailText.getText().isEmpty()) {
			// TODO
			controller.requestModel(emailText.getText());
		}
		return super.performOk();
	}

	private void handleEnabledState() {
		additionalInformationGroup.setEnabled(enableRecommenderButton.getSelection());
		enableComponentSelectionButton.setEnabled(enableRecommenderButton.getSelection());
		platformComponentsCombo.setEnabled(enableComponentSelectionButton.isEnabled()
				&& enableComponentSelectionButton.getSelection() && enableRecommenderButton.getSelection());
		enableTargetMilestoneSelection.setEnabled(enableRecommenderButton.getSelection());
		targetMilestonesCombo.setEnabled(enableTargetMilestoneSelection.isEnabled()
				&& enableTargetMilestoneSelection.getSelection() && enableRecommenderButton.getSelection());
		emailGroup.setEnabled(enableRecommenderButton.getSelection());
		emailText.setEnabled(enableRecommenderButton.getSelection());
	}
}
