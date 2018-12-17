package com.vogella.common.core.service;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;

public interface BrowserService {
	void openExternalBrowser(URL url) throws CoreException;

	void openInternalBrowser(URL url, String browserId) throws CoreException;
}
