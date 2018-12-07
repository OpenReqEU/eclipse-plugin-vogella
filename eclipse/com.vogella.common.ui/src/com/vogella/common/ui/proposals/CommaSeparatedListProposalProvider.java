package com.vogella.common.ui.proposals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class CommaSeparatedListProposalProvider implements IContentProposalProvider {

	private List<String> proposals;

	public CommaSeparatedListProposalProvider(List<String> proposals) {
		this.proposals = proposals;
	}

	public CommaSeparatedListProposalProvider(String... proposals) {
		this.proposals = Arrays.asList(proposals);
	}
	
	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		return null;
	}

}
