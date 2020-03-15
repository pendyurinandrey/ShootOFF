package com.shootoff;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestJWS {
	@Test
	public void testResourcesMetadata() {
		JFXApplication JFXApplication = new JFXApplication();

		String metadataXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<resources version=\"1.1\" fileSize=\"400319\" />";

		Optional<JFXApplication.ResourcesInfo> ri = JFXApplication.deserializeMetadataXML(metadataXML);

		assertTrue(ri.isPresent());

		assertEquals("1.1", ri.get().getVersion());
		assertEquals(400319, ri.get().getFileSize());
		assertEquals(metadataXML, ri.get().getXML());
	}
}
