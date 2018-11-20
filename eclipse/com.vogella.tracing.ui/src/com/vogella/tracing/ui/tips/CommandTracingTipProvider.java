package com.vogella.tracing.ui.tips;

import java.io.IOException;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.bindings.EBindingService;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.tips.core.TipImage;
import org.eclipse.tips.core.TipProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

@SuppressWarnings("restriction")
public class CommandTracingTipProvider extends TipProvider {

	private TipImage tipImage;

	@Override
	public String getDescription() {
		return "Provider for tips based on user interaction with the Eclipse IDE";
	}

	@Override
	public String getID() {
		return getClass().getName();
	}

	@Override
	public TipImage getImage() {
		if (tipImage == null) {
			Bundle bundle = FrameworkUtil.getBundle(getClass());

			try {
				tipImage = new TipImage(bundle.getEntry("icons/48/openreqlogo.png"));
			} catch (IOException ex) {
				getManager().log(new Status(IStatus.ERROR, bundle.getSymbolicName(), ex.getMessage(), ex));
			}
		}
		return tipImage;
	}

	@Override
	public IStatus loadNewTips(IProgressMonitor monitor) {

//		ECommandService commandService = PlatformUI.getWorkbench().getService(ECommandService.class);
//		EHandlerService handlerService = PlatformUI.getWorkbench().getService(EHandlerService.class);
//		UISynchronize uiSync = PlatformUI.getWorkbench().getService(UISynchronize.class);
//		InnoSensrService innoSensrService = PlatformUI.getWorkbench().getService(InnoSensrService.class);
//
//		ArrayList<Tip> tips = new ArrayList<>();
//		tips.add(new CommandInvocationShortCutTip(getID(), innoSensrService, uiSync));
//
//		MeterRegistry meterRegistry = PlatformUI.getWorkbench().getService(MeterRegistry.class);
//		EBindingService bindingService = PlatformUI.getWorkbench().getService(EBindingService.class);
//		List<Meter> meters = meterRegistry.getMeters();
//		if (meterRegistry instanceof CompositeMeterRegistry) {
//			Set<MeterRegistry> registries = ((CompositeMeterRegistry) meterRegistry).getRegistries();
//			java.util.Optional<MeterRegistry> findAny = registries.stream()
//					.filter(SimpleMeterRegistry.class::isInstance).findAny();
//			if (findAny.isPresent()) {
//				meters = findAny.get().getMeters();
//			}
//		}
//		List<CommandStats> list = meters.stream()
//				.filter(meter -> "command.calls.contributionitem".equals(meter.getId().getName())).flatMap(meter -> {
//					return StreamSupport.stream(meter.measure().spliterator(), false).map(measurement -> {
//						String commandId = meter.getId().getTag("commandId");
//						double invocations = measurement.getValue();
//
//						ParameterizedCommand command = commandService.createCommand(commandId, null);
//						return new CommandStats(commandId, getCommandName(command), (int) invocations,
//								getKeybinding(command, bindingService));
//					});
//				}).collect(Collectors.toList());
//
//		for (CommandStats commandStats : list) {
//			double invocations = commandStats.getInvocations();
//			if (invocations > 3) {
//				tips.add(new ShortcutTip(getID(), commandStats.getCommandName(), commandStats.getKeybinding(),
//						commandService, handlerService, uiSync, innoSensrService));
//			}
//		}
//
//		setTips(tips);

		return Status.OK_STATUS;
	}

	private String getKeybinding(ParameterizedCommand command, EBindingService bindingService) {
		TriggerSequence bestSequenceFor = bindingService.getBestSequenceFor(command);
		if (bestSequenceFor != null) {
			return bestSequenceFor.format();
		}
		return null;
	}

	private String getCommandName(ParameterizedCommand command) {
		try {
			return command.getName();
		} catch (NotDefinedException e) {
			// unlikely to happen
			return "Command does not have a name";
		}
	}

	@Override
	public void dispose() {
	}
}
