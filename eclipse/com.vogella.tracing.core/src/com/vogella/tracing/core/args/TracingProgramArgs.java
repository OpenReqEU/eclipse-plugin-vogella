package com.vogella.tracing.core.args;

import com.beust.jcommander.Parameter;

public class TracingProgramArgs {

	@Parameter(names = "-useLocalAtlasServer")
	private boolean useLocalAtlasServer;

	@Parameter(names = "-atlasServerUrl")
	private String atlasServerUrl;

	public boolean isUseLocalAtlasServer() {
		return useLocalAtlasServer;
	}

	public String getAtlasServerUrl() {
		return atlasServerUrl;
	}
}
