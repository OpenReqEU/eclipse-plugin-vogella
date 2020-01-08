package com.vogella.prioritizer.bugzilla.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BugzillaCommentTest {

	@Test
	void trivialBugZillaCommentTests() {
		BugzillaComment bugComment = new BugzillaComment();
		bugComment.setBugId(1);
		
		assertEquals(1,bugComment.getBugId());
	}

}
