package com.vogella.common.ui.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.hamcrest.Matchers;
import org.junit.Test;

import com.vogella.common.ui.proposals.CommaSeparatedListProposalProvider;

public class CommaSeparatedListProposalProviderTest {

	private static final int POSITION_NOT_RELEVANT = 0;

	@Test
	public void testGetProposals() {
		// arrange
		CommaSeparatedListProposalProvider commaSeparatedListProposalProvider = new CommaSeparatedListProposalProvider(
				"Simon", "Jennifer", "vogella");

		// act
		IContentProposal[] proposals = commaSeparatedListProposalProvider.getProposals("Sim", POSITION_NOT_RELEVANT);

		// assert
		assertThat(proposals, Matchers.arrayWithSize(1));
		assertThat(proposals[0].getContent(), equalTo("Simon"));
	}

	@Test
	public void testGetProposalsCommaSeparated() {
		// arrange
		CommaSeparatedListProposalProvider commaSeparatedListProposalProvider = new CommaSeparatedListProposalProvider(
				"Simon", "Jennifer", "vogella");

		// act
		IContentProposal[] proposals = commaSeparatedListProposalProvider.getProposals("", POSITION_NOT_RELEVANT);

		// assert
		assertThat(proposals, Matchers.arrayWithSize(3));
		List<String> proposalsList = Arrays.asList(proposals).stream().map(IContentProposal::getContent)
				.collect(Collectors.toList());
		assertThat(proposalsList, Matchers.contains("Simon", "Jennifer", "vogella"));
	}

	@Test
	public void testGetProposalsNewTextAfterComma() {
		// arrange
		CommaSeparatedListProposalProvider commaSeparatedListProposalProvider = new CommaSeparatedListProposalProvider(
				"Simon", "Jennifer", "vogella");

		// act
		IContentProposal[] proposals = commaSeparatedListProposalProvider.getProposals("Sim, Jen", POSITION_NOT_RELEVANT);

		// assert
		assertThat(proposals, Matchers.arrayWithSize(1));
		assertThat(proposals[0].getContent(), equalTo("Jennifer"));
	}
}
