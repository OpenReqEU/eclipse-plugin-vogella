package com.vogella.prioritizer.service.browser;

import java.net.URL;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.component.annotations.Component;

import com.vogella.prioritizer.core.service.BrowserService;

@Component
public class BrowserServiceImpl implements BrowserService {

	@Override
	public void openExternalBrowser(URL url) throws PartInitException {
		PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);
	}

	@Override
	public void openInternalBrowser(URL url, String browserId) throws PartInitException {
		PlatformUI.getWorkbench().getBrowserSupport().createBrowser(browserId).openURL(url);
	}

}
