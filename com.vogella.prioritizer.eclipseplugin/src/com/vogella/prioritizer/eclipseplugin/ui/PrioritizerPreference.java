package com.vogella.prioritizer.eclipseplugin.ui;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PrioritizerPreference extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private IntegerFieldEditor updateIntervalFieldEditor;

	public PrioritizerPreference() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
		setDescription("Prioritizer Settings");

		IPreferenceStore prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				"com.vogella.prioritizer.eclipseplugin");
		setPreferenceStore(prefStore);
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor("enable", "Enable Prioritizer", getFieldEditorParent()));
		addField(new RadioGroupFieldEditor("update_settings", "Update settings", 1,
				new String[][] { { "Manually", "manually" }, { "Automatically", "automatically" } },
				getFieldEditorParent(), true));

		updateIntervalFieldEditor = new IntegerFieldEditor("interval", "Update interval", getFieldEditorParent());
		updateIntervalFieldEditor.setEnabled(getPreferenceStore().getString("update_settings").equals("automatically"),
				getFieldEditorParent());
		addField(updateIntervalFieldEditor);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		FieldEditor fieldEditor = (FieldEditor) event.getSource();
		if (fieldEditor.getPreferenceName().equals("update_settings")) {
			updateIntervalFieldEditor.setEnabled(event.getNewValue().equals("automatically"), getFieldEditorParent());
		}
		super.propertyChange(event);
	}
}
