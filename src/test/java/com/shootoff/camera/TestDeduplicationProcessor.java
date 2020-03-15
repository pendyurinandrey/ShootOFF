package com.shootoff.camera;

import com.shootoff.camera.processors.DeduplicationProcessor;
import com.shootoff.camera.shot.ShotColor;
import com.shootoff.config.ConfigurationException;
import com.shootoff.util.loaders.OpenCVLoader;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestDeduplicationProcessor {

	@Test
	public void testReset() throws ConfigurationException {
		OpenCVLoader.INSTANCE.loadSharedLibs();

		DeduplicationProcessor deduplicationProcessor = new DeduplicationProcessor(new MockCameraManager());

		assertFalse(deduplicationProcessor.getLastShot().isPresent());

		Shot shot = new Shot(ShotColor.GREEN, 0, 0, 0, 0);

		deduplicationProcessor.processShot(shot);

		assertTrue(deduplicationProcessor.getLastShot().isPresent());

		deduplicationProcessor.reset();

		assertFalse(deduplicationProcessor.getLastShot().isPresent());
	}
}
