package com.vogella.prioritizer.eclipseplugin.ui;

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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.vogella.prioritizer.eclipseplugin.Activator;

public class PrioritizerPreference extends PreferencePage implements IWorkbenchPreferencePage {

	public static class PreferenceKeys {
		private static final String KEY_ENABLE_RECOMMENDER = "enableRecommender";
		private static final String KEY_ENABLE_COMPONENT_SELECTION = "enableComponentSelection";
		private static final String KEY_COMPONENT_SELECTION = "componentSelection";
		private static final String KEY_COMPONENT_SELECTION_INDEX = "componentSelectionIndex";
		private static final String KEY_USER_ID = "userID";
		private static final String KEY_ENABLE_MILESTONE_SELECTION = "enableMilestoneSelection";
		private static final String KEY_MILESTONE_SELECTION = "milestoneSelection";
		private static final String KEY_MILESTONE_SELECTION_INDEX = "milestoneSelectionIndex";
		private static final String KEY_ENABLE_DATE_SELECTION = "enableDateSelection";
		private static final String KEY_IS_OLDEST_FIRST = "isNewestFirst";
	}

	private String[] platformComponentEntries = new String[] { "UI", "SWT", "Debug", "Releng", "Compare", "Team", "IDE",
			"User Assistance", "Runtime", "Text", "Resources", "Search", "PMC", "Ant", "Doc", "Incubator", "CVS",
			"Website", "WebDAV", "Scripting" };

	private String[] targetMilestoneEntries = new String[] { "4.7", "4.7 RC1", "4.7 RC2", "4.7 RC3", "4.7 RC4",
			"4.7 RC4a", "4.7.1", "4.8", "4.8 M1", "4.8 M2", "4.8 M3", "4.8 M4",
			"4.8 M5", "4.8 M6", "4.8 M7" };

	private Group inputGroup;
	private Button enableRecommenderButton;
	private Button enableComponentSelectionButton;
	private Combo platformComponentsCombo;
	private Label userIdLabel;
	private Button enableTargetMilestoneSelection;
	private Combo targetMilestonesCombo;
	private Button enableRecommendByDateButton;
	private Button newestFirstRadioButton;
	private Button oldestFirstRadioButton;

	@Override
	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
		setDescription("Prioritizer Settings");
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	private void initPreferences() {
		enableRecommenderButton.setSelection(getPreferenceStore().getBoolean(PreferenceKeys.KEY_ENABLE_RECOMMENDER));
		enableComponentSelectionButton
				.setSelection(getPreferenceStore().getBoolean(PreferenceKeys.KEY_ENABLE_COMPONENT_SELECTION));
		platformComponentsCombo.select(getPreferenceStore().getInt(PreferenceKeys.KEY_COMPONENT_SELECTION_INDEX));
		targetMilestonesCombo.select(getPreferenceStore().getInt(PreferenceKeys.KEY_MILESTONE_SELECTION_INDEX));
		String userId = getPreferenceStore().getString(PreferenceKeys.KEY_USER_ID);
		userIdLabel.setText(userId.isEmpty() ? "---" : userId);
		enableRecommendByDateButton
				.setSelection(getPreferenceStore().getBoolean(PreferenceKeys.KEY_ENABLE_DATE_SELECTION));
		newestFirstRadioButton.setSelection(!getPreferenceStore().getBoolean(PreferenceKeys.KEY_IS_OLDEST_FIRST));
		oldestFirstRadioButton.setSelection(getPreferenceStore().getBoolean(PreferenceKeys.KEY_IS_OLDEST_FIRST));

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
		createUserIdPanel(composite);
		createInputPanel(composite);
		initPreferences();
		handleEnabledState();
		return composite;
	}

	private void createUserIdPanel(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout panelLayout = new GridLayout(3, true);
		panelLayout.marginWidth = 0;
		panelLayout.marginHeight = 0;
		panel.setLayout(panelLayout);

		new Label(panel, SWT.NONE).setText("Your unique user id: ");
		userIdLabel = new Label(panel, SWT.NONE);
		Button resetUserData = new Button(panel, SWT.PUSH);
		GridData buttonGridData = GridDataFactory.fillDefaults().create();
		resetUserData.setLayoutData(buttonGridData);
		resetUserData.setText("Reset");
	}

	private void createInputPanel(Composite composite) {
		GridLayout gridLayout = null;
		GridData gridData = null;

		inputGroup = new Group(composite, SWT.BORDER | SWT.WRAP);
		gridLayout = new GridLayout();
		inputGroup.setLayout(gridLayout);
		inputGroup.setText("Provide additional information");
		inputGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label explanation = new Label(inputGroup, SWT.WRAP);
		explanation.setText("To get better recommendations, you can provide additional information here.");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 400;
		explanation.setLayoutData(gridData);

		enableComponentSelectionButton = new Button(inputGroup, SWT.CHECK);
		enableComponentSelectionButton.setText("Specify Eclipse Platform Component");
		enableComponentSelectionButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEnabledState();
				super.widgetSelected(e);
			}
		});

		platformComponentsCombo = new Combo(inputGroup, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalIndent = 8;
		platformComponentsCombo.setLayoutData(gridData);
		for (String platform : platformComponentEntries) {
			platformComponentsCombo.add(platform);
		}

		enableTargetMilestoneSelection = new Button(inputGroup, SWT.CHECK);
		enableTargetMilestoneSelection.setText("Specify Target Milestone of Issue");
		enableTargetMilestoneSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEnabledState();
				super.widgetSelected(e);
			}
		});

		targetMilestonesCombo = new Combo(inputGroup, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalIndent = 8;
		targetMilestonesCombo.setLayoutData(gridData);
		for (String targetMilestone : targetMilestoneEntries) {
			targetMilestonesCombo.add(targetMilestone);
		}

		enableRecommendByDateButton = new Button(inputGroup, SWT.CHECK);
		enableRecommendByDateButton.setText("Recommend Issues based on their age");
		enableRecommendByDateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEnabledState();
				super.widgetSelected(e);
			}
		});

		Composite dateRadioComposite = new Composite(inputGroup, SWT.NONE);
		gridLayout = new GridLayout(2, true);
		dateRadioComposite.setLayout(gridLayout);
		dateRadioComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		newestFirstRadioButton = new Button(dateRadioComposite, SWT.RADIO);
		newestFirstRadioButton.setText("Newest first");
		oldestFirstRadioButton = new Button(dateRadioComposite, SWT.RADIO);
		oldestFirstRadioButton.setText("Oldest first");
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
		getPreferenceStore().setValue(PreferenceKeys.KEY_ENABLE_DATE_SELECTION,
				enableRecommendByDateButton.getSelection());
		getPreferenceStore().setValue(PreferenceKeys.KEY_IS_OLDEST_FIRST, oldestFirstRadioButton.getSelection());
		return super.performOk();
	}

	private void handleEnabledState() {
		inputGroup.setEnabled(enableRecommenderButton.getSelection());
		enableComponentSelectionButton.setEnabled(enableRecommenderButton.getSelection());
		platformComponentsCombo.setEnabled(
				enableComponentSelectionButton.isEnabled() && enableComponentSelectionButton.getSelection()
						&& enableRecommenderButton.getSelection());
		enableTargetMilestoneSelection.setEnabled(enableRecommenderButton.getSelection());
		targetMilestonesCombo.setEnabled(enableTargetMilestoneSelection.isEnabled()
				&& enableTargetMilestoneSelection.getSelection() && enableRecommenderButton.getSelection());
		enableRecommendByDateButton.setEnabled(enableRecommenderButton.getSelection());
		newestFirstRadioButton.setEnabled(enableRecommendByDateButton.isEnabled()
				&& enableRecommendByDateButton.getSelection() && enableRecommenderButton.getSelection());
		oldestFirstRadioButton.setEnabled(enableRecommendByDateButton.isEnabled()
				&& enableRecommendByDateButton.getSelection() && enableRecommenderButton.getSelection());
	}
}
