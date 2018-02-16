package com.vogella.prioritizer.ui.nattable;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellLabelMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

public class LinkClickConfiguration<T> extends AbstractUiBindingConfiguration implements IMouseAction {

    private final List<IMouseAction> clickLiseners = new ArrayList<IMouseAction>();
	private String cellLabel;

    public LinkClickConfiguration(String cellLabel) {
		this.cellLabel = cellLabel;
	}
    
    /**
     * Configure the UI bindings for the mouse click
     */
    @Override
    public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
        // Match a mouse event on the body, when the left button is clicked
        // and the custom cell label is present
        CellLabelMouseEventMatcher mouseEventMatcher =
                new CellLabelMouseEventMatcher(
                        GridRegion.BODY,
                        MouseEventMatcher.LEFT_BUTTON,
                        cellLabel);

        CellLabelMouseEventMatcher mouseHoverMatcher = new CellLabelMouseEventMatcher(GridRegion.BODY, 0, cellLabel);

        // Inform the button painter of the click.
        uiBindingRegistry.registerMouseDownBinding(mouseEventMatcher, this);

        // show hand cursor, which is usually used for links
        uiBindingRegistry.registerMouseEnterBinding(mouseHoverMatcher, (natTable, event) -> {
            natTable.setCursor(natTable.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
        });

        // change cursor to the default again
        uiBindingRegistry.registerMouseExitBinding(mouseHoverMatcher, (natTable, event) -> {
            natTable.setCursor(natTable.getDisplay().getSystemCursor(SWT.CURSOR_APPSTARTING));
        });
    }

    @Override
    public void run(final NatTable natTable, MouseEvent event) {
        for (IMouseAction listener : this.clickLiseners) {
            listener.run(natTable, event);
        }
    }

    public void addClickListener(IMouseAction mouseAction) {
        this.clickLiseners.add(mouseAction);
    }

    public void removeClickListener(IMouseAction mouseAction) {
        this.clickLiseners.remove(mouseAction);
    }
}