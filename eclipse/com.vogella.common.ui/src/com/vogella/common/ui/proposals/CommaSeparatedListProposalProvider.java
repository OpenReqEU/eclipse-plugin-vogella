package com.vogella.common.ui.proposals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.fieldassist.ContentProposal;
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
		int lastIndexOfComma = contents.lastIndexOf(',');

		if (lastIndexOfComma < 0) {
			return filterContainsIgnoreCase(contents.trim());
		}

		if (contents.length() < lastIndexOfComma + 1) {
			return proposals.stream().map(p -> new ContentProposal(p)).toArray(IContentProposal[]::new);
		}

		String searchString = contents.substring(lastIndexOfComma + 1).trim();

		return filterContainsIgnoreCase(searchString);
	}

	private IContentProposal[] filterContainsIgnoreCase(String contents) {
		return proposals.stream().filter(p -> p.toLowerCase().contains(contents.toLowerCase()))
				.map(p -> new ContentProposal(p)).toArray(IContentProposal[]::new);
	}

}
