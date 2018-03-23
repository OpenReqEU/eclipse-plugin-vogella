package com.vogella.tracing.meterregistry;

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.osgi.service.component.annotations.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@Component(property = IContextFunction.SERVICE_CONTEXT_KEY + "=io.micrometer.core.instrument.MeterRegistry")
public class MeterRegistryContextFunction implements IContextFunction {

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		MeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();

		MApplication app = context.get(MApplication.class);
		app.getContext().set(MeterRegistry.class, simpleMeterRegistry);
		
		// TODO configure the MeterRegistry

		return simpleMeterRegistry;
	}

}
