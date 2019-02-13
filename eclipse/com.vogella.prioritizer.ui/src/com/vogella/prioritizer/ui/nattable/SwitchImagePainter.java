package com.vogella.prioritizer.ui.nattable;

import java.util.Objects;
import java.util.function.Predicate;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.swt.graphics.Image;

public class SwitchImagePainter extends ImagePainter {


	private Image alternative;
	private Predicate<ILayerCell> useAlternativePredicate;



	public SwitchImagePainter(Image image, Image alternative, Predicate<ILayerCell> useAlternativePredicate) {
		super(Objects.requireNonNull(image));
		this.alternative = Objects.requireNonNull(alternative);
		this.useAlternativePredicate = Objects.requireNonNull(useAlternativePredicate);
	}
	
	@Override
	protected Image getImage(ILayerCell cell, IConfigRegistry configRegistry) {
		if(useAlternativePredicate.test(cell)) {
			return alternative;
		}
		return super.getImage(cell, configRegistry);
	}
}
