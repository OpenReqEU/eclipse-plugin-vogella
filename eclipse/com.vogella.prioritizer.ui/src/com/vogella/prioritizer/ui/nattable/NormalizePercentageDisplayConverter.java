package com.vogella.prioritizer.ui.nattable;

import java.text.DecimalFormat;

import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

public class NormalizePercentageDisplayConverter extends DisplayConverter {

	private static final DecimalFormat formatter = new DecimalFormat("0.00");
	private double min;
	private double max;

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		if (canonicalValue instanceof Number) {
			Number value = (Number) canonicalValue;
			double doubleValue = value.doubleValue();

			return formatToPercentage(min, max, doubleValue);
		} else if (canonicalValue instanceof String) {
			double doubleValue = Double.parseDouble((String) canonicalValue);

			return formatToPercentage(min, max, doubleValue);
		}
		return canonicalValue;
	}

	private Object formatToPercentage(double min, double max, double doubleValue) {
		double normalized100Percent = (doubleValue - min) * 100d / (max - min);
		return formatter.format(normalized100Percent) + " %";
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		return displayValue;
	}

	public void setMinAndMax(double min, double max) {
		this.min = min;
		this.max = max;
	}
}
