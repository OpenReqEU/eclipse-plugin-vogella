package com.vogella.tracing.core.args;

import com.beust.jcommander.Parameter;

public class TracingProgramArgs {

	@Parameter(names = "-atlasServerUrl")
	private String atlasServerUrl;

	public String getAtlasServerUrl() {
		return atlasServerUrl;
	}
}
