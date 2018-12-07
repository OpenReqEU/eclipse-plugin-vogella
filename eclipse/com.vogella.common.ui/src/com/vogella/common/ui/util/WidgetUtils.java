package com.vogella.common.ui.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class WidgetUtils {

	private WidgetUtils() {}
	
	private static ImageDescriptor smartModeImageDescriptor;
	
	static {
        Bundle bundle = FrameworkUtil.getBundle(WidgetUtils.class);
        URL find = FileLocator.find(bundle, new Path("/icons/smartmode.png"));
        smartModeImageDescriptor = ImageDescriptor.createFromURL(find);
	}
	
	private static final String LCL = "abcdefghijklmnopqrstuvwxyz";
    private static final String UCL = LCL.toUpperCase();
    private static final String NUMS = "0123456789";
	
	public static void createContentAssist(Text text, ResourceManager resourceManager, String... proposals) {
		SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(proposals);
        ContentProposalAdapter proposalAdapter = new ContentProposalAdapter(
            text,
            new TextContentAdapter(),
            proposalProvider,
            getActivationKeystroke(),
            getAutoactivationChars());
        proposalProvider.setFiltering(false);
        proposalAdapter.setPropagateKeys(false);
        proposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);
        
        ControlDecoration decoration = new ControlDecoration(text, SWT.TOP | SWT.LEFT);
        decoration.setImage(resourceManager.createImage(smartModeImageDescriptor));
        decoration.setDescriptionText("This text field has content assist. (CTRL + SPACE)");
        decoration.setShowOnlyOnFocus(true);
	}
	
	
	
    private static char[] getAutoactivationChars() {
        // To enable content proposal on deleting a char
        String allChars = LCL + UCL + NUMS;
        return allChars.toCharArray();
    }
 
    private static KeyStroke getActivationKeystroke() {
        KeyStroke keyStroke = KeyStroke.getInstance(
                SWT.CTRL, ' ');
        return keyStroke;
    }
}
