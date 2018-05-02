package com.vogella.tracing.meterregistry;

import java.time.Duration;

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.osgi.service.component.annotations.Component;

import com.netflix.spectator.atlas.AtlasConfig;

import io.micrometer.atlas.AtlasMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;

@Component(property = IContextFunction.SERVICE_CONTEXT_KEY + "=io.micrometer.core.instrument.MeterRegistry")
public class MeterRegistryContextFunction implements IContextFunction {

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		
		AtlasConfig atlasConfig = new AtlasConfig() {
			
		    @Override
		    public Duration step() {
		        return Duration.ofSeconds(10);
		    }

		    @Override
		    public String get(String k) {
		        return null; // accept the rest of the defaults
		    }
		};
		MeterRegistry registry = new AtlasMeterRegistry(atlasConfig, Clock.SYSTEM);
		
		MApplication app = context.get(MApplication.class);
		app.getContext().set(MeterRegistry.class, registry);
		
		return registry;
	}
}
