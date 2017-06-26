package com.vogella.prioritizer.eclipseplugin.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class PrioritizerPreference extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PrioritizerPreference() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
		setDescription("Prioritizer Settings");
		setPreferenceStore(PlatformUI.getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor("enable", "Enable Prioritizer", getFieldEditorParent()));
	}
}
