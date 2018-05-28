
package com.vogella.tracing.core.addon;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.equinox.app.IApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;
import org.zeroturnaround.process.JavaProcess;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.PidUtil;
import org.zeroturnaround.process.Processes;

import com.beust.jcommander.JCommander;
import com.vogella.tracing.core.args.TracingProgramArgs;

public class AtlasServerAddon {

	private static final Logger LOG = LoggerFactory.getLogger(AtlasServerAddon.class);
	private JavaProcess atlasProcess;

	@PostConstruct
	public void startAltasServer(IApplicationContext applicationContext)
			throws IOException, InvalidExitValueException, InterruptedException, TimeoutException {
		String[] args = (String[]) applicationContext.getArguments().get(IApplicationContext.APPLICATION_ARGS);

		TracingProgramArgs tracingProgramArgs = new TracingProgramArgs();
		JCommander.newBuilder().addObject(tracingProgramArgs).acceptUnknownOptions(true).build().parse(args);

		if (tracingProgramArgs.getAtlasServerUrl() == null) {
			// start local atlas server
			StartedProcess start = new ProcessExecutor()
					.command("/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "/home/simon/atlas/atlas-1.5.3-standalone.jar", "memory.conf")
					.redirectOutput(Slf4jStream.ofCaller().asInfo()).start();
			atlasProcess = Processes.newJavaProcess(start.getProcess());

			int pid = PidUtil.getPid(start.getProcess());
			LOG.info("Started local Atlas server with process id: " + pid);
		}
	}

	@PreDestroy
	public void dispose() throws IOException, InterruptedException {
		atlasProcess.destroyGracefully();
	}

}
