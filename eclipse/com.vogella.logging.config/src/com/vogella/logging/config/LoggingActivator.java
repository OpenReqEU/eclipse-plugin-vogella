package com.vogella.logging.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class LoggingActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		configureLogbackInBundle(context.getBundle());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

	private void configureLogbackInBundle(Bundle bundle) throws JoranException, IOException {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator jc = new JoranConfigurator();
		jc.setContext(context);
		context.reset();

		// get the configuration location where the logback.xml is located
		Location configurationLocation = Platform.getInstallLocation();
		File logbackFile = new File(configurationLocation.getURL().getPath(), "logback.xml");

		// overriding the log directory property programmatically
		String logDirProperty = logbackFile.getAbsolutePath();
		context.putProperty("LOG_DIR", logDirProperty);
		if (logbackFile.exists()) {
			jc.doConfigure(logbackFile);
		} else {
			URL logbackConfigFileUrl = FileLocator.find(bundle, new Path("logback.xml"), null);
			jc.doConfigure(logbackConfigFileUrl.openStream());
		}
	}
}
