package com.vogella.prioritizer.ui.nattable;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.grid.cell.AlternatingRowConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.grid.layer.config.DefaultGridLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Color;

/**
 * Sets up alternate row coloring. Applied by
 * {@link DefaultGridLayerConfiguration}
 */
public class AlternateRowStyleConfiguration extends
        AbstractRegistryConfiguration {

    public Color evenRowBgColor = GUIHelper.COLOR_WIDGET_BACKGROUND;
    public Color oddRowBgColor = GUIHelper.COLOR_WHITE;

    public void configureRegistry(IConfigRegistry configRegistry) {
        configureOddRowStyle(configRegistry);
        configureEvenRowStyle(configRegistry);
    }

    protected void configureOddRowStyle(IConfigRegistry configRegistry) {
        Style cellStyle = new Style();
        cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
                oddRowBgColor);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE,
                cellStyle, DisplayMode.NORMAL,
                AlternatingRowConfigLabelAccumulator.EVEN_ROW_CONFIG_TYPE);
    }

    protected void configureEvenRowStyle(IConfigRegistry configRegistry) {
        Style cellStyle = new Style();
        cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
                evenRowBgColor);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE,
                cellStyle, DisplayMode.NORMAL,
                AlternatingRowConfigLabelAccumulator.ODD_ROW_CONFIG_TYPE);
    }
}
