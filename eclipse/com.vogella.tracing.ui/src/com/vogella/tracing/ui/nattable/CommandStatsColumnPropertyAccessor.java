package com.vogella.tracing.ui.nattable;

import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

import com.vogella.tracing.ui.domain.CommandStats;

public class CommandStatsColumnPropertyAccessor implements IColumnPropertyAccessor<CommandStats> {

	private static final List<String> propertyNames = Arrays.asList("commandId", "commandName", "invocations", "keybinding");
	
	@Override
	public Object getDataValue(CommandStats commandStats, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return commandStats.getCommandId();
		case 1:
			return commandStats.getCommandName();
		case 2:
			return commandStats.getInvocations();
		case 3:
			return commandStats.getKeybinding();
		}

		return commandStats;
	}

	@Override
	public void setDataValue(CommandStats rowObject, int columnIndex, Object newValue) {
		// no editing necessary
	}

	@Override
	public int getColumnCount() {
		return propertyNames.size();
	}

	@Override
	public String getColumnProperty(int columnIndex) {
		return propertyNames.get(columnIndex);
	}

	@Override
	public int getColumnIndex(String propertyName) {
		return propertyNames.indexOf(propertyName);
	}

}
